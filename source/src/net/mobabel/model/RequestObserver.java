package net.mobabel.model;

import net.mobabel.item.ItemContact;

/**
 *  Inteface RequestObserver implemented from all Objects, doing HttpRequests
 * 
 * 
 *  @author A.Lichtenberger M-Way Solutions 2008
 * 
 *  @version     %I%, %G%
 *  @since       1.0 * 
 * 
 * 
 */
public interface RequestObserver
{
	/**
	 * 
	 *
	 */
	public void onDownloadStart();
	
	/**
     * Method onProgressNotification
     * to be overwritten in classes which are implementing the interface
     * handling Messages from the running request
     * 
     * @param aMessage      String containing the message data
     */
	public void onProgressNotification(String message);

	/**
	 * Method onDownloadFinished
	 * to be overwritten in classes which are implementing the interface
	 * sending all the result data to the object observing the request
	 * 
	 * @param aArray        a bytearray containing all the result data of the 
	 *                      last query
	 */
	public void onDownloadFinished(byte[] array);
	
	public void onScanFinished(ItemContact[] itemcontacts);

	/**
	 * Method onBytesLoaded
	 * to be overwritten in classes which are implementing the interface
	 * sending an counter containing the totally number of downloaded bytes of
	 * the actual request
	 * 
	 * @param bytes        containing the number of bytes totally downloaded
	 *                     by the actual request
	 */
	public void onBytesLoaded(int bytes);

	/**
	 * Method onAborted
	 * To be overwritten in classes which are implementing the interface.
	 * This method is called whenever the request has to be aborted and cannot 
	 * return a response. A reason may be given for the abortion.
	 * 
	 * @param reason		an optional reason for the the abortion
	 */
	public void onAborted(String reason);
	
	public void stop();

}
