package org.vidtec.rfc3550.rtcp.types.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vidtec.rfc3550.rtcp.types.RTCPPacket;

/**
 * An implementation of an RTCP report-related packet types (SR/RR) according to RFC 3550/.
 * https://tools.ietf.org/html/rfc3550
 */
public abstract class ReportRTCPPacket<T> extends RTCPPacket<T>
{

	/** The SSRC of the sender. */
	private final long ssrcSenderIdentifier;
	
	/** The report blocks in this packet. */
	private final List<ReportBlock> reportBlocks = new ArrayList<>();
	
	
	/**
	 * Create a Report RTCP packet.
	 * 
	 * @param type The packet type.
	 * @param ssrcIdentifier The sender SSRC identifier.
	 * @param blocks The list of report blocks.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	protected ReportRTCPPacket(final PayloadType type, final long ssrcIdentifier, final List<ReportBlock> blocks)
	{
		super(type);

		if (blocks.size() > 31) // 0x1F
		{
			throw new IllegalArgumentException("maximum report block size exceeded, expected at most 31, but was " + blocks.size());
		}
		
		this.ssrcSenderIdentifier = ssrcIdentifier;
		this.reportBlocks.addAll(blocks);
	}
	
	
	/**
	 * Gets the sender's sync. source identifier.
	 * 
	 * @return The sender's ssrc identifier as a 32 bit unsigned integer.
	 */
	public long ssrcSenderIdentifier()
	{
		return ssrcSenderIdentifier;
	}
	
	
	/**
	 * Indicates if this packet has report blocks.
	 * 
	 * @return true if blocks are present, false otherwise.
	 */
	public boolean hasBlocks() 
	{
		return !reportBlocks.isEmpty();
	}
	

	/**
	 * Gets the number of report block elements.
	 * 
	 * @return An integer count from 0 - 31.
	 */
	public short blockCount() 
	{
		return (short) reportBlocks.size();
	}
	
	
	/**
	 * Gets the report blocks in this packet.
	 * 
	 * @return The report blocks.
	 */
	public List<ReportBlock> blocks()
	{
		return Collections.unmodifiableList(reportBlocks);
	}
	
}
