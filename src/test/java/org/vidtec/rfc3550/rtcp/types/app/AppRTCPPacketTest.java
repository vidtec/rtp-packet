package org.vidtec.rfc3550.rtcp.types.app;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.testng.annotations.Test;
import org.vidtec.rfc3550.rtcp.types.RTCPPacket.PayloadType;

@Test
public class AppRTCPPacketTest 
{

	
	public void testCanCastSelfToConcreteType()
	{
		final AppRTCPPacket r = AppRTCPPacket.builder()
				.withAppFields(0, "ABCD")
				.withSsrc(20)
				.build();
		
		final AppRTCPPacket p = r.asConcreteType();
		assertEquals(p.packetLength(), 12, "incorrect packet length");

	}

	public void testCanCreateEmptyAppPacketFromBuilder()
	{
		final byte[] data = { (byte)0x80, (byte)0xCC, 0x00, 0x0C, 0x00, 0x00, 0x00, 0x00, 0x20, 0x20, 0x20, 0x20 };

		AppRTCPPacket r = AppRTCPPacket.builder()
				.withData(null)
				.build();
		
		assertEquals(r.packetLength(), 12, "incorrect packet length");
		assertTrue(r.is(PayloadType.APP), "incorrect payload type");
		assertTrue(!r.is(PayloadType.SDES), "incorrect payload type");
		assertTrue(!r.is(null), "incorrect payload type");
		assertEquals(r.payloadType(), PayloadType.APP, "incorrect payload type");

		assertTrue(r.name() != null, "incorrect name");
		assertTrue(!r.name().isEmpty(), "incorrect name");

		assertEquals(r.ssrc(), 0, "incorrect ssrc");
		assertEquals(r.name(), "    ", "incorrect name");
		assertEquals(r.subType(), 0, "incorrect subtype");
		assertEquals(r.data(), new byte[] { }, "incorrect data");
		
		assertEquals(r.asByteArray(), data, "packet data not reformed correctly.");
		
		r = AppRTCPPacket.builder()
				.withAppFields(0, null)
				.build();
		
		assertEquals(r.packetLength(), 12, "incorrect packet length");
		assertTrue(r.is(PayloadType.APP), "incorrect payload type");
		assertTrue(!r.is(PayloadType.SDES), "incorrect payload type");
		assertTrue(!r.is(null), "incorrect payload type");
		assertEquals(r.payloadType(), PayloadType.APP, "incorrect payload type");

		assertTrue(r.name() != null, "incorrect name");
		assertTrue(!r.name().isEmpty(), "incorrect name");

		assertEquals(r.ssrc(), 0, "incorrect ssrc");
		assertEquals(r.name(), "    ", "incorrect name");
		assertEquals(r.subType(), 0, "incorrect subtype");
		assertEquals(r.data(), new byte[] { }, "incorrect data");
		
		assertEquals(r.asByteArray(), data, "packet data not reformed correctly.");
	}
	
	
	public void testCanCreateSimpleAppPacketFromBuilder()
	{
		final byte[] data = { (byte)0x81, (byte)0xCC, 0x00, 0x0D, 0x00, 0x00, 0x00, 0x14, 0x30, 0x30, 0x30, 0x31, 0x01 };
	
		AppRTCPPacket r = AppRTCPPacket.builder()
				.withAppFields(1, "0001")
				.withSsrc(20)
				.withData(new byte[] { 0x01 } )
				.build();
		
		assertEquals(r.packetLength(), 13, "incorrect packet length");
		assertTrue(r.is(PayloadType.APP), "incorrect payload type");
		assertTrue(!r.is(PayloadType.SDES), "incorrect payload type");
		assertTrue(!r.is(null), "incorrect payload type");
		assertEquals(r.payloadType(), PayloadType.APP, "incorrect payload type");
	
		assertTrue(r.name() != null, "incorrect name");
		assertTrue(!r.name().isEmpty(), "incorrect name");
	
		assertEquals(r.ssrc(), 20, "incorrect ssrc");
		assertEquals(r.name(), "0001", "incorrect name");
		assertEquals(r.subType(), 1, "incorrect subtype");
		assertEquals(r.data(), new byte[] { 0x01 }, "incorrect data");
		
		assertEquals(r.asByteArray(), data, "packet data not reformed correctly.");
	}

	
	public void testCanValidatesCorrectlyFromBuilder() throws UnknownHostException
	{
		try
		{
			AppRTCPPacket.builder()
					.withAppFields(32, "abcd")
					.withSsrc(20)
					.withData(new byte[] { 0x01 } )
					.build();
			
			fail("Expected error");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "subtype cannot be > 31", "wrong validation message");
		}
		try
		{
			AppRTCPPacket.builder()
					.withAppFields(1, "abcd")
					.withSsrc(20)
					.withData(new byte[65535] )
					.build();
			
			fail("Expected error");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "app-specific data cannot be > 65523", "wrong validation message");
		}
		try
		{
			AppRTCPPacket.builder()
					.withAppFields(1, "ab")
					.withSsrc(20)
					.withData(new byte[] { 0x01 } )
					.build();
			
			fail("Expected error");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "name must be exactly 4 bytes long", "wrong validation message");
		}
		try
		{
			AppRTCPPacket.builder()
					.withAppFields(1, "abcde")
					.withSsrc(20)
					.withData(new byte[] { 0x01 } )
					.build();
			
			fail("Expected error");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "name must be exactly 4 bytes long", "wrong validation message");
		}
	}
	

	public void testCanCreateEmptyAppPacketFromBuilderAtLimits()
	{
		final byte[] data = new byte[65535];
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.put((byte)0x9F);
		bb.put((byte)0xCC);
		bb.putShort((short)0xFFFF);
		bb.putInt((int) 0xFFFFFFFFL);
		bb.putInt((int) 0x30303031L);
		
		final AppRTCPPacket r = AppRTCPPacket.builder()
				.withAppFields(0x1F, "0001")
				.withSsrc(0xFFFFFFFFL)
				.withData(new byte[65523])
				.build();
		

		assertEquals(r.packetLength(), 65535, "incorrect packet length");
		assertTrue(r.is(PayloadType.APP), "incorrect payload type");
		assertTrue(!r.is(PayloadType.SDES), "incorrect payload type");
		assertTrue(!r.is(null), "incorrect payload type");
		assertEquals(r.payloadType(), PayloadType.APP, "incorrect payload type");
	
		assertTrue(r.name() != null, "incorrect name");
		assertTrue(!r.name().isEmpty(), "incorrect name");
	
		assertEquals(r.ssrc(), 0xFFFFFFFFL, "incorrect ssrc");
		assertEquals(r.name(), "0001", "incorrect name");
		assertEquals(r.subType(), 31, "incorrect subtype");
		assertEquals(r.data(), new byte[65523], "incorrect data");		
		
		assertEquals(r.asByteArray(), data, "packet data not reformed correctly.");
	}
		
	

	
	
	public void testCanCreateSimpleAppPacketFromByteArray()
	{
		final byte[] data = { (byte)0x81, (byte)0xCC, 0x00, 0x0E, 0x00, 0x00, 0x00, 0x14, 0x30, 0x30, 0x30, 0x30, 0x01, 0x02 };

		final AppRTCPPacket r = AppRTCPPacket.fromByteArray(data);
		
		assertEquals(r.packetLength(), 14, "incorrect packet length");
		assertTrue(r.is(PayloadType.APP), "incorrect payload type");
		assertTrue(!r.is(PayloadType.SDES), "incorrect payload type");
		assertTrue(!r.is(null), "incorrect payload type");
		assertEquals(r.payloadType(), PayloadType.APP, "incorrect payload type");

		assertTrue(r.name() != null, "incorrect name");
		assertTrue(!r.name().isEmpty(), "incorrect name");

		assertEquals(r.ssrc(), 20, "incorrect ssrc");
		assertEquals(r.name(), "0000", "incorrect name");
		assertEquals(r.subType(), 1, "incorrect subtypr");
		assertEquals(r.data(), new byte[] { 0x01, 0x02 }, "incorrect data");

		assertEquals(r.asByteArray(), data, "packet data not reformed correctly.");
	}

	
	public void testCanCreateSimpleAppPacketFromByteArrayAtLimits()
	{
		final byte[] data = new byte[65535];
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.put((byte)0x9F);
		bb.put((byte)0xCC);
		bb.putShort((short)0xFFFF);
		bb.putInt((int) 0xFFFFFFFFL);
		bb.putInt((int) 0x30303031L);

		final AppRTCPPacket r = AppRTCPPacket.fromByteArray(data);

		assertEquals(r.packetLength(), 65535, "incorrect packet length");
		assertTrue(r.is(PayloadType.APP), "incorrect payload type");
		assertTrue(!r.is(PayloadType.SDES), "incorrect payload type");
		assertTrue(!r.is(null), "incorrect payload type");
		assertEquals(r.payloadType(), PayloadType.APP, "incorrect payload type");
	
		assertTrue(r.name() != null, "incorrect name");
		assertTrue(!r.name().isEmpty(), "incorrect name");
	
		assertEquals(r.ssrc(), 0xFFFFFFFFL, "incorrect ssrc");
		assertEquals(r.name(), "0001", "incorrect name");
		assertEquals(r.subType(), 31, "incorrect subtype");
		assertEquals(r.data(), new byte[65523], "incorrect data");		
		
		assertEquals(r.asByteArray(), data, "packet data not reformed correctly.");
	}
	
	
	public void testCanValidatesCorrectlyFromByteArray() throws UnknownHostException
	{
		try
		{
			AppRTCPPacket.fromByteArray( null );
			fail("Expected error");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "packet data cannot be null", "wrong validation message");
		}
		try
		{
			AppRTCPPacket.fromByteArray(new byte[] { (byte)0x80 } );
			fail("Expected error");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Packet too short, expecting at least 12 bytes, but found 1", "wrong validation message");
		}
		try
		{
			AppRTCPPacket.fromByteArray(new byte[] { (byte)0x80, (byte)0xD9, 0x00, 0x00, 
																0x00, 0x00, 0x00, 0x00, 0x20,0x20,0x20,0x20 } );
			fail("Expected error");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Invalid or unexpected packet type - should be 204", "wrong validation message");
		}
		try
		{
			AppRTCPPacket.fromByteArray(new byte[] { (byte)0x80, (byte)0xC9, 0x00, 0x10, 
																0x00, 0x00, 0x00, 0x00, 0x20,0x20,0x20,0x20 } );
			fail("Expected error");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Packet states 16 bytes length, but actual length is 12", "wrong validation message");
		}
		try
		{
			AppRTCPPacket.fromByteArray(new byte[] { (byte)0xA0, (byte)0xCC, 0x00, 0x0C, 0x00, 0x00, 0x00, 0x14, 0x30, 0x30, 0x30, 0x30 } );
			fail("Expected error");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "APP packet should never be padded, malformed packet found", "wrong validation message");
		}
	} 
	

}
