package org.vidtech.rfc3550.rtcp.types;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;
import org.vidtech.rfc3550.rtcp.types.ReportRTCPPacket.ReportBlock;

@Test
public class ReportBlockTest 
{

	public void testCanCreateReportBlockFromValidByteArray()
	{
		// valid report block
		final byte[] data = { 0x04, 0x03, 0x02, 0x01, 0x01, 0x03, 0x02, 0x01, 0x05, 0x04, 0x03, 0x02,
						      0x06, 0x03, 0x02, 0x01, 0x07, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00 };
		
		final ReportBlock b = ReportBlock.fromByteArray(data);

		assertEquals(b.ssrcIdentifier(), 0x04030201, "ssrc should be 0x04030201");
		assertEquals(b.fractionLost(), 1, "fraction lost should be 0x01");
		assertEquals(b.cumulativeLost(), 0x030201, "cum lost should be 0x030201");
		assertEquals(b.extendedHighestSequenceNumber(), 0x05040302, "seq no should be 0x05040302");
		assertEquals(b.interarrivalJitter(), 0x06030201, "jitter should be 0x06030201");
		assertEquals(b.lastSR(), 0x07000000, "lsr should be 0x07000000");
		assertEquals(b.dlSR(), 0x08000000, "dlsr should be 0x08000000");

		assertEquals(b.asByteArray(), data, "report block data not reformed correctly.");
	}
	
	public void testCanCreateReportBlockFromValidByteArrayAtLimits()
	{
		// valid report block
		final byte[] data = { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, 
				              (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,  };
		
		final ReportBlock b = ReportBlock.fromByteArray(data);

		assertEquals(b.ssrcIdentifier(), 0xFFFFFFFF, "ssrc should be 0xFFFFFFFF");
		assertEquals(b.fractionLost(), 0xFF, "fraction lost should be 0xFF");
		assertEquals(b.cumulativeLost(), 0xFFFFFF, "cum lost should be 0xFFFFFF");
		assertEquals(b.extendedHighestSequenceNumber(), 0xFFFFFFFF, "seq no should be 0xFFFFFFFF");
		assertEquals(b.interarrivalJitter(), 0xFFFFFFFF, "jitter should be 0xFFFFFFFF");
		assertEquals(b.lastSR(), 0xFFFFFFFF, "lsr should be 0xFFFFFFFF");
		assertEquals(b.dlSR(), 0xFFFFFFFF, "dlsr should be 0xFFFFFFFF");

		assertEquals(b.asByteArray(), data, "report block data not reformed correctly.");
	}

}
