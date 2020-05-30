package org.vidtec.rfc3550.rtcp.types.sdes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.vidtec.rfc3550.rtcp.types.sdes.SdesItem.ItemType;

/**
 * A helper class to marshal/un-marshal a collection of SDES items, t
 * taking care of reading/writing terminator nulls correctly on 4-byte boundaries.
 */
final class SdesItems 
{
	
	/**
	 * No instantiations needed.
	 */
	private SdesItems() { /*8 Empty constructor */ }
	
	
	/**
	 * Read a list of SDES item entries from a ByteBuffer.
	 * 
	 * NB: Terminating nulls will be read off the buffer, but NOT added as a list item.
	 * NB: The list order shall be the order in which items were read.
	 * 
	 * @param bb The buffer to read items from.
	 * @return The collection of items, this MAY be empty if only a terminator item is sent.
	 * 
	 * @throws IllegalArgumentException If there is a problem extracting the items from the buffer.
	 */
	public static List<SdesItem> fromByteBuffer(final ByteBuffer bb)
	throws IllegalArgumentException
	{
		final List<SdesItem> items = new ArrayList<>();
		int startPos = bb.position();

		boolean shouldStop = false;
		while (!shouldStop)
		{
			final ItemType nextType = SdesItem.peekItemType(bb);
			if (nextType.equals(ItemType.TERM))
			{
				// there must be enough bytes left in the buffer to align to q 4-byte boundary
				// bytes to read = 4 - ((position - startpos) % 4)
				final int paddingNeeded = 4 - ((bb.position() - startPos) % 4);
				if (bb.remaining() < paddingNeeded)
				{
					throw new IllegalArgumentException("not enough padding bytes, expected " + paddingNeeded + " but found " + bb.remaining());
				}
				
				// Special case, need to read ahead to 4-byte boundary, then stop.
				for (int i = 0 ; i < paddingNeeded ; i++)
				{
					if (bb.get() != 0x00)
					{
						throw new IllegalArgumentException("Expected to read all nulls in terminator.");
					}
				}
				
				shouldStop = true;
				continue;
			}
			
			// This is a normal item, so handle accordingly.
			items.add(SdesItem.fromByteBuffer(bb));
		}
		
		return items;
	}
	
	
	/**
	 * Write a list of SDES item entries to a byte[].
	 * 
	 * NB: Terminating nulls will be written to the array to align to a 4-byte boundary.
	 * NB: The list order shall be the order in which items are written.
	 * NB: There should be NO terminator entry.
	 * 
	 * @param items The collection of items, this MAY be empty, in which case only a terminator block is written.
	 * @return The byte[] containing the data.
	 * 
	 * @throws IllegalArgumentException If there is a problem extracting the items from the buffer.
	 */
	public static byte[] toByteArray(final List<SdesItem> items)
	throws IllegalArgumentException
	{
		if (items == null)
		{
			throw new IllegalArgumentException("items cannot be null");
		}
		
		final int itemsLength = items.stream().flatMapToInt(i -> IntStream.of(i.itemLength())).sum();
		final int requiredNullCount = 4 - (itemsLength % 4);
		final byte[] data = new byte[ itemsLength + requiredNullCount ];
		final ByteBuffer bb = ByteBuffer.wrap(data);
		
		items.forEach(item -> bb.put(item.asByteArray()));
		IntStream.range(0, requiredNullCount).forEach(i -> bb.put((byte) 0x00));
		
		return data;
	}
	
	
	/**
	 * Calculate the byte length of an item list including any null padding required.
	 * 
	 * @param items The list of items.
	 * @return The number of bytes required for the list.
	 * 
	 * @throws IllegalArgumentException If the items list is null.
	 */
	public static int byteLength(final List<SdesItem> items)
	throws IllegalArgumentException
	{
		if (items == null)
		{
			throw new IllegalArgumentException("items cannot be null");
		}
		
		final int itemsLength = items.stream().flatMapToInt(i -> IntStream.of(i.itemLength())).sum();
		final int requiredNullCount = 4 - (itemsLength % 4);
		return itemsLength + requiredNullCount;
	}

}
