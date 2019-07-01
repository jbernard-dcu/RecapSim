/**
 * 
 */
package eu.recap.sim.applicationworkload;

import java.util.concurrent.ThreadLocalRandom;


/**
 * Used to create an application logic behaviour based on measured historical data or probability distributions 
 * 
 * @author Sergej Svorobej
 *
 */
public abstract class ApplicationWorkloadDistributionAbstract implements IApplicationWorkloadDistribution{
	
	/**
	 * Generates random integer from supplied range
	 * @param min
	 * @param max
	 * @return
	 */
	public Integer getRandomInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
	

}
