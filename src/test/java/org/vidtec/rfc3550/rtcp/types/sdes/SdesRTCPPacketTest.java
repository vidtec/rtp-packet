package org.vidtec.rfc3550.rtcp.types.sdes;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

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
		final byte[] data = { (byte)0x80, (byte)0xCA, 0x00, 0x00 };

		SdesRTCPPacket r = SdesRTCPPacket.builder()
				.build();
		
		assertEquals(r.packetLength(), 4, "incorrect packet length");
		assertTrue(r.is(PayloadType.SDES), "incorrect payload type");
		assertTrue(!r.is(PayloadType.APP), "incorrect payload type");
		assertTrue(!r.is(null), "incorrect payload type");
		assertEquals(r.payloadType(), PayloadType.SDES, "incorrect payload type");

		assertTrue(r.chunks() != null, "incorrect chunks");
		assertTrue(r.chunks().isEmpty(), "incorrect chunks");
		assertTrue(!r.hasChunks(), "incorrect chunks");
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
		assertTrue(!r.hasChunks(), "incorrect chunks");
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
		assertTrue(!r.hasChunks(), "incorrect chunks");
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
	
	
	public void testCanCreateSingleChunkPacketFromBuilder()
	{
		final byte[] data = { (byte)0x81, (byte)0xCA, 0x00, 0x03, 
				                    0x00, 0x00, 0x00, 0x14, 
				                    0x01, 0x03, 0x30, 0x31, 
				                    0x32, 0x00, 0x00, 0x00};

		SdesRTCPPacket r = SdesRTCPPacket.builder()
				.withChunks(
					Chunk.builder()
						.withSsrc(20)
						.withItems(SdesItem.cname("012")).build()
				)
				.build();
		
		assertEquals(r.packetLength(), 16, "incorrect packet length");
		assertTrue(r.is(PayloadType.SDES), "incorrect payload type");
		assertTrue(!r.is(PayloadType.APP), "incorrect payload type");
		assertTrue(!r.is(null), "incorrect payload type");
		assertEquals(r.payloadType(), PayloadType.SDES, "incorrect payload type");

		assertTrue(r.chunks() != null, "incorrect chunks");
		assertTrue(!r.chunks().isEmpty(), "incorrect chunks");
		assertTrue(r.hasChunks(), "incorrect chunks");
		assertEquals(r.chunkCount(), 1, "incorrect chunks");
		assertEquals(r.chunks().get(0).ssrcIdentifier(), 20, "wrong creation");
		assertEquals(r.chunks().get(0).hasItems(), true, "wrong creation");
		
		assertEquals(r.asByteArray(), data, "incorrect reassembly");
	}
	
	
	public void testValidatesBuilderCorrectly()
	{
		try
		{
			Chunk c = Chunk.builder().withSsrc(20).build();
			SdesRTCPPacket.builder()
					.withChunks(c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, 
								c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c
					)
					.build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "maximum report chunk size exceeded, expected at most 31, but was 32", "wrong validation message");
		}
	}
	
	
	public void testValidatesFromByteArrayCorrectly()
	{
		try
		{
			SdesRTCPPacket.fromByteArray( null );
			fail("Expected error");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "packet data cannot be null", "wrong validation message");
		}
		try
		{
			SdesRTCPPacket.fromByteArray(new byte[] { (byte)0x80 } );
			fail("Expected error");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Packet too short, expecting at least 4 bytes, but found 1", "wrong validation message");
		}
		try
		{
			SdesRTCPPacket.fromByteArray(new byte[] { (byte)0x82, (byte)0xCA, 0x00, 0x00, 
																0x00, 0x00, 0x00, 0x00 } );
			fail("Expected error");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Packet states 2 chunks, so expecting length of at least 20, but only found 8 bytes.", "wrong validation message");
		}
		try
		{
			SdesRTCPPacket.fromByteArray(new byte[] { (byte)0x80, (byte)0xD9, 0x00, 0x00, 
																0x00, 0x00, 0x00, 0x00 } );
			fail("Expected error");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Invalid or unexpected packet type - should be 202", "wrong validation message");
		}
		try
		{
			SdesRTCPPacket.fromByteArray(new byte[] { (byte)0x80, (byte)0xCA, 0x00, 0x03, 
																0x00, 0x00, 0x00, 0x00 } );
			fail("Expected error");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Packet states 16 bytes length, but actual length is 8", "wrong validation message");
		}
		try
		{
			SdesRTCPPacket.fromByteArray(new byte[] { (byte)0xA0, (byte)0xC9, 0x00, 0x08, 
																0x00, 0x00, 0x00, 0x00 } );
			fail("Expected error");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "SDES packet should never be padded, malformed packet found", "wrong validation message");
		}

	}
	
	
	public void testCanCreateEmptySdesPacketFromBytes()
	{
		final byte[] data = { (byte)0x80, (byte)0xCA, 0x00, 0x00 };

		SdesRTCPPacket r = SdesRTCPPacket.fromByteArray(data);
		
		assertEquals(r.packetLength(), 4, "incorrect packet length");
		assertTrue(r.is(PayloadType.SDES), "incorrect payload type");
		assertTrue(!r.is(PayloadType.APP), "incorrect payload type");
		assertTrue(!r.is(null), "incorrect payload type");
		assertEquals(r.payloadType(), PayloadType.SDES, "incorrect payload type");

		assertTrue(r.chunks() != null, "incorrect chunks");
		assertTrue(r.chunks().isEmpty(), "incorrect chunks");
		assertTrue(!r.hasChunks(), "incorrect chunks");
		assertEquals(r.chunkCount(), 0, "incorrect chunks");
		assertEquals(r.asByteArray(), data, "incorrect reassembly");

		r = SdesRTCPPacket.fromByteArray(data);
		
		assertEquals(r.packetLength(), 4, "incorrect packet length");
		assertTrue(r.is(PayloadType.SDES), "incorrect payload type");
		assertTrue(!r.is(PayloadType.APP), "incorrect payload type");
		assertTrue(!r.is(null), "incorrect payload type");
		assertEquals(r.payloadType(), PayloadType.SDES, "incorrect payload type");

		assertTrue(r.chunks() != null, "incorrect chunks");
		assertTrue(r.chunks().isEmpty(), "incorrect chunks");
		assertTrue(!r.hasChunks(), "incorrect chunks");
		assertEquals(r.chunkCount(), 0, "incorrect chunks");
		assertEquals(r.asByteArray(), data, "incorrect reassembly");
		
		r = SdesRTCPPacket.fromByteArray(data);
		
		assertEquals(r.packetLength(), 4, "incorrect packet length");
		assertTrue(r.is(PayloadType.SDES), "incorrect payload type");
		assertTrue(!r.is(PayloadType.APP), "incorrect payload type");
		assertTrue(!r.is(null), "incorrect payload type");
		assertEquals(r.payloadType(), PayloadType.SDES, "incorrect payload type");

		assertTrue(r.chunks() != null, "incorrect chunks");
		assertTrue(r.chunks().isEmpty(), "incorrect chunks");
		assertTrue(!r.hasChunks(), "incorrect chunks");
		assertEquals(r.chunkCount(), 0, "incorrect chunks");
		assertEquals(r.asByteArray(), data, "incorrect reassembly");
		
		r = SdesRTCPPacket.fromByteArray(data);
		
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
	
	
	public void testCanCreateSingleChunkPacketFromBytes()
	{
		final byte[] data = { (byte)0x81, (byte)0xCA, 0x00, 0x03, 
				                    0x00, 0x00, 0x00, 0x14, 
				                    0x01, 0x03, 0x30, 0x31, 
				                    0x32, 0x00, 0x00, 0x00};

		SdesRTCPPacket r = SdesRTCPPacket.fromByteArray(data);
		
		assertEquals(r.packetLength(), 16, "incorrect packet length");
		assertTrue(r.is(PayloadType.SDES), "incorrect payload type");
		assertTrue(!r.is(PayloadType.APP), "incorrect payload type");
		assertTrue(!r.is(null), "incorrect payload type");
		assertEquals(r.payloadType(), PayloadType.SDES, "incorrect payload type");

		assertTrue(r.chunks() != null, "incorrect chunks");
		assertTrue(!r.chunks().isEmpty(), "incorrect chunks");
		assertTrue(r.hasChunks(), "incorrect chunks");
		assertEquals(r.chunkCount(), 1, "incorrect chunks");
		assertEquals(r.chunks().get(0).ssrcIdentifier(), 20, "wrong creation");
		assertEquals(r.chunks().get(0).hasItems(), true, "wrong creation");
		
		assertEquals(r.asByteArray(), data, "incorrect reassembly");
	}
	
	
	

}
