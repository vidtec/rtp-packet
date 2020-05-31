package org.vidtec.rfc3550.rtcp.types.sdes;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;
import org.vidtec.rfc3550.rtcp.types.RTCPPacket.PayloadType;

@Test
public class SdesRTCPPacketTest 
{
	
	public void testCanCastSelfToConcreteType()
	{
		final SdesRTCPPacket r = SdesRTCPPacket.builder()
				.build();
		
		final SdesRTCPPacket p = r.asConcreteType();
		assertEquals(p.packetLength(), 4, "incorrect packet length");
	}


	public void testCanCreateEmptySdesPacketFromBuilder()
	{
		final byte[] data = { (byte)0x80, (byte)0xCA, 0x00, 0x04 };

		SdesRTCPPacket r = SdesRTCPPacket.builder()
				.build();
		
		assertEquals(r.packetLength(), 4, "incorrect packet length");
		assertTrue(r.is(PayloadType.SDES), "incorrect payload type");
		assertTrue(!r.is(PayloadType.APP), "incorrect payload type");
		assertTrue(!r.is(null), "incorrect payload type");
		assertEquals(r.payloadType(), PayloadType.SDES, "incorrect payload type");

		assertTrue(r.chunks() != null, "incorrect chunks");
		assertTrue(r.chunks().isEmpty(), "incorrect chunks");
		assertEquals(r.chunkCount(), 0, "incorrect chunks");
		assertEquals(r.asByteArray(), data, "incorrect reassembly");

		r = SdesRTCPPacket.builder()
				.withChunks((Chunk[])null)
				.build();
		
		assertEquals(r.packetLength(), 4, "incorrect packet length");
		assertTrue(r.is(PayloadType.SDES), "incorrect payload type");
		assertTrue(!r.is(PayloadType.APP), "incorrect payload type");
		assertTrue(!r.is(null), "incorrect payload type");
		assertEquals(r.payloadType(), PayloadType.SDES, "incorrect payload type");

		assertTrue(r.chunks() != null, "incorrect chunks");
		assertTrue(r.chunks().isEmpty(), "incorrect chunks");
		assertEquals(r.chunkCount(), 0, "incorrect chunks");
		assertEquals(r.asByteArray(), data, "incorrect reassembly");
		
		r = SdesRTCPPacket.builder()
				.withChunks((List<Chunk>)null)
				.build();
		
		assertEquals(r.packetLength(), 4, "incorrect packet length");
		assertTrue(r.is(PayloadType.SDES), "incorrect payload type");
		assertTrue(!r.is(PayloadType.APP), "incorrect payload type");
		assertTrue(!r.is(null), "incorrect payload type");
		assertEquals(r.payloadType(), PayloadType.SDES, "incorrect payload type");

		assertTrue(r.chunks() != null, "incorrect chunks");
		assertTrue(r.chunks().isEmpty(), "incorrect chunks");
		assertEquals(r.chunkCount(), 0, "incorrect chunks");
		assertEquals(r.asByteArray(), data, "incorrect reassembly");
		
		r = SdesRTCPPacket.builder()
				.withChunks(Collections.emptyList())
				.build();
		
		assertEquals(r.packetLength(), 4, "incorrect packet length");
		assertTrue(r.is(PayloadType.SDES), "incorrect payload type");
		assertTrue(!r.is(PayloadType.APP), "incorrect payload type");
		assertTrue(!r.is(null), "incorrect payload type");
		assertEquals(r.payloadType(), PayloadType.SDES, "incorrect payload type");

		assertTrue(r.chunks() != null, "incorrect chunks");
		assertTrue(r.chunks().isEmpty(), "incorrect chunks");
		assertEquals(r.chunkCount(), 0, "incorrect chunks");
		assertEquals(r.asByteArray(), data, "incorrect reassembly");
	}
	
	
	public void testCanCreateEmptyAppPacketFromBuilder()
	{
		final byte[] data = { (byte)0x80, (byte)0xCC, 0x00, 0x0C, 0x00, 0x00, 0x00, 0x00, 0x20, 0x20, 0x20, 0x20 };

		SdesRTCPPacket r = SdesRTCPPacket.builder()
				.withChunks(
					Chunk.builder()
						.withSsrc(20)
						.withItems(SdesItem.cname("012")).build()
				)
				.build();
		
//		assertEquals(r.packetLength(), 12, "incorrect packet length");
//		assertTrue(r.is(PayloadType.APP), "incorrect payload type");
//		assertTrue(!r.is(PayloadType.SDES), "incorrect payload type");
//		assertTrue(!r.is(null), "incorrect payload type");
//		assertEquals(r.payloadType(), PayloadType.APP, "incorrect payload type");
//
//		assertTrue(r.name() != null, "incorrect name");
//		assertTrue(!r.name().isEmpty(), "incorrect name");
//
//		assertEquals(r.ssrc(), 0, "incorrect ssrc");
//		assertEquals(r.name(), "    ", "incorrect name");
//		assertEquals(r.subType(), 0, "incorrect subtype");
//		assertEquals(r.data(), new byte[] { }, "incorrect data");
//		
//		assertEquals(r.asByteArray(), data, "packet data not reformed correctly.");
//		
//		r = SdesRTCPPacket.builder()
//				.withAppFields(0, null)
//				.build();
//		
//		assertEquals(r.packetLength(), 12, "incorrect packet length");
//		assertTrue(r.is(PayloadType.APP), "incorrect payload type");
//		assertTrue(!r.is(PayloadType.SDES), "incorrect payload type");
//		assertTrue(!r.is(null), "incorrect payload type");
//		assertEquals(r.payloadType(), PayloadType.APP, "incorrect payload type");
//
//		assertTrue(r.name() != null, "incorrect name");
//		assertTrue(!r.name().isEmpty(), "incorrect name");
//
//		assertEquals(r.ssrc(), 0, "incorrect ssrc");
//		assertEquals(r.name(), "    ", "incorrect name");
//		assertEquals(r.subType(), 0, "incorrect subtype");
//		assertEquals(r.data(), new byte[] { }, "incorrect data");
//		
//		assertEquals(r.asByteArray(), data, "packet data not reformed correctly.");
	}

}
