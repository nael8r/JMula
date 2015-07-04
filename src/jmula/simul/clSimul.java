package jmula.simul;

import static jmula.simul.entity.clEntidades.*;
import jmula.simul.event.clEventoBase;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

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
	private int clock;
	// ID das entidades de pedidos
	private Integer ID;

	private MersenneTwister rand;
	private NormalDistribution norm;

	private Queue<clEventoBase> fes;
	// fila de aguardo do processo e expedição
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
	private LinkedList<Integer> filaAgResina;
	private LinkedList<Integer> filaAgMáquina;
	private LinkedList<Integer> filaAgManual;
	private LinkedList<Integer> filaAgCaixote;
	private LinkedList<Integer> filaAgSCaixote;
	private LinkedList<Integer> filaAgCheckoutLimpAcab;
	private LinkedList<Integer> filaAgLimpEsp;
	private LinkedList<Integer> filaAgAcabamento;
	private LinkedList<Integer> filaAgEsmeril;
	private LinkedList<Integer> filaAgRebarb;
	private LinkedList<Integer> filaAgRebarbMan;
	private LinkedList<Integer> filaAgAnalisVisual;
	private LinkedList<Integer> filaAgTerceir;
	private LinkedList<Integer> filaAgValid;
	private LinkedList<Integer> filaAgPint;
	private LinkedList<Integer> filaAgDoc;

	// Métodos de geração
	private void nascedouro()
	{
		// TODO ver se gera um ou mais itens em primeira instância
		clEventoBase newEv = new clEventoBase(this, tFinalEvento(norm), enumTipoEvento.CHEGADA_PEDIDO);

		fes.add(newEv);

		filaAgProc.addLast(ID);

		ID++;
	}

	// Métodos principais da simulação
	public void init(long seed)
	{
		rand = new MersenneTwister(seed);
		fes = new PriorityQueue<>();
		norm = new NormalDistribution(rand, clNormParams.media, clNormParams.sd);
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
		filaAgIdentif  = new LinkedList<>();;
		filaAgResina = new LinkedList<>();
		filaAgMáquina = new LinkedList<>();
		filaAgManual = new LinkedList<>();
		filaAgCaixote = new LinkedList<>();
		filaAgSCaixote = new LinkedList<>();
		filaAgCheckoutLimpAcab = new LinkedList<>();
		filaAgLimpEsp = new LinkedList<>();
		filaAgAcabamento = new LinkedList<>();
		filaAgEsmeril = new LinkedList<>();
		filaAgRebarb = new LinkedList<>();
		filaAgRebarbMan = new LinkedList<>();
		filaAgAnalisVisual = new LinkedList<>();
		filaAgTerceir = new LinkedList<>();
		filaAgValid = new LinkedList<>();
		filaAgPint = new LinkedList<>();
		filaAgDoc = new LinkedList<>();
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

	private void trataChegadaPedido(clEventoBase ev)
	{
		if(!filaAgProc.isEmpty() && assistSetorCompra!=0)
		{
			filaAgProc.removeFirst();

			assistSetorCompra--;

			clock += ev.getTempoExec();

			clEventoBase newEv = new clEventoBase(ev,
					tFinalEvento(new TriangularDistribution(rand, clTriParams.proc_exp_min, clTriParams.proc_exp_max, clTriParams.proc_exp_med)),
					enumTipoEvento.TERM_PROC);

			fes.add(newEv);

			filaAgAnalis.addLast(ID);
		}
	}

	private void trataTerminoExpedPedido(clEventoBase ev)
	{
		if(!filaAgAnalis.isEmpty() && funcSetorExpedicao!=0)
		{
			filaAgAnalis.removeFirst();

			funcSetorExpedicao--;

			clock += ev.getTempoExec();

			clEventoBase newEv = new clEventoBase(ev,
					tFinalEvento(new TriangularDistribution(rand, clTriParams.quimic_mater_min, clTriParams.quimic_mater_max, clTriParams.quimic_mater_med)),
					enumTipoEvento.TERM_QUIMIC_VERIFI_MAT_EX);

			fes.add(newEv);

			filaAgVerifDataMater.addLast(ID);
		}
	}

	// Verificar datas, matéria primas
	private void trataTerminoVerifDatasMater(clEventoBase ev)
	{
		if(!filaAgVerifDataMater.isEmpty() && funcSetorExpedicao!=0)
		{
			filaAgVerifDataMater.removeFirst();

			funcSetorExpedicao--;

			clock += ev.getTempoExec();

			clEventoBase newEv = new clEventoBase(ev,
					tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.verif_data_mater_unif_min, clUnifParams.verif_data_mater_unif_max)),
					enumTipoEvento.TERM_VERIF_DATAS_MAT_PRIM);

			fes.add(newEv);

			filaAgPlanejamento.addLast(ID);
		}
	}

	// estudo, planejamento dos processos de produção
	private void trataTerminoPlanProcProd(clEventoBase ev)
	{
		if(!filaAgPlanejamento.isEmpty() && funcSetorExpedicao!=0)
		{
			filaAgPlanejamento.removeFirst();

			funcSetorExpedicao--;

			clEventoBase newEv = new clEventoBase(ev,
					tFinalEvento(new NormalDistribution(rand, clNormParams.media_plan_proc_prod, clNormParams.sd_plan_proc_prod)),
					enumTipoEvento.TERM_PLAN_PROC_PROD);

			fes.add(newEv);

			filaAgVerificacao.add(ID);
		}
	}

	// término da verificação de arquivos de modelagem no depósito
	private void trataTerminoVerifArqModelDepo(clEventoBase ev, double prob)
	{
		if(!filaAgVerificacao.isEmpty() && funcSetorModel!=0)
		{
			filaAgVerificacao.removeFirst();

			funcSetorModel--;

			clEventoBase newEv = new clEventoBase(ev,
					tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.verif_arq_depo_unif_min, clUnifParams.verif_arq_depo_unif_max)),
					enumTipoEvento.TERM_VERIF_ARQ_MOD_DEPOT);

			fes.add(newEv);

			if(prob <= 0.3)
			{
				filaAgMoldPronto.addLast(ID);
			}
			else
			{
				filaAgRastr.addLast(ID);
			}

		}
	}

	// término da criação do modelo
	private void trataTerminoCriacaoModelo(clEventoBase ev)
	{
		if(!filaAgMoldPronto.isEmpty() && funcSetorModel!=0)
		{
			filaAgMoldPronto.removeFirst();

			funcSetorModel--;

			clEventoBase newEv = new clEventoBase(ev,
					tFinalEvento(new TriangularDistribution(rand, clTriParams.cri_model_min, clTriParams.cri_model_max, clTriParams.cri_model_med)),
					enumTipoEvento.TERM_CRIA_MOD);

			fes.add(newEv);

			filaAgRastr.addLast(ID);
		}
	}

	// ratreabilidade, histórico do material
	private void trataTerminoRastr(clEventoBase ev)
	{
		if(!filaAgRastr.isEmpty() && funcSetorModel!=0)
		{
			filaAgRastr.removeFirst();

			funcSetorModel--;

			clEventoBase newEv = new clEventoBase(ev,
					tFinalEvento(new TriangularDistribution(rand, clTriParams.rastr_min, clTriParams.rastr_max, clTriParams.rastr_med)),
					enumTipoEvento.TERM_RASTR);

			fes.add(newEv);

			filaAgColeta.addLast(ID);
		}
	}

	private void trataTerminoInfoPeca(clEventoBase ev)
	{
		if(!filaAgColeta.isEmpty() && funcSetorModel!=0)
		{
			filaAgColeta.removeFirst();

			funcSetorModel--;

			clEventoBase newEv = new clEventoBase(ev,
					tFinalEvento(new TriangularDistribution(rand, clTriParams.peca_info_min, clTriParams.peca_info_max, clTriParams.peca_info_med)),
					enumTipoEvento.TERM_INFO_PECA);

			fes.add(newEv);

			filaAgPreeAreia.addLast(ID);
		}
	}

	private void trataTerminoPreencAreia(clEventoBase ev)
	{
		if(!filaAgPreeAreia.isEmpty() && funcSetorProducao!=0)
		{
			filaAgPreeAreia.removeFirst();

			funcSetorProducao--;

			clEventoBase newEv = new clEventoBase(ev,
					tFinalEvento(new NormalDistribution(rand, clNormParams.media_areia, clNormParams.sd_areia)),
					enumTipoEvento.TERM_PREE_AREIA);

			fes.add(newEv);

			filaAgIdentif.addLast(ID);
		}
	}

	private void trataTerminoIdentifMater(clEventoBase ev, double prob)
	{
		if(!filaAgIdentif.isEmpty() && funcSetorProducao!=0)
		{
			filaAgIdentif.removeFirst();

			funcSetorProducao--;

			clEventoBase newEv = new clEventoBase(ev,
					tFinalEvento(clConst.const_identif_material),
					enumTipoEvento.TERM_ID_MATERIAL);

			fes.add(newEv);

			// resina
			if(prob<30)
			{
				if(prob<10)
				{
					filaAgCaixote.addLast(ID);
				}
				else
				{
					filaAgSCaixote.addLast(ID);
				}
			}
			// máquina
			else if(prob>=30 && prob<60)
			{
				filaAgMáquina.addLast(ID);
			}
			// manual
			else
			{
				filaAgManual.addLast(ID);
			}
		}
	}

	private void trataTerminoResinaCxt(clEventoBase ev)
	{
		if(!filaAgCaixote.isEmpty() && funcSetorProducao!=0)
		{
			filaAgCaixote.removeFirst();

			funcSetorProducao--;

			clEventoBase newEv = new clEventoBase(ev,
					tFinalEvento(new TriangularDistribution(rand, clTriParams.cxt_min, clTriParams.cxt_max, clTriParams.cxt_med)),
					enumTipoEvento.TERM_CXT);

			fes.add(newEv);

			filaAgCheckoutLimpAcab.addLast(ID);

		}
	}

	private void trataTerminoResinaSCxt(clEventoBase ev)
	{
		if(!filaAgSCaixote.isEmpty() && funcSetorProducao!=0)
		{
			filaAgSCaixote.removeFirst();

			funcSetorProducao--;

			clEventoBase newEv = new clEventoBase(ev,
					tFinalEvento(clConst.const_sem_cxt),
					enumTipoEvento.TERM_CXT);

			fes.add(newEv);

			filaAgCheckoutLimpAcab.addLast(ID);

		}
	}

	private void trataTerminoMaquina(clEventoBase ev)
	{
		if(!filaAgMáquina.isEmpty() && funcSetorProducao!=0)
		{
			filaAgMáquina.removeFirst();

			funcSetorProducao--;

			clEventoBase newEv = new clEventoBase(ev,
					tFinalEvento(clConst.const_maquina),
					enumTipoEvento.TERM_MAQ);

			fes.add(newEv);

			filaAgCheckoutLimpAcab.addLast(ID);
		}
	}

	private void trataTerminoManual(clEventoBase ev)
	{
		if(!filaAgManual.isEmpty() && funcProdManual!=0)
		{
			filaAgManual.removeFirst();

			funcProdManual--;

			clEventoBase newEv = new clEventoBase(ev,
					tFinalEvento(clConst.const_man),
					enumTipoEvento.TERM_MAN);

			fes.add(newEv);

			filaAgCheckoutLimpAcab.addLast(ID);

		}
	}

	private void trataTerminoCheckout(clEventoBase ev)
	{
		if(!filaAgCheckoutLimpAcab.isEmpty() && funcSetorFinal!=0)
		{
			filaAgCheckoutLimpAcab.removeFirst();

			funcSetorFinal--;

			clEventoBase newEv = new clEventoBase(ev,
					tFinalEvento(new UniformIntegerDistribution(rand, clUnifParams.checkout_unif_min, clUnifParams.checkout_unif_max)),
					enumTipoEvento.TERM_LIMP_ACAB);

			fes.add(newEv);

			// se tem resina ou não
			if(ev.gettEvento()==enumTipoEvento.TERM_CXT || ev.gettEvento()==enumTipoEvento.TERM_S_CXT)
			{
				filaAgLimpEsp.add(ID);
			}
			else
			{
				filaAgAcabamento.add(ID);
			}
		}
	}

	private void trataFinalLimpEsp(clEventoBase ev)
	{
		
	}


	public void simul()
	{
		clEventoBase ev;
		nascedouro();

		while(!fes.isEmpty())
		{
			ev = fes.poll();

			// tratar aqui a liberação dos funcionários
			switch (ev.gettEvento())
			{
				case CHEGADA_PEDIDO: trataChegadaPedido(ev);  break;
			}
		}
	}
}
