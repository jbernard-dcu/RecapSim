/**
 * 
 */
package eu.recap.sim.usecases.validation.linknovate;

import java.util.ArrayList;

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
public class LinknovateValidationRWM {
	
	/**
	 * Generates number of requests per device. Each request sent out at the time specified in the csv file.
	 * Requests are sent always on the 1st component of each application in a round robin fashion 
	 * @param deviceQty
	 * @param requestPerPathQty
	 * @param pathQty
	 * @param ram
	 * @return
	 */
	public static Workload GenerateLinknovateValidationDeviceBehavior(int deviceQty, int totalRequests, int pathQty, ApplicationLandscape ram) {

		//1. Load "app.lkn.apache.log.access.csv" file 
		
		//2. Load unique IP addresses from "clientip" column as device IDs
		
		//3. Create requests based on "time" column (needs to be reset to 0) and ID and "bytes" column
		
		
		
		Double[] normalDistribution = {0.13,0.14,0.16,0.16,0.18,0.23};
		Integer[] requestDistribution = DistributionLNK.Distro(totalRequests,normalDistribution);
		
		Workload.Builder workload = Workload.newBuilder();
		Device.Builder device = Device.newBuilder();
		device.setDeviceId(deviceQty + "");
		device.setDeviceName("IP_52.168.167.253"+deviceQty);
		
		//api Counter starts with 1
		int apiCounter =1;
		for(int bucket:requestDistribution){
			int count=0;
			while(count!=bucket){
				
				Request.Builder request = Request.newBuilder();
				
				request.setApplicationId(ram.getApplicationsList().get(0).getApplicationId());								
				request.setComponentId("1");
				request.setApiId("1_"+apiCounter);
				request.setTime(0);
				request.setDataToTransfer(100);

				device.addRequests(request.build());
				
				count++;
			}
			apiCounter++;
		}
		workload.addDevices(device.build());
		return workload.build();


	}
	

	
	/**
	 * Generates number of requests per device. Each request sent out at the same time to each path.
	 * Requests are sent always on the 1st component of each application in a round robin fashion 
	 * @param deviceQty
	 * @param requestPerPathQty
	 * @param pathQty
	 * @param ram
	 * @return
	 */
	public static Workload GenerateLinknovateValidationSimpleDeviceBehavior(int deviceQty, int requestPerPathQty, int pathQty, ApplicationLandscape ram) {


		
		
		ArrayList<String> appIds = new ArrayList<String>(ram.getApplicationsCount());
		
		for (Application app :ram.getApplicationsList()){
			
			appIds.add(app.getApplicationId());
			
		}
		
		
		//before csv integration generate requests per path per each device
		
		int indexNmberOfApplications = appIds.size()-1;
		int indexNmberOfApplicationsCounter = 0;
		
		Workload.Builder workload = Workload.newBuilder();
		
		while (deviceQty != 0) {
			Device.Builder device = Device.newBuilder();
			device.setDeviceId(deviceQty + "");
			device.setDeviceName("IP_52.168.167.253"+deviceQty);
			
			while (pathQty != 0){
				//generate requests
				int requestQtyCounter = requestPerPathQty;
				while (requestQtyCounter != 0) {
					Request.Builder request = Request.newBuilder();
					
					request.setApplicationId(appIds.get(indexNmberOfApplicationsCounter));
					//reset if we ran out of applications or advance counter
					if(indexNmberOfApplicationsCounter==indexNmberOfApplications){
						indexNmberOfApplicationsCounter =0;
					}else{
						indexNmberOfApplicationsCounter++;
					}
									
					request.setComponentId("1");
					request.setApiId("1_"+pathQty);
					request.setTime(0);
					request.setDataToTransfer(100);
	
					device.addRequests(request.build());
					requestQtyCounter--;
				}
				pathQty --;
			}
			
			workload.addDevices(device.build());
			deviceQty--;
		}

		return workload.build();

	}
	
	
	
}
