# 📢 Back-end LES

[![Java](https://img.shields.io/badge/Java-17+-blue?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.7+-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Python](https://img.shields.io/badge/Python-3.11+-3776AB?style=for-the-badge&logo=python&logoColor=white)](https://www.python.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)

---

## 📋 Descrição do Projeto

Aplicação backend que recebe um arquivo PDF, realiza sua **tradução** para outro idioma, gera um **resumo** (opcional) e disponibiliza o resultado para **download**.  
Desenvolvido com **Spring Boot** e **Python**, integrando APIs externas para tradução e sumarização de texto.

---

## 🚀 Funcionalidades

- ✅ Upload de arquivos PDF  
- ✅ Tradução automática para outro idioma  
- ✅ Geração de resumo do conteúdo traduzido  
- ✅ Disponibilização para download do novo arquivo  

---

## 🔗 Endpoints Disponíveis

| Método | Endpoint                    | Descrição                              |
|:------:|:----------------------------|:--------------------------------------:|
| POST   | `/uploadFile`               | Upload de um arquivo PDF para processar |
| GET    | `/downloadFile/{fileName}`  | Download do arquivo processado         |

---

## 🛠️ Tecnologias Utilizadas

- Java 17+  
- Spring Boot  
- Python 3.11+  
- Maven  
- APIs externas para tradução e resumo  
- Git / GitHub  

---

## ⚙️ Como Rodar o Projeto Localmente

### Pré-requisitos

- Java 17 ou superior  
- Maven instalado  
- Python 3.11 ou superior instalado  
- Configurar o acesso às APIs externas no projeto  

### Passos

```bash
# Clone o repositório
git clone https://github.com/pedro0402/Back-end-LES.git

# Acesse o diretório
cd Back-end-LES

# Instale as dependências Java
mvn install

# Execute o backend Spring Boot
mvn spring-boot:run

# (Se houver scripts Python auxiliares, instale as dependências Python)
pip install -r requirements.txt
```

## 👤 Estrutura do Projeto

```
src
 └── main
      ├── java
      │    └── com.les.backend
      │         ├── controller
      │         ├── service
      │         └── config
      └── resources
           └── application.properties
python
 └── services
      ├── translator.py
      └── summarizer.py
```
---

## 🎯 Melhorias Futuras

- Implementar autenticação de usuários
- Histórico de arquivos traduzidos e resumidos
- Upload de múltiplos arquivos simultaneamente
- Deploy do projeto na nuvem (Render, Railway, AWS)
- Melhorias na interface para upload/download (opcional)

---

## 👨‍💼 Autor

| [![Pedro Henrique](https://avatars.githubusercontent.com/u/yourgithubid?s=100)](https://github.com/pedro0402) |
|:------------------------------------------------------------------------------------------------------------:|
| [Pedro Henrique](https://github.com/pedro0402) - Desenvolvedor Backend |

---

## 📬 Contato

[![LinkedIn](https://img.shields.io/badge/LinkedIn-blue?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/seu-linkedin)

---

> Projeto desenvolvido para fins de estudo e aprimoramento em backend Java/Spring Boot + Python.
