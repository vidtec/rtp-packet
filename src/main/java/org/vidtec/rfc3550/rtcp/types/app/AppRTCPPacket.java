package org.vidtec.rfc3550.rtcp.types.app;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.vidtec.rfc3550.rtcp.types.RTCPPacket;

/**
 * An implementation of an RTCP app-related packet type according to RFC 3550 section 6.7.
 * https://tools.ietf.org/html/rfc3550
 */
public class AppRTCPPacket extends RTCPPacket<AppRTCPPacket>
{
	
	// RTCP Bye Packet (BYE) format is defined as: (per RFC 3550, section 6.6)
	//
	//    0                   1                   2                   3
	//    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//   |V=2|P| subtype |   PT=APP=204  |             length            |
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//   |                           SSRC/CSRC                           |
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//   |                          name (ASCII)                         |
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//   |                   application-dependent data                ...
	//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//
	
	/** The minimum header length. */
	private static final int MIN_HEAD_LENGTH = 12;
	
	
	/** The sub-type of the APP message. */
	private final short subType;
	
	/** The SSRC for the APP message. */
	private final long ssrc;
	
	/** The APP name. */
	private final String name;
	
	/** The APP data. */
	private final byte[] appData;
	
	
	/**
	 * Create a (APP) App RTCP packet from a builder.
	 * NB: This constructor will validate the packet data is valid as per RFC 3550.
	 * 
	 * @param builder The builder instance to construct a packet from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	private AppRTCPPacket(final AppBuilder builder)
	throws IllegalArgumentException
	{
		super(PayloadType.APP);

		if (builder.subType > 31)
		{
			throw new IllegalArgumentException("subtype cannot be > 31");
		}
		
		if (builder.name != null && !builder.name.isEmpty() && builder.name.getBytes(Charset.forName("ascii")).length != 4)
		{
			throw new IllegalArgumentException("name must be exactly 4 bytes long");
		}
		
		if (builder.data != null && builder.data.length > 65523)
		{
			throw new IllegalArgumentException("app-specific data cannot be > 65523");
		}
		
		this.subType = builder.subType;
		this.ssrc = builder.ssrcIdentifier;
		this.name = builder.name == null || builder.name.isEmpty() ? "    " : builder.name;
		this.appData = builder.data == null ? new byte[] {} : builder.data;
	}

	
	/**
	 * Create a (APP) App RTCP packet from values.
	 * 
	 * @param subType The APP sub type.
	 * @param ssrcIdentifier The sender SSRC identifier.
	 * @param name The APP name.
	 * @param data The application specific data.
	 */
	private AppRTCPPacket(final short subType, final long ssrcIdentifier, final String name, final byte[] data)
	{
		super(PayloadType.APP);
		
		this.subType = subType;
		this.ssrc = ssrcIdentifier;
		this.name = name;
		this.appData = data;
	}
	
	
	/**
	 * Gets the sub-type identifier element.
	 * 
	 * @return The sub-type identifier for this packet.
	 */
	public short subType() 
	{
		return subType;
	}	

	
	/**
	 * Gets the ssrc identifier element.
	 * 
	 * @return The SSRC identifier for this packet.
	 */
	public long ssrc() 
	{
		return ssrc;
	}

	
	/**
	 * Indicates the APP name.
	 * 
	 * @return The name string (max 4 bytes in ASCII encoding).
	 */
	public String name()
	{
		return name;
	}
	
	
	/**
	 * The app-specific data in this packet.
	 * 
	 * @return The app-specific data, will be empty [] if none specified
	 */
	public byte[] data()
	{
		return Arrays.copyOf(appData, appData.length);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int packetLength() 
	{
		return MIN_HEAD_LENGTH + appData.length;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override	
	public byte[] asByteArray()
	{
		final byte[] data = new byte[packetLength()];
		final ByteBuffer bb = ByteBuffer.wrap(data);
		
		bb.put((byte)(VERSION << 6 | (0x1F & subType) ));
		bb.put((byte)(0xFF & payloadType().pt));
		bb.putShort((short)data.length);

		bb.putInt((int)(0xFFFFFFFFL & ssrc));
		bb.put(name.getBytes(Charset.forName("ASCII")));
		bb.put(appData);
		
		return data;
	}
	
	
	/**
	 * Returns an RTCP packet derived from a given byte array.
	 * 
	 * @param data The byte[] to construct a packet from.
	 * 
	 * @throws IllegalArgumentException If there is a problem with the validity of the packet.
	 */
	public static AppRTCPPacket fromByteArray(final byte[] data)
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

		// Get the sub-type
		final short subType = (short)(0x1F & firstByte);
		
		// Ensure that there is no padding - should not be present on this packet !!
		if ((0x20 & firstByte) == 0x20)
		{
			throw new IllegalArgumentException("APP packet should never be padded, malformed packet found");
		}
		
		try
		{
			// Check the payload type.
			PayloadType.fromTypeValue(0xFF & bb.get());
		}
		catch (IllegalArgumentException e)
		{
			// Wrong payload type.
			throw new IllegalArgumentException("Invalid or unexpected packet type - should be " + PayloadType.APP.pt);
		}
		
		// Get the length, and validate.
		final int length = 0xFFFF & bb.getShort();
		if (bb.remaining() + 4 != length)
		{
			// Invalid packet length
			throw new IllegalArgumentException("Packet states " + length + " bytes length, but actual length is " + (bb.remaining() + 4));
		}

		// Get the ssrc
		final long ssrc = 0xFFFFFFFFL & bb.getInt();
		
		// Get the name.
		final byte[] buffer = new byte[4];
		bb.get(buffer);
		final String name = new String(buffer, Charset.forName("ascii"));
		
		// Get app data
		final byte[] appdata = new byte[bb.remaining()];
		bb.get(appdata);
		
		return new AppRTCPPacket(subType, ssrc, name, appdata);
	}

	
	/**
	 * Creates a builder to manually build an {@link AppRTCPPacket}.
	 * 
	 * @return The builder instance.
	 */
	public static AppBuilder builder() 
	{
		return new AppBuilder();
	}
	
	
	/**
	 * A AppBuilder class to build {@link AppRTCPPacket} instances.
	 */
	public static final class AppBuilder 
	{
		private short subType;
		private long ssrcIdentifier;
		private String name = "";
		private byte[] data;
		
		/**
		 * Private constructor.
		 */
		private AppBuilder() { /* Empty Constructor */ }

		
		/**
		 * This packet should have it's app specific parts.
		 * 
		 * @param subType The packet sub-type max 5 bits - i.e. 0 - 31
		 * @param name The name string (max 4 chars[ASCII] ).
		 * @return The builder instance.
		 */
		public AppBuilder withAppFields(final int subType, final String name) 
		{
			this.subType = (short)(0xFF & subType);
			this.name = name;
			return this;
		}
		

		/**
		 * This packet should have a ssrc identifier.
		 * 
		 * @param ssrcs The ssrc identifier.
		 * @return The builder instance.
		 */
		public AppBuilder withSsrc(final long ssrc)
		{
			this.ssrcIdentifier = ssrc;
			return this;
		}

		
		/**
		 * This packet may have app-specific data.
		 * 
		 * @param data The data as a byte[].
		 * @return The builder instance.
		 */
		public AppBuilder withData(final byte[] data)
		{
			this.data = data != null ? Arrays.copyOf(data, data.length) : null;
			return this;
		}
		
		
		/**
		 * Build the packet.
		 * 
		 * @return The packet instance.
		 * 
		 * @throws IllegalArgumentException If there is a problem with the supplied packet data.
		 */
		public AppRTCPPacket build() 
		throws IllegalArgumentException
		{
			return new AppRTCPPacket(this);
		}
	}
	
}
