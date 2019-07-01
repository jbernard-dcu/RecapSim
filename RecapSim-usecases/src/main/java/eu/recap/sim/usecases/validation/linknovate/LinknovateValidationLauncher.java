/**
 * 
 */
package eu.recap.sim.usecases.validation.linknovate;



import eu.recap.sim.RecapSim;
import eu.recap.sim.experiments.ExperimentHelpers;
import eu.recap.sim.models.ApplicationModel.ApplicationLandscape;
import eu.recap.sim.models.WorkloadModel.Workload;
import eu.recap.sim.models.ExperimentModel.Experiment;
import eu.recap.sim.models.InfrastructureModel.Infrastructure;

/**
 * 
 * @author Malika
 *
 */
public class LinknovateValidationLauncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		/* Create Infrastructure
		 * We don't know as it is hosted on public cloud
		 * We assume all the VMs are deployed on separate nodes which amounts to 8 nodes
		 */
		int nSites = 1; 
		int nNodesPerSite = 8;	
		long startTime = System.currentTimeMillis();
		Infrastructure rim = ExperimentHelpers.GenerateLinkNovateInfrastructure("LNK-Infrastructure", nSites, nNodesPerSite);
		
		/*
		 * Application model consists of: 
		 * 1x Web Server (98F9): 8 cores, 28 GB RAM, 181 GB storage
		 * 1x ES Client (77A0): 16 cores, 112 GB RAM, 181 GB storage
		 * 6x Data Nodes (2A17, BA99, F421, 9A4F, 318C, AB2F): 8 cores, 28 GB RAM, 181 GB storage
		 */
		int nApps = 1;
		ApplicationLandscape ram = LinknovateValidarionRAM.GenerateLinknovateValidationApplication(nApps, rim);
		
		//int deviceQty = 1; 
		//int totalRequests = 10;
		//int pathQty = 6;
		//Workload rwm = ExperimentHelpers.GenerateLinknovateValidationDeviceBehavior(deviceQty,totalRequests,pathQty,ram);
		//Workload rwm = LinknovateValidationRWM.GenerateLinknovateValidationSimpleDeviceBehavior(deviceQty, 1, 6, ram);
		//Workload rwm = LinknovateValidationRWM_LogAccess.GenerateLinknovateValidationDeviceBehaviorLogRequestOneToOne(ram);
		
		int numberDataNodesPerRequest=4;
		Workload rwm = LinknovateValidationRWM_LogAccess.GenerateLinknovateValidationDeviceBehaviorLogRequestTrueOneToManyToOne(numberDataNodesPerRequest,ram);
		
		
		
		Experiment config = ExperimentHelpers.GenerateConfiguration("LNK-Validation-Config-"+startTime, 200,rim,ram,rwm);//duration only will work if simulation runs longer
		
		long stopTime = System.currentTimeMillis();
		
		
		System.out.println("Model generation took: "+(stopTime-startTime)+"ms");
					
		//run the example
		RecapSim recapExperiment = new RecapSim();

		String simulationId = recapExperiment.StartSimulation(config);
		
		System.out.println("Simulation is:"+ recapExperiment.SimulationStatus(simulationId));
		
		
	}

}
