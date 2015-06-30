package jmula.simul.event;

import java.util.EventObject;

/**
 * Created by nael on 29/06/15.
 */
public class clEventBase extends EventObject
{
	private int timeToFire;

	/**
	 * Constructs a prototypical Event.
	 *
	 * @param source The object on which the Event initially occurred.
	 * @throws IllegalArgumentException if source is null.
	 */
	public clEventBase(Object source)
	{
		super(source);
	}

	public clEventBase(Object source, int timeToFire)
	{
		super(source);
		setTimeToFire(timeToFire);
	}


	public int getTimeToFire()
	{
		return timeToFire;
	}

	public void setTimeToFire(int timeToFire)
	{
		this.timeToFire = timeToFire;
	}
}
