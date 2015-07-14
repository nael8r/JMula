package jmula.common;

/**
 * Autores:
 *
 * João Paulo Fernandes Cerqueira César
 * Natanael Ramos
 * Rodolfo Labiapari Mansur Guimarães
 *
 * Classe responsável por gerenciar as mensagens de Debug emitidas pela aplicação, sendo essas mensagens classificadas
 * como:
 *
 *  Entidade: Mostra informações sobre as entidades da simulação (peças, funcionários ...)
 *  Evento: Mostra informações sobre os eventos da simulação
 *  Fila: Mostra informações sobre as filas da simulação
 *  Info: Mostra demais informações
 */
public class clDebug
{
	// tipos de debug
	public static boolean dbgEntidade, dbgEvento, dbgFila, dbgInfo;

	/**
	 * Método principal de Debug, para exibição de mensagens
	 *
	 * @param tipo Tipo de Debug
	 * @param mensagem Mensagem a ser imprimida
	 */
	public static void dbg(enumTipoDebug tipo, String mensagem)
	{
		if (tipo==enumTipoDebug.ENTIDADE && dbgEntidade)
		{
			System.out.println("[ENTIDADE] "+mensagem);
		}

		if (tipo==enumTipoDebug.EVENTO && dbgEvento)
		{
			System.out.println("[EVENTO] "+mensagem);
		}

		if (tipo==enumTipoDebug.FILA && dbgFila)
		{
			System.out.println("[FILA] "+mensagem);
		}

		if (tipo==enumTipoDebug.INFO && dbgInfo)
		{
			System.out.println("[INFO] "+mensagem);
		}


	}
}
