/**
 * 
 */
package eu.recap.sim.applicationworkload;

import eu.recap.sim.models.ApplicationModel.Application.Component;
import eu.recap.sim.models.ApplicationModel.Application.Component.Api;
import eu.recap.sim.models.WorkloadModel.Request;

/**
 * 
 * 
 * @author Sergej Svorobej
 *
 */
public interface IApplicationWorkloadDistribution {
	
	//returns number of MIPS to assign to cloudlet
	public Integer getMipsLoad(Component component, Api api);
	//returns number of IOPS to assign to cloudlet
	public Integer getIopsLoad(Component component, Api api);
	
	//returns number of sent data assigned to cloudlet
	public Integer getDataLoad(Component component, Api api);
	//returns number of sent data assigned to cloudlet based on the initial device(user) request
	public Integer getDataLoad(Component component, Api api, Request request);

}
