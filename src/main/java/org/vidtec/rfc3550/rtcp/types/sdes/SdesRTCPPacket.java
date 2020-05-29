package org.vidtec.rfc3550.rtcp.types.sdes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.vidtec.rfc3550.rtcp.types.RTCPPacket;
import org.vidtec.rfc3550.rtcp.types.report.ReportBlock;

/**
 * An implementation of an RTCP sdes-related packet type according to RFC 3550 section 6.5.
 * https://tools.ietf.org/html/rfc3550
 */
public class SdesRTCPPacket extends RTCPPacket<SdesRTCPPacket>
{

	//         0                   1                   2                   3
	//         0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	// header |V=2|P|    SC   |  PT=SDES=202  |             length            |
	//        +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	// chunk  |                          SSRC/CSRC_1                          |
	//   1    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//        |                           SDES items                          |
	//        |                              ...                              |
	//        +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	// chunk  |                          SSRC/CSRC_2                          |
	// 2      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//        |                           SDES items                          |
	//        |                              ...                              |
	//        +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	
	
	/** The minimum header length. */
	private static final int MIN_HEAD_LENGTH = 4;
	

	/** The report blocks in this packet. */
	private final List<ReportBlock> chunks = new ArrayList<>();

	
	/**
	 * Create a (SDES)  RTCP packet from a builder.
	 * NB: This constructor will validate the packet data is valid as per RFC 3550.
	 * 
	 * @param builder The builder instance to construct a packet from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	private SdesRTCPPacket(final SdesBuilder builder)
	throws IllegalArgumentException
	{
		super(PayloadType.SDES);
		
		this.chunks.addAll(builder.chunks == null ? Collections.emptyList() : builder.chunks);
	}
	
	
	/**
	 * Create a SDES RTCP packet.
	 * 
	 * @param chunks The list of chunks.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	private SdesRTCPPacket(final List<ReportBlock> chunks)
	throws IllegalArgumentException
	{
		super(PayloadType.SDES);

		if (chunks.size() > 31) // 0x1F
		{
			throw new IllegalArgumentException("maximum report chunk size exceeded, expected at most 31, but was " + chunks.size());
		}
		
		this.chunks.addAll(chunks);
	}
	
	
	/**
	 * Indicates if this packet has chunks.
	 * 
	 * @return true if chunks are present, false otherwise.
	 */
	public boolean hasChunks() 
	{
		return !chunks.isEmpty();
	}
	

	/**
	 * Gets the number of chunk elements.
	 * 
	 * @return An integer count from 0 - 31.
	 */
	public short chunkCount() 
	{
		return (short) chunks.size();
	}
	
	
	/**
	 * Gets the chunks in this packet.
	 * 
	 * @return The chunks.
	 */
	public List<ReportBlock> chunks()
	{
		return Collections.unmodifiableList(chunks);
	}	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int packetLength() 
	{
		return MIN_HEAD_LENGTH + chunks().stream().flatMapToInt(block -> IntStream.of(block.length())).sum();
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override	
	public byte[] asByteArray()
	{
		final byte[] data = new byte[packetLength()];
		final ByteBuffer bb = ByteBuffer.wrap(data);
		
		bb.put((byte)(VERSION << 6 | (0x1F & chunks().size()) ));
		bb.put((byte)(0xFF & payloadType().pt));
		bb.putShort((short)data.length);
		
		chunks().forEach(chunk -> bb.put(chunk.asByteArray()));
		
		return data;
	}
	
	
	/**
	 * Returns an RTCP packet derived from a given byte array.
	 * 
	 * @param data The byte[] to construct a packet from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	public static SdesRTCPPacket fromByteArray(final byte[] data)
	throws IllegalArgumentException
	{
		if (data == null)
		{
			throw new IllegalArgumentException("packet data cannot be null");
		}
		
		final ByteBuffer bb = ByteBuffer.wrap(data);

		if (bb.remaining() < MIN_HEAD_LENGTH)
		{
			// As per RFC 3550 - the header is 4 bytes, there must be data - anything less is a bad packet.
			throw new IllegalArgumentException("Packet too short, expecting at least " + MIN_HEAD_LENGTH + " bytes, but found " + bb.remaining());
		}

		final short firstByte = (short)(0xFF & bb.get());

		// Get the chunk count
		final short chunkCount = (short)(0x1F & firstByte);
		if (bb.remaining() < MIN_HEAD_LENGTH - 1 + chunkCount * ReportBlock.BLOCK_SIZE)
		{
			// Amount of data left is less than number of report blocks
			throw new IllegalArgumentException("Packet states " + chunkCount + " chunks, so expecting length " + (MIN_HEAD_LENGTH + (chunkCount * ReportBlock.BLOCK_SIZE)) + ", but only found " + (bb.remaining() + 1) + " bytes.");
		}
		
		// Ensure that there is no padding - should not be present on this packet !!
		if ((0x20 & firstByte) == 0x20)
		{
			throw new IllegalArgumentException("SDES packet should never be padded, malformed packet found");
		}
		
		try
		{
			// Check the payload type.
			PayloadType.fromTypeValue(0xFF & bb.get());
		}
		catch (IllegalArgumentException e)
		{
			// Wrong payload type.
			throw new IllegalArgumentException("Invalid or unexpected packet type - should be " + PayloadType.SDES.pt);
		}
		
		// Get the length, and validate.
		final int length = 0xFFFF & bb.getShort();
		if (bb.remaining() + 4 != length)
		{
			// Invalid packet length
			throw new IllegalArgumentException("Packet states " + length + " bytes length, but actual length is " + (bb.remaining() + 4));
		}


		// Get the blocks
		final List<ReportBlock> chunks = new ArrayList<>();
		final byte[] buffer = new byte[ReportBlock.BLOCK_SIZE];
		for (int i = 0 ; i < chunkCount ; i++)
		{
			bb.get(buffer);
			chunks.add(ReportBlock.fromByteArray(buffer));
		}
		
		return new SdesRTCPPacket(chunks);
	}

	
	/**
	 * Creates a builder to manually build an {@link SdesRTCPPacket}.
	 * 
	 * @return The builder instance.
	 */
	public static SdesBuilder builder() 
	{
		return new SdesBuilder();
	}
	
	
	/**
	 * A SdesBuilder class to build {@link SdesRTCPPacket} instances.
	 */
	public static final class SdesBuilder 
	{
		private List<ReportBlock> chunks = new ArrayList<>();

		
		/**
		 * Private constructor.
		 */
		private SdesBuilder() { /* Empty Constructor */ }


		/**
		 * This packet should have chunks.
		 * 
		 * @param chunks A variable args set of chunks.
		 * @return The builder instance.
		 */
		public SdesBuilder withChunks(final ReportBlock ... chunks)
		{
			if (chunks != null)
			{
				this.chunks.addAll(Arrays.asList(chunks));
			}
			
			return this;
		}

		
		/**
		 * This packet should have chunks.
		 * 
		 * @param blocks A list of chunks.
		 * @return The builder instance.
		 */
		public SdesBuilder withChunks(final List<ReportBlock> chunks)
		{
			if (chunks != null)
			{
				this.chunks.addAll(chunks);
			}
			
			return this;
		}

		
		/**
		 * Build the packet.
		 * 
		 * @return The packet instance.
		 * 
		 * @throws IllegalArgumentException If there is a problem with the supplied packet data.
		 */
		public SdesRTCPPacket build() 
		throws IllegalArgumentException
		{
			return new SdesRTCPPacket(this);
		}
	}
	
	
}
