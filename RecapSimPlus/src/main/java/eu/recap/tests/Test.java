package eu.recap.tests;

import java.io.File;

import eu.recap.sim.models.InfrastructureModel.*;
import eu.recap.sim.models.InfrastructureModel.Node.CPU;
import eu.recap.sim.models.InfrastructureModel.Node.Core;
import eu.recap.sim.models.InfrastructureModel.Node.Memory;
import eu.recap.sim.models.InfrastructureModel.Node.Storage;
import eu.recap.sim.models.InfrastructureModel.ResourceSite.SiteLevel;
import eu.recap.sim.models.LocationModel.Location;


public class Test {
	
	/**
	 * Creates test Infrastructure model for tieto usecase
	 * @ ExoerimentHelpers for original method
	 * @param name
	 * @param numberOfSites
	 * @param numberOfNodesPerSite
	 * @return the populated Infrastructure model
	 */
	public static Infrastructure GenerateInfrastructure(String name, int numberOfSites, int numberOfNodesPerSite) {
		/*TODO
		 * Use different configurations for these parameters
		 */
		final int cpuFrequency = 3000; //MIPS or 2.6 GHz
		final int cpuCores = 80; 
		final int ram = 2048_000; // host memory (MEGABYTE)
		final int hdd = 1000000_000; // host storage (MEGABYTE)
		final int bw = 10_000; // in 10Gbit/s
		
		
		Infrastructure.Builder infrastructure = Infrastructure.newBuilder();
		infrastructure.setName(name);
		
		//only one link where all sites are connected
		Link.Builder link = Link.newBuilder();
		link.setId("0");
		link.setBandwith(bw);
		
		//create sites
	    for(int i=0; i<numberOfSites; i++){
            
	    	ResourceSite.Builder site = ResourceSite.newBuilder();
	    	site.setName("Site_"+i);
	    	site.setId(i+"");
	    	
	    	Location.Builder geolocation = Location.newBuilder();
	    	geolocation.setLatitude(i);
	    	geolocation.setLongitude(i);
	    	site.setLocation(geolocation.build());
	    	site.setHierarchyLevel(SiteLevel.Edge);
	    	
	    	//create nodes
	    	for(int j=0; j<numberOfNodesPerSite; j++){
	    		
	    		//TODO
	    		VMConfig config=new VMConfig(cpuFrequency,cpuCores,ram,hdd);
	    		
	    		Node.Builder node = Node.newBuilder();
	    		node.setName("Node_"+i+"_"+j);
	    		node.setId(i+"_"+j);

	    		/*TODO 
	    		 * Change cpu specifications
	    		 * change access to config
	    		 */
	    		CPU.Builder cpu = CPU.newBuilder();
	    		cpu.setName("Xeon_"+i+"_"+j);
	    		cpu.setId(i+"_"+j);
	    		cpu.setMake("Intel");
	    		cpu.setRating("12345");
	    		cpu.setFrequency(config.getConfig()[0]/*cpuFrequency*/);
	    		//create cores
	    		for(int e=0; e<config.getConfig()[1]/*cpuCores*/; e++){
	    			Core.Builder core = Core.newBuilder();
	    			core.setId(i+"_"+j+"_"+e);
	    			cpu.addCpuCores(core.build());
	    		}
	    		
	    		
	    		Memory.Builder memory = Memory.newBuilder();
	    		memory.setId(i+"_"+j);
	    		memory.setCapacity(config.getConfig()[2]/*ram*/);
	    		
	    		Storage.Builder storage = Storage.newBuilder();
	    		storage.setId(i+"_"+j);
	    		storage.setSize(config.getConfig()[3]/*hdd*/);
	    		
	    		
	    		//add resources to node
	    		node.addProcessingUnits(cpu.build());
	    		node.addMemoryUnits(memory.build());
	    		node.addStorageUnits(storage.build());
	    		
	    		//add node to site
	    		site.addNodes(node.build());
	    	}
	    	ResourceSite builtSite = site.build();
	    	//add sites to infrastructure
	    	infrastructure.addSites(builtSite);
	    	
	    	//add sites to link by id
	    	link.addConnectedSites(builtSite);
	    	
	    }
		
	    infrastructure.addLinks(link.build());
		return infrastructure.build();

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
		int[] r={this.cpuFrequency,this.cpuCores,this.ram, this.hdd};
		return r;
	}
}
