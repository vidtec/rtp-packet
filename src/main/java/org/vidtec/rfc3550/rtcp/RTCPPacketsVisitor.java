package org.vidtec.rfc3550.rtcp;

import org.vidtec.rfc3550.rtcp.types.ReceiverReportRTCPPacket;

/**
 * An interface for supporting visiting RTCP packets in a stream.
 */
public interface RTCPPacketsVisitor 
{

	/**
	 * Handle a RR RTCP packet in the packet stream.
	 * 
	 * @param packet The packet instance.
	 */
	default void visit(final ReceiverReportRTCPPacket packet)
	{
		// Do nothing
	};
	
}
