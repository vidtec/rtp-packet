package org.vidtec.rfc3550.rtcp;

import org.vidtec.rfc3550.rtcp.types.ByeRTCPPacket;
import org.vidtec.rfc3550.rtcp.types.ReceiverReportRTCPPacket;
import org.vidtec.rfc3550.rtcp.types.SenderReportRTCPPacket;

public class CountingVisitor implements RTCPPacketsVisitor
{
	public int total = 0;
	public int sr = 0;
	public int rr = 0;
	public int sdes = 0;
	public int app = 0;
	public int bye = 0;
	
	@Override
	public void visit(SenderReportRTCPPacket packet) 
	{
		sr++;
		total++;
	}
	
	
	@Override
	public void visit(ReceiverReportRTCPPacket packet) 
	{
		rr++;
		total++;
	}
	
	
	@Override
	public void visit(ByeRTCPPacket packet) 
	{
		bye++;
		total++;
	}
	
	

}
