/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import ga.MainGA;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import pso.MainPSO;
import sgo.MainSGO;
import utils.Logger;

/**
 *
 * @author meimarcel
 */
public class Main {
    
    private static boolean plotGraph = false;
    private static boolean saveLog = false;
    private static boolean error = false;
    private static long seed;
    private static Logger LOGGER = new Logger();
    
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        
        seed = (new Random()).nextLong();
        StringBuilder log = new StringBuilder();
        log.append(LOGGER.header());
        
        if(args.length > 0) {
            for(String var : args) {
                setVariables(var);
            }
        }
        
        if(error) System.exit(1);
        Random random = new Random();
        random.setSeed(seed);
        
        String algorithm;
        List<String> algorithms = Arrays.asList("0","1","2", "3");
        while(true) {
            System.out.println("\nEscolha um algoritmo");
            System.out.println("1 - GENETIC ALGORITHM (GA)");
            System.out.println("2 - PARTICLE SWARM OPTIMIZATION (PSO)");
            System.out.println("3 - SOCCER GAME OPTIMIZATION (SGO)");
            System.out.println("0 - CANCELAR");
            System.out.print("[ENTRADA]: ");
            algorithm = in.nextLine();
            algorithm = algorithm.trim();
            if(!algorithms.contains(algorithm)) {
                LOGGER.error("Opção inválida\n");
                System.out.print("[ENTRADA]: ");
            } else {
                break;
            }
        }
        
        switch(algorithm) {
            case "1":
                MainGA.run(plotGraph, saveLog, seed, algorithm);
                break;
            case "2":
                MainPSO.run(plotGraph, saveLog, seed, algorithm);
                break;
            case "3":
                MainSGO.run(plotGraph, saveLog, seed, algorithm);
        }
    }
    
    public static void setVariables(String var) {
        String values[] = var.split("=");
        
        if(values.length != 2) {
            LOGGER.error("Bad Format in '"+var+"'\n");
            error = true;
            return;
        }
        
        if(values[0].equals("-PlotGraph")) {
            if(values[1].toLowerCase().equals("true") || values[1].equals("1") || 
                    values[1].toLowerCase().equals("false") || values[1].equals("0")) {
                plotGraph = (values[1].toLowerCase().equals("true") || values[1].equals("1"));
                LOGGER.info("Plot Graph Activated\n");
            } else {
                LOGGER.error("Invalid value in '"+var+"'\n");
                error = true;
            }
            
        } else if(values[0].equals("-SaveLog")) {
            if(values[1].toLowerCase().equals("true") || values[1].equals("1") || 
                    values[1].toLowerCase().equals("false") || values[1].equals("0")) {
                saveLog = (values[1].toLowerCase().equals("true") || values[1].equals("1")); 
                LOGGER.info("Logs will be saved\n");
            } else {
                LOGGER.error("Invalid value in '"+var+"'\n");
                error = true;
            }
            
        } else if(values[0].equals("-Seed")) {
            try {
                seed = Long.parseLong(values[1]);
                LOGGER.info("Seed setted as "+seed+"\n");
            } catch(NumberFormatException e) {
                LOGGER.error("Invalid value in '"+var+"'\n");
                error = true;
            }
        } else {
            LOGGER.error("Invalid argument '"+var+"'\n");
            error = true;
        }
    }
    
}
