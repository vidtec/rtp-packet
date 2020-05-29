package org.vidtec.rfc3550.rtcp.types.sdes;

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
    //

	
	/** The synchronisation/client source identifier (SSRC). */
	//private final long ssrcIdentifier;

	
	
	
	
	
	
	
	
	

	/**
	 * The SSRC identifier for this chunk.
	 * 
	 * @return The SSRC identifier.
	 */
	public long ssrcIdentifier() 
	{
		return -1;
	//	return ssrcIdentifier;
	}

	
	
	
}
