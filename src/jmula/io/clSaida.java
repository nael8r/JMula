package jmula.io;

import jmula.common.clEstatisticas;
import jmula.simul.parameters.clSimulParams;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
/**
 * Autores:
 *
 * João Paulo Fernandes Cerqueira César
 * Natanael Ramos
 * Rodolfo Labiapari Mansur Guimarães
 *
 * Classe para gerenciamento da saída de dados.
 *
 */
public class clSaida
{
	/**
	 * Gera o arquivo de estatísticas
	 *
	 * @param est Objeto de estatísticas
	 */
	public static void geraArqSaida(clEstatisticas est)
	{
		FileWriter output = null;
		File file = null;
		PrintWriter writeOnOutput = null;

		try
		{
			// tenta criar o arquivo
			file = new File(clSimulParams.NOME_ARQ_SAIDA);

			// se o arquivo ainda não existe
			if (!file.exists())
			{
				// cria o arquivo
				file.createNewFile();
				output = new FileWriter(file.getName(), true);
				writeOnOutput = new PrintWriter(output);

				// escreve as colunas
				writeOnOutput.print("TEMPO_FILA_TER\t");
				writeOnOutput.print("QTD_PECAS_FILA\t");
				writeOnOutput.print("TEMPO_SIMU\t");
				writeOnOutput.print("SEMENTE\t");
				writeOnOutput.print("QTD_PECAS");
			}
			// se o arquivo já existe
			else
			{
				output = new FileWriter(file.getName(), true);
				writeOnOutput = new PrintWriter(output);
			}

			writeOnOutput.println();
			// escreve estatísticas
			writeOnOutput.print(est.totTempoEspfilaAgTerceir + "\t");
			writeOnOutput.print(est.totPedidoEmfilaAgTerceir + "\t");
			writeOnOutput.print(est.tempoSimulacao + "\t");
			writeOnOutput.print(clSimulParams.SEMENTE_ATUAL + "\t");
			writeOnOutput.print(est.pedidosAtendidos);

			writeOnOutput.close();

		}
		catch (IOException e)
		{
			System.err.println(e.getMessage());
		}
	}


}
