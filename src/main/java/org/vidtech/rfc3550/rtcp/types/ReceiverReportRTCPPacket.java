package org.vidtech.rfc3550.rtcp.types;

import java.util.Arrays;

import org.vidtech.rfc3550.rtcp.RTCPPacket;
import org.vidtech.rfc3550.rtp.RTPPacket;

/**
 * 
 * TODO
 *
 */
public class ReceiverReportRTCPPacket extends RTCPPacket
{

	
	
	
	/**
	 * A ReceiverReportBuilder class to build {@link RTPPacket} instances.
	 */
	public static final class ReceiverReportBuilder 
	{
		private int payloadType = -1;
		private long ssrcIdentifier = -1;

		private byte[] payload;
		private short paddingBytes = 0;

		
		/**
		 * Private constructor.
		 */
		private ReceiverReportBuilder() { /* Empty Constructor */ }

		/**
		 * This packet should have required header fields set.
		 * 
		 * @param payloadType The payload type.
		 * @param ssrc The ssrc identifier.
		 * @return The ReceiverReportBuilder instance.
		 */
		public ReceiverReportBuilder withRequiredHeaderFields(final int payloadType, final long ssrc) 
		{
			this.payloadType = payloadType;
			this.ssrcIdentifier = ssrc;
			return this;
		}

// FIX ME PT known
		
		


		
		/**
		 * Build the packet.
		 * 
		 * @return The packet instance.
		 * 
		 * @throws IllegalArgumentException If there is a problem with the supplied packet data.
		 */
		public ReceiverReportRTCPPacket build() 
		{
			return null;
			//return new RTPPacket(this);
		}
	}

	
}
