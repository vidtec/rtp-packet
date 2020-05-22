package org.vidtec.rfc3550.rtcp;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

@Test
public class TransmissionStatisticsTest 
{

	public void testCanCreateEmptyStatistics()
	{
		final TransmissionStatistics s = TransmissionStatistics.fromStartSequence(2);

		assertEquals(s.maxExtendedSequenceNumber(), 2, "expected max seq no.");
		assertEquals(s.fractionLost(), 0, "expected no loss.");
		assertEquals(s.lost(), 0, "expected total loss.");
		assertEquals(s.received(), 0, "expected no packets.");
	}

	
	public void testCanTriviallyIncrementStats()
	{
		final TransmissionStatistics s = TransmissionStatistics.fromStartSequence(2);
		s.update(3);
		
		assertEquals(s.maxExtendedSequenceNumber(), 3, "expected max seq no.");
		assertEquals(s.fractionLost(), 0, "expected no loss.");
		assertEquals(s.lost(), 0, "expected total loss.");
		assertEquals(s.received(), 1, "expected 1 packet.");
	}
	
	
	public void testCorrectlyRejectsInvalidStartSequence()
	{
		// > 0xFFFF && < 0
	}

}
