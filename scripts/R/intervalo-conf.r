# Script para análise de dados de resultados da simulação

# Calcula o intervalo de confian?a para um ?nico indicador de desempenho da simula??o
IntervaloConfianca = function(amostra, alpha, precisao)
{
  boxplot(amostra);
  
  # calculando a m?dia e desvio padr?o
  media = mean(amostra);
  desvio = sd(amostra);

  cat('média: ', media, '\n');
  cat('sd: ', desvio, '\n');
  
  # calculando a probabilidade (1 - alpha)%
  prob = 1 - alpha/2;
  
  # calculando a amplitude do intervalo
  H = qt(p=prob, df=(length(amostra) - 1)) * desvio / sqrt(length(amostra));

  cat('H:',H, '\n');
  
  inf <- media - H;
  sup <- media + H;
  
  cat('Intervalo de Confiança: [',inf,';',sup,']\n');
  
  if(H > precisao)
  {
  	 cat('Precisão inferior a 2 horas, estimando o número n* de rodadas para alcançar a precisão desejada\n');

	  hStar <- precisao;
	  
	  nStar <- ceiling(length(amostra)*((H/hStar)^2));
	  
	  cat('Rodadas n*: ', nStar, '\n');
  }
  
}

# pegar argumentos da linha de comando
args <- commandArgs(TRUE);

inputFile <- args[1];
varName <- args[2];
precisao <- as.numeric(args[3]);

dataset <- read.table(file = inputFile, header = T, sep = '\t');

names(dataset);

sample <- dataset[[varName]];

cat('\nTamanho da Amostra: ',length(sample),'\n');

png(paste('box-',inputFile,'.png'));
boxplot(sample, main=varName);
dev.off();

png(paste('hist-',inputFile,'.png'));
hist(sample, freq=F, main=varName, xlab = varName);
dev.off();

IntervaloConfianca(sample, 0.05, precisao);