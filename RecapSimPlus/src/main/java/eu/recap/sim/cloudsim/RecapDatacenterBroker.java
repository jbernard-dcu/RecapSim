/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package eu.recap.sim.cloudsim;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.events.CloudSimEvent;
import eu.recap.sim.helpers.Log;
import org.cloudsimplus.listeners.EventInfo;

/**
 * A power-aware {@link DatacenterBrokerSimple}.
 *
 * <br/>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:<br/>
 *
 * <ul>
 * <li><a href="http://dx.doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class RecapDatacenterBroker extends DatacenterBrokerSimple {

	/**
	 * Instantiates a new PowerDatacenterBroker.
	 *
	 * @param simulation The CloudSim instance that represents the simulation the Entity is related to
	 */
	public RecapDatacenterBroker(CloudSim simulation)  {
		super(simulation);
		
		//this is where we cave a listener
		getSimulation().addOnEventProcessingListener(this::onEventPrcessingListener);
		
		Log.printConcatLine(getName(), " is starting...");
		
		
		
	}
	

	
    /**
     *  The eventlistener method is used to create events that will be triggered at a particular simulation time. 
     *   Such events include user requests, and optimisation requests.
     * 
     * @param eventInfo
     */
    private  void onEventPrcessingListener(EventInfo eventInfo) {
    	    	
    	CloudSimEvent csEvent = (CloudSimEvent)eventInfo;
    	//Log.printConcatLine("#Simtime: "+getSimulation().clock()+" From:"+csEvent.getSource()+" To: "+csEvent.getDestination()+" Event Type: "+csEvent.getType()+" Event Tag: "+csEvent.getTag());
    	
    	if(getSimulation().clock() == 0.0 && csEvent.getTag() == 2){
    		
    		//TO-DO: This breaks new version code, see in future how to schedule new events
    		//schedule(getId(), (int) 0.0, 111);
    		
    		Log.printConcatLine("#Simtime: "+getSimulation().clock()+" Scheduled initialisation event.");
    	}
    	
    	if(getSimulation().clock() == 0.0 && csEvent.getTag() == 111){
    		
    		Log.printConcatLine("#Loading all the events for cloudlets on demand.");
    		
    		//1. Look through list of requests and schedule events at a given time with number 112
    		//schedule(getId(), requestTime, 112);
    		
    		
    	}
    	
    	if(csEvent.getTag() == 112){
    		
    		Log.printConcatLine("#Creating cloudlet");
    		
    		
    		
    		
    		
    	}
    	
     
        
    }
	
    

//	@Override
//	public void startEntity() {
//		Log.printConcatLine(getName(), " is starting...");
//		schedule(getId(), 0, 0);//initialise at time-zero
//		
//	}
	
	//I think we will make our callback function here
//	@Override
//	public void processEvent(SimEvent ev) {
//		
//
//	Log.printConcatLine("#Simtime:"+getSimulation().clock());
//		
//	}


//	@Override
//	protected boolean processVmCreateResponseFromDatacenter(SimEvent ev) {
//        final Vm vm = (Vm) ev.getData();
//
//		if (!vm.isCreated()) {
//            Log.printConcatLine(getSimulation().clock() + ": " + getName() + ": Creation of VM #" + vm.getId()
//                + " failed in Datacenter #" + vm.getHost().getDatacenter().getId());
//		}
//		return super.processVmCreateResponseFromDatacenter(ev);
//	}

}
