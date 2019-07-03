package Classes;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

//imports depuis Workload generator
import Classes.Query;
import Classes.Long;
import Classes.WorkloadGen;

public class GeneralLauncherTest {
	
	/* TODO
	 * Define parameters
	 */

	
	public static void main(String[] args) {
		
		/*TODO
		 * Follow the main of Linknovate, adding tunable parameters
		 */
		
		final int nSites=1;
		final int nNodesPerSite=1;
		
		long startTime=System.currentTimeMillis();
		
	
		
		//WORKLOAD GENERATION
		
		/*TODO
		 * Use the workloads of RecapSim
		 */
		final int nbTermSet=1000;
		final int nbQuery=1000;
		
		List<Long> termSet=new ArrayList<Long>();
		List<Double> termFreq=new ArrayList<Double>();
		
		//TODO : change list creation
		for(int i=1;i<=nbTermSet;i++) {
			Long w=new Long(i);
			termSet.add(w);
			termFreq.add(1./w.getId());
		}
		
		WorkloadGen w=new WorkloadGen(termSet,termFreq,nbQuery);
		
		
		
		//
		
		
		
		
		
		
	}

}
