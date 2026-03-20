# Ping Monitor (Java)

Sistema de monitoramento de conectividade desenvolvido em Java, com foco em rastreabilidade de falhas e práticas de observabilidade (SRE).

## Funcionalidades

- Monitoramento contínuo de múltiplos hosts
- Detecção de queda de conexão (DOWN)
- Detecção de recuperação (UP)
- Cálculo de tempo de indisponibilidade (downtime)
- Sistema de retry para evitar falso positivo
- Logs estruturados com timestamp

---

## Conceitos aplicados

- Monitoramento de serviços
- Observabilidade (SRE)
- Detecção de eventos (state change)
- Resiliência (retry)
- Organização de código e separação de responsabilidades

---

## ⚙️ Tecnologias

- Java 21

---

## ▶️ Como executar

1. Compile os arquivos:

```bash
javac *.java