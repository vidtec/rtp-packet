package org.vidtec.rfc3550.rtcp;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;
import org.vidtec.rfc3550.rtcp.types.RTCPPacket;
import org.vidtec.rfc3550.rtcp.types.RTCPPacket.PayloadType;
import org.vidtec.rfc3550.rtcp.types.app.AppRTCPPacket;
import org.vidtec.rfc3550.rtcp.types.bye.ByeRTCPPacket;
import org.vidtec.rfc3550.rtcp.types.report.ReceiverReportRTCPPacket;
import org.vidtec.rfc3550.rtcp.types.report.SenderReportRTCPPacket;

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
		
		CountingVisitor v = new CountingVisitor();
		packets.visit(v);

		assertEquals(v.total, 1, "visitor not correct");
		assertEquals(v.sr, 0, "visitor not correct");
		assertEquals(v.rr, 1, "visitor not correct");
		assertEquals(v.sdes, 0, "visitor not correct");
		assertEquals(v.app, 0, "visitor not correct");
		assertEquals(v.bye, 0, "visitor not correct");
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

		CountingVisitor v = new CountingVisitor();
		packets.visit(v);

		assertEquals(v.total, 2, "visitor not correct");
		assertEquals(v.sr, 0, "visitor not correct");
		assertEquals(v.rr, 1, "visitor not correct");
		assertEquals(v.sdes, 0, "visitor not correct");
		assertEquals(v.app, 0, "visitor not correct");
		assertEquals(v.bye, 1, "visitor not correct");
		
		byte[] data2 = { (byte)0x80, (byte)0xC8, 0x00, 0x1C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			  	   	    (byte)0x80, (byte)0xCB, 0x00, 0x04 };
		
		packets = RTCPPackets.fromByteArray( data2 );
		
		assertEquals(packets.lengthAsPacket(), 32, "incorrect sizing");
		assertEquals(packets.isCompund(), true, "incorrect sizing");
		assertTrue(packets.packets() != null, "packets should be valid");
		assertEquals(packets.packets().size(), 2, "container should be compound");
		assertEquals(packets.packets().get(0).payloadType(), PayloadType.SR, "container have valid order");
		assertEquals(packets.packets().get(1).payloadType(), PayloadType.BYE, "container have valid order");
		
		assertEquals(packets.asByteArray(), data2, "packet not reassembled correctly.");
		
		v = new CountingVisitor();
		packets.visit(v);
		
		assertEquals(v.total, 2, "visitor not correct");
		assertEquals(v.sr, 1, "visitor not correct");
		assertEquals(v.rr, 0, "visitor not correct");
		assertEquals(v.sdes, 0, "visitor not correct");
		assertEquals(v.app, 0, "visitor not correct");
		assertEquals(v.bye, 1, "visitor not correct");
		
		byte[] data3 = { (byte)0x80, (byte)0xC8, 0x00, 0x1C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
						 (byte)0x80, (byte)0xCC, 0x00, 0x0C, 0x00, 0x00, 0x00, 0x00, 0x20, 0x20, 0x20, 0x20 };
		
		packets = RTCPPackets.fromByteArray( data3 );
		
		assertEquals(packets.lengthAsPacket(), 40, "incorrect sizing");
		assertEquals(packets.isCompund(), true, "incorrect sizing");
		assertTrue(packets.packets() != null, "packets should be valid");
		assertEquals(packets.packets().size(), 2, "container should be compound");
		assertEquals(packets.packets().get(0).payloadType(), PayloadType.SR, "container have valid order");
		assertEquals(packets.packets().get(1).payloadType(), PayloadType.APP, "container have valid order");
		
		assertEquals(packets.asByteArray(), data3, "packet not reassembled correctly.");
		
		v = new CountingVisitor();
		packets.visit(v);
		
		assertEquals(v.total, 2, "visitor not correct");
		assertEquals(v.sr, 1, "visitor not correct");
		assertEquals(v.rr, 0, "visitor not correct");
		assertEquals(v.sdes, 0, "visitor not correct");
		assertEquals(v.app, 1, "visitor not correct");
		assertEquals(v.bye, 0, "visitor not correct");
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

		CountingVisitor v = new CountingVisitor();
		p.visit(v);

		assertEquals(v.total, 1, "visitor not correct");
		assertEquals(v.sr, 0, "visitor not correct");
		assertEquals(v.rr, 1, "visitor not correct");
		assertEquals(v.sdes, 0, "visitor not correct");
		assertEquals(v.app, 0, "visitor not correct");
		assertEquals(v.bye, 0, "visitor not correct");

		p = RTCPPackets.builder()
				.withPacket( ReceiverReportRTCPPacket.builder().withSsrc(20).build() )
				.withPacket( ReceiverReportRTCPPacket.builder().withSsrc(20).build() )
				.build();
			
		assertEquals(p.packets().size(), 2, "incorrect packet size");
		assertEquals(p.packets().get(0).payloadType(), PayloadType.RR, "incorrect packet set");
		assertEquals(p.packets().get(1).payloadType(), PayloadType.RR, "incorrect packet set");
		assertEquals(p.isCompund(), true, "incorrect packet container");
		assertEquals(p.lengthAsPacket(), 16, "incorrect packet length");

		v = new CountingVisitor();
		p.visit(v);

		assertEquals(v.total, 2, "visitor not correct");
		assertEquals(v.sr, 0, "visitor not correct");
		assertEquals(v.rr, 2, "visitor not correct");
		assertEquals(v.sdes, 0, "visitor not correct");
		assertEquals(v.app, 0, "visitor not correct");
		assertEquals(v.bye, 0, "visitor not correct");

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

		v = new CountingVisitor();
		p.visit(v);

		assertEquals(v.total, 3, "visitor not correct");
		assertEquals(v.sr, 0, "visitor not correct");
		assertEquals(v.rr, 3, "visitor not correct");
		assertEquals(v.sdes, 0, "visitor not correct");
		assertEquals(v.app, 0, "visitor not correct");
		assertEquals(v.bye, 0, "visitor not correct");

		p = RTCPPackets.builder()
				.withPackets(  Arrays.asList(ReceiverReportRTCPPacket.builder().withSsrc(20).build(),
								             ReceiverReportRTCPPacket.builder().withSsrc(20).build() ))
				.build();
			
		assertEquals(p.packets().size(), 2, "incorrect packet size");
		assertEquals(p.packets().get(0).payloadType(), PayloadType.RR, "incorrect packet set");
		assertEquals(p.packets().get(1).payloadType(), PayloadType.RR, "incorrect packet set");
		assertEquals(p.isCompund(), true, "incorrect packet container");
		assertEquals(p.lengthAsPacket(), 16, "incorrect packet length");

		v = new CountingVisitor();
		p.visit(v);

		assertEquals(v.total, 2, "visitor not correct");
		assertEquals(v.sr, 0, "visitor not correct");
		assertEquals(v.rr, 2, "visitor not correct");
		assertEquals(v.sdes, 0, "visitor not correct");
		assertEquals(v.app, 0, "visitor not correct");
		assertEquals(v.bye, 0, "visitor not correct");
		

		p = RTCPPackets.builder()
				.withPackets( SenderReportRTCPPacket.builder().withSsrc(20).build(),
					          ReceiverReportRTCPPacket.builder().withSsrc(20).build(), 

					          // SDES builder
					          
					          AppRTCPPacket.builder().withAppFields(0, "0000").withSsrc(20).build(),
					          ByeRTCPPacket.builder().build() )
				.build();
			
		assertEquals(p.packets().size(), 4, "incorrect packet size");
		assertEquals(p.packets().get(0).payloadType(), PayloadType.SR, "incorrect packet set");
		assertEquals(p.packets().get(1).payloadType(), PayloadType.RR, "incorrect packet set");
		assertEquals(p.packets().get(2).payloadType(), PayloadType.APP, "incorrect packet set");
		assertEquals(p.packets().get(3).payloadType(), PayloadType.BYE, "incorrect packet set");
		assertEquals(p.isCompund(), true, "incorrect packet container");
		assertEquals(p.lengthAsPacket(), 52, "incorrect packet length");

		v = new CountingVisitor();
		p.visit(v);

		assertEquals(v.total, 4, "visitor not correct");
		assertEquals(v.sr, 1, "visitor not correct");
		assertEquals(v.rr, 1, "visitor not correct");
		assertEquals(v.sdes, 0, "visitor not correct");
		assertEquals(v.app, 1, "visitor not correct");
		assertEquals(v.bye, 1, "visitor not correct");
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
		try
		{
			RTCPPackets.builder()
					.withPacket( ByeRTCPPacket.builder().build() )
					.withPacket( SenderReportRTCPPacket.builder().withSsrc(20).build() )
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

		CountingVisitor v = new CountingVisitor();
		packets.visit(v);

		assertEquals(v.total, 2, "visitor not correct");
		assertEquals(v.sr, 0, "visitor not correct");
		assertEquals(v.rr, 2, "visitor not correct");
		assertEquals(v.sdes, 0, "visitor not correct");
		assertEquals(v.app, 0, "visitor not correct");
		assertEquals(v.bye, 0, "visitor not correct");
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

		CountingVisitor v = new CountingVisitor();
		packets.visit(v);

		assertEquals(v.total, 2, "visitor not correct");
		assertEquals(v.sr, 0, "visitor not correct");
		assertEquals(v.rr, 2, "visitor not correct");
		assertEquals(v.sdes, 0, "visitor not correct");
		assertEquals(v.app, 0, "visitor not correct");
		assertEquals(v.bye, 0, "visitor not correct");
	}
	
}
