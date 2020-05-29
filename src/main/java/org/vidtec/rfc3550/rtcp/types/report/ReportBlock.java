package org.vidtec.rfc3550.rtcp.types.report;

import java.nio.ByteBuffer;

import org.vidtec.rfc3550.rtcp.stats.TransmissionStatistics;

/**
 * A RTCP report block class.
 * This definition is common to SR and RR packet types.
 */
public final class ReportBlock
{
	// RTCP Report block format is defined as: (per RFC 3550, section 6.4.2)
	// https://tools.ietf.org/html/rfc3550#section-6.4.2
	//
	//    0                   1                   2                   3
	//    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    //    +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
    //    |                 SSRC_1 (SSRC of first source)                 |
	//    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//    | fraction lost |       cumulative number of packets lost       |
	//    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//    |           extended highest sequence number received           |
	//    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//    |                      interarrival jitter                      |
	//    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//    |                         last SR (LSR)                         |
	//    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//    |                   delay since last SR (DLSR)                  |
	//    +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+

	
	/** The block size in bytes. */
	public static final int BLOCK_SIZE = 4 * 6;

	
	/** The synchronisation source identifier (SSRC). */
	private final long ssrcIdentifier;

	/** The fraction lost. */
	private final short fractionLost;
	
	/** The count of cumulative lost packets. */
	private final long cumulativeLost;
	
	/** The highest sequence number seen. */
	private final long extendedHighestSequenceNumber;
	
	/** The number of out of order packets arriving. */
	private final long interarrivalJitter;
	
	/** The last send receipt timestamp. */
	private final long lastSR;
	
	/** The delay since last send receipt. */
	private final long dlSR;

	
	/**
	 * Create a ReportBlock from a builder.
	 * 
	 * @param builder The builder instance to construct a packet from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	private ReportBlock(final Builder builder) 
	{
		if (builder.ssrcIdentifier > 0xFFFFFFFFL || builder.ssrcIdentifier < 0)
		{
			throw new IllegalArgumentException("Expected valid SSRC value.");
		}
		if (builder.extendedHighestSequenceNumber > 0xFFFFFFFFL || builder.extendedHighestSequenceNumber < 0)
		{
			throw new IllegalArgumentException("Expected valid extended sequence number.");
		}
		if (builder.fractionLost > 0xFF || builder.cumulativeLost > 0xFFFFFF || builder.interarrivalJitter > 0xFFFFFFFFL)
		{
			throw new IllegalArgumentException("Expected valid packet statistics values.");
		}			
		if (builder.fractionLost < 0 || builder.cumulativeLost < 0 || builder.interarrivalJitter < 0)
		{
			throw new IllegalArgumentException("Expected valid packet statistics values.");
		}
		if (builder.lastSR > 0xFFFFFFFFL || builder.lastSR < 0)
		{
			throw new IllegalArgumentException("Expected valid sender report values.");
		}
		if (builder.dlSR > 0xFFFFFFFFL || builder.dlSR < 0)
		{
			throw new IllegalArgumentException("Expected valid sender report values.");
		}
		
		ssrcIdentifier = builder.ssrcIdentifier;
		fractionLost = builder.fractionLost;
		cumulativeLost = builder.cumulativeLost;
		extendedHighestSequenceNumber = builder.extendedHighestSequenceNumber;
		interarrivalJitter = builder.interarrivalJitter;
		lastSR = builder.lastSR;
		dlSR = builder.dlSR;
	}
	

	/**
	 * Create a ReportBlock from a given byte array.
     * NB: This constructor will validate the packet data is valid as per RFC 3550.
 	 * 
	 * @param data The byte[] to construct a report block from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the block.
	 */
	private ReportBlock(final byte[] data)
	throws IllegalArgumentException
	{
		if (data == null)
		{
			throw new IllegalArgumentException("data cannot be null");
		}
		
		final ByteBuffer bb = ByteBuffer.wrap(data);

		if (bb.remaining() != BLOCK_SIZE)
		{
			// As per RFC 3550 - the report block is 24 bytes, so anything less is a bad block.
			throw new IllegalArgumentException("report block was wrong size, expecting " + BLOCK_SIZE + " bytes, but found " + bb.remaining());
		}

		// SSRC id is bytes 0-3
		ssrcIdentifier = 0xFFFFFFFF & bb.getInt();
		
		long secondWord = 0xFFFFFFFF & bb.getInt();
		
		// Set fraction lost
		fractionLost = (short)(secondWord >> 24 & 0xFF);
					
		// set cumulative lost
		cumulativeLost = 0xFFFFFF & secondWord;
		
		// Set the extended max sequence number.
		extendedHighestSequenceNumber = 0xFFFFFFFF & bb.getInt();
		
		// Set the inter-arrival jitter.
		interarrivalJitter = 0xFFFFFFFF & bb.getInt();
		
		// Set the last send report ts.
		lastSR = 0xFFFFFFFF & bb.getInt();

		// Set delay since the last send report.
		dlSR = 0xFFFFFFFF & bb.getInt();
	}


	/**
	 * The SSRC identifier for this block.
	 * 
	 * @return The SSRC identifier.
	 */
	public long ssrcIdentifier() 
	{
		return ssrcIdentifier;
	}


	/**
	 * Get the fraction of lost packets.
	 * 
	 * @return The fraction of lost packets.
	 */
	public short fractionLost() 
	{
		return fractionLost;
	}


	/**
	 * Get the total number of lost packets during transmission.
	 * NB: Due to packet restrictions, this value will never exceed 0xFFFFFF
	 * 
	 * @return The total lost packet count.
	 */
	public long cumulativeLost() 
	{
		return cumulativeLost;
	}


	/**
	 * Get the highest sequence number seen - extended to 32bit int over normal 16-bit int value.
	 * 
	 * @return The highest sequence number seen.
	 */
	public long extendedHighestSequenceNumber() 
	{
		return extendedHighestSequenceNumber;
	}


	/**
	 * Get the number of packets that have arrived out of order.
	 * 
	 * @return The out-of-order packet (jitter) count.
	 */
	public long interarrivalJitter() 
	{
		return interarrivalJitter;
	}


	/**
	 * Get the timestamp of the last sender report.
	 * 
	 * @return The last SR timestamp.
	 */
	public long lastSR() 
	{
		return lastSR;
	}


	/**
	 * Get the delay since last SR.
	 * 
	 * @return The delay value.
	 */
	public long dlSR() 
	{
		return dlSR;
	}

	
	/**
	 * Get the block length.
	 * 
	 * @return The length of this report block.
	 */
	public int length()
	{
		return BLOCK_SIZE;
	}
	
	
	/**
	 * Gets the block as a byte[].
	 * 
	 * @return The block represented as raw bytes.
	 */
	public byte[] asByteArray()
	{
		final byte[] data = new byte[BLOCK_SIZE];
		final ByteBuffer bb = ByteBuffer.wrap(data);
		
		bb.putInt((int)ssrcIdentifier);
		bb.putInt((int)(fractionLost << 24 | cumulativeLost));
		bb.putInt((int)extendedHighestSequenceNumber);
		bb.putInt((int)interarrivalJitter);
		bb.putInt((int)lastSR);
		bb.putInt((int)dlSR);
		
		return data;
	}
	
	
	/**
	 * Returns a ReportBlock derived from a given byte array.
	 * 
	 * @param data The byte[] to construct a block from.
	 * @return The generated block.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the block.
	 */
	public static ReportBlock fromByteArray(final byte[] data)
	throws IllegalArgumentException
	{
		return new ReportBlock(data);
	}
	

	/**
	 * Creates a builder to manually build an {@link ReportBlock}.
	 * 
	 * @return The builder instance.
	 */
	public static ReportBlock.Builder builder() 
	{
		return new Builder();
	}

	
	/**
	 * A Builder class to build {@link ReportBlock} instances.
	 */
	public static final class Builder 
	{
		private long ssrcIdentifier = -1;
		private short fractionLost = -1;
		private long cumulativeLost = -1;
		private long extendedHighestSequenceNumber = -1;
		private long interarrivalJitter = -1;
		private long lastSR = -1;
		private long dlSR = -1;

		/**
		 * Private constructor.
		 */
		private Builder() { /* Empty Constructor */ }


		/**
		 * This block should have an ssrc identifier.
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
		 * This packet should have an extended seq, number.
		 * @param extendedHighestSequenceNumber The extended sequence number.
		 * 
		 * @return The builder instance.
		 */
		public Builder withExtendedSequenceNumber(long extendedHighestSequenceNumber) 
		{
			this.extendedHighestSequenceNumber = extendedHighestSequenceNumber;
			return this;
		}
		

		/**
		 * This block should have report statistics
		 * 
		 * @param fractionLost The fraction of lost packets.
		 * @param cumLost The total lost packets count.
		 * @param jitter The jitter packet count.
		 * @return The builder instance.
		 */
		public Builder withStatistics(final int fractionLost, final long cumLost, final long jitter)
		{
			this.fractionLost =  (short) (0xFFFF & fractionLost);
			this.cumulativeLost = cumLost;
			this.interarrivalJitter = jitter;
			return this;
		}
		

		/**
		 * This block should have report statistics.
		 * 
		 * @param stats The statistics object get data from.
		 * @return The builder instance.
		 */
		public Builder withStatistics(final TransmissionStatistics stats)
		{
			this.fractionLost = stats.fractionLost();
			this.cumulativeLost = Math.min(0xFFFFFFL, stats.lost());	// clamp of 24-bit as per spec (no rollover).

this.interarrivalJitter = 0;
//FIXME

			this.extendedHighestSequenceNumber = stats.maxExtendedSequenceNumber();
			return this;
		}
		
		
		/**
		 * This packet needs sender report data.
		 * 
		 * @param lastSR The last sender report timestamp.
		 * @param delaySinceLastSR The delay since the last sender report.
		 * @return The builder instance.
		 */
		public Builder withSenderReportData(final long lastSR, final long delaySinceLastSR)
		{
			this.lastSR = lastSR;
			this.dlSR = delaySinceLastSR;
			return this;
		}
		
		
		/**
		 * Build the report block.
		 * 
		 * @return The report block instance.
		 * 
		 * @throws IllegalArgumentException If there is a problem with the supplied block data.
		 */
		public ReportBlock build() 
		throws IllegalArgumentException
		{
			return new ReportBlock(this);
		}
	}
}