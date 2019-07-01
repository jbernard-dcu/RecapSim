/**
 * 
 */
package eu.recap.sim.helpers;

/**
 * Message will be returned to capture any errors/messages that may appear within the code
 * this class will be used to communicate up the stack
 * @author Sergej Svorobej
 *
 */
public class Message {
	
	public MessageType messageType;
	public String messageOrigin;
	public String messageText;
	public String remediationAdvise;
	public String systemError;
	

}
