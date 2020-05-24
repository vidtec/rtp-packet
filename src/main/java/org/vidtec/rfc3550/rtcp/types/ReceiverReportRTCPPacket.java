package org.vidtec.rfc3550.rtcp.types;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * An implementation of an RTCP report-related packet types (SR/RR) according to RFC 3550 section 6.4.2.
 * https://tools.ietf.org/html/rfc3550
 */
public class ReceiverReportRTCPPacket extends ReportRTCPPacket
{
	
	// todo padding
	
	
	// RTCP Receiver Report Packet (RR) format is defined as: (per RFC 3550, section 6.4.2)
	//
	//         0                   1                   2                   3
	//         0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	// header |V=2|P|    RC   |   PT=RR=201   |             length            |
	//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//        |                     SSRC of packet sender                     |
	//        +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	// report |                 SSRC_1 (SSRC of first source)                 |
	// block  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	// 1      | fraction lost |       cumulative number of packets lost       |
	//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//        |           extended highest sequence number received           |
	//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//        |                      interarrival jitter                      |
	//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//        |                         last SR (LSR)                         |
	//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//        |                   delay since last SR (DLSR)                  |
	//        +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	// report |                 SSRC_2 (SSRC of second source)                |
	// block  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	// 2      :                               ...                             :
	//        +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	//        |                  profile-specific extensions                  |
	//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	
	
	
	/** The minimum header length. */
	private static final int MIN_HEAD_LENGTH = 8;
	
	
	/**
	 * Create a (RR) Receiver Report RTCP packet from a builder.
	 * NB: This constructor will validate the packet data is valid as per RFC 3550.
	 * 
	 * @param builder The builder instance to construct a packet from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	private ReceiverReportRTCPPacket(final ReceiverReportBuilder builder)
	throws IllegalArgumentException
	{
		super(PayloadType.RR, builder.ssrcIdentifier, builder.blocks);
	}

	
	/**
	 * Create a (RR) Receiver Report RTCP packet from values.
	 * NB: This constructor will validate the packet data is valid as per RFC 3550.
	 * 
	 * @param ssrcIdentifier The sender SSRC identifier.
	 * @param blocks The list of report blocks.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	private ReceiverReportRTCPPacket(final long ssrcIdentifier, final List<ReportBlock> blocks)
	{
		super(PayloadType.RR, ssrcIdentifier, blocks);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int packetLength() 
	{
		return MIN_HEAD_LENGTH + blocks().stream().flatMapToInt(block -> IntStream.of(block.length())).sum();
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override	
	public byte[] asByteArray()
	{
		final byte[] data = new byte[packetLength()];
		final ByteBuffer bb = ByteBuffer.wrap(data);
		
		bb.put((byte)(VERSION << 6 | (0x1F & blocks().size()) ));
		bb.put((byte)(0xFF & payloadType().pt));
		bb.putShort((short)data.length);
		bb.putInt((int)ssrcSenderIdentifier());
		
		blocks().forEach(block -> bb.put(block.asByteArray()));
		
		return data;
	}
	
	
	/**
	 * Returns an RTCP packet derived from a given byte array.
	 * 
	 * @param data The byte[] to construct a packet from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	public static ReceiverReportRTCPPacket fromByteArray(final byte[] data)
	throws IllegalArgumentException
	{
		final ByteBuffer bb = ByteBuffer.wrap(data);

		if (bb.remaining() < MIN_HEAD_LENGTH)
		{
			// As per RFC 3550 - the header is 12 bytes, there must be data - anything less is a bad packet.
			throw new IllegalArgumentException("Packet too short, expecting at least " + MIN_HEAD_LENGTH + " bytes, but found " + bb.remaining());
		}

		final short firstByte = (short)(0xFF & bb.get());

		// Get the block count
		final short blockCount = (short)(0x1F & firstByte);
		if (bb.remaining() < MIN_HEAD_LENGTH - 1 + blockCount * ReportBlock.BLOCK_SIZE)
		{
			// Amount of data left is less than number of report blocks
			throw new IllegalArgumentException("Packet states " + blockCount + " report blocks, so expecting length " + (MIN_HEAD_LENGTH + (blockCount * ReportBlock.BLOCK_SIZE)) + ", but only found " + (bb.remaining() + 1) + " bytes.");
		}
		
		// Ensure that there is no padding - should not be present on this packet !!
		if ((0x20 & firstByte) == 0x20)
		{
			throw new IllegalArgumentException("RR packet should never be padded, malformed packet found");
		}
		
		try
		{
			// Check the payload type.
			PayloadType.fromTypeValue(0xFF & bb.get());
		}
		catch (IllegalArgumentException e)
		{
			// Wrong payload type.
			throw new IllegalArgumentException("Invalid or unexpected packet type - should be " + PayloadType.RR.pt);
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
		final List<ReportBlock> blocks = new ArrayList<>();
		final byte[] buffer = new byte[ReportBlock.BLOCK_SIZE];
		for (int i = 0 ; i < blockCount ; i++)
		{
			bb.get(buffer);
			blocks.add(ReportBlock.fromByteArray(buffer));
		}
		
		return new ReceiverReportRTCPPacket(sssrc, blocks);
	}

	
	/**
	 * Creates a builder to manually build an {@link ReceiverReportRTCPPacket}.
	 * 
	 * @return The builder instance.
	 */
	public static ReceiverReportBuilder builder() 
	{
		return new ReceiverReportBuilder();
	}
	
	
	/**
	 * A ReceiverReportBuilder class to build {@link ReceiverReportRTCPPacket} instances.
	 */
	public static final class ReceiverReportBuilder 
	{
		private long ssrcIdentifier = -1;
		private List<ReportBlock> blocks = new ArrayList<>();

		
		/**
		 * Private constructor.
		 */
		private ReceiverReportBuilder() { /* Empty Constructor */ }


		/**
		 * This packet should have an ssrc identifier.
		 * 
		 * @param ssrc The ssrc identifier.
		 * @return The builder instance.
		 */
		public ReceiverReportBuilder withSsrc(final long ssrc) 
		{
			this.ssrcIdentifier = ssrc;
			return this;
		}
		

		/**
		 * This packet should have report blocks.
		 * 
		 * @param blocks A variable args set of report blocks.
		 * @return The builder instance.
		 */
		public ReceiverReportBuilder withReportBlocks(final ReportBlock ...blocks)
		{
			if (blocks != null)
			{
				this.blocks.addAll(Arrays.asList(blocks));
			}
			
			return this;
		}

		
		/**
		 * This packet should have report blocks.
		 * 
		 * @param blocks A list of report blocks.
		 * @return The builder instance.
		 */
		public ReceiverReportBuilder withReportBlocks(final List<ReportBlock> blocks)
		{
			if (blocks != null)
			{
				this.blocks.addAll(blocks);
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
		public ReceiverReportRTCPPacket build() 
		{
			return new ReceiverReportRTCPPacket(this);
		}
	}
	
}
