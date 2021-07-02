/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga;


import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import utils.FileManager;
import utils.Function;
import utils.Logger;
import utils.Utils;

/**
 *
 * @author meimarcel
 */
public class MainGA {
    private static Logger LOGGER = new Logger();
    
    public static void run(boolean plotGraph, boolean saveLog, long seedDefined, String header) {
        StringBuilder log = new StringBuilder();
        log.append(header);
        log.append("\n");
        log.append(LOGGER.headerGA());
        
        Random random = new Random();
        random.setSeed(seedDefined);
        
        int numberOfPopulation = Utils.getInt("Tamanho da poulação[1 - 1000]", 1, 1000);
        int iterationLimit = Utils.getInt("Número máximo de iterações[1 - 100.000]", 1, 100_000);
        Function function = Utils.getFunction();

        double beginRange = Utils.getDoubleWithDefault("Limite inferior (Deixe em branco para manter o valor padrão "+GA.DEFAULT_BEGIN_RANGE+")", -100_000, 100_000, GA.DEFAULT_BEGIN_RANGE);
        double endRange = Utils.getDoubleWithDefault("Limite superior (Deixe em branco para manter o valor padrão "+GA.DEFAULT_END_RANGE+")", -100_000, 100_000, GA.DEFAULT_END_RANGE);
        double crossoverProbability = Utils.getDoubleWithDefault("Taxa de crossover[0.0 - 1.0] (Deixe em branco para manter o valor padrão "+GA.DEFAULT_CROSSOVER_PROBABILITY+")", 0, 1, GA.DEFAULT_CROSSOVER_PROBABILITY);
        double mutationProbability = Utils.getDoubleWithDefault("Taxa de mutação[0.0 - 1.0] (Deixe em branco para manter o valor padrão "+GA.DEFAULT_MUTATION_PROBABILITY+")", 0, 1, GA.DEFAULT_MUTATION_PROBABILITY);
        int elitism = Utils.getInt("Elitismo[0 - "+numberOfPopulation+"] (Número de indivíduos a serem passados para pŕoxima geração)", 0, numberOfPopulation);
        
        GA.CrossoverType crossoverType = getCrossoverType();
        GA.SelectionType selectionType = getSelectionType();

        GA.StopConditionType stopCondition = getStopCondition();
        double conditionError = 0.0001;
        double conditionTarget = 0;
        int conditionWindow = 20;

        GA ga = new GA(numberOfPopulation, iterationLimit, crossoverProbability, mutationProbability, beginRange, endRange, elitism, crossoverType, function, selectionType);
        ga.setStopConditionType(stopCondition);
        ga.setRandom(random);

        if(stopCondition == GA.StopConditionType.ACCEPTABLE_ERROR) {
            conditionTarget = Utils.getDouble("Alvo", -10e9, 10e9);
            conditionError = Utils.getDouble("Error", -10e9, 10e9);

            ga.setConditionTarget(conditionTarget);
            ga.setConditionError(conditionError);

        } else if(stopCondition == GA.StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT || stopCondition == GA.StopConditionType.FUNCTION_SLOPE || stopCondition == GA.StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT_POPULATION) {
            conditionWindow = Utils.getInt("Janela de interações", Integer.MIN_VALUE, Integer.MAX_VALUE);
            conditionError = Utils.getDouble("Error", -10e9, 10e9);

            ga.setConditionWindow(conditionWindow);
            ga.setConditionError(conditionError);

        }

        System.out.println("");
        log.append("\n");
        log.append(LOGGER.info("Function: "+function.getStringFunction()+"\n"));
        log.append(LOGGER.info("Number of population: "+numberOfPopulation+"\n"));
        log.append(LOGGER.info("Iteration Limit: "+iterationLimit+"\n"));
        log.append(LOGGER.info("Begin Range: "+beginRange+"\n"));
        log.append(LOGGER.info("End Range: "+endRange+"\n"));
        log.append(LOGGER.info("Crossover Probability: "+crossoverProbability+"\n"));
        log.append(LOGGER.info("Mutation Probability: "+mutationProbability+"\n"));
        log.append(LOGGER.info("Elitism: "+elitism+"\n"));
        log.append(LOGGER.info("Stop Condition: "+stopCondition+"\n"));
        log.append(LOGGER.info("Crossover Type: "+crossoverType+"\n"));
        log.append(LOGGER.info("Selectio Type: "+selectionType+"\n"));

        if(stopCondition == GA.StopConditionType.ACCEPTABLE_ERROR) {
            log.append(LOGGER.info("Target: "+conditionTarget+"\n"));
            log.append(LOGGER.info("Error: "+conditionError+"\n"));

        } else if(stopCondition == GA.StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT || stopCondition == GA.StopConditionType.FUNCTION_SLOPE || stopCondition == GA.StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT_POPULATION) {
            log.append(LOGGER.info("Iteration Window: "+conditionWindow+"\n"));
            log.append(LOGGER.info("Error: "+conditionError+"\n"));

        }
        System.out.println("");

        if(plotGraph) 
            ga.setPlotGraph(plotGraph);
        
        log.append(ga.runGA());
        
        if(saveLog) {
            String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
            FileManager.Write(FileSystems.getDefault().getPath("").toAbsolutePath()+"/data/"+timeStamp, "log.txt", log.toString());
        }
    }
    
    public static GA.StopConditionType getStopCondition() {
        String option;
        Scanner in = new Scanner(System.in);
        System.out.println("\nEscolha a condição de parada");
        System.out.println("1 - Somente número de iterações");
        System.out.println("2 - Parar em um erro aceitável");
        System.out.println("3 - Parar quando não houver melhorias");
        System.out.println("4 - Parar quando não houver melhorias na população");
        System.out.println("5 - Parar quando a inclinação da função objetiva for menor que um erro");
        System.out.print("[ENTRADA]: ");
        
        List<String> options = Arrays.asList("1","2","3","4","5");
        while(true) {
            option = in.nextLine();
            option = option.trim();
            if(!options.contains(option)) {
                LOGGER.error("Opção inválida\n");
                System.out.print("[ENTRADA]: ");
            } else {
                break;
            }
        }
        switch(option) {
            case "1":
                return GA.StopConditionType.ONLY_ITERATION;
            case "2":
                return GA.StopConditionType.ACCEPTABLE_ERROR;
            case "3":
                return GA.StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT;
            case "4":
                return GA.StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT_POPULATION;
            case "5":
                return GA.StopConditionType.FUNCTION_SLOPE;
            default:
                return GA.StopConditionType.ONLY_ITERATION;
        }
    }
    
    public static GA.CrossoverType getCrossoverType() {
        String option;
        Scanner in = new Scanner(System.in);
        System.out.println("\nEscolha o tipo de crossover");
        System.out.println("1 - Média aritmética");
        System.out.println("2 - Média ponderada");
        System.out.println("3 - Um ponto de corte");
        System.out.println("4 - Dois pontos de corte");
        System.out.print("[ENTRADA]: ");
        
        List<String> options = Arrays.asList("1","2","3","4");
        while(true) {
            option = in.nextLine();
            option = option.trim();
            if(!options.contains(option)) {
                LOGGER.error("Opção inválida\n");
                System.out.print("[ENTRADA]: ");
            } else {
                break;
            }
        }
        switch(option) {
            case "1":
                return GA.CrossoverType.ARITHMETIC_MEAN;
            case "2":
                return GA.CrossoverType.WEIGHTED_MEAN;
            case "3":
                return GA.CrossoverType.ONE_POINT;
            case "4":
                return GA.CrossoverType.TWO_POINT;
            default:
                return GA.CrossoverType.ONE_POINT;
        }
    }
    
    public static GA.SelectionType getSelectionType() {
        String option;
        Scanner in = new Scanner(System.in);
        System.out.println("\nEscolha o tipo de seleção");
        System.out.println("1 - Método da roleta");
        System.out.println("2 - Método do torneio");
        System.out.print("[ENTRADA]: ");
        
        List<String> options = Arrays.asList("1","2");
        while(true) {
            option = in.nextLine();
            option = option.trim();
            if(!options.contains(option)) {
                LOGGER.error("Opção inválida\n");
                System.out.print("[ENTRADA]: ");
            } else {
                break;
            }
        }
        switch(option) {
            case "1":
                return GA.SelectionType.ROULETTE_WHEEL;
            case "2":
                return GA.SelectionType.TOURNAMENT;
            default:
                return GA.SelectionType.TOURNAMENT;
        }
    }  
}
