/**
 * 
 */
package eu.recap.sim.helpers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Class is a holder of the connections between the sites
 * networkTopology - includes list of connections for every site 
 * networkChainTopology - includes the list of sites connected for each chain
 * 
 * TO-DO: move the generator code to the generator class and rename this class
 * 
 * @author Sergej Svorobej
 *
 */
public class Switch implements Serializable{

	private static final long serialVersionUID = 1L;
	//the ID of a site and list of IDs they are connected to 
	Map<String,List<String>> networkTopology;
	Map<String,List<String>> networkChainTopology;


	public Switch(){
		networkTopology = new HashMap<String,List<String>>();
		networkChainTopology = new HashMap<String,List<String>>();
	}
	private void addSiteConnection(String siteID, String connectingSiteID){

		//check if it is connection to self		 
		if(!connectingSiteID.equals(siteID)){

			if (networkTopology.get(siteID) == null){
				//if it does not exist create the list add the entry and add it to map
				List<String> listOfsites = new ArrayList<String>();
				if(!connectingSiteID.equals(siteID)){
					listOfsites.add(connectingSiteID);
				}
				networkTopology.put(siteID, listOfsites);

			}else{//if entry exist get the list and add the entry to the list

				//check if exists to have unique list of sites per chain link
				boolean exists = false;
				for(String site :networkTopology.get(siteID)){
					if(site.equals(connectingSiteID)){
						//already in the list
						exists=true;
						break;
					}

				}//end for loop

				if(!exists){
					networkTopology.get(siteID).add(connectingSiteID);
				}
			}
		}//end of connection to self condition
	}

	/**
	 *  Loops through chains and creates network topology
	 * @return
	 */
	public Map<String, List<String>> getNetworkTopology() {

		if(networkTopology.isEmpty()){

			for(Entry<String, List<String>> chainEntry: networkChainTopology.entrySet()){

				List<String> siteList = chainEntry.getValue();

				//iterate through all the elements and add each other to its lists
				int outerLoop = siteList.size()-1;

				while(outerLoop>=0){
					int innerLoop = siteList.size()-1;
					while(innerLoop>=0){
						addSiteConnection(siteList.get(outerLoop),siteList.get(innerLoop));
						innerLoop--;
					}//end inner loop
					outerLoop--;
				}//end outer loop

			}

		}

		return networkTopology;
	}

	public void setChainConnections(String chainID, List<String> connectingSiteIDs){
		networkTopology.put(chainID, connectingSiteIDs);
	}

	public void addChainConnection(String chainID, String siteID){

		if (networkChainTopology.get(chainID) == null){
			//if it does not exist create the list add the entry and add it to map
			List<String> listOfsites = new ArrayList<String>();
			listOfsites.add(siteID);
			networkChainTopology.put(chainID, listOfsites);

		}else{//if entry exist get the list and add the entry to the list

			//check if exists to have unique list of sites per chain link
			boolean exists = false;
			for(String site :networkChainTopology.get(chainID)){
				if(site.equals(siteID)){
					//already in the list
					exists=true;
					break;
				}
			}//end for loop

			if(!exists){
				networkChainTopology.get(chainID).add(siteID);
			}

		}

	}

	public Map<String, List<String>> getChainTopology() {
		return networkChainTopology;
	}

}
