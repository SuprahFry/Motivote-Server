package com.rspserver.motivote;

/**
 * The class you must extend to accept votes/rewards.
 * @author Supah Fly
 * @param <T> The type of incentive task to be handled. Normally either a {@link Vote} or a {@link Reward}.
 */
public abstract class MotivoteHandler<T extends Incentive>
{
	/**
	 * Called when T is successfully completed by the user on the website.
	 * @param inc The completed incentive task.
	 */
	public abstract void onCompletion(T inc);
}
