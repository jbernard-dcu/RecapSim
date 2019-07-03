package Classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import eu.recap.sim.models.InfrastructureModel.*;
import eu.recap.sim.models.InfrastructureModel.Node.*;
import eu.recap.sim.models.InfrastructureModel.ResourceSite.SiteLevel;
import eu.recap.sim.models.LocationModel.Location;

public class Test {

	/**
	 * Creates test Infrastructure model for tieto usecase @ ExoerimentHelpers for
	 * original method
	 * 
	 * @param name
	 * @param numberOfSites
	 * @param numberOfNodesPerSite
	 * @return the populated Infrastructure model
	 */
	public static Infrastructure GenerateInfrastructure(String name, int numberOfSites, int numberOfNodesPerSite) {
		/*
		 * TODO Use different configurations for these parameters
		 */
		final int cpuFrequency = 3000; // MIPS or 2.6 GHz
		final int cpuCores = 80;
		final int ram = 2048_000; // host memory (MEGABYTE)
		final int hdd = 1000000_000; // host storage (MEGABYTE)
		final int bw = 10_000; // in 10Gbit/s

		Infrastructure.Builder infrastructure = Infrastructure.newBuilder();
		infrastructure.setName(name);

		// only one link where all sites are connected
		Link.Builder link = Link.newBuilder();
		link.setId("0");
		link.setBandwith(bw);

		// create sites
		for (int i = 0; i < numberOfSites; i++) {

			ResourceSite.Builder site = ResourceSite.newBuilder();
			site.setName("Site_" + i);
			site.setId(i + "");

			Location.Builder geolocation = Location.newBuilder();
			geolocation.setLatitude(i);
			geolocation.setLongitude(i);
			site.setLocation(geolocation.build());
			site.setHierarchyLevel(SiteLevel.Edge);

			// create nodes
			for (int j = 0; j < numberOfNodesPerSite; j++) {

				// TODO
				VMConfig config = new VMConfig(cpuFrequency, cpuCores, ram, hdd);

				Node.Builder node = Node.newBuilder();
				node.setName("Node_" + i + "_" + j);
				node.setId(i + "_" + j);

				/*
				 * TODO Change cpu specifications change access to config
				 */
				CPU.Builder cpu = CPU.newBuilder();
				cpu.setName("Xeon_" + i + "_" + j);
				cpu.setId(i + "_" + j);
				cpu.setMake("Intel");
				cpu.setRating("12345");
				cpu.setFrequency(config.getConfig()[0]/* cpuFrequency */);
				// create cores
				for (int e = 0; e < config.getConfig()[1]/* cpuCores */; e++) {
					Core.Builder core = Core.newBuilder();
					core.setId(i + "_" + j + "_" + e);
					cpu.addCpuCores(core.build());
				}

				Memory.Builder memory = Memory.newBuilder();
				memory.setId(i + "_" + j);
				memory.setCapacity(config.getConfig()[2]/* ram */);

				Storage.Builder storage = Storage.newBuilder();
				storage.setId(i + "_" + j);
				storage.setSize(config.getConfig()[3]/* hdd */);

				// add resources to node
				node.addProcessingUnits(cpu.build());
				node.addMemoryUnits(memory.build());
				node.addStorageUnits(storage.build());

				// add node to site
				site.addNodes(node.build());
			}
			ResourceSite builtSite = site.build();
			// add sites to infrastructure
			infrastructure.addSites(builtSite);

			// add sites to link by id
			link.addConnectedSites(builtSite);

		}

		infrastructure.addLinks(link.build());
		return infrastructure.build();

	}

}

class ID {
	private long id;

	public ID() {
		this.id = 0;
	}

	public String createID() {
		return Long.toString(this.id++);
	}
}

class FreqD<T> {

	private List<T> dataset;
	private List<Double> freq;

	public FreqD(List<T> dataset, List<Double> freq) {
		this.dataset = dataset;
		this.freq = freq;
	}

	public FreqD(TreeMap<T, Double> termDist) {
		this.dataset.addAll(termDist.keySet());
		this.freq.addAll(termDist.values());
	}

	public T sample() {
		List<Double> cumSum = new ArrayList<Double>();
		cumSum.add(this.freq.get(0));
		for (int i = 1; i < this.dataset.size(); i++) {
			cumSum.add(cumSum.get(i - 1) + this.freq.get(i));
		}

		Double r = new Random().nextDouble() * cumSum.get(cumSum.size() - 1);
		int i = 0;
		while (r > cumSum.get(i)) {
			i++;
		}
		return this.dataset.get(i);
	}

}

class ExpD {
	private double lambda;

	public ExpD(double lambda) {
		this.lambda = lambda;
	}

	public double sample() {
		return -Math.log(Math.random()) / lambda;
	}
}

class VMConfig {
	private int cpuFrequency;
	private int cpuCores;
	private int ram;
	private int hdd;

	public VMConfig(int cpuFrequency, int cpuCores, int ram, int hdd) {
		super();
		this.cpuFrequency = cpuFrequency;
		this.cpuCores = cpuCores;
		this.ram = ram;
		this.hdd = hdd;
	}

	public int[] getConfig() {
		int[] r = { this.cpuFrequency, this.cpuCores, this.ram, this.hdd };
		return r;
	}
}
