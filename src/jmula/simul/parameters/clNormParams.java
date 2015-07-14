package jmula.simul.parameters;

/**
 * Autores:
 *
 * João Paulo Fernandes Cerqueira César
 * Natanael Ramos
 * Rodolfo Labiapari Mansur Guimarães
 *
 * Classe com os parâmetros das atividades que seguem uma distribuição normal.
 */
public class clNormParams
{
	// chegada do pedido
	public static double cheg_ped_media = 120;
	public static double cheg_ped_sd = 40;

	// estudo, planejamento dos processos de produção
	public static double est_plan_proc_prod_media = 30;
	public static double est_plan_proc_prod_sd = 12;

	// preenchimento com areia
	public static double pree_areia_media = 4;
	public static double pree_areia_sd = 2;

	// acabamento
	public static double acab_media = 60;
	public static double acab_sd = 30;

	// pintura
	public static double receb_pint_media = 1;
	public static double receb_pint_sd = 2;

}
