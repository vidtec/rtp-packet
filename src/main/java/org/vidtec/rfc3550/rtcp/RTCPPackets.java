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
import org.vidtec.rfc3550.rtcp.types.app.AppRTCPPacket;
import org.vidtec.rfc3550.rtcp.types.bye.ByeRTCPPacket;
import org.vidtec.rfc3550.rtcp.types.report.ReceiverReportRTCPPacket;
import org.vidtec.rfc3550.rtcp.types.report.SenderReportRTCPPacket;
import org.vidtec.rfc3550.rtcp.types.sdes.SdesRTCPPacket;

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
	{
		if (builder.packets.isEmpty())
		{
			throw new IllegalArgumentException("container must have at least one packet.");
		}
		if (builder.packets.size() > 1)
		{
			final PayloadType pt = builder.packets.get(0).payloadType();
			if (!(PayloadType.SR == pt) && !(PayloadType.RR == pt))
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
	 * @param bb The ByteBuffer to decode from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the data.
	 */
	private RTCPPackets(final ByteBuffer bb)
	{

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
			final PayloadType pt = RTCPPacket.peekPayloadType(bb);
			if (!(PayloadType.SR == pt) && !(PayloadType.RR == pt))
			{
				throw new IllegalArgumentException("This looks like a compound packet, but first entry is NOT SR or RR.");
			}
		}
		
		while (bb.hasRemaining())
		{

			final int nextPacketLength = RTCPPacket.peekStatedLength(bb);
			
// if bb.reamining < packetlength - thorw IAE
			// if remiaining data - error in packet
			//		throw new IlleglArugE("Unexpected error - data remaining after packet decodes.");

// At the very least there must be a header remaining ... if not error time.
			


			// Work out the packet type.
			final PayloadType payloadType = RTCPPacket.peekPayloadType(bb);

			// Read the packet data,
			final byte[] buffer = new byte[nextPacketLength];
			bb.get(buffer);
			
			// Now parse based on the payload type.
			switch (payloadType)
			{
				case SR:
				{
					packets.add(SenderReportRTCPPacket.fromByteArray(buffer));
					break;
				}
				case RR:
				{
					packets.add(ReceiverReportRTCPPacket.fromByteArray(buffer));
					break;
				}
				case SDES:
				{
					packets.add(SdesRTCPPacket.fromByteArray(buffer));
					break;
				}
				case APP:
				{
					packets.add(AppRTCPPacket.fromByteArray(buffer));
					break;
				}
				case BYE:
				{
					packets.add(ByeRTCPPacket.fromByteArray(buffer));
					break;
				}
			}
		}
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
	
	
	/**
	 * Visit all the packets in this container with a visitor that performs some actions.
	 * 
	 * @param visitor A visitor instance to interrogate the packets.
	 */
	public void visit(final RTCPPacketsVisitor visitor)
	{
		for (RTCPPacket<?> p : packets)
		{
			switch (p.payloadType())
			{
				case SR:
				{
					visitor.visit((SenderReportRTCPPacket)p);
					break;
				}
				case RR:
				{
					visitor.visit((ReceiverReportRTCPPacket)p);
					break;
				}
				case SDES:
				{
					visitor.visit((SdesRTCPPacket)p);
					break;
				}
				case APP:
				{
					visitor.visit((AppRTCPPacket)p);
					break;
				}
				case BYE:
				{
					visitor.visit((ByeRTCPPacket)p);
					break;
				}
			}
		}
	}
	
	
// FIXME - We need to handle packets bigger than a single MTU	
	
	
	/**
	 * Gets the data from this container as a byte[].
	 * NB: If the container is compound, the returned byte[] will be the compound packet.
	 * NB: It is the responsibility of the sender to determine if this fits inside a single transmission MTU.
	 * 
	 * @return a copy of the RDP packet data.
	 */
	public byte[] asByteArray()
	{
		final byte[] data = new byte[lengthAsPacket()];
		final ByteBuffer bb = ByteBuffer.wrap(data);
		
		packets.stream().forEach(p -> bb.put(p.asByteArray()));
		
		return data;
	}
	
	
	/**
	 * Gets the data from this container as a DatagramPacket.
	 * NB: If the container is compound, the returned byte[] will be the compound packet.
	 * NB: It is the responsibility of the sender to determine if this fits inside a single transmission MTU.
	 * 
	 * @param address The InetAddress that the datagram will be sent to.
	 * @param port The port that the datagram will be sent to.
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
	 *             	  
	 * @param data DatagramPacket construct a RTCP packet(s) from.
	 * @return The RTCPPackets instance representing the given data.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	public static RTCPPackets fromByteArray(final byte[] data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("packet data cannot be null");
		}
		
		return new RTCPPackets( ByteBuffer.wrap(data) );
	}
	
	
	/**
	 * Returns an RTCPPackets object derived from a given DatagramPacket.
	 * NB: A payload may be one to more RTTCP packets (compound), so a container is returned.
	 * 
	 * @param packet DatagramPacket construct a RTCP packet(s) from.
	 * @return The RTCPPackets instance representing the given data.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	public static RTCPPackets fromDatagramPacket(final DatagramPacket packet)
	{
		if (packet == null)
		{
			throw new IllegalArgumentException("packet cannot be null");
		}
		
		return new RTCPPackets( ByteBuffer.wrap(packet.getData(), 0, packet.getLength()) );
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
	 * Creates a builder to manually build an {@link RTCPPackets}.
	 * 
	 * @param packets The collections of packets to add.
	 * @return The RTCPPackets instance built from the supplied data.
	 */
	public static RTCPPackets buildWithPackets(final RTCPPacket<?> ... packets) 
	{
		return new Builder().withPackets(packets).build();
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
		 * This container should have a packet.
		 * 
		 * @param packet A packet.
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
