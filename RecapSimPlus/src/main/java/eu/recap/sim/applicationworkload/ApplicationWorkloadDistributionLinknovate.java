/**
 * 
 */
package eu.recap.sim.applicationworkload;

import eu.recap.sim.models.ApplicationModel.Application.Component;
import eu.recap.sim.models.ApplicationModel.Application.Component.Api;
import eu.recap.sim.models.WorkloadModel.Request;

/**
 * Generates application resource demands using monitored data analysis Methods
 * are used to create cloudlet resource demands such as MIPS, IOPS and data to
 * transfer Methods are used at the RECAP and cloudsim model translation points
 * in "RecapSim.java" file
 * 
 * @author Sergej Svorobej
 *
 */
public class ApplicationWorkloadDistributionLinknovate extends ApplicationWorkloadDistributionAbstract {
	
	

	@Override
	public Integer getMipsLoad(Component component, Api api) {
		int mips=0;
		switch (component.getComponentId()) {
		case "1":
			mips = this.getRandomInt(1, 10);
			break;
		case "2":
			mips = 2;
			break;			
		case "3":
			mips = 3;
			break;
		case "4":
			mips = 4;
		case "5":
			mips = 5;
			break;
		case "6":
			mips = 6;
			break;
		case "7":
			mips = 7;
			break;
		case "8":
			mips = 8;
			break;
		default:
			System.out.println("Invalid Linknovate aplication component ID");
			return null;
		}

		return mips;
	}



	@Override
	public Integer getIopsLoad(Component component, Api api) {
		int iops=0;
		switch (component.getComponentId()) {
		case "1":
			iops = this.getRandomInt(1, 10);
			break;
		case "2":
			iops = 2;
			break;			
		case "3":
			iops = 3;
			break;
		case "4":
			iops = 4;
		case "5":
			iops = 5;
			break;
		case "6":
			iops = 6;
			break;
		case "7":
			iops = 7;
			break;
		case "8":
			iops = 8;
			break;
		default:
			System.out.println("Invalid Linknovate aplication component ID");
			return null;
		}

		return iops;
	}


	@Override
	public Integer getDataLoad(Component component, Api api) {
		int data=0;
		switch (component.getComponentId()) {
		case "1":
			data = this.getRandomInt(1, 10);
			break;
		case "2":
			data = 2;
			break;			
		case "3":
			data = 3;
			break;
		case "4":
			data = 4;
		case "5":
			data = 5;
			break;
		case "6":
			data = 6;
			break;
		case "7":
			data = 7;
			break;
		case "8":
			data = 8;
			break;
		default:
			System.out.println("Invalid Linknovate aplication component ID");
			return null;
		}

		return data;
	}

	@Override
	public Integer getDataLoad(Component component, Api api, Request request) {
		return Integer.decode(request.getApiId());
	}

}
