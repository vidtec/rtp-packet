package org.vidtech.rfc3550.rtp;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.testng.annotations.Test;

@Test
public class RTPPacketTest 
{

	public void testCanCreateSimplePacketFromValidByteArray()
	{
		// PCMU with 4 samples
		final byte[] data = { (byte)0x80, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
								    0x40, 0x40, 0x40, 0x40 };
		
		final RTPPacket p = RTPPacket.from(data);
		
		assertEquals(p.version(), 2, "incorrect version decode.");
		assertTrue(!p.isPadded(), "should not be padded");
		assertEquals(p.getPaddedBytes(), 0, "should not be padded");
		assertTrue(!p.hasExtension(), "should not have extension");
		assertTrue(!p.hasCsrcs(), "should not have contributing sources");
	assertEquals(p.csrcCount(), 0, "contributing source count should be 0");
	// TODO - test no csrcs
	
		assertTrue(!p.hasMarker(), "should not have marker");
		assertEquals(p.payloadType(), 0, "payload type should be 0");
		assertEquals(p.sequenceNumber(), 257, "seq. no should be 0x0101");
		assertEquals(p.timestamp(), 16909060, "timestamp should be 0x01020304");
		assertEquals(p.ssrcIdentifier(), 67305985, "timestamp should be 0x04030201");
	
	// TODO -> test no extension is present	
		
		assertEquals(p.payloadLength(), 4, "payload should be 4 bytes.");
		assertEquals(p.payload(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid payload data");

		assertEquals(p.payloadLengthRaw(), 4, "raw payload should be 4 bytes.");
		assertEquals(p.payloadRaw(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid raw payload data");

		assertEquals(p.packetLength(), 16, "packet length should be 16 bytes.");
		
		assertEquals(p.asByteArray(), data, "packet data not reformed correctly.");
	}
	

	public void testCanCreatePacketFromValidByteArrayWithPadding()
	{
		// PCMU with 1 sample and 3 bytes padding
		final byte[] data = { (byte)0xA0, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
								    0x40, 0x00, 0x00, 0x03 };
		
		final RTPPacket p = RTPPacket.from(data);
		
		assertEquals(p.version(), 2, "incorrect version decode.");
		assertTrue(p.isPadded(), "should be padded");
		assertEquals(p.getPaddedBytes(), 3, "should be padded");
		assertTrue(!p.hasExtension(), "should not have extension");
		assertTrue(!p.hasCsrcs(), "should not have contributing sources");
	assertEquals(p.csrcCount(), 0, "contributing source count should be 0");
	// TODO - test no csrcs
	
		assertTrue(!p.hasMarker(), "should not have marker");
		assertEquals(p.payloadType(), 0, "payload type should be 0");
		assertEquals(p.sequenceNumber(), 257, "seq. no should be 0x0101");
		assertEquals(p.timestamp(), 16909060, "timestamp should be 0x01020304");
		assertEquals(p.ssrcIdentifier(), 67305985, "timestamp should be 0x04030201");
	
	// TODO -> test no extension is present	
		
		assertEquals(p.payloadLength(), 1, "payload should be 1 byte1.");
		assertEquals(p.payload(), new byte[] { 0x40 }, "invalid payload data");

		assertEquals(p.payloadLengthRaw(), 4, "raw payload should be 4 bytes.");
		assertEquals(p.payloadRaw(), new byte[] { 0x40, 0x00, 0x00, 0x03 }, "invalid raw payload data");

		assertEquals(p.packetLength(), 16, "packet length should be 16 bytes.");
		
		assertEquals(p.asByteArray(), data, "packet data not reformed correctly.");
	}
	
	
	
	

	
	
	public void testByteArrayConstuctorValidatesPacketCorrectly()
	{
		try 
		{ 
			// Not enough header - v short.
			RTPPacket.from(new byte[] { 0x40 });
			fail("Expected exception.");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Packet too short, expecting at least 13 bytes, but found 1", "wrong validation message");
		}
		try 
		{ 
			// Not enough header - one short.
			RTPPacket.from(new byte[] { 0x40, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01 });
			fail("Expected exception.");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Packet too short, expecting at least 13 bytes, but found 12", "wrong validation message");
		}
		
		try 
		{ 
			// bad version number 1, not 2.
			RTPPacket.from(new byte[] { 0x40, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 0x40, 0x40, 0x40, 0x40 });
			fail("Expected exception.");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Invalid version number found, expecting 2", "wrong validation message");
		}
		
	}
	
	
}
