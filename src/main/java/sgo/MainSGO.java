/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sgo;

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
public class MainSGO {
    
    private static Logger LOGGER = new Logger();
        
    
    public static void run(boolean plotGraph, boolean saveLog, long seedDefined, String header) {
        StringBuilder log = new StringBuilder();
        log.append(header);
        log.append("\n");
        log.append(LOGGER.headerSGO());
        
        Random random = new Random();
        random.setSeed(seedDefined);
        
        
        Function function = getFunction();
        int playerNumber = getInt("Número de jogadores[1 - 1000]", 1, 1000);
        int substituteNumber = getInt("Número de substitutos[0 - "+playerNumber+"]", 1, playerNumber);
        int kicksLimit = getInt("Número máximo de chutes(iterações)[1 - 100.000]", 1, 100_000);

        double beginRange = getDoubleWithDefault("Limite inferior (Deixe em branco para manter o valor padrão "+SGO.DEFAULT_BEGIN_RANGE+")", -100_000, 100_000, SGO.DEFAULT_BEGIN_RANGE);
        double endRange = getDoubleWithDefault("Limite superior (Deixe em branco para manter o valor padrão "+SGO.DEFAULT_END_RANGE+")", -100_000, 100_000, SGO.DEFAULT_END_RANGE);
        double inertiaWeight = getDoubleWithDefault("Peso de inécia (Deixe em branco para manter o valor padrão "+SGO.DEFAULT_INERTIA_WEIGHT+")", 0, 100_000, SGO.DEFAULT_INERTIA_WEIGHT);
        double cognitiveWeight = getDoubleWithDefault("Peso cognitivo (Deixe em branco para manter o valor padrão "+SGO.DEFAULT_COGNITIVE_WEIGHT+")", 0, 100_000, SGO.DEFAULT_COGNITIVE_WEIGHT);
        double socialWeight = getDoubleWithDefault("Peso social (Deixe em branco para manter o valor padrão "+SGO.DEFAULT_SOCIAL_WEIGHT+")", 0, 100_000, SGO.DEFAULT_SOCIAL_WEIGHT);
        double moveOffProbability = getDoubleWithDefault("Probabilidade de Move Off(Deixe em branco para manter o valor padrão "+SGO.DEFAULT_MOVE_OFF_PROBABILITY+")", 0, 1, SGO.DEFAULT_MOVE_OFF_PROBABILITY);
        double moveForwardAfterMoveOffProbability = getDoubleWithDefault("Probabilidade de Move Forward após Move Off (Deixe em branco para manter o valor padrão "+SGO.DEFAULT_MOVE_FORWARD_AFTER_MOVE_OFF+")", 0, 1, SGO.DEFAULT_MOVE_FORWARD_AFTER_MOVE_OFF);
        double substitutionProbability = getDoubleWithDefault("Probabilidade de substituição (Deixe em branco para manter o valor padrão "+SGO.DEFAULT_SUBSTITUTION_PROBABILITY+")", 0, 1, SGO.DEFAULT_SUBSTITUTION_PROBABILITY);
        double nonUniformityDegree = getDoubleWithDefault("Grau de não uniformidade (Deixe em branco para manter o valor padrão "+SGO.DEFAULT_NON_UNIFORMITY_DEGREE+")", 0, 100_000, SGO.DEFAULT_NON_UNIFORMITY_DEGREE);

        SGO.StopConditionType stopCondition = getStopCondition();
        double conditionError = 0.0001;
        double conditionTarget = 0;
        int conditionWindow = 20;

        SGO sgo = new SGO(playerNumber, substituteNumber, kicksLimit, inertiaWeight, 
            cognitiveWeight, socialWeight, function,
            beginRange, endRange, moveOffProbability, moveForwardAfterMoveOffProbability,
            substitutionProbability, nonUniformityDegree);
        
        sgo.setStopConditionType(stopCondition);
        sgo.setRandom(random);

        if(stopCondition == SGO.StopConditionType.ACCEPTABLE_ERROR) {
            conditionTarget = getDouble("Alvo", -10e9, 10e9);
            conditionError = getDouble("Error", -10e9, 10e9);

            sgo.setConditionTarget(conditionTarget);
            sgo.setConditionError(conditionError);

        } else if(stopCondition == SGO.StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT || stopCondition == SGO.StopConditionType.FUNCTION_SLOPE) {
            conditionWindow = getInt("Janela de interações", Integer.MIN_VALUE, Integer.MAX_VALUE);
            conditionError = getDouble("Error", -10e9, 10e9);

            sgo.setConditionWindow(conditionWindow);
            sgo.setConditionError(conditionError);

        }

        System.out.println("");
        log.append("\n");
        log.append(LOGGER.info("Function: "+function.getFunctionType()+"\n"));
        log.append(LOGGER.info("Number of players: "+playerNumber+"\n"));
        log.append(LOGGER.info("Number of substitutes: "+substituteNumber+"\n"));
        log.append(LOGGER.info("Kicks(iteration) Limit: "+kicksLimit+"\n"));
        log.append(LOGGER.info("Begin Range: "+beginRange+"\n"));
        log.append(LOGGER.info("End Range: "+endRange+"\n"));
        log.append(LOGGER.info("Inertia Weight: "+inertiaWeight+"\n"));
        log.append(LOGGER.info("Cognitive Weight: "+cognitiveWeight+"\n"));
        log.append(LOGGER.info("Social Weight: "+socialWeight+"\n"));
        
        log.append(LOGGER.info("Move Off Probability: "+moveOffProbability+"\n"));
        log.append(LOGGER.info("Move Forward after Move Off Probability: "+moveForwardAfterMoveOffProbability+"\n"));
        log.append(LOGGER.info("Substitution Probability: "+substitutionProbability+"\n"));
        log.append(LOGGER.info("Non-Uniformity Degree: "+nonUniformityDegree+"\n"));
        
        log.append(LOGGER.info("Stop Condition: "+stopCondition+"\n"));

        if(stopCondition == SGO.StopConditionType.ACCEPTABLE_ERROR) {
            log.append(LOGGER.info("Target: "+conditionTarget+"\n"));
            log.append(LOGGER.info("Error: "+conditionError+"\n"));

        } else if(stopCondition == SGO.StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT || stopCondition == SGO.StopConditionType.FUNCTION_SLOPE) {
            log.append(LOGGER.info("Iteration Window: "+conditionWindow+"\n"));
            log.append(LOGGER.info("Error: "+conditionError+"\n"));

        }
        System.out.println("");

        if(plotGraph) 
            sgo.setPlotGraph(plotGraph);

        log.append(sgo.runSGO());
        
        if(saveLog) {
            String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
            FileManager.Write(FileSystems.getDefault().getPath("").toAbsolutePath()+"/data/"+timeStamp, "log.txt", log.toString());
        }
        
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
    
    public static SGO.StopConditionType getStopCondition() {
        String option;
        Scanner in = new Scanner(System.in);
        System.out.println("\nEscolha a condição de parada");
        System.out.println("1 - Somente número de iterações");
        System.out.println("2 - Parar em um erro aceitável");
        System.out.println("3 - Parar quando não houver melhorias");
        System.out.println("4 - Parar quando a inclinação da função objetiva for menor que um erro");
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
                return SGO.StopConditionType.ONLY_ITERATION;
            case "2":
                return SGO.StopConditionType.ACCEPTABLE_ERROR;
            case "3":
                return SGO.StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT;
            case "4":
                return SGO.StopConditionType.FUNCTION_SLOPE;
            default:
                return SGO.StopConditionType.ONLY_ITERATION;
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
    
    public static double getDoubleWithDefault(String message, double start, double end, double defaultValue) {
        double value = defaultValue;
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
