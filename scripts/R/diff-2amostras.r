# Autores
# João Paulo Fernandes Cerqueira César
# Natanael Ramos
# Rodolfo Labiapari Mansur Guimarães

#
# Calcula intervalo de confianca para diferenca de duas amostras com mesmo
# numero de observacoes
#
IntervaloConfianca2AmostasNIguais <- function(nome, amostra1, amostra2, alpha,precisao)
{
  # obtem dimensao das amostras (neste caso, o tamanho de ambas sao iguais)
  numObs = length(amostra1);
  
  # Calcula a amostra com as diferencas
  Diferenca <- amostra1 - amostra2;
  
  # Media e desvio
  Media  <- mean(Diferenca);
  Desvio <- sd(Diferenca);
  
  # Calculando a probabilidade (1-alpha)%
  Prob   <- (1 - alpha/2);
  
  # Calculando a amplitude do intervalo
  H      <- qt(p=Prob, df=(numObs - 1)) * Desvio / sqrt(numObs);
  
  cat('H = ', H, '\n');
  
  hStar <- precisao;
  
  if(H > precisao)
  {
    cat('Precisão inferior a 2 horas, estimando o número n* de rodadas para alcançar a precisão desejada\n');
    
    hStar <- precisao;
    
    nStar <- ceiling(length(Diferenca)*((H/hStar)^2));
    
    cat('Rodadas n*: ', nStar, '\n');
  }

  # Calcula limites do intervalo
  Inferior <- Media - H;
  Superior <- Media + H;
  
  cat(nome, ': [', Inferior, ';', Superior, ']\n');
  
  # Classifica de acordo com os limites do intervalo
  Classificacao <- NA;
  
  # Nada pode ser concluído
  if(Inferior < 0 && Superior > 0)
  {
    Classificacao <- 0;
  }
  else
  {
    # A média da Alternativa 2 é maior do que a média da Alternativa 1.
    if(Inferior < 0 && Superior < 0)
    {
      Classificacao <- -1;
    }
    # A média da Alternativa 1 é maior do que a média da Alternativa 2.
    else
    {
      Classificacao <- +1;
    }
  }
  
  # Retorna a classificacao
  return(Classificacao)
}

#
# Faz analise sobre o desempenho de duas amostras
#
Analisa2Amostras <- function(nome, amostra1, amostra2,precisao)
{
  # Calcula as estatisticas das amostras informadas
  Media1   <- mean(amostra1);
  Media2   <- mean(amostra2);
  Desvio1  <- sd(amostra1);
  Desvio2  <- sd(amostra2);
  Min1     <- min(amostra1);
  Min2     <- min(amostra2);
  Max1     <- max(amostra1);
  Max2     <- max(amostra2);
  Mediana1 <- quantile(amostra1, 0.5);
  Mediana2 <- quantile(amostra1, 0.5);
  
  cat('Analisando Variável ', nome, '\n\n');
  cat('Medida:\tAmostra1\tAmostra2\n');
  cat('Minimo:\t',Min1,'\t',Min2,'\n');
  cat('Media:\t',Media1,'\t',Media2,'\n');
  cat('Mediana:\t',Mediana1,'\t',Mediana2,'\n');
  cat('Desvio:\t',Desvio1,'\t',Desvio2,'\n');
  cat('Maximo:\t',Max1,'\t',Max2,'\n');
  
  Resultado <- IntervaloConfianca2AmostasNIguais(nome, amostra1, amostra2, 0.05, precisao);
  
  # Imprime resultado textualmente
  if(Resultado == 0)
  {
    cat('Nao é possivel diferenciar as amostras fornecidas\n');
  }
  if(Resultado == -1)
  {
    cat('A amostra1 tem media < amostra2\n');
  }
  if(Resultado == +1)
  {
    cat('A amostra1 tem media > amostra2\n');
  }
}

# pegar argumentos da linha de comando
args <- commandArgs(TRUE);

inputFile1 <- args[1];
inputFile2 <- args[2];
varName <- args[3];
precisao <- as.numeric(args[4]);

dataset1 <- read.table(file = inputFile1, header = T, sep = '\t');
dataset2 <- read.table(file = inputFile2, header = T, sep = '\t');

names(dataset1);
names(dataset2);

sample1 <- dataset1[[varName]];
sample2 <- dataset2[[varName]];

Analisa2Amostras(varName, sample1, sample2, precisao);
