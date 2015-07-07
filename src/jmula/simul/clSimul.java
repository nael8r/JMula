package jmula.simul;

import static jmula.simul.entity.clEntidades.*;

import jmula.simul.entity.clEntidadePedido;
import jmula.simul.event.clEventoBase;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import jmula.simul.event.enumTipoEvento;
import jmula.simul.parameters.clConst;
import jmula.simul.parameters.clNormParams;
import jmula.simul.parameters.clTriParams;
import jmula.simul.parameters.clUnifParams;
import org.apache.commons.math3.distribution.*;
import org.apache.commons.math3.random.MersenneTwister;

/**
 * Created by nael on 02/07/15.
 */
public class clSimul
{
	// TODO instanciar as distribuições fora do laço
	private int tmpTAtividade;
	private int tmpTFila;
	private int atualID;
	private int clock;
	// ID das entidades de pedidos
	private Integer ID;

	private MersenneTwister rand;

	private HashMap<Integer, clEntidadePedido> atividadesCriadas;

	private Queue<clEventoBase> fel;
	// fila de aguardo do processo e expedição
	private LinkedList<Integer> filaAgProc;
	private int totTempoEspfilaAgProc;
	private LinkedList<Integer> filaAgAnalis;
	private int totTempoEspfilaAgAnalis;
	private LinkedList<Integer> filaAgVerifDataMater;
	private int totTempoEspfilaAgVerifDataMater;
	private LinkedList<Integer> filaAgPlanejamento;
	private int totTempoEspfilaAgPlanejamento;
	private LinkedList<Integer> filaAgVerificacao;
	private int totTempoEspfilaAgVerificacao;
	private LinkedList<Integer> filaAgMoldPronto;
	private int totTempoEspfilaAgMoldPronto;
	private LinkedList<Integer> filaAgRastr;
	private int totTempoEspfilaAgRastr;
	private LinkedList<Integer> filaAgColeta;
	private int totTempoEspfilaAgColeta;
	private LinkedList<Integer> filaAgPreeAreia;
	private int totTempoEspfilaAgPreeAreia;
	private LinkedList<Integer> filaAgIdentif;
	private int totTempoEspfilaAgIdentif;
	private LinkedList<Integer> filaAgMaquina;
	private int totTempoEspfilaAgMaquina;
	private LinkedList<Integer> filaAgManual;
	private int totTempoEspfilaAgManual;
	private LinkedList<Integer> filaAgCaixote;
	private int totTempoEspfilaAgCaixote;
	private LinkedList<Integer> filaAgSCaixote;
	private int totTempoEspfilaAgSCaixote;
	private LinkedList<Integer> filaAgCheckoutLimpAcab;
	private int totTempoEspfilaAgCheckoutLimpAcab;
	private LinkedList<Integer> filaAgLimpEsp;
	private int totTempoEspfilaAgLimpEsp;
	private LinkedList<Integer> filaAgAcabamento;
	private int totTempoEspfilaAgAcabamento;
	private LinkedList<Integer> filaAgEsmeril;
	private int totTempoEspfilaAgEsmeril;
	private LinkedList<Integer> filaAgRebarbMaq;
	private int totTempoEspfilaAgRebarb;
	private LinkedList<Integer> filaAgRebarbMan;
	private int totTempoEspfilaAgRebarbMan;
	private LinkedList<Integer> filaAgAnalisVisual;
	private int totTempoEspfilaAgAnalisVisual;
	private LinkedList<Integer> filaAgTerceir;
	private int totTempoEspfilaAgTerceir;
	private LinkedList<Integer> filaAgPint;
	private int totTempoEspfilaAgPint;
	private LinkedList<Integer> filaAgDoc;
	private int totTempoEspfilaAgDoc;
	private LinkedList<Integer> limbo;

	// Métodos principais da simulação
	public void init(long seed)
	{
		tmpTAtividade = 0;
		tmpTFila = 0;
		ID = 0;
		clock = 0;

		atividadesCriadas = new HashMap<>();
		rand = new MersenneTwister(seed);
		fel = new PriorityQueue<>();
		// filas
		filaAgProc = new LinkedList<>();
		filaAgAnalis = new LinkedList<>();
		filaAgVerifDataMater = new LinkedList<>();
		filaAgPlanejamento = new LinkedList<>();
		filaAgVerificacao = new LinkedList<>();
		filaAgMoldPronto = new LinkedList<>();
		filaAgRastr = new LinkedList<>();
		filaAgColeta = new LinkedList<>();
		filaAgPreeAreia = new LinkedList<>();
		filaAgIdentif = new LinkedList<>();
		filaAgMaquina = new LinkedList<>();
		filaAgManual = new LinkedList<>();
		filaAgCaixote = new LinkedList<>();
		filaAgSCaixote = new LinkedList<>();
		filaAgCheckoutLimpAcab = new LinkedList<>();
		filaAgLimpEsp = new LinkedList<>();
		filaAgAcabamento = new LinkedList<>();
		filaAgEsmeril = new LinkedList<>();
		filaAgRebarbMaq = new LinkedList<>();
		filaAgRebarbMan = new LinkedList<>();
		filaAgAnalisVisual = new LinkedList<>();
		filaAgTerceir = new LinkedList<>();
		filaAgPint = new LinkedList<>();
		filaAgDoc = new LinkedList<>();
		limbo = new LinkedList<>();
	}

	// Métodos de geração
	private void nascedouro(int qtdPecas)
	{
		int time;

		// Só pra não criar objetos dentro do laço
		NormalDistribution norm = new NormalDistribution(rand, clNormParams.cheg_ped_media, clNormParams.cheg_ped_sd);

		atividadesCriadas.putIfAbsent(ID, new clEntidadePedido(ID));

		clEventoBase newEv = new clEventoBase(this, clock,
				enumTipoEvento.CHEGADA_PEDIDO,
				ID);

		fel.add(newEv);

		filaAgProc.addLast(ID);

		atividadesCriadas.get(ID).setMarcaTempoEntrada(clock);

		ID++;

		for (int i = 1; i < qtdPecas; i++)
		{
			atividadesCriadas.putIfAbsent(ID, new clEntidadePedido(ID));

			time = tFinalEvento(norm);

			newEv = new clEventoBase(this, time,
					enumTipoEvento.CHEGADA_PEDIDO,
					ID);

			fel.add(newEv);

			filaAgProc.addLast(ID);

			atividadesCriadas.get(ID).setMarcaTempoEntrada(time);

			ID++;
		}

	}

	private int tFinalEvento(AbstractRealDistribution dist)
	{
		return (int) Math.ceil(clock + dist.sample());
	}

	private int tFinalEvento(AbstractIntegerDistribution dist)
	{
		return (int) Math.ceil(clock + dist.sample());
	}

	private int tFinalEvento(int cons)
	{
		return (int) Math.ceil(clock + cons);
	}

	private void term_ChegPedExec_ProcExp(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgProc.isEmpty() && assistSetorCompra != 0; i++)
		{

			atualID = filaAgProc.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgProc += tmpTFila;

			assistSetorCompra--;

			tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.proc_exp_min, clTriParams.proc_exp_med, clTriParams.proc_exp_max));
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_PROC,
					atualID);

			fel.add(newEv);

			filaAgAnalis.addLast(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);
		}
	}

	private void term_ProcExpExec_QuimicVerifMatEx(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgAnalis.isEmpty() && funcSetorExpedicao != 0; i++)
		{

			atualID = filaAgAnalis.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgAnalis += tmpTFila;

			funcSetorExpedicao--;

			tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.quimic_mater_min, clTriParams.quimic_mater_med, clTriParams.quimic_mater_max));
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_QUIMIC_VERIFI_MAT_EX,
					atualID);

			fel.add(newEv);


			filaAgVerifDataMater.addLast(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);
		}

	}

	// Verificar datas, matéria primas
	private void term_QuimicVerifMatExExec_VerifDatMatPrim(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgVerifDataMater.isEmpty() && funcSetorExpedicao != 0; i++)
		{
			atualID = filaAgVerifDataMater.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgVerifDataMater += tmpTFila;

			funcSetorExpedicao--;

			tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.verif_data_mater_unif_min, clUnifParams.verif_data_mater_unif_max));
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_VERIF_DATAS_MAT_PRIM,
					atualID);

			fel.add(newEv);


			filaAgPlanejamento.addLast(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);
		}
	}

	// estudo, planejamento dos processos de produção
	private void term_VerifDatMatPrimExec_PlanProcProd(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgPlanejamento.isEmpty() && funcSetorExpedicao != 0; i++)
		{

			atualID = filaAgPlanejamento.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgPlanejamento += tmpTFila;

			funcSetorExpedicao--;

			tmpTAtividade = tFinalEvento(new NormalDistribution(rand, clNormParams.plan_proc_prod_media, clNormParams.plan_proc_prod_sd));
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_PLAN_PROC_PROD,
					atualID);

			fel.add(newEv);


			filaAgVerificacao.add(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);
		}
	}

	// término da verificação de arquivos de modelagem no depósito
	private void term_PlanProcProdExec_VerifArqModDepot(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgVerificacao.isEmpty() && funcSetorModel != 0; i++)
		{
			double prob = rand.nextDouble();
			atualID = filaAgVerificacao.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgVerificacao += tmpTFila;

			funcSetorModel--;


			if (prob <= 0.3)
			{

				tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.verif_arq_depo_unif_min, clUnifParams.verif_arq_depo_unif_max));
				clEventoBase newEv = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_VERIF_ARQ_MOD_DEPOT_CRIA,
						atualID);

				fel.add(newEv);

				filaAgMoldPronto.addLast(atualID);
				atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() + tmpTAtividade);

			}
			else
			{

				tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.verif_arq_depo_unif_min, clUnifParams.verif_arq_depo_unif_max));
				clEventoBase newEv = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_VERIF_ARQ_MOD_DEPOT_RASTR,
						atualID);

				fel.add(newEv);

				filaAgRastr.addLast(atualID);
				atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
						tmpTAtividade +
						tmpTFila);

			}
		}
	}

	// término da criação do modelo
	private void term_VerifArqModDepot_Exec_CriaMod(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgMoldPronto.isEmpty() && funcSetorModel != 0; i++)
		{


			atualID = filaAgMoldPronto.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgMoldPronto += tmpTFila;

			funcSetorModel--;

			tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.cri_model_min, clTriParams.cri_model_med, clTriParams.cri_model_max));
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_CRIA_MOD,
					atualID);

			fel.add(newEv);


			filaAgRastr.addLast(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);
		}
	}

	// ratreabilidade, histórico do material
	private void term_CriaMod_Exec_Rastr(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgRastr.isEmpty() && funcSetorModel != 0; i++)
		{


			atualID = filaAgRastr.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgRastr += tmpTFila;

			funcSetorModel--;

			tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.rastr_min, clTriParams.rastr_med, clTriParams.rastr_max));
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_RASTR,
					atualID);

			fel.add(newEv);


			filaAgColeta.addLast(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);
		}
	}

	private void term_Rastr_Exec_Info_Peca(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgColeta.isEmpty() && funcSetorModel != 0; i++)
		{


			atualID = filaAgColeta.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgColeta += tmpTFila;

			funcSetorModel--;

			tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.peca_info_min, clTriParams.peca_info_med, clTriParams.peca_info_max));
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_INFO_PECA,
					atualID);

			fel.add(newEv);


			filaAgPreeAreia.addLast(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);

		}
	}

	private void term_InfoPeca_Exec_Pree_Areia(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgPreeAreia.isEmpty() && funcSetorProducao != 0; i++)
		{


			atualID = filaAgPreeAreia.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgPreeAreia += tmpTFila;

			funcSetorProducao--;

			tmpTAtividade = tFinalEvento(new NormalDistribution(rand, clNormParams.pree_areia_media, clNormParams.pree_areia_sd));
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_PREE_AREIA,
					atualID);

			fel.add(newEv);


			filaAgIdentif.addLast(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);
		}
	}

	private void term_Pree_Areia_Exec_IdMaterial(clEventoBase ev)
	{
		double prob;

		for (int i = 0; i < 2 && !filaAgIdentif.isEmpty() && funcSetorProducao != 0; i++)
		{

			prob = rand.nextDouble();
			atualID = filaAgIdentif.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgIdentif += tmpTFila;

			funcSetorProducao--;

			// resina
			if (prob < 0.3)
			{
				if (prob < 0.1)
				{
					tmpTAtividade = tFinalEvento(clConst.IDENTIF_MATERIAL);
					clEventoBase newEv = new clEventoBase(ev,
							tmpTAtividade,
							enumTipoEvento.TERM_ID_MATERIAL_CXT,
							atualID);

					fel.add(newEv);


					filaAgCaixote.addLast(atualID);
					atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() + tmpTAtividade);
				}
				else
				{
					tmpTAtividade = tFinalEvento(clConst.IDENTIF_MATERIAL);
					clEventoBase newEv = new clEventoBase(ev,
							tmpTAtividade,
							enumTipoEvento.TERM_ID_MATERIAL_S_CXT,
							atualID);

					fel.add(newEv);


					filaAgSCaixote.addLast(atualID);
					atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
							tmpTAtividade +
							tmpTFila);
				}
			}
			// máquina
			else if (prob >= 0.3 && prob < 0.6)
			{
				tmpTAtividade = tFinalEvento(clConst.IDENTIF_MATERIAL);
				clEventoBase newEv = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_ID_MATERIAL_MAQ,
						atualID);

				fel.add(newEv);


				filaAgMaquina.addLast(atualID);
				atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
						tmpTAtividade +
						tmpTFila);
			}
			// manual
			else
			{
				tmpTAtividade = tFinalEvento(clConst.IDENTIF_MATERIAL);
				clEventoBase newEv = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_ID_MATERIAL_MAN,
						atualID);

				fel.add(newEv);


				filaAgManual.addLast(atualID);
				atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
						tmpTAtividade +
						tmpTFila);

			}
		}
	}

	private void term_IdMaterial_Exec_ProdCxt(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgCaixote.isEmpty() && funcSetorProducao != 0; i++)
		{


			atualID = filaAgCaixote.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgCaixote += tmpTFila;

			funcSetorProducao--;

			tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.cxt_min, clTriParams.cxt_med, clTriParams.cxt_max));
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_PROD_CXT,
					atualID);

			fel.add(newEv);


			filaAgCheckoutLimpAcab.addLast(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);

		}
	}

	private void term_IdMaterial_Exec_ProdSCxt(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgSCaixote.isEmpty() && funcSetorProducao != 0; i++)
		{


			atualID = filaAgSCaixote.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgSCaixote += tmpTFila;

			funcSetorProducao--;

			tmpTAtividade = tFinalEvento(clConst.PROD_SEM_CXT);
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_PROD_S_CXT,
					atualID);

			fel.add(newEv);


			filaAgCheckoutLimpAcab.addLast(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);
		}
	}

	private void term_IdMaterial_Exec_ProdMaq(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgMaquina.isEmpty() && funcSetorProducao != 0; i++)
		{


			atualID = filaAgMaquina.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgMaquina += tmpTFila;

			funcSetorProducao--;

			tmpTAtividade = tFinalEvento(clConst.PROD_MAQUINA);
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_PROD_MAQ,
					atualID);

			fel.add(newEv);


			filaAgCheckoutLimpAcab.addLast(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);
		}
	}

	private void term_IdMaterial_Exec_ProdMan(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgManual.isEmpty() && funcProdManual != 0; i++)
		{


			atualID = filaAgManual.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgManual += tmpTFila;

			funcProdManual--;

			tmpTAtividade = tFinalEvento(clConst.PROD_MAN);
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_PROD_MAN,
					atualID);

			fel.add(newEv);


			filaAgCheckoutLimpAcab.addLast(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);
		}
	}

	private void term_Prod_Exec_Checkout(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgCheckoutLimpAcab.isEmpty() && funcSetorFinal != 0; i++)
		{


			atualID = filaAgCheckoutLimpAcab.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgCheckoutLimpAcab += tmpTFila;

			funcSetorFinal--;

			// se tem resina ou não
			if (ev.gettEvento() == enumTipoEvento.TERM_PROD_CXT || ev.gettEvento() == enumTipoEvento.TERM_PROD_S_CXT)
			{
				tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.checkout_unif_min, clUnifParams.checkout_unif_max));
				clEventoBase newEv = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_CHECKOUT_LIMP_ACAB_RESIN,
						atualID);

				fel.add(newEv);


				filaAgLimpEsp.addLast(atualID);
				atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
						tmpTAtividade +
						tmpTFila);

			}
			else
			{
				tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.checkout_unif_min, clUnifParams.checkout_unif_max));
				clEventoBase newEv = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_CHECKOUT_LIMP_ACAB,
						atualID);

				fel.add(newEv);


				filaAgAcabamento.addLast(atualID);
				atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
						tmpTAtividade +
						tmpTFila);
			}
		}
	}

	private void term_Checkout_Exec_LimpEsp(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgLimpEsp.isEmpty() && funcCheckoutResina != 0; i++)
		{


			atualID = filaAgLimpEsp.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgLimpEsp += tmpTFila;

			funcCheckoutResina--;

			tmpTAtividade = tFinalEvento(clConst.LIMP_ESP);
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_LIMP_RESIN,
					atualID);

			fel.add(newEv);


			filaAgAcabamento.addLast(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);

		}
	}

	private void term_CheckoutLimpEsp_Exec_Acab(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgAcabamento.isEmpty() && funcSetorFinal != 0; i++)
		{


			atualID = filaAgAcabamento.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgAcabamento += tmpTFila;

			funcSetorFinal--;

			tmpTAtividade = tFinalEvento(new NormalDistribution(rand, clNormParams.acabamento_media, clNormParams.acabamento_sd));
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_ACAB,
					atualID);

			fel.add(newEv);


			filaAgEsmeril.addLast(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);
		}
	}

	private void term_Acab_Exec_Esmeril(clEventoBase ev)
	{
		double prob;

		for (int i = 0; i < 2 && !filaAgEsmeril.isEmpty() && funcSetorFinal != 0; i++)
		{

			prob = rand.nextDouble();
			atualID = filaAgEsmeril.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgEsmeril += tmpTFila;

			funcSetorFinal--;

			if (prob < 0.4)
			{
				tmpTAtividade = tFinalEvento(clConst.ESMERIL);
				clEventoBase newEv = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_ESMER_MAQ,
						atualID);

				fel.add(newEv);


				filaAgRebarbMaq.addLast(atualID);
				atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
						tmpTAtividade +
						tmpTFila);

			}
			else
			{
				tmpTAtividade = tFinalEvento(clConst.ESMERIL);
				clEventoBase newEv = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_ESMER_MAN,
						atualID);

				fel.add(newEv);


				filaAgRebarbMan.addLast(atualID);
				atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
						tmpTAtividade +
						tmpTFila);


			}
		}
	}

	private void term_Esmeril_Exec_RebarbMaq(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgRebarbMaq.isEmpty() && funcSetorFinal != 0; i++)
		{


			atualID = filaAgRebarbMaq.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgRebarb += tmpTFila;

			funcSetorFinal--;

			tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.rebarb_maq_unif_min, clUnifParams.rebarb_maq_unif_max));
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_REB_MAQU,
					atualID);

			fel.add(newEv);


			filaAgAnalisVisual.addLast(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);
		}
	}

	private void term_Esmeril_Exec_RebarbMan(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgRebarbMan.isEmpty() && funcSetorFinal != 0; i++)
		{


			atualID = filaAgRebarbMan.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgRebarbMan += tmpTFila;

			funcSetorFinal--;

			tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.rebarb_min, clTriParams.rebarb_med, clTriParams.rebarb_max));
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_REB_MAN,
					atualID);

			fel.add(newEv);


			filaAgAnalisVisual.addLast(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);
		}
	}

	private void term_Rebarb_Exec_AnalisVisual(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgAnalisVisual.isEmpty() && funcSetorQualidade != 0; i++)
		{


			atualID = filaAgAnalisVisual.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgAnalisVisual += tmpTFila;

			funcSetorQualidade--;

			tmpTAtividade = tFinalEvento(clConst.ANALISE_VIS);
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_ANA_VIS,
					atualID);

			fel.add(newEv);


			filaAgTerceir.addLast(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);
		}
	}

	private void term_AnalisVisual_Exec_ServTer(clEventoBase ev)
	{
		double prob;

		for (int i = 0; i < 2 && !filaAgTerceir.isEmpty(); i++)
		{

			prob = rand.nextDouble();
			atualID = filaAgTerceir.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgTerceir += tmpTFila;

			if (prob < 0.9)
			{
				tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.terce_min, clTriParams.terce_med, clTriParams.terce_max));
				clEventoBase newEv = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_SERV_TER_PIN,
						atualID);

				fel.add(newEv);


				filaAgPint.addLast(atualID);
				atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
						tmpTAtividade +
						tmpTFila);

			}
			else
			{

				tmpTAtividade = tFinalEvento(new TriangularDistribution(rand, clTriParams.terce_min, clTriParams.terce_med, clTriParams.terce_max));
				clEventoBase newEv = new clEventoBase(ev,
						tmpTAtividade,
						enumTipoEvento.TERM_SERV_TER,
						atualID);

				fel.add(newEv);


				filaAgDoc.addLast(atualID);
				atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
						tmpTAtividade +
						tmpTFila);

			}
		}
	}

	private void term_ServTer_Exec_Pint(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgPint.isEmpty() && funcSetorContProd != 0; i++)
		{


			atualID = filaAgPint.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgPint += tmpTFila;

			funcSetorContProd--;

			tmpTAtividade = tFinalEvento(new NormalDistribution(rand, clNormParams.pintura_media, clNormParams.pintura_sd));
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_PINT,
					atualID);

			fel.add(newEv);


			filaAgDoc.addLast(atualID);
			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);
		}
	}

	private void term_Pint_Exec_GerDoc(clEventoBase ev)
	{
		for (int i = 0; i < 2 && !filaAgDoc.isEmpty() && funcSetorDoc != 0; i++)
		{


			atualID = filaAgDoc.removeFirst();
			clock += ev.getTempoExec() - clock;
			tmpTFila = clock - atividadesCriadas.get(atualID).getMarcaTempoEntrada();
			totTempoEspfilaAgDoc += tmpTFila;

			funcSetorDoc--;

			tmpTAtividade = tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.doc_unif_min, clUnifParams.doc_unif_max));
			clEventoBase newEv = new clEventoBase(ev,
					tmpTAtividade,
					enumTipoEvento.TERM_GER_DOC,
					atualID);

			fel.add(newEv);

			atividadesCriadas.get(atualID).setMarcaTempoEntrada(atividadesCriadas.get(atualID).getMarcaTempoEntrada() +
					tmpTAtividade +
					tmpTFila);

			limbo.addLast(atualID);
		}
	}

	// ALT+INSERT


	public void simul(int qtdPecas, int minToRun, long seed)
	{
		int tamAntigo = -1;
		int opt = -1;
		clEventoBase ev;

		init(seed);

		nascedouro(qtdPecas);

		// inc CLOCK
		// TODO mudar semente a cada replicação
		while (clock <= minToRun && !fel.isEmpty())
		{

			ev = fel.poll();

			tamAntigo = fel.size();

			// tratar aqui a liberação dos funcionários
			// TODO verificar quando liberar os recursos
			switch (ev.gettEvento())
			{
				case CHEGADA_PEDIDO:
					term_ChegPedExec_ProcExp(ev);
					break;
				case TERM_PROC:
					assistSetorCompra++;
					term_ProcExpExec_QuimicVerifMatEx(ev);
					break;

				case TERM_QUIMIC_VERIFI_MAT_EX:
					funcSetorExpedicao++;
					term_QuimicVerifMatExExec_VerifDatMatPrim(ev);
					break;
				case TERM_VERIF_DATAS_MAT_PRIM:
					funcSetorExpedicao++;
					term_VerifDatMatPrimExec_PlanProcProd(ev);
					break;

				case TERM_PLAN_PROC_PROD:
					funcSetorExpedicao++;
					term_PlanProcProdExec_VerifArqModDepot(ev);
					break;

				case TERM_VERIF_ARQ_MOD_DEPOT_CRIA:
					funcSetorModel++;
					term_VerifArqModDepot_Exec_CriaMod(ev);
					break;

				case TERM_VERIF_ARQ_MOD_DEPOT_RASTR:
					funcSetorModel++;
					term_CriaMod_Exec_Rastr(ev);
					break;


				case TERM_CRIA_MOD:
					funcSetorModel++;
					term_CriaMod_Exec_Rastr(ev);
					break;

				case TERM_RASTR:
					funcSetorModel++;
					term_Rastr_Exec_Info_Peca(ev);
					break;

				case TERM_INFO_PECA:
					funcSetorModel++;
					term_InfoPeca_Exec_Pree_Areia(ev);
					break;

				case TERM_PREE_AREIA:
					funcSetorProducao++;
					term_Pree_Areia_Exec_IdMaterial(ev);
					break;

				case TERM_ID_MATERIAL_CXT:
					funcSetorProducao++;
					term_IdMaterial_Exec_ProdCxt(ev);
					break;
				case TERM_ID_MATERIAL_S_CXT:
					funcSetorProducao++;
					term_IdMaterial_Exec_ProdSCxt(ev);
					break;
				case TERM_ID_MATERIAL_MAQ:
					funcSetorProducao++;
					term_IdMaterial_Exec_ProdMaq(ev);
					break;
				case TERM_ID_MATERIAL_MAN:
					funcSetorProducao++;
					term_IdMaterial_Exec_ProdMan(ev);
					break;

				case TERM_PROD_MAN:
					funcProdManual++;
					term_Prod_Exec_Checkout(ev);
					break;

				case TERM_PROD_RESIN:
				case TERM_PROD_MAQ:
				case TERM_PROD_CXT:
				case TERM_PROD_S_CXT:
					funcSetorProducao++;
					term_Prod_Exec_Checkout(ev);
					break;

				case TERM_CHECKOUT_LIMP_ACAB_RESIN:
					funcSetorFinal++;
					term_Checkout_Exec_LimpEsp(ev);
					break;

				case TERM_CHECKOUT_LIMP_ACAB:
					funcSetorFinal++;
					term_CheckoutLimpEsp_Exec_Acab(ev);
					break;


				case TERM_LIMP_RESIN:
					funcCheckoutResina++;
					term_CheckoutLimpEsp_Exec_Acab(ev);
					break;

				case TERM_ACAB:
					funcSetorFinal++;
					term_Acab_Exec_Esmeril(ev);
					break;

				case TERM_ESMER_MAQ:
					funcSetorFinal++;
					term_Esmeril_Exec_RebarbMaq(ev);
					break;

				case TERM_ESMER_MAN:
					funcSetorFinal++;
					term_Esmeril_Exec_RebarbMan(ev);
					break;

				case TERM_REB_MAQU:
				case TERM_REB_MAN:
					funcSetorFinal++;
					term_Rebarb_Exec_AnalisVisual(ev);
					break;

				case TERM_ANA_VIS:
					funcSetorQualidade++;
					term_AnalisVisual_Exec_ServTer(ev);
					break;

				case TERM_SERV_TER:
					term_Pint_Exec_GerDoc(ev);
					break;

				case TERM_SERV_TER_PIN:
					term_ServTer_Exec_Pint(ev);
					break;

				case TERM_PINT:
					funcSetorContProd++;
					term_Pint_Exec_GerDoc(ev);
					break;

				case TERM_GER_DOC:
					funcSetorDoc++;
					//nascedouro(qtdPecas);
					break;
				default:
					fel.add(ev);
					break;
			}

		}

		System.out.println("Número de peças atendidas: " + limbo.size());
		System.out.println("Clock: " + clock);
	}
}
