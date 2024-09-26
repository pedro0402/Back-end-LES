# Back-end-LES
### Objetivo do projeto

O projeto consiste em uma aplicação que consome um arquivo PDF, realiza sua tradução e resumo, e disponibiliza o resultado para download. Para alcançar isso, o sistema se comunica com duas APIs externas: uma para realizar a tradução do conteúdo e outra para gerar o resumo (opcional) . O backend da aplicação é desenvolvido utilizando SpringBoot, oferecendo uma interface REST para que o usuário possa enviar o PDF e baixar o resultado.

### Fluxo da aplicação:
- O usuário envia um arquivo PDF para o sistema via uma requisição HTTP
- O sistema processa o arquivo, extrai o texto e o envia para duas APIs:
A primeira API realiza a tradução do texto para o idioma escolhido.
A segunda API gera um resumo a partir do texto traduzido.
- O texto traduzido e resumido é reformatado em um novo arquivo PDF.
- O novo PDF é devolvido ao usuário para download.

### Endpoint das APIs
- 1: API para upload do PDF: O usuário pode fazer upload de um arquivo PDF através de um endpoint: @PostMapping("/uploadFile")
- 2: API para download do PDF: Após o processamento, o arquivo pode ser baixado em:  @GetMapping("/downloadFile/{fileName:.+}")

### Stack e o porquê utilizamos
SpringBoot: Escolhemos o SpringBoot devido à flexibilidade, suporte a serviços RESTful e integração com aplicações utilizando o React.
API de Tradução: Provê serviços de tradução automática do conteúdo para a linguagem escolhida.
API de Resumo: Faz o resumo do conteúdo traduzido.

### Acesso e execução do código
Pré-requisitos
- Java 17 ou superior instalado.
- Maven para gerenciar as dependências.
- SpringBoot instalado e configurado.
