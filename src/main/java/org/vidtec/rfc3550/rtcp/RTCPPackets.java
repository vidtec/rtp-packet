package org.vidtec.rfc3550.rtcp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * A container for RTCP packets. This is the entry point for 
 * decoding RTCP packets as received, as they could be compound.
 * 
 * https://tools.ietf.org/html/rfc3550
 */
public final class RTCPPackets 
{

	/* The list of packets. */
	private final List<RTCPPacket> packets = new ArrayList<>();
	
	
	
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
		// packet must be at least minimum of one header
		
		
		// wrap in byte buffer
		
		// while available == true
		
			// decode first packet
			
			// is there remaining ?? - if so repeast
			
		
		// OR
		
		// inspect header
		
		// if header < totalength then - look ahead methods
		
		
		
		
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
	private int lengthAsPacket() 
	{
		// Iterate each packet, and sum the packet length, this is the compound length.
		return packets.stream().flatMapToInt(packet -> IntStream.of(packet.length())).sum();
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
	 * @param packet DatagramPacket construct a RTCP packet(s) from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	public static RTCPPackets fromByteArrat(final byte[] data)
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
		return fromByteArrat(packet.getData());
	}
	
	
}
