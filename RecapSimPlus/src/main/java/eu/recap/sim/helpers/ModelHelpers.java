/**
 * 
 */
package eu.recap.sim.helpers;


import java.util.List;
import eu.recap.sim.models.ApplicationModel.Application;
import eu.recap.sim.models.ApplicationModel.Application.Component;
import eu.recap.sim.models.ApplicationModel.Application.Component.Api;
import eu.recap.sim.models.InfrastructureModel.Infrastructure;
import eu.recap.sim.models.InfrastructureModel.Link;
import eu.recap.sim.models.InfrastructureModel.ResourceSite;
import eu.recap.sim.models.WorkloadModel.Device;
import eu.recap.sim.models.WorkloadModel.Request;
import eu.recap.sim.models.WorkloadModel.Workload;

/**
 * @author Sergej Svorobej
 *
 */
public class ModelHelpers {
	
	/**
	 * Method finds a link to use between sites when sending a cloudlet between them
	 * 
	 * @param ram
	 * @param sourceSiteId
	 * @param destinationSiteId
	 * @return
	 */
	public static Link getNetworkLink(Infrastructure rim, String sourceSiteId, String destinationSiteId) {
		
		List<Link> linksList = rim.getLinksList();
		boolean sourceSiteIdMatch=false;
		boolean destinationSiteIdMatch =false;
		
		for(Link link:linksList){

			
			//check if both sites 
			for (ResourceSite site:link.getConnectedSitesList()){
				
				if(site.getName().equals(sourceSiteId)){
					sourceSiteIdMatch = true;
				}
				if(site.getName().equals(destinationSiteId)){
					destinationSiteIdMatch = true;
				}
				
			}
			
			//match found
			if(sourceSiteIdMatch&&destinationSiteIdMatch){
				return link;
			}else{
				//match not found resetting counters
				sourceSiteIdMatch=false;
				destinationSiteIdMatch=false;
			}
			
			
		}
		
		System.out.printf("Fatal error: no connection found between Site %s and Site %s . Stopping simulation.",sourceSiteId,destinationSiteId);
		throw new RuntimeException("Fatal error: no connection found between Site"+sourceSiteId+" and Site "+destinationSiteId+" . Stopping simulation.");
		
	}


	/**
	 * Iterates through list of applications to find the required API
	 * 
	 * @param applications
	 * @param componentId
	 * @param apiId
	 * @return
	 */
	public static Api getApiTask(List<Application> applications, String applicationId, String componentId,
			String apiId) {

		for (Application application : applications) {
			if (application.getApplicationId().equals(applicationId)) {
				for (Component component : application.getComponentsList()) {

					if (component.getComponentId().equals(componentId)) {

						for (Api api : component.getApisList()) {

							if (api.getApiId().equals(apiId)) {

								return api;
							}

						}

					}

				}

			} // app if
		} // app for

		// if no results found return null
		return null;

	}
	
	
	/**
	 * Iterates through list of requests to find the required request
	 * 
	 * @param workload
	 * @param requestId
	 * @param deviceId
	 * @return
	 */
	public static Request getRequestTask(List<Device> devices, String deviceId, int requestId) {

		for (Device device : devices) {
			if (device.getDeviceId().equals(deviceId)) {
				for (Request request :device.getRequestsList()) {

					if (request.getRequestId() == requestId) {

								return request;
					}

				}

			}

		}
		

		// if no results found return null
		return null;

	}

}
