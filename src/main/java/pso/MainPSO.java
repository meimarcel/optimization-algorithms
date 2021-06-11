/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pso;

import utils.Function;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import utils.FileManager;
import utils.Logger;

/**
 *
 * @author meimarcel
 */
public class MainPSO {
    
    private static Logger LOGGER = new Logger();
        
    
    public static void run(boolean plotGraph, boolean saveLog, long seedDefined, String header) {
        StringBuilder log = new StringBuilder();
        log.append(header);
        log.append("\n");
        log.append(LOGGER.headerPSO());
        
        Random random = new Random();
        random.setSeed(seedDefined);
        
        
        int neighborhoodSize = -1;
        String psoType = getPSOType();
        Function function = getFunction();
        int particleNumber = getInt("Número de partículas[1 - 1000]", 1, 1000);
        int epochs = getInt("Número máximo de iterações[1 - 100.000]", 1, 100_000);

        if(psoType.equals("2")) 
            neighborhoodSize = getInt("Tamanho da vizinhança[1 - "+particleNumber+"]", 1, particleNumber);

        double beginRange = getDoubleWithDefault("Limite inferior (Deixe em branco para manter o valor padrão "+PSO.DEFAULT_BEGIN_RANGE+")", -100_000, 100_000);
        double endRange = getDoubleWithDefault("Limite superior (Deixe em branco para manter o valor padrão "+PSO.DEFAULT_END_RANGE+")", -100_000, 100_000);
        double inertiaWeight = getDoubleWithDefault("Peso de inécia (Deixe em branco para manter o valor padrão "+PSO.DEFAULT_INERTIA_WEIGHT+")", 0, 100_000);
        double cognitiveWeight = getDoubleWithDefault("Peso cognitivo (Deixe em branco para manter o valor padrão "+PSO.DEFAULT_COGNITIVE_WEIGHT+")", 0, 100_000);
        double socialWeight = getDoubleWithDefault("Peso social (Deixe em branco para manter o valor padrão "+PSO.DEFAULT_SOCIAL_WEIGHT+")", 0, 100_000);

        if(beginRange == -1) 
            beginRange = PSO.DEFAULT_BEGIN_RANGE;

        if(endRange == -1) 
            endRange = PSO.DEFAULT_END_RANGE;

        if(inertiaWeight == -1) 
            inertiaWeight = PSO.DEFAULT_INERTIA_WEIGHT;

        if(cognitiveWeight == -1) 
            cognitiveWeight = PSO.DEFAULT_COGNITIVE_WEIGHT;

        if(socialWeight == -1) 
            socialWeight = PSO.DEFAULT_SOCIAL_WEIGHT;

        PSO.StopConditionType stopCondition = getStopCondition();
        double conditionError = 0.0001;
        double conditionTarget = 0;
        int conditionWindow = 20;

        PSO pso = new PSO(particleNumber, epochs, inertiaWeight, cognitiveWeight, socialWeight, function, beginRange, endRange);
        pso.setStopConditionType(stopCondition);
        pso.setRandom(random);

        if(stopCondition == PSO.StopConditionType.ACCEPTABLE_ERROR) {
            conditionTarget = getDouble("Alvo", -10e9, 10e9);
            conditionError = getDouble("Error", -10e9, 10e9);

            pso.setConditionTarget(conditionTarget);
            pso.setConditionError(conditionError);

        } else if(stopCondition == PSO.StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT || stopCondition == PSO.StopConditionType.FUNCTION_SLOPE) {
            conditionWindow = getInt("Janela de interações", Integer.MIN_VALUE, Integer.MAX_VALUE);
            conditionError = getDouble("Error", -10e9, 10e9);

            pso.setConditionWindow(conditionWindow);
            pso.setConditionError(conditionError);

        } else if(stopCondition == PSO.StopConditionType.NORMALIZED_RADIUS) {
            conditionError = getDouble("Error", -10e9, 10e9);
            pso.setConditionError(conditionError);
        }

        System.out.println("");
        log.append("\n");
        log.append(LOGGER.info("PSO Type: "+((psoType.equals("1")) ? "Gbest PSO" : "Lbest PSO")+"\n"));
        log.append(LOGGER.info("Function: "+function.getFunctionType()+"\n"));
        log.append(LOGGER.info("Number of particles: "+particleNumber+"\n"));
        log.append(LOGGER.info("Iteration Limit: "+epochs+"\n"));

        if(psoType.equals("2")) 
            log.append(LOGGER.info("Neighborhood Size: "+neighborhoodSize+"\n"));

        log.append(LOGGER.info("Begin Range: "+beginRange+"\n"));
        log.append(LOGGER.info("End Range: "+endRange+"\n"));
        log.append(LOGGER.info("Inertia Weight: "+inertiaWeight+"\n"));
        log.append(LOGGER.info("Cognitive Weight: "+cognitiveWeight+"\n"));
        log.append(LOGGER.info("Social Weight: "+socialWeight+"\n"));
        log.append(LOGGER.info("Stop Condition: "+stopCondition+"\n"));

        if(stopCondition == PSO.StopConditionType.ACCEPTABLE_ERROR) {
            log.append(LOGGER.info("Alvo: "+conditionTarget+"\n"));
            log.append(LOGGER.info("Erro: "+conditionError+"\n"));

        } else if(stopCondition == PSO.StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT || stopCondition == PSO.StopConditionType.FUNCTION_SLOPE) {
            log.append(LOGGER.info("Janela de Iterações: "+conditionWindow+"\n"));
            log.append(LOGGER.info("Erro: "+conditionError+"\n"));

        } else if(stopCondition == PSO.StopConditionType.NORMALIZED_RADIUS) {
            log.append(LOGGER.info("Erro: "+conditionError+"\n"));
        }
        System.out.println("");

        if(plotGraph) 
            pso.setPlotGraph(plotGraph);

        if(psoType.equals("1")) {
            log.append(pso.runGBestPSO());
        } else {
            pso.setNeighborhoodSize(neighborhoodSize);
            log.append(pso.runLBestPSO());
        }
            
        
        
        if(saveLog) {
            String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
            FileManager.Write(FileSystems.getDefault().getPath("").toAbsolutePath()+"/data/"+timeStamp, "log.txt", log.toString());
        }
        
    }
    
    
    public static String getPSOType() {
        String psoType = "-1";
        Scanner in = new Scanner(System.in);
        System.out.println("\nEscolha um o algoritmo PSO");
        System.out.println("1 - Global Best PSO (gbest PSO)");
        System.out.println("2 - Local Best PSO (lbest PSO)");
        System.out.print("[ENTRADA]: ");
        
        while(true) {
            psoType = in.nextLine();
            psoType = psoType.trim();
            if(!psoType.equals("1") && !psoType.equals("2")) {
                LOGGER.error("Opção inválida\n");
                System.out.print("[ENTRADA]: ");
            } else {
                break;
            }
        }
        
        return psoType;
    }
    
    public static Function getFunction() {
        String option;
        Scanner in = new Scanner(System.in);
        System.out.println("\nEscolha uma função");
        System.out.println("1 - Function A f(x1,x2) = (x1 - 2)⁴ + (x1 - 2 * x2)²");
        System.out.println("2 - Function B f(x1,x2) = (x1 - 1)² - 10 * cos(2 * π * x1) + x2² - 10 * cos(2 * π * x2)");
        System.out.print("[ENTRADA]: ");
        
        while(true) {
            option = in.nextLine();
            option = option.trim();
            if(!option.equals("1") && !option.equals("2")) {
                LOGGER.error("Opção inválida\n");
                System.out.print("[ENTRADA]: ");
            } else {
                break;
            }
        }
        
        if(option.equals("1")) {
            return new Function(Function.FunctionType.FUNCTION_A);
        } else {
            return new Function(Function.FunctionType.FUNCTION_B);
        }
    }
    
    public static PSO.StopConditionType getStopCondition() {
        String option;
        Scanner in = new Scanner(System.in);
        System.out.println("\nEscolha a condição de parada");
        System.out.println("1 - Somente número de épocas");
        System.out.println("2 - Parar em um erro aceitável");
        System.out.println("3 - Parar quando não houver melhorias");
        System.out.println("4 - Parar quando o raio do enxame for menor que um erro");
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
                return PSO.StopConditionType.ONLY_EPOCH;
            case "2":
                return PSO.StopConditionType.ACCEPTABLE_ERROR;
            case "3":
                return PSO.StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT;
            case "4":
                return PSO.StopConditionType.NORMALIZED_RADIUS;
            case "5":
                return PSO.StopConditionType.FUNCTION_SLOPE;
            default:
                return PSO.StopConditionType.ONLY_EPOCH;
        }
    }
    
    public static int getInt(String message, int start, int end) {
        int value = 1;
        Scanner in = new Scanner(System.in);
        System.out.print("\n"+message+": ");
        
        while(true) {
            String input = in.nextLine();
            input = input.trim();
            try{
                value = Integer.parseInt(input);
            } catch(NumberFormatException e) {
                LOGGER.error("Entrada inválida\n");
                System.out.print(message+": ");
                continue;
            }
            
            if(value < start || value > end) {
                LOGGER.error("Entrada inválida\n");
                System.out.print(message+": ");
            } else {
                break;
            }
        }
        
        return value;
    }
    
    public static double getDoubleWithDefault(String message, double start, double end) {
        double value = -1;
        Scanner in = new Scanner(System.in);
        System.out.print("\n"+message+": ");
        
        while(true) {
            String input = in.nextLine();
            input = input.trim();
            if(input.trim().equals("")) {
                break;
            }
            
            try{
                value = Double.parseDouble(input);
            } catch(NumberFormatException e) {
                LOGGER.error("Entrada inválida\n");
                System.out.print(message+": ");
                continue;
            }
            
            if(value < start || value > end) {
                LOGGER.error("Entrada inválida\n");
                System.out.print(message+": ");
            } else {
                break;
            }

        }
        return value;
    }
    
    public static double getDouble(String message, double start, double end) {
        double value = -1;
        Scanner in = new Scanner(System.in);
        System.out.print("\n"+message+": ");
        
        while(true) {
            String input = in.nextLine();
            input = input.trim();
            try{
                value = Double.parseDouble(input);
            } catch(NumberFormatException e) {
                LOGGER.error("Entrada inválida\n");
                System.out.print(message+": ");
                continue;
            }
            
            if(value < start || value > end) {
                LOGGER.error("Entrada inválida\n");
                System.out.print(message+": ");
            } else {
                break;
            }

        }
        return value;
    }
    
}
