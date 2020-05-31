package org.vidtec.rfc3550.rtcp.types.sdes;

/**
 * An interface for supporting visiting RTCP SDES items in a stream.
 */
public interface SdesItemsVisitor 
{
	
	/**
	 * Handle an SDES CNAME item in the stream.
	 * 
	 * @param item The item instance.
	 */
	default void visitCname(final SdesItem item)
	{
		// Do nothing.
	}

	
	/**
	 * Handle an SDES CNAME item in the stream.
	 * 
	 * @param item The item instance.
	 */
	default void visitName(final SdesItem item)
	{
		// Do nothing.
	}
	

	/**
	 * Handle an SDES EMAIL item in the stream.
	 * 
	 * @param item The item instance.
	 */
	default void visitEmail(final SdesItem item)
	{
		// Do nothing.
	}
	

	/**
	 * Handle an SDES PHONE item in the stream.
	 * 
	 * @param item The item instance.
	 */
	default void visitPhone(final SdesItem item)
	{
		// Do nothing.
	}

	
	/**
	 * Handle an SDES LOC item in the stream.
	 * 
	 * @param item The item instance.
	 */
	default void visitLoc(final SdesItem item)
	{
		// Do nothing.
	}

	
	/**
	 * Handle an SDES TOOL item in the stream.
	 * 
	 * @param item The item instance.
	 */
	default void visitTool(final SdesItem item)
	{
		// Do nothing.
	}
	

	/**
	 * Handle an SDES NOTE item in the stream.
	 * 
	 * @param item The item instance.
	 */
	default void visitNote(final SdesItem item)
	{
		// Do nothing.
	}

	
	/**
	 * Handle an SDES PRIV item in the stream.
	 * 
	 * @param item The item instance.
	 */
	default void visitPriv(final SdesItem item)
	{
		// Do nothing.
	}
	
}
