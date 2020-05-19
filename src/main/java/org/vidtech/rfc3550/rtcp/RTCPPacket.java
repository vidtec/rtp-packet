package org.vidtech.rfc3550.rtcp;

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
	
	// Supported packet types and values
	//
	// SR     200   sender report          
	// RR     201   receiver report          
	// SDES   202   source description        
	// BYE    203   goodbye          
	// APP    204   application-defined          

	
	public static enum PayloadType
	{
		RR
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
	
	
	
	
	
	
	public boolean isCompund()
	{
		return false;
	}
	
	
	public boolean is(final PayloadType type)
	{
		// if compund cannot be ....
		
		// check against type.
		
		return false;
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
