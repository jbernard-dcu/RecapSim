/**
 * 
 */
package eu.recap.sim.cloudsim.cloudlet;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;

/**
 * @author Sergej Svorobej
 *
 */
public interface IRecapCloudlet extends Cloudlet {

	String getApplicationId();

	IRecapCloudlet setApplicationId(String applicationId);

	String getApplicationComponentId();

	IRecapCloudlet setApplicationComponentId(String applicationComponentId);

	String getApiId();

	IRecapCloudlet setApiId(String apiId);

	String getLinkId();

	IRecapCloudlet setLinkId(String apiId);

	int getRequestId();

	void setRequestId(int requestId);

	String getOriginDeviceId();

	void setOriginDeviceId(String originDeviceId);

	double getBwUpdateTime();

	IRecapCloudlet setBwUpdateTime(double bwUpdateSimTime);

	// Use only for intermediate step to calculate remainder of bytes to be
	// transferred
	double getTransferredBytes();

	IRecapCloudlet setTransferredBytes(double transferredBytes);

}
