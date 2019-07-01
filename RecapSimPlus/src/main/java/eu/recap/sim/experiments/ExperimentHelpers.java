/**
 * 
 */
package eu.recap.sim.experiments;

import java.io.File;
import java.io.FileNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import eu.recap.sim.models.WorkloadModel.Device;
import eu.recap.sim.models.LocationModel.Location;
import eu.recap.sim.models.WorkloadModel.Request;
import eu.recap.sim.models.ApplicationModel.VeFlavour;
import eu.recap.sim.models.WorkloadModel.Workload;
import eu.recap.sim.models.ExperimentModel.Experiment;
import eu.recap.sim.models.InfrastructureModel.Infrastructure;
import eu.recap.sim.models.InfrastructureModel.Link;
import eu.recap.sim.models.InfrastructureModel.Node;
import eu.recap.sim.models.InfrastructureModel.ResourceSite;
import eu.recap.sim.models.InfrastructureModel.ResourceSite.SiteLevel;
import eu.recap.sim.models.InfrastructureModel.Node.CPU;
import eu.recap.sim.models.InfrastructureModel.Node.Core;
import eu.recap.sim.models.InfrastructureModel.Node.Memory;
import eu.recap.sim.models.InfrastructureModel.Node.Storage;
import eu.recap.sim.cloudsim.host.IRecapHost;
import eu.recap.sim.models.ApplicationModel.Application;
import eu.recap.sim.models.ApplicationModel.ApplicationLandscape;

/**
 * Class provides static methods to generate dummy data and help compose
 * experiments
 * 
 * @author Sergej Svorobej
 *
 */
/**
 * @author Sergej Svorobej
 *
 */
public class ExperimentHelpers {


	
	/*
	 * LINKNOVATE
	 */
	
	
	
	
	
/**
 *  Get host from the list by id	
 * @param hostId
 * @param hostList
 * @return
 */
	public static IRecapHost GetHostByIdFromList(String recapNodeId, List<IRecapHost> hostList) {
		for (IRecapHost host:hostList){
			
			if(recapNodeId.equals("")){
				return null;
			}
			
			if(host.getRecapNodeId().equals(recapNodeId)){
				return host;
			}
		}
		//no host found
		return null;
	}
	
	/**
	 * Submit device location at the time of request and get the closest location of data centre where application
	 * is running 
	 * 
	 * @param latitude
	 * @param longitude
	 * @param rim
	 * @return
	 */
	private static String getApplicationIdByCoordinates(double latitude, double longitude, Infrastructure rim, ApplicationLandscape ram){
		double shortestDistance = -1;
		String closestApplicationId = "-1";
		
		
		for(ResourceSite site:rim.getSitesList()){
			double rimLatitude =  site.getLocation().getLatitude();
			double rimLongitude = site.getLocation().getLongitude();
			//root((latitude - rimLatitude)sq + (longitude-rimLongitude)sq)
			double distance = Math.sqrt((Math.pow(latitude - rimLatitude, 2) + Math.pow(longitude-rimLongitude, 2)));
			//set an initial shortest distance as first element
			if (shortestDistance<0){
				shortestDistance=distance;
			}
			//if new distance is shortest lookup application ID and set it alongside
			if(distance<=shortestDistance){
				shortestDistance = distance;
				
				//map application to the DC location
				for (Application application: ram.getApplicationsList()){
					//we assume each application is deployed entirely on the same DC
					if (site.getId().equals(application.getComponents(0).getDeployment().getSiteId())){
						closestApplicationId = application.getApplicationId();					
					}
					
				}
				
			}
			
			
			
		}
		
		return closestApplicationId;
	}
	
	/** returns difference between two dates in seconds
	 * @param startingDateTime
	 * @param currentDateTime
	 * @return
	 */
	public static int differenceInSeconds(LocalDateTime startingDateTime, LocalDateTime currentDateTime){
		
		int daysDifference = (currentDateTime.getDayOfMonth() - startingDateTime.getDayOfMonth())*86400;
		int hourDifference = (currentDateTime.getHour() - startingDateTime.getHour())*3600;
		int minutesDifference = (currentDateTime.getMinute() -startingDateTime.getMinute())*60;
		int secondDifference = currentDateTime.getSecond() - startingDateTime.getSecond();
		
		
		return daysDifference+hourDifference+minutesDifference+secondDifference;
	}
	
	
	
	/**
	 * Creates test Infrastructure model for tieto usecase
	 * 
	 * @param name
	 * @param numberOfSites
	 * @param numberOfNodesPerSite
	 * @return the populated Infrastructure model
	 */
	public static Infrastructure GenerateLinkNovateInfrastructure(String name, int numberOfSites, int numberOfNodesPerSite) {
		final int cpuFrequency = 3000; //MIPS or 2.6 GHz
		final int cpuCores = 80; 
		final int ram = 2048_000; // host memory (MEGABYTE)
		final int hdd = 1000000_000; // host storage (MEGABYTE)
		final int bw = 10_000; // in 10Gbit/s
		
		Infrastructure.Builder infrastructure = Infrastructure.newBuilder();
		infrastructure.setName(name);
		
		//only one link where all sites are connected
		Link.Builder link = Link.newBuilder();
		link.setId("0");
		link.setBandwith(bw);
		
		//create sites
	    for(int i=0; i<numberOfSites; i++){
            
	    	ResourceSite.Builder site = ResourceSite.newBuilder();
	    	site.setName("Site_"+i);
	    	site.setId(i+"");
	    	
	    	Location.Builder geolocation = Location.newBuilder();
	    	geolocation.setLatitude(i);
	    	geolocation.setLongitude(i);
	    	site.setLocation(geolocation.build());
	    	site.setHierarchyLevel(SiteLevel.Edge);
	    	
	    	//create nodes
	    	for(int j=0; j<numberOfNodesPerSite; j++){
	    		
	    		Node.Builder node = Node.newBuilder();
	    		node.setName("Node_"+i+"_"+j);
	    		node.setId(i+"_"+j);

	    		
	    		CPU.Builder cpu = CPU.newBuilder();
	    		cpu.setName("Xeon_"+i+"_"+j);
	    		cpu.setId(i+"_"+j);
	    		cpu.setMake("Intel");
	    		cpu.setRating("12345");
	    		cpu.setFrequency(cpuFrequency);
	    		//create cores
	    		for(int e=0; e<cpuCores; e++){
	    			Core.Builder core = Core.newBuilder();
	    			core.setId(i+"_"+j+"_"+e);
	    			cpu.addCpuCores(core.build());
	    		}
	    		
	    		
	    		Memory.Builder memory = Memory.newBuilder();
	    		memory.setId(i+"_"+j);
	    		memory.setCapacity(ram);
	    		
	    		Storage.Builder storage = Storage.newBuilder();
	    		storage.setId(i+"_"+j);
	    		storage.setSize(hdd);
	    		
	    		
	    		//add resources to node
	    		node.addProcessingUnits(cpu.build());
	    		node.addMemoryUnits(memory.build());
	    		node.addStorageUnits(storage.build());
	    		
	    		//add node to site
	    		site.addNodes(node.build());
	    	}
	    	ResourceSite builtSite = site.build();
	    	//add sites to infrastructure
	    	infrastructure.addSites(builtSite);
	    	
	    	//add sites to link by id
	    	link.addConnectedSites(builtSite);
	    	
	    }
		
	    infrastructure.addLinks(link.build());
		return infrastructure.build();

	}


	/**
	 * Generates test configuration object
	 * 
	 * @param name
	 * @param duration
	 * @param rwm 
	 * @param ram 
	 * @param rim 
	 * @return
	 */
	public static Experiment GenerateConfiguration(String name, double duration, Infrastructure rim, ApplicationLandscape ram, Workload rwm) {
		Experiment.Builder configuration = Experiment.newBuilder();
		configuration.setName(name);
		configuration.setDuration(duration);
		configuration.setApplicationLandscape(ram);
		configuration.setInfrastructure(rim);
		configuration.setWorkload(rwm);

		return configuration.build();

	}
	


}
