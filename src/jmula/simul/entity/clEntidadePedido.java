package jmula.simul.entity;

/**
 * Autores:
 *
 * João Paulo Fernandes Cerqueira César
 * Natanael Ramos
 * Rodolfo Labiapari Mansur Guimarães
 *
 * CLasse que contem a entidade de cada pedido.
 */
public class clEntidadePedido
{
	// identificador interno do pedido
	private int ID;
	// marca o tempo em que o pedido entrou em alguma fila, para calcular o tempo de espera de cada fila
	private int marcaTempoEntrada;

	/**
	 * Cria uma nova entidade
	 *
	 * @param ID Identificador
	 */
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
