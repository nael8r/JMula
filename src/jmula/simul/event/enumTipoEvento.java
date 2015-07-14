package jmula.simul.event;

/**
 * Autores:
 *
 * João Paulo Fernandes Cerqueira César
 * Natanael Ramos
 * Rodolfo Labiapari Mansur Guimarães
 *
 * Enumerador dos tipos de eventos que podem ocorrer.
 */
public enum enumTipoEvento
{
	// chegada de um pedido
	CHEGADA_PEDIDO(0),
	// término do processo e expedição do pedido
	TERM_PROC(1),
	// término da análise química e verificação dos materiais existentes
	TERM_QUIMIC_VERIFI_MAT_EX(3),
	// Término da verificação de datas e matérias primas
	TERM_VERIF_DATAS_MAT_PRIM(4),
	// Término do planejamento dos processos de produção
	TERM_PLAN_PROC_PROD(5),
	// Término da verificação dos arquivos de modelagem no deposito
	TERM_VERIF_ARQ_MOD_DEPOT(6),
	// término verificação dos arquivos de modelagem no deposito e vai para criação do modelo
	TERM_VERIF_ARQ_MOD_DEPOT_CRIA(36),
	// verificação dos arquivos de modelagem no deposito e vai para rastreabilidade
	TERM_VERIF_ARQ_MOD_DEPOT_RASTR(37),
	// Término da criação do modelo
	TERM_CRIA_MOD(7),
	// Término da rastreabilidade
	TERM_RASTR(8),
	// Término da definição das informações da peça
	TERM_INFO_PECA(9),
	// Término do preenchimento com areia
	TERM_PREE_AREIA(10),
	// Término da identificação do material
	TERM_ID_MATERIAL(11),
	// Término da identificação do material e vai para produção de resina com caixa
	TERM_ID_MATERIAL_CXT(28),
	// Término da identificação do material e vai para produção de resina sem caixa
	TERM_ID_MATERIAL_S_CXT(29),
	// Término da identificação do material e vai para produção com máquina
	TERM_ID_MATERIAL_MAQ(30),
	// Término da identificação do material e vai para produção manual
	TERM_ID_MATERIAL_MAN(31),
	// Término da Resina
	TERM_PROD_RESIN(12),
	// Término da máquina
	TERM_PROD_MAQ(13),
	// Término do Manual
	TERM_PROD_MAN(14),
	// Término com caixote
	TERM_PROD_CXT(15),
	// Término sem caixote
	TERM_PROD_S_CXT(16),
	// Término do chekout/limpeza e acabamento
	TERM_CHECKOUT_LIMP_ACAB(17),
	// término do checkout e vai para a limpeza especial da resina
	TERM_CHECKOUT_LIMP_ACAB_RESIN(32),
	// Término da limpeza da resina
	TERM_LIMP_RESIN(18),
	// Término do acabamento
	TERM_ACAB(19),
	// Término do esmeril
	TERM_ESMER(20),
	// termina o esmerila e vai para rebarbeamento por máquina
	TERM_ESMER_MAQ(33),
	// termina o esmerila e vai para rebarbeamento manual
	TERM_ESMER_MAN(34),
	// Término da rebarbação por máquina
	TERM_REB_MAQU(21),
	// Término da rebarbação manual
	TERM_REB_MAN(22),
	// Término da análise visual
	TERM_ANA_VIS(23),
	// Término do serviço terceirizado
	TERM_SERV_TER(24),
	// termina o serviço de terceiros e vai para a pintura
	TERM_SERV_TER_PIN(35),
	// Término da pintura
	TERM_PINT(26),
	// Término da geração da documentação
	TERM_GER_DOC(27);

	private int value;

	enumTipoEvento(int value)
	{
		this.value = value;
	}
}
