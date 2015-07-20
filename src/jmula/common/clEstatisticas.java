package jmula.common;

/**
 * Autores:
 * <p>
 * João Paulo Fernandes Cerqueira César
 * Natanael Ramos
 * Rodolfo Labiapari Mansur Guimarães
 * <p>
 * Classe para gerenciamento das estatísticas e indicadores de desempenho da simulação.
 */
public class clEstatisticas
{
	// tempo total em cada fila da simulação
	public int totTempoEspfilaAgProc;
	public int totPedidoEmfilaAgProc;
	public double mediaEsperafilaAgProc;
	public int totTempoEspfilaAgAnalis;
	public int totPedidoEmfilaAgAnalis;
	public double mediaEsperafilaAgAnalis;
	public int totTempoEspfilaAgVerifDataMater;
	public int totPedidoEmfilaAgVerifDataMater;
	public double mediaEsperafilaAgVerifDataMater;
	public int totTempoEspfilaAgPlanejamento;
	public int totPedidoEmfilaAgPlanejamento;
	public double mediaEsperafilaAgPlanejamento;
	public int totTempoEspfilaAgVerificacao;
	public int totPedidoEmfilaAgVerificacao;
	public double mediaEsperafilaAgVerificacao;
	public int totTempoEspfilaAgMoldPronto;
	public int totPedidoEmfilaAgMoldPronto;
	public double mediaEsperafilaAgMoldPronto;
	public int totTempoEspfilaAgRastr;
	public int totPedidoEmfilaAgRastr;
	public double mediaEsperafilaAgRastr;
	public int totTempoEspfilaAgColeta;
	public int totPedidoEmfilaAgColeta;
	public double mediaEsperafilaAgColeta;
	public int totTempoEspfilaAgPreeAreia;
	public int totPedidoEmfilaAgPreeAreia;
	public double mediaEsperafilaAgPreeAreia;
	public int totTempoEspfilaAgIdentif;
	public int totPedidoEmfilaAgIdentif;
	public double mediaEsperafilaAgIdentif;
	public int totTempoEspfilaAgMaquina;
	public int totPedidoEmfilaAgMaquina;
	public double mediaEsperafilaAgMaquina;
	public int totTempoEspfilaAgManual;
	public int totPedidoEmfilaAgManual;
	public double mediaEsperafilaAgManual;
	public int totTempoEspfilaAgCaixote;
	public int totPedidoEmfilaAgCaixote;
	public double mediaEsperafilaAgCaixote;
	public int totTempoEspfilaAgSCaixote;
	public int totPedidoEmfilaAgSCaixote;
	public double mediaEsperafilaAgSCaixote;
	public int totTempoEspfilaAgCheckoutLimpAcab;
	public int totPedidoEmfilaAgCheckoutLimpAcab;
	public double mediaEsperafilaAgCheckoutLimpAcab;
	public int totTempoEspfilaAgLimpEsp;
	public int totPedidoEmfilaAgLimpEsp;
	public double mediaEsperafilaAgLimpEsp;
	public int totTempoEspfilaAgAcabamento;
	public int totPedidoEmfilaAgAcabamento;
	public double mediaEsperafilaAgAcabamento;
	public int totTempoEspfilaAgEsmeril;
	public int totPedidoEmfilaAgEsmeril;
	public double mediaEsperafilaAgEsmeril;
	public int totTempoEspfilaAgRebarbMaq;
	public int totPedidoEmfilaAgRebarbMaq;
	public double mediaEsperafilaAgRebarbMaq;
	public int totTempoEspfilaAgRebarbMan;
	public int totPedidoEmfilaAgRebarbMan;
	public double mediaEsperafilaAgRebarbMan;
	public int totTempoEspfilaAgAnalisVisual;
	public int totPedidoEmfilaAgAnalisVisual;
	public double mediaEsperafilaAgAnalisVisual;
	public int totTempoEspfilaAgTerceir;
	public int totPedidoEmfilaAgTerceir;
	public double mediaEsperafilaAgTerceir;
	public int totTempoEspfilaAgPint;
	public int totPedidoEmfilaAgPint;
	public double mediaEsperafilaAgPint;
	public int totTempoEspfilaAgDoc;
	public int totPedidoEmfilaAgDoc;
	public double mediaEsperafilaAgDoc;

	// tempo em simulação
	public long tempoSimulacao;

	// quantidade de pedidos atendidos
	public int pedidosAtendidos;

	/**
	 * Imprime o tempo de espera em todas as filas
	 */
	public void imprimeTempoEmFilas()
	{
		System.out.println("\n================= TEMPO MÉDIO EM FILAS ==========================");
		System.out.println("Espera processamento e Expedição: " + mediaEsperafilaAgProc);
		System.out.println("Espera Análise quimica e verificação: " + mediaEsperafilaAgAnalis);
		System.out.println("Espera Verificação Data e Matérias Primas: " + mediaEsperafilaAgVerifDataMater);
		System.out.println("Espera Planejamento: " + mediaEsperafilaAgPlanejamento);
		System.out.println("Espera Arquivos Modelagem Depósito: " + mediaEsperafilaAgVerificacao);
		System.out.println("Espera Verificar Moldes Prontos: " + mediaEsperafilaAgMoldPronto);
		System.out.println("Espera Rastreabilidade: " + mediaEsperafilaAgRastr);
		System.out.println("Espera Coleta: " + mediaEsperafilaAgColeta);
		System.out.println("Espera Preenchimento Areia: " + mediaEsperafilaAgPreeAreia);
		System.out.println("Espera Identificação: " + mediaEsperafilaAgIdentif);
		System.out.println("Espera Produção Máquina: " + mediaEsperafilaAgMaquina);
		System.out.println("Espera Produção Manual: " + mediaEsperafilaAgManual);
		System.out.println("Espera Produção Resina Caixote: " + mediaEsperafilaAgCaixote);
		System.out.println("Espera Produção Resina Sem Caixote: " + mediaEsperafilaAgSCaixote);
		System.out.println("Espera Checkout/Limpeza e Acabamento: " + mediaEsperafilaAgCheckoutLimpAcab);
		System.out.println("Espera Limpeza Especial: " + mediaEsperafilaAgLimpEsp);
		System.out.println("Espera Acabamento: " + mediaEsperafilaAgAcabamento);
		System.out.println("Espera Esmeril: " + mediaEsperafilaAgEsmeril);
		System.out.println("Espera Rebarbação Máquina: " + mediaEsperafilaAgRebarbMaq);
		System.out.println("Espera Rebarbação Manual: " + mediaEsperafilaAgRebarbMan);
		System.out.println("Espera Análise Visual: " + mediaEsperafilaAgAnalisVisual);
		System.out.println("Espera Serviço de Terceiros: " + mediaEsperafilaAgTerceir);
		System.out.println("Espera Pintura: " + mediaEsperafilaAgPint);
		System.out.println("Espera Documentação: " + mediaEsperafilaAgDoc);
		System.out.println();
	}

	/**
	 * Imprime estatísticas de desempenho da simulação
	 */
	public void imprimeEstatisticasDesempenho()
	{
		System.out.println();
		System.out.println("Tempo de simulação(ns): " + tempoSimulacao);
		System.out.println("Quantidade de pedidos atendidos: " + pedidosAtendidos);
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
