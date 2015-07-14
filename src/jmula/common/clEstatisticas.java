package jmula.common;

/**
 * Autores:
 *
 * João Paulo Fernandes Cerqueira César
 * Natanael Ramos
 * Rodolfo Labiapari Mansur Guimarães
 *
 * Classe para gerenciamento das estatísticas e indicadores de desempenho da simulação.
 *
 */
public class clEstatisticas
{
	// tempo total em cada fila da simulação
	public int totTempoEspfilaAgProc;
	public int totTempoEspfilaAgAnalis;
	public int totTempoEspfilaAgVerifDataMater;
	public int totTempoEspfilaAgPlanejamento;
	public int totTempoEspfilaAgVerificacao;
	public int totTempoEspfilaAgMoldPronto;
	public int totTempoEspfilaAgRastr;
	public int totTempoEspfilaAgColeta;
	public int totTempoEspfilaAgPreeAreia;
	public int totTempoEspfilaAgIdentif;
	public int totTempoEspfilaAgMaquina;
	public int totTempoEspfilaAgManual;
	public int totTempoEspfilaAgCaixote;
	public int totTempoEspfilaAgSCaixote;
	public int totTempoEspfilaAgCheckoutLimpAcab;
	public int totTempoEspfilaAgLimpEsp;
	public int totTempoEspfilaAgAcabamento;
	public int totTempoEspfilaAgEsmeril;
	public int totTempoEspfilaAgRebarbMaq;
	public int totTempoEspfilaAgRebarbMan;
	public int totTempoEspfilaAgAnalisVisual;
	public int totTempoEspfilaAgTerceir;
	public int totTempoEspfilaAgPint;
	public int totTempoEspfilaAgDoc;

	// tempo em simulação
	public long tempoSimulacao;

	// quantidade de pedidos atendidos
	public int pedidosAtendidos;

	/**
	 * Imprime o tempo de espera em todas as filas
	 */
	public void imprimeTempoEmFilas()
	{
		System.out.println("\n================= TEMPO EM FILAS ==========================");
		System.out.println("Espera processamento e Expedição: "+totTempoEspfilaAgProc);
		System.out.println("Espera Análise quimica e verificação: "+totTempoEspfilaAgAnalis);
		System.out.println("Espera Verificação Data e Matérias Primas: "+totTempoEspfilaAgVerifDataMater);
		System.out.println("Espera Planejamento: "+totTempoEspfilaAgPlanejamento);
		System.out.println("Espera Arquivos Modelagem Depósito: "+totTempoEspfilaAgVerificacao);
		System.out.println("Espera Verificar Moldes Prontos: "+totTempoEspfilaAgMoldPronto);
		System.out.println("Espera Rastreabilidade: "+totTempoEspfilaAgRastr);
		System.out.println("Espera Coleta: "+totTempoEspfilaAgColeta);
		System.out.println("Espera Preenchimento Areia: "+totTempoEspfilaAgPreeAreia);
		System.out.println("Espera Identificação: "+totTempoEspfilaAgIdentif);
		System.out.println("Espera Produção Máquina: "+totTempoEspfilaAgMaquina);
		System.out.println("Espera Produção Manual: "+totTempoEspfilaAgManual);
		System.out.println("Espera Produção Resina Caixote: "+totTempoEspfilaAgCaixote);
		System.out.println("Espera Produção Resina Sem Caixote: "+totTempoEspfilaAgSCaixote);
		System.out.println("Espera Checkout/Limpeza e Acabamento: "+totTempoEspfilaAgCheckoutLimpAcab);
		System.out.println("Espera Limpeza Especial: "+totTempoEspfilaAgLimpEsp);
		System.out.println("Espera Acabamento: "+totTempoEspfilaAgAcabamento);
		System.out.println("Espera Esmeril: "+totTempoEspfilaAgEsmeril);
		System.out.println("Espera Rebarbação Máquina: "+ totTempoEspfilaAgRebarbMaq);
		System.out.println("Espera Rebarbação Manual: "+totTempoEspfilaAgRebarbMan);
		System.out.println("Espera Análise Visual: "+totTempoEspfilaAgAnalisVisual);
		System.out.println("Espera Serviço de Terceiros: "+totTempoEspfilaAgTerceir);
		System.out.println("Espera Pintura: "+totTempoEspfilaAgPint);
		System.out.println("Espera Documentação: "+totTempoEspfilaAgDoc);
		System.out.println();
	}

	/**
	 * Imprime estatísticas de desempenho da simulação
	 */
	public void imprimeEstatisticasDesempenho()
	{
		System.out.println();
		System.out.println("Tempo de simulação(ns): "+tempoSimulacao);
		System.out.println("Quantidade de pedidos atendidos: "+ pedidosAtendidos);
		System.out.println();
	}

	/**
	 * Imprime as estatísticas
	 */
	public void imprimeEstatisticas()
	{
		imprimeTempoEmFilas();
		imprimeEstatisticasDesempenho();
	}
}
