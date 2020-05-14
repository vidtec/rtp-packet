package org.vidtech.rfc3550.rtp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

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

	
	/** The RTP version constant. */
	public static final short VERSION = 2;

	
	/** The number of padding bytes in this packet. */
	private short paddingBytes;
	
	/** The flag indicating if this packet has an extension header. */
	private boolean hasExtension;

	/** The number of contributing source records in this packet. */
	private short csrcCount;

	/** The flag that indicates if this packet has a market set. */
	private boolean hasMarker;
	
	/** The payload type for this packet (e.g. as set in RFC 3551). */
	private short payloadType;

	/** The packet sequence number (16-bit). */
	private int sequenceNumber;

	/** The packet timestamp (32-bit). */
	private long timestamp;
	
	/** The synchronisation source identifier (SSRC). */
	private long ssrcIdentifier;
	
	
	/** The contributing source identifiers (CSRC)s. */
	private long[] csrcIdentifiers;
	

	/** The extension profile number (16-bit). */
	private int extensionProfile;
	
	/** The extension header length (16-bit). */
	private int extensionLength;
	
	/** The extension header data. */
	private byte[] extensionHeader;
	

	/** The payload length (EXCLUDING any padding data). */
	private short payloadLength;

	/** The actual packet payload. */
	private byte[] payload;


	
	
	
	
	
	
	
	
	
//	private RTPPacket()
//	{
//		this(VERSION);
//	}
//	
//	private RTPPacket(final short version)
//	{
//		
//	}
	
	
	
	
	
	
	/**
	 * Create an RTP packet from a given byte array.
	 * NB: This constructor will validate the packet data is valid as per RFC 3550.
	 * 
	 * @param data The byte[] to construct a packet from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	public RTPPacket(final byte[] data)
	throws IllegalArgumentException
	{
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

		// If padding flag is set, then get padding byte count.
		if ((firstByte & 0x20) == 0x20)
		{
			paddingBytes = bb.get(bb.limit() - 1);
		}
		
		// Set the extension header if flag set.
		if ((firstByte & 0x10) == 0x10)
		{
			hasExtension = true;
		}
		
		// Set number of csrc 
		csrcCount = (short)(firstByte & 0x0F);
		
		
		// Handle and unpack the 2nd byte.
		int secondByte = bb.get();
		
		// Set the marker hedaer if flag set.
		if ((secondByte & 0x80) == 0x80)
		{
			hasMarker = true;
		}
		
		// Set the payload type.
		payloadType = (short)(secondByte & 0x7F);
		
		// Sequence Number is bytes 3-4
		sequenceNumber = bb.getShort();
		
		// Timestamp is bytes 5-8
		timestamp = bb.getInt();
		
		// SSRC id is bytes 9-12
		ssrcIdentifier = bb.getInt();
		
		if (bb.remaining() < csrcCount * 4 + 1)
		{
			// As per RFC 3550 - each csrc is 4 bytes, there must be data - anything less is a bad packet.
			throw new IllegalArgumentException("Packet too short, expecting at least " + (csrcCount * 4 + 1) + " bytes, but found " + bb.remaining());
		}
		
		// CSRCs follow ...
		csrcIdentifiers = new long[csrcCount];
		for (int i = 0 ; i < csrcCount ; i++)
		{
			csrcIdentifiers[i] = bb.getInt();
		}
		
		if (hasExtension())
		{
// TEST FOR LEN
			
			
			// handle header extension parts.
			extensionProfile = bb.getShort();
			extensionLength = bb.getShort();
			extensionHeader = new byte[extensionLength];
			bb.get(extensionHeader);
		}
		
		// handle payload length and payload
		bb.limit(bb.capacity() - paddingBytes);
		payloadLength = (short)bb.remaining();

		payload = new byte[payloadLength];
		bb.get(payload);
	}
	
	
	


	/**
	 * Packets are compared based on sequence number alone.
	 * There is a special case where a sequence number rolls over.
	 *   ie.  max-1 max 0
	 *   
	 * In this instance, the packets are still deemed to be in correct order.
	 * 
	 * {@inheritDoc}
	 */
	public int compareTo(RTPPacket o) 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	

	
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() 
	{
		return Objects.hash(sequenceNumber, timestamp);
	}


	/**
	 * A packet is deemed equal if it has the same sequence number and timestamp.
	 * payload data is NOT inspected.
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
		
		return this.sequenceNumber == ((RTPPacket)obj).sequenceNumber &&
			   this.timestamp == ((RTPPacket)obj).timestamp;
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
		bb.put((byte)(hasMarker() ? 0x80 : 0x00 | payloadType));
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
	public DatagramPacket asDatagramPacket(final InetAddress address, final int port)
	{
		return new DatagramPacket(asByteArray(), packetLength(), address, port);
	}
//	
//	
//	
//	
//	public void writeTo(DatagramChannel channel)
//	{
//		// nio write
//		
//	}
	
//	public void write(Datagram DatagramChannel)
//	{
//		//DatagramChannel
//	}
	





	/**
	 * Returns an RTP packet derived from a given byte array.
	 * 
	 * @param data The byte[] to construct a packet from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	public static RTPPacket from(final byte[] data)
	throws IllegalArgumentException
	{
		return new RTPPacket(data);
	}






//	public static RTPPacket from(final DatagramChannel channel) 
//	throws IllegalArgumentException
//	{
//		return null;
//	}



}
