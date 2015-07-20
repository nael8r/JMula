# pegar argumentos da linha de comando
args <- commandArgs(TRUE);

inputFile <- args[1];
varName <- args[2];
precisao <- as.numeric(args[3]);

dataset <- read.table(file = inputFile, header = T, sep = '\t');

names(dataset);

sample <- dataset[[varName]];

cat('\nTamanho da Amostra: ',length(sample),'\n');

summary(sample);

cat('\nsd: ',sd(sample),'\n');