package jmula.simul.entity;

/**
 * Created by nael on 03/07/15.
 */
public class clEntidadePedido
{
	private int ID;
	private int marcaTempoEntrada;

	public clEntidadePedido (int ID)
	{
		this.ID = ID;
		marcaTempoEntrada=0;
	}

	public int getMarcaTempoEntrada()
	{
		return marcaTempoEntrada;
	}

	public void setMarcaTempoEntrada(int marcaTempoEntrada)
	{
		this.marcaTempoEntrada = marcaTempoEntrada;
	}


	public int getID()
	{
		return ID;
	}

	public void setID(int ID)
	{
		this.ID = ID;
	}

	@Override
	public int hashCode()
	{
		return ID;

	}
}
