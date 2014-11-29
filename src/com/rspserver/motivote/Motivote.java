package com.rspserver.motivote;

import java.util.ArrayList;

public final class Motivote<T extends Incentive>
{
	public static final float VERSION = 1.3f;

	private final MotivoteHandler<T> handler;
	private MotivoteThread worker;
	private final String securityKey;
	private final String pageURL;

	protected final ArrayList<Integer> finalized = new ArrayList<Integer>();
	protected final ArrayList<Integer> pending = new ArrayList<Integer>();
	
	public Motivote(MotivoteHandler<T> handler, String webDir, String securityKey)
	{
		this.handler = handler;
		this.pageURL = webDir + "databack.php";
		this.securityKey = securityKey;
		mp("Server Library Version " + VERSION);
	}
	
	public void start()
	{
		if (worker == null)
		{
			mp("Library will now continuously check web server for vote data.");
			worker = new MotivoteThread(this);
			worker.start();
		}
		else
		{
			mp("Library already working on this server.");
		}
	}
	
	public String pageURL()
	{
		return pageURL;
	}
	
	public String securityKey()
	{
		return securityKey;
	}
	
	public MotivoteHandler<T> handler()
	{
		return handler;
	}
	
	public void fail(T incentive)
	{
		synchronized(pending)
		{
			synchronized(finalized)
			{
				pending.remove((Integer)incentive.internalID());
			}
		}
	}
	
	public void complete(T incentive)
	{
		synchronized(pending)
		{
			synchronized(finalized)
			{
				pending.remove((Integer)incentive.internalID());
				finalized.add(incentive.internalID());
			}
		}
		
		System.out.println("Finalizing " + incentive);
	}
	
	public static void mp(Object string)
	{
		System.out.println("[MOTIVOTE] " + string);
	}
	
	public static void main(String[] args) throws Exception
	{
		mp("Test mode.");
		Motivote<?> m = new Motivote<Reward>(new MotivoteHandler<Reward>()
		{
			@Override
			public void onCompletion(Reward inc)
			{
				System.out.println(inc.internalID() + " | Reward received for " + inc.username() + " (" + inc.rewardName() + ", " + inc.amount() + ")");
				//System.out.println(inc.internalID() + " | Vote received for " + inc.username());
				inc.complete();
			}
		}, "http://localhost/motivote/", "0f26fe1e0b");
		m.start();
	}
}
