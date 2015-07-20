package jmula;

import jmula.common.clEstatisticas;
import jmula.common.enumTipoDebug;
import jmula.simul.clSimul;
import jmula.simul.parameters.clSimulParams;
import org.apache.commons.math3.random.MersenneTwister;

import static jmula.common.clDebug.dbg;
import static jmula.io.clEntrada.leArgumentos;
import static jmula.io.clEntrada.leArquivoConfig;
import static jmula.io.clSaida.geraArqSaida;

/**
 * Autores:
 *
 * João Paulo Fernandes Cerqueira César
 * Natanael Ramos
 * Rodolfo Labiapari Mansur Guimarães
 *
 * Classe principal.
 */
public class JMula
{
	public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException
	{
		// lê os argumentos
		leArgumentos(args);
		// lê o arquivo de configuração
		leArquivoConfig();

		// objeto da simulação
		clSimul s = new clSimul();

		// estatísticas retornadas pela simulação
		clEstatisticas est;

		MersenneTwister rand = new MersenneTwister();

		// executa todas as replicações
		for(long i=0; i< clSimulParams.MAX_REPLICACOES; i++)
		{
			System.out.println("===================================================================================");
			System.out.println("Início da Simulação da replicação: "+i);

			// semente atual
			clSimulParams.SEMENTE_ATUAL = rand.nextLong();
			// simula
			s.simul(clSimulParams.SEMENTE_ATUAL);
			// gera saída
			geraArqSaida(s.getEst());

			System.out.println("Término da Simulação da replicação: "+i);
			System.out.println("===============================================================================");
		}


		System.out.println();
	}
}
