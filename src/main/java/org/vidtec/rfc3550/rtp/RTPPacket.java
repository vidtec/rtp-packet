package org.vidtec.rfc3550.rtp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/**
 * An implementation of an RTP packet according to RFC 3550/.
 * https://tools.ietf.org/html/rfc3550
 * 
 * This class also supports the header extension.
 */
public class RTPPacket implements Comparable<RTPPacket>
{
	
	// RTP Packet format is defined as: (per RFC 3550, section 5.1)
	//
	//    0                   1                   2                   3
	//    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//   |V=2|P|X|  CC   |M|     PT      |       sequence number         |
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//   |                           timestamp                           |
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//   |           synchronization source (SSRC) identifier            |
	//   +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	//   |            contributing source (CSRC) identifiers             |
	//   |                             ....                              |
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

	//   Optional extension header - if X flag == true: (as per RFC 3550, section 5.3.1)
	//
	//    0                   1                   2                   3
	//    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//   |      defined by profile       |           length              |
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//   |                        header extension                       |
	//   |                             ....                              |
	
	// NB: Maximum possible size of the full header is
	//   = 4 + 4 (timestamp) + 4(ssrc) + 15 * 4(csrc) + 4(estension top) + 0xFFFF(extension)
	//   = 12 + 60 + 4 + 0xFFFF
	//   = 76 + 0xFFFF
	//   = 65611 (0x1004B) bytes

	
	/** The RTP version constant. */
	public static final short VERSION = 2;

	
	/** The number of padding bytes in this packet. */
	private final short paddingBytes;
	
	/** The flag indicating if this packet has an extension header. */
	private final boolean hasExtension;

	/** The number of contributing source records in this packet. */
	private final short csrcCount;

	/** The flag that indicates if this packet has a market set. */
	private final boolean hasMarker;
	
	/** The payload type for this packet (e.g. as set in RFC 3551). */
	private final short payloadType;

	/** The packet sequence number (16-bit). */
	private final int sequenceNumber;

	/** The packet timestamp (32-bit). */
	private final long timestamp;
	
	/** The synchronisation source identifier (SSRC). */
	private final long ssrcIdentifier;
	
	
	/** The contributing source identifiers (CSRC)s. */
	private final long[] csrcIdentifiers;
	

	/** The extension profile number (16-bit). */
	private final int extensionProfile;
	
	/** The extension header length (16-bit). */
	private final int extensionLength;
	
	/** The extension header data. */
	private final byte[] extensionHeader;
	

	/** The payload length (EXCLUDING any padding data). */
	private final short payloadLength;

	/** The actual packet payload. */
	private final byte[] payload;


	/**
	 * Create an RTP packet from a builder.
	 * NB: This constructor will validate the packet data is valid as per RFC 3550.
	 * 
	 * @param builder The builder instance to construct a packet from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	private RTPPacket(final Builder builder) 
	{
		hasMarker = builder.hasMarker;
		
		if (builder.payloadType > 127 || builder.payloadType < 0)
		{
			throw new IllegalArgumentException("Expected valid payload type not " + builder.payloadType);
		}
		payloadType = (short)builder.payloadType;
		
		if (builder.sequenceNumber > 0xFFFF || builder.sequenceNumber < 0)
		{
			throw new IllegalArgumentException("Expected valid sequence number not " + builder.sequenceNumber);
		}
		sequenceNumber = builder.sequenceNumber;
				
		if (builder.timestamp > 0xFFFFFFFFL || builder.timestamp < 0)
		{
			throw new IllegalArgumentException("Expected valid timestamp not " + builder.timestamp);
		}
		timestamp = builder.timestamp;
				
		if (builder.ssrcIdentifier > 0xFFFFFFFFL || builder.ssrcIdentifier < 0)
		{
			throw new IllegalArgumentException("Expected valid ssrcIdentifier not " + builder.ssrcIdentifier);
		}
		ssrcIdentifier = builder.ssrcIdentifier;
		
		if (builder.csrcIdentifiers != null)
		{
			if (builder.csrcIdentifiers.length > 15)
			{
				throw new IllegalArgumentException("Expected valid ccsrc identifiers count not " + builder.csrcIdentifiers.length);
			}
			csrcIdentifiers = builder.csrcIdentifiers;
			csrcCount = (short)builder.csrcIdentifiers.length;
		}
		else
		{
			csrcCount = 0;
			csrcIdentifiers = new long[0];
		}
		
		if (builder.hasExtension)
		{
			if (builder.extensionHeader == null)
			{
				throw new IllegalArgumentException("Expected valid header not null");
			}
			if (builder.extensionHeader.length > 0xFFFF)
			{
				throw new IllegalArgumentException("Expected valid header length not " + builder.extensionHeader.length);
			}
			if (builder.extensionProfile > 0xFFFF || builder.extensionProfile < 0)
			{
				throw new IllegalArgumentException("Expected valid extension profile not " + builder.extensionProfile);
			}
			
			hasExtension = true;
			extensionProfile = builder.extensionProfile;
			extensionHeader = builder.extensionHeader;
			extensionLength = builder.extensionHeader.length;
		}
		else
		{
			// No header extension.
			hasExtension = false;
			extensionProfile = -1;
			extensionHeader = new byte[0];
			extensionLength = -1;
		}
		
		if (builder.payload == null || builder.payload.length == 0)
		{
			throw new IllegalArgumentException("Expected valid payload not null or empty");
		}
		
		payload = builder.payload;
		payloadLength = (short)builder.payload.length;
		paddingBytes = builder.paddingBytes;
	}
	
	
	/**
	 * Create an RTP packet from a given byte array.
	 * NB: This constructor will validate the packet data is valid as per RFC 3550.
	 * 
	 * @param data The byte[] to construct a packet from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	private RTPPacket(final byte[] data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("packet data cannot be null");
		}
		
		final ByteBuffer bb = ByteBuffer.wrap(data);

		if (bb.remaining() < 13)
		{
			// As per RFC 3550 - the header is 12 bytes, there must be data - anything less is a bad packet.
			throw new IllegalArgumentException("Packet too short, expecting at least 13 bytes, but found " + bb.remaining());
		}

		// Handle and unpack the 1st byte.
		int firstByte = bb.get();
		
		// First check the version number.
		if ((firstByte & 0xC0) != VERSION << 6)
		{
			// This is not a valid version number.
			throw new IllegalArgumentException("Invalid version number found, expecting " + VERSION);
		}

		// If padding flag is set, then get padding byte count, otherwise set 0.
		paddingBytes = ((firstByte & 0x20) == 0x20) ? bb.get(bb.limit() - 1) : 0;
		
		// Set the extension header if flag set.
		hasExtension = ((firstByte & 0x10) == 0x10) ? true : false;
		
		// Set number of csrc 
		csrcCount = (short)(firstByte & 0x0F);
		
		
		// Handle and unpack the 2nd byte.
		int secondByte = bb.get();
		
		// Set the marker header if flag set.
		hasMarker = ((secondByte & 0x80) == 0x80) ? true : false;
		
		// Set the payload type.
		payloadType = (short)(secondByte & 0x7F);
		
		// Sequence Number is bytes 3-4
		sequenceNumber = 0xFFFF & bb.getShort();
		
		// Timestamp is bytes 5-8
		timestamp = 0xFFFFFFFF & bb.getInt();
		
		// SSRC id is bytes 9-12
		ssrcIdentifier = 0xFFFFFFFF & bb.getInt();
		
		if (bb.remaining() < csrcCount * 4 + 1)
		{
			// As per RFC 3550 - each csrc is 4 bytes, there must be data - anything less is a bad packet.
			throw new IllegalArgumentException("Packet too short, expecting at least " + (csrcCount * 4 + 1) + " bytes, but found " + bb.remaining());
		}
		
		// CSRCs follow ...
		csrcIdentifiers = new long[csrcCount];
		for (int i = 0 ; i < csrcCount ; i++)
		{
			csrcIdentifiers[i] = 0xFFFFFFFF & bb.getInt();
		}
		
		if (hasExtension)
		{
			if (bb.remaining() < 4 + 1)
			{
				// As per RFC 3550 - extn desc is 4 min bytes, there must be data - anything less is a bad packet.
				throw new IllegalArgumentException("Packet too short, expecting at least " + (4 + 1) + " bytes, but found " + bb.remaining());
			}
			
			// handle header extension parts.
			extensionProfile = 0xFFFF & bb.getShort();
			extensionLength = 0xFFFF & bb.getShort();
			
			if (bb.remaining() < extensionLength + 1)
			{
				// As per RFC 3550 - extn header is extensionLength bytes, there must be data - anything less is a bad packet.
				throw new IllegalArgumentException("Packet too short, expecting at least " + (extensionLength + 1) + " bytes, but found " + bb.remaining());
			}
			
			extensionHeader = new byte[extensionLength];
			bb.get(extensionHeader);
		}
		else
		{
			// no extension.
			extensionProfile = -1;
			extensionLength = -1;
			extensionHeader = new byte[0];
		}
		
		// handle payload length and payload
		bb.limit(bb.capacity() - paddingBytes);
		payloadLength = (short)bb.remaining();

		payload = new byte[payloadLength];
		bb.get(payload);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() 
	{
		return Objects.hash(sequenceNumber);
	}


	/**
	 * A packet is deemed equal if it has the same sequence number.
	 * timestamp and payload data are NOT inspected.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) 
	{
		if (obj == null || !(obj instanceof RTPPacket))
		{
			return false;
		}
		
		return this.sequenceNumber == ((RTPPacket)obj).sequenceNumber;
	}


	/**
	 * Packets are compared based on sequence number alone.
	 * There is a special case where a sequence number rolls over.
	 *   ie.  max-1 max 0
	 *   
	 * This comparison WILL NOT handle that case.
	 * 
	 * {@inheritDoc}
	 */
	public int compareTo(final RTPPacket o) 
	{
		if (o == null)
		{
			throw new NullPointerException("Unexpect null in compareTo");
		}

		return this.sequenceNumber - o.sequenceNumber;
	}


	/**
	 * A packet cannot be cloned, it makes no sense as it has no mutable state.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException 
	{
		throw new CloneNotSupportedException();
	}


	/**
	 * Get the RTP protocol version - should be 2 as per RFC 3550.
	 * 
	 * @return The RTP protocol version.
	 */
	public short version() 
	{
		return VERSION;
	}

	
	/**
	 * Indicates if this packet is padded.
	 * 
	 * @return true if padded, false otherwise.
	 */
	public boolean isPadded() 
	{
		return paddingBytes > 0;
	}
	
	
	/**
	 * Gets the number of padding bytes.
	 * 
	 * @return The number of padding bytes used, 0 indicates no padding.
	 */
	public short paddedBytesCount() 
	{
		return paddingBytes;
	}	


	/**
	 * Indicates if this packet has an extension header.
	 * 
	 * @return true if extended, false otherwise.
	 */
	public boolean hasExtension() 
	{
		return hasExtension;
	}

	
	/**
	 * Indicates if this packet has contributing sources.
	 * 
	 * @return true if contributing sources present, false otherwise.
	 */
	public boolean hasCsrcs() 
	{
		return csrcCount > 0;
	}
	

	/**
	 * Gets the number of contributing source elements.
	 * 
	 * @return An integer count from 0 - 15.
	 */
	public short csrcCount() 
	{
		return csrcCount;
	}
	

	/**
	 * Indicates if the packets has a marker set.
	 * 
	 * @return true if marker set, false otherwise.
	 */
	public boolean hasMarker() 
	{
		return hasMarker;
	}
	

	/**
	 * Gets the payload type.
	 * 
	 * @return The payload type from 0 - 127.
	 */
	public short payloadType() 
	{
		return payloadType;
	}

	 
	/**
	 * Gets the packet sequence number.
	 * 
	 * @return The packet sequence number ranging from 0 - 65,536 (16-bit integer).
	 */
	public int sequenceNumber() 
	{
		return sequenceNumber;
	}
	

	/**
	 * Gets the packet timestamp (exact format as per RFC 3550 is per clock rate needed).
	 * 
	 * @return The packet timestamp as a 32-bit unsigned integer.
	 */
	public long timestamp() 
	{
		return timestamp;
	}

	
	/**
	 * Gets the sync. source identifier.
	 * 
	 * @return The ssrc identifier as a 32 bit unsigned integer.
	 */
	public long ssrcIdentifier()
	{
		return ssrcIdentifier;
	}
	
	
	/**
	 * Get the set of corresponding source identifiers as a long[].
	 * 
	 * @return The array of csrc identifiers matching the csrc count.
	 */
	public long[] csrcIdentifiers()
	{
		return hasCsrcs() ? Arrays.copyOf(csrcIdentifiers, csrcCount) : new long[0];
	}
	
	
	/**
	 * Get the header extension profile (if extension is present).
	 * 
	 * @return The extension profile, or -1 if no extension is present.
	 */
	public int extensionProfile()
	{
		return hasExtension() ? extensionProfile : -1;
	}

	
	/**
	 * Get the header extension length (if extension is present).
	 * 
	 * @return The extension length, or -1 if no extension is present.
	 */
	public int extensionLength()
	{
		return hasExtension() ? extensionLength : -1;
	}
	
	
	/**
	 * Get the header extension data (if extension is present).
	 * 
	 * @return The extension data, or empty byte[] if no extension is present.
	 */
	public byte[] extensionHeaderAsByteArray()
	{
		return hasExtension() ? Arrays.copyOf(extensionHeader, extensionLength) : new byte[0];
	}

	
	/**
	 * Gets the payload length WITHOUT PADDING.
	 * NB: If the payload length with padding is required, see payloadLengthRaw().
	 * NB: This is the normal usage, as you want padding removed in most cases.
	 * 
	 * @return The payload length - padding byte count.
	 */
	public int payloadLength() 
	{
		return payloadLength;
	}
	
	
	/**
	 * Gets the payload length WITH PADDING.
	 * NB: If the payload length without padding is required, see payloadLength().
	 * 
	 * @return The payload length - padding byte count.
	 */
	public int payloadLengthRaw() 
	{
		return payloadLength + paddingBytes;
	}
	
	
	/**
	 * Gets the payload data with padding REMOVED as a byte[].
	 * NB: If the payload with padding is required, see payloadRawAsByteArray().
	 * NB: This is the normal usage, as you want padding removed in most cases.
	 * 
	 * @return a copy of the RDP packet payload as a ByteBuffer.
	 */
	public ByteBuffer payloadAsByteBuffer() 
	{
		return ByteBuffer.wrap(Arrays.copyOf(payload, payloadLength));
	}
	
	
	/**
	 * Gets the payload data with padding REMOVED.
	 * NB: If the payload with padding is required, see payloadRawAsByteArray().
	 * NB: This is the normal usage, as you want padding removed in most cases.
	 * 
	 * @return a copy of the RDP packet payload as a byte[].
	 */
	public byte[] payloadAsByteArray() 
	{
		return Arrays.copyOf(payload, payloadLength);
	}
	
	
	/**
	 * Gets the payload data WITH padding as a byte[].
	 * NB: If the payload without padding is required, see payloadAsByteArray().
	 * 
	 * @return a copy of the RDP packet payload as a byte[].
	 */
	public byte[] payloadRawAsByteArray() 
	{
		final byte[] data = Arrays.copyOf(payload, payloadLengthRaw());
		if (isPadded()) 
		{
			data[data.length - 1] = (byte)paddingBytes;
		}
		
		return data;
	}
	
	
	/**
	 * Return the full length of the packet in bytes.
	 * 
	 * @return The number of bytes required for this packet.
	 */
	public int packetLength()
	{
		return 12 + (4 * csrcCount()) + (hasExtension ? 4 + extensionLength : 0) + payloadLengthRaw();
	}
	
	
	/**
	 * Gets the packet data as a byte[].
	 * 
	 * @return a copy of the RDP packet data.
	 */
	public byte[] asByteArray()
	{
		final byte[] data = new byte[packetLength()];
		final ByteBuffer bb = ByteBuffer.wrap(data);
		
		bb.put((byte)(VERSION << 6 | (isPadded() ? 0x20 : 0x00) | (hasExtension() ? 0x10 : 0x00) | csrcCount ));
		bb.put((byte)(hasMarker() ? 0x80 | payloadType : 0x00 | payloadType));
		bb.putShort((short)sequenceNumber);
		bb.putInt((int)timestamp);
		bb.putInt((int)ssrcIdentifier);
		
		for (int i = 0 ; i < csrcCount ; i++)
		{
			bb.putInt((int)csrcIdentifiers[i]);
		}

		if (hasExtension())
		{
			bb.putShort((short)extensionProfile);
			bb.putShort((short)extensionLength);
			bb.put(extensionHeader);
		}
		
		bb.put(payload);
		
		if (isPadded()) 
		{
			data[data.length - 1] = (byte)paddingBytes;
		}
		
		return data;
	}
	
	
	/**
	 * 
	 * @param address The InetAddress that the datagram will be sent to.
	 * @param port The port that the datagram will be sent to.
	 * 
	 * @return A DatagramPacket that is ready to send.
	 */
	public DatagramPacket asDatagramPacket(InetAddress address, int port) 
	{
		return new DatagramPacket(asByteArray(), packetLength(), address, port);
	}
	
	
	/**
	 * Returns an RTP packet derived from a given byte array.
	 * 
	 * @param data The byte[] to construct a packet from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	public static RTPPacket fromByteArray(final byte[] data)
	throws IllegalArgumentException
	{
		return new RTPPacket(data);
	}


	/**
	 * Returns an RTP packet derived from a given DatagramPacket.
	 * 
	 * @param packet DatagramPacket construct a packet from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	public static RTPPacket fromDatagramPacket(final DatagramPacket packet)
	throws IllegalArgumentException
	{
		return fromByteArray(packet.getData());
	}
	

	/**
	 * Creates a builder to manually build an {@link RTPPacket}.
	 * 
	 * @return The builder instance.
	 */
	public static Builder builder() 
	{
		return new Builder();
	}


	/**
	 * A Builder class to build {@link RTPPacket} instances.
	 */
	public static final class Builder 
	{
		private boolean hasMarker;
		private int payloadType = -1;
		private int sequenceNumber = -1;
		private long timestamp = -1;
		private long ssrcIdentifier = -1;

		private long[] csrcIdentifiers;

		private boolean hasExtension;
		private int extensionProfile = -1;
		private byte[] extensionHeader;
		
		private byte[] payload;
		private short paddingBytes = 0;

		
		/**
		 * Private constructor.
		 */
		private Builder() { /* Empty Constructor */ }

		/**
		 * This packet should have a marker set.
		 * 
		 * @return The builder instance.
		 */
		public Builder withMarker() 
		{
			this.hasMarker = true;
			return this;
		}


		/**
		 * This packet should have required header fields set.
		 * 
		 * @param payloadType The payload type.
		 * @param sequenceNumber The sequence number.
		 * @param timestamp The timestamp.
		 * @param ssrc The ssrc identifier.
		 * @return The builder instance.
		 */
		public Builder withRequiredHeaderFields(final int payloadType, final int sequenceNumber, final long timestamp, final long ssrc) 
		{
			this.payloadType = payloadType;
			this.sequenceNumber = sequenceNumber;
			this.timestamp = timestamp;
			this.ssrcIdentifier = ssrc;
			return this;
		}
		
		
		/**
		 * This packet should have csrc identifiers set.
		 * 
		 * @param csrcIdentifiers The csrc identifiers to set.
		 * @return The builder instance.
		 */
		public Builder withCsrcIdentifiers(final long ... csrcIdentifiers) 
		{
			this.csrcIdentifiers = csrcIdentifiers;
			return this;
		}

		
		/**
		 * This packet should have a header extension set.
		 * 
		 * @param extensionProfile The profile.
		 * @param header The header data.
		 * @return The builder instance.
		 */
		public Builder withHeaderExtension(final int extensionProfile, final byte[] header) 
		{
			this.extensionProfile = extensionProfile;
			this.extensionHeader = header == null ? null : Arrays.copyOf(header, header.length);
			this.hasExtension = true;
			return this;
		}

		
		/**
		 * This packet should have a payload set.
		 * 
		 * @param payload The payload data.
		 * @return The builder instance.
		 */
		public Builder withPayload(final byte[] payload) 
		{
			this.payload = payload == null ? null : Arrays.copyOf(payload, payload.length);
			return this;
		}
		

		/**
		 * This packet should have a payload set but be aligned to a given byte boundary
		 * (i.e. padded if needed).
		 * 
		 * @param payload The payload data.
		 * @param alignToBytes The byte alignment boundary.
		 * @return The builder instance.
		 */
		public Builder withPayload(final byte[] payload, final int alignToBytes) 
		{
			if (payload == null)
			{
				throw new IllegalArgumentException("cannot align to boundary of null data.");
			}
			
			this.payload = Arrays.copyOf(payload, payload.length);
			this.paddingBytes = (short)(payload.length % alignToBytes); 
			return this;
		}

		
		/**
		 * Build the packet.
		 * 
		 * @return The packet instance.
		 * 
		 * @throws IllegalArgumentException If there is a problem with the supplied packet data.
		 */
		public RTPPacket build() 
		{
			return new RTPPacket(this);
		}
	}


}
