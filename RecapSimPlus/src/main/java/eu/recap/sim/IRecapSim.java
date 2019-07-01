/**
 * 
 */
package eu.recap.sim;

import eu.recap.sim.models.ExperimentModel.Experiment;

/**
 * The interface provides methods for simulation interaction
 * 
 * @author Sergej Svorobej
 * @version 1.0
 */
public interface IRecapSim {
	
	/**
	 * Method which starts simulation process
	 * @param rim Recap Infrastructure Model object
	 * @param ram Recap Application Model object
	 * @param rwk Recap Workload Model object
	 * @param config Configuration setup
	 * @return the ID of simulation process
	 */
	public String StartSimulation(Experiment experiment);
	
	/**
	 * Method for checking the status of simulation by ID
	 * @param simulationId the ID of simulation 
	 * @return the status of the simulation experiment "RUNNING","FINISHED","NOTFOUND"
	 */
	public SimulationStatus SimulationStatus (String simulationId);
	
	/**
	 * Simulation status enums "RUNNING","FINISHED","NOTFOUND" 
	 *
	 */
	public enum SimulationStatus{
		RUNNING, FINISHED , NOTFOUND	
	}
}
