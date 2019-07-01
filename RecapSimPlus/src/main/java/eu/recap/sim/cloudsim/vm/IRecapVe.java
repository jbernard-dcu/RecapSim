/**
 * 
 */
package eu.recap.sim.cloudsim.vm;

import org.cloudbus.cloudsim.vms.Vm;

/**
 * RECAP interface that adds notion of deployed application 
 * 
 * @author Sergej Svorobej
 *
 */
public interface IRecapVe extends Vm{

	
	String getApplicationID();
	IRecapVe setApplicationID(String applicationId);
	
	String getApplicationComponentID();
	IRecapVe setApplicationComponentID(String applicationComponentId);
	
	boolean isLoadbalancer();
	
}
