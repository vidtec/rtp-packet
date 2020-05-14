package org.vidtech.rfc3550.rtp;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

@Test
public class RTPPacketTest 
{

	public void testCanCreateSimplePacketFromValidByteArray()
	{
		// PCMU with 4 samples
		final byte[] data = { (byte)0x80, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
								    0x40, 0x40, 0x40, 0x40 };
		
		final RTPPacket p = RTPPacket.fromByteArray(data);
		
		assertEquals(p.version(), 2, "incorrect version decode.");
		assertTrue(!p.isPadded(), "should not be padded");
		assertEquals(p.paddedBytesCount(), 0, "should not be padded");
		assertTrue(!p.hasExtension(), "should not have extension");
		assertTrue(!p.hasCsrcs(), "should not have contributing sources");
		assertEquals(p.csrcCount(), 0, "contributing source count should be 0");
		assertEquals(p.csrcIdentifiers(), new byte[0], "contributing sources should be empty[]");
	
		assertTrue(!p.hasMarker(), "should not have marker");
		assertEquals(p.payloadType(), 0, "payload type should be 0");
		assertEquals(p.sequenceNumber(), 257, "seq. no should be 0x0101");
		assertEquals(p.timestamp(), 16909060, "timestamp should be 0x01020304");
		assertEquals(p.ssrcIdentifier(), 67305985, "timestamp should be 0x04030201");
	
		assertEquals(p.extensionProfile(), -1, "extn prof should not be set.");
		assertEquals(p.extensionLength(), -1, "extn length should not be set.");
		assertEquals(p.extensionHeaderAsByteArray(), new byte[0], "extn header should not be set.");
		
		assertEquals(p.payloadLength(), 4, "payload should be 4 bytes.");
		assertEquals(p.payloadAsByteArray(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid payload data");
		assertEquals(p.payloadAsByteBuffer().compareTo(ByteBuffer.wrap(new byte[] { 0x40, 0x40, 0x40, 0x40 })), 0, "invalid raw payload data");

		assertEquals(p.payloadLengthRaw(), 4, "raw payload should be 4 bytes.");
		assertEquals(p.payloadRawAsByteArray(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid raw payload data");

		assertEquals(p.packetLength(), 16, "packet length should be 16 bytes.");
		
		assertEquals(p.asByteArray(), data, "packet data not reformed correctly.");
	}
	

	public void testCanCreatePacketFromValidByteArrayWithPadding()
	{
		// PCMU with 1 sample and 3 bytes padding
		final byte[] data = { (byte)0xA0, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
								    0x40, 0x00, 0x00, 0x03 };
		
		final RTPPacket p = RTPPacket.fromByteArray(data);
		
		assertEquals(p.version(), 2, "incorrect version decode.");
		assertTrue(p.isPadded(), "should be padded");
		assertEquals(p.paddedBytesCount(), 3, "should be padded");
		assertTrue(!p.hasExtension(), "should not have extension");
		assertTrue(!p.hasCsrcs(), "should not have contributing sources");
		assertEquals(p.csrcCount(), 0, "contributing source count should be 0");
		assertEquals(p.csrcIdentifiers(), new byte[0], "contributing sources should be empty[]");
	
		assertTrue(!p.hasMarker(), "should not have marker");
		assertEquals(p.payloadType(), 0, "payload type should be 0");
		assertEquals(p.sequenceNumber(), 257, "seq. no should be 0x0101");
		assertEquals(p.timestamp(), 16909060, "timestamp should be 0x01020304");
		assertEquals(p.ssrcIdentifier(), 67305985, "timestamp should be 0x04030201");
	
		assertEquals(p.extensionProfile(), -1, "extn prof should not be set.");
		assertEquals(p.extensionLength(), -1, "extn length should not be set.");
		assertEquals(p.extensionHeaderAsByteArray(), new byte[0], "extn header should not be set.");
	
		assertEquals(p.payloadLength(), 1, "payload should be 1 byte1.");
		assertEquals(p.payloadAsByteArray(), new byte[] { 0x40 }, "invalid payload data");
		assertEquals(p.payloadAsByteBuffer().compareTo(ByteBuffer.wrap(new byte[] { 0x40 })), 0, "invalid raw payload data");

		assertEquals(p.payloadLengthRaw(), 4, "raw payload should be 4 bytes.");
		assertEquals(p.payloadRawAsByteArray(), new byte[] { 0x40, 0x00, 0x00, 0x03 }, "invalid raw payload data");

		assertEquals(p.packetLength(), 16, "packet length should be 16 bytes.");
		
		assertEquals(p.asByteArray(), data, "packet data not reformed correctly.");
	}
	
	
	public void testCanCreatePacketFromValidByteArrayWithMarker()
	{
		// PCMU with 1 sample and 3 bytes padding
		final byte[] data = { (byte)0x80, (byte)0x80, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
								    0x40, 0x40, 0x40, 0x40 };
		
		final RTPPacket p = RTPPacket.fromByteArray(data);
		
		assertEquals(p.version(), 2, "incorrect version decode.");
		assertTrue(!p.isPadded(), "should not be padded");
		assertEquals(p.paddedBytesCount(), 0, "should not be padded");
		assertTrue(!p.hasExtension(), "should not have extension");
		assertTrue(!p.hasCsrcs(), "should not have contributing sources");
		assertEquals(p.csrcCount(), 0, "contributing source count should be 0");
		assertEquals(p.csrcIdentifiers(), new byte[0], "contributing sources should be empty[]");
	
		assertTrue(p.hasMarker(), "should have marker");
		assertEquals(p.payloadType(), 0, "payload type should be 0");
		assertEquals(p.sequenceNumber(), 257, "seq. no should be 0x0101");
		assertEquals(p.timestamp(), 16909060, "timestamp should be 0x01020304");
		assertEquals(p.ssrcIdentifier(), 67305985, "timestamp should be 0x04030201");
	
		assertEquals(p.extensionProfile(), -1, "extn prof should not be set.");
		assertEquals(p.extensionLength(), -1, "extn length should not be set.");
		assertEquals(p.extensionHeaderAsByteArray(), new byte[0], "extn header should not be set.");
	
		assertEquals(p.payloadLength(), 4, "payload should be 4 bytes.");
		assertEquals(p.payloadAsByteArray(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid payload data");
		assertEquals(p.payloadAsByteBuffer().compareTo(ByteBuffer.wrap(new byte[] { 0x40, 0x40, 0x40, 0x40 })), 0, "invalid raw payload data");

		assertEquals(p.payloadLengthRaw(), 4, "raw payload should be 4 bytes.");
		assertEquals(p.payloadRawAsByteArray(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid raw payload data");

		assertEquals(p.packetLength(), 16, "packet length should be 16 bytes.");
		
		assertEquals(p.asByteArray(), data, "packet data not reformed correctly.");
	}
	

	public void testCanCreatePacketFromValidByteArrayWithCsrcs()
	{
		// PCMU with 1 sample and 3 bytes padding
		final byte[] data = { (byte)0x81, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
								    0x00, 0x00, 0x00, 0x01, 0x40, 0x40, 0x40, 0x40 };
		
		final RTPPacket p = RTPPacket.fromByteArray(data);
		
		assertEquals(p.version(), 2, "incorrect version decode.");
		assertTrue(!p.isPadded(), "should not be padded");
		assertEquals(p.paddedBytesCount(), 0, "should not be padded");
		assertTrue(!p.hasExtension(), "should not have extension");
		assertTrue(p.hasCsrcs(), "should have contributing sources");
		assertEquals(p.csrcCount(), 1, "contributing source count should be 1");
		assertEquals(p.csrcIdentifiers(), new long[] { 0x01 }, "contributing sources should be valid[]");
	
		assertTrue(!p.hasMarker(), "should not have marker");
		assertEquals(p.payloadType(), 0, "payload type should be 0");
		assertEquals(p.sequenceNumber(), 257, "seq. no should be 0x0101");
		assertEquals(p.timestamp(), 16909060, "timestamp should be 0x01020304");
		assertEquals(p.ssrcIdentifier(), 67305985, "timestamp should be 0x04030201");
	
		assertEquals(p.extensionProfile(), -1, "extn prof should not be set.");
		assertEquals(p.extensionLength(), -1, "extn length should not be set.");
		assertEquals(p.extensionHeaderAsByteArray(), new byte[0], "extn header should not be set.");
	
		assertEquals(p.payloadLength(), 4, "payload should be 4 bytes.");
		assertEquals(p.payloadAsByteArray(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid payload data");
		assertEquals(p.payloadAsByteBuffer().compareTo(ByteBuffer.wrap(new byte[] { 0x40, 0x40, 0x40, 0x40 })), 0, "invalid raw payload data");

		assertEquals(p.payloadLengthRaw(), 4, "raw payload should be 4 bytes.");
		assertEquals(p.payloadRawAsByteArray(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid raw payload data");

		assertEquals(p.packetLength(), 20, "packet length should be 20 bytes.");
		
		assertEquals(p.asByteArray(), data, "packet data not reformed correctly.");
	}

	public void testCanCreatePacketFromValidByteArrayWithHeaderExtn()
	{
		// PCMU with 1 sample and 3 bytes padding
		final byte[] data = { (byte)0x90, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
								    0x00, 0x03, 0x00, 0x02, 0x00, 0x01, 0x40, 0x40, 0x40, 0x40 };
		
		final RTPPacket p = RTPPacket.fromByteArray(data);
		
		assertEquals(p.version(), 2, "incorrect version decode.");
		assertTrue(!p.isPadded(), "should not be padded");
		assertEquals(p.paddedBytesCount(), 0, "should not be padded");
		assertTrue(p.hasExtension(), "should have extension");
		assertTrue(!p.hasCsrcs(), "should not have contributing sources");
		assertEquals(p.csrcCount(), 0, "contributing source count should be 0");
		assertEquals(p.csrcIdentifiers(), new byte[0], "contributing sources should be empty[]");
	
		assertTrue(!p.hasMarker(), "should not have marker");
		assertEquals(p.payloadType(), 0, "payload type should be 0");
		assertEquals(p.sequenceNumber(), 257, "seq. no should be 0x0101");
		assertEquals(p.timestamp(), 16909060, "timestamp should be 0x01020304");
		assertEquals(p.ssrcIdentifier(), 67305985, "timestamp should be 0x04030201");
	
		assertEquals(p.extensionProfile(), 3, "extn prof should be set.");
		assertEquals(p.extensionLength(), 2, "extn length should be set.");
		assertEquals(p.extensionHeaderAsByteArray(), new byte[] { 0x00, 0x01 }, "extn header should be set.");
	
		assertEquals(p.payloadLength(), 4, "payload should be 4 bytes.");
		assertEquals(p.payloadAsByteArray(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid payload data");
		assertEquals(p.payloadAsByteBuffer().compareTo(ByteBuffer.wrap(new byte[] { 0x40, 0x40, 0x40, 0x40 })), 0, "invalid raw payload data");

		assertEquals(p.payloadLengthRaw(), 4, "raw payload should be 4 bytes.");
		assertEquals(p.payloadRawAsByteArray(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid raw payload data");

		assertEquals(p.packetLength(), 22, "packet length should be 22 bytes.");
		
		assertEquals(p.asByteArray(), data, "packet data not reformed correctly.");
	}

	
	public void testCanCreatePacketFromValidByteArrayWithCSrcsAndHeaderExtn()
	{
		// PCMU with 1 sample and 3 bytes padding
		final byte[] data = { (byte)0x91, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
								    0x00, 0x00, 0x00, 0x01, 0x00, 0x03, 0x00, 0x02, 0x00, 0x01, 0x40, 0x40, 0x40, 0x40 };
		
		final RTPPacket p = RTPPacket.fromByteArray(data);
		
		assertEquals(p.version(), 2, "incorrect version decode.");
		assertTrue(!p.isPadded(), "should not be padded");
		assertEquals(p.paddedBytesCount(), 0, "should not be padded");
		assertTrue(p.hasExtension(), "should have extension");
		assertTrue(p.hasCsrcs(), "should have contributing sources");
		assertEquals(p.csrcCount(), 1, "contributing source count should be 1");
		assertEquals(p.csrcIdentifiers(), new long[] { 0x01 }, "contributing sources should be valid[]");
	
		assertTrue(!p.hasMarker(), "should not have marker");
		assertEquals(p.payloadType(), 0, "payload type should be 0");
		assertEquals(p.sequenceNumber(), 257, "seq. no should be 0x0101");
		assertEquals(p.timestamp(), 16909060, "timestamp should be 0x01020304");
		assertEquals(p.ssrcIdentifier(), 67305985, "timestamp should be 0x04030201");
	
		assertEquals(p.extensionProfile(), 3, "extn prof should be set.");
		assertEquals(p.extensionLength(), 2, "extn length should be set.");
		assertEquals(p.extensionHeaderAsByteArray(), new byte[] { 0x00, 0x01 }, "extn header should be set.");
	
		assertEquals(p.payloadLength(), 4, "payload should be 4 bytes.");
		assertEquals(p.payloadAsByteArray(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid payload data");
		assertEquals(p.payloadAsByteBuffer().compareTo(ByteBuffer.wrap(new byte[] { 0x40, 0x40, 0x40, 0x40 })), 0, "invalid raw payload data");

		assertEquals(p.payloadLengthRaw(), 4, "raw payload should be 4 bytes.");
		assertEquals(p.payloadRawAsByteArray(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid raw payload data");

		assertEquals(p.packetLength(), 26, "packet length should be 26 bytes.");
		
		assertEquals(p.asByteArray(), data, "packet data not reformed correctly.");
	}
	
	
	public void testCanCreatePacketFromValidByteArrayWithBoundaryValues()
	{
		// create a packet of max extn header size. 20 + 0xFFFF + 1
		final byte[] data = new byte[20 + 0xFFFF + 1];
		data[0] = (byte)0x91;
		data[2] = (byte)0xFF;
		data[3] = (byte)0xFF;
		data[4] = (byte)0xFF;
		data[5] = (byte)0xFF;
		data[6] = (byte)0xFF;
		data[7] = (byte)0xFF;
		data[8] = (byte)0xFF;
		data[9] = (byte)0xFF;
		data[10] = (byte)0xFF;
		data[11] = (byte)0xFF;
		data[12] = (byte)0xFF;
		data[13] = (byte)0xFF;
		data[14] = (byte)0xFF;
		data[15] = (byte)0xFF;
		data[16] = (byte)0xFF;
		data[17] = (byte)0xFF;
		data[18] = (byte)0xFF;
		data[19] = (byte)0xFF;
		
		final RTPPacket p = RTPPacket.fromByteArray(data);
		
		assertEquals(p.version(), 2, "incorrect version decode.");
		assertTrue(!p.isPadded(), "should not be padded");
		assertEquals(p.paddedBytesCount(), 0, "should not be padded");
		assertTrue(p.hasExtension(), "should have extension");
		assertTrue(p.hasCsrcs(), "should have contributing sources");
		assertEquals(p.csrcCount(), 1, "contributing source count should be 1");
		assertEquals(p.csrcIdentifiers(), new long[] { 0xFFFFFFFF }, "contributing sources should be valid[]");
	
		assertTrue(!p.hasMarker(), "should not have marker");
		assertEquals(p.payloadType(), 0, "payload type should be 0");
		assertEquals(p.sequenceNumber(), 0xFFFF, "seq. no should be 0xFFFF");
		assertEquals(p.timestamp(), 0xFFFFFFFF, "timestamp should be 0xFFFFFFFF");
		assertEquals(p.ssrcIdentifier(), 0xFFFFFFFF, "timestamp should be 0xFFFFFFFF");
	
		assertEquals(p.extensionProfile(), 0xFFFF, "extn prof should be set.");
		assertEquals(p.extensionLength(), 0xFFFF, "extn length should be set.");
		assertEquals(p.extensionHeaderAsByteArray(), new byte[0xFFFF] , "extn header should be set.");
	
		assertEquals(p.payloadLength(), 1, "payload should be 1 bytes.");
		assertEquals(p.payloadAsByteArray(), new byte[] { 0x00 }, "invalid payload data");
		assertEquals(p.payloadAsByteBuffer().compareTo(ByteBuffer.wrap(new byte[] { 0x00 })), 0, "invalid raw payload data");

		assertEquals(p.payloadLengthRaw(), 1, "raw payload should be 1 bytes.");
		assertEquals(p.payloadRawAsByteArray(), new byte[] { 0x00 }, "invalid raw payload data");

		assertEquals(p.packetLength(), 20 + 0xFFFF + 1, "packet length should be 26 bytes.");
		
		assertEquals(p.asByteArray(), data, "packet data not reformed correctly.");
	}
	

	
	public void testByteArrayConstuctorValidatesPacketCorrectly()
	{
		try 
		{ 
			// Not enough header - v short.
			RTPPacket.fromByteArray(new byte[] { (byte)0x80 });
			fail("Expected exception.");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Packet too short, expecting at least 13 bytes, but found 1", "wrong validation message");
		}
		try 
		{ 
			// Not enough header - one short.
			RTPPacket.fromByteArray(new byte[] { (byte)0x80, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01 });
			fail("Expected exception.");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Packet too short, expecting at least 13 bytes, but found 12", "wrong validation message");
		}
		
		try 
		{ 
			// bad version number 1, not 2.
			RTPPacket.fromByteArray(new byte[] { 0x40, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 0x40, 0x40, 0x40, 0x40 });
			fail("Expected exception.");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Invalid version number found, expecting 2", "wrong validation message");
		}
		
		try 
		{ 
			// csrc count = 1 but no csrc data..
			RTPPacket.fromByteArray(new byte[] { (byte)0x81, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 0x40, 0x40, 0x40, 0x40 });
			fail("Expected exception.");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Packet too short, expecting at least 5 bytes, but found 4", "wrong validation message");
		}
		
		
// jedaer extn
		
		
	}

	
	public void testCanCorrectlyCGenerateHashCode()
	{
		// PCMU with 4 samples
		final byte[] data1 = { (byte)0x80, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
								     0x40, 0x40, 0x40, 0x40 };
		
		final RTPPacket p1 = RTPPacket.fromByteArray(data1);
		
		assertEquals(p1.hashCode(), 288, "worng hash");
	}
	
	
	@SuppressWarnings("unlikely-arg-type")
	public void testCanCorrectlyCompareWithEquals()
	{
		// PCMU with 4 samples
		final byte[] data1 = { (byte)0x80, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
								     0x40, 0x40, 0x40, 0x40 };
		
		final RTPPacket p1 = RTPPacket.fromByteArray(data1);
		final RTPPacket p1a = RTPPacket.fromByteArray(data1);
		
		// different seq no
		final byte[] data2 = { (byte)0x80, 0x00, 0x01, 0x02, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
									 0x40, 0x40, 0x40, 0x40 };

		final RTPPacket p2 = RTPPacket.fromByteArray(data2);
		
		
		assertTrue(p1.equals(p1));
		assertTrue(p1.equals(p1a));
		assertTrue(!p1.equals(p2));
		assertTrue(!p1.equals(null));
		assertTrue(!p1.equals(new String("bob")));
	}
	
	
	public void testCannotBeCloned()
	{
		// PCMU with 4 samples
		final byte[] data = { (byte)0x80, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
					    		     0x40, 0x40, 0x40, 0x40 };
		
		final RTPPacket p = RTPPacket.fromByteArray(data);
		
		try
		{
			p.clone();
			fail("Expected clone to fail");
		}
		catch (CloneNotSupportedException e)
		{
			// do nothing.
		}
	}
	
	
	public void testCanBeCorrectlyComparedBasedOnSequenceNumber()
	{
		final byte[] data1 = { (byte)0x80, 0x00, 0x00, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
	   		     0x40, 0x40, 0x40, 0x40 };
		final byte[] data2 = { (byte)0x80, 0x00, 0x00, 0x02, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
	   		     0x40, 0x40, 0x40, 0x40 };
		final byte[] data3 = { (byte)0x80, 0x00, 0x00, 0x04, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
	   		     0x40, 0x40, 0x40, 0x40 };
		final byte[] data4 = { (byte)0x80, 0x00, (byte)0xFF, (byte)0xFF, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
		   		     0x40, 0x40, 0x40, 0x40 };
			
		final List<RTPPacket> packets = new ArrayList<>(4);
		packets.add(RTPPacket.fromByteArray(data3));
		packets.add(RTPPacket.fromByteArray(data1));
		packets.add(RTPPacket.fromByteArray(data2));
		packets.add(RTPPacket.fromByteArray(data4));
		
		// current order is 0004, 0001, 0002, FFFF
		Collections.sort(packets);
		
		// order should be FFFF, 0001, 0002, 0004
		assertEquals(packets.get(0).sequenceNumber(), 0x0001, "wrong sort order" );
		assertEquals(packets.get(1).sequenceNumber(), 0x0002, "wrong sort order" );
		assertEquals(packets.get(2).sequenceNumber(), 0x0004, "wrong sort order" );
		assertEquals(packets.get(3).sequenceNumber(), 0xFFFF, "wrong sort order" );
	}

	
	public void testCanBuildRTPPacketFromDatagramPacketCorrectly()
	{
		// PCMU with 4 samples
		final byte[] data = { (byte)0x80, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
					    		     0x40, 0x40, 0x40, 0x40 };
		
		DatagramPacket dp = new DatagramPacket(data, data.length);
		
		final RTPPacket p = RTPPacket.fromDatagramPacket(dp);

		assertEquals(p.version(), 2, "incorrect version decode.");
		assertTrue(!p.isPadded(), "should not be padded");
		assertEquals(p.paddedBytesCount(), 0, "should not be padded");
		assertTrue(!p.hasExtension(), "should not have extension");
		assertTrue(!p.hasCsrcs(), "should not have contributing sources");
		assertEquals(p.csrcCount(), 0, "contributing source count should be 0");
		assertEquals(p.csrcIdentifiers(), new byte[0], "contributing sources should be empty[]");
	
		assertTrue(!p.hasMarker(), "should not have marker");
		assertEquals(p.payloadType(), 0, "payload type should be 0");
		assertEquals(p.sequenceNumber(), 257, "seq. no should be 0x0101");
		assertEquals(p.timestamp(), 16909060, "timestamp should be 0x01020304");
		assertEquals(p.ssrcIdentifier(), 67305985, "timestamp should be 0x04030201");
	
		assertEquals(p.extensionProfile(), -1, "extn prof should not be set.");
		assertEquals(p.extensionLength(), -1, "extn length should not be set.");
		assertEquals(p.extensionHeaderAsByteArray(), new byte[0], "extn header should not be set.");
		
		assertEquals(p.payloadLength(), 4, "payload should be 4 bytes.");
		assertEquals(p.payloadAsByteArray(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid payload data");
		assertEquals(p.payloadAsByteBuffer().compareTo(ByteBuffer.wrap(new byte[] { 0x40, 0x40, 0x40, 0x40 })), 0, "invalid raw payload data");

		assertEquals(p.payloadLengthRaw(), 4, "raw payload should be 4 bytes.");
		assertEquals(p.payloadRawAsByteArray(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid raw payload data");

		assertEquals(p.packetLength(), 16, "packet length should be 16 bytes.");
		
		assertEquals(p.asByteArray(), data, "packet data not reformed correctly.");
		
	}
	
	
	public void testCanBuildDatagramPacketFromObjectCorrectly() throws UnknownHostException
	{
		// PCMU with 4 samples
		final byte[] data = { (byte)0x80, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
					    		     0x40, 0x40, 0x40, 0x40 };
		
		final RTPPacket p = RTPPacket.fromByteArray(data);
		
		DatagramPacket d = p.asDatagramPacket(InetAddress.getByName("192.168.1.1"), 25000);
		assertEquals(d.getAddress().getHostAddress(), "192.168.1.1", "incorrect inet address");
		assertEquals(d.getPort(), 25000, "incorrect port");
		assertEquals(d.getLength(), data.length, "incorrect length");
		assertEquals(d.getData(), data, "incorrect data");
	}
	 
	
	
	
}
