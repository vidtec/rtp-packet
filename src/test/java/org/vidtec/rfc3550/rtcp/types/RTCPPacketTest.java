package org.vidtec.rfc3550.rtcp.types;

import static org.testng.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.testng.annotations.Test;
import org.vidtec.rfc3550.rtcp.types.RTCPPacket.PayloadType;

@Test
public class RTCPPacketTest 
{

	public void testCanCorrectlyExtractPayloadTypeFromValidData()
	{
		// valid RR RTCP packet
		final byte[] data = { (byte)0x80, (byte)0xC9, 0x00, 0x08, 0x00, 0x00, 0x00, 0x08 };
		final ByteBuffer b = ByteBuffer.wrap(data);

		assertEquals(b.position(), 0, "incorrect start position.");
		assertEquals(b.remaining(), 8, "incorrect remaining.");
		assertEquals(b.limit(), 8, "incorrect limit.");
		
		assertEquals(RTCPPacket.peekPayloadType(b), PayloadType.RR, "incorrect payload type");

		assertEquals(b.position(), 0, "incorrect start position after peek.");
		assertEquals(b.remaining(), 8, "incorrect remaining after peek.");
		assertEquals(b.limit(), 8, "incorrect limit after peek.");

	}
	
	
	public void testCorrectlyRejectsBadDataDuringPayloadTypePeek()
	{
		ByteBuffer b = null;
		try
		{
			b = ByteBuffer.wrap(new byte[] { (byte)0x80 } );
			
			assertEquals(b.position(), 0, "incorrect start position.");
			assertEquals(b.remaining(), 1, "incorrect remaining.");
			assertEquals(b.limit(), 1, "incorrect limit.");

			RTCPPacket.peekPayloadType( b );
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Invalid packet length - too short to peek type.", "wrong validation message");
			
			assertEquals(b.position(), 0, "incorrect start position after peek.");
			assertEquals(b.remaining(), 1, "incorrect remaining after peek.");
			assertEquals(b.limit(), 1, "incorrect limit after peek.");
		}
		try
		{
			b = ByteBuffer.wrap(new byte[] { (byte)0x80, (byte)0xE9, 0x00, 0x08, 0x00, 0x00, 0x00, 0x08 } );
			
			assertEquals(b.position(), 0, "incorrect start position.");
			assertEquals(b.remaining(), 8, "incorrect remaining.");
			assertEquals(b.limit(), 8, "incorrect limit.");

			RTCPPacket.peekPayloadType( b );
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Unknown type - 233", "wrong validation message");
			
			assertEquals(b.position(), 0, "incorrect start position after peek.");
			assertEquals(b.remaining(), 8, "incorrect remaining after peek.");
			assertEquals(b.limit(), 8, "incorrect limit after peek.");
		}
	}
	
	
	public void testCanCorrectlyExtractLengthFromValidData()
	{
		// valid RR RTCP packet
		final byte[] data = { (byte)0x80, (byte)0xC9, (byte)0xFF, (byte)0xFF, 0x00, 0x00, (byte)0xDD, 0x00 };
		final ByteBuffer b = ByteBuffer.wrap(data);

		assertEquals(b.position(), 0, "incorrect start position.");
		assertEquals(b.remaining(), 8, "incorrect remaining.");
		assertEquals(b.limit(), 8, "incorrect limit.");
		
		assertEquals(RTCPPacket.peekStatedLength(b), 0xFFFFL, "incorrect packet length");

		assertEquals(b.position(), 0, "incorrect start position after peek.");
		assertEquals(b.remaining(), 8, "incorrect remaining after peek.");
		assertEquals(b.limit(), 8, "incorrect limit after peek.");

	}
	
	
	public void testCorrectlyRejectsBadDataDuringLengthPeek()
	{
		ByteBuffer b = null;
		try
		{
			b = ByteBuffer.wrap(new byte[] { (byte)0x80 } );
			
			assertEquals(b.position(), 0, "incorrect start position.");
			assertEquals(b.remaining(), 1, "incorrect remaining.");
			assertEquals(b.limit(), 1, "incorrect limit.");

			RTCPPacket.peekStatedLength( b );
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Invalid packet length - too short to peek stated length.", "wrong validation message");

			assertEquals(b.position(), 0, "incorrect start position after peek.");
			assertEquals(b.remaining(), 1, "incorrect remaining after peek.");
			assertEquals(b.limit(), 1, "incorrect limit after peek.");
		}
	}

}
