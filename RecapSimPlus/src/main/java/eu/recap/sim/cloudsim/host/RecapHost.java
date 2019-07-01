/**
 * 
 */
package eu.recap.sim.cloudsim.host;

import java.util.List;

import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;


/**
 * Through the Interface and simple host extension we are adding RECAP specific model IDs
 * @author Sergej Svorobej
 *
 */
public class RecapHost extends HostSimple implements IRecapHost {
	private String recapNodeId;
	private String recapNodeName;
	private String recapResourceSiteId;
	private String recapResourceSiteName;

	public RecapHost(long ram, long bw, long storage, List<Pe> peList) {
		super(ram, bw, storage, peList);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see eu.recap.sim.cloudsim.host.IRecapHost#getRecapNodeId()
	 */
	@Override
	public String getRecapNodeId() {
		
		return this.recapNodeId;
	}

	/* (non-Javadoc)
	 * @see eu.recap.sim.cloudsim.host.IRecapHost#getRecapNodeName()
	 */
	@Override
	public String getRecapNodeName() {
		
		return this.recapNodeName;
	}

	@Override
	public String getRecapResourceSiteId() {
		
		return this.recapResourceSiteId;
	}

	@Override
	public String getRecapResourceSiteName() {
		
		return this.recapResourceSiteName;
	}


	@Override
	public void setRecapNodeName(String nodeName) {
		this.recapNodeName=nodeName;
		
	}

	@Override
	public void setRecapResourceSiteId(String recapResourceSiteId) {
		this.recapResourceSiteId = recapResourceSiteId;
		
	}

	@Override
	public void setRecapResourceSiteName(String recapResourceSiteName) {
		this.recapResourceSiteName = recapResourceSiteName;
		
	}

	@Override
	public void setRecapNodeId(String nodeId) {
		this.recapNodeId = nodeId;
		
	}



}
