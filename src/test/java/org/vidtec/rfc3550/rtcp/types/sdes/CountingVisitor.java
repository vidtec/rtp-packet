package org.vidtec.rfc3550.rtcp.types.sdes;

public class CountingVisitor implements SdesItemsVisitor
{
	public int total = 0;
	public int cname = 0;
	public int name = 0;
	public int phone = 0;
	public int email = 0;
	public int loc = 0;
	public int tool = 0;
	public int note = 0;
	public int priv = 0;

	
	@Override
	public void visitCname(SdesItem item) 
	{
		SdesItemsVisitor.super.visitCname(item);

		cname++;
		total++;
	}


	@Override
	public void visitName(SdesItem item) 
	{
		SdesItemsVisitor.super.visitName(item);

		name++;
		total++;
	}


	@Override
	public void visitEmail(SdesItem item) 
	{
		SdesItemsVisitor.super.visitEmail(item);

		email++;
		total++;
	}


	@Override
	public void visitPhone(SdesItem item) 
	{
		SdesItemsVisitor.super.visitPhone(item);

		phone++;
		total++;
	}


	@Override
	public void visitLoc(SdesItem item) 
	{
		SdesItemsVisitor.super.visitLoc(item);

		loc++;
		total++;
	}


	@Override
	public void visitTool(SdesItem item) 
	{
		SdesItemsVisitor.super.visitTool(item);

		tool++;
		total++;
	}


	@Override
	public void visitNote(SdesItem item) 
	{
		SdesItemsVisitor.super.visitNote(item);

		note++;
		total++;
	}


	@Override
	public void visitPriv(SdesItem item) 
	{
		SdesItemsVisitor.super.visitPriv(item);

		priv++;
		total++;
	}

}
