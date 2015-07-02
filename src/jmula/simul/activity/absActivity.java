package jmula.simul.activity;

/**
 * Created by nael on 29/06/15.
 */
public abstract class absActivity
{
	private int duration;
	/**
	 * Condições de início da atividade.
	 *
	 * @return true se a atividade pode iniciar
	 */
	public abstract boolean condition();

	public int getDuration()
	{
		return duration;
	}

	public void setDuration(int duration)
	{
		this.duration = duration;
	}
}
