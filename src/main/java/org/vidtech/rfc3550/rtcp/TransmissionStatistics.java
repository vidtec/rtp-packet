package org.vidtech.rfc3550.rtcp;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class to manage RTP transmission statistics needed in RTCP SR/RR reports.
 * 
 * This class should be considered thread-safe.
 */
public class TransmissionStatistics 
{
	
	
	
	// https://www.freesoft.org/CIE/RFC/1889/53.htm
	
	
	/** The fraction of packets lost. */
	private final AtomicInteger fractionLoss = new AtomicInteger(0);
	
	/** The total lost packet count. */
	private final AtomicLong cumulativeLost = new AtomicLong(0);
	
	
	// initital sequence number
	
	// max etxneded sequence number 
	
	// cycles - += 
	
	
	
	
	
	private TransmissionStatistics(final int sequenceNumber) 
	{
		// TODO Auto-generated constructor stub
	}


	/**
	 * A packet was received, update the statistics based on the sequence number.
	 * 
	 * @param sequenceNumber The received packet sequence number.
	 */
	public void update(final int sequenceNumber)
	{
		
	}
	
	
	public static TransmissionStatistics fromStartSequence(final int sequenceNumber)
	{
		return new TransmissionStatistics(sequenceNumber);
	}


	public short fractionLost() {
		// TODO Auto-generated method stub
		return 0;
	}


	/**
	 * 
	 *     the fractionLost value will, however, continue to be updated based
	 *     on total receipt statistics.
	 *     
	 * @return
	 */
	public long totalLost() 
	{
		// TODO Auto-generated method stub
		return 0;
	}


	public long maxSequenceNumber() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
