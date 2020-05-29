package org.vidtec.rfc3550.rtcp.types.sdes;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.nio.ByteBuffer;

import org.testng.annotations.Test;
import org.vidtec.rfc3550.rtcp.types.sdes.SdesItem.ItemType;

@Test
public class SdesItemTest 
{

	public void testCanCorrectlyExtractSdesItemTypeFromValidData()
	{
		// valid CNAME item
		final byte[] data = { (byte)0x01, (byte)0x03, 0x30, 0x30, 0x30 };
		final ByteBuffer b = ByteBuffer.wrap(data);

		assertEquals(b.position(), 0, "incorrect start position.");
		assertEquals(b.remaining(), 5, "incorrect remaining.");
		assertEquals(b.limit(), 5, "incorrect limit.");
		
		assertEquals(SdesItem.peekItemType(b), ItemType.CNAME, "incorrect item type");

		assertEquals(b.position(), 0, "incorrect start position after peek.");
		assertEquals(b.remaining(), 5, "incorrect remaining after peek.");
		assertEquals(b.limit(), 5, "incorrect limit after peek.");
	}
	
	
	public void testCorrectlyRejectsBadDataDuringSdesItemTypePeek()
	{
		ByteBuffer b = null;
		try
		{
			b = ByteBuffer.wrap(new byte[] { } );
			
			assertEquals(b.position(), 0, "incorrect start position.");
			assertEquals(b.remaining(), 0, "incorrect remaining.");
			assertEquals(b.limit(), 0, "incorrect limit.");

			SdesItem.peekItemType( b );
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Invalid item length - too short to peek type.", "wrong validation message");
			
			assertEquals(b.position(), 0, "incorrect start position after peek.");
			assertEquals(b.remaining(), 0, "incorrect remaining after peek.");
			assertEquals(b.limit(), 0, "incorrect limit after peek.");
		}
		try
		{
			b = ByteBuffer.wrap(new byte[] { (byte)0x0A, (byte)0x03, 0x30, 0x30, 0x30 } );
			
			assertEquals(b.position(), 0, "incorrect start position.");
			assertEquals(b.remaining(), 5, "incorrect remaining.");
			assertEquals(b.limit(), 5, "incorrect limit.");

			SdesItem.peekItemType( b );
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Unknown type - 10", "wrong validation message");
			
			assertEquals(b.position(), 0, "incorrect start position after peek.");
			assertEquals(b.remaining(), 5, "incorrect remaining after peek.");
			assertEquals(b.limit(), 5, "incorrect limit after peek.");
		}
	}
	
	
	public void testCanCorrectlyExtractLengthFromValidData()
	{
		// valid RR RTCP item
		final byte[] data = { (byte)0x01, (byte)0xFF, 0x00 };
		final ByteBuffer b = ByteBuffer.wrap(data);

		assertEquals(b.position(), 0, "incorrect start position.");
		assertEquals(b.remaining(), 3, "incorrect remaining.");
		assertEquals(b.limit(), 3, "incorrect limit.");
		
		assertEquals(SdesItem.peekStatedLength(b), 0xFF, "incorrect item length");

		assertEquals(b.position(), 0, "incorrect start position after peek.");
		assertEquals(b.remaining(), 3, "incorrect remaining after peek.");
		assertEquals(b.limit(), 3, "incorrect limit after peek.");

	}
	
	
	public void testCorrectlyRejectsBadDataDuringLengthPeek()
	{
		ByteBuffer b = null;
		try
		{
			b = ByteBuffer.wrap(new byte[] { (byte)0x01 } );
			
			assertEquals(b.position(), 0, "incorrect start position.");
			assertEquals(b.remaining(), 1, "incorrect remaining.");
			assertEquals(b.limit(), 1, "incorrect limit.");

			SdesItem.peekStatedLength( b );
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Invalid item length - too short to peek stated length.", "wrong validation message");

			assertEquals(b.position(), 0, "incorrect start position after peek.");
			assertEquals(b.remaining(), 1, "incorrect remaining after peek.");
			assertEquals(b.limit(), 1, "incorrect limit after peek.");
		}
	}
	
	
	public void testCorrectlyBuildsPacketsForAllTypes()
	{
		SdesItem i = SdesItem.cname("012");
		assertTrue(i.is(ItemType.CNAME), "wrong type");
		assertEquals(i.value(), "012", "wrong value");
		assertTrue(!i.isPrivate(), "wrong type");
		assertEquals(i.prefix(), "", "wrong prefix");
		assertEquals(i.asByteArray(), new byte[] { 0x01, 0x03, 0x30, 0x31, 0x32 }, "wrong reassembly");
		assertEquals(i.itemLength(), 5, "wrong length");

		i = SdesItem.name("012");
		assertTrue(i.is(ItemType.NAME), "wrong type");
		assertEquals(i.value(), "012", "wrong value");
		assertTrue(!i.isPrivate(), "wrong type");
		assertEquals(i.prefix(), "", "wrong prefix");
		assertEquals(i.asByteArray(), new byte[] { 0x02, 0x03, 0x30, 0x31, 0x32 }, "wrong reassembly");
		assertEquals(i.itemLength(), 5, "wrong length");

		i = SdesItem.email("012");
		assertTrue(i.is(ItemType.EMAIL), "wrong type");
		assertEquals(i.value(), "012", "wrong value");
		assertTrue(!i.isPrivate(), "wrong type");
		assertEquals(i.prefix(), "", "wrong prefix");
		assertEquals(i.asByteArray(), new byte[] { 0x03, 0x03, 0x30, 0x31, 0x32 }, "wrong reassembly");
		assertEquals(i.itemLength(), 5, "wrong length");

		i = SdesItem.phone("012");
		assertTrue(i.is(ItemType.PHONE), "wrong type");
		assertEquals(i.value(), "012", "wrong value");
		assertTrue(!i.isPrivate(), "wrong type");
		assertEquals(i.prefix(), "", "wrong prefix");
		assertEquals(i.asByteArray(), new byte[] { 0x04, 0x03, 0x30, 0x31, 0x32 }, "wrong reassembly");
		assertEquals(i.itemLength(), 5, "wrong length");

		i = SdesItem.location("012");
		assertTrue(i.is(ItemType.LOC), "wrong type");
		assertEquals(i.value(), "012", "wrong value");
		assertTrue(!i.isPrivate(), "wrong type");
		assertEquals(i.prefix(), "", "wrong prefix");
		assertEquals(i.asByteArray(), new byte[] { 0x05, 0x03, 0x30, 0x31, 0x32 }, "wrong reassembly");
		assertEquals(i.itemLength(), 5, "wrong length");

		i = SdesItem.tool("012");
		assertTrue(i.is(ItemType.TOOL), "wrong type");
		assertEquals(i.value(), "012", "wrong value");
		assertTrue(!i.isPrivate(), "wrong type");
		assertEquals(i.prefix(), "", "wrong prefix");
		assertEquals(i.asByteArray(), new byte[] { 0x06, 0x03, 0x30, 0x31, 0x32 }, "wrong reassembly");
		assertEquals(i.itemLength(), 5, "wrong length");

		i = SdesItem.note("012");
		assertTrue(i.is(ItemType.NOTE), "wrong type");
		assertEquals(i.value(), "012", "wrong value");
		assertTrue(!i.isPrivate(), "wrong type");
		assertEquals(i.prefix(), "", "wrong prefix");
		assertEquals(i.asByteArray(), new byte[] { 0x07, 0x03, 0x30, 0x31, 0x32 }, "wrong reassembly");
		assertEquals(i.itemLength(), 5, "wrong length");

		i = SdesItem.priv("012", "0");
		assertTrue(i.is(ItemType.PRIV), "wrong type");
		assertEquals(i.value(), "012", "wrong value");
		assertTrue(i.isPrivate(), "wrong type");
		assertEquals(i.prefix(), "0", "wrong prefix");
		assertEquals(i.asByteArray(), new byte[] { 0x08, 0x05, 0x01, 0x30, 0x30, 0x31, 0x32 }, "wrong reassembly");
		assertEquals(i.itemLength(), 7, "wrong length");
	}
	
	
	public void testConfirmsTypeCorrectly()
	{
		assertTrue(!SdesItem.cname("bob").is(null), "wrong logic");
		assertTrue(!SdesItem.cname("bob").is(ItemType.EMAIL), "wrong logic");
		assertTrue(SdesItem.cname("bob").is(ItemType.CNAME), "wrong logic");
	}

	
	public void testValidatesBuildersCorrectly()
	{
		try
		{
			SdesItem.cname(null);
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "value cannot be null", "wrong validation message");
		}
		try
		{
			SdesItem.priv("", null);
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "prefix cannot be null", "wrong validation message");
		}
		try
		{
			SdesItem.cname("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
					+ "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
					+ "012345678901234567890123456789012345678901234567890123456789");
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "maximum value length is 255 bytes", "wrong validation message");
		}
		try
		{
			SdesItem.priv("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
					+ "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
					+ "012345678901234567890123456789012345678901234567890123456789",
					
					"012345678");
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "maximum value length is 254 bytes", "wrong validation message");
		}		
		try
		{
			SdesItem.priv("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
					+ "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
					,
					"012345678901234567890123456789012345678901234567890123456789");
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "maximum value and prefix length is 254 bytes", "wrong validation message");
		}	
	}

	
	public void testCanDecodeFormByteArrayCorrectly()
	{
		byte[] data = new byte[] { 0x08, 0x05, 0x01, 0x30, 0x30, 0x31, 0x32 };
		
		SdesItem i = SdesItem.fromByteArray(data);
		
		assertTrue(i.is(ItemType.PRIV), "wrong type");
		assertEquals(i.value(), "012", "wrong value");
		assertTrue(i.isPrivate(), "wrong type");
		assertEquals(i.prefix(), "0", "wrong prefix");
		assertEquals(i.asByteArray(), data, "wrong reassembly");
		assertEquals(i.itemLength(), 7, "wrong length");
	}	
	
	
	public void testCanDecodeFormByteBufferCorrectly()
	{
		byte[] data = new byte[] { 0x08, 0x05, 0x01, 0x30, 0x30, 0x31, 0x32 };
		
		SdesItem i = SdesItem.fromByteBuffer(ByteBuffer.wrap(data));
		
		assertTrue(i.is(ItemType.PRIV), "wrong type");
		assertEquals(i.value(), "012", "wrong value");
		assertTrue(i.isPrivate(), "wrong type");
		assertEquals(i.prefix(), "0", "wrong prefix");
		assertEquals(i.asByteArray(), data, "wrong reassembly");
		assertEquals(i.itemLength(), 7, "wrong length");

		byte[] data2 = new byte[] { 0x01, 0x03, 0x30, 0x31, 0x32 };
		
		i = SdesItem.fromByteBuffer(ByteBuffer.wrap(data2));
		
		assertTrue(i.is(ItemType.CNAME), "wrong type");
		assertEquals(i.value(), "012", "wrong value");
		assertEquals(i.asByteArray(), data2, "wrong reassembly");
		assertEquals(i.itemLength(), 5, "wrong length");
	}
	
	
	public void testValidatesBytesCorrectly()
	{
		try
		{
			SdesItem.fromByteArray(null);
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "data cannot be null", "wrong validation message");
		}
		try
		{
			SdesItem.fromByteBuffer(null);
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "data cannot be null", "wrong validation message");
		}
		try
		{
			SdesItem.fromByteBuffer(ByteBuffer.wrap(new byte[] { 0x01 }));
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "item was wrong size, expecting at least 2 bytes, but found 1", "wrong validation message");
		}
		try
		{
			SdesItem.fromByteBuffer(ByteBuffer.wrap(new byte[] { 0x21, 0x03, 0x30, 0x31, 0x32 }));
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Unknown type - 33", "wrong validation message");
		}
		try
		{
			SdesItem.fromByteBuffer(ByteBuffer.wrap(new byte[] { 0x01, 0x04, 0x30, 0x31, 0x32 }));
			fail("should fail");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "data too short for stated length.");
		}
	}
	
	
	
	

}
