package eu.recap.sim.cloudsim.cloudlet;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;


public class RecapCloudlet extends CloudletSimple implements IRecapCloudlet{
	
	String applicationId;
	String componentId;
	String apiId;
	String linkId;
	double bwUpdateTime;
	double transferredBytes;
	int requestId;
	String originDeviceId;


	public RecapCloudlet(final int id,  final long cloudletLength,  final int pesNumber){
		super(id, cloudletLength, pesNumber);
		
		
	}

	/* 
	 * Not sure what is this doing but we just going with this
	 */
	@Override
	public int compareTo(Cloudlet cloudlet) {
		return Double.compare(getLength(), cloudlet.getLength());
		
	}

	@Override
	public String getApplicationId() {

		return this.applicationId;
	}

	@Override
	public IRecapCloudlet setApplicationId(String applicationId) {
		this.applicationId = applicationId;
		
		return this;
	}
	
	
	@Override
	public int getRequestId() {

		return this.requestId;
	}
	
	@Override
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	
	@Override
	public String getOriginDeviceId() {

		return this.originDeviceId;
	}
	
	@Override
	public void setOriginDeviceId(String originDeviceId) {
		this.originDeviceId = originDeviceId;
	}
	

	@Override
	public String getApplicationComponentId() {
		
		return this.componentId;
	}

	@Override
	public IRecapCloudlet setApplicationComponentId(String applicationComponentId) {
		this.componentId = applicationComponentId;
		
		return this;
	}

	@Override
	public String getApiId() {
		
		return this.apiId;
	}

	@Override
	public IRecapCloudlet setApiId(String apiId) {
		this.apiId = apiId;
		
		return this;
	}

	@Override
	public String getLinkId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRecapCloudlet setLinkId(String apiId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getBwUpdateTime() {
		// TODO Auto-generated method stub
		return bwUpdateTime;
	}

	@Override
	public IRecapCloudlet setBwUpdateTime(double bwUpdateTime) {
		this.bwUpdateTime=bwUpdateTime;
		return this;
	}

	@Override
	public double getTransferredBytes() {
		// TODO Auto-generated method stub
		return transferredBytes;
	}

	@Override
	public IRecapCloudlet setTransferredBytes(double transferredBytes) {
		this.transferredBytes = transferredBytes;
		return this;
	}
	
	
	
	
	
	
	
}
