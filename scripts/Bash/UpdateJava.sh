#!/bin/bash

# Fonte: http://www.edivaldobrito.com.br/como-instalar-o-oracle-java/

# Remove os binários do OpenJDK
sudo apt-get purge openjdk*

# Repositório com os binários mais atuais do Java
sudo add-apt-repository ppa:webupd8team/java

# Atualiza o índice de pacotes
sudo apt-get update

# Instala o JDK (Já contem o JRE)
# Para versões mais antigas, pode-se substituir o 8 por 6 ou 7
sudo apt-get install oracle-java8-installer

# Pacote para definir o Java proprietário como padrão
# Para versões mais antigas, pode-se substituir o 8 por 6 ou 7
sudo apt-get install oracle-java8-set-default