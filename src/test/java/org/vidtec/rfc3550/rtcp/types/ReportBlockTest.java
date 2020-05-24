package org.vidtec.rfc3550.rtcp.types;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;
import org.vidtec.rfc3550.rtcp.stats.TransmissionStatistics;
import org.vidtec.rfc3550.rtcp.types.ReportRTCPPacket.ReportBlock;

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

		assertEquals(b.length(), 24, "report block size incorrect.");
		assertEquals(b.asByteArray().length, 24, "report block data not reformed correctly.");
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

		assertEquals(b.length(), 24, "report block size incorrect.");
		assertEquals(b.asByteArray().length, 24, "report block data not reformed correctly.");
		assertEquals(b.asByteArray(), data, "report block data not reformed correctly.");
	}
	
	public void testCorrectlyRejectsBadByteData()
	{
		try
		{
			ReportBlock.fromByteArray(new byte[2]);
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "report block was wrong size, expecting 24 bytes, but found 2", "wrong validation message");
		}
		try
		{
			ReportBlock.fromByteArray(new byte[40]);
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "report block was wrong size, expecting 24 bytes, but found 40", "wrong validation message");
		}
	}
	
	
	public void testCanCreateReportBlockFromValidBuilder()
	{
		// valid report block
		final byte[] data = { 0x04, 0x03, 0x02, 0x01, 0x01, 0x03, 0x02, 0x01, 0x05, 0x04, 0x03, 0x02,
						      0x06, 0x03, 0x02, 0x01, 0x07, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00 };
		
		final ReportBlock b = ReportBlock.builder()
							.withSsrc(0x04030201L)	
							.withExtendedSequenceNumber(0x05040302L)
							.withStatistics(1, 0x030201, 0x06030201L)
							.withSenderReportData(0x07000000L, 0x08000000L)
							.build();

		assertEquals(b.ssrcIdentifier(), 0x04030201L, "ssrc should be 0x04030201");
		assertEquals(b.fractionLost(), 1, "fraction lost should be 0x01");
		assertEquals(b.cumulativeLost(), 0x030201, "cum lost should be 0x030201");
		assertEquals(b.extendedHighestSequenceNumber(), 0x05040302L, "seq no should be 0x05040302");
		assertEquals(b.interarrivalJitter(), 0x06030201L, "jitter should be 0x06030201");
		assertEquals(b.lastSR(), 0x07000000L, "lsr should be 0x07000000");
		assertEquals(b.dlSR(), 0x08000000L, "dlsr should be 0x08000000");

		assertEquals(b.length(), 24, "report block size incorrect.");
		assertEquals(b.asByteArray().length, 24, "report block data not reformed correctly.");
		assertEquals(b.asByteArray(), data, "report block data not reformed correctly.");
	}
	
	
	public void testCanCreateReportBlockFromValidBuilderAndStatsObject()
	{
		// valid report block
		final byte[] data = { 0x04, 0x03, 0x02, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
						      0x00, 0x00, 0x00, 0x00, 0x07, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00 };
		
		final ReportBlock b = ReportBlock.builder()
							.withSsrc(0x04030201L)	
							.withExtendedSequenceNumber(0x05040302L)
							.withStatistics(TransmissionStatistics.fromStartSequence(2))
							.withSenderReportData(0x07000000L, 0x08000000L)
							.build();

		assertEquals(b.ssrcIdentifier(), 0x04030201L, "ssrc should be 0x04030201");
		assertEquals(b.fractionLost(), 0, "fraction lost should be 0");
		assertEquals(b.cumulativeLost(), 0, "cum lost should be 0");
		assertEquals(b.extendedHighestSequenceNumber(), 2, "seq no should be 2");
		assertEquals(b.interarrivalJitter(), 0, "jitter should be 0");
		assertEquals(b.lastSR(), 0x07000000L, "lsr should be 0x07000000");
		assertEquals(b.dlSR(), 0x08000000L, "dlsr should be 0x08000000");

		assertEquals(b.length(), 24, "report block size incorrect.");
		assertEquals(b.asByteArray().length, 24, "report block data not reformed correctly.");
		assertEquals(b.asByteArray(), data, "report block data not reformed correctly.");
	}
	
	
	public void testCanCreateReportBlockFromValidBuilderAtLimits()
	{
		// valid report block
		final byte[] data = { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, 
				              (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,  };
		
		final ReportBlock b = ReportBlock.builder()
				.withSsrc(0xFFFFFFFFL)	
				.withExtendedSequenceNumber(0xFFFFFFFFL)
				.withStatistics(0xFF, 0xFFFFFF, 0xFFFFFFFFL)
				.withSenderReportData(0xFFFFFFFFL, 0xFFFFFFFFL)
				.build();
		
		assertEquals(b.ssrcIdentifier(), 0xFFFFFFFFL, "ssrc should be 0xFFFFFFFF");
		assertEquals(b.fractionLost(), 0xFF, "fraction lost should be 0xFF");
		assertEquals(b.cumulativeLost(), 0xFFFFFF, "cum lost should be 0xFFFFFF");
		assertEquals(b.extendedHighestSequenceNumber(), 0xFFFFFFFFL, "seq no should be 0xFFFFFFFF");
		assertEquals(b.interarrivalJitter(), 0xFFFFFFFFL, "jitter should be 0xFFFFFFFF");
		assertEquals(b.lastSR(), 0xFFFFFFFFL, "lsr should be 0xFFFFFFFF");
		assertEquals(b.dlSR(), 0xFFFFFFFFL, "dlsr should be 0xFFFFFFFF");

		assertEquals(b.length(), 24, "report block size incorrect.");
		assertEquals(b.asByteArray().length, 24, "report block data not reformed correctly.");
		assertEquals(b.asByteArray(), data, "report block data not reformed correctly.");
	}
	
	
	public void testCorrectlyRejectsBadBuilderData()
	{
		try
		{
			ReportBlock.builder().withSsrc(-1).build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid SSRC value.", "wrong validation message");
		}
		try
		{
			ReportBlock.builder().withSsrc(0xFFFFFFFFFL).build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid SSRC value.", "wrong validation message");
		}
		try
		{
			ReportBlock.builder().withSsrc(1).withExtendedSequenceNumber(-1).build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid extended sequence number.", "wrong validation message");
		}		
		try
		{
			ReportBlock.builder().withSsrc(1).withExtendedSequenceNumber(0xFFFFFFFFFL).build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid extended sequence number.", "wrong validation message");
		}		
		
		try
		{
			ReportBlock.builder().withSsrc(1).withExtendedSequenceNumber(1).withStatistics(-1, 0, 0).build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid packet statistics values.", "wrong validation message");
		}
		try
		{
			ReportBlock.builder().withSsrc(1).withExtendedSequenceNumber(1).withStatistics(0,-1, 0).build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid packet statistics values.", "wrong validation message");
		}
		try
		{
			ReportBlock.builder().withSsrc(1).withExtendedSequenceNumber(1).withStatistics(0, 0, -1).build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid packet statistics values.", "wrong validation message");
		}
		try
		{
			ReportBlock.builder().withSsrc(1).withExtendedSequenceNumber(1).withStatistics(0xFFF, 0, 0).build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid packet statistics values.", "wrong validation message");
		}
		try
		{
			ReportBlock.builder().withSsrc(1).withExtendedSequenceNumber(1).withStatistics(0,0xFFFFFFFL, 0).build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid packet statistics values.", "wrong validation message");
		}
		try
		{
			ReportBlock.builder().withSsrc(1).withExtendedSequenceNumber(1).withStatistics(0, 0, 0xFFFFFFFFFL).build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid packet statistics values.", "wrong validation message");
		}
		try
		{
			ReportBlock.builder().withSsrc(1).withExtendedSequenceNumber(1).withStatistics(0, 0, 0).withSenderReportData(-1, 0).build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid sender report values.", "wrong validation message");
		}
		try
		{
			ReportBlock.builder().withSsrc(1).withExtendedSequenceNumber(1).withStatistics(0, 0, 0).withSenderReportData(0, -1).build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid sender report values.", "wrong validation message");
		}
		try
		{
			ReportBlock.builder().withSsrc(1).withExtendedSequenceNumber(1).withStatistics(0, 0, 0).withSenderReportData(0xFFFFFFFFFL, 0).build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid sender report values.", "wrong validation message");
		}
		try
		{
			ReportBlock.builder().withSsrc(1).withExtendedSequenceNumber(1).withStatistics(0, 0, 0).withSenderReportData(0, 0xFFFFFFFFFL).build();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "Expected valid sender report values.", "wrong validation message");
		}
	}

}
