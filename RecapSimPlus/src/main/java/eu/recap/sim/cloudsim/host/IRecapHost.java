/**
 * 
 */
package eu.recap.sim.cloudsim.host;


import org.cloudbus.cloudsim.hosts.Host;

/**
 * @author Sergej Svorobej
 *
 */
public interface IRecapHost extends Host{
	/**
	 * Returns RECAP model bindings
	 * @return
	 */
	public String getRecapNodeId();
	public void setRecapNodeId(String nodeId);
	
	public String getRecapNodeName();
	public  void setRecapNodeName(String nodeName);
	
	public String getRecapResourceSiteId();
	public void setRecapResourceSiteId(String recapResourceSiteId);
	
	public String getRecapResourceSiteName();
	public  void setRecapResourceSiteName(String recapResourceSiteName);
}
