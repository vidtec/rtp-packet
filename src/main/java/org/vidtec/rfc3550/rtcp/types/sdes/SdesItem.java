package org.vidtec.rfc3550.rtcp.types.sdes;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Common functionality for all SDES chunk items.
 */
public final class SdesItem 
{

	//    0                   1                   2                   3
	//    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//   |    TYPE=1-7   |     length    | value                       ...
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//
	// or
	//
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//   |     PRIV=8    |     length    | prefix length |prefix string...
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//   ...             |                  value string               ...
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+	
	//
	

	/** THe item type. */
	private final ItemType type;
	
	/** The item value (max 255 bytes) NB: BYTES not chars. */
	private final String value;
	
	/** The prefix value (max 255 - value.length - 1 bytes) for PRIV items only!  NB: BYTES not chars. */
	private final String prefix;
		
	
	/**
	 * Create an SDES item.
	 * 
	 * @param type The item type for this item.
	 * @param value The item value (max 255 bytes).
     *
	 * @throws IllegalArgumentException If there is a problem with the supplied item data.
	 */
	private SdesItem(final ItemType type, final String value)
	throws IllegalArgumentException
	{
		this(type, value, "");
	}
	
	
	/**
	 * Create an SDES item.
	 * 
	 * @param type The item type for this item.
	 * @param value The item value (max 255 bytes).
	 * @param prefix The prefix value.
     *
	 * @throws IllegalArgumentException If there is a problem with the supplied item data.
	 */
	private SdesItem(final ItemType type, final String value, final String prefix)
	throws IllegalArgumentException
	{
		if (value == null)
		{
			throw new IllegalArgumentException("value cannot be null");
		}
		if (prefix == null)
		{
			throw new IllegalArgumentException("prefix cannot be null");
		}
		
		if (type.equals(ItemType.PRIV))
		{
			final int valLength = value.getBytes(Charset.forName("utf-8")).length; 
			if (valLength > 254)
			{
				throw new IllegalArgumentException("maximum value length is 254 bytes");
			}

			final int prefixLength = prefix.getBytes(Charset.forName("utf-8")).length; 
			if (valLength + 1 + prefixLength > 254)
			{
				throw new IllegalArgumentException("maximum value and prefix length is 254 bytes");
			}
		}
		else
		{
			if (value.getBytes(Charset.forName("utf-8")).length > 255)
			{
				throw new IllegalArgumentException("maximum value length is 255 bytes");
			}
		}
		
		this.type = type;
		this.value = value;
		this.prefix = prefix;
	}


	/**
	 * Check if this item is of a given type.
	 * 
	 * @param type The item type to compare against.
	 * @return true if the types match, false otherwise.
	 */
	public boolean is(final ItemType type)
	{
		return type == null ? false : this.type.equals(type);
	}

	
	/**
	 * Get the SDES item type.
	 * 
	 * @return The item's type.
	 */
	public ItemType itemType()
	{
		return type;
	}
	
	
	/**
	 * Get the SDES item value.
	 * 
	 * @return The item's value.
	 */
	public String value()
	{
		return value;
	}
	
	
	/**
	 * Is this a private item.
	 * 
	 * @return true if the item is private, false otherwise.
	 */
	public boolean isPrivate()
	{
		return is(ItemType.PRIV);
	}
	
	
	/**
	 * Get the SDES item prefix value.
	 * 
	 * @return The item's prefix value.
	 */
	public String prefix()
	{
		return prefix;
	}
	

	/**
	 * Return the full length of the item in bytes.
	 * 
	 * @return The number of bytes required for this item.
	 */
	public int itemLength() 
	{
		return isPrivate() ? 3 + value.getBytes(Charset.forName("utf-8")).length + prefix.getBytes(Charset.forName("utf-8")).length
				           : 2 + value.getBytes(Charset.forName("utf-8")).length;
	}
	
	
	/**
	 * Gets the item data as a byte[].
	 * 
	 * @return a copy of the SDES item data.
	 */
	public byte[] asByteArray() 
	{
		final byte[] valueBytes = value.getBytes(Charset.forName("utf-8"));
		final byte[] prefixBytes = prefix.getBytes(Charset.forName("utf-8"));
		
		final byte[] data = new byte[2 + valueBytes.length + (isPrivate() ? 1 + prefixBytes.length : 0)];
		final ByteBuffer bb = ByteBuffer.wrap(data);
		
		bb.put((byte)(0xFF & itemType().type));

		if (isPrivate())
		{
			bb.put((byte)(0xFF & (1 + prefixBytes.length + valueBytes.length)));
			bb.put((byte)(0xFF & prefixBytes.length));
			
			bb.put(prefixBytes);
			bb.put(valueBytes);
		}
		else
		{
			bb.put((byte)(0xFF & valueBytes.length));
			bb.put(valueBytes);
		}
		
		
		return data;
	}

	
	/**
	 * Returns a SdesItem derived from a given byte[].
	 * 
	 * @param data The byte[] to construct an item from.
	 * @return The generated item.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the item.
	 */
	public static SdesItem fromByteArray(final byte[] data)
	throws IllegalArgumentException
	{	
		if (data == null)
		{
			throw new IllegalArgumentException("data cannot be null");
		}
	
		return fromByteBuffer(ByteBuffer.wrap(data));
	}	
	
	
	/**
	 * Returns a SdesItem derived from a given bytebuffer.
	 * 
	 * @param data The bytebuffer to construct an item from.
	 * @return The generated item.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the item.
	 */
	public static SdesItem fromByteBuffer(final ByteBuffer bb)
	throws IllegalArgumentException
	{	
		if (bb == null)
		{
			throw new IllegalArgumentException("data cannot be null");
		}
	
		if (bb.remaining() < 2)
		{
			// As per RFC 3550 - item must be at least 2 bytes.
			throw new IllegalArgumentException("item was wrong size, expecting at least 2 bytes, but found " + bb.remaining());
		}
	
		// Item type is byte 1
		final ItemType type = ItemType.fromTypeValue(bb.get());
		
		// Length is byte 2
		final int length = 0xFF & bb.get();
		
		if (bb.remaining() < length)
		{
			throw new IllegalArgumentException("data too short for stated length.");
		}
		
		String value = "";
		String prefix = "";
		
		if (type.equals(ItemType.PRIV))
		{
			final int prefixLength = 0xFF & bb.get();

			byte[] buffer = new byte[prefixLength];
			bb.get(buffer);
			prefix = new String(buffer, Charset.forName("utf-8"));	
			
			// value is the string thats left.
			buffer = new byte[length - 1 - prefixLength];
			bb.get(buffer);
			value = new String(buffer, Charset.forName("utf-8"));	
		}
		else
		{
			// value is the string thats left.
			final byte[] buffer = new byte[length];
			bb.get(buffer);
			
			value = new String(buffer, Charset.forName("utf-8"));	
		}
		
		return new SdesItem(type, value, prefix);
	}
	
	
	/**
	 * A helper method to extract the SDES item type from a bytebuffer without
	 * needing to decode the whole packet. 
	 * 
	 * NB: This method expects that the bytebuffer position is at the START of the SDES item.
	 * NB: This method will mark and rewind, so the position of the buffer is not mutated
	 *     as a side-effect of use before full decode.
	 * 
	 * @param buffer The ByteBuffer instance to check.
	 * @return The payload type (if valid)
	 * 
	 * @throws IllegalArgumentException If the there is a problem extracting the item type or it is invalid.
	 */
	public static ItemType peekItemType(final ByteBuffer buffer)
	throws IllegalArgumentException
	{
		buffer.mark();
		try
		{
			if (buffer.remaining() < 2)
			{
				// not enough data in the packet.
				throw new IllegalArgumentException("Invalid item length - too short to peek type.");
			}

			// item type is in the first byte of packet.
			return ItemType.fromTypeValue((0xFF00 & buffer.getShort()) >> 8);
		}
		finally
		{
			buffer.reset();
		}
	}
	
	
	/**
	 * A helper method to extract the SDES item length from a bytebuffer without
	 * needing to decode the whole packet. 
	 * 
	 * NB: This method expects that the bytebuffer position is at the START of the SDES item.
	 * NB: This method will mark and rewind, so the position of the buffer is not mutated
	 *     as a side-effect of use before full decode.
	 * 
	 * @param buffer The ByteBuffer instance to check.
	 * @return The packet length (if valid).
	 * 
	 * @throws IllegalArgumentException If the there is a problem extracting the item length or it is invalid.
	 */
	public static int peekStatedLength(final ByteBuffer buffer)
	throws IllegalArgumentException
	{
		buffer.mark();
		try
		{
			if (buffer.remaining() < 2)
			{
				// not enough data in the packet.
				throw new IllegalArgumentException("Invalid item length - too short to peek stated length.");
			}

			// Stated length is in byte 2 of packet.
			return 0x00FF & buffer.getShort();
		}
		finally
		{
			buffer.reset();
		}		
	}


	/**
	 * An enumeration of SDES item types.
	 * 
	 * 	 Supported item types and values
	 * 
	 *   TERM   0   End of items (not an official item)
	 * 	 CNAME  1   canonical name   
	 * 	 NAME   2   user name        
	 * 	 EMAIL  3   user's electronic mail address  
	 * 	 PHONE  4   user's phone number             
	 * 	 LOC    5   geographic user location        
	 * 	 TOOL   6   name of application or tool  
	 * 	 NOTE   7   notice about the source     
	 * 	 PRIV   8   private extensions  
	 */
	public static enum ItemType
	{
		CNAME(1), NAME(2), EMAIL(3), PHONE(4), LOC(5), TOOL(6), NOTE(7), PRIV(8), TERM(0);
		
		/** The numeric placeholder. */
		public final short type;
		
		/** internal cache of values to enumerations. */
		private static final Map<Integer, ItemType> TYPES = new HashMap<>();
		
		
		static
		{
			Arrays.stream(ItemType.values()).forEach(t -> TYPES.put(Integer.valueOf(t.type), t));
		}
		
		
		/**
		 * Create an enumeration with a numeric value.
		 * 
		 * @param value The item type value as per RFC3550 SDES definition (sections 6.5.x).
		 */
		private ItemType(final int value)
		{
			type = (short)(0xFF & value);
		}
		
		
		/**
		 * Get a item type enumeration from a packet value.
		 * 
		 * @param value The item type value.
		 * @return The corresponding enumeration instance.
		 * 
		 * @throws IllegalArgumentException If the value given is not valid.
		 */
		public static ItemType fromTypeValue(final int value)
		throws IllegalArgumentException
		{
			final ItemType type = TYPES.get(Integer.valueOf(value));
			if (type == null)
			{
				throw new IllegalArgumentException("Unknown type - " + String.valueOf(value));
			}
			
			return type;
		}
	}
	
	
	/** 
	 * Build a CNAME SDES item.
	 * 
	 * @param value The value for this item.
	 * @return The SDESItem instance representing the CNAME.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the supplied item data.
	 */
	public static SdesItem cname(final String value)
	throws IllegalArgumentException
	{
		return new SdesItem(ItemType.CNAME, value);
	}
	
	
	/** 
	 * Build a NAME SDES item.
	 * 
	 * @param value The value for this item.
	 * @return The SDESItem instance representing the NAME.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the supplied item data.
	 */
	public static SdesItem name(final String value)
	throws IllegalArgumentException
	{
		return new SdesItem(ItemType.NAME, value);
	}
	
	
	/** 
	 * Build a EMAIL SDES item.
	 * 
	 * @param value The value for this item.
	 * @return The SDESItem instance representing the EMAIL.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the supplied item data.
	 */
	public static SdesItem email(final String value)
	throws IllegalArgumentException
	{
		return new SdesItem(ItemType.EMAIL, value);
	}
	
	
	/** 
	 * Build a PHONE SDES item.
	 * 
	 * @param value The value for this item.
	 * @return The SDESItem instance representing the PHONE.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the supplied item data.
	 */
	public static SdesItem phone(final String value)
	throws IllegalArgumentException
	{
		return new SdesItem(ItemType.PHONE, value);
	}
	
	
	/** 
	 * Build a LOC SDES item.
	 * 
	 * @param value The value for this item.
	 * @return The SDESItem instance representing the LOC.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the supplied item data.
	 */
	public static SdesItem location(final String value)
	throws IllegalArgumentException
	{
		return new SdesItem(ItemType.LOC, value);
	}
	
	
	/** 
	 * Build a TOOL SDES item.
	 * 
	 * @param value The value for this item.
	 * @return The SDESItem instance representing the TOOL.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the supplied item data.
	 */
	public static SdesItem tool(final String value)
	throws IllegalArgumentException
	{
		return new SdesItem(ItemType.TOOL, value);
	}
	
	
	/** 
	 * Build a NOTE SDES item.
	 * 
	 * @param value The value for this item.
	 * @return The SDESItem instance representing the NOTE.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the supplied item data.
	 */
	public static SdesItem note(final String value)
	throws IllegalArgumentException
	{
		return new SdesItem(ItemType.NOTE, value);
	}
	
	
	/** 
	 * Build a PRIV SDES item.
	 * 
	 * @param value The value for this item.
	 * @param prefix The prefix for this item.
	 * @return The SDESItem instance representing the PRIV.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the supplied item data.
	 */
	public static SdesItem priv(final String value, final String prefix)
	throws IllegalArgumentException
	{
		return new SdesItem(ItemType.PRIV, value, prefix);
	}
	
}
