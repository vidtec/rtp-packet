package org.vidtec.rfc3550.rtcp.types;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vidtec.rfc3550.rtcp.types.ReportRTCPPacket.ReportBlock;

/**
 * An implementation of an RTCP bye-related packet type according to RFC 3550 section 6.6.
 * https://tools.ietf.org/html/rfc3550
 */
public class ByeRTCPPacket extends RTCPPacket<ByeRTCPPacket>
{
	
	// RTCP Bye Packet (BYE) format is defined as: (per RFC 3550, section 6.6)
	//
	//        0                   1                   2                   3
	//        0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	//       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//       |V=2|P|    SC   |   PT=BYE=203  |             length            |
	//       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//       |                           SSRC/CSRC                           |
	//       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//       :                              ...                              :
	//       +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	// (opt) |     length    |               reason for leaving            ...
	//       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

	
	/** The minimum header length. */
	private static final int MIN_HEAD_LENGTH = 4;
	
	
	/** The SSRCs for the BYE message. */
	private final long[] ssrcs;
	
	/** The reason for leaving. */
	private final String reason;
	
	
	/**
	 * Create a (RR) Receiver Report RTCP packet from a builder.
	 * NB: This constructor will validate the packet data is valid as per RFC 3550.
	 * 
	 * @param builder The builder instance to construct a packet from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	private ByeRTCPPacket(final ByeBuilder builder)
	throws IllegalArgumentException
	{
		super(PayloadType.BYE);

		// Add in the ssrc identifiers (or create an empty array - valid but pointless).
		this.ssrcs = builder.ssrcIdentifiers == null ? new long[0] : builder.ssrcIdentifiers;
		
		if (builder.reason != null && builder.reason.getBytes(Charset.forName("utf-8")).length > 255)
		{
			// message is too long.
			throw new IllegalArgumentException("'reason' message is too long, max is 255 bytes, but saw - " + builder.reason.getBytes(Charset.forName("utf-8")).length);
		}
		
		// Add the reason, empty string if not set.
		this.reason = builder.reason == null ? "" : builder.reason;
	}

	
	/**
	 * Create a (RR) Receiver Report RTCP packet from values.
	 * NB: This constructor will validate the packet data is valid as per RFC 3550.
* FIXME	 * 
	 * @param ssrcIdentifier The sender SSRC identifier.
	 * @param blocks The list of report blocks.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	private ByeRTCPPacket(final long ssrcIdentifier, final List<ReportBlock> blocks)
	{
		super(PayloadType.BYE);
		ssrcs = null;
		reason = null;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int packetLength() 
	{
// FIXMW
		return MIN_HEAD_LENGTH + (ssrcs == null ? 0 : 4 * ssrcs.length); // + optional
		
//		return MIN_HEAD_LENGTH + blocks().stream().flatMapToInt(block -> IntStream.of(block.length())).sum();
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override	
	public byte[] asByteArray()
	{
		final byte[] data = new byte[packetLength()];
		final ByteBuffer bb = ByteBuffer.wrap(data);
		
		bb.put((byte)(VERSION << 6 | (0x1F & (ssrcs == null ? 0 : ssrcs.length)) ));
		bb.put((byte)(0xFF & payloadType().pt));
		bb.putShort((short)data.length);

		Arrays.stream(ssrcs).forEach(ssrc -> bb.putInt((int)(0xFFFFFFFFL & ssrc)));

// FIXME string reason
		
		return data;
	}
	
	
	/**
	 * Returns an RTCP packet derived from a given byte array.
	 * 
	 * @param data The byte[] to construct a packet from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	public static ByeRTCPPacket fromByteArray(final byte[] data)
	throws IllegalArgumentException
	{
		final ByteBuffer bb = ByteBuffer.wrap(data);

		if (bb.remaining() < MIN_HEAD_LENGTH)
		{
			// As per RFC 3550 - the header is 4 bytes, there must be data - anything less is a bad packet.
			throw new IllegalArgumentException("Packet too short, expecting at least " + MIN_HEAD_LENGTH + " bytes, but found " + bb.remaining());
		}

		final short firstByte = (short)(0xFF & bb.get());

// FIXME string
		// Get the block count
		final short blockCount = (short)(0x1F & firstByte);
		if (bb.remaining() < MIN_HEAD_LENGTH - 1 + blockCount * 4)
		{
			// Amount of data left is less than number of report blocks
			throw new IllegalArgumentException("Packet states " + blockCount + " report blocks, so expecting length " + (MIN_HEAD_LENGTH + (blockCount * ReportBlock.BLOCK_SIZE)) + ", but only found " + (bb.remaining() + 1) + " bytes.");
		}
		
		// Ensure that there is no padding - should not be present on this packet !!
		if ((0x20 & firstByte) == 0x20)
		{
			throw new IllegalArgumentException("BYE packet should never be padded, malformed packet found");
		}
		
		try
		{
			// Check the payload type.
			PayloadType.fromTypeValue(0xFF & bb.get());
		}
		catch (IllegalArgumentException e)
		{
			// Wrong payload type.
			throw new IllegalArgumentException("Invalid or unexpected packet type - should be " + PayloadType.BYE.pt);
		}
		
		// Get the length, and validate.
		final int length = 0xFFFF & bb.getShort();
		if (bb.remaining() + 4 != length)
		{
			// Invalid packet length
			throw new IllegalArgumentException("Packet states " + length + " bytes length, but actual length is " + (bb.remaining() + 4));
		}
		
		// Get the sender SSRC
		final long sssrc = 0xFFFFFFFFL & bb.getInt();
		

		// Get the blocks
//		final List<ReportBlock> blocks = new ArrayList<>();
//		final byte[] buffer = new byte[ReportBlock.BLOCK_SIZE];
//		for (int i = 0 ; i < blockCount ; i++)
//		{
//			bb.get(buffer);
//			blocks.add(ReportBlock.fromByteArray(buffer));
//		}
		
		return new ByeRTCPPacket(sssrc, new ArrayList<>());
	}

	
	/**
	 * Creates a builder to manually build an {@link ByeRTCPPacket}.
	 * 
	 * @return The builder instance.
	 */
	public static ByeBuilder builder() 
	{
		return new ByeBuilder();
	}
	
	
	/**
	 * A ReceiverReportBuilder class to build {@link ByeRTCPPacket} instances.
	 */
	public static final class ByeBuilder 
	{
		private long[] ssrcIdentifiers;
		private String reason = "";
		
		/**
		 * Private constructor.
		 */
		private ByeBuilder() { /* Empty Constructor */ }

		
		/**
		 * This packet may have an reason for leaving.
		 * 
		 * @param ssrc The reason string (max 255 chars).
		 * @return The builder instance.
		 */
		public ByeBuilder withReason(final String reason) 
		{
			this.reason = reason;
			return this;
		}
		

		/**
		 * This packet should have ssrc identifiers.
		 * 
		 * @param ssrcs The ssrc identifiers.
		 * @return The builder instance.
		 */
		public ByeBuilder withwSsrcs(final long ... ssrcs)
		{
			this.ssrcIdentifiers = ssrcs;
			return this;
		}

		
		/**
		 * Build the packet.
		 * 
		 * @return The packet instance.
		 * 
		 * @throws IllegalArgumentException If there is a problem with the supplied packet data.
		 */
		public ByeRTCPPacket build() 
		{
			return new ByeRTCPPacket(this);
		}
	}
	
}
