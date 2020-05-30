package org.vidtec.rfc3550.rtcp.types.sdes;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.nio.ByteBuffer;

import org.testng.annotations.Test;
import org.vidtec.rfc3550.rtcp.types.sdes.SdesItem.ItemType;

@Test
public class ChunkTest 
{

	public void testCanBuildChunkFromBuilderWithNoItemsCorrectly()
	{
		final byte[] data = { 0x00, 0x00, 0x00, 0x14,
				              0x00, 0x00, 0x00, 0x00 };
		
		Chunk c = Chunk.builder().withSsrc(20).build();
		assertEquals(c.ssrcIdentifier(), 20, "bad ssrc identifier set");
		assertTrue(c.items().isEmpty(), "bad items list");
		assertTrue(!c.hasItems(), "bad items list");
		assertEquals(c.items().size(), 0, "bad items list");
		assertEquals(c.items(ItemType.CNAME).size(), 0, "bad items list");
		assertEquals(c.asByteArray(), data, "bad reassembly");

		c = Chunk.builder().withSsrc(20).withItems((SdesItem[])null).build();
		assertEquals(c.ssrcIdentifier(), 20, "bad ssrc identifier set");
		assertTrue(c.items().isEmpty(), "bad items list");
		assertTrue(!c.hasItems(), "bad items list");
		assertEquals(c.items().size(), 0, "bad items list");
		assertEquals(c.items(ItemType.CNAME).size(), 0, "bad items list");
		assertEquals(c.asByteArray(), data, "bad reassembly");
	}

	
	public void testCanBuildChunkFromBuilderWithItemsCorrectly()
	{
		final byte[] data = { 0x00, 0x00, 0x00, 0x14,
							  0x01, 0x03, 0x30, 0x31, 0x32, 0x02, 0x02, 0x30, 0x31, 0x00, 0x00, 0x00 };
		
		Chunk c = Chunk.builder()
				       .withSsrc(20)
				       .withItems(SdesItem.cname("012"), SdesItem.name("01"))
				       .build();
		assertEquals(c.ssrcIdentifier(), 20, "bad ssrc identifier set");
		assertTrue(!c.items().isEmpty(), "bad items list");
		assertTrue(c.hasItems(), "bad items list");
		assertEquals(c.items().size(), 2, "bad items list");
		assertEquals(c.items(ItemType.CNAME).size(), 1, "bad items list");
		assertEquals(c.items(ItemType.NAME).size(), 1, "bad items list");
		assertEquals(c.items(ItemType.PRIV).size(), 0, "bad items list");
		assertEquals(c.asByteArray(), data, "bad reassembly");
	}

	
	public void testValidatesBadBuilderDataCorrectly()
	{
		try
		{
			Chunk.builder().build();
			fail("should exception here");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Invalid ssrc value", "wrong validation message");
		}
		try
		{
			Chunk.builder().withSsrc(0xFFFFFFFFFL).build();
			fail("should exception here");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Invalid ssrc value", "wrong validation message");
		}
		try
		{
			Chunk.builder().withSsrc(-2).build();
			fail("should exception here");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Invalid ssrc value", "wrong validation message");
		}
	}
	
	
	public void testCanCorrectlyDecodePointlessTerminatorOnly()
	{
		final byte[] data = { 0x00, 0x00, 0x00, 0x14,
							  0x00, 0x00, 0x00, 0x00 };

		Chunk c = Chunk.fromByteArray(data);
		assertEquals(c.ssrcIdentifier(), 20, "bad ssrc identifier set");
		assertTrue(c.items().isEmpty(), "bad items list");
		assertTrue(!c.hasItems(), "bad items list");
		assertEquals(c.items().size(), 0, "bad items list");
		assertEquals(c.items(ItemType.CNAME).size(), 0, "bad items list");
		assertEquals(c.asByteArray(), data, "bad reassembly");
	}
	
	
	public void testCanBuildChunkFromByteBufferWithItemsCorrectly()
	{
		final byte[] data = { 0x00, 0x00, 0x00, 0x14,
							  0x01, 0x03, 0x30, 0x31, 0x32, 0x02, 0x02, 0x30, 0x31, 0x00, 0x00, 0x00 };
		
		Chunk c = Chunk.fromByteBuffer(ByteBuffer.wrap(data));
		
		assertEquals(c.ssrcIdentifier(), 20, "bad ssrc identifier set");
		assertTrue(!c.items().isEmpty(), "bad items list");
		assertTrue(c.hasItems(), "bad items list");
		assertEquals(c.items().size(), 2, "bad items list");
		assertEquals(c.items(ItemType.CNAME).size(), 1, "bad items list");
		assertEquals(c.items(ItemType.NAME).size(), 1, "bad items list");
		assertEquals(c.items(ItemType.PRIV).size(), 0, "bad items list");
		assertEquals(c.asByteArray(), data, "bad reassembly");
	}
	
	
	public void testValidatesBadInputInFromBytePartsCorrectly()
	{
		try
		{
			Chunk.fromByteArray(null);
			fail("should exception here");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "data cannot be null", "wrong validation message");
		}
		try
		{
			Chunk.fromByteBuffer(null);
			fail("should exception here");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "data cannot be null", "wrong validation message");
		}	
		try
		{
			Chunk.fromByteArray( new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00 } );
			fail("should exception here");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "chunk was wrong size, expecting at least 8 bytes, but found 5", "wrong validation message");
		}
		try
		{
			final byte[] data = {   0x00, 0x00, 0x00, 0x14,
									0x01, 0x03, 0x30, 0x31, 0x32, 0x02, 0x02, 0x30, 0x31, 0x00, 0x00 };
			Chunk.fromByteBuffer(ByteBuffer.wrap(data));
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "not enough padding bytes, expected 3 but found 2", "wrong validation message");
		}
	}
	
}

