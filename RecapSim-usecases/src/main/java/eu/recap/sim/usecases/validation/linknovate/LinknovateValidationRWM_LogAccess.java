/**
 * 
 */
package eu.recap.sim.usecases.validation.linknovate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.google.common.base.Optional;

import eu.recap.sim.experiments.DistributionLNK;
import eu.recap.sim.models.ApplicationModel.Application;
import eu.recap.sim.models.ApplicationModel.ApplicationLandscape;
import eu.recap.sim.models.WorkloadModel.Device;
import eu.recap.sim.models.WorkloadModel.Request;
import eu.recap.sim.models.WorkloadModel.Workload;



/**
 * @author Malika 
 *
 */
public class LinknovateValidationRWM_LogAccess {
	public static int timeUnits=1000; // multiply by this to get in millisecond
	
	
	
	
	
	
	
	/**
	 * Generates number of requests per device. Each request sent out at the time specified in the csv file.
	 * Each request is sent once as one cloudlet to the web server, then once to the ES.
	 * However numberOfDataNodesPerRequest cloudlets are generated (one for each of the randomly selected numberOfDataNodesPerRequest data nodes) 
	 * One cloudlet is created to aggregate results at the ES, when all cloudlets in datanode that belong to the same request are accomplished
	 * @param numberOfDataNodesPerRequest: the number of data nodes that are used for each request
	 * @param ram
	 * @return
	 * 
	 */
	public static Workload GenerateLinknovateValidationDeviceBehaviorLogRequestTrueOneToManyToOne(int numberOfDataNodesPerRequest, ApplicationLandscape ram){
		
		//////////////////////////////////////////////
		//1. Load "app.lkn.apache.log.access.csv" file 
		//////////////////////////////////////////////
		
		//using super-csv
		//https://super-csv.github.io/super-csv/examples_reading.html
		//add dependency to pom.xml
		String csvFile = "app_lkn_apache_log_access.csv";  // Remove extra commas in column "request" at lines 121, 284
		//app_lkn_apache_log_access.csv \\Test-4h-1.csv\\Test-4h-validation.csv
		ICsvListReader listReader = null;
		List<List<Object>> validLogRequests = new ArrayList< List<Object> >();
        
		
		try {   
			listReader = new CsvListReader(new FileReader(csvFile), CsvPreference.STANDARD_PREFERENCE);
			
	        
	       
			listReader.getHeader(true); // skip the header (can't be used with CsvListReader)
	        
			final CellProcessor[] processors = getProcessors();
	        
			
			List<Object> logRequestRow;
			while( (logRequestRow = listReader.read(processors)) != null ) {
		        //System.out.println(String.format("lineNo=%s, rowNo=%s, customerList=%s", listReader.getLineNumber(),listReader.getRowNumber(), logRequestRow));
		        boolean validRequest=true;
		        //check if valid
		        if(((int)logRequestRow.get(14)) < 500000)
		        {
		        	validRequest=false;
		        }
		        
		        if(logRequestRow.get(1).equals("Go-http-client/1.1"))
		        {
		        	validRequest=false;
		        }
		        
		        if(!logRequestRow.get(12).equals(200)) // http ok response
		        {
		        	validRequest=false;
		        }
		        
		        if(((String)logRequestRow.get(11)).startsWith("/server-status?auto=") ||
		        		((String)logRequestRow.get(11)).startsWith("/static")
		        		
		        ) 
		        {
		        	validRequest=false;
		        }
		        if(! ((String)logRequestRow.get(11)).contains("?query=")
		        	&&
		        	! ((String)logRequestRow.get(11)).contains("dashboard")
		        	&&
		        	! ((String)logRequestRow.get(11)).contains("slug")
        			&&
		        	! ((String)logRequestRow.get(11)).contains("summary")
		        )
		        {
		        	validRequest=false;
		        }
		        
		        
		        if(validRequest) 
		        {
		        	validLogRequests.add(logRequestRow); // line at the end
		        	//System.out.println(logRequestRow.get(14)+"\t"+logRequestRow.get(11));
		        	//System.out.println(logRequestRow.get(14));
		        }
			}
			//System.exit(0);
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        finally {
                if( listReader != null ) {
                        try {
							listReader.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                }
        }
    
		
		
		
		//////////////////////////////////////////////
		//2. Load unique IP addresses from "clientip" column as device IDs
		//////////////////////////////////////////////
		
		//Get IP addresses of devices who sent requests
		int deviceQty=0;
		HashMap<String,Device.Builder> devices = new HashMap<String,Device.Builder>(); 
		for (List<Object> logRequest : validLogRequests) 
		{
			String clientIP = (String) logRequest.get(4 /*index of Client IP*/);
			if (!devices.containsKey(clientIP))
			{
				Device.Builder device = Device.newBuilder();
				device.setDeviceId(deviceQty + "");
				device.setDeviceName("IP_"+clientIP+"_"+deviceQty);
				deviceQty++;
				
				devices.put(clientIP, device);
			}
			
		}
		
		
		
		
		
		
		//////////////////////////////////////////////
		//3. Create requests based on "time" column (needs to be reset to 0) and ID and "bytes" column
		//////////////////////////////////////////////
		
		
		Double[] normalDistribution = {0.13,0.14,0.16,0.16,0.18,0.23};
		// transform normalDistribution into additive distribution
		for(int i=1; i<normalDistribution.length; i++)
		{
			normalDistribution[i]+=normalDistribution[i-1];
		}
		
		
		//generate api requests
		
		long dateInitialRequest = ((Date) (validLogRequests.get(0).get(0 /*position of time*/))).getTime();
		
		int counter=1;
		for (List<Object> logRequest : validLogRequests) 
		{
			
			//randomly generate a number of destination data nodes destination using provided normalDistribution
			HashSet<Integer> dataNodeDestinations = new HashSet<Integer>(numberOfDataNodesPerRequest);
			while(dataNodeDestinations.size()<numberOfDataNodesPerRequest)
			{
				double r = Math.random();
				int destinationDataNode;
				for (destinationDataNode=0;destinationDataNode<normalDistribution.length;destinationDataNode++)
				{
					if(r<normalDistribution[destinationDataNode])
						break;
				}
				destinationDataNode++; // Counting starts at 1
			
				if(!dataNodeDestinations.contains(destinationDataNode))
				{
					dataNodeDestinations.add(destinationDataNode);
				}
			}
			
			
			//compute time request was sent (starting from 0s)
			long dateCurrentRequest = ((Date) logRequest.get(0 /*position of time*/)).getTime();
			int time= (int) (dateCurrentRequest-dateInitialRequest)/1000;
	
			Request.Builder request = Request.newBuilder();
			request.setApplicationId(ram.getApplicationsList().get(0).getApplicationId());								
			request.setComponentId("1");
			request.setApiId("1_1");
			request.setTime(time*timeUnits);
			request.setRequestId(counter);
			request.setExpectedDuration((int)Math.ceil((int)logRequest.get(14)/1000) ); //divide by 1000 as original file in nanosecond, convert into milisecond
			//TODO: calculate the number of MIPS in data nodes to get the right exec time
			double Clients_CpuFrequency = 3000.0;
			double requestTimeInSecond=(int)logRequest.get(14)*(Clients_CpuFrequency/1000000);
			
			int mipsPerDataNode = ((int)(Math.max(requestTimeInSecond-1200,100)))/10;
			request.setMipsDataNodes(mipsPerDataNode*timeUnits);
			
			// create requests 
			for(int destinationDataNode: dataNodeDestinations)
			{
				request.addDataNodes(destinationDataNode);
			}
			
			request.setDataToTransfer(1);
			System.out.println("Request: "+counter+" StartTime (ms):"+time*timeUnits);
			System.out.println("            Expected duration (ms):"+request.getExpectedDuration());
			
			//add request to origin ip device
			String clientIP = (String) logRequest.get(4 /*index of Client IP*/);
			Device.Builder device = devices.get(clientIP);
			device.addRequests(request.build());
		
			
			
			if(counter>=10)
			{
				//break;
			}
			counter++;
			
		}
		
		
		//System.exit(0);
				
		
		
		Workload.Builder workload = Workload.newBuilder();
		// add all devices to the workload
		for(Device.Builder dev : devices.values())
		{
			workload.addDevices(dev);
		}
		
		return workload.build();


	}
	
	
	
	/**
	 * CSV Reader
	 * Define the type of cells
	 * Sets up the processors used for the examples. There are 10 CSV columns, so 10 processors are defined. Empty
	 * columns are read as null (hence the NotNull() for mandatory columns).
	 * 
	 * @return the cell processors
	 */
	private static CellProcessor[] getProcessors() {
	        
	        final String emailRegex = "[a-z0-9\\._]+@[a-z0-9\\.]+"; // just an example, not very robust!
	        StrRegEx.registerMessage(emailRegex, "must be a valid email address");
	        
	        final CellProcessor[] processors = new CellProcessor[] { 
	        		new ParseDate("dd.MM.yyyy HH:mm:ss"), // time 0
	        		new NotNull(),	//agent 1
	        		new NotNull(),	//auth 2
	        		new ParseInt(), // time 3
	                new NotNull(), // clientip 4
	                new NotNull(), // cloudId 5
	                new NotNull(), // cloudcloud_region 6
	                new NotNull(), // ident 7
	                new NotNull(), // metric_layer 8
	                new NotNull(), // referrer 9
	                new NotNull(), // remote_ip 10
	                new NotNull(), // request 11
	                new ParseInt(), // response 12
	                new NotNull(), // source 13
	                new ParseInt(), // timeTakenResquest 14
	                new NotNull(), // verb 15 
	                new NotNull(), // vmuuid 16
	                
//	                new ParseDate("dd/MM/yyyy"), // birthDate
//	                new NotNull(), // mailingAddress
//	                new Optional(new ParseBool()), // married
//	                new Optional(new ParseInt()), // numberOfKids
//	                new NotNull(), // favouriteQuote
//	                new StrRegEx(emailRegex), // email
//	                new LMinMax(0L, LMinMax.MAX_LONG) // loyaltyPoints
	        };
	        
	        return processors;
	}
	
	
}
