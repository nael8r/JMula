package jmula.simul.event;

import java.util.EventObject;

/**
 * Created by nael on 29/06/15.
 */
public class clEventoBase extends EventObject implements Comparable<clEventoBase>
{
	private enumTipoEvento tEvento;
	private int tempoExec;

	/**
	 * Constructs a prototypical Event.
	 *
	 * @param source The object on which the Event initially occurred.
	 * @throws IllegalArgumentException if source is null.
	 */
	public clEventoBase(Object source)
	{
		super(source);
	}

	public clEventoBase(Object source, int tempoExec, enumTipoEvento tEvento)
	{
		super(source);
		setTempoExec(tempoExec);
		settEvento(tEvento);
	}


	public int getTempoExec()
	{
		return tempoExec;
	}

	public void setTempoExec(int tempoExec)
	{
		this.tempoExec = tempoExec;
	}

	@Override
	public int compareTo(clEventoBase outro)
	{
		if(this.tempoExec > outro.getTempoExec())
			return 1;
		else if(this.tempoExec < outro.getTempoExec())
			return -1;
		else return 0;
	}

	public enumTipoEvento gettEvento()
	{
		return tEvento;
	}

	public void settEvento(enumTipoEvento tEvento)
	{
		this.tEvento = tEvento;
	}
}
