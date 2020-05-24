package org.vidtec.rfc3550.rtcp.stats;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class to manage RTP transmission statistics needed in RTCP SR/RR reports.
 * 
 * This class should be considered thread-safe.
 */
public class TransmissionStatistics 
{
	
	
	// TODO: need to be able to also calculate betwee two stats
	
	// https://www.freesoft.org/CIE/RFC/1889/53.htm
	
	
	/** The fraction of packets lost. */
//	private final AtomicInteger fractionLoss = new AtomicInteger(0);
	
	/** The total lost packet count. */
	private final AtomicLong lost = new AtomicLong(0);

	/** The total received packet count. */
	private final AtomicLong received = new AtomicLong(0);
	
	
	/** The most recent sequence number received. */
	private final AtomicInteger lastSequenceNumberReceived;
	
	/** The maximum sequence number received. */
	private final AtomicLong maxSequenceNumberReceived;
	
	
	// initital sequence number
	
	// max etxneded sequence number 
	
	// cycles - += 
	
	
	
	
	/**
	 * Create a statistic object based on the given start sequence number.
	 * 
	 * @param sequenceNumber The sequence number to start with.
	 */
	private TransmissionStatistics(final int sequenceNumber) 
	{
		// validate
		
		lastSequenceNumberReceived = new AtomicInteger(sequenceNumber);
		maxSequenceNumberReceived = new AtomicLong(sequenceNumber);
	}


	/**
	 * Return the fraction of packets lost as per RFC 3550.
	 * @return The fraction of lost packets as an integer fraction part.
	 */
	public short fractionLost() 
	{
// V WRONG - needs to be corrected
// temp hack
		long lost = this.lost.get();
		long received = this.received.get();
		return received == 0 ? 0 : (short)(0xFF & ((lost << 8) / received));
	}

	
// todo clamp in report block	
//	*     the fractionLost value will, however, continue to be updated based
//	*     on total receipt statistics.

	
	/**
	 * Get the total number of packets lost.
	 *     
	 * @return The total lost packet count.
	 */
	public long lost() 
	{
		return lost.get();
	}


	/**
	 * Get the total number of packets received.
	 * 
	 * @return The total packet count.
	 */
	public long received() 
	{
		return received.get();
	}
	

	/**
	 * Get the maximum extended sequence number (32-bit integer).
	 * 
	 * @return The maximum extended sequence number seen.
	 */
	public long maxExtendedSequenceNumber() 
	{
// WRONG - calculate from cycles + max seq no
		return maxSequenceNumberReceived.get();
	}


	/**
	 * A packet was received, update the statistics based on the sequence number.
	 * 
	 * @param sequenceNumber The received packet sequence number.
	 */
	public void update(final int sequenceNumber)
	{
// todo validate.
// V WRONG - temp hack
		
		// not at all correct - jsut inc packet for now.
		received.incrementAndGet();
		
		lastSequenceNumberReceived.set(sequenceNumber);
		maxSequenceNumberReceived.updateAndGet(m -> Math.max(sequenceNumber, m));
		
	}
	
	
	/**
	 * Get a statistics object starting at the given sequence number.
	 * 
	 * @param sequenceNumber The number to start with.
	 * @return The empty statistics object.
	 * 
	 * @throws IllegalArgumentException If the sequence number is invalid.
	 */
	public static TransmissionStatistics fromStartSequence(final int sequenceNumber)
	{
		return new TransmissionStatistics(sequenceNumber);
	}
	
}
