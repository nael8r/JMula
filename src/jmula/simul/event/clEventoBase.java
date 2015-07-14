package jmula.simul.event;

import java.util.EventObject;

/**
 * Autores:
 *
 * João Paulo Fernandes Cerqueira César
 * Natanael Ramos
 * Rodolfo Labiapari Mansur Guimarães
 *
 * Classe para o evento básico de um sistema, qualquer evento que necessite de informações adicionais, pode herdar dessa
 * classe.
 *
 * Herda da classe de eventos do Java e implementa o comparador para manter a fel (Future Event List) ordenada.
 *
 */
public class clEventoBase extends EventObject implements Comparable<clEventoBase>
{
	// tipo de evento
	private enumTipoEvento tEvento;
	// tempo em que o evento será terminado
	private int tempoTermino;
	// identificador da peça piloto associada a este evento
	private Integer pecaID;

	/**
	 * Cria um novo evento base
	 *
	 * @param source Outro evento que originou o atual
	 */
	public clEventoBase(Object source)
	{
		super(source);
	}

	/**
	 * Cria um novo evento base
	 *
	 * @param source Outro evento que originou o atual
	 * @param tempoTermino Tempo de término do evento
	 * @param tEvento Tipo do evento
	 * @param pecaID Identificador da peça piloto
	 */
	public clEventoBase(Object source, int tempoTermino, enumTipoEvento tEvento, Integer pecaID)
	{
		// chama o construtor do EventObject
		super(source);
		setTempoTermino(tempoTermino);
		settEvento(tEvento);
		setPecaID(pecaID);
	}

	public Integer getPecaID()
	{
		return pecaID;
	}

	public void setPecaID(Integer pecaID)
	{
		this.pecaID = pecaID;
	}


	public int getTempoTermino()
	{
		return tempoTermino;
	}

	public void setTempoTermino(int tempoTermino)
	{
		this.tempoTermino = tempoTermino;
	}

	/**
	 * Compara dois eventos de acordo com o tempo de término, em ordem ascendente
	 *
	 * @param outroEvento Evento a ser comparado
	 * @return 1 se o evento atual possui tempo de término maior, -1 se possui menor e 0 se é igual
	 */
	@Override
	public int compareTo(clEventoBase outroEvento)
	{
		if(this.tempoTermino > outroEvento.getTempoTermino())
			return 1;
		else if(this.tempoTermino < outroEvento.getTempoTermino())
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

	/**
	 * Informações úteis sobre o evento
	 *
	 * @return Informações do evento
	 */
	@Override
	public String toString()
	{
		return "evento: "+tEvento.name()+" ID-peça: "+pecaID+" time: "+ tempoTermino;
	}
}
