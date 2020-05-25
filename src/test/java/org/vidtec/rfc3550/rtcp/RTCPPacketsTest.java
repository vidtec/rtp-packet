package org.vidtec.rfc3550.rtcp;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;
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
			byte[] data = { (byte)0x80 };
			RTCPPackets.fromByteArray( data );
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Invalid packet length - too short.", "wrong validation message");
		}
		
		
		try
		{
			byte[] data = { (byte)0x80, (byte)0xCA, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00,
					        (byte)0x80, (byte)0xC9, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00 };
			RTCPPackets.fromByteArray( data );
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "This looks like a compound packet, but first entry is NOT SR or RR.");
		}
		
		
				
		
		

		
	}

	
	public void testCanCreatePacketsContainerFromSinglePacketAsByteArray()
	{
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
		
		
		//todo validate if more than one packet, then first must be SR or RR
		
		
//		if (builder.packets.size() > 1)
//		{
//			final PayloadType pt = builder.packets.get(0).payloadType();
//			if (!PayloadType.SR.equals(pt) && !PayloadType.RR.equals(pt))
//			{
//				throw new IllegalArgumentException("a Compound packet must start with a packet of type SR or RR.");
//			}
//		}
		
		
	}
	
}
