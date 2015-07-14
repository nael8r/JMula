package jmula.simul.parameters;

/**
 * Autores:
 *
 * João Paulo Fernandes Cerqueira César
 * Natanael Ramos
 * Rodolfo Labiapari Mansur Guimarães
 *
 * Classe com os parâmetros das atividades que seguem uma distribuição triangular.
 */
public class clTriParams
{
	// processamento e expedição do pedido
	public static double proc_exp_ped_min = 30;
	public static double proc_exp_ped_max = 180;
	public static double proc_exp_ped_med = 40;

	// análise quimica e verificação dos materiais existentes
	public static double ana_quimic_verif_mat_ex_min = 15;
	public static double ana_quimic_verif_mat_ex_max = 180;
	public static double ana_quimic_verif_mat_ex_med = 30;

	// criação do modelo
	public static double cria_mod_min = 480 ;
	public static double cria_mod_max = 7200;
	public static double cria_mod_med = 4320;

	// rastreabilidade, histórico do material
	public static double rastr_hist_mat_min = 2;
	public static double rastr_hist_mat_max = 180;
	public static double rastr_hist_mat_med = 30;

	// colocar informações na peça
	public static double coloc_info_peca_min = 5;
	public static double coloc_info_peca_max = 40;
	public static double coloc_info_peca_med = 10;

	// produção da peça com resina e caixote
	public static double resin_cxt_min = 20;
	public static double resin_cxt_max = 28;
	public static double resin_cxt_med = 24;

	// rebarbeamento manual
	public static double rebarb_man_min = 3;
	public static double rebarb_man_max = 10;
	public static double rebarb_man_med = 5;

	// serviço de terceiros
	public static double serv_ter_min = 4320;
	public static double serv_ter_max = 10080;
	public static double serv_ter_med = 5760;

}
