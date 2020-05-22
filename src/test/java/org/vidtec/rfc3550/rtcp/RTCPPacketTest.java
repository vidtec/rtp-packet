package org.vidtec.rfc3550.rtcp;

import org.testng.annotations.Test;

@Test
public class RTCPPacketTest 
{

	public void testCanCreateSimpleSRPacketFromValidByteArray()
	{
		// PCMU with 4 samples
//		final byte[] data = { (byte)0x80, 0x01, 0x00, 0x08, 0x01, 0x02, 0x03, 0x04 };
//		
//		final RTCPPacket p = RTCPPacket.fromByteArray(data);
//		
//		assertEquals(p.version(), 2, "incorrect version decode.");
//		assertTrue(!p.isPadded(), "should not be padded");
//		assertEquals(p.paddedBytesCount(), 0, "should not be padded");
//		assertEquals(p.payloadType(), 1, "payload type should be 1");
//		assertEquals(p.ssrcIdentifier(), 67305985, "timestamp should be 0x04030201");
//	
//		assertEquals(p.payloadLength(), 4, "payload should be 4 bytes.");
//		assertEquals(p.payloadAsByteArray(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid payload data");
//		assertEquals(p.payloadAsByteBuffer().compareTo(ByteBuffer.wrap(new byte[] { 0x40, 0x40, 0x40, 0x40 })), 0, "invalid raw payload data");
//
//		assertEquals(p.payloadLengthRaw(), 4, "raw payload should be 4 bytes.");
//		assertEquals(p.payloadRawAsByteArray(), new byte[] { 0x40, 0x40, 0x40, 0x40 }, "invalid raw payload data");
//
//		assertEquals(p.packetLength(), 16, "packet length should be 16 bytes.");
//		
//		assertEquals(p.asByteArray(), data, "packet data not reformed correctly.");
	}

}
