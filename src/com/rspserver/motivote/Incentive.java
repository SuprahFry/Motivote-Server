package com.rspserver.motivote;

@SuppressWarnings("rawtypes")
public abstract class Incentive
{
	private final Motivote motivote;
	private final int internalID;
	private final String username;
	private final String ip;
	
	public Incentive(Motivote motivote, int internalID, String username, String ip)
	{
		this.motivote = motivote;
		this.internalID = internalID;
		this.username = username;
		this.ip = ip;
	}
	
	public int internalID()
	{
		return internalID;
	}
	
	public String username()
	{
		return username;
	}
	
	public String ip()
	{
		return ip;
	}
	
	@SuppressWarnings("unchecked")
	public void complete()
	{
		motivote.complete(this);
	}
	
	@SuppressWarnings("unchecked")
	public void fail()
	{
		motivote.fail(this);
	}
}
