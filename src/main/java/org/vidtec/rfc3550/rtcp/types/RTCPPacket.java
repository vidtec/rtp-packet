package org.vidtec.rfc3550.rtcp.types;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of an RTCP packet according to RFC 3550/.
 * https://tools.ietf.org/html/rfc3550
 * 
 * TODO: support extensions as per RFC 3611
 */
public abstract class RTCPPacket<T> 
{
	
	// RTCP Packet header format is defined as: (per RFC 3550, section 6.1)
	//
	//    0                   1                   2                   3
	//    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//   |V=2|P|   ??    |       PT      |            length             |
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//   |                     packet type specific                      |
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

	// RTCP packets must be 32-bit boundary aligned.
	// RTCP packets can be compound - if they are:
	//  - the first packet must have payload type = SR/RR
	//  - the overall packet size cannot exceed MTU of the transport.
	
	
	/** The RTCP version constant. */
	public static final short VERSION = 2;

	
	/** The packet payload type */
	private final PayloadType type;
	
	
	/**
	 * Create an RTCP packet.
	 * 
	 * @param type The payload type for this packet.
	 */
	protected RTCPPacket(final PayloadType type)
	{
		this.type = type;
	}
	
	
	/**
	 * Check if this packet is of a given type.
	 * 
	 * @param type The payload type to compare against.
	 * @return true if the types match, false otherwise.
	 */
	public boolean is(final PayloadType type)
	{
		return this.type == type;
	}

	
	/**
	 * Get the packet payload type.
	 * 
	 * @return The packet's payload type.
	 */
	public PayloadType payloadType()
	{
		return type;
	}
	

	/**
	 * Return the full length of the packet in bytes.
	 * 
	 * @return The number of bytes required for this packet.
	 */
	public abstract int packetLength(); 
	
	
	/**
	 * Gets the packet data as a byte[].
	 * 
	 * @return a copy of the RTCP packet data.
	 */
	public abstract byte[] asByteArray();
	
	
	/**
	 * Helper method to return the packet object as it's real class type (prevents casting in client code).
	 * 
	 * @return The concrete packet type <T>.
	 */
	@SuppressWarnings("unchecked")
	public T asConcreteType()
	{
		return (T)this;
	}
	
	
	/**
	 * A helper method to extract the RTCP packet type from a bytebuffer without
	 * needing to decode the whole packet. 
	 * 
	 * NB: This method expects that the bytebuffer position is at the START of the RTCP packet.
	 * NB: This method will mark and rewind, so the position of the buffer is not mutated
	 *     as a side-effect of use before full decode.
	 * 
	 * @param buffer The ByteBuffer instance to check.
	 * @return The payload type (if valid)
	 * 
	 * @throws IllegalArgumentException If the there is a problem extracting the payload type or it is invalid.
	 */
	public static PayloadType peekPayloadType(final ByteBuffer buffer)
	throws IllegalArgumentException
	{
		buffer.mark();
		try
		{
			if (buffer.remaining() < 2)
			{
				// not enough data in the packet.
				throw new IllegalArgumentException("Invalid packet length - too short to peek type.");
			}

			// payload type is in second byte of packet.
			return PayloadType.fromTypeValue(0xFF & buffer.getShort());
		}
		finally
		{
			buffer.reset();
		}
	}
	
	
	/**
	 * A helper method to extract the RTCP packet length from a bytebuffer without
	 * needing to decode the whole packet. 
	 * 
	 * NB: This method expects that the bytebuffer position is at the START of the RTCP packet.
	 * NB: This method will mark and rewind, so the position of the buffer is not mutated
	 *     as a side-effect of use before full decode.
	 * 
	 * @param buffer The ByteBuffer instance to check.
	 * @return The packet length (if valid).
	 * 
	 * @throws IllegalArgumentException If the there is a problem extracting the payload length or it is invalid.
	 */
	public static int peekStatedLength(final ByteBuffer buffer)
	throws IllegalArgumentException
	{
		buffer.mark();
		try
		{
			if (buffer.remaining() < 2)
			{
				// not enough data in the packet.
				throw new IllegalArgumentException("Invalid packet length - too short to peek stated length.");
			}

			// Stated length is in bytes 3-4 of packet.
			return 0x0000FFFF & buffer.getInt();
		}
		finally
		{
			buffer.reset();
		}		
	}
	

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
		{
			final PayloadType type = TYPES.get(Integer.valueOf(value));
			if (type == null)
			{
				throw new IllegalArgumentException("Unknown type - " + String.valueOf(value));
			}
			
			return type;
		}
	}
	
}
