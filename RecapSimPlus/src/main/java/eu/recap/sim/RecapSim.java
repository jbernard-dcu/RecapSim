/**
 * 
 */
package eu.recap.sim;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.Identifiable;
import org.cloudbus.cloudsim.core.events.CloudSimEvent;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import eu.recap.sim.helpers.Log;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel.Unit;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelStochastic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.TableColumn;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventInfo;

import eu.recap.sim.applicationworkload.ApplicationWorkloadDistributionDummy;
import eu.recap.sim.applicationworkload.ApplicationWorkloadDistributionLinknovate;
import eu.recap.sim.applicationworkload.IApplicationWorkloadDistribution;
import eu.recap.sim.cloudsim.RecapDatacenterBroker;
import eu.recap.sim.cloudsim.RecapVmAllocationPolicy;
import eu.recap.sim.cloudsim.cloudlet.RecapCloudlet;
import eu.recap.sim.cloudsim.cloudlet.IRecapCloudlet;
import eu.recap.sim.cloudsim.host.IRecapHost;
import eu.recap.sim.cloudsim.host.RecapHost;
import eu.recap.sim.cloudsim.vm.IRecapVe;
import eu.recap.sim.cloudsim.vm.RecapVe;
import eu.recap.sim.experiments.ExperimentHelpers;
import eu.recap.sim.helpers.ModelHelpers;
import eu.recap.sim.helpers.RecapCloudletsTableBuilder;
import eu.recap.sim.models.ApplicationModel.Application;

import eu.recap.sim.models.ApplicationModel.ApplicationLandscape;
import eu.recap.sim.models.WorkloadModel.Device;
import eu.recap.sim.models.WorkloadModel.Request;
import eu.recap.sim.models.WorkloadModel.Workload;
import eu.recap.sim.models.ApplicationModel.Application.Component;
import eu.recap.sim.models.ApplicationModel.Application.Component.Api;
import eu.recap.sim.models.ExperimentModel.Experiment;
import eu.recap.sim.models.InfrastructureModel.Infrastructure;
import eu.recap.sim.models.InfrastructureModel.Link;
import eu.recap.sim.models.InfrastructureModel.Node;
import eu.recap.sim.models.InfrastructureModel.Node.CPU;
import eu.recap.sim.models.InfrastructureModel.Node.Core;
import eu.recap.sim.models.InfrastructureModel.Node.Memory;
import eu.recap.sim.models.InfrastructureModel.Node.Storage;
import eu.recap.sim.models.InfrastructureModel.ResourceSite;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The general class where RECAP simulation is started using RECAP based models
 * and configurations
 * 
 * @author Sergej Svorobej
 *
 */
public class RecapSim implements IRecapSim {
	public static int timeUnits = 1000; // multiply by this to get in millisecond

	private final CloudSim simulation;
	private List<IRecapVe> veList;
	private List<IRecapCloudlet> cloudletList;
	private List<IRecapCloudlet> onTheFlycloudletList;
	DatacenterBroker broker0;
	private List<Datacenter> datacenterList;
	private List<IRecapHost> hostList;
	private HashMap<Long, Boolean> finishedCloudlets;
	// linkID list of cloudlets that are still being transferred
	private HashMap<String, List<IRecapCloudlet>> activeLinkCloudlets;
	private Infrastructure rim;
	private ApplicationLandscape ram;
	private Workload rwm;
	private Experiment config;
	private IApplicationWorkloadDistribution appWorkloadDistribution;

	private Map<Integer, Integer> executedDataNodesPerRequest = new HashMap<Integer, Integer>();

	// adding RAM and Bw utilisation.
	// Inspired by
	// https://github.com/manoelcampos/cloudsim-plus/blob/master/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/resourceusage/VmsRamAndBwUsageExample.java
	private Map<Vm, Map<Double, Double>> allVmsRamUtilizationHistory; // to record ram utilisation
	private Map<Vm, Map<Double, Double>> allVmsBwUtilizationHistory; // to record bandwidth utilisation

	public RecapSim() {
		Log.printLine("Starting RecapSim...");
		this.simulation = new CloudSim();
		simulation.addOnClockTickListener(this::onClockTickListener);
		// simulation.addOnEventProcessingListener(listener)
		// InitiateLists
		this.veList = new ArrayList<>();
		this.cloudletList = new ArrayList<>();
		this.onTheFlycloudletList = new ArrayList<>();
		this.datacenterList = new ArrayList<>();
		this.hostList = new ArrayList<>();
		this.activeLinkCloudlets = new HashMap<String, List<IRecapCloudlet>>();
		// EventListener<EventInfo> listener
		// this.simulation.addOnClockTickListener(this::onSimTimeAdvanceListener);

		/*
		 * Creates a Broker accountable for submission of VMs and Cloudlets on behalf of
		 * a given cloud user (customer).
		 */
		// CHAGE
		broker0 = new RecapDatacenterBroker(simulation);
		// broker0 = new DatacenterBrokerSimple(simulation);
		// bugfix list
		finishedCloudlets = new HashMap<Long, Boolean>();

	}

	@Override
	public String StartSimulation(Experiment experiment) {
		long conversionStartTime = System.currentTimeMillis();
		System.out.println("Starting model conversion...");
		this.rim = experiment.getInfrastructure();
		this.ram = experiment.getApplicationLandscape();
		this.rwm = experiment.getWorkload();
		this.config = experiment;

		// check for Linknovate or Tieto
		if (ram.getNotes().equals("linknovate")) {
			this.appWorkloadDistribution = new ApplicationWorkloadDistributionLinknovate();
		} else {
			this.appWorkloadDistribution = new ApplicationWorkloadDistributionDummy();
		}

		// Generate the simulation ID from the experiment name and the time
		// stamp
		String simulationID = config.getName() + "_" + (System.currentTimeMillis() / 1000L);

		/**
		 * Create Infrastructure
		 */

		// bw the Bandwidth (BW) capacity in Megabits/s
		// TO-DO: see how this needed for node
		// int bw = rim.getLinksList().get(0).getBandwith();
		int nodeBw = 10_000; // 100 Mbps = 12.5 MB/s
		int veBw = 100;
		for (ResourceSite site : rim.getSitesList()) {
			// storing all the dc hosts temporary
			List<IRecapHost> siteHostList = new ArrayList<>();

			// generating hosts
			for (Node node : site.getNodesList()) {
				// get PEs
				List<Pe> pesList = new ArrayList<>(); // List of CPU cores
				for (CPU cpu : node.getProcessingUnitsList()) {
					for (Core core : cpu.getCpuCoresList()) {
						pesList.add(new PeSimple(cpu.getFrequency(), new PeProvisionerSimple()));
					}
				}

				// get memory
				int totalNodeMemory = 0;
				for (Memory memory : node.getMemoryUnitsList()) {
					totalNodeMemory = totalNodeMemory + memory.getCapacity();
				}

				// get storage
				int totalNodeStorage = 0;
				for (Storage storage : node.getStorageUnitsList()) {
					totalNodeStorage = totalNodeStorage + storage.getSize();
				}

				IRecapHost host = (IRecapHost) new RecapHost(totalNodeMemory, nodeBw, totalNodeStorage, pesList)
						.setRamProvisioner(new ResourceProvisionerSimple())
						.setBwProvisioner(new ResourceProvisionerSimple()).setVmScheduler(new VmSchedulerTimeShared());

				host.setRecapNodeId(node.getId());
				host.setRecapNodeName(node.getName());
				host.setRecapResourceSiteId(site.getId());
				host.setRecapResourceSiteName(site.getName());

				// add to site host list
				siteHostList.add(host);

			}

			Datacenter datacenter = new DatacenterSimple(simulation, siteHostList, new RecapVmAllocationPolicy());
			DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple(datacenter);
			datacenter.setName(site.getName());
			// record utilisation every 5 seconds
			datacenter.setSchedulingInterval(100);
			// TO-DO: extend datacentre class to mach with site IDs
			// datacenter.setId(1);

			// adding to lists
			this.datacenterList.add(datacenter);
			this.hostList.addAll(siteHostList);
		} // end of Infrastructure creation

		/**
		 * Create VEs and deploy applications
		 * 
		 */

		// assign application components to VEs in order
		for (Application application : ram.getApplicationsList()) {
			for (Component component : application.getComponentsList()) {

				/*
				 * Creates VEs
				 */
				IRecapHost host = ExperimentHelpers.GetHostByIdFromList(component.getDeployment().getNodeId(),
						this.hostList);

				// TO-DO: in future we will implement placement policy to take care of this
				if (host == null) {
					Log.printLine("Error: No component deployment found for component #" + component.getComponentName()
							+ ". Strict deployment for now");
					System.exit(1);

				}

				component.getDeployment().getNodeId();

				RecapVe ve = new RecapVe(veList.size(), broker0, host.getPeList().get(0).getCapacity(),
						component.getFlavour().getCores(), component.getFlavour().getMemory(), veBw,
						component.getFlavour().getStorage(), "xen", new CloudletSchedulerTimeShared());

				// setting bandwith to 0 because we dont calculate bandwith between VMs only
				// between sites
				ve.setBw(veBw);
				ve.setHost(host);
				ve.setApplicationID(application.getApplicationId());
				ve.setApplicationComponentID(component.getComponentId());
				ve.getUtilizationHistory().enable();
				this.veList.add(ve);

				Log.printFormattedLine("ApplicationID:" + application.getApplicationId() + " Component:"
						+ component.getComponentId() + " Placed on VE:" + ve.getId());

			}

		}
		allVmsRamUtilizationHistory = initializeUtilizationHistory(this.veList); // initialize Ram utilisation recoder
		allVmsBwUtilizationHistory = initializeUtilizationHistory(this.veList); // initialize Bandwidth utilisation
																				// recoder

		/*
		 * Create initial requests for the submitted applications from Workload model
		 * 
		 */
		for (Device device : rwm.getDevicesList()) {
			for (Request request : device.getRequestsList()) {
				for (Application application : ram.getApplicationsList()) {
					// check that app matches the app in the request
					if (application.getApplicationId().equals(request.getApplicationId())) {

						for (Component component : application.getComponentsList()) {
							// check that component matches
							if (component.getComponentId().equals(request.getComponentId())) {

								for (Api api : component.getApisList()) {
									// check that api matches
									if (api.getApiId().equals(request.getApiId())) {
										int mi = api.getMips();
										int io = api.getIops();
										long outputFileSize = api.getDataToTransfer();
										long cloudletDelay = request.getTime();
										long inputFileSize = request.getDataToTransfer();
										IRecapVe requestVe = getMatchingVeId(application.getApplicationId(),
												component.getComponentId());
										IRecapCloudlet cl = createCloudlet(requestVe, mi, inputFileSize, outputFileSize,
												io, cloudletDelay, application.getApplicationId(),
												component.getComponentId(), api.getApiId(), api.getRam(),
												request.getRequestId(), device.getDeviceId());

										// System.out.println(application.getApplicationId()+component.getComponentId()+api.getApiId());
										cloudletList.add(cl);
									}
								}
							}
						}
					}
				}
			}
		}

		broker0.submitVmList(veList);
		broker0.submitCloudletList(cloudletList);

		System.out.println(
				"Model cloudsim model creation took: " + (System.currentTimeMillis() - conversionStartTime) + "ms");

		System.out.println("Simulation started...");
		long startSimTime = System.currentTimeMillis();

		/* Starts the simulation and waits all cloudlets to be executed. */
		final double finishTime = simulation.start();
		System.out.println("Simulation took: " + (System.currentTimeMillis() - startSimTime) + "ms real time for "
				+ simulation.clock() + "s simtime.");

		/*
		 * Prints results when the simulation is over (you can use your own code here to
		 * print what you want from this cloudlet list)
		 */

		List<RecapCloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
		new RecapCloudletsTableBuilder(finishedCloudlets, this.rim, this.ram, this.rwm, this.config).build();

		// print Host CPU UTIL
		// showCpuUtilizationForAllHosts();

		// print VM utilisation
		// showCpuUtilizationForAllVms(finishTime,veList);
		// showRamUtilizationForAllVms(finishTime,veList);
		// showBwUtilizationForAllVms(finishTime,veList);

		// print VM resource consumption as a Table
		showTableCpuUtilizationForAllVms(finishTime, veList);
		showTableRamUtilizationForAllVms(finishTime, veList);

		// output JSON File
		outputTableAsJSON(finishedCloudlets, this.rim, this.ram, this.rwm, this.config);

		outputTableForExcel(finishedCloudlets, this.rim, this.ram, this.rwm, this.config);

		return simulationID;
	}

	/**
	 * Shows CPU utilization of all hosts into a given Datacenter.
	 */
	private void showCpuUtilizationForAllHosts() {
		// System.out.println("\nHosts CPU utilization history for the entire simulation
		// period");
		int numberOfUsageHistoryEntries = 0;
		for (Host host : hostList) {
			double mipsByPe = host.getTotalMipsCapacity() / (double) host.getNumberOfPes();
			System.out.printf("Host %d: Number of PEs %2d, MIPS by PE %.0f\n", host.getId(), host.getNumberOfPes(),
					mipsByPe);
			for (Map.Entry<Double, Double> entry : host.getUtilizationHistorySum().entrySet()) {
				final double time = entry.getKey();
				final double cpuUsage = entry.getValue() * 100;
				numberOfUsageHistoryEntries++;
				// System.out.printf("\tTime: %4.1f CPU Utilization: %6.2f%%\n", time,
				// cpuUsage);
			}
			System.out.println("--------------------------------------------------");
		}

		if (numberOfUsageHistoryEntries == 0) {
			System.out.println("No CPU usage history was found");
		}
	}

	/**
	 * Shows CPU utilization of all VMs into a given Datacenter.
	 */
	private void showCpuUtilizationForAllVms(final double simulationFinishTime, List<IRecapVe> veList) {
		System.out.println("\nVMs CPU utilization history for the entire simulation period\n");
		int numberOfUsageHistoryEntries = 0;
		for (Vm vm : veList) {
			System.out.printf("VM %d\n", vm.getId());
			if (vm.getUtilizationHistory().getHistory().isEmpty()) {
				System.out.println("\tThere isn't any usage history");
				continue;
			}

			for (Map.Entry<Double, Double> entry : vm.getUtilizationHistory().getHistory().entrySet()) {
				final double time = entry.getKey() / timeUnits;
				final double vmCpuUsage = entry.getValue() * 100;
				if (vmCpuUsage > 0) {
					numberOfUsageHistoryEntries++;
					System.out.printf("\tTime: %2.0f CPU Utilization: %6.2f%%\n", time, vmCpuUsage);
				}
			}
		}

		if (numberOfUsageHistoryEntries == 0) {
			System.out.println("No CPU usage history was found");
		}
	}

	private IRecapVe getMatchingVeId(String applicationId, String componentId) {

		// select matching VE where to send 1st
		// task
		IRecapVe veMatch = null;
		int matchingVEfound = 0;
		for (IRecapVe ve : veList) {
			if (ve.getApplicationID().equals(applicationId) && ve.getApplicationComponentID().equals(componentId)) {
				veMatch = ve;
				matchingVEfound++;
			}
		}

		// WARNING: when unable to match a VE
		// with the task
		if (matchingVEfound > 1) {
			Log.printLine(
					"Error: More than one VE found with the same application and component ID, stopping simulation");
			System.exit(1);
		} else if (matchingVEfound == 0) {
			Log.printLine("Error: No VE found with the same application and component ID,stopping simulation");
			System.exit(1);
		}

		return veMatch;
	}

	private IRecapCloudlet createCloudlet(Vm vm, long mi, long inputFileSize, long outputFileSize, long io,
			double submissionDelay, String applicationId, String componentId, String apiId, double ram_cloudlet,
			int requestId, String originDeviceId) {
		// final long length = 10000; //in Million Structions (MI)
		// final long fileSize = 300; //Size (in bytes) before execution
		// final long outputSize = 300; //Size (in bytes) after execution
		int numberOfCpuCores = (int) vm.getNumberOfPes(); // cloudlet will
															// use all the
															// VM's CPU
															// cores
		numberOfCpuCores = 1;

		// Defines how CPU, RAM and Bandwidth resources are used
		// Sets the same utilization model for all these resources.
		// UtilizationModel utilization = new UtilizationModelStochastic();
		UtilizationModel utilization = new UtilizationModelFull();
		UtilizationModel utilizationCpu = new UtilizationModelDynamic(Unit.ABSOLUTE, 300);
		UtilizationModel utilizationRam = new UtilizationModelDynamic(Unit.ABSOLUTE, ram_cloudlet);
		UtilizationModel utilizationBw = new UtilizationModelFull();

//		IRecapCloudlet recapCloudlet = (IRecapCloudlet) new RecapCloudlet(cloudletList.size(), mi, numberOfCpuCores)
//				.setFileSize(inputFileSize).setOutputSize(outputFileSize).setUtilizationModel(new UtilizationModelDynamic(0.1)).setVm(vm)
//				.addOnFinishListener(this::onCloudletFinishListener);

		IRecapCloudlet recapCloudlet = (IRecapCloudlet) new RecapCloudlet(cloudletList.size(), mi, numberOfCpuCores)
				.setFileSize(inputFileSize).setOutputSize(outputFileSize).setUtilizationModelCpu(utilizationCpu)
				.setUtilizationModelRam(utilizationRam).setUtilizationModelBw(utilization).setVm(vm)
				.addOnFinishListener(this::onCloudletFinishListener);

		recapCloudlet.setSubmissionDelay(submissionDelay);
		recapCloudlet.setApplicationId(applicationId);
		recapCloudlet.setApplicationComponentId(componentId);
		recapCloudlet.setApiId(apiId);
		recapCloudlet.setRequestId(requestId);
		recapCloudlet.setOriginDeviceId(originDeviceId);

		return recapCloudlet;
	}

	private void onSimTimeAdvanceListener(EventInfo eventInfo) {
		System.out.println("Second: " + eventInfo.getTime());

	}

	private void onCloudletFinishListener(CloudletVmEventInfo eventInfo) {

		// 0. Bug workaround: check if it is a second execution of the listener
		// If the entry already here then we skip it
		if (this.finishedCloudlets.containsKey(eventInfo.getCloudlet().getId())) {

			// Log.printFormattedLine("\n#Bugfix#All following entries already
			// executed for CloudletId:"+eventInfo.getCloudlet().getId()+"\n");

		} else {
			IRecapCloudlet finishedRecapCloudlet = (IRecapCloudlet) eventInfo.getCloudlet();
			RecapVe currentVe = (RecapVe) eventInfo.getVm();

			Log.printFormattedLine("Finished ApplicationId:" + finishedRecapCloudlet.getApplicationId()
					+ " ComponentId:" + finishedRecapCloudlet.getApplicationComponentId() + " apiTaskId: "
					+ finishedRecapCloudlet.getApiId());

			Log.printFormattedLine("\n#EventListener: Cloudlet %d finished running at Vm %d at time %.2f",
					finishedRecapCloudlet.getId(), currentVe.getId(), eventInfo.getTime());

			// 1. Check if the Request that has triggered the VM has next
			// cloudlet to execute
			Api currentApi = ModelHelpers.getApiTask(this.ram.getApplicationsList(),
					finishedRecapCloudlet.getApplicationId(), finishedRecapCloudlet.getApplicationComponentId(),
					finishedRecapCloudlet.getApiId());

			// check if we have a chain of application compoents attached
			if (!currentApi.getNextApiIdList().isEmpty() && !currentApi.getNextComponentIdList().isEmpty()) {
				Log.printFormattedLine("Found next component IDs:" + currentApi.getNextComponentIdList());
				Log.printFormattedLine("            with API IDs:" + currentApi.getNextApiIdList());

				// case if we only have one next API
				if (currentApi.getNextApiIdList().size() == 1 && currentApi.getNextComponentIdList().size() == 1) {
					Api nextApi = ModelHelpers.getApiTask(this.ram.getApplicationsList(),
							finishedRecapCloudlet.getApplicationId(), currentApi.getNextComponentId(0),
							currentApi.getNextApiId(0));

					Request request = ModelHelpers.getRequestTask(this.rwm.getDevicesList(),
							finishedRecapCloudlet.getOriginDeviceId(), finishedRecapCloudlet.getRequestId());
					// if next API is DataNode To ES => need to aggregate the APIs
					if (nextApi.getApiId().equals("2_2")) {
						if (!executedDataNodesPerRequest.containsKey(request.getRequestId())) {
							executedDataNodesPerRequest.put(request.getRequestId(), 1);
						} else {
							executedDataNodesPerRequest.put(request.getRequestId(),
									executedDataNodesPerRequest.get(request.getRequestId()) + 1);
						}
					}

					if ((nextApi.getApiId().equals("2_2")
							&& executedDataNodesPerRequest.get(request.getRequestId()) == request.getDataNodesCount())
							|| (!nextApi.getApiId().equals("2_2"))) {
						Log.printFormattedLine("Create one new cloudlet");

						// 2. Create cloudlet using api specs
						double delay = 0.0;
						IRecapVe targetVe = getMatchingVeId(finishedRecapCloudlet.getApplicationId(),
								currentApi.getNextComponentId(0));
						// 2b. create cloudlet

						IRecapCloudlet newRecapCloudlet = createCloudlet(targetVe, nextApi.getMips(),
								nextApi.getDataToTransfer(), nextApi.getDataToTransfer(), nextApi.getIops(), delay,
								finishedRecapCloudlet.getApplicationId(), currentApi.getNextComponentId(0),
								nextApi.getApiId(), nextApi.getRam(), finishedRecapCloudlet.getRequestId(),
								finishedRecapCloudlet.getOriginDeviceId());
						newRecapCloudlet.setBwUpdateTime(simulation.clock());

						// 2a. calculate delay based on the connection
						// TO-DO: Update transfer remaining speeds when a cloudlet finished transferring
						// through a link
						// is cloudlet being sent between DC sites?
						if (targetVe.getHost().getDatacenter().getId() != currentVe.getHost().getDatacenter().getId()) {
							// if so calculate link BW demand
							Link link = ModelHelpers.getNetworkLink(rim, currentVe.getHost().getDatacenter().getName(),
									targetVe.getHost().getDatacenter().getName());
							int linkBw = link.getBandwith();

							// get current cloudlets on the link
							List<IRecapCloudlet> listActivecloudlets;
							if (activeLinkCloudlets.containsKey(link.getId())) {

								listActivecloudlets = activeLinkCloudlets.get(link.getId());

								// clean cloudlets list that are being processed already. Cloudlets that are not
								// in status instantiated are removed from the list
								for (IRecapCloudlet cl : listActivecloudlets) {
									if (!cl.getStatus()
											.equals(org.cloudbus.cloudsim.cloudlets.Cloudlet.Status.INSTANTIATED)) {
										listActivecloudlets.remove(cl);
									}
								}
								listActivecloudlets.add(newRecapCloudlet);
								// update
								activeLinkCloudlets.put(link.getId(), listActivecloudlets);

							} else {
								// create list and add the cloudlet
								listActivecloudlets = new ArrayList<IRecapCloudlet>();
								listActivecloudlets.add(newRecapCloudlet);
								activeLinkCloudlets.put(link.getId(), listActivecloudlets);
							}

							// assume bandwidth divided equally
							double availableBandwithSliceForCloudlet = linkBw / listActivecloudlets.size();

							// bandwith speed is in Megabits per second where file size is in Bytes, so we
							// convert Megabits to Bytes by multiplying by 125000
							// calculate delay Megabits Bytes
							double ByteperSecond = 125000 * availableBandwithSliceForCloudlet;
							delay = ByteperSecond / newRecapCloudlet.getFileSize();
							newRecapCloudlet.setSubmissionDelay(delay);

							// Update the delay for the rest of cloudlets in the list based on more
							// cloudlets in the link
							// check if more cloudlets in the list than the new one
							if (listActivecloudlets.size() > 1) {
								// calculate how much of data was already transferred in the previous time slice
								// update with new delays for the remainder of the data to be transferred
								for (IRecapCloudlet cl : listActivecloudlets) {
									// all except the new one
									if (cl.getId() != newRecapCloudlet.getId()) {
										double timePassedInDataTransfer = simulation.clock() - cl.getBwUpdateTime();
										// calculate already how much was transferred
										double availableBandwithSliceBeforeNewVM = linkBw
												/ (listActivecloudlets.size() - 1);
										double transferredBytes = cl.getFileSize() - (timePassedInDataTransfer
												* (availableBandwithSliceBeforeNewVM * 125000));
										// new delay with new slice byteper second
										double newDelay = ByteperSecond / (cl.getFileSize() - transferredBytes);
										cl.setSubmissionDelay(newDelay);
										// set the bytes that were transferred in the past time and time when that was
										// updated before the new time delay estimation
										cl.setTransferredBytes(transferredBytes);
										cl.setBwUpdateTime(simulation.clock());

									}
								}

							}

						}

						// need to add cloudlet to the list to have a consistent ID
						cloudletList.add(newRecapCloudlet);
						onTheFlycloudletList.add(newRecapCloudlet);
						Log.printFormattedLine("Submitting Cloudlet ID: " + newRecapCloudlet.getId());
						this.broker0.submitCloudlet(newRecapCloudlet);
						System.out.println("#Submittedcl " + newRecapCloudlet.getStatus());

						// System.out.println("#FinishedCL: "+eventInfo.getCloudlet().getStatus());

						// add the key to the check list
						finishedCloudlets.put(eventInfo.getCloudlet().getId(), true);

						this.broker0.getCloudletWaitingList();

					}

				}

				else // case if we have multiple next APIs
				{
					Log.printFormattedLine("Create many new cloudlets");

					Request request = ModelHelpers.getRequestTask(this.rwm.getDevicesList(),
							finishedRecapCloudlet.getOriginDeviceId(), finishedRecapCloudlet.getRequestId());

					for (int positionNexApi = 0; positionNexApi < currentApi.getNextApiIdCount(); positionNexApi++) {
						if (!request.getDataNodesList().contains(positionNexApi + 1)) {
							continue;
						}

						Api nextApi = ModelHelpers.getApiTask(this.ram.getApplicationsList(),
								finishedRecapCloudlet.getApplicationId(), currentApi.getNextComponentId(positionNexApi),
								currentApi.getNextApiId(positionNexApi));

						// 2. Create cloudlet using api specs
						double delay = 0.0;
						IRecapVe targetVe = getMatchingVeId(finishedRecapCloudlet.getApplicationId(),
								currentApi.getNextComponentId(positionNexApi));
						// 2b. create cloudlet

						// Updated MIPS DataNodes here

						IRecapCloudlet newRecapCloudlet = createCloudlet(targetVe, request.getMipsDataNodes(),
								nextApi.getDataToTransfer(), nextApi.getDataToTransfer(), nextApi.getIops(), delay,
								finishedRecapCloudlet.getApplicationId(), currentApi.getNextComponentId(positionNexApi),
								nextApi.getApiId(), nextApi.getRam(), finishedRecapCloudlet.getRequestId(),
								finishedRecapCloudlet.getOriginDeviceId());
						newRecapCloudlet.setBwUpdateTime(simulation.clock());

						// 2a. calculate delay based on the connection
						// TO-DO: Update transfer remaining speeds when a cloudlet finished transferring
						// through a link
						// is cloudlet being sent between DC sites?
						if (targetVe.getHost().getDatacenter().getId() != currentVe.getHost().getDatacenter().getId()) {
							// if so calculate link BW demand
							Link link = ModelHelpers.getNetworkLink(rim, currentVe.getHost().getDatacenter().getName(),
									targetVe.getHost().getDatacenter().getName());
							int linkBw = link.getBandwith();

							// get current cloudlets on the link
							List<IRecapCloudlet> listActivecloudlets;
							if (activeLinkCloudlets.containsKey(link.getId())) {

								listActivecloudlets = activeLinkCloudlets.get(link.getId());

								// clean cloudlets list that are being processed already. Cloudlets that are not
								// in status instantiated are removed from the list
								for (IRecapCloudlet cl : listActivecloudlets) {
									if (!cl.getStatus()
											.equals(org.cloudbus.cloudsim.cloudlets.Cloudlet.Status.INSTANTIATED)) {
										listActivecloudlets.remove(cl);
									}
								}
								listActivecloudlets.add(newRecapCloudlet);
								// update
								activeLinkCloudlets.put(link.getId(), listActivecloudlets);

							} else {
								// create list and add the cloudlet
								listActivecloudlets = new ArrayList<IRecapCloudlet>();
								listActivecloudlets.add(newRecapCloudlet);
								activeLinkCloudlets.put(link.getId(), listActivecloudlets);
							}

							// assume bandwidth divided equally
							double availableBandwithSliceForCloudlet = linkBw / listActivecloudlets.size();

							// bandwith speed is in Megabits per second where file size is in Bytes, so we
							// convert Megabits to Bytes by multiplying by 125000
							// calculate delay Megabits Bytes
							double ByteperSecond = 125000 * availableBandwithSliceForCloudlet;
							delay = ByteperSecond / newRecapCloudlet.getFileSize();
							newRecapCloudlet.setSubmissionDelay(delay);

							// Update the delay for the rest of cloudlets in the list based on more
							// cloudlets in the link
							// check if more cloudlets in the list than the new one
							if (listActivecloudlets.size() > 1) {
								// calculate how much of data was already transferred in the previous time slice
								// update with new delays for the remainder of the data to be transferred
								for (IRecapCloudlet cl : listActivecloudlets) {
									// all except the new one
									if (cl.getId() != newRecapCloudlet.getId()) {
										double timePassedInDataTransfer = simulation.clock() - cl.getBwUpdateTime();
										// calculate already how much was transferred
										double availableBandwithSliceBeforeNewVM = linkBw
												/ (listActivecloudlets.size() - 1);
										double transferredBytes = cl.getFileSize() - (timePassedInDataTransfer
												* (availableBandwithSliceBeforeNewVM * 125000));
										// new delay with new slice byteper second
										double newDelay = ByteperSecond / (cl.getFileSize() - transferredBytes);
										cl.setSubmissionDelay(newDelay);
										// set the bytes that were transferred in the past time and time when that was
										// updated before the new time delay estimation
										cl.setTransferredBytes(transferredBytes);
										cl.setBwUpdateTime(simulation.clock());

									}
								}

							}

						}

						// need to add cloudlet to the list to have a consistent ID
						cloudletList.add(newRecapCloudlet);
						onTheFlycloudletList.add(newRecapCloudlet);
						Log.printFormattedLine("Submitting Cloudlet ID: " + newRecapCloudlet.getId());
						this.broker0.submitCloudlet(newRecapCloudlet);
						System.out.println("#Submittedcl " + newRecapCloudlet.getStatus());

						// System.out.println("#FinishedCL: "+eventInfo.getCloudlet().getStatus());

						// add the key to the check list
						finishedCloudlets.put(eventInfo.getCloudlet().getId(), true);

						this.broker0.getCloudletWaitingList();
					}

				}

			}

		}

	}

	@Override
	public eu.recap.sim.IRecapSim.SimulationStatus SimulationStatus(String simulationId) {

		return SimulationStatus.FINISHED;

	}

	/**
	 * Initializes a map that will store utilization history for some resource (such
	 * as RAM or BW) of every VM. It also creates an empty internal map to store the
	 * resource utilization for every VM along the simulation execution. The
	 * internal map for every VM will be empty. They are filled inside the
	 * {@link #onClockTickListener(EventInfo)}.
	 */
	private Map<Vm, Map<Double, Double>> initializeUtilizationHistory(List<IRecapVe> veList) {
		// TreeMap sorts entries based on the key
		final Map<Vm, Map<Double, Double>> map = new HashMap<>();

		for (Vm vm : veList) {
			map.put(vm, new TreeMap<>());
		}

		return map;
	}

	/**
	 * Keeps track of simulation clock. Every time the clock changes, this method is
	 * called. To enable this method to be called at a defined interval, you need to
	 * set the {@link Datacenter#setSchedulingInterval(double) scheduling interval}.
	 *
	 * @param evt information about the clock tick event
	 * @see #SCHEDULING_INTERVAL
	 */
	private void onClockTickListener(final EventInfo evt) {
		collectVmResourceUtilization(this.allVmsRamUtilizationHistory, Ram.class, veList);
		collectVmResourceUtilization(this.allVmsBwUtilizationHistory, Bandwidth.class, veList);
	}

	/**
	 * Collects the utilization percentage of a given VM resource for every VM.
	 * CloudSim Plus already has built-in features to obtain VM's CPU utilization.
	 * Check {@link org.cloudsimplus.examples.power.PowerExample}.
	 *
	 * @param allVmsUtilizationHistory the map where the collected utilization for
	 *                                 every VM will be stored
	 * @param resourceClass            the kind of resource to collect its
	 *                                 utilization (usually {@link Ram} or
	 *                                 {@link Bandwidth}).
	 */
	private void collectVmResourceUtilization(final Map<Vm, Map<Double, Double>> allVmsUtilizationHistory,
			Class<? extends ResourceManageable> resourceClass, List<IRecapVe> veList) {
		for (Vm vm : veList) {
			/*
			 * Gets the internal resource utilization map for the current VM. The key of
			 * this map is the time the usage was collected (in seconds) and the value the
			 * percentage of utilization (from 0 to 1).
			 */
			final Map<Double, Double> vmUtilizationHistory = allVmsUtilizationHistory.get(vm);
			vmUtilizationHistory.put(simulation.clock(), vm.getResource(resourceClass).getPercentUtilization());
		}
	}

	/**
	 * Shows RAM utilization of all VMs into a given Datacenter.
	 */
	private void showRamUtilizationForAllVms(final double simulationFinishTime, List<IRecapVe> veList) {
		System.out.println("\nVMs RAM utilization history for the entire simulation period\n");
		int numberOfUsageHistoryEntries = 0;
		for (Vm vm : veList) {
			System.out.println("RAM Of VM:" + vm);
			// A set containing all resource utilization collected times
			final Set<Double> timeSet = allVmsRamUtilizationHistory.get(vm).keySet();

			final Map<Double, Double> vmRamUtilization = allVmsRamUtilizationHistory.get(vm);

			for (final double time : timeSet) {
				if (vmRamUtilization.get(time) > 0) {
					// System.out.printf(
					// "\tTime: %2.0f RAM Utilization: %6.2f%%\n",
					// time/timeUnits, vmRamUtilization.get(time) * 100);
					numberOfUsageHistoryEntries++;
				}
			}

		}

		if (numberOfUsageHistoryEntries == 0) {
			System.out.println("No RAM usage history was found");
		}

	}

	/**
	 * Shows Bandwidth utilization of all VMs into a given Datacenter.
	 */
	private void showBwUtilizationForAllVms(final double simulationFinishTime, List<IRecapVe> veList) {
		System.out.println("\nVMs Bandwidth utilization history for the entire simulation period\n");
		int numberOfUsageHistoryEntries = 0;
		for (Vm vm : veList) {
			System.out.println("Bandwidth Of VM:" + vm);
			// A set containing all resource utilization collected times
			final Set<Double> timeSet = allVmsBwUtilizationHistory.get(vm).keySet();

			final Map<Double, Double> vmBwUtilization = allVmsBwUtilizationHistory.get(vm);

			for (final double time : timeSet) {
				if (vmBwUtilization.get(time) > 0) {
					System.out.printf("\tTime: %2.0f Bandwidth Utilization: %6.2f%%\n", time / timeUnits,
							vmBwUtilization.get(time) * 100);
					numberOfUsageHistoryEntries++;
				}
			}

		}

		if (numberOfUsageHistoryEntries == 0) {
			System.out.println("No Bandwidth usage history was found");
		}

	}

	/**
	 * Shows TABLE CPU utilization of all VMs into a given Datacenter.
	 */
	private void showTableCpuUtilizationForAllVms(final double simulationFinishTime, List<IRecapVe> veList) {

		TreeMap<Double, List<Double>> cpuUtilisation = new TreeMap<Double, List<Double>>();

		for (Vm vm : veList) {
			for (Map.Entry<Double, Double> entry : vm.getUtilizationHistory().getHistory().entrySet()) {
				final double time = entry.getKey();
				final double vmCpuUsage = entry.getValue() * 100;

				if (!cpuUtilisation.containsKey(time)) {
					ArrayList<Double> zeros = new ArrayList<Double>();
					for (int i = 0; i < veList.size(); i++) {
						zeros.add(-1.);
					}
					cpuUtilisation.put(time, zeros);
				}

				cpuUtilisation.get(time).set((int) vm.getId(), vmCpuUsage);
			}
		}

		System.out.println("/*********************************/");
		System.out.println(" CPU Utilisation");
		System.out.println("/*********************************/");

		System.out.print("Time");
		for (int i = 0; i < veList.size(); i++) {
			System.out.print("\tVM" + i);
		}
		System.out.println("");

		// continue printing previous CPU utilisation of unchanged
		// start at zero
		Double previousTime = 0.0;
		List<Double> previousValues = new ArrayList<Double>();
		for (int v = 0; v < veList.size(); v++) {
			previousValues.add(0.);
		}

		for (Map.Entry<Double, List<Double>> entry : cpuUtilisation.entrySet()) {
			// print zero usage of cpu if waited too long
			if ((entry.getKey() / timeUnits - previousTime) > 20) {
				// print next time
				System.out.printf("%6.5f", previousTime + 0.001);
				List<Double> currentValues = entry.getValue();
				for (int v = 0; v < currentValues.size(); v++) {

					System.out.printf("\t%6.3f", 0.0);

				}
				System.out.println("");

				// print previous time
				System.out.printf("%6.5f", entry.getKey() / timeUnits - 0.001);
				for (int v = 0; v < currentValues.size(); v++) {

					System.out.printf("\t%6.3f", 0.0);

				}
				System.out.println("");
			}

			// print CPU value
			System.out.printf("%6.5f", entry.getKey() / timeUnits);

			List<Double> currentValues = entry.getValue();
			for (int v = 0; v < currentValues.size(); v++) {
				if (currentValues.get(v) == -1) {
					currentValues.set(v, previousValues.get(v));
				}

				System.out.printf("\t%6.3f", currentValues.get(v));

			}
			System.out.println("");
			previousValues = currentValues;
			previousTime = entry.getKey() / timeUnits;
		}
	}

	/**
	 * Shows TABLE RAM utilization of all VMs into a given Datacenter.
	 */
	private void showTableRamUtilizationForAllVms(final double simulationFinishTime, List<IRecapVe> veList) {

		TreeMap<Double, List<Double>> ramUtilisation = new TreeMap<Double, List<Double>>();

		for (Vm vm : veList) {
			for (Map.Entry<Double, Double> entry : allVmsRamUtilizationHistory.get(vm).entrySet()) {
				final double time = entry.getKey();
				final double vmCpuUsage = entry.getValue() * 100;
				if (!ramUtilisation.containsKey(time)) {
					ArrayList<Double> zeros = new ArrayList<Double>();
					for (int i = 0; i < veList.size(); i++) {
						zeros.add(-1.);
					}
					ramUtilisation.put(time, zeros);
				}

				ramUtilisation.get(time).set((int) vm.getId(), vmCpuUsage);
			}
		}

		System.out.println("/*********************************/");
		System.out.println(" RAM Utilisation");
		System.out.println("/*********************************/");
		System.out.print("Time");
		for (int i = 0; i < veList.size(); i++) {
			System.out.print("\tVM" + i);
		}
		System.out.println("");

		// continue printing previous CPU utilisation of unchanged
		// start at zero
		Double previousTime = 0.0;
		List<Double> previousValues = new ArrayList<Double>();
		for (int v = 0; v < veList.size(); v++) {
			previousValues.add(0.);
		}

		for (Map.Entry<Double, List<Double>> entry : ramUtilisation.entrySet()) {

			// print zero usage of cpu if waited too long
			if ((entry.getKey() / timeUnits - previousTime) > 20) {

				// print next time
				System.out.printf("%6.5f", previousTime + 0.001);
				List<Double> currentValues = entry.getValue();
				for (int v = 0; v < currentValues.size(); v++) {

					System.out.printf("\t%6.3f", 0.0);

				}
				System.out.println("");

				// print previous time
				System.out.printf("%6.5f", entry.getKey() / timeUnits - 0.001);
				for (int v = 0; v < currentValues.size(); v++) {

					System.out.printf("\t%6.3f", 0.0);

				}
				System.out.println("");
			}

			System.out.printf("%6.5f", entry.getKey() / timeUnits);

			List<Double> currentValues = entry.getValue();
			for (int v = 0; v < currentValues.size(); v++) {
				if (currentValues.get(v) == -1) {
					// currentValues.set(v, previousValues.get(v));
				}

				System.out.printf("\t%6.3f", currentValues.get(v));
			}
			System.out.println("");
			previousValues = currentValues;
			previousTime = entry.getKey() / timeUnits;
		}
	}

	/*
	 * output the summary table as JSON File
	 */
	public void outputTableAsJSON(final List<? extends RecapCloudlet> list, Infrastructure rim,
			ApplicationLandscape ram, Workload rwm, Experiment config) {
		System.out.println("Create JSON File for Cloudlet Infos");
		HashMap<Integer, Double> timeStartingRequest = new HashMap<Integer, Double>();
		JSONArray finishedCloudletsInfo = new JSONArray();

		for (RecapCloudlet cloudlet : list) {
			JSONObject cloudInfo = new JSONObject();
			cloudInfo.put("Cloudlet", cloudlet.getId());
			cloudInfo.put("Request", cloudlet.getRequestId());
			cloudInfo.put("Application", cloudlet.getApplicationId());
			cloudInfo.put("Component", cloudlet.getApplicationComponentId());
			cloudInfo.put("Status", cloudlet.getStatus().name());
			cloudInfo.put("DC", cloudlet.getVm().getHost().getDatacenter().getId());
			cloudInfo.put("Host", cloudlet.getVm().getHost().getId());
			cloudInfo.put("VM", cloudlet.getVm().getId());
			cloudInfo.put("VM PEs", cloudlet.getVm().getNumberOfPes());
			cloudInfo.put("CloudletLen", cloudlet.getLength());
			cloudInfo.put("CloudletPEs", cloudlet.getNumberOfPes());

			cloudInfo.put("StartTime", Math.floor(cloudlet.getExecStartTime() * 1000 / timeUnits));
			cloudInfo.put("FinishTime", Math.floor(cloudlet.getFinishTime() * 1000 / timeUnits));
			cloudInfo.put("ExecTime", Math.floor(cloudlet.getActualCpuTime() * 1000 / timeUnits));

			if (!timeStartingRequest.containsKey(cloudlet.getRequestId())) {
				timeStartingRequest.put(cloudlet.getRequestId(), cloudlet.getExecStartTime());
			}
			cloudInfo.put("TotalExecTime",
					cloudlet.getApiId().equals("1_2")
							? Math.floor((cloudlet.getFinishTime() - timeStartingRequest.get(cloudlet.getRequestId()))
									* 1000 / timeUnits)
							: null);

			cloudInfo.put("Final", cloudlet.getApiId().equals("1_2") ? "Yes" : "No");
			cloudInfo.put("ExpectedTime",
					cloudlet.getApiId().equals("1_2")
							? Integer
									.toString(
											ModelHelpers
													.getRequestTask(this.rwm.getDevicesList(),
															cloudlet.getOriginDeviceId(), cloudlet.getRequestId())
													.getExpectedDuration() / timeUnits)
							: "");

			finishedCloudletsInfo.add(cloudInfo);
		}
		// Write JSON file
		try (FileWriter file = new FileWriter("cloudletsInfo.json")) {

			file.write(finishedCloudletsInfo.toJSONString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * output the summary table as JSON File
	 */
	public void outputTableForExcel(final List<? extends RecapCloudlet> list, Infrastructure rim,
			ApplicationLandscape ram, Workload rwm, Experiment config) {
		HashMap<Integer, Double> timeStartingRequest = new HashMap<Integer, Double>();
		System.out.println("OutTableForExcel");

		System.out.print("Cloudlet\t");
		System.out.print("Request\t");
		System.out.print("Application\t");
		System.out.print("Component\t");
		System.out.print("Status\t");
		System.out.print("DC\t");
		System.out.print("Host\t");
		System.out.print("VM\t");
		System.out.print("VM PEs\t");
		System.out.print("CloudletLent\t");
		System.out.print("CloudletPEs\t");

		System.out.print("StartTime\t");
		System.out.print("FinishTime\t");
		System.out.print("ExecTime\t");

		System.out.print("TotalExecTimet\t");

		System.out.print("Final\t");
		System.out.print("ExpectedTime");
		System.out.println();

		for (RecapCloudlet cloudlet : list) {
			if (!timeStartingRequest.containsKey(cloudlet.getRequestId())) {
				timeStartingRequest.put(cloudlet.getRequestId(), cloudlet.getExecStartTime());
			}
//    		

		}
	}

	/*
	 * output the summary table as JSON File
	 */
	public void outputResourceConsumptionAsJSON(List<IRecapVe> veList) {
		System.out.println("Create JSON File for Resource Consumption");

		JSONArray resourceConsumption = new JSONArray();
		for (Vm vm : veList) {
			JSONObject vmInfo = new JSONObject();

		}

	}
}
