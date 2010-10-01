package com.hapiware.util.publisher;


public interface PublishingPolicy<INTERFACE>
{
	public INTERFACE publish(final Object obj);
}
