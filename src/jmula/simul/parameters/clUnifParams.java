package jmula.simul.parameters;

/**
 * Autores:
 *
 * João Paulo Fernandes Cerqueira César
 * Natanael Ramos
 * Rodolfo Labiapari Mansur Guimarães
 *
 * Classe com os parâmetros das atividades que seguem uma distribuição uniforme.
 */
public class clUnifParams
{
	// verifica datas, metérias primas
	public static int verif_data_mat_prim_min = 15;
	public static int verif_data_mat_prim_max = 45;

	// verifica arquivos de modelagem no depósito
	public static int verif_arq_mod_dep_min = 15;
	public static int verif_arq_mod_dep_max = 25;

	// checkout, limpeza e acabamento
	public static int rel_checkout_lim_acab_min = 2;
	public static int rel_checkout_lim_acab_max = 3;

	// rebarbeamento por máquina
	public static int rebarb_maq_min = 1;
	public static int rebarb_maq_max = 3;

	// geração da documentação
	public static int ger_doc_min = 1440;
	public static int ger_doc_max = 4320;

}
