package org.vidtech.rfc3550.rtcp;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.testng.annotations.Test;
import org.vidtech.rfc3550.rtcp.RTCPPacket.PayloadType;

@Test
public class PayloadTypeTest 
{

	public void testHasValidTypes()
	{
		final PayloadType[] types = PayloadType.values();
		assertEquals(types.length, 5, "expected 5 types");
		assertEquals(types[0], PayloadType.SR, "expected SR type");
		assertEquals(types[1], PayloadType.RR, "expected RR type");
		assertEquals(types[2], PayloadType.SDES, "expected SDES type");
		assertEquals(types[3], PayloadType.BYE, "expected BYE type");
		assertEquals(types[4], PayloadType.APP, "expected APP type");

		assertEquals(types[0].pt, 200, "expected 200");
		assertEquals(types[1].pt, 201, "expected 201");
		assertEquals(types[2].pt, 202, "expected 202");
		assertEquals(types[3].pt, 203, "expected 203");
		assertEquals(types[4].pt, 204, "expected 204");
	}

	
	public void testCanLookupTypeFromValueCorrectly()
	{
		// test validation
		try
		{
			PayloadType.fromTypeValue(20);
			fail("expected to fail iwth exception");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Unknown type - 20", "incorrect message");
		}
		try
		{
			PayloadType.fromTypeValue(-1);
			fail("expected to fail iwth exception");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Unknown type - -1", "incorrect message");
		}
		
		assertEquals(PayloadType.fromTypeValue(200), PayloadType.SR, "expected SR type");
		assertEquals(PayloadType.fromTypeValue(201), PayloadType.RR, "expected RR type");
		assertEquals(PayloadType.fromTypeValue(202), PayloadType.SDES, "expected SDES type");
		assertEquals(PayloadType.fromTypeValue(203), PayloadType.BYE, "expected BYE type");
		assertEquals(PayloadType.fromTypeValue(204), PayloadType.APP, "expected APP type");
	}
}
