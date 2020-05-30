package org.vidtec.rfc3550.rtcp.types.sdes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.vidtec.rfc3550.rtcp.types.sdes.SdesItem.ItemType;

/**
 * A RTCP chunk class.
 * This definition specific to the SDES packet type.
 */
public final class Chunk 
{

	//         0                   1                   2                   3
	//         0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	//        +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	// chunk  |                          SSRC/CSRC_1                          |
	//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//        |                           SDES items                          |
	//        |                              ...                              |
    // repeat
	//  ...

	
	/** The synchronisation/client source identifier (SSRC). */
	private final long ssrcIdentifier;

	/** The list of items in the chunk. */
	private final List<SdesItem> items = new ArrayList<>();
	
	
	
	
	
	
	
	
	
	/**
	 * Create a chunk from a builder.
	 * 
	 * @param builder The builder instance to create a chunk from.
	 * 
	 * @throws IllegalArgumentException If there is an issue validating the builder data.
	 */
	private Chunk(final Builder builder) 
	throws IllegalArgumentException
	{
		if (builder.ssrcIdentifier > 0xFFFFFFFFL || builder.ssrcIdentifier < 0)
		{
			throw new IllegalArgumentException("Invalid ssrc value");
		}
		
		this.ssrcIdentifier = builder.ssrcIdentifier;
		this.items.addAll(builder.items);
	}


	/**
	 * Create a chunk from internal values.
	 * 
	 * @param ssrc The SSRC identifier.
	 * @param items The list of SDES items.
	 */
	private Chunk(final long ssrc, final List<SdesItem> items) 
	{
		this.ssrcIdentifier = ssrc;
		this.items.addAll(items);
	}


	/**
	 * The SSRC identifier for this chunk.
	 * 
	 * @return The SSRC identifier.
	 */
	public long ssrcIdentifier() 
	{
		return ssrcIdentifier;
	}

	
	/**
	 * Determine if this chunk has any items.
	 * 
	 * @return true if items are present, false otherwise.
	 */
	public boolean hasItems()
	{
		return !items.isEmpty();
	}
	
	
	/**
	 * Get the items in this chunk.
	 * 
	 * @return The list of SDES item entries.
	 */
	public List<SdesItem> items()
	{
		return Collections.unmodifiableList(items);
	}
	
	
	/**
	 * Get the items in this chunk of a specific type.
	 * 
	 * @param filter The item type to filter by.
	 * @return The list of SDES item entries.
	 */
	public List<SdesItem> items(final ItemType filter)
	{
		return items.stream().filter(f -> f.itemType() == filter).collect(Collectors.toList());
	}
	

	/**
	 * Return the full length of the chunk in bytes.
	 * 
	 * @return The number of bytes required for this chunk.
	 */
	public int chunkLength() 
	{
		// length is 4 bytes plus either 4 for terminating nulls
		return 4 + SdesItems.byteLength(items);
	}
	
	
	/**
	 * Gets the chunk data as a byte[].
	 * 
	 * @return The SDES chunk data.
	 */
	public byte[] asByteArray() 
	{
		final byte[] data = new byte[chunkLength()];
		final ByteBuffer bb = ByteBuffer.wrap(data);
		
		bb.putInt((int)(0xFFFFFFFFL & ssrcIdentifier));
		bb.put(SdesItems.toByteArray(items));
		
		return data;
	}

	
	/**
	 * Returns a Chunk derived from a given byte[].
	 * 
	 * @param data The byte[] to construct a chunk from.
	 * @return The generated chunk.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the chunk.
	 */
	public static Chunk fromByteArray(final byte[] data)
	throws IllegalArgumentException
	{	
		if (data == null)
		{
			throw new IllegalArgumentException("data cannot be null");
		}
	
		return fromByteBuffer(ByteBuffer.wrap(data));
	}	
	
	
	/**
	 * Returns a Chunk derived from a given bytebuffer.
	 * 
	 * @param data The bytebuffer to construct a chunk from.
	 * @return The generated chunk.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the chunk.
	 */
	public static Chunk fromByteBuffer(final ByteBuffer bb)
	throws IllegalArgumentException
	{	
		if (bb == null)
		{
			throw new IllegalArgumentException("data cannot be null");
		}
	
		if (bb.remaining() < 8)
		{
			// As per RFC 3550 - item must be at least 8 bytes - ssrc plus 4 bytes (null term or tiny cname).
			throw new IllegalArgumentException("chunk was wrong size, expecting at least 8 bytes, but found " + bb.remaining());
		}
	
		// SSRC is 4 bytes.
		final long ssrc = 0xFFFFFFFFL & bb.getInt();
		
		// Now pull out all of the items for this chunk,
		final List<SdesItem> items = SdesItems.fromByteBuffer(bb);
		
		return new Chunk(ssrc, items);
	}
	

	/**
	 * Creates a builder to manually build an {@link Chunk}.
	 * 
	 * @return The builder instance.
	 */
	public static Chunk.Builder builder() 
	{
		return new Builder();
	}

	
	/**
	 * A Builder class to build {@link Chunk} instances.
	 */
	public static final class Builder 
	{
		private long ssrcIdentifier = -1;
		private ArrayList<SdesItem> items = new ArrayList<>();

		/**
		 * Private constructor.
		 */
		private Builder() { /* Empty Constructor */ }


		/**
		 * This chunk should have an ssrc identifier.
		 * 
		 * @param ssrc The ssrc identifier.
		 * @return The builder instance.
		 */
		public Builder withSsrc(final long ssrc) 
		{
			this.ssrcIdentifier = ssrc;
			return this;
		}
		

		/**
		 * This chunk needs SDES items.
		 * 
		 * @param itesm The The items to include in the chunk.
		 * @return The builder instance.
		 */
		public Builder withItems(final SdesItem ... items)
		{
			this.items.addAll(items == null ? Collections.emptyList() : Arrays.asList(items));
			return this;
		}
		
		
		/**
		 * Build the chunk.
		 * 
		 * @return The chunk instance.
		 * 
		 * @throws IllegalArgumentException If there is a problem with the supplied chunk data.
		 */
		public Chunk build() 
		throws IllegalArgumentException
		{
			return new Chunk(this);
		}
	}
	
	
}
