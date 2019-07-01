/**
 * 
 */
package eu.recap.sim.applicationworkload;

import eu.recap.sim.models.ApplicationModel.Application.Component;
import eu.recap.sim.models.ApplicationModel.Application.Component.Api;
import eu.recap.sim.models.WorkloadModel.Request;

/**
 * Generates application resource demands using dummy data for testing
 * 
 * @author Sergej Svorobej
 *
 */
public class ApplicationWorkloadDistributionDummy extends ApplicationWorkloadDistributionAbstract{

	@Override
	public Integer getMipsLoad(Component component, Api api) {
		// TODO Auto-generated method stub
		return 10;
	}


	@Override
	public Integer getIopsLoad(Component component, Api api) {
		// TODO Auto-generated method stub
		return 10;
	}



	@Override
	public Integer getDataLoad(Component component, Api api) {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public Integer getDataLoad(Component component, Api api, Request request) {
		// TODO Auto-generated method stub
		return 10;
	}
	

}
