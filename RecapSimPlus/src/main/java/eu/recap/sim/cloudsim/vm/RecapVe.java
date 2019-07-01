/**
 * 
 */
package eu.recap.sim.cloudsim.vm;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.vms.UtilizationHistory;
import org.cloudbus.cloudsim.vms.VmSimple;


/**
 * RECAP notion of VE component with a deployed application  
 * 
 * @author Sergej Svorobej
 *
 */
public class RecapVe extends VmSimple implements IRecapVe{
	String applicationId;
	String applicationComponentId;
	boolean isLoadbalancer = false;
	//storage for resource utilisation
	private final RecapVmUtilizationHistory utilizationHistory;
    private double lastBusyTime;
	

	
	public RecapVe(int id, DatacenterBroker broker, long mipsCapacity, int numberOfPes, long ramCapacity,
			long bwCapacity, long size, String vmm, CloudletScheduler cloudletScheduler) {
		
		super(id,mipsCapacity,numberOfPes);
		super.setBroker(broker);
		super.setRam(ramCapacity);
		super.setBw(bwCapacity);
		super.setSize(size);
		super.setVmm(vmm);
		super.setCloudletScheduler(cloudletScheduler);
		this.lastBusyTime = 0;
		utilizationHistory = new RecapVmUtilizationHistory(this, true);
	}
	
	
    @Override
    public double updateProcessing(final double currentTime, final List<Double> mipsShare) {
        requireNonNull(mipsShare);

        if(!super.getCloudletScheduler().getCloudletExecList().isEmpty()){
            this.lastBusyTime = getSimulation().clock();
        }
        final double nextEventDelay = super.getCloudletScheduler().updateProcessing(currentTime, mipsShare);
        notifyOnUpdateProcessingListeners();

        /* If the current time is some value with the decimals greater than x.0
         * (such as 45.1) and the next event delay is any integer number such as 5,
         * then the next simulation time will be 50.1.
         * At time 50.1 the utilization will be reduced due to the completion of the Cloudlet.
         * At time 50.0 the Cloudlet is still running, so there is some CPU utilization.
         * But since the next update will be only at time 50.1, the utilization
         * at time 50.0 won't be collected to enable knowing the exact time
         * before the utilization dropped.
         */
        final double decimals = currentTime - (int) currentTime;
        utilizationHistory.addUtilizationHistory(currentTime);
        return nextEventDelay - decimals;
    }
    
    @Override
    public UtilizationHistory getUtilizationHistory() {
        return utilizationHistory;
    }
    //TODO Network
    //TODO IOPS
    
    @Override
    public double getLastBusyTime() {
        return this.lastBusyTime;
    }

    @Override
    public double getIdleInterval() {
        return getSimulation().clock() - lastBusyTime;
    }
    
	@Override
	public String getApplicationID() {
		
		return applicationId;
	}

	@Override
	public IRecapVe setApplicationID(String applicationId) {
		this.applicationId =  Objects.isNull(applicationId) ? "" : applicationId;
        
        return this;
		
	}

	@Override
	public String getApplicationComponentID() {
		
		return applicationComponentId;
	}

	@Override
	public IRecapVe setApplicationComponentID(String applicationComponentId) {
		this.applicationComponentId = Objects.isNull(applicationComponentId) ? "" : applicationComponentId;
		return this;
	}

	@Override
	public boolean isLoadbalancer() {
		return isLoadbalancer;
	}

}
