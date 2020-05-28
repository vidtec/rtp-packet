package org.vidtec.rfc3550.rtcp;

import org.vidtec.rfc3550.rtcp.types.AppRTCPPacket;
import org.vidtec.rfc3550.rtcp.types.ByeRTCPPacket;
import org.vidtec.rfc3550.rtcp.types.ReceiverReportRTCPPacket;
import org.vidtec.rfc3550.rtcp.types.SdesRTCPPacket;
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
		RTCPPacketsVisitor.super.visit(packet);

		sr++;
		total++;
	}


	@Override
	public void visit(ReceiverReportRTCPPacket packet) 
	{
		RTCPPacketsVisitor.super.visit(packet);

		rr++;
		total++;
	}

	
	@Override
	public void visit(SdesRTCPPacket packet) 
	{
		RTCPPacketsVisitor.super.visit(packet);

		sdes++;
		total++;
	}
	
	
	@Override
	public void visit(AppRTCPPacket packet) 
	{
		RTCPPacketsVisitor.super.visit(packet);

		app++;
		total++;
	}
	
	
	@Override
	public void visit(ByeRTCPPacket packet) 
	{
		RTCPPacketsVisitor.super.visit(packet);

		bye++;
		total++;
	}

}
