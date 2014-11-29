package com.rspserver.motivote;

import java.io.FileNotFoundException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public final class MotivoteThread extends Thread
{
	private final Motivote<?> motivote;
	private boolean reward = false;
	
	public MotivoteThread(Motivote<?> motivote)
	{
		this.motivote = motivote;
		this.setName("Motivote");
	}
	
	@Override
	public void run()
	{
		try
		{
			Scanner s = new Scanner(new URL("http://rspserver.com/ext/mvserversion.txt").openStream(), "UTF-8");
			Float v = Float.parseFloat(s.useDelimiter("\\A").next());
			
			if (v > Motivote.VERSION)
			{
				for (int i = 0; i < 5; i++)
				{
					Motivote.mp("VERSION " + v + " AVAILABLE at http://www.rspserver.com/");
				}
			}
			else
			{
				Motivote.mp("No updates found.");
			}
			
			s.close();
		}
		catch (Exception ex)
		{
			Motivote.mp("Error while checking for updates, check http://www.rspserver.com/ manually for updates.");
		}
		
		while (true)
		{
			try
			{
				synchronized(motivote.finalized)
				{
					if (!motivote.finalized.isEmpty())
					{
						String ids = "";
						
						for (Integer id : motivote.finalized)
						{
							ids += id + ",";
						}
						
						ids = ids.substring(0, ids.length() - 1);
						Scanner s = new Scanner(new URL(motivote.pageURL() + "?do=finalize&type=" + (reward ? "rewards" : "votes") + "&key=" + motivote.securityKey() + "&ids=" + ids).openStream(), "UTF-8");
						String out = s.useDelimiter("\\A").next();
						s.close();
						
						if (out.equalsIgnoreCase("success"))
						{
							Motivote.mp("Finalized " + motivote.finalized.size() + " " + (reward ? "rewards" : "votes"));
							motivote.finalized.clear();
						}
						else
						{
							Motivote.mp(out);
						}
					}
				}
			}
			catch (SocketTimeoutException ex)
			{
				Motivote.mp("SUF: Timeout: " + ex.getMessage());
			}
			catch (FileNotFoundException ex)
			{
				Motivote.mp("SUF: FileNotFound: " + ex.getMessage());
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
			try
			{
				Scanner s = new Scanner(new URL(motivote.pageURL() + "?do=pending&key=" + motivote.securityKey()).openStream(), "UTF-8");
				String out = s.useDelimiter("\\A").next();
				s.close();
				
				JSONParser parser = new JSONParser();
				Object dat = parser.parse(out);
				
				if (dat instanceof JSONObject)
				{
					JSONObject obj = (JSONObject)dat;
					
					if (obj.containsKey("error"))
					{
						Motivote.mp("Error: " + ((JSONObject)obj).get("error"));
					}
					else
					{
						//mp(obj);
						reward = (boolean)obj.get("reward");
						
						synchronized(motivote.pending)
						{
							synchronized(motivote.finalized)
							{
								JSONArray dataArray = null;
								
								if (reward && obj.containsKey("rewards"))
								{
									dataArray = (JSONArray)obj.get("rewards");
								}
								else if (obj.containsKey("votes"))
								{
									dataArray = (JSONArray)obj.get("votes");
								}
								
								if (dataArray != null)
								{
									JSONObject[] datas = (JSONObject[])dataArray.toArray(new JSONObject[0]);
									
									for (JSONObject v : datas)
									{
										int internalID = Integer.parseInt((String)v.get("id"));
										
										if (!motivote.finalized.contains(internalID) && !motivote.pending.contains(internalID))
										{
											motivote.pending.add(internalID);
											String user = (String)v.get("user");
											String ip = (String)v.get("ip");
											
											if (reward)
											{
												Reward re = new Reward(motivote, internalID, Integer.parseInt((String)v.get("incentive")), user, ip, (String)v.get("name"), Integer.parseInt((String)v.get("amount")));
												((Motivote<Reward>)motivote).handler().onCompletion(re);
											}
											else
											{
												Vote vo = new Vote(motivote, internalID, Integer.parseInt((String)v.get("site")), user, ip);
												((Motivote<Vote>)motivote).handler().onCompletion(vo);
											}
											
											//motivote.pending.remove((Integer)internalID);
										}
									}
								}
								
								motivote.pending.clear();
							}
						}
					}
				}
				else
				{
					Motivote.mp(dat);
				}
				
			}
			catch (SocketTimeoutException ex)
			{
				Motivote.mp("PRE: Timeout: " + ex.getMessage());
			}
			catch (FileNotFoundException ex)
			{
				Motivote.mp("PRE: FileNotFound: " + ex.getMessage());
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
			try
			{
				Thread.sleep(5000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
