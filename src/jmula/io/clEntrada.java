package jmula.io;

import jmula.common.clDebug;
import jmula.simul.entity.clEntidades;
import jmula.simul.parameters.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Autores:
 *
 * João Paulo Fernandes Cerqueira César
 * Natanael Ramos
 * Rodolfo Labiapari Mansur Guimarães
 *
 * Trata as operações de entrada de dados da aplicação.
 */
public class clEntrada
{
	// cabeçalho de leitura do arquivo
	private static String cabecalho = "";

	/**
	 * Emite mensagens de erro de leitura do arquivo de configuração.
	 *
	 * @param tag Tag do arquivo de configuração que está mal formada.
	 */
	public static void err(String tag)
	{
		System.err.println("Arquivo de entrada mal formado! Na tag: " + tag);
		System.err.println("Abortando...");
		System.exit(-1);
	}

	/**
	 * Lê os parâmetros de uma distribuição triangular e preenche para a respectiva variável.
	 *
	 * @param in Leitor do arquivo
	 * @param var Variavel
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	private static void leTriParams(Scanner in, String var) throws NoSuchFieldException, IllegalAccessException
	{
		// campo da classe clTriParams
		Field campo;

		// se tem o próximo valor
		if (in.hasNextDouble())
		{
			// retorna o campo da classe de acordo com o nome dele
			campo = clTriParams.class.getField(var + "_min");

			// define o valor do atributo
			campo.setDouble(clTriParams.class, in.nextDouble());
		}
		else
			err(cabecalho);

		// nome do parâmetro
		cabecalho = in.next();

		// valor do parâmetro
		if (in.hasNextDouble())
		{
			// retorna o campo da classe de acordo com o nome dele
			campo = clTriParams.class.getField(var + "_med");

			// define o valor do atributo
			campo.setDouble(clTriParams.class, in.nextDouble());
		}
		else
			err(cabecalho);

		// nome do parâmetro
		cabecalho = in.next();

		// valor do parâmetro
		if (in.hasNextDouble())
		{
			// retorna o campo da classe de acordo com o nome dele
			campo = clTriParams.class.getField(var + "_max");

			// define o valor do atributo
			campo.setDouble(clTriParams.class, in.nextDouble());
		}
		else
			err(cabecalho);

	}

	/**
	 * Lê os parâmetros de uma distribuição uniforme e preenche para a respectiva variável.
	 *
	 * @param in Leitor do arquivo
	 * @param var Variável
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	private static void leUnifParams(Scanner in, String var) throws NoSuchFieldException, IllegalAccessException
	{
		// campo da classe clunifParams
		Field campo;

		// se tem o próximo valor
		if (in.hasNextInt())
		{
			// retorna o campo da classe de acordo com o nome dele
			campo = clUnifParams.class.getField(var + "_min");

			// define o valor do atributo
			campo.setInt(clTriParams.class, in.nextInt());
		}
		else
			err(cabecalho);

		// nome do parâmetro
		cabecalho = in.next();

		// valor do parâmetro
		if (in.hasNextInt())
		{
			// retorna o campo da classe de acordo com o nome dele
			campo = clUnifParams.class.getField(var + "_max");

			// define o valor do atributo
			campo.setInt(clTriParams.class, in.nextInt());
		}
		else
			err(cabecalho);

	}

	/**
	 * Lê os parâmetros de uma distribuição normal e preenche para a respectiva variável.
	 *
	 * @param in Leitor do arquivo
	 * @param var Variável
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	private static void leNormParams(Scanner in, String var) throws NoSuchFieldException, IllegalAccessException
	{
		// campo da classe clNormParams
		Field campo;

		// se tem o próximo valor
		if (in.hasNextDouble())
		{
			// retorna o campo da classe de acordo com o nome dele
			campo = clNormParams.class.getField(var + "_media");

			// define o valor do atributo
			campo.setDouble(clTriParams.class, in.nextDouble());
		}
		else
			err(cabecalho);

		// nome do parâmetro
		cabecalho = in.next();

		// valor do parâmetro
		if (in.hasNextDouble())
		{
			// retorna o campo da classe de acordo com o nome dele
			campo = clNormParams.class.getField(var + "_sd");

			// define o valor do atributo
			campo.setDouble(clTriParams.class, in.nextDouble());
		}
		else
			err(cabecalho);

	}

	/**
	 * Lê os parâmetros de uma constante e preenche para a respectiva variável.
	 *
	 * @param in Leitor do arquivo
	 * @param var Variável
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	private static void leCTE(Scanner in, String var) throws NoSuchFieldException, IllegalAccessException
	{
		// campo da classe clConst
		Field campo;

		// se tem o próximo valor
		if (in.hasNextInt())
		{
			// retorna o campo da classe de acordo com o nome dele
			campo = clConst.class.getField(var + "_cte");

			// define o valor do atributo
			campo.setInt(clConst.class, in.nextInt());
		}
		else
			err(cabecalho);

	}

	/**
	 * Método para leitura do arquivo de configuração.
	 *
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public static void leArquivoConfig() throws NoSuchFieldException, IllegalAccessException
	{
		// tenta criar o leitor de arquivo
		try (Scanner in = new Scanner(new File(clSimulParams.ARQ_CONF)))
		{
			// enquanto houver o que ler no arquivo
			while (in.hasNext())
			{
				// tag que está sendo lida
				cabecalho = in.next();

				switch (cabecalho)
				{
					// quantidade de replicações
					case "n":
						if (in.hasNextInt())
							clSimulParams.MAX_REPLICACOES = in.nextInt();
						else
							err("n");
						break;
					// tempo máximo de simulação
					case "t":
						if (in.hasNextInt())
							clSimulParams.MAX_TEMPO = in.nextInt();
						else
							err("t");
						break;
					// número máximo de pedidos para chegarem no período especificado
					case "p":
						if (in.hasNextInt())
							clSimulParams.MAX_PEDIDO = in.nextInt();
						else
							err("p");
						break;
					// Quantidade de entidades
					case "funcSetorCompra":
						if (in.hasNextInt())
							clEntidades.funcSetorCompra = in.nextInt();
						else
							err("funcSetorCompra");
						break;
					case "funcSetorExpedicao":
						if (in.hasNextInt())
							clEntidades.funcSetorExpedicao = in.nextInt();
						else
							err("funcSetorCompra");
						break;
					case "funcSetorModel":
						if (in.hasNextInt())
							clEntidades.funcSetorModel = in.nextInt();
						else
							err("funcSetorCompra");
						break;
					case "funcSetorProducao":
						if (in.hasNextInt())
							clEntidades.funcSetorProducao = in.nextInt();
						else
							err("funcSetorCompra");
						break;
					case "funcProdManual":
						if (in.hasNextInt())
							clEntidades.funcProdManual = in.nextInt();
						else
							err("funcSetorCompra");
						break;
					case "funcSetorFinal":
						if (in.hasNextInt())
							clEntidades.funcSetorFinal = in.nextInt();
						else
							err("funcSetorCompra");
						break;
					case "funcCheckoutResina":
						if (in.hasNextInt())
							clEntidades.funcCheckoutResina = in.nextInt();
						else
							err("funcSetorCompra");
						break;
					case "funcSetorQualidade":
						if (in.hasNextInt())
							clEntidades.funcSetorQualidade = in.nextInt();
						else
							err("funcSetorCompra");
						break;
					case "funcSetorContProd":
						if (in.hasNextInt())
							clEntidades.funcSetorContProd = in.nextInt();
						else
							err("funcSetorCompra");
						break;
					case "funcSetorDoc":
						if (in.hasNextInt())
							clEntidades.funcSetorDoc = in.nextInt();
						else
							err("funcSetorCompra");
						break;
					case "servTerc":
						if (in.hasNextInt())
							clEntidades.servTerc = in.nextInt();
						else
							err("servTerc");
						break;
					// parâmetros das distribuições
					case "norm_cheg_ped_med":
						leNormParams(in, "cheg_ped");
						break;
					case "tri_proc_exp_ped_min":
						leTriParams(in, "proc_exp_ped");
						break;
					case "tri_ana_quimic_verif_mat_ex_min":
						leTriParams(in, "ana_quimic_verif_mat_ex");
						break;
					case "unif_verif_data_mat_prim_min":
						leUnifParams(in, "verif_data_mat_prim");
						break;
					case "norm_est_plan_proc_prod_med":
						leNormParams(in, "est_plan_proc_prod");
						break;
					case "unif_verif_arq_mod_dep_min":
						leUnifParams(in, "verif_arq_mod_dep");
						break;
					case "tri_cria_mod_min":
						leTriParams(in, "cria_mod");
						break;
					case "tri_rastr_hist_mat_min":
						leTriParams(in, "rastr_hist_mat");
						break;
					case "tri_coloc_info_peca_min":
						leTriParams(in, "coloc_info_peca");
						break;
					case "norm_pree_areia_med":
						leNormParams(in, "pree_areia");
						break;
					case "cte_id_mat":
						leCTE(in, "id_mat");
						break;
					case "cte_resin_s_cxt":
						leCTE(in, "resin_s_cxt");
						break;
					case "cte_prod_maq":
						leCTE(in, "prod_maq");
						break;
					case "cte_prod_man":
						leCTE(in, "prod_man");
						break;
					case "tri_resin_cxt_min":
						leTriParams(in, "resin_cxt");
						break;
					case "unif_rel_checkout_lim_acab_min":
						leUnifParams(in, "rel_checkout_lim_acab");
						break;
					case "cte_limp_esp":
						leCTE(in, "limp_esp");
						break;
					case "norm_acab_med":
						leNormParams(in, "acab");
						break;
					case "cte_esmeril":
						leCTE(in, "esmeril");
						break;
					case "unif_rebarb_maq_min":
						leUnifParams(in, "rebarb_maq");
						break;
					case "tri_rebarb_man_min":
						leTriParams(in, "rebarb_man");
						break;
					case "cte_ana_vis":
						leCTE(in, "ana_vis");
						break;
					case "tri_serv_ter_min":
						leTriParams(in, "serv_ter");
						break;
					case "norm_receb_pint_med":
						leNormParams(in, "receb_pint");
						break;
					case "unif_ger_doc_min":
						leUnifParams(in, "ger_doc");
						break;
					default:
						// comentário
						in.nextLine();
						break;

				}
			}
		}
		// se não encontrar o arquivo de entrada
		catch (FileNotFoundException e)
		{
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Interpreta os argumentos passados para a aplicação
	 *
	 * @param args argumentos
	 */
	public static void leArgumentos(String[] args)
	{
		StringTokenizer strTk;
		String tmp = "";

		// tem que ter no mínimo dois argumentos, in e out
		if (args.length >= 2)
		{
			// percorre os argumentos
			for (String arg : args)
			{
				// se for o de debug, vê o tipo desejado
				if (arg.contains("debug"))
				{
					// filtra o argumento, pegando apenas os valores
					strTk = new StringTokenizer(arg, "=");
					strTk.nextToken();
					tmp = strTk.nextToken();
					// separa cada valor
					strTk = new StringTokenizer(tmp, ",");

					while (strTk.hasMoreTokens())
					{
						// entidade
						tmp = strTk.nextToken();
						if (tmp.equals("entidade"))
							clDebug.dbgEntidade = true;
						else if (!clDebug.dbgEntidade)
							clDebug.dbgEntidade = false;

						// fila
						if (tmp.equals("fila"))
							clDebug.dbgFila = true;
						else if (!clDebug.dbgFila)
							clDebug.dbgFila = false;

						// evento
						if (tmp.equals("evento"))
							clDebug.dbgEvento = true;
						else if (!clDebug.dbgEvento)
							clDebug.dbgEvento = false;

						// informação
						if (tmp.equals("info"))
							clDebug.dbgInfo = true;
						else if (!clDebug.dbgInfo)
							clDebug.dbgInfo = false;

						// todos os tipos de debug
						if (tmp.equals("tudo"))
						{
							clDebug.dbgEntidade = true;
							clDebug.dbgFila = true;
							clDebug.dbgEvento = true;
						}
					}
				}
				// arquivo de entrada
				else if (arg.contains("in"))
				{
					strTk = new StringTokenizer(arg, "=");
					strTk.nextToken();
					tmp = strTk.nextToken();
					clSimulParams.ARQ_CONF = tmp;
				}
				// arquivo de saida
				else if (arg.contains("out"))
				{
					strTk = new StringTokenizer(arg, "=");
					strTk.nextToken();
					tmp = strTk.nextToken();
					clSimulParams.NOME_ARQ_SAIDA = tmp;
				}
			}
		}
		else
		{
			System.err.println("Sintaxe incorreta! Utilizar:");
			System.err.println("java jmula.JMula in=<arquivo-configuração> out=<arquivo-saida> degub=<opções>");
			System.err.println("Opções de debug (separadas por vírgula, sem espaço):");
			System.err.println("\tentidade: mostra informações das entidades" +
					"\n\tfila: mostra informações das filas" +
					"\n\tevento: mostra informações dos eventos" +
					"\n\ttudo: mostra todas as informações");

			System.exit(-1);
		}
	}
}
