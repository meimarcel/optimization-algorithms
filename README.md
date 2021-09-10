# Optimization Algorithms

Os algoritmos foram escritos na linguagem de programação Java e foi utilizada a ferramenta Maven para buildar e gerenciar as dependências.

# Requisitos

- Java 8+
- Maven 3+

# Algoritmos

- Particle Swarm Optimization
- Genetic Algorithm

# Utilização

Para executar o algoritmo, baixe o projeto e execute o seguinte comando para fazer o build do pojeto:


```bash
mvn package
```
Depois de ter feito o build, vá até a pasta **target/OptimizationAlgorithms/** e execute o seguinte comando para executar o projeto:
```bash
java -jar OptimizationAlgorithms.jar
```
	
Alguns comando adicionais podem ser adicionados na execução.	

Para plotar os gráficos:
```bash
java -jar OptimizationAlgorithms.jar -PlotGraph=true
```
	
Para salvar os resultados. Os resultados serão salvos na pasta data/:
```bash
java -jar OptimizationAlgorithms.jar -SaveLog=true
```

Para definir um seed:
```bash
java -jar OptimizationAlgorithms.jar -Seed=<number>
```

Para definir um algoritmo:
```bash
java -jar OptimizationAlgorithms.jar -Algorithm=<algorithm>
```

Para definir um arquivo json para realizar um batch de testes:
```bash
java -jar OptimizationAlgorithms.jar -FilePath=<filePath>
```
Todos comandos podem ser utilizados juntos.
