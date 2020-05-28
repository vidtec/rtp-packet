package org.vidtec.rfc3550.rtcp;

import org.vidtec.rfc3550.rtcp.types.AppRTCPPacket;
import org.vidtec.rfc3550.rtcp.types.ByeRTCPPacket;
import org.vidtec.rfc3550.rtcp.types.ReceiverReportRTCPPacket;
import org.vidtec.rfc3550.rtcp.types.SdesRTCPPacket;
import org.vidtec.rfc3550.rtcp.types.SenderReportRTCPPacket;

/**
 * An interface for supporting visiting RTCP packets in a stream.
 */
public interface RTCPPacketsVisitor 
{

	/**
	 * Handle a SR RTCP packet in the packet stream.
	 * 
	 * @param packet The packet instance.
	 */
	default void visit(final SenderReportRTCPPacket packet)
	{
		// Do nothing
	};
	
	
	/**
	 * Handle a RR RTCP packet in the packet stream.
	 * 
	 * @param packet The packet instance.
	 */
	default void visit(final ReceiverReportRTCPPacket packet)
	{
		// Do nothing
	};
	

	/**
	 * Handle a SDES RTCP packet in the packet stream.
	 * 
	 * @param packet The packet instance.
	 */
	default void visit(final SdesRTCPPacket packet)
	{
		// Do nothing
	};	
	

	/**
	 * Handle a APP RTCP packet in the packet stream.
	 * 
	 * @param packet The packet instance.
	 */
	default void visit(final AppRTCPPacket packet)
	{
		// Do nothing
	};
	
	
	/**
	 * Handle a BYE RTCP packet in the packet stream.
	 * 
	 * @param packet The packet instance.
	 */
	default void visit(final ByeRTCPPacket packet)
	{
		// Do nothing
	};
	
}
