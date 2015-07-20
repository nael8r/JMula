package jmula.simul;

import jmula.common.clDebug;
import jmula.common.clEstatisticas;
import jmula.common.enumTipoDebug;
import jmula.simul.entity.clEntidadePedido;
import jmula.simul.event.clEventoBase;
import jmula.simul.event.enumTipoEvento;
import jmula.simul.parameters.*;
import org.apache.commons.math3.distribution.*;
import org.apache.commons.math3.random.MersenneTwister;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import static jmula.common.clDebug.dbg;
import static jmula.io.clEntrada.leArquivoConfig;
import static jmula.simul.entity.clEntidades.*;

/**
 * Autores:
 * <p>
 * João Paulo Fernandes Cerqueira César
 * Natanael Ramos
 * Rodolfo Labiapari Mansur Guimarães
 * <p>
 * Classe com principal da aplicação, com os métodos de simulaçao.
 * <p>
 * Observações:
 * <p>
 * - Nomes dos métodos
 * <p>
 * Métodos com os nomes term_X_exec_Y significam que a atividade X foi terminada e que a Y vai ser iniciada,
 * agendando seu término.
 * <p>
 * Métodos com o prefixo tenta_X tem o intuito de tirar atividades que estão em alguma fila e gerar seus respectivos
 * eventos. Tais métodos são invocados quando é reconhecido o término da atividade X, então tenta-se executar a
 * atividade Y, porém não existem recursos disponíveis, então, no método de Y, o método tenta_X é chamado.
 * <p>
 * - Contagem do tempo de simulação
 * <p>
 * A invocação dos métodos paraCronometragem() e comecaCronometragem() tem o intuito de não contar as mensagens de
 * depuração inseridas no código fonte.
 * <p>
 * - Recurso do Java
 * <p>
 * Utilizaram-se recursos do que é chamado de Java Reflection, para mais informações: http://is.gd/o0YFy9
 * <p>
 * - Biblioteca de geração de números aleatórios
 * <p>
 * Utilizou-se a biblioteca math commons do Apache para a geração de números aleatórios, sob a licença
 * Apache Software Foundation (ASF).
 */
public class clSimul
{
	// objeto com os dados estatísticos
	private clEstatisticas est;
	// auxiliares
	// tempo de término da atividade
	private int tmpTAtividade;
	// tempo que a atividade X ficou na fila
	private int tmpTFila;
	private int atualID;
	// relógio da simulação
	private int clock;
	// contador de identificadores das entidades de pedidos
	private Integer ID;
	// map de entidades de pedido criadas
	private HashMap<Integer, clEntidadePedido> pedidosCriados;
	// contadores do tempo de simulação
	private long iniTempo;
	private long fimTempo;
	// gerador de números aleatórios: http://is.gd/DVpC6p
	private MersenneTwister rand;
	// future event list
	private Queue<clEventoBase> fel;
	//filas
	private LinkedList<Integer> filaAgProc;
	private LinkedList<Integer> filaAgAnalis;
	private LinkedList<Integer> filaAgVerifDataMater;
	private LinkedList<Integer> filaAgPlanejamento;
	private LinkedList<Integer> filaAgVerificacao;
	private LinkedList<Integer> filaAgMoldPronto;
	private LinkedList<Integer> filaAgRastr;
	private LinkedList<Integer> filaAgColeta;
	private LinkedList<Integer> filaAgPreeAreia;
	private LinkedList<Integer> filaAgIdentif;
	private LinkedList<Integer> filaAgMaquina;
	private LinkedList<Integer> filaAgManual;
	private LinkedList<Integer> filaAgCaixote;
	private LinkedList<Integer> filaAgSCaixote;
	private LinkedList<Integer> filaAgCheckoutLimpAcab;
	private LinkedList<Integer> filaAgLimpEsp;
	private LinkedList<Integer> filaAgAcabamento;
	private LinkedList<Integer> filaAgEsmeril;
	private LinkedList<Integer> filaAgRebarbMaq;
	private LinkedList<Integer> filaAgRebarbMan;
	private LinkedList<Integer> filaAgAnalisVisual;
	private LinkedList<Integer> filaAgTerceir;
	private LinkedList<Integer> filaAgPint;
	private LinkedList<Integer> filaAgDoc;

	/**
	 * Para a contagem do tempo de simulação.
	 */
	private void paraCronometragem()
	{
		// medição em nanossegundos
		fimTempo = (long) ((System.nanoTime() - iniTempo));

		est.tempoSimulacao += fimTempo;
	}

	/**
	 * Começa a contagem do tempo de simulação
	 */
	private void comecaCronotragem()
	{
		iniTempo = System.nanoTime();
	}

	/**
	 * Método de inicialização da simulação.
	 *
	 * @param seed Semente do gerador de números aleatórios
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public void init(long seed) throws NoSuchFieldException, IllegalAccessException
	{
		// leê o arquivo de configuração para resetar as unidades disponíveis de cada entidade
		leArquivoConfig();

		// reinicia as variáveis
		tmpTAtividade = 0;
		tmpTFila = 0;
		atualID = 0;
		ID = 0;
		clock = 0;
		fimTempo = 0;
		iniTempo = 0;

		// reseta as estatísticas
		setEst(new clEstatisticas());

		// reseta as coleções
		if (pedidosCriados == null)
			pedidosCriados = new HashMap<>();
		else
			pedidosCriados.clear();
		rand = new MersenneTwister(seed);
		if (fel == null)
			fel = new PriorityQueue<>();
		else
			fel.clear();
		if (filaAgProc == null)
			filaAgProc = new LinkedList<>();
		else
			filaAgProc.clear();

		if (filaAgAnalis == null)
			filaAgAnalis = new LinkedList<>();
		else
			filaAgAnalis.clear();

		if (filaAgVerifDataMater == null)
			filaAgVerifDataMater = new LinkedList<>();
		else
			filaAgVerifDataMater.clear();

		if (filaAgPlanejamento == null)
			filaAgPlanejamento = new LinkedList<>();
		else
			filaAgPlanejamento.clear();

		if (filaAgVerificacao == null)
			filaAgVerificacao = new LinkedList<>();
		else
			filaAgVerificacao.clear();

		if (filaAgMoldPronto == null)
			filaAgMoldPronto = new LinkedList<>();
		else
			filaAgMoldPronto.clear();

		if (filaAgRastr == null)
			filaAgRastr = new LinkedList<>();
		else
			filaAgRastr.clear();

		if (filaAgColeta == null)
			filaAgColeta = new LinkedList<>();
		else
			filaAgColeta.clear();

		if (filaAgPreeAreia == null)
			filaAgPreeAreia = new LinkedList<>();
		else
			filaAgPreeAreia.clear();

		if (filaAgIdentif == null)
			filaAgIdentif = new LinkedList<>();
		else
			filaAgIdentif.clear();

		if (filaAgMaquina == null)
			filaAgMaquina = new LinkedList<>();
		else
			filaAgMaquina.clear();

		if (filaAgManual == null)
			filaAgManual = new LinkedList<>();
		else
			filaAgManual.clear();

		if (filaAgCaixote == null)
			filaAgCaixote = new LinkedList<>();
		else
			filaAgCaixote.clear();

		if (filaAgSCaixote == null)
			filaAgSCaixote = new LinkedList<>();
		else
			filaAgSCaixote.clear();

		if (filaAgCheckoutLimpAcab == null)
			filaAgCheckoutLimpAcab = new LinkedList<>();
		else
			filaAgCheckoutLimpAcab.clear();

		if (filaAgLimpEsp == null)
			filaAgLimpEsp = new LinkedList<>();
		else
			filaAgLimpEsp.clear();

		if (filaAgAcabamento == null)
			filaAgAcabamento = new LinkedList<>();
		else
			filaAgAcabamento.clear();

		if (filaAgEsmeril == null)
			filaAgEsmeril = new LinkedList<>();
		else
			filaAgEsmeril.clear();

		if (filaAgRebarbMaq == null)
			filaAgRebarbMaq = new LinkedList<>();
		else
			filaAgRebarbMaq.clear();

		if (filaAgRebarbMan == null)
			filaAgRebarbMan = new LinkedList<>();
		else
			filaAgRebarbMan.clear();

		if (filaAgAnalisVisual == null)
			filaAgAnalisVisual = new LinkedList<>();
		else
			filaAgAnalisVisual.clear();

		if (filaAgTerceir == null)
			filaAgTerceir = new LinkedList<>();
		else
			filaAgTerceir.clear();

		if (filaAgPint == null)
			filaAgPint = new LinkedList<>();
		else
			filaAgPint.clear();

		if (filaAgDoc == null)
			filaAgDoc = new LinkedList<>();
		else
			filaAgDoc.clear();

	}

	/**
	 * Nascedouro, gera as entidades de pedido.
	 */
	private void nascedouro()
	{
		// variável auxiliar
		int time = 0;

		// Só pra não criar objetos dentro do laço
		NormalDistribution norm = new NormalDistribution(rand, clNormParams.cheg_ped_media, clNormParams.cheg_ped_sd);

		clEventoBase novoEvento;

		// quantidade máxima de pedidos que devem ser gerados
		for (int i = 0; i < clSimulParams.MAX_PEDIDO; i++)
		{
			// insere o novo pedido
			pedidosCriados.putIfAbsent(ID, new clEntidadePedido(ID));

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "pedido de ID: " + ID + " criado");
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = (int) Math.ceil(norm.sample());

			// incrementa no time, para que as próximas atividades tenham seu tempo de término baseado no da última
			// atividade
			time += tmpTAtividade;

			// cria o evento de chegada
			novoEvento = new clEventoBase(this, time,
					enumTipoEvento.CHEGADA_PEDIDO,
					ID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.CHEGADA_PEDIDO.name() +
					" pedido de ID: " + ID +
					" termina em: " + time);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);

			// define o tempo de chegada na fila das atividades
			pedidosCriados.get(ID).setMarcaTempoEntrada(time);

			// incrementa o identificador de atividades
			ID++;
		}

	}

	/**
	 * Calcula o tempo de término do evento, somando ao clock a constante ou número gerado pela distribuição
	 *
	 * @param dist Distribuição continua
	 * @return Tempo de término do evento
	 */
	private int tFinalEvento(AbstractRealDistribution dist)
	{
		return (int) Math.ceil(clock + dist.sample());
	}

	/**
	 * Calcula o tempo de término do evento, somando ao clock a constante ou número gerado pela distribuição
	 *
	 * @param dist Distribuição discreta
	 * @return Tempo de término do evento
	 */
	private int tFinalEvento(AbstractIntegerDistribution dist)
	{
		return (int) Math.ceil(clock + dist.sample());
	}

	/**
	 * Calcula o tempo de término do evento, somando ao clock a constante ou número gerado pela distribuição
	 *
	 * @param cons constante de duração
	 * @return Tempo de término do evento
	 */
	private int tFinalEvento(int cons)
	{
		return (int) Math.ceil(clock + cons);
	}

	/**
	 * Término da chegada de um pedido e execução do processamento e expedição.
	 *
	 * @param ev evento gerador
	 */
	private void term_ChegPed_Exec_ProcExp(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;

		// Se o recurso não está disponível
		if (funcSetorCompra == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgProc.contains(ev.getPecaID()))
				filaAgProc.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgProc de tamanho: " + filaAgProc.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);
		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgProc.contains(ev.getPecaID()))
				filaAgProc.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgProc de tamanho: " + filaAgProc.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgProc.isEmpty() && funcSetorCompra != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgProc.removeFirst();
				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgProc++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgProc += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorCompra--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de compra alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorCompra);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.proc_exp_ped_min,
						clTriParams.proc_exp_ped_med,
						clTriParams.proc_exp_ped_max));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_PROC,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_PROC.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);
			}
		}
	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_ChegPed_Exec_ProcExp(clEventoBase ev)
	{
		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgProc.isEmpty() && funcSetorCompra != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgProc.removeFirst();
			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgProc++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgProc += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorCompra--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de compra alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorCompra);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.proc_exp_ped_min, clTriParams.proc_exp_ped_med, clTriParams.proc_exp_ped_max));
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_PROC,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_PROC.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);
		}
	}

	/**
	 * Termina processo e expedição e executa análise química e verificação dos materiais existentes.
	 *
	 * @param ev Evento gerador
	 */
	private void term_ProcExp_Exec_QuimicVerifMatEx(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;

		// Se o recurso não está disponível
		if (funcSetorExpedicao == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgAnalis.contains(ev.getPecaID()))
				filaAgAnalis.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgAnalis de tamanho: " + filaAgAnalis.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_ChegPed_Exec_ProcExp(ev);

		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgAnalis.contains(ev.getPecaID()))
				filaAgAnalis.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgAnalis de tamanho: " + filaAgAnalis.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgAnalis.isEmpty() && funcSetorExpedicao != 0; i++)
			{

				// pega a primeira atividade que está na fila
				atualID = filaAgAnalis.removeFirst();
				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgAnalis++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgAnalis += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorExpedicao--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de expedição alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorExpedicao);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.ana_quimic_verif_mat_ex_min, clTriParams.ana_quimic_verif_mat_ex_med, clTriParams.ana_quimic_verif_mat_ex_max));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_QUIMIC_VERIFI_MAT_EX,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_QUIMIC_VERIFI_MAT_EX.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);

//				pedidosCriados.get(atualID).setMarcaTempoEntrada(pedidosCriados.get(atualID).getMarcaTempoEntrada() +
//						(tmpTAtividade - clock) +
//						tmpTFila);
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_ProcExp_Exec_QuimicVerifMatEx(clEventoBase ev)
	{
		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgAnalis.isEmpty() && funcSetorExpedicao != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgAnalis.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgAnalis++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgAnalis += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorExpedicao--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de expedição alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorExpedicao);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.ana_quimic_verif_mat_ex_min, clTriParams.ana_quimic_verif_mat_ex_med, clTriParams.ana_quimic_verif_mat_ex_max));
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_QUIMIC_VERIFI_MAT_EX,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_QUIMIC_VERIFI_MAT_EX.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);
		}
	}

	/**
	 * Termina Análise Quimica e Verificaçaõ de Materias Existentes e Executa Verificar datas, matéria primas.
	 *
	 * @param ev Evento gerador
	 */
	private void term_QuimicVerifMatEx_Exec_VerifDatMatPrim(clEventoBase ev)
	{

		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorExpedicao == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgVerifDataMater.contains(ev.getPecaID()))
				filaAgVerifDataMater.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgVerifDataMater de tamanho: " + filaAgVerifDataMater.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_ProcExp_Exec_QuimicVerifMatEx(ev);
		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgVerifDataMater.contains(ev.getPecaID()))
				filaAgVerifDataMater.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgVerifDataMater de tamanho: " + filaAgVerifDataMater.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgVerifDataMater.isEmpty() && funcSetorExpedicao != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgVerifDataMater.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgVerifDataMater++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgVerifDataMater += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorExpedicao--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de expedição alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorExpedicao);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.verif_data_mat_prim_min, clUnifParams.verif_data_mat_prim_max));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_VERIF_DATAS_MAT_PRIM,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_VERIF_DATAS_MAT_PRIM.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);

			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_QuimicVerifMatEx_Exec_VerifDatMatPrim(clEventoBase ev)
	{
		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgVerifDataMater.isEmpty() && funcSetorExpedicao != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgVerifDataMater.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgVerifDataMater++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgVerifDataMater += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorExpedicao--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de expedição alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorExpedicao);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.verif_data_mat_prim_min, clUnifParams.verif_data_mat_prim_max));
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_VERIF_DATAS_MAT_PRIM,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_VERIF_DATAS_MAT_PRIM.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);
		}

	}

	/**
	 * Termina Verificar datas, matéria primas e executa Estudo, Planejamento dos Processos de Produção.
	 *
	 * @param ev Evento gerador
	 */
	private void term_VerifDatMatPrim_Exec_PlanProcProd(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorExpedicao == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgPlanejamento.contains(ev.getPecaID()))
				filaAgPlanejamento.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgPlanejamento de tamanho: " + filaAgPlanejamento.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_QuimicVerifMatEx_Exec_VerifDatMatPrim(ev);
		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgPlanejamento.contains(ev.getPecaID()))
				filaAgPlanejamento.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgPlanejamento de tamanho: " + filaAgPlanejamento.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgPlanejamento.isEmpty() && funcSetorExpedicao != 0; i++)
			{

				// pega a primeira atividade que está na fila
				atualID = filaAgPlanejamento.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgPlanejamento++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgPlanejamento += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorExpedicao--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de expedição alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorExpedicao);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new NormalDistribution(rand, clNormParams.est_plan_proc_prod_media, clNormParams.est_plan_proc_prod_sd));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_PLAN_PROC_PROD,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_PLAN_PROC_PROD.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_VerifDatMatPrim_Exec_PlanProcProd(clEventoBase ev)
	{
		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgPlanejamento.isEmpty() && funcSetorExpedicao != 0; i++)
		{

			// pega a primeira atividade que está na fila
			atualID = filaAgPlanejamento.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgPlanejamento++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgPlanejamento += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorExpedicao--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de expedição alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorExpedicao);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(new NormalDistribution(rand, clNormParams.est_plan_proc_prod_media, clNormParams.est_plan_proc_prod_sd));
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_PLAN_PROC_PROD,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_PLAN_PROC_PROD.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);
		}

	}

	/**
	 * Termina Planejamento dos Processos de Produção e executa Verificação de Arquivos de Modelagem no Depósito
	 *
	 * @param ev Evento gerador
	 */
	private void term_PlanProcProd_Exec_VerifArqModDepot(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorModel == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgVerificacao.contains(ev.getPecaID()))
				filaAgVerificacao.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgVerificacao de tamanho: " + filaAgVerificacao.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);
			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_VerifDatMatPrim_Exec_PlanProcProd(ev);
		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgVerificacao.contains(ev.getPecaID()))
				filaAgVerificacao.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgVerificacao de tamanho: " + filaAgVerificacao.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgVerificacao.isEmpty() && funcSetorModel != 0; i++)
			{
				double prob = rand.nextDouble();
				// pega a primeira atividade que está na fila
				atualID = filaAgVerificacao.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgVerificacao++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgVerificacao += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorModel--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de modelagem alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorModel);
				comecaCronotragem();

				if (prob <= 0.3)
				{

					// calcula o tempo de término da atividade
					tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.verif_arq_mod_dep_min, clUnifParams.verif_arq_mod_dep_max));
					// cria o novo evento
					clEventoBase novoEvento = new clEventoBase(ev,
							tmpTAtividade,
							enumTipoEvento.TERM_VERIF_ARQ_MOD_DEPOT_CRIA,
							atualID);

					// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
					paraCronometragem();
					dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_VERIF_ARQ_MOD_DEPOT_CRIA.name() +
							" pedido de ID: " + atualID +
							" termina em: " + tmpTAtividade);
					comecaCronotragem();

					// adiciona o novo evento à fel
					fel.add(novoEvento);

				}
				else
				{

					// calcula o tempo de término da atividade
					tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.verif_arq_mod_dep_min, clUnifParams.verif_arq_mod_dep_max));
					// cria o novo evento
					clEventoBase novoEvento = new clEventoBase(ev,
							tmpTAtividade,
							enumTipoEvento.TERM_VERIF_ARQ_MOD_DEPOT_RASTR,
							atualID);

					// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
					paraCronometragem();
					dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_VERIF_ARQ_MOD_DEPOT_RASTR.name() +
							" pedido de ID: " + atualID +
							" termina em: " + tmpTAtividade);
					comecaCronotragem();

					// adiciona o novo evento à fel
					fel.add(novoEvento);

				}
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_PlanProcProd_Exec_VerifArqModDepot(clEventoBase ev)
	{
		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgVerificacao.isEmpty() && funcSetorModel != 0; i++)
		{
			double prob = rand.nextDouble();
			// pega a primeira atividade que está na fila
			atualID = filaAgVerificacao.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgVerificacao++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgVerificacao += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorModel--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de modelagem alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorModel);
			comecaCronotragem();

			if (prob <= 0.3)
			{

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.verif_arq_mod_dep_min, clUnifParams.verif_arq_mod_dep_max));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_VERIF_ARQ_MOD_DEPOT_CRIA,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_VERIF_ARQ_MOD_DEPOT_CRIA.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);

			}
			else
			{

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.verif_arq_mod_dep_min, clUnifParams.verif_arq_mod_dep_max));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_VERIF_ARQ_MOD_DEPOT_RASTR,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_VERIF_ARQ_MOD_DEPOT_RASTR.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);

			}
		}

	}

	/**
	 * Termina Verificação de Arquivos de Modelagem no Depósito e executa Criação do Modelo
	 *
	 * @param ev Evento gerador
	 */
	private void term_VerifArqModDepot_Exec_CriaMod(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorModel == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgMoldPronto.contains(ev.getPecaID()))
				filaAgMoldPronto.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgMoldPronto de tamanho: " + filaAgMoldPronto.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_PlanProcProd_Exec_VerifArqModDepot(ev);

		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgMoldPronto.contains(ev.getPecaID()))
				filaAgMoldPronto.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgMoldPronto de tamanho: " + filaAgMoldPronto.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgMoldPronto.isEmpty() && funcSetorModel != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgMoldPronto.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgMoldPronto++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgMoldPronto += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorModel--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de modelagem alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorModel);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.cria_mod_min, clTriParams.cria_mod_med, clTriParams.cria_mod_max));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_CRIA_MOD,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_CRIA_MOD.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_VerifArqModDepot_Exec_CriaMod(clEventoBase ev)
	{

		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgMoldPronto.isEmpty() && funcSetorModel != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgMoldPronto.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgMoldPronto++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgMoldPronto += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorModel--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de modelagem alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorModel);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.cria_mod_min, clTriParams.cria_mod_med, clTriParams.cria_mod_max));
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_CRIA_MOD,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_CRIA_MOD.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);
		}
	}

	/**
	 * Termina Criação do Modelo ou verificação de arquivos de modelagem no depósito e executa Ratreabilidade,
	 * Histórico do Material
	 *
	 * @param ev Evento gerador
	 */
	private void term_CriaMod_ou_VerifArqModDepot_Exec_Rastr(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorModel == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgRastr.contains(ev.getPecaID()))
				filaAgRastr.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgRastr de tamanho: " + filaAgRastr.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			switch (ev.gettEvento())
			{
				case TERM_VERIF_ARQ_MOD_DEPOT_RASTR:
					// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
					tenta_term_PlanProcProd_Exec_VerifArqModDepot(ev);
					break;
				case TERM_CRIA_MOD:
					// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
					tenta_term_VerifArqModDepot_Exec_CriaMod(ev);
					break;
			}
		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgRastr.contains(ev.getPecaID()))
				filaAgRastr.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgRastr de tamanho: " + filaAgRastr.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgRastr.isEmpty() && funcSetorModel != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgRastr.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgRastr++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgRastr += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorModel--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de modelagem alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorModel);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.rastr_hist_mat_min, clTriParams.rastr_hist_mat_med, clTriParams.rastr_hist_mat_max));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_RASTR,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_RASTR.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_CriaMod_ou_VerifArqModDepot_Exec_Rastr(clEventoBase ev)
	{

		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgRastr.isEmpty() && funcSetorModel != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgRastr.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgRastr++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgRastr += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorModel--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de modelagem alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorModel);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.rastr_hist_mat_min, clTriParams.rastr_hist_mat_med, clTriParams.rastr_hist_mat_max));
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_RASTR,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_RASTR.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);
		}

	}

	/**
	 * Termina Ratreabilidade, Histórico do Material e executa Colocar Informações na Peça
	 *
	 * @param ev Evento gerador
	 */
	private void term_Rastr_Exec_InfoPeca(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorModel == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgColeta.contains(ev.getPecaID()))
				filaAgColeta.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgColeta de tamanho: " + filaAgColeta.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_CriaMod_ou_VerifArqModDepot_Exec_Rastr(ev);

		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgColeta.contains(ev.getPecaID()))
				filaAgColeta.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgColeta de tamanho: " + filaAgColeta.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgColeta.isEmpty() && funcSetorModel != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgColeta.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgColeta++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgColeta += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorModel--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de modelagem alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorModel);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.coloc_info_peca_min, clTriParams.coloc_info_peca_med, clTriParams.coloc_info_peca_max));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_INFO_PECA,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_INFO_PECA.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);

			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_Rastr_Exec_InfoPeca(clEventoBase ev)
	{

		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgColeta.isEmpty() && funcSetorModel != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgColeta.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgColeta++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgColeta += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorModel--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de modelagem alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorModel);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.coloc_info_peca_min, clTriParams.coloc_info_peca_med, clTriParams.coloc_info_peca_max));
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_INFO_PECA,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_INFO_PECA.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);

		}
	}

	/**
	 * Termina Colocar Informações na Peça e executa Preenchimento com Areia
	 *
	 * @param ev Evento gerador
	 */
	private void term_InfoPeca_Exec_PreeAreia(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorProducao == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgPreeAreia.contains(ev.getPecaID()))
				filaAgPreeAreia.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgPreeAreia de tamanho: " + filaAgPreeAreia.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_Rastr_Exec_InfoPeca(ev);
		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgPreeAreia.contains(ev.getPecaID()))
				filaAgPreeAreia.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgPreeAreia de tamanho: " + filaAgPreeAreia.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgPreeAreia.isEmpty() && funcSetorProducao != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgPreeAreia.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgPreeAreia++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgPreeAreia += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorProducao--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de produção alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorProducao);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new NormalDistribution(rand, clNormParams.pree_areia_media, clNormParams.pree_areia_sd));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_PREE_AREIA,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_PREE_AREIA.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_InfoPeca_Exec_PreeAreia(clEventoBase ev)
	{

		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgPreeAreia.isEmpty() && funcSetorProducao != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgPreeAreia.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgPreeAreia++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgPreeAreia += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorProducao--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de produção alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorProducao);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(new NormalDistribution(rand, clNormParams.pree_areia_media, clNormParams.pree_areia_sd));
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_PREE_AREIA,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_PREE_AREIA.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);
		}

	}

	/**
	 * Termina Preenchimento com Areia e executa Identificação do Material
	 *
	 * @param ev Evento gerador
	 */
	private void term_Pree_Areia_Exec_IdMaterial(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorProducao == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgIdentif.contains(ev.getPecaID()))
				filaAgIdentif.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgIdentif de tamanho: " + filaAgIdentif.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);
			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_InfoPeca_Exec_PreeAreia(ev);
		}
		else
		{
			double prob;
			// adiciona a atividade a respectiva fila
			if (!filaAgIdentif.contains(ev.getPecaID()))
				filaAgIdentif.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgIdentif de tamanho: " + filaAgIdentif.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgIdentif.isEmpty() && funcSetorProducao != 0; i++)
			{

				prob = rand.nextDouble();
				// pega a primeira atividade que está na fila
				atualID = filaAgIdentif.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgIdentif++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgIdentif += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorProducao--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de produção alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorProducao);
				comecaCronotragem();

				// resina
				if (prob < 0.3)
				{
					if (prob < 0.1)
					{
						// calcula o tempo de término da atividade
						tmpTAtividade = tFinalEvento(clConst.id_mat_cte);
						// cria o novo evento
						clEventoBase novoEvento = new clEventoBase(ev,
								tmpTAtividade,
								enumTipoEvento.TERM_ID_MATERIAL_CXT,
								atualID);

						// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
						paraCronometragem();
						dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_ID_MATERIAL_CXT.name() +
								" pedido de ID: " + atualID +
								" termina em: " + tmpTAtividade);
						comecaCronotragem();

						// adiciona o novo evento à fel
						fel.add(novoEvento);
					}
					else
					{
						// calcula o tempo de término da atividade
						tmpTAtividade = tFinalEvento(clConst.id_mat_cte);
						// cria o novo evento
						clEventoBase novoEvento = new clEventoBase(ev,
								tmpTAtividade,
								enumTipoEvento.TERM_ID_MATERIAL_S_CXT,
								atualID);

						// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
						paraCronometragem();
						dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_ID_MATERIAL_S_CXT.name() +
								" pedido de ID: " + atualID +
								" termina em: " + tmpTAtividade);
						comecaCronotragem();

						// adiciona o novo evento à fel
						fel.add(novoEvento);
					}
				}
				// máquina
				else if (prob >= 0.3 && prob < 0.6)
				{
					// calcula o tempo de término da atividade
					tmpTAtividade = tFinalEvento(clConst.id_mat_cte);
					// cria o novo evento
					clEventoBase novoEvento = new clEventoBase(ev,
							tmpTAtividade,
							enumTipoEvento.TERM_ID_MATERIAL_MAQ,
							atualID);

					// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
					paraCronometragem();
					dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_ID_MATERIAL_MAQ.name() +
							" pedido de ID: " + atualID +
							" termina em: " + tmpTAtividade);
					comecaCronotragem();

					// adiciona o novo evento à fel
					fel.add(novoEvento);
				}
				// manual
				else
				{
					// calcula o tempo de término da atividade
					tmpTAtividade = tFinalEvento(clConst.id_mat_cte);
					// cria o novo evento
					clEventoBase novoEvento = new clEventoBase(ev,
							tmpTAtividade,
							enumTipoEvento.TERM_ID_MATERIAL_MAN,
							atualID);

					// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
					paraCronometragem();
					dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_ID_MATERIAL_MAN.name() +
							" pedido de ID: " + atualID +
							" termina em: " + tmpTAtividade);
					comecaCronotragem();

					// adiciona o novo evento à fel
					fel.add(novoEvento);

				}
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_Pree_Areia_Exec_IdMaterial(clEventoBase ev)
	{
		double prob;

		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgIdentif.isEmpty() && funcSetorProducao != 0; i++)
		{
			prob = rand.nextDouble();
			// pega a primeira atividade que está na fila
			atualID = filaAgIdentif.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgIdentif++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgIdentif += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorProducao--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de produção alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorProducao);
			comecaCronotragem();

			// resina
			if (prob < 0.3)
			{
				if (prob < 0.1)
				{
					// calcula o tempo de término da atividade
					tmpTAtividade = tFinalEvento(clConst.id_mat_cte);
					// cria o novo evento
					clEventoBase novoEvento = new clEventoBase(ev,
							tmpTAtividade,
							enumTipoEvento.TERM_ID_MATERIAL_CXT,
							atualID);

					// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
					paraCronometragem();
					dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_ID_MATERIAL_CXT.name() +
							" pedido de ID: " + atualID +
							" termina em: " + tmpTAtividade);
					comecaCronotragem();

					// adiciona o novo evento à fel
					fel.add(novoEvento);
				}
				else
				{
					// calcula o tempo de término da atividade
					tmpTAtividade = tFinalEvento(clConst.id_mat_cte);
					// cria o novo evento
					clEventoBase novoEvento = new clEventoBase(ev,
							tmpTAtividade,
							enumTipoEvento.TERM_ID_MATERIAL_S_CXT,
							atualID);

					// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
					paraCronometragem();
					dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_ID_MATERIAL_S_CXT.name() +
							" pedido de ID: " + atualID +
							" termina em: " + tmpTAtividade);
					comecaCronotragem();

					// adiciona o novo evento à fel
					fel.add(novoEvento);
				}
			}
			// máquina
			else if (prob >= 0.3 && prob < 0.6)
			{
				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(clConst.id_mat_cte);
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_ID_MATERIAL_MAQ,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_ID_MATERIAL_MAQ.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);
			}
			// manual
			else
			{
				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(clConst.id_mat_cte);
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_ID_MATERIAL_MAN,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_ID_MATERIAL_MAN.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);

			}
		}

	}

	/**
	 * Termina Identificação do Material e executa produção da peça com resina com caixa
	 *
	 * @param ev Evento gerador
	 */
	private void term_IdMaterial_Exec_ProdCxt(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorProducao == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgCaixote.contains(ev.getPecaID()))
				filaAgCaixote.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgCaixote de tamanho: " + filaAgCaixote.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_Pree_Areia_Exec_IdMaterial(ev);

		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgCaixote.contains(ev.getPecaID()))
				filaAgCaixote.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgCaixote de tamanho: " + filaAgCaixote.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgCaixote.isEmpty() && funcSetorProducao != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgCaixote.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgCaixote++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgCaixote += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorProducao--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de produção alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorProducao);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.resin_cxt_min, clTriParams.resin_cxt_med, clTriParams.resin_cxt_max));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_PROD_CXT,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_PROD_CXT.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);

			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_IdMaterial_Exec_ProdCxt(clEventoBase ev)
	{

		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgCaixote.isEmpty() && funcSetorProducao != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgCaixote.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgCaixote++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgCaixote += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorProducao--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de produção alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorProducao);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.resin_cxt_min, clTriParams.resin_cxt_med, clTriParams.resin_cxt_max));
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_PROD_CXT,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_PROD_CXT.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);

		}
	}

	/**
	 * Termina Identificação do Material e executa produção da peça com resina sem caixa
	 *
	 * @param ev Evento gerador
	 */
	private void term_IdMaterial_Exec_ProdSCxt(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorProducao == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgSCaixote.contains(ev.getPecaID()))
				filaAgSCaixote.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgSCaixote de tamanho: " + filaAgSCaixote.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_Pree_Areia_Exec_IdMaterial(ev);
		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgSCaixote.contains(ev.getPecaID()))
				filaAgSCaixote.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgSCaixote de tamanho: " + filaAgSCaixote.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgSCaixote.isEmpty() && funcSetorProducao != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgSCaixote.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgSCaixote++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgSCaixote += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorProducao--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de produção alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorProducao);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(clConst.resin_s_cxt_cte);
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_PROD_S_CXT,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_PROD_S_CXT.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_IdMaterial_Exec_ProdSCxt(clEventoBase ev)
	{

		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgSCaixote.isEmpty() && funcSetorProducao != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgSCaixote.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgSCaixote++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgSCaixote += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorProducao--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de produção alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorProducao);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(clConst.resin_s_cxt_cte);
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_PROD_S_CXT,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_PROD_S_CXT.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);
		}

	}

	/**
	 * Termina Identificação do Material e executa produção da peça por máquina
	 *
	 * @param ev Evento gerador
	 */
	private void term_IdMaterial_Exec_ProdMaq(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorProducao == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgMaquina.contains(ev.getPecaID()))
				filaAgMaquina.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgMaquina de tamanho: " + filaAgMaquina.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_Pree_Areia_Exec_IdMaterial(ev);
		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgMaquina.contains(ev.getPecaID()))
				filaAgMaquina.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgMaquina de tamanho: " + filaAgMaquina.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgMaquina.isEmpty() && funcSetorProducao != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgMaquina.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgMaquina++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgMaquina += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorProducao--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de produção alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorProducao);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(clConst.prod_maq_cte);
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_PROD_MAQ,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_PROD_MAQ.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_IdMaterial_Exec_ProdMaq(clEventoBase ev)
	{

		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgMaquina.isEmpty() && funcSetorProducao != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgMaquina.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgMaquina++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgMaquina += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorProducao--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de produção alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorProducao);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(clConst.prod_maq_cte);
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_PROD_MAQ,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_PROD_MAQ.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);
		}
	}

	/**
	 * Termina Identificação do Material e executa produção da peça manual
	 *
	 * @param ev Evento gerador
	 */
	private void term_IdMaterial_Exec_ProdMan(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcProdManual == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgManual.contains(ev.getPecaID()))
				filaAgManual.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgManual de tamanho: " + filaAgManual.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_Pree_Areia_Exec_IdMaterial(ev);
		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgManual.contains(ev.getPecaID()))
				filaAgManual.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgManual de tamanho: " + filaAgManual.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgManual.isEmpty() && funcProdManual != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgManual.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgManual++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgManual += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcProdManual--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de produção manual alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcProdManual);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(clConst.prod_man_cte);
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_PROD_MAN,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_PROD_MAN.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_IdMaterial_Exec_ProdMan(clEventoBase ev)
	{

		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgManual.isEmpty() && funcProdManual != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgManual.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgManual++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgManual += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcProdManual--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de produção manual alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcProdManual);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(clConst.prod_man_cte);
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_PROD_MAN,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_PROD_MAN.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);
		}
	}

	/**
	 * Termina Produção e executa Checkout/Limpeza e Acabamento
	 *
	 * @param ev Evento gerador
	 */
	private void term_Prod_Exec_Checkout(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorFinal == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgCheckoutLimpAcab.contains(ev.getPecaID()))
				filaAgCheckoutLimpAcab.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgCheckoutLimpAcab de tamanho: " + filaAgCheckoutLimpAcab.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			switch (ev.gettEvento())
			{
				case TERM_PROD_CXT:
					// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
					tenta_term_IdMaterial_Exec_ProdCxt(ev);
					break;
				case TERM_PROD_S_CXT:
					// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
					tenta_term_IdMaterial_Exec_ProdSCxt(ev);
					break;
				case TERM_PROD_MAQ:
					// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
					tenta_term_IdMaterial_Exec_ProdMaq(ev);
					break;
				case TERM_PROD_MAN:
					// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
					tenta_term_IdMaterial_Exec_ProdMan(ev);
					break;
			}
		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgCheckoutLimpAcab.contains(ev.getPecaID()))
				filaAgCheckoutLimpAcab.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgCheckoutLimpAcab de tamanho: " + filaAgCheckoutLimpAcab.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgCheckoutLimpAcab.isEmpty() && funcSetorFinal != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgCheckoutLimpAcab.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgCheckoutLimpAcab++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgCheckoutLimpAcab += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorFinal--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de finalização alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorFinal);
				comecaCronotragem();

				// se tem resina ou não
				if (ev.gettEvento() == enumTipoEvento.TERM_PROD_CXT || ev.gettEvento() == enumTipoEvento.TERM_PROD_S_CXT)
				{
					// calcula o tempo de término da atividade
					tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.rel_checkout_lim_acab_min, clUnifParams.rel_checkout_lim_acab_max));
					// cria o novo evento
					clEventoBase novoEvento = new clEventoBase(ev,
							tmpTAtividade,
							enumTipoEvento.TERM_CHECKOUT_LIMP_ACAB_RESIN,
							atualID);

					// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
					paraCronometragem();
					dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_CHECKOUT_LIMP_ACAB_RESIN.name() +
							" pedido de ID: " + atualID +
							" termina em: " + tmpTAtividade);
					comecaCronotragem();

					// adiciona o novo evento à fel
					fel.add(novoEvento);

				}
				else
				{
					// calcula o tempo de término da atividade
					tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.rel_checkout_lim_acab_min, clUnifParams.rel_checkout_lim_acab_max));
					// cria o novo evento
					clEventoBase novoEvento = new clEventoBase(ev,
							tmpTAtividade,
							enumTipoEvento.TERM_CHECKOUT_LIMP_ACAB,
							atualID);

					// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
					paraCronometragem();
					dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_CHECKOUT_LIMP_ACAB.name() +
							" pedido de ID: " + atualID +
							" termina em: " + tmpTAtividade);
					comecaCronotragem();

					// adiciona o novo evento à fel
					fel.add(novoEvento);
				}
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_Prod_Exec_Checkout(clEventoBase ev)
	{

		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgCheckoutLimpAcab.isEmpty() && funcSetorFinal != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgCheckoutLimpAcab.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgCheckoutLimpAcab++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgCheckoutLimpAcab += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorFinal--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de finalização alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorFinal);
			comecaCronotragem();

			// se tem resina ou não
			if (ev.gettEvento() == enumTipoEvento.TERM_PROD_CXT || ev.gettEvento() == enumTipoEvento.TERM_PROD_S_CXT)
			{
				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.rel_checkout_lim_acab_min, clUnifParams.rel_checkout_lim_acab_max));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_CHECKOUT_LIMP_ACAB_RESIN,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_CHECKOUT_LIMP_ACAB_RESIN.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);

			}
			else
			{
				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.rel_checkout_lim_acab_min, clUnifParams.rel_checkout_lim_acab_max));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_CHECKOUT_LIMP_ACAB,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_CHECKOUT_LIMP_ACAB.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);
			}
		}

	}

	/**
	 * Termina Checkout/Limpeza e Acabamento e executa Limpeza Especial
	 *
	 * @param ev Evento gerador
	 */
	private void term_Checkout_Exec_LimpEsp(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcCheckoutResina == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgLimpEsp.contains(ev.getPecaID()))
				filaAgLimpEsp.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgLimpEsp de tamanho: " + filaAgLimpEsp.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);
			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_Prod_Exec_Checkout(ev);
		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgLimpEsp.contains(ev.getPecaID()))
				filaAgLimpEsp.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgLimpEsp de tamanho: " + filaAgLimpEsp.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgLimpEsp.isEmpty() && funcCheckoutResina != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgLimpEsp.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgLimpEsp++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgLimpEsp += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcCheckoutResina--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor da limpeza especial alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcCheckoutResina);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(clConst.limp_esp_cte);
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_LIMP_RESIN,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_LIMP_RESIN.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);

			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_Checkout_Exec_LimpEsp(clEventoBase ev)
	{
		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgLimpEsp.isEmpty() && funcCheckoutResina != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgLimpEsp.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgLimpEsp++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgLimpEsp += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcCheckoutResina--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor da limpeza especial alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcCheckoutResina);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(clConst.limp_esp_cte);
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_LIMP_RESIN,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_LIMP_RESIN.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);

		}

	}

	/**
	 * Termina Checkout/Limpeza e Acabamento ou Limpeza Especial e executa Acabamento
	 *
	 * @param ev Evento gerador
	 */
	private void term_CheckoutLimpEsp_Exec_Acab(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorFinal == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgAcabamento.contains(ev.getPecaID()))
				filaAgAcabamento.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgAcabamento de tamanho: " + filaAgAcabamento.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			switch (ev.gettEvento())
			{
				case TERM_CHECKOUT_LIMP_ACAB:
					// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
					tenta_term_Prod_Exec_Checkout(ev);
					break;
				case TERM_CHECKOUT_LIMP_ACAB_RESIN:
					// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
					tenta_term_Checkout_Exec_LimpEsp(ev);
					break;
			}
		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgAcabamento.contains(ev.getPecaID()))
				filaAgAcabamento.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgAcabamento de tamanho: " + filaAgAcabamento.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgAcabamento.isEmpty() && funcSetorFinal != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgAcabamento.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgAcabamento++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgAcabamento += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorFinal--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor da finalização alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorFinal);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new NormalDistribution(rand, clNormParams.acab_media, clNormParams.acab_sd));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_ACAB,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_ACAB.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_CheckoutLimpEsp_Exec_Acab(clEventoBase ev)
	{
		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgAcabamento.isEmpty() && funcSetorFinal != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgAcabamento.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgAcabamento++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgAcabamento += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorFinal--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor da finalização alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorFinal);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(new NormalDistribution(rand, clNormParams.acab_media, clNormParams.acab_sd));
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_ACAB,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_ACAB.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);
		}
	}

	/**
	 * Termina  Acabamento e executa Esmeril
	 *
	 * @param ev Evento gerador
	 */
	private void term_Acab_Exec_Esmeril(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorFinal == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgEsmeril.contains(ev.getPecaID()))
				filaAgEsmeril.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgEsmeril de tamanho: " + filaAgEsmeril.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_CheckoutLimpEsp_Exec_Acab(ev);

		}
		else
		{
			double prob;

			// adiciona a atividade a respectiva fila
			if (!filaAgEsmeril.contains(ev.getPecaID()))
				filaAgEsmeril.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgEsmeril de tamanho: " + filaAgEsmeril.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgEsmeril.isEmpty() && funcSetorFinal != 0; i++)
			{

				prob = rand.nextDouble();
				// pega a primeira atividade que está na fila
				atualID = filaAgEsmeril.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgEsmeril++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgEsmeril += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorFinal--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor da finalização alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorFinal);
				comecaCronotragem();

				if (prob < 0.4)
				{
					// calcula o tempo de término da atividade
					tmpTAtividade = tFinalEvento(clConst.esmeril_cte);
					// cria o novo evento
					clEventoBase novoEvento = new clEventoBase(ev,
							tmpTAtividade,
							enumTipoEvento.TERM_ESMER_MAQ,
							atualID);

					// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
					paraCronometragem();
					dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_ESMER_MAQ.name() +
							" pedido de ID: " + atualID +
							" termina em: " + tmpTAtividade);
					comecaCronotragem();

					// adiciona o novo evento à fel
					fel.add(novoEvento);

				}
				else
				{
					// calcula o tempo de término da atividade
					tmpTAtividade = tFinalEvento(clConst.esmeril_cte);
					// cria o novo evento
					clEventoBase novoEvento = new clEventoBase(ev,
							tmpTAtividade,
							enumTipoEvento.TERM_ESMER_MAN,
							atualID);

					// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
					paraCronometragem();
					dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_ESMER_MAN.name() +
							" pedido de ID: " + atualID +
							" termina em: " + tmpTAtividade);
					comecaCronotragem();

					// adiciona o novo evento à fel
					fel.add(novoEvento);

				}
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_Acab_Exec_Esmeril(clEventoBase ev)
	{
		double prob;

		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgEsmeril.isEmpty() && funcSetorFinal != 0; i++)
		{

			prob = rand.nextDouble();
			// pega a primeira atividade que está na fila
			atualID = filaAgEsmeril.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgEsmeril++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgEsmeril += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorFinal--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor da finalização alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorFinal);
			comecaCronotragem();

			if (prob < 0.4)
			{
				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(clConst.esmeril_cte);
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_ESMER_MAQ,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_ESMER_MAQ.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);

			}
			else
			{
				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(clConst.esmeril_cte);
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_ESMER_MAN,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_ESMER_MAN.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);

			}
		}
	}

	/**
	 * Termina Esmeril e executa Rebarbeamento por Máquina
	 *
	 * @param ev Evento gerador
	 */
	private void term_Esmeril_Exec_RebarbMaq(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorFinal == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgRebarbMaq.contains(ev.getPecaID()))
				filaAgRebarbMaq.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgRebarbMaq de tamanho: " + filaAgRebarbMaq.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_Acab_Exec_Esmeril(ev);
		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgRebarbMaq.contains(ev.getPecaID()))
				filaAgRebarbMaq.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgRebarbMaq de tamanho: " + filaAgRebarbMaq.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgRebarbMaq.isEmpty() && funcSetorFinal != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgRebarbMaq.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgRebarbMaq++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgRebarbMaq += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorFinal--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor da finalização alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorFinal);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.rebarb_maq_min, clUnifParams.rebarb_maq_max));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_REB_MAQU,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_REB_MAQU.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_Esmeril_Exec_RebarbMaq(clEventoBase ev)
	{

		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgRebarbMaq.isEmpty() && funcSetorFinal != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgRebarbMaq.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgRebarbMaq++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgRebarbMaq += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorFinal--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor da finalização alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorFinal);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.rebarb_maq_min, clUnifParams.rebarb_maq_max));
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_REB_MAQU,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_REB_MAQU.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);
		}

	}

	/**
	 * Termina Esmeril e executa Rebarbeamento Manual
	 *
	 * @param ev Evento gerador
	 */
	private void term_Esmeril_Exec_RebarbMan(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorFinal == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgRebarbMan.contains(ev.getPecaID()))
				filaAgRebarbMan.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgRebarbMan de tamanho: " + filaAgRebarbMan.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_Acab_Exec_Esmeril(ev);
		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgRebarbMan.contains(ev.getPecaID()))
				filaAgRebarbMan.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgRebarbMan de tamanho: " + filaAgRebarbMan.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgRebarbMan.isEmpty() && funcSetorFinal != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgRebarbMan.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgRebarbMan++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgRebarbMan += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorFinal--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor da finalização alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorFinal);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.rebarb_man_min, clTriParams.rebarb_man_med, clTriParams.rebarb_man_max));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_REB_MAN,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_REB_MAN.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_Esmeril_Exec_RebarbMan(clEventoBase ev)
	{

		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgRebarbMan.isEmpty() && funcSetorFinal != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgRebarbMan.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgRebarbMan++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgRebarbMan += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorFinal--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor da finalização alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorFinal);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.rebarb_man_min, clTriParams.rebarb_man_med, clTriParams.rebarb_man_max));
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_REB_MAN,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_REB_MAN.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);
		}

	}

	/**
	 * Termina Rebarbeamento e executa Análise Visual
	 *
	 * @param ev Evento gerador
	 */
	private void term_Rebarb_Exec_AnalisVisual(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorQualidade == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgAnalisVisual.contains(ev.getPecaID()))
				filaAgAnalisVisual.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgAnalisVisual de tamanho: " + filaAgAnalisVisual.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			switch (ev.gettEvento())
			{
				case TERM_REB_MAN:
					// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
					tenta_term_Esmeril_Exec_RebarbMan(ev);
					break;
				case TERM_REB_MAQU:
					// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
					tenta_term_Esmeril_Exec_RebarbMaq(ev);
					break;
			}

		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgAnalisVisual.contains(ev.getPecaID()))
				filaAgAnalisVisual.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgAnalisVisual de tamanho: " + filaAgAnalisVisual.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgAnalisVisual.isEmpty() && funcSetorQualidade != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgAnalisVisual.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgAnalisVisual++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgAnalisVisual += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorQualidade--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor da qualidade alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorQualidade);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(clConst.ana_vis_cte);
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_ANA_VIS,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_ANA_VIS.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_Rebarb_Exec_AnalisVisual(clEventoBase ev)
	{

		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgAnalisVisual.isEmpty() && funcSetorQualidade != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgAnalisVisual.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgAnalisVisual++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgAnalisVisual += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorQualidade--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor da qualidade alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorQualidade);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(clConst.ana_vis_cte);
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_ANA_VIS,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_ANA_VIS.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);
		}
	}

	/**
	 * Termina Análise Visual e executa a Análise por Serviços de Terceiros
	 *
	 * @param ev Evento gerador
	 */
	private void term_AnalisVisual_Exec_ServTer(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (servTerc == 0)
		{

			if (!filaAgTerceir.contains(ev.getPecaID()))
				filaAgTerceir.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgTerceir de tamanho: " + filaAgTerceir.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_Rebarb_Exec_AnalisVisual(ev);
		}
		else
		{
			double prob;

			if (!filaAgTerceir.contains(ev.getPecaID()))
				filaAgTerceir.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgTerceir de tamanho: " + filaAgTerceir.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgTerceir.isEmpty() && servTerc != 0; i++)
			{
				prob = rand.nextDouble();
				// pega a primeira atividade que está na fila
				atualID = filaAgTerceir.removeFirst();
				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgTerceir++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgTerceir += tmpTFila;

				// aloca o recurso para a respectiva atividade
				servTerc--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Serviços de terceiros alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + servTerc);
				comecaCronotragem();

				if (prob < 0.9)
				{
					// calcula o tempo de término da atividade
					tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.serv_ter_min, clTriParams.serv_ter_med, clTriParams.serv_ter_max));
					// cria o novo evento
					clEventoBase novoEvento = new clEventoBase(ev,
							tmpTAtividade,
							enumTipoEvento.TERM_SERV_TER_PIN,
							atualID);

					// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
					paraCronometragem();
					dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_SERV_TER_PIN.name() +
							" pedido de ID: " + atualID +
							" termina em: " + tmpTAtividade);
					comecaCronotragem();

					// adiciona o novo evento à fel
					fel.add(novoEvento);

				}
				else
				{

					// calcula o tempo de término da atividade
					tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.serv_ter_min, clTriParams.serv_ter_med, clTriParams.serv_ter_max));
					// cria o novo evento
					clEventoBase novoEvento = new clEventoBase(ev,
							tmpTAtividade,
							enumTipoEvento.TERM_SERV_TER,
							atualID);

					// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
					paraCronometragem();
					dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_SERV_TER.name() +
							" pedido de ID: " + atualID +
							" termina em: " + tmpTAtividade);
					comecaCronotragem();

					// adiciona o novo evento à fel
					fel.add(novoEvento);

				}
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_AnalisVisual_Exec_ServTer(clEventoBase ev)
	{
		double prob;

		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgTerceir.isEmpty() && servTerc != 0; i++)
		{
			prob = rand.nextDouble();
			// pega a primeira atividade que está na fila
			atualID = filaAgTerceir.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgTerceir++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgTerceir += tmpTFila;

			// aloca o recurso para a respectiva atividade
			servTerc--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Serviços de terceiros alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + servTerc);
			comecaCronotragem();

			if (prob < 0.9)
			{
				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.serv_ter_min, clTriParams.serv_ter_med, clTriParams.serv_ter_max));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_SERV_TER_PIN,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_SERV_TER_PIN.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);

			}
			else
			{

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.serv_ter_min, clTriParams.serv_ter_med, clTriParams.serv_ter_max));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_SERV_TER,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_SERV_TER.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);

			}
		}
	}

	/**
	 * Termina Análise por Serviços de Terceiros e executa Pintura
	 *
	 * @param ev Evento gerador
	 */
	private void term_ServTer_Exec_Pint(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorContProd == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgPint.contains(ev.getPecaID()))
				filaAgPint.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgPint de tamanho: " + filaAgPint.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);
			// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
			tenta_term_AnalisVisual_Exec_ServTer(ev);
		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgPint.contains(ev.getPecaID()))
				filaAgPint.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgPint de tamanho: " + filaAgPint.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgPint.isEmpty() && funcSetorContProd != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgPint.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgPint++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgPint += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorContProd--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de continuação de produção alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorContProd);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new NormalDistribution(rand, clNormParams.receb_pint_media, clNormParams.receb_pint_sd));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_PINT,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_PINT.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);
			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_ServTer_Exec_Pint(clEventoBase ev)
	{

		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgPint.isEmpty() && funcSetorContProd != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgPint.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgPint++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgPint += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorContProd--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de continuação de produção alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorContProd);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(new NormalDistribution(rand, clNormParams.receb_pint_media, clNormParams.receb_pint_sd));
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_PINT,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_PINT.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);
		}
	}

	/**
	 * Termina Análise por Serviços de Terceiros ou Pintura e executa Geração da Documentação
	 *
	 * @param ev Evento gerador
	 */
	private void term_Pint_ou_ServTer_Exec_GerDoc(clEventoBase ev)
	{
		// incrementa o clock de acordo com o término do evento reconhecido
		clock += ev.getTempoTermino() - clock;
		// Se o recurso não está disponível
		if (funcSetorDoc == 0)
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgDoc.contains(ev.getPecaID()))
				filaAgDoc.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgDoc de tamanho: " + filaAgDoc.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			switch (ev.gettEvento())
			{
				case TERM_SERV_TER:
					// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
					tenta_term_AnalisVisual_Exec_ServTer(ev);
					break;
				case TERM_SERV_TER_PIN:
					// se não tem recurso para a atividade atual, tenta executar mais entidades na atividade anterior
					tenta_term_ServTer_Exec_Pint(ev);
					break;
			}
		}
		else
		{
			// adiciona a atividade a respectiva fila
			if (!filaAgDoc.contains(ev.getPecaID()))
				filaAgDoc.addLast(ev.getPecaID());

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.FILA, "pedido de ID: " + ev.getPecaID() +
					" chegou na filaAgDoc de tamanho: " + filaAgDoc.size());
			comecaCronotragem();

			// marca o tempo que a respectiva atividade entrou na fila
			pedidosCriados.get(ev.getPecaID()).setMarcaTempoEntrada(clock);

			// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
			for (int i = 0; i < 2 && !filaAgDoc.isEmpty() && funcSetorDoc != 0; i++)
			{
				// pega a primeira atividade que está na fila
				atualID = filaAgDoc.removeFirst();

				// calcula o tempo de espera da respectiva atividade na respectiva fila
				tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

				// só considera que a atividade passou na fila se tiver tempo de espera > 0
				if (tmpTFila != 0)
					getEst().totPedidoEmfilaAgDoc++;

				// calcula o acumulado de tempo de espera da respectiva fila
				getEst().totTempoEspfilaAgDoc += tmpTFila;

				// aloca o recurso para a respectiva atividade
				funcSetorDoc--;

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de documentação alocado para pedido de ID: " + atualID +
						"total atual do recurso: " + funcSetorDoc);
				comecaCronotragem();

				// calcula o tempo de término da atividade
				tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.ger_doc_min, clUnifParams.ger_doc_max));
				// cria o novo evento
				clEventoBase novoEvento = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_GER_DOC,
						atualID);

				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_GER_DOC.name() +
						" pedido de ID: " + atualID +
						" termina em: " + tmpTAtividade);
				comecaCronotragem();

				// adiciona o novo evento à fel
				fel.add(novoEvento);

			}
		}

	}

	/**
	 * Tenta remover atividades da fila e gerar novos eventos, quando os recursos da próxima atividade não estão disponíveis
	 *
	 * @param ev evento gerador
	 */
	private void tenta_term_Pint_ou_ServTer_Exec_GerDoc(clEventoBase ev)
	{

		// se a fila não está vazia e tem recursos disponíveis, tenta puxar duas entidades da fila
		for (int i = 0; i < 2 && !filaAgDoc.isEmpty() && funcSetorDoc != 0; i++)
		{
			// pega a primeira atividade que está na fila
			atualID = filaAgDoc.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			// só considera que a atividade passou na fila se tiver tempo de espera > 0
			if (tmpTFila != 0)
				getEst().totPedidoEmfilaAgDoc++;

			// calcula o acumulado de tempo de espera da respectiva fila
			getEst().totTempoEspfilaAgDoc += tmpTFila;

			// aloca o recurso para a respectiva atividade
			funcSetorDoc--;

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.ENTIDADE, "Funcionário do setor de documentação alocado para pedido de ID: " + atualID +
					"total atual do recurso: " + funcSetorDoc);
			comecaCronotragem();

			// calcula o tempo de término da atividade
			tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.ger_doc_min, clUnifParams.ger_doc_max));
			// cria o novo evento
			clEventoBase novoEvento = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_GER_DOC,
					atualID);

			// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
			paraCronometragem();
			dbg(enumTipoDebug.EVENTO, "evento: " + enumTipoEvento.TERM_GER_DOC.name() +
					" pedido de ID: " + atualID +
					" termina em: " + tmpTAtividade);
			comecaCronotragem();

			// adiciona o novo evento à fel
			fel.add(novoEvento);

		}

	}

	/**
	 * Termina a geração de documentação e finaliza
	 *
	 * @param ev evento gerador
	 */
	private void term_gerDoc_Exec_Final(clEventoBase ev)
	{
		est.pedidosAtendidos++;
	}

	/**
	 * Verifica se todas as filas estão vazias
	 *
	 * @return true se todas as filas estão vazias
	 */
	private boolean filasVazias()
	{
		return filaAgAcabamento.isEmpty() &&
				filaAgAnalis.isEmpty() &&
				filaAgAnalisVisual.isEmpty() &&
				filaAgCaixote.isEmpty() &&
				filaAgCheckoutLimpAcab.isEmpty() &&
				filaAgColeta.isEmpty() &&
				filaAgDoc.isEmpty() &&
				filaAgEsmeril.isEmpty() &&
				filaAgIdentif.isEmpty() &&
				filaAgLimpEsp.isEmpty() &&
				filaAgManual.isEmpty() &&
				filaAgMaquina.isEmpty() &&
				filaAgMoldPronto.isEmpty() &&
				filaAgPint.isEmpty() &&
				filaAgPlanejamento.isEmpty() &&
				filaAgPreeAreia.isEmpty() &&
				filaAgProc.isEmpty() &&
				filaAgRastr.isEmpty() &&
				filaAgRebarbMan.isEmpty() &&
				filaAgRebarbMaq.isEmpty() &&
				filaAgSCaixote.isEmpty() &&
				filaAgTerceir.isEmpty() &&
				filaAgVerifDataMater.isEmpty() &&
				filaAgVerificacao.isEmpty();
	}

	/**
	 * Depois do término da simulação, calcula o tempo em fila para cada atividade restante em alguma fila.
	 *
	 * @param filaNome Nome da fila
	 * @param fila     fila
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	private void calculaFila(String filaNome, LinkedList<Integer> fila) throws NoSuchFieldException, IllegalAccessException
	{
		// pega o padrão dos campos de tempo em fila: totTempoEsp+<nome-fila>
		String tempo = "totTempoEsp" + filaNome;
		Field campoTotTempo = null;

		// utiliza dos recursos de Java Reflection para pegar o campo na classe clEstatisticas com o respectivo
		// nome totTempoEsp+<nome-fila>
		campoTotTempo = clEstatisticas.class.getDeclaredField(tempo);

		// quantidade de pedidos que passaram pela respectiva fila
		String pedidosEmFila = "totPedidoEm" + filaNome;
		Field campoPedidosEmFila;

		campoPedidosEmFila = clEstatisticas.class.getDeclaredField(pedidosEmFila);

		// média de espera dos pedidos na respectiva fila
		String mediaEsperaFila = "mediaEspera" + filaNome;
		Field campoMediaEspera;

		// campo do cálculo da média dessa fila
		campoMediaEspera = clEstatisticas.class.getDeclaredField(mediaEsperaFila);

		// TODO consertar isso
		if (fila.size() > clSimulParams.MAX_PEDIDO || (Integer) campoPedidosEmFila.get(getEst()) > clSimulParams.MAX_PEDIDO)
		{
			System.out.println("fila errada:" + filaNome);
		}

		// enquanto a respectiva fila não estiver vazia
		while (!fila.isEmpty())
		{
			// pega o primeiro elemento da fila
			atualID = fila.removeFirst();

			// calcula o tempo de espera da respectiva atividade na respectiva fila
			tmpTFila = clock - pedidosCriados.get(atualID).getMarcaTempoEntrada();

			if (tmpTFila != 0)
				campoPedidosEmFila.setInt(getEst(), (Integer) campoPedidosEmFila.get(getEst()) + 1);

			// define o valor do campo encontrado
			// getEst() -> em qual classe o campo deve ser definido
			// campo.get(getEst()) + tmpTFila -> retorna o valor do campo e incrementa com o novo valor calculado
			campoTotTempo.setInt(getEst(), (Integer) campoTotTempo.get(getEst()) + tmpTFila);
		}

		//se teve pedidos em fila
		if ((Integer) campoPedidosEmFila.get(getEst()) != 0)
		{
			// calcula a média e atribui ao campo
			campoMediaEspera.setDouble(getEst(),
					(Integer) campoTotTempo.get(getEst()) / (Integer) campoPedidosEmFila.get(getEst()));
		}
		else
		{
			// se não teve então a média de espera é 0
			campoMediaEspera.setDouble(getEst(), 0.0);
		}

	}

	/**
	 * Se alguma das filas ainda não está vazia no final da simulação, calcula o tempo de espera em cada fila.
	 *
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	private void calculaTempoFilasFinal() throws NoSuchFieldException, IllegalAccessException
	{
		// se alguma das filas não está vazia
		if (!filasVazias())
		{
			paraCronometragem();
			dbg(enumTipoDebug.INFO, "Alguma(s) das filas não está vazia no final da simulação, calculando tempo de " +
					"espera...");
			comecaCronotragem();

			// calcula os tempos de espera
			calculaFila("filaAgAcabamento", filaAgAcabamento);
			calculaFila("filaAgAnalis", filaAgAnalis);
			calculaFila("filaAgAnalisVisual", filaAgAnalisVisual);
			calculaFila("filaAgCaixote", filaAgCaixote);
			calculaFila("filaAgCheckoutLimpAcab", filaAgCheckoutLimpAcab);
			calculaFila("filaAgColeta", filaAgColeta);
			calculaFila("filaAgDoc", filaAgDoc);
			calculaFila("filaAgEsmeril", filaAgEsmeril);
			calculaFila("filaAgIdentif", filaAgIdentif);
			calculaFila("filaAgLimpEsp", filaAgLimpEsp);
			calculaFila("filaAgManual", filaAgManual);
			calculaFila("filaAgMaquina", filaAgMaquina);
			calculaFila("filaAgMoldPronto", filaAgMoldPronto);
			calculaFila("filaAgPint", filaAgPint);
			calculaFila("filaAgPlanejamento", filaAgPlanejamento);
			calculaFila("filaAgPreeAreia", filaAgPreeAreia);
			calculaFila("filaAgProc", filaAgProc);
			calculaFila("filaAgRastr", filaAgRastr);
			calculaFila("filaAgRebarbMan", filaAgRebarbMan);
			calculaFila("filaAgRebarbMaq", filaAgRebarbMaq);
			calculaFila("filaAgSCaixote", filaAgSCaixote);
			calculaFila("filaAgTerceir", filaAgTerceir);
			calculaFila("filaAgVerifDataMater", filaAgVerifDataMater);
			calculaFila("filaAgVerificacao", filaAgVerificacao);
		}
	}

	/**
	 * Se a simulação terminar por fel vazia e ainda tiver clock que pode ser incrementado, tenta encontrar alguma fila
	 * não vazia para gerar eventos com as atividades encontradas nessas filas.
	 *
	 * @param ev Evento gerador
	 */
	private void tentaLimparFilas(clEventoBase ev)
	{
		// se alguma(s) das filas está vazia
		if (!filasVazias())
		{
			if (!filaAgAcabamento.isEmpty())
			{
				tenta_term_CheckoutLimpEsp_Exec_Acab(ev);
			}
			if (!filaAgAnalis.isEmpty())
			{
				tenta_term_ProcExp_Exec_QuimicVerifMatEx(ev);
			}
			if (!filaAgAnalisVisual.isEmpty())
			{
				tenta_term_Rebarb_Exec_AnalisVisual(ev);
			}
			if (!filaAgCaixote.isEmpty())
			{
				tenta_term_IdMaterial_Exec_ProdCxt(ev);
			}
			if (!filaAgCheckoutLimpAcab.isEmpty())
			{
				tenta_term_Prod_Exec_Checkout(ev);
			}
			if (!filaAgColeta.isEmpty())
			{
				tenta_term_Rastr_Exec_InfoPeca(ev);
			}
			if (!filaAgDoc.isEmpty())
			{
				tenta_term_Pint_ou_ServTer_Exec_GerDoc(ev);
			}
			if (!filaAgEsmeril.isEmpty())
			{
				tenta_term_Acab_Exec_Esmeril(ev);
			}
			if (!filaAgIdentif.isEmpty())
			{
				tenta_term_Pree_Areia_Exec_IdMaterial(ev);
			}
			if (!filaAgLimpEsp.isEmpty())
			{
				tenta_term_Checkout_Exec_LimpEsp(ev);
			}
			if (!filaAgManual.isEmpty())
			{
				tenta_term_IdMaterial_Exec_ProdMan(ev);
			}
			if (!filaAgMaquina.isEmpty())
			{
				tenta_term_IdMaterial_Exec_ProdMaq(ev);
			}
			if (!filaAgMoldPronto.isEmpty())
			{
				tenta_term_VerifArqModDepot_Exec_CriaMod(ev);
			}
			if (!filaAgPint.isEmpty())
			{
				tenta_term_ServTer_Exec_Pint(ev);
			}
			if (!filaAgPlanejamento.isEmpty())
			{
				tenta_term_VerifDatMatPrim_Exec_PlanProcProd(ev);
			}
			if (!filaAgPreeAreia.isEmpty())
			{
				tenta_term_InfoPeca_Exec_PreeAreia(ev);
			}
			if (!filaAgProc.isEmpty())
			{
				tenta_term_ChegPed_Exec_ProcExp(ev);
			}
			if (!filaAgRastr.isEmpty())
			{
				tenta_term_CriaMod_ou_VerifArqModDepot_Exec_Rastr(ev);
			}
			if (!filaAgRebarbMan.isEmpty())
			{
				tenta_term_Esmeril_Exec_RebarbMan(ev);
			}
			if (!filaAgRebarbMaq.isEmpty())
			{
				tenta_term_Esmeril_Exec_RebarbMaq(ev);
			}
			if (!filaAgSCaixote.isEmpty())
			{
				tenta_term_IdMaterial_Exec_ProdSCxt(ev);
			}
			if (!filaAgTerceir.isEmpty())
			{
				tenta_term_AnalisVisual_Exec_ServTer(ev);
			}
			if (!filaAgVerifDataMater.isEmpty())
			{
				tenta_term_QuimicVerifMatEx_Exec_VerifDatMatPrim(ev);
			}
			if (!filaAgVerificacao.isEmpty())
			{
				tenta_term_PlanProcProd_Exec_VerifArqModDepot(ev);
			}
		}
	}

	/**
	 * Método principal da simulação.
	 *
	 * @param seed Semente do gerador de números aleatórios
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public void simul(long seed) throws NoSuchFieldException, IllegalAccessException
	{
		// evento auxiliar
		clEventoBase ev;

		// inicialização da simulação
		init(seed);

		// começa a contar o tempo
		comecaCronotragem();

		// gera as entidades
		nascedouro();

		// enquanto não atinge o clock e fel vazia
		while (clock <= clSimulParams.MAX_TEMPO && !fel.isEmpty())
		{
			// tira o primeiro evento da fel
			ev = fel.poll();

			// vê o tipo de evento
			switch (ev.gettEvento())
			{
				case CHEGADA_PEDIDO:
					term_ChegPed_Exec_ProcExp(ev);
					break;
				case TERM_PROC:
					// libera o recurso
					funcSetorCompra++;
					term_ProcExp_Exec_QuimicVerifMatEx(ev);
					break;

				case TERM_QUIMIC_VERIFI_MAT_EX:
					// libera o recurso
					funcSetorExpedicao++;
					term_QuimicVerifMatEx_Exec_VerifDatMatPrim(ev);
					break;
				case TERM_VERIF_DATAS_MAT_PRIM:
					// libera o recurso
					funcSetorExpedicao++;
					term_VerifDatMatPrim_Exec_PlanProcProd(ev);
					break;

				case TERM_PLAN_PROC_PROD:
					// libera o recurso
					funcSetorExpedicao++;
					term_PlanProcProd_Exec_VerifArqModDepot(ev);
					break;

				case TERM_VERIF_ARQ_MOD_DEPOT_CRIA:
					// libera o recurso
					funcSetorModel++;
					term_VerifArqModDepot_Exec_CriaMod(ev);
					break;

				case TERM_VERIF_ARQ_MOD_DEPOT_RASTR:
					// libera o recurso
					funcSetorModel++;
					term_CriaMod_ou_VerifArqModDepot_Exec_Rastr(ev);
					break;

				case TERM_CRIA_MOD:
					// libera o recurso
					funcSetorModel++;
					term_CriaMod_ou_VerifArqModDepot_Exec_Rastr(ev);
					break;

				case TERM_RASTR:
					// libera o recurso
					funcSetorModel++;
					term_Rastr_Exec_InfoPeca(ev);
					break;

				case TERM_INFO_PECA:
					// libera o recurso
					funcSetorModel++;
					term_InfoPeca_Exec_PreeAreia(ev);
					break;

				case TERM_PREE_AREIA:
					// libera o recurso
					funcSetorProducao++;
					term_Pree_Areia_Exec_IdMaterial(ev);
					break;

				case TERM_ID_MATERIAL_CXT:
					// libera o recurso
					funcSetorProducao++;
					term_IdMaterial_Exec_ProdCxt(ev);
					break;
				case TERM_ID_MATERIAL_S_CXT:
					// libera o recurso
					funcSetorProducao++;
					term_IdMaterial_Exec_ProdSCxt(ev);
					break;
				case TERM_ID_MATERIAL_MAQ:
					// libera o recurso
					funcSetorProducao++;
					term_IdMaterial_Exec_ProdMaq(ev);
					break;
				case TERM_ID_MATERIAL_MAN:
					// libera o recurso
					funcSetorProducao++;
					term_IdMaterial_Exec_ProdMan(ev);
					break;

				case TERM_PROD_MAN:
					// libera o recurso
					funcProdManual++;
					term_Prod_Exec_Checkout(ev);
					break;

				case TERM_PROD_RESIN:
				case TERM_PROD_MAQ:
				case TERM_PROD_CXT:
				case TERM_PROD_S_CXT:
					// libera o recurso
					funcSetorProducao++;
					term_Prod_Exec_Checkout(ev);
					break;

				case TERM_CHECKOUT_LIMP_ACAB_RESIN:
					// libera o recurso
					funcSetorFinal++;
					term_Checkout_Exec_LimpEsp(ev);
					break;

				case TERM_CHECKOUT_LIMP_ACAB:
					// libera o recurso
					funcSetorFinal++;
					term_CheckoutLimpEsp_Exec_Acab(ev);
					break;

				case TERM_LIMP_RESIN:
					// libera o recurso
					funcCheckoutResina++;
					term_CheckoutLimpEsp_Exec_Acab(ev);
					break;

				case TERM_ACAB:
					// libera o recurso
					funcSetorFinal++;
					term_Acab_Exec_Esmeril(ev);
					break;

				case TERM_ESMER_MAQ:
					// libera o recurso
					funcSetorFinal++;
					term_Esmeril_Exec_RebarbMaq(ev);
					break;

				case TERM_ESMER_MAN:
					// libera o recurso
					funcSetorFinal++;
					term_Esmeril_Exec_RebarbMan(ev);
					break;

				case TERM_REB_MAQU:
				case TERM_REB_MAN:
					// libera o recurso
					funcSetorFinal++;
					term_Rebarb_Exec_AnalisVisual(ev);
					break;

				case TERM_ANA_VIS:
					// libera o recurso
					funcSetorQualidade++;
					term_AnalisVisual_Exec_ServTer(ev);
					break;

				case TERM_SERV_TER:
					servTerc++;
					term_Pint_ou_ServTer_Exec_GerDoc(ev);
					break;

				case TERM_SERV_TER_PIN:
					term_ServTer_Exec_Pint(ev);
					break;

				case TERM_PINT:
					// libera o recurso
					funcSetorContProd++;
					term_Pint_ou_ServTer_Exec_GerDoc(ev);
					break;

				case TERM_GER_DOC:
					// libera o recurso
					funcSetorDoc++;
					term_gerDoc_Exec_Final(ev);
					break;
				default:
					fel.add(ev);
					break;
			}

			// se atingir o clock máximo
			if (clock > clSimulParams.MAX_TEMPO)
			{
				// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
				paraCronometragem();
				dbg(enumTipoDebug.INFO, "Simulação terminou por clock: " + clock);
				comecaCronotragem();
			}
			// se a fel ficar vazia
			else if (fel.isEmpty())
				// se sobrar alguem em fila, tenta executá-lo
				tentaLimparFilas(ev);

		}

		// calcula o tempo das atividades restantes nas filas
		calculaTempoFilasFinal();

		// para a contagem de tempo, para não contar as mensagens de debug no tempo de simulação
		paraCronometragem();

		// imprime as estatísticas
		if (clDebug.dbgInfo)
			getEst().imprimeEstatisticas();
	}

	public clEstatisticas getEst()
	{
		return est;
	}

	public void setEst(clEstatisticas est)
	{
		this.est = est;
	}
}
