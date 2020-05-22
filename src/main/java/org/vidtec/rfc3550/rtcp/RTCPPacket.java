package org.vidtech.rfc3550.rtcp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of an RTCP packet according to RFC 3550/.
 * https://tools.ietf.org/html/rfc3550
 * 
 * TODO: support extensions as per RFC 3611
 */
public abstract class RTCPPacket 
{
	
	// RTCP Packet header format is defined as: (per RFC 3550, section 6.1)
	//
	//    0                   1                   2                   3
	//    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//   |V=2|P|   RC    |       PT      |            length             |
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//   |           synchronization source (SSRC) identifier            |
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

	// RTCP packets must be 32-bit boundary aligned.
	// RTCP packets can be compound - if they are:
	//  - the first packet must have payload type = SR/RR
	//  - the overall packet size cannot exceed MTU of the transport.
	
	
	/**
	 * An enumeration of payload types.
	 * 
	 * 	 Supported packet types and values
	 * 	 	
	 * 	 SR     200   sender report          
	 * 	 RR     201   receiver report          
	 * 	 SDES   202   source description        
	 * 	 BYE    203   goodbye          
	 * 	 APP    204   application-defined          
	 */
	public static enum PayloadType
	{
		SR(200), RR(201), SDES(202), BYE(203), APP(204);
		
		/** The numeric placeholder. */
		public final short pt;
		
		/** internal cache of values to enumerations. */
		private static final Map<Integer, PayloadType> TYPES = new HashMap<>();
		
		
		static
		{
			Arrays.stream(PayloadType.values()).forEach(t -> TYPES.put(Integer.valueOf(t.pt), t));
		}
		
		
		/**
		 * Create an enumeration with a numeric value.
		 * 
		 * @param value The payload type value as per RFC3550 and extensions.
		 */
		private PayloadType(final int value)
		{
			pt = (short)(0xFF & value);
		}
		
		
		/**
		 * Get a payload type enumeration from a packet value.
		 * 
		 * @param value The payload type value.
		 * @return The corresponding enumeration instance.
		 * 
		 * @throws IllegalArgumentException If the value given is not valid.
		 */
		public static PayloadType fromTypeValue(final int value)
		throws IllegalArgumentException
		{
			final PayloadType type = TYPES.get(Integer.valueOf(value));
			if (type == null)
			{
				throw new IllegalArgumentException("Unknown type - " + String.valueOf(value));
			}
			
			return type;
		}
		
	}
	
	// Supported SDES types
	//
	// END    end of SDES list 
	// CNAME  canonical name   
	// NAME   user name        
	// EMAIL  user's electronic mail address  
	// PHONE  user's phone number             
	// LOC    geographic user location        
	// TOOL   name of application or tool  
	// NOTE   notice about the source     
	// PRIV   private extensions  
	
	
	

	
	
	public boolean is(final PayloadType type)
	{
		// if compund cannot be ....
		
		// check against type.
		
		return false;
	}


	public int length() 
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
//	/**
//	 * Creates a builder to manually build an {@link SenderReportRTCPPacket}.
//	 * 
//	 * @return The builder instance.
//	 */
//	public static SenderReportBuilder senderReportBuilder() 
//	{
//		return new SenderReportBuilder();
//	}
	
	/**
	 * Creates a builder to manually build an {@link ReceiverReportRTCPPacket}.
	 * 
	 * @return The builder instance.
	 */
//	public static ReceiverReportBuilder receiverReportBuilder() 
//	{
//		return new ReceiverReportBuilder();
//	}
	
	
	
	
	
}
