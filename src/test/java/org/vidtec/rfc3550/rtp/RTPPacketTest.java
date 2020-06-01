package org.vidtec.rfc3550.rtp;

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
		final byte[] data = { (byte)0x80, 0x01, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
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
		assertEquals(p.payloadType(), 1, "payload type should be 1");
		assertEquals(p.sequenceNumber(), 257, "seq. no should be 0x0101");
		assertEquals(p.timestamp(), 16909060, "timestamp should be 0x01020304");
		assertEquals(p.ssrcIdentifier(), 67305985, "ssrc should be 0x04030201");
	
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
		assertEquals(p.ssrcIdentifier(), 67305985, "ssrc should be 0x04030201");
	
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
		final byte[] data = { (byte)0x80, (byte)0x81, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
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
		assertEquals(p.payloadType(), 1, "payload type should be 1");
		assertEquals(p.sequenceNumber(), 257, "seq. no should be 0x0101");
		assertEquals(p.timestamp(), 16909060, "timestamp should be 0x01020304");
		assertEquals(p.ssrcIdentifier(), 67305985, "ssrc should be 0x04030201");
	
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
		assertEquals(p.ssrcIdentifier(), 67305985, "ssrc should be 0x04030201");
	
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
		assertEquals(p.ssrcIdentifier(), 67305985, "ssrc should be 0x04030201");
	
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
		assertEquals(p.ssrcIdentifier(), 67305985, "ssrc should be 0x04030201");
	
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
		final ByteBuffer bb = ByteBuffer.wrap(data);
		bb.put((byte)0x91).put((byte)0x7F).putShort((short)0xFFFF).putInt(0xFFFFFFFF).putInt(0xFFFFFFFF);
		bb.putInt(0xFFFFFFFF);
		bb.putShort((short)0xFFFF).putShort((short)0xFFFF);
		data[data.length - 1] = 0x01;
		
		final RTPPacket p = RTPPacket.fromByteArray(data);
		
		assertEquals(p.version(), 2, "incorrect version decode.");
		assertTrue(!p.isPadded(), "should not be padded");
		assertEquals(p.paddedBytesCount(), 0, "should not be padded");
		assertTrue(p.hasExtension(), "should have extension");
		assertTrue(p.hasCsrcs(), "should have contributing sources");
		assertEquals(p.csrcCount(), 1, "contributing source count should be 1");
		assertEquals(p.csrcIdentifiers(), new long[] { 0xFFFFFFFF }, "contributing sources should be valid[]");
	
		assertTrue(!p.hasMarker(), "should not have marker");
		assertEquals(p.payloadType(), 127, "payload type should be 127");
		assertEquals(p.sequenceNumber(), 0xFFFF, "seq. no should be 0xFFFF");
		assertEquals(p.timestamp(), 0xFFFFFFFF, "timestamp should be 0xFFFFFFFF");
		assertEquals(p.ssrcIdentifier(), 0xFFFFFFFF, "ssrc should be 0xFFFFFFFF");
	
		assertEquals(p.extensionProfile(), 0xFFFF, "extn prof should be set.");
		assertEquals(p.extensionLength(), 0xFFFF, "extn length should be set.");
		assertEquals(p.extensionHeaderAsByteArray(), new byte[0xFFFF] , "extn header should be set.");
	
		assertEquals(p.payloadLength(), 1, "payload should be 1 bytes.");
		assertEquals(p.payloadAsByteArray(), new byte[] { 0x01 }, "invalid payload data");
		assertEquals(p.payloadAsByteBuffer().compareTo(ByteBuffer.wrap(new byte[] { 0x01 })), 0, "invalid raw payload data");

		assertEquals(p.payloadLengthRaw(), 1, "raw payload should be 1 bytes.");
		assertEquals(p.payloadRawAsByteArray(), new byte[] { 0x01 }, "invalid raw payload data");

		assertEquals(p.packetLength(), 20 + 0xFFFF + 1, "packet length should be 26 bytes.");
		
		assertEquals(p.asByteArray(), data, "packet data not reformed correctly.");
	}
	
	
	public void testCanCreatePacketFromUsingBuilder()
	{
		final byte[] data = { (byte)0xB3, (byte)0x96, 0x00, (byte)0x7B, 0x00, 0x00, 0x01, (byte)0xC8, 0x00, 0x00, 0x03, 0x15, 
			    				0x00, 0x00, 0x00, (byte)0xAA, 0x00, 0x00, 0x00, (byte)0xBB, 0x00, 0x00, 0x00, (byte)0xCC,
			    				0x00, (byte)0xDD, 0x00, 0x04, 0x01, 0x02, 0x03, 0x04, 0x01, 0x02, 0x00, 0x02
			    				};

		final RTPPacket p = RTPPacket.builder()
							  .withMarker()
				 			  .withRequiredHeaderFields(22, 123, 456, 789)	
							  .withCsrcIdentifiers(0xAA, 0xBB, 0xCC)
							  .withHeaderExtension(0xDD, new byte[] { 0x01, 0x02, 0x03, 0x04 })
							  .withPayload(new byte[] { 0x01, 0x02 }, 4) /// also non version
							  .build();
		
		assertEquals(p.version(), 2, "incorrect version decode.");
		assertTrue(p.isPadded(), "should be padded");
		assertEquals(p.paddedBytesCount(), 2, "should be padded");
		assertTrue(p.hasExtension(), "should have extension");
		assertTrue(p.hasCsrcs(), "should have contributing sources");
		assertEquals(p.csrcCount(), 3, "contributing source count should be 1");
		assertEquals(p.csrcIdentifiers(), new long[] { 0xAA, 0xBB, 0xCC }, "contributing sources should be valid[]");
	
		assertTrue(p.hasMarker(), "should have marker");
		assertEquals(p.payloadType(), 22, "payload type should be 22");
		assertEquals(p.sequenceNumber(), 123, "seq. no should be 123");
		assertEquals(p.timestamp(), 456, "timestamp should be 456");
		assertEquals(p.ssrcIdentifier(), 789, "ssrc should be 789");
	
		assertEquals(p.extensionProfile(), 0xDD, "extn prof should be set.");
		assertEquals(p.extensionLength(), 4, "extn length should be set.");
		assertEquals(p.extensionHeaderAsByteArray(), new byte[] { 0x01, 0x02, 0x03, 0x04 }, "extn header should be set.");
	
		assertEquals(p.payloadLength(), 2, "payload should be 2 bytes.");
		assertEquals(p.payloadAsByteArray(), new byte[] { 0x01, 0x02 }, "invalid payload data");
		assertEquals(p.payloadAsByteBuffer().compareTo(ByteBuffer.wrap(new byte[] { 0x01, 0x02 })), 0, "invalid raw payload data");

		assertEquals(p.payloadLengthRaw(), 4, "raw payload should be 4 bytes.");
		assertEquals(p.payloadRawAsByteArray(), new byte[] { 0x01, 0x02, 0x00, 0x02 }, "invalid raw payload data");

		assertEquals(p.packetLength(), 36, "packet length should be 36 bytes.");
		
		assertEquals(p.asByteArray(), data, "packet data not reformed correctly.");
	}
	
	public void testCanCreatePacketFromUsingBuilderWithBoundaryValues()
	{
		final RTPPacket p = RTPPacket.builder()
							  .withMarker()
				 			  .withRequiredHeaderFields(0x7F, 0xFFFF, 0xFFFFFFFFL, 0xFFFFFFFFL)	
							  .withCsrcIdentifiers(1,2,3,4,5,6,7,8,9,10,11,12,13,14,0xFFFFFFFFL)
							  .withHeaderExtension(0xFFFF, new byte[0xFFFF])
							  .withPayload(new byte[] { 0x01 })
							  .build();
		
		// create a packet of max extn header size. 20 + 0xFFFF + 1
		final byte[] data = new byte[76 + 0xFFFF + 1];
		final ByteBuffer bb = ByteBuffer.wrap(data);
		bb.put((byte)0x9F).put((byte)0xFF).putShort((short)0xFFFF).putInt(0xFFFFFFFF).putInt(0xFFFFFFFF);
		bb.putInt(1).putInt(2).putInt(3).putInt(4).putInt(5).putInt(6).putInt(7).putInt(8).putInt(9).putInt(10);
		bb.putInt(11).putInt(12).putInt(13).putInt(14).putInt(0xFFFFFFFF);
		bb.putShort((short)0xFFFF).putShort((short)0xFFFF);
		data[data.length - 1] = 0x01;
		
		assertEquals(p.version(), 2, "incorrect version decode.");
		assertTrue(!p.isPadded(), "should not be padded");
		assertEquals(p.paddedBytesCount(), 0, "should not be padded");
		assertTrue(p.hasExtension(), "should have extension");
		assertTrue(p.hasCsrcs(), "should have contributing sources");
		assertEquals(p.csrcCount(), 15, "contributing source count should be 1");
		assertEquals(p.csrcIdentifiers(), new long[] { 1,2,3,4,5,6,7,8,9,10,11,12,13,14,0xFFFFFFFFL }, "contributing sources should be valid[]");
	
		assertTrue(p.hasMarker(), "should have marker");
		assertEquals(p.payloadType(), 127, "payload type should be 127");
		assertEquals(p.sequenceNumber(), 0xFFFF, "seq. no should be 0xFFFF");
		assertEquals(p.timestamp(), 0xFFFFFFFFL, "timestamp should be 0xFFFFFFFF");
		assertEquals(p.ssrcIdentifier(), 0xFFFFFFFFL, "ssrc should be 0xFFFFFFFF");
	
		assertEquals(p.extensionProfile(), 0xFFFF, "extn prof should be set.");
		assertEquals(p.extensionLength(), 0xFFFF, "extn length should be set.");
		assertEquals(p.extensionHeaderAsByteArray(), new byte[0xFFFF] , "extn header should be set.");
	
		assertEquals(p.payloadLength(), 1, "payload should be 1 bytes.");
		assertEquals(p.payloadAsByteArray(), new byte[] { 0x01 }, "invalid payload data");
		assertEquals(p.payloadAsByteBuffer().compareTo(ByteBuffer.wrap(new byte[] { 0x01 })), 0, "invalid raw payload data");

		assertEquals(p.payloadLengthRaw(), 1, "raw payload should be 1 bytes.");
		assertEquals(p.payloadRawAsByteArray(), new byte[] { 0x01 }, "invalid raw payload data");

		assertEquals(p.packetLength(), 76 + 0xFFFF + 1, "packet length should be 65556 bytes.");
		
		assertEquals(p.asByteArray(), data, "packet data not reformed correctly.");

	}
	 
	
	public void testCanSuccessfullyValidateAndRejectBadBuilderData()
	{
		try
		{
			RTPPacket.builder()
		 			  .withRequiredHeaderFields(-1, 123, 456, 789)	
					  .build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid payload type not -1", "wrong validation message");
		}
		try
		{
			RTPPacket.builder()
		 			  .withRequiredHeaderFields(256, 123, 456, 789)	
					  .build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid payload type not 256", "wrong validation message");
		}
		try
		{
			RTPPacket.builder()
		 			  .withRequiredHeaderFields(22, -1, 456, 789)	
					  .build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid sequence number not -1", "wrong validation message");
		}
		try
		{
			RTPPacket.builder()
		 			  .withRequiredHeaderFields(22, 0xFFFFFF, 456, 789)	
					  .build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid sequence number not 16777215", "wrong validation message");
		}
		try
		{
			RTPPacket.builder()
		 			  .withRequiredHeaderFields(22, 123, -1, 789)	
					  .build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid timestamp not -1", "wrong validation message");
		}
		try
		{
			RTPPacket.builder()
		 			  .withRequiredHeaderFields(22, 123, 0x1FFFFFFFFL, 789)	
					  .build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid timestamp not 8589934591", "wrong validation message");
		}
		try
		{
			RTPPacket.builder()
		 			  .withRequiredHeaderFields(22, 123, 456, -1)	
					  .build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid ssrcIdentifier not -1", "wrong validation message");
		}
		try
		{
			RTPPacket.builder()
		 			  .withRequiredHeaderFields(22, 123, 456, 0x1FFFFFFFFL)	
					  .build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid ssrcIdentifier not 8589934591", "wrong validation message");
		}
		
		try
		{
			RTPPacket.builder()
		 			  .withRequiredHeaderFields(22, 123, 456, 689)
		 			  .withCsrcIdentifiers(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17)
					  .build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid ccsrc identifiers count not 17", "wrong validation message");
		}
		
		try
		{
			RTPPacket.builder()
		 			  .withRequiredHeaderFields(22, 123, 456, 689)
		 			  .withHeaderExtension(1, null)
					  .build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid header not null", "wrong validation message");
		}
		try
		{
			RTPPacket.builder()
		 			  .withRequiredHeaderFields(22, 123, 456, 689)
		 			  .withHeaderExtension(1, new byte[0x1FFFF])
					  .build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid header length not 131071", "wrong validation message");
		}	
		try
		{
			RTPPacket.builder()
		 			  .withRequiredHeaderFields(22, 123, 456, 689)
		 			  .withHeaderExtension(-1, new byte[1])
					  .build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid extension profile not -1", "wrong validation message");
		}		
		try
		{
			RTPPacket.builder()
		 			  .withRequiredHeaderFields(22, 123, 456, 689)
		 			  .withHeaderExtension(0xFFFFFF, new byte[1])
					  .build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid extension profile not 16777215", "wrong validation message");
		}	
		try
		{
			RTPPacket.builder()
		 			  .withRequiredHeaderFields(22, 123, 456, 689)
		 			  .withPayload(null)
					  .build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid payload not null or empty", "wrong validation message");
		}			
		try
		{
			RTPPacket.builder()
		 			  .withRequiredHeaderFields(22, 123, 456, 689)
		 			  .withPayload(new byte[0])
					  .build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid payload not null or empty", "wrong validation message");
		}	
		try
		{
			RTPPacket.builder()
		 			  .withRequiredHeaderFields(22, 123, 456, 689)
		 			  .withPayload(null, 4)
					  .build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "cannot align to boundary of null data.", "wrong validation message");
		}	
	}
	
	
	public void testByteArrayConstuctorValidatesPacketCorrectly()
	{
		try
		{
			RTPPacket.fromByteArray( null );
			fail("Expected error");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "packet data cannot be null", "wrong validation message");
		}

		// Tests that [RTP-PACKET-1] https://github.com/vidtec/rtp-packet/issues/1 is resolved.
		try
		{
			RTPPacket.fromDatagramPacket( null );
			fail("Expected error");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "packet cannot be null", "wrong validation message");
		}
		
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
		
		try 
		{ 
			// has extension but no data
			RTPPacket.fromByteArray(new byte[] { (byte)0x90, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 0x40, 0x40, 0x40, 0x40 });
			fail("Expected exception.");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Packet too short, expecting at least 5 bytes, but found 4", "wrong validation message");
		}
		
		try 
		{ 
			// has extension but no data
			RTPPacket.fromByteArray(new byte[] { (byte)0x90, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 0x40, 0x40, 0x00, 0x02, 0x10, 0x40 });
			fail("Expected exception.");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Packet too short, expecting at least 3 bytes, but found 2", "wrong validation message");
		}
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
		
		packets.clear();
		packets.add(null);
		packets.add(RTPPacket.fromByteArray(data1));
		
		try
		{
			Collections.sort(packets);
			fail("should error with NPE");
		}
		catch (NullPointerException e)
		{
			// expected
		}
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
		assertEquals(p.ssrcIdentifier(), 67305985, "ssrc should be 0x04030201");
	
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
	
	
	
	public void testCanBuildRTPPacketFromDatagramPacketWhereSourceBufferIsOversizedComparedToReadDataCorrectly()
	{
		// Tests that [RTP-PACKET-1] https://github.com/vidtec/rtp-packet/issues/1 is resolved.
		
		// PCMU with 4 samples - NB: data is greater than packet size 'read'.
		final byte[] data = { (byte)0x80, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
					    		     0x40, 0x40, 0x40, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		
		// PCMU with 4 samples - NB: data is greater than packet size 'read'.
		final byte[] mindata = { (byte)0x80, 0x00, 0x01, 0x01, 0x01, 0x02, 0x03, 0x04, 0x04, 0x03, 0x02, 0x01, 
					    		     0x40, 0x40, 0x40, 0x40 };
		
		// Simulate the reading of a packet - 'read' 16 bytes, but buffer is bigger.
		DatagramPacket dp = new DatagramPacket(data, 16);
		
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
		assertEquals(p.ssrcIdentifier(), 67305985, "ssrc should be 0x04030201");
	
		assertEquals(p.extensionProfile(), -1, "extn prof should not be set.");
		assertEquals(p.extensionLength(), -1, "extn length should not be set.");
		assertEquals(p.extensionHeaderAsByteArray(), new byte[0], "extn header should not be set.");
		
		assertEquals(p.payloadLength(), 4, "payload should be 4 bytes.");
		assertEquals(p.payloadAsByteArray(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid payload data");
		assertEquals(p.payloadAsByteBuffer().compareTo(ByteBuffer.wrap(new byte[] { 0x40, 0x40, 0x40, 0x40 })), 0, "invalid raw payload data");

		assertEquals(p.payloadLengthRaw(), 4, "raw payload should be 4 bytes.");
		assertEquals(p.payloadRawAsByteArray(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid raw payload data");

		assertEquals(p.packetLength(), 16, "packet length should be 16 bytes.");
		
		assertEquals(p.asByteArray(), mindata, "packet data not reformed correctly.");
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
