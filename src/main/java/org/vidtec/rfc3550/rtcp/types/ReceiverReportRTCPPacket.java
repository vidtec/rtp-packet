package org.vidtec.rfc3550.rtcp.types;

import org.vidtec.rfc3550.rtcp.RTCPPacket;
import org.vidtec.rfc3550.rtp.RTPPacket;

/**
 * 
 * TODO
 *
 */
public class ReceiverReportRTCPPacket extends RTCPPacket
{

	/*
    0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
header |V=2|P|    RC   |   PT=RR=201   |             length            |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                     SSRC of packet sender                     |
   +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
report |                 SSRC_1 (SSRC of first source)                 |
block  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
1    | fraction lost |       cumulative number of packets lost       |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |           extended highest sequence number received           |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                      interarrival jitter                      |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                         last SR (LSR)                         |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                   delay since last SR (DLSR)                  |
   +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
report |                 SSRC_2 (SSRC of second source)                |
block  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
2    :                               ...                             :
   +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
   |                  profile-specific extensions                  |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	*/
	
	
	
	/**
	 * A ReceiverReportBuilder class to build {@link RTPPacket} instances.
	 */
	public static final class ReceiverReportBuilder 
	{
//		private int payloadType = -1;
//		private long ssrcIdentifier = -1;
//
//		private byte[] payload;
//		private short paddingBytes = 0;
//
//		
//		/**
//		 * Private constructor.
//		 */
//		private ReceiverReportBuilder() { /* Empty Constructor */ }
//
//		/**
//		 * This packet should have required header fields set.
//		 * 
//		 * @param payloadType The payload type.
//		 * @param ssrc The ssrc identifier.
//		 * @return The ReceiverReportBuilder instance.
//		 */
//		public ReceiverReportBuilder withRequiredHeaderFields(final int payloadType, final long ssrc) 
//		{
//			this.payloadType = payloadType;
//			this.ssrcIdentifier = ssrc;
//			return this;
//		}

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
