package org.vidtec.rfc3550.rtcp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.vidtec.rfc3550.rtcp.types.RTCPPacket;
import org.vidtec.rfc3550.rtcp.types.RTCPPacket.PayloadType;

/**
 * A container for RTCP packets. This is the entry point for 
 * decoding RTCP packets as received, as they could be compound.
 * 
 * https://tools.ietf.org/html/rfc3550
 */
public final class RTCPPackets 
{

	/* The list of packets. */
	private final List<RTCPPacket<?>> packets = new ArrayList<>();
	
	
	/**
	 * Construct a packet container with decoded packets from a given byte[]
	 * NB: This will validate the inbound data.
	 * NB: The container will hold one or more RTCP packets, 
	 *     use isCompound() to determine this.
	 * 
	 * @param builder The builder instance to construct a packet from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the data.
	 */
	private RTCPPackets(final Builder builder)
	throws IllegalArgumentException
	{
		if (builder.packets.isEmpty())
		{
			throw new IllegalArgumentException("container must have at least one packet.");
		}
		if (builder.packets.size() > 1)
		{
			final PayloadType pt = builder.packets.get(0).payloadType();
			if (!PayloadType.SR.equals(pt) && !PayloadType.RR.equals(pt))
			{
				throw new IllegalArgumentException("This looks like a compound packet, but first entry is NOT SR or RR.");
			}
		}
		
		this.packets.addAll(builder.packets);
	}
	
	
	/**
	 * Construct a packet container with decoded packets from a given byte[]
	 * NB: This will validate the inbound data.
	 * NB: The container will hold one or more RTCP packets, 
	 *     use isCompound() to determine this.
	 * 
	 * @param data The byte[] to decode from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the data.
	 */
	private RTCPPackets(final byte[] data)
	throws IllegalArgumentException
	{
		final ByteBuffer bb = ByteBuffer.wrap(data);

		// packet must be at least minimum of one header (min 32 bits)
		if (bb.remaining() < 4)
		{
			throw new IllegalArgumentException("Invalid packet length - too short.");
		}
		
		// Determine if we THINK this is a compound packet 
		// i.e. this packet length is more than declared length of first packet.
		final int firstStatedLength = RTCPPacket.peekStatedLength(bb);
		final boolean isCompound = firstStatedLength < bb.remaining();
		
		if (isCompound)
		{
			// If we are compound ... first should be SR or RR
			final PayloadType firstPayloadType = RTCPPacket.peekPayloadType(bb);
			if (!PayloadType.SR.equals(firstPayloadType) && !PayloadType.RR.equals(firstPayloadType))
			{
				throw new IllegalArgumentException("This looks like a compound packet, but first entry is NOT SR or RR.");
			}
		}
		
		while (bb.hasRemaining())
		{

			// if bb.reamining < packetlength - thorw IAE
			
			// At the very least there must be a header remaining ... if not error time.
			
			
			// Now parse based on the payload type.
			final PayloadType payloadType = RTCPPacket.peekPayloadType(bb);
			switch (payloadType)
			{
				case SR:
				{
//					packets.add(SenderReportRTCPPacket.fromByteArray(bb));
					break;
				}
				case RR:
				{
//					packets.add(ReceiverReportRTCPPacket.fromByteArray(bb));
					break;
				}
				case SDES:
				{
//					packets.add(SDESRTCPPacket.fromByteArray(bb));
					break;
				}
				case APP:
				{
//					packets.add(AppRTCPPacket.fromByteArray(bb));
					break;
				}
				case BYE:
				{
//					packets.add(ByeRTCPPacket.fromByteArray(bb));
					break;
				}
			}
		}
		
		
		
		
		
		// if remiaining data - error in packet
		//		throw new IlleglArugE("Unexpected error - data remaining after packet decodes.");
		
		
		
	}
	
	
	/**
	 * Are there multiple (compound) RTCP packets ?
	 * 
	 * @return true if this container is compound (multiple packets), false otherwise.
	 */
	public boolean isCompund()
	{
		return packets.size() > 1;
	}
	

	/**
	 * Get the length of all the RTCP packets when combined.
	 * 
	 * @return The total length of all RTCP packets in the container.
	 *         NB: This will be same as the packet length if only 1 packet is present.
	 */
	public int lengthAsPacket() 
	{
		// Iterate each packet, and sum the packet length, this is the compound length.
		return packets.stream().flatMapToInt(packet -> IntStream.of(packet.packetLength())).sum();
	}
	
	
	/**
	 * Get the list of packets in the container.
	 * 
	 * @return A list of RTCP packets contained herein.
	 */
	public List<RTCPPacket<?>> packets()
	{
		return Collections.unmodifiableList(packets);
	}
	
	
	
	
	
	public void visit(final RTCPPacketsVisitor visitor)
	{
//		packets.forEach(p -> visitor.visit(p.asConcreteType()));
	}
	
	
	
	
	
	
	
// FIXME - refactor - use byte[][] and list - detaulf method takes mtu DEFAULT - can override	
	
	
	/**
	 * Gets the data from this container as a byte[].
	 * NB: If the container is compound, the returned byte[] will be the compound packet.
	 * NB: It is the responsibility of the sender to determine if this fits inside a single transmission MTU.
	 * 
	 * @return a copy of the RDP packet data.
	 */
	public byte[] asByteArray()
	{
		return null;
	}
	
	
	/**
	 * Gets the data from this container as a DatagramPacket.
	 * NB: If the container is compound, the returned byte[] will be the compound packet.
	 * NB: It is the responsibility of the sender to determine if this fits inside a single transmission MTU.
	 * 
	 * @param address The InetAddress that the datagram will be sent to.
	 * @param port The port that the datagram will be sent to.
	 * 
	 * @return A DatagramPacket that is ready to send.
	 */
	public DatagramPacket asDatagramPacket(InetAddress address, int port) 
	{
		return new DatagramPacket(asByteArray(), lengthAsPacket(), address, port);
	}
	

	/**
	 * Returns an RTCPPackets object derived from a given byte[].
	 * NB: A payload may be one to more RTCP packets (compound), so a container is returned.
	 *     
	 *     
	 * NB: FIXME - this method assumes that all packets are in the data[] given
	 *             it does not handle re-assembly from multiple protocol layer (UDP)
	 *             frames.    
	 *             	 * 
	 * @param packet DatagramPacket construct a RTCP packet(s) from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	public static RTCPPackets fromByteArray(final byte[] data)
	throws IllegalArgumentException
	{
		return new RTCPPackets(data);
	}
	
	
	/**
	 * Returns an RTCPPackets object derived from a given DatagramPacket.
	 * NB: A payload may be one to more RTTCP packets (compound), so a container is returned.
	 * 
	 * @param packet DatagramPacket construct a RTCP packet(s) from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	public static RTCPPackets fromDatagramPacket(final DatagramPacket packet)
	throws IllegalArgumentException
	{
		return fromByteArray(packet.getData());
	}
	
	
	/**
	 * Creates a builder to manually build an {@link RTCPPackets}.
	 * 
	 * @return The builder instance.
	 */
	public static Builder builder() 
	{
		return new Builder();
	}

	
	/**
	 * A Builder class to build {@link RTCPPackets} instances.
	 */
	public static final class Builder 
	{
		private List<RTCPPacket<?>> packets = new ArrayList<>();

		
		/**
		 * Private constructor.
		 */
		private Builder() { /* Empty Constructor */ }


		/**
		 * This container should have packets.
		 * 
		 * @param packets A list of packets.
		 * @return The builder instance.
		 */
		public Builder withPacket(final RTCPPacket<?> packet)
		{
			if (packet != null)
			{
				this.packets.add(packet);
			}
			
			return this;
		}
		
		
		/**
		 * This container should have packets.
		 * 
		 * @param packets A list of packets.
		 * @return The builder instance.
		 */
		public Builder withPackets(final RTCPPacket<?> ... packets)
		{
			if (packets != null)
			{
				this.packets.addAll(Arrays.asList(packets));
			}
			
			return this;
		}

		
		/**
		 * This container should have packets.
		 * 
		 * @param packets A list of packets.
		 * @return The builder instance.
		 */
		public Builder withPackets(final List<RTCPPacket<?>> packets)
		{
			if (packets != null)
			{
				this.packets.addAll(packets);
			}
			
			return this;
		}

		
		/**
		 * Build the packet.
		 * 
		 * @return The packet instance.
		 * 
		 * @throws IllegalArgumentException If there is a problem with the supplied packet data.
		 */
		public RTCPPackets build() 
		{
			return new RTCPPackets(this);
		}
	}
	
	
	
	
	
}