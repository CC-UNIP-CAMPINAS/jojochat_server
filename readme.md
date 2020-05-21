# JoJochat - Servidor
<p align="center">
</p>

----

## Descrição
> Software escrito em Java para conversão através de Berkeley sockets (PROJETO 5º SEMESTRE UNIP).
>Vertente servidor para tratamento de requisições dos clientes;


---

## Status

![](https://img.shields.io/badge/version-v2.0-green)
[![GitHub issues](https://img.shields.io/github/issues/CC-UNIP-CAMPINAS/jojochat_server/issues)](https://github.com/CC-UNIP-CAMPINAS/jojochat_server/issues)
[![GitHub forks](https://img.shields.io/github/forks/CC-UNIP-CAMPINAS/jojochat_server)](https://github.com/CC-UNIP-CAMPINAS/jojochat_server/network)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/d1b6d6eaa7b44ce7a5262eae72b422cd)](https://www.codacy.com/gh/CC-UNIP-CAMPINAS/jojochat_server?utm_source=github.com&utm_medium=referral&utm_content=CC-UNIP-CAMPINAS/jojochat_server&utm_campaign=Badge_Grade_Dashboard)
---

## Dependências

 - [mariadb-connector-j](https://github.com/mariadb-corporation/mariadb-connector-j) >= v4.2
 - [Java SE](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) = v8u241

---
## Instalação
> <b>ATENÇÃO: A VERTENTE CLIENTE PODE SER ENCONTRADA [AQUI](https://github.com/CC-UNIP-CAMPINAS/jojochat_client/). APÓS A CONFIGURAÇÃO DO SERVIDOR, VERIFIQUE A VERSÃO PARA USO DOS USUÁRIOS.</b>

>O software foi desenvolvido com o propósito de ser um projeto acadêmico. Sua utilização é para fins educativos! Veja como implementar o cliente em seu computador: 

- 1º

    * Faça um clone do repositório ou baixe o código fonte  [AQUI](https://github.com/CC-UNIP-CAMPINAS/jojochat_server/archive/master.zip).

- 2º

    * Instale todas as depêndencias citadas e importe em seu projeto.

- 3º 

    * Criei duas pastas no mesmo endereço abaixo chamadas: <b>Arquivos</b> e <b>users_data</b>
    *Windows: C:\Users\USUARIO\Documents\JOJO_DATA*
    *Linux: /home/USUARIO/Documents/JOJO_DATA*
    *A primeira vai armazenar todos os arquivos enviados e a segunda fotos de perfil dos usuários.*

- 4º 
    * <b>Crie o arquivo de conexão com o banco de dados ( </b><u>db.properties</u><b> ) dentro da pasta acima</b>
    <br/><b>Exemplo de arquivo:</b>
    ```s
    user=USUARIO_DO_BANCO
    password=SENHA_DE_ACESSO
    dburl=jdbc:mariadb://ENDERECO_DO_BANCO:3306/NOME_DO_BANCO
    useSSL=false
    ```

- 5º
    * Dependendo do endereço e porta que o servidor vai ser configurado, lembre-se de  alterar essa informação no Main() deste projeto e criar as devidas regras de firewall;

- 6º 
    * Iniciar servidor em um terminal!
---

## Funcionamento

>Na atual versão do programa (v2.0) o design apresentava o seguinte visual:

[![server.gif](https://s7.gifyu.com/images/server.gif)](https://gifyu.com/image/ngWH)
----

## Meta

<center><b>-=Leonardo Petta do Nascimento=-</b></center></br> 

Site: [Meu Portfólio](https://leonardopn.github.io/)</br>
Facebook: [@leonardo.petta.nascimento](https://www.facebook.com/leonardo.petta.nascimento)
Email: leonardocps9@protonmail.com
Linkedin: [Leonardo Petta Do Nascimento](https://www.linkedin.com/in/leonardo-petta-do-nascimento-75674015b/)

<center><b>-=Lucas Limas=-</b></center></br> 

Facebook: [llimasf](https://www.facebook.com/llimasf)
Email: llimas.f@gmail.com

---

## Agradecimentos

1. [Maria DB Foundation;](https://mariadb.org/)
2. [UNIP](https://unip.br/)
---
