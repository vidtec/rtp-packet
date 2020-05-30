package org.vidtec.rfc3550.rtcp.types.sdes;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;
import org.vidtec.rfc3550.rtcp.types.sdes.SdesItem.ItemType;

@Test
public class SdesItemsTest 
{
	
	public void testCanCorrectlyDecodePointlessTerminatorOnly()
	{
		final byte[] data = { 0x00, 0x00, 0x00, 0x00 };
		final List<SdesItem> items = SdesItems.fromByteBuffer(ByteBuffer.wrap(data));
		
		assertTrue(items != null, "wrong length");
		assertTrue(items.isEmpty(), "wrong length");
		assertEquals(items.size(), 0, "wrong length");
	}

	
	public void testCanCorrectlyDecodeSingleEntryAndTerminatorOn4ByteBOundary()
	{
		final byte[] data = { 0x01, 0x02, 0x30, 0x31, 0x00, 0x00, 0x00, 0x00 };
		final List<SdesItem> items = SdesItems.fromByteBuffer(ByteBuffer.wrap(data));
		
		assertTrue(items != null, "wrong length");
		assertTrue(!items.isEmpty(), "wrong length");
		assertEquals(items.size(), 1, "wrong length");
		assertEquals(items.get(0).itemType(), ItemType.CNAME, "wrong data");
		assertEquals(items.get(0).value(), "01", "wrong data");
	}

	
	public void testCanCorrectlyDecodeSingleEntryAndTerminatorWithPaddingNeeded()
	{
		final byte[] data = { 0x01, 0x03, 0x30, 0x31, 0x32, 0x00, 0x00, 0x00 };
		final List<SdesItem> items = SdesItems.fromByteBuffer(ByteBuffer.wrap(data));
		
		assertTrue(items != null, "wrong length");
		assertTrue(!items.isEmpty(), "wrong length");
		assertEquals(items.size(), 1, "wrong length");
		assertEquals(items.get(0).itemType(), ItemType.CNAME, "wrong data");
		assertEquals(items.get(0).value(), "012", "wrong data");
	}

	
	public void testCanCorrectlyDecodeMultipleEntriesAndTerminatorWithPaddingNeeded()
	{
		final byte[] data = { 0x01, 0x03, 0x30, 0x31, 0x32, 0x02, 0x02, 0x30, 0x31, 0x00, 0x00, 0x00 };
		final List<SdesItem> items = SdesItems.fromByteBuffer(ByteBuffer.wrap(data));
		
		assertTrue(items != null, "wrong length");
		assertTrue(!items.isEmpty(), "wrong length");
		assertEquals(items.size(), 2, "wrong length");
		assertEquals(items.get(0).itemType(), ItemType.CNAME, "wrong data");
		assertEquals(items.get(0).value(), "012", "wrong data");
		assertEquals(items.get(1).itemType(), ItemType.NAME, "wrong data");
		assertEquals(items.get(1).value(), "01", "wrong data");
	}
	
	
	public void testCorrectlyValidatesNonAlignedItems()
	{
		try
		{
			final byte[] data = { 0x01, 0x03, 0x30, 0x31, 0x32, 0x02, 0x02, 0x30, 0x31, 0x00, 0x00 };
			SdesItems.fromByteBuffer(ByteBuffer.wrap(data));
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "not enough padding bytes, expected 3 but found 2", "wrong validation message");
		}
		try
		{
			final byte[] data = { 0x01, 0x03, 0x30, 0x31, 0x32, 0x02, 0x02, 0x30, 0x31, 0x00, 0x01, 0x00 };
			SdesItems.fromByteBuffer(ByteBuffer.wrap(data));
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected to read all nulls in terminator.", "wrong validation message");
		}
	}
	
	
	
	public void testCanCorrectlyEncodePointlessTerminatorOnly()
	{
		final byte[] data = { 0x00, 0x00, 0x00, 0x00 };
		
		assertEquals(SdesItems.toByteArray(new ArrayList<>()), data, "incorrect assembly");
		assertEquals(SdesItems.byteLength(new ArrayList<>()), 4, "incorrect length calculation");
	}

	
	public void testCanCorrectlyEncodeSingleEntryAndTerminatorWithPaddingNeeded()
	{
		final byte[] data = { 0x01, 0x03, 0x30, 0x31, 0x32, 0x00, 0x00, 0x00 };

		assertEquals(SdesItems.toByteArray( Arrays.asList(SdesItem.cname("012")) ), data, "incorrect assembly");
		assertEquals(SdesItems.byteLength( Arrays.asList(SdesItem.cname("012")) ), 8, "incorrect length calculation");
	}
	
	
	public void testCanCorrectlyEncodeSingleEntryAndTerminatorOn4ByteBOundary()
	{
		final byte[] data = { 0x01, 0x02, 0x30, 0x31, 0x00, 0x00, 0x00, 0x00 };
		
		assertEquals(SdesItems.toByteArray( Arrays.asList(SdesItem.cname("01")) ), data, "incorrect assembly");
		assertEquals(SdesItems.byteLength( Arrays.asList(SdesItem.cname("01")) ), 8, "incorrect length calculation");
	}
	
	
	public void testCanCorrectlyEncodeMultipleEntriesAndTerminatorWithPaddingNeeded()
	{
		final byte[] data = { 0x01, 0x03, 0x30, 0x31, 0x32, 0x02, 0x02, 0x30, 0x31, 0x00, 0x00, 0x00 };

		assertEquals(SdesItems.toByteArray( Arrays.asList(SdesItem.cname("012"), SdesItem.name("01")) ), data, "incorrect assembly");
		assertEquals(SdesItems.byteLength( Arrays.asList(SdesItem.cname("012"), SdesItem.name("01")) ), 12, "incorrect length calculation");
	}
	
	
	public void testCorrectlyValidatesOnWrite()
	{
		try
		{
			SdesItems.toByteArray(null);
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "items cannot be null", "wrong validation message");
		}
		try
		{
			SdesItems.byteLength(null);
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "items cannot be null", "wrong validation message");
		}
	}
	
}
