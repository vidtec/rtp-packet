package org.vidtec.rfc3550.rtcp;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;
import org.vidtec.rfc3550.rtcp.types.ByeRTCPPacket;
import org.vidtec.rfc3550.rtcp.types.RTCPPacket;
import org.vidtec.rfc3550.rtcp.types.RTCPPacket.PayloadType;
import org.vidtec.rfc3550.rtcp.types.ReceiverReportRTCPPacket;

@Test
public class RTCPPacketsTest 
{

	public void testCorrecltValidatesInvalidPacketData()
	{
		try
		{
			// null
			RTCPPackets.fromByteArray( null );
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "packet data cannot be null", "wrong validation message");
		}
				try
		{
			// too short
			byte[] data = { (byte)0x80 };
			RTCPPackets.fromByteArray( data );
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Invalid packet length - too short.", "wrong validation message");
		}
		
		try
		{
			// SR/RR not first
			byte[] data = { (byte)0x80, (byte)0xCA, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00,
					        (byte)0x80, (byte)0xC9, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00 };
			RTCPPackets.fromByteArray( data );
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "This looks like a compound packet, but first entry is NOT SR or RR.");
		}
		
		try
		{
			// Invalid packet type in stream
			byte[] data = { (byte)0x80, (byte)0xC9, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00,
					        (byte)0x80, (byte)0xF9, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00 };
			RTCPPackets.fromByteArray( data );
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Unknown type - 249");
		}		
				
		try
		{
			// Invalid packet type in stream
			byte[] data = { (byte)0x80, (byte)0xCB, 0x00, 0x04,
					        (byte)0x80, (byte)0xF9, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00 };
			RTCPPackets.fromByteArray( data );
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "This looks like a compound packet, but first entry is NOT SR or RR.");
		}		
		
		

		
	}

	public void testCanCreatePacketsContainerFromSinglePacketAsByteArray()
	{
		byte[] data = { (byte)0x80, (byte)0xC9, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00 };
		
		RTCPPackets packets = RTCPPackets.fromByteArray( data );

		assertEquals(packets.lengthAsPacket(), 8, "incorrect sizing");
		assertEquals(packets.isCompund(), false, "container should not be compound");
		assertTrue(packets.packets() != null, "packets should be valid");
		assertEquals(packets.packets().size(), 1, "container should not be compound");
		assertEquals(packets.packets().get(0).payloadType(), PayloadType.RR, "container should have valid order");
		
		assertEquals(packets.asByteArray(), data, "packet not reassembled correctly.");
	}

	
	public void testCanCreatePacketsContainerFromMultiplePacketsAsByteArray()
	{
		byte[] data = { (byte)0x80, (byte)0xC9, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00,
 		  	   	        (byte)0x80, (byte)0xCB, 0x00, 0x04 };

		RTCPPackets packets = RTCPPackets.fromByteArray( data );
		
		assertEquals(packets.lengthAsPacket(), 12, "incorrect sizing");
		assertEquals(packets.isCompund(), true, "incorrect sizing");
		assertTrue(packets.packets() != null, "packets should be valid");
		assertEquals(packets.packets().size(), 2, "container should be compound");
		assertEquals(packets.packets().get(0).payloadType(), PayloadType.RR, "container have valid order");
		assertEquals(packets.packets().get(1).payloadType(), PayloadType.BYE, "container have valid order");
		
		assertEquals(packets.asByteArray(), data, "packet not reassembled correctly.");
	}

	
	
	
	
	
	public void testCanCreatePacketsContainerFromBuilder()
	{
		RTCPPackets p = RTCPPackets.builder()
			.withPacket( ReceiverReportRTCPPacket.builder().withSsrc(20).build() )
			.build();
		
		assertEquals(p.packets().size(), 1, "incorrect packet size");
		assertEquals(p.packets().get(0).payloadType(), PayloadType.RR, "incorrect packet set");
		assertEquals(p.isCompund(), false, "incorrect packet container");
		assertEquals(p.lengthAsPacket(), 8, "incorrect packet length");

		p = RTCPPackets.builder()
				.withPacket( ReceiverReportRTCPPacket.builder().withSsrc(20).build() )
				.withPacket( ReceiverReportRTCPPacket.builder().withSsrc(20).build() )
				.build();
			
		assertEquals(p.packets().size(), 2, "incorrect packet size");
		assertEquals(p.packets().get(0).payloadType(), PayloadType.RR, "incorrect packet set");
		assertEquals(p.packets().get(1).payloadType(), PayloadType.RR, "incorrect packet set");
		assertEquals(p.isCompund(), true, "incorrect packet container");
		assertEquals(p.lengthAsPacket(), 16, "incorrect packet length");

		p = RTCPPackets.builder()
				.withPackets( ReceiverReportRTCPPacket.builder().withSsrc(20).build(),
					          ReceiverReportRTCPPacket.builder().withSsrc(20).build(), 
							  ReceiverReportRTCPPacket.builder().withSsrc(20).build() )
				.build();
			
		assertEquals(p.packets().size(), 3, "incorrect packet size");
		assertEquals(p.packets().get(0).payloadType(), PayloadType.RR, "incorrect packet set");
		assertEquals(p.packets().get(1).payloadType(), PayloadType.RR, "incorrect packet set");
		assertEquals(p.packets().get(2).payloadType(), PayloadType.RR, "incorrect packet set");
		assertEquals(p.isCompund(), true, "incorrect packet container");
		assertEquals(p.lengthAsPacket(), 24, "incorrect packet length");

		p = RTCPPackets.builder()
				.withPackets(  Arrays.asList(ReceiverReportRTCPPacket.builder().withSsrc(20).build(),
								             ReceiverReportRTCPPacket.builder().withSsrc(20).build() ))
				.build();
			
		assertEquals(p.packets().size(), 2, "incorrect packet size");
		assertEquals(p.packets().get(0).payloadType(), PayloadType.RR, "incorrect packet set");
		assertEquals(p.packets().get(1).payloadType(), PayloadType.RR, "incorrect packet set");
		assertEquals(p.isCompund(), true, "incorrect packet container");
		assertEquals(p.lengthAsPacket(), 16, "incorrect packet length");
	}

	
	public void testCorrectlyValidatesPacketsWhenUsingBuilder()
	{
		try
		{
			RTCPPackets.builder().withPacket( null ).build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "container must have at least one packet.");
		}	
		try
		{
			RTCPPackets.builder().withPackets( (RTCPPacket[])null ).build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "container must have at least one packet.");
		}	
		try
		{
			RTCPPackets.builder().withPackets( (List<RTCPPacket<?>>)null ).build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "container must have at least one packet.");
		}	
		
		try
		{
			RTCPPackets.builder()
					.withPacket( ByeRTCPPacket.builder().build() )
					.withPacket( ReceiverReportRTCPPacket.builder().withSsrc(20).build() )
					.build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "This looks like a compound packet, but first entry is NOT SR or RR.");
		}
		
	}
	
	
	public void testCanBuildRTCPPacketsFromDatagramPacketCorrectly()
	{
		byte[] data = { (byte)0x80, (byte)0xC9, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00,
						(byte)0x80, (byte)0xC9, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00 };

		DatagramPacket dp = new DatagramPacket(data, data.length);
		RTCPPackets packets = RTCPPackets.fromDatagramPacket( dp );
		
		assertEquals(packets.lengthAsPacket(), 16, "incorrect sizing");
		assertEquals(packets.isCompund(), true, "incorrect sizing");
		assertTrue(packets.packets() != null, "packets should be valid");
		assertEquals(packets.packets().size(), 2, "container should be compound");
		assertEquals(packets.packets().get(0).payloadType(), PayloadType.RR, "container have valid order");
		assertEquals(packets.packets().get(1).payloadType(), PayloadType.RR, "container have valid order");
		
		assertEquals(packets.asByteArray(), data, "packet not reassembled correctly.");
	}
	
	
	public void testCanBuildDatagramPacketFromObjectCorrectly() throws UnknownHostException
	{
		byte[] data = { (byte)0x80, (byte)0xC9, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00,
						(byte)0x80, (byte)0xC9, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00 };
		
		final RTCPPackets p = RTCPPackets.fromByteArray(data);

		DatagramPacket d = p.asDatagramPacket(InetAddress.getByName("192.168.1.1"), 25000);
		assertEquals(d.getAddress().getHostAddress(), "192.168.1.1", "incorrect inet address");
		assertEquals(d.getPort(), 25000, "incorrect port");
		assertEquals(d.getLength(), data.length, "incorrect length");
		assertEquals(d.getData(), data, "incorrect data");

		RTCPPackets packets = RTCPPackets.fromDatagramPacket( d );
		assertEquals(packets.asByteArray(), data, "packet not reassembled correctly.");
	}
	
}
