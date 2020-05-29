package org.vidtec.rfc3550.rtcp.types.sdes;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.testng.annotations.Test;
import org.vidtec.rfc3550.rtcp.types.sdes.SdesItem.ItemType;

@Test
public class ItemTypeTest 
{

	public void testHasValidTypes()
	{
		final ItemType[] types = ItemType.values();
		assertEquals(types.length, 9, "expected 7 types");
		assertEquals(types[0], ItemType.CNAME, "expected CNAME type");
		assertEquals(types[1], ItemType.NAME, "expected NAME type");
		assertEquals(types[2], ItemType.EMAIL, "expected EMAIL type");
		assertEquals(types[3], ItemType.PHONE, "expected PHONE type");
		assertEquals(types[4], ItemType.LOC, "expected LOC type");
		assertEquals(types[5], ItemType.TOOL, "expected TOOL type");
		assertEquals(types[6], ItemType.NOTE, "expected NOTE type");
		assertEquals(types[7], ItemType.PRIV, "expected PRIV type");
		assertEquals(types[8], ItemType.TERM, "expected TERM type");

		assertEquals(types[0].type, 1, "expected 1");
		assertEquals(types[1].type, 2, "expected 2");
		assertEquals(types[2].type, 3, "expected 3");
		assertEquals(types[3].type, 4, "expected 4");
		assertEquals(types[4].type, 5, "expected 5");
		assertEquals(types[5].type, 6, "expected 6");
		assertEquals(types[6].type, 7, "expected 7");
		assertEquals(types[7].type, 8, "expected 8");
		assertEquals(types[8].type, 0, "expected 0");
	}

	
	public void testCanLookupTypeFromValueCorrectly()
	{
		// test validation
		try
		{
			ItemType.fromTypeValue(20);
			fail("expected to fail with excetypeion");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Unknown type - 20", "incorrect message");
		}
		try
		{
			ItemType.fromTypeValue(-1);
			fail("expected to fail with excetypeion");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Unknown type - -1", "incorrect message");
		}
		
		assertEquals(ItemType.fromTypeValue(1), ItemType.CNAME, "expected CNAME type");
		assertEquals(ItemType.fromTypeValue(2), ItemType.NAME, "expected NAME type");
		assertEquals(ItemType.fromTypeValue(3), ItemType.EMAIL, "expected EMAIL type");
		assertEquals(ItemType.fromTypeValue(4), ItemType.PHONE, "expected PHONE type");
		assertEquals(ItemType.fromTypeValue(5), ItemType.LOC, "expected LOC type");
		assertEquals(ItemType.fromTypeValue(6), ItemType.TOOL, "expected TOOL type");
		assertEquals(ItemType.fromTypeValue(7), ItemType.NOTE, "expected NOTE type");
		assertEquals(ItemType.fromTypeValue(8), ItemType.PRIV, "expected PRIV type");
		assertEquals(ItemType.fromTypeValue(0), ItemType.TERM, "expected TERM type");
	}
}
