Notas:
	
	Foram utilizados alguns recursos mais modernos da linguagem Java, como o Java Reflection, dessa forma, não é garantido que a aplicação funcione em versões muito antigas do Java. Para ajudar o usuário, dentro da pasta scripts/Bash/ existe o script 'UpdateJava.sh' para atualizar o Java para a última versão em distribuições Linux.

=================================================================================================================================================
Compilar:

	Para compilar é necessária a utilização da ferramenta de build Ant (uma espécie de make do Java), foi utilizada tal ferramenta devido a quantidade de classes e dependências da aplicação. Tal ferramenta é gratuita e está disponivel na maioria dos repositorios de distribuições Linux, com o nome de pacote 'ant. Caso seja de preferência, binários e o código-fonte da aplicação também podem ser obtidos em: http://ant.apache.org/

	Uma vez instalada a ferramenta, no diretório raiz da aplicação codigo/, que contem o arquivo 'build.xml', executa-se o comando:

		ant

	E os arquivos compilados são gerados em ./codigo/out/production/JMula/

	Caso deseje-se apagar os arquivos compilados, pode-se, no diretório raiz da aplicação, executar:

		ant clean

=================================================================================================================================================
Executar:

Uma vez compilado, acessar o diretório ./codigo/out/production/JMula/ e A PARTIR DELE, executar:

	java jmula.JMula in=<arquivo-configuração> out=<nome-arquivo-saida> debug=<opção>

Onde jmula é o nome do pacote e JMula é o nome da classe principal. O PARÂMETRO DEBUG É OPCIONAL.

Tipos de debug disponíveis:
	entidade: mostra informações das entidades
	fila: mostra informações das filas
	evento: mostra informações dos eventos
	info: informações úteis, estatísticas
	tudo: mostra todas as informações

Versões do Java onde foi testado (java -version):

	java version "1.8.0_45"
	Java(TM) SE Runtime Environment (build 1.8.0_45-b14)
	Java HotSpot(TM) 64-Bit Server VM (build 25.45-b02, mixed mode)

=================================================================================================================================================
Scripts [R]

	Dentro do diretório ./codigo/scripts/R/ estão contidos os scripts utilizados para análise da saída de dados da simulação:

		basic-stat.r: calcula estatísticas de posição e dispersão da amostra. Como usar:

			Rscript basic-stat.r <arquivo-de-entrada> <nome-variável>

		Onde <nome-variável> é o nome do cabeçalho para ser utilizado como amostra.

		diff-2amostras.r: calcula o intervalo de confiança da diferença de duas médias e mostra qual amostra tem maior média. Como usar:

			Rscript diff-2amostras.r <arquivo-de-entrada-amostra1> <arquivo-de-entrada-amostra2> <nome-variável> <precisão-IC>

		Onde <nome-variável> é o nome do cabeçalho para ser utilizado como amostra e <precisão-IC> é a precisão desejada para o intervalo de confiança.

		intervalo-conf.r: Calcula o intervalo de confiança de uma amostra e também, gera seu histograma de densidade e boxplot. Como usar:

			Rscript intervalo-conf.r <arquivo-de-entrada> <nome-variável> <precisão-IC>

		Onde <nome-variável> é o nome do cabeçalho para ser utilizado como amostra e <precisão-IC> é a precisão desejada para o intervalo de confiança.

=================================================================================================================================================
Documentação

	Dentro do diretório ./codigo/doc/ pode ser encontrada a documentação da aplicação em formato html, podendo ser acessada pelo arquivo 'index.html'

=================================================================================================================================================
Configuração

	Dentro do diretório ./codigo/config/ existe o arquivo 'comfig.template' que serve como template para criar arquivos de configuração para a aplicação.
