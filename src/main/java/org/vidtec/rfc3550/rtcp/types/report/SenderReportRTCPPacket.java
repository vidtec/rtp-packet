package org.vidtec.rfc3550.rtcp.types.report;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * An implementation of an RTCP report-related packet types (SR/RR) according to RFC 3550 section 6.4.2.
 * https://tools.ietf.org/html/rfc3550
 */
public class SenderReportRTCPPacket extends ReportRTCPPacket<SenderReportRTCPPacket>
{
	
	// RTCP Sender Report Packet (SR) format is defined as: (per RFC 3550, section 6.4.1)
	//
	//         0                   1                   2                   3
	//         0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	// header |V=2|P|    RC   |   PT=SR=200   |             length            |
	//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//        |                         SSRC of sender                        |
	//        +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	// sender |              NTP timestamp, most significant word             |
	// info   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//        |             NTP timestamp, least significant word             |
	//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//        |                         RTP timestamp                         |
	//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//        |                     sender's packet count                     |
	//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//        |                      sender's octet count                     |
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
	private static final int MIN_HEAD_LENGTH = 28;
	
	
	/** The NTP timestamp at the point this report was sent. */
	private long ntpTimestamp;
	
	/** Equivalent RTP timestamp. */
	private long rtpTimestamp;
	
	/** The number of packets transmitted by the sender. */
	private long packetCount;
	
	/** The number of octets transmitted by the sender. */
	private long octetCount;
	
	
	/**
	 * Create a (SR) Sender Report RTCP packet from a builder.
	 * NB: This constructor will validate the packet data is valid as per RFC 3550.
	 * 
	 * @param builder The builder instance to construct a packet from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	private SenderReportRTCPPacket(final SenderReportBuilder builder)
	{
		super(PayloadType.SR, builder.ssrcIdentifier, builder.blocks);
		
		this.ntpTimestamp = builder.ntpTimestamp;
		this.rtpTimestamp = builder.rtpTimestamp;
		this.packetCount = builder.packetCount;
		this.octetCount = builder.octetCount;
	}

	
	/**
	 * Create a (SR) Sender Report RTCP packet from values.
	 * NB: This constructor will validate the packet data is valid as per RFC 3550.
	 * 
	 * @param ssrcIdentifier The sender SSRC identifier.
	 * @param ntpTimestamp The senders RTP timestamp at report time.
	 * @param rtpTimestamp The senders NTP timestamp at report time.  
	 * @param packetCount The packets sent since last report.
	 * @param octetCount The octets sent since last report.
	 * @param blocks The list of report blocks.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	private SenderReportRTCPPacket(final long ssrcIdentifier, final long ntpTimestamp, final long rtpTimestamp, final long packetCount, final long octetCount, final List<ReportBlock> blocks)
	{
		super(PayloadType.SR, ssrcIdentifier, blocks);
		
		this.ntpTimestamp = ntpTimestamp;
		this.rtpTimestamp = rtpTimestamp;
		this.packetCount = packetCount;
		this.octetCount =  octetCount;
	}
	

	/**
	 * Get the NTP timestamp for this report.
	 * 
	 * @return The NTP timestamp as per RFC 3550.
	 */
	public long ntpTimestamp()
	{
		return ntpTimestamp;
	}
	
	
	/**
	 * Get the RTP timestamp for this report.
	 * 
	 * @return The RTP timestamp as per RFC 3550.
	 */
	public long rtpTimestamp()
	{
		return rtpTimestamp;
	}
	
	
	/**
	 * Get the number of packets sent since last report.
	 * 
	 * @return The packet count.
	 */
	public long packetCount()
	{
		return packetCount;
	}
	

	/**
	 * Get the number of octets (bytes) sent since last report.
	 * 
	 * @return The byte count.
	 */
	public long octetCount()
	{
		return octetCount;
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
		
		bb.putLong(0xFFFFFFFFFFFFFFFFL & ntpTimestamp);
		bb.putInt((int)(0xFFFFFFFFL & rtpTimestamp));
		bb.putInt((int)(0xFFFFFFFFL & packetCount));
		bb.putInt((int)(0xFFFFFFFFL & octetCount));
		
		blocks().forEach(block -> bb.put(block.asByteArray()));
		
		return data;
	}
	
	
	/**
	 * Returns an RTCP packet derived from a given byte array.
	 * 
	 * @param data The byte[] to construct a packet from.
	 * @return The packet instance.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	public static SenderReportRTCPPacket fromByteArray(final byte[] data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("packet data cannot be null");
		}
		
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
			throw new IllegalArgumentException("SR packet should never be padded, malformed packet found");
		}
		
		try
		{
			// Check the payload type.
			PayloadType.fromTypeValue(0xFF & bb.get());
		}
		catch (IllegalArgumentException e)
		{
			// Wrong payload type.
			throw new IllegalArgumentException("Invalid or unexpected packet type - should be " + PayloadType.SR.pt, e);
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
		
		// Get timestamps
		final long ntpTimestamp = bb.getLong();
		final long rtpTimestamp = 0xFFFFFFFFL & bb.getInt();

		// Get counts
		final long packetCount = 0xFFFFFFFFL & bb.getInt();
		final long octetCount = 0xFFFFFFFFL & bb.getInt();
		

		// Get the blocks
		final List<ReportBlock> blocks = new ArrayList<>(blockCount);
		final byte[] buffer = new byte[ReportBlock.BLOCK_SIZE];
		for (int i = 0 ; i < blockCount ; i++)
		{
			bb.get(buffer);
			blocks.add(ReportBlock.fromByteArray(buffer));
		}
		
		return new SenderReportRTCPPacket(sssrc, ntpTimestamp, rtpTimestamp, packetCount, octetCount, blocks);
	}

	
	/**
	 * Creates a builder to manually build an {@link SenderReportRTCPPacket}.
	 * 
	 * @return The builder instance.
	 */
	public static SenderReportBuilder builder() 
	{
		return new SenderReportBuilder();
	}
	
	
	/**
	 * A SenderReportBuilder class to build {@link SenderReportRTCPPacket} instances.
	 */
	public static final class SenderReportBuilder 
	{
		private long ssrcIdentifier = -1;
		private long ntpTimestamp = 0;
		private long rtpTimestamp = -1;
		private long packetCount = -1;
		private long octetCount = -1;
		private List<ReportBlock> blocks = new ArrayList<>();

		
		/**
		 * Private constructor.
		 */
		private SenderReportBuilder() { /* Empty Constructor */ }


		/**
		 * This packet should have an ssrc identifier.
		 * 
		 * @param ssrc The ssrc identifier.
		 * @return The builder instance.
		 */
		public SenderReportBuilder withSsrc(final long ssrc) 
		{
			this.ssrcIdentifier = ssrc;
			return this;
		}
	
		
		/**
		 * This packet should have an ssrc identifier.
		 * 
		 * @param ntpTimestamp The ssrc identifier.
		 * @param rtpTimestamp The ssrc identifier.
		 * @return The builder instance.
		 */
		public SenderReportBuilder withTimestamps(final long ntpTimestamp, final long rtpTimestamp) 
		{
			this.ntpTimestamp = ntpTimestamp;
			this.rtpTimestamp = rtpTimestamp;
			return this;
		}

		
		/**
		 * This packet should have counts.
		 * 
		 * @param packetCount The number of packets sent since last report.
		 * @param octetCount The number of octects (bytes) sent since last report.
		 * @return The builder instance.
		 */
		public SenderReportBuilder withCounts(final long packetCount, final long octetCount) 
		{
			this.packetCount = packetCount;
			this.octetCount = octetCount;
			return this;
		}
		

		/**
		 * This packet should have report blocks.
		 * 
		 * @param blocks A variable args set of report blocks.
		 * @return The builder instance.
		 */
		public SenderReportBuilder withReportBlocks(final ReportBlock ...blocks)
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
		public SenderReportBuilder withReportBlocks(final List<ReportBlock> blocks)
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
		public SenderReportRTCPPacket build() 
		{
			return new SenderReportRTCPPacket(this);
		}
	}
	
}
