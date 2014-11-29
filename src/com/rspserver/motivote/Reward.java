package com.rspserver.motivote;

public final class Reward extends Incentive
{
	private final int amount;
	private final int incentiveID;
	private final String incentiveName;
	
	public Reward(Motivote motivote, int rewardID, int incentiveID, String username, String ip, String incentiveName, int amount)
	{
		super(motivote, rewardID, username, ip);
		this.incentiveID = incentiveID;
		this.incentiveName = incentiveName;
		this.amount = amount;
	}
	
	public int incentiveID()
	{
		return incentiveID;
	}
	
	public String rewardName()
	{
		return incentiveName;
	}
	
	public int amount()
	{
		return amount;
	}
}
