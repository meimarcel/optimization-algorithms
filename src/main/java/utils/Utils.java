/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author meimarcel
 */
public class Utils {
    
    private static Logger LOGGER = new Logger();
    
    public static Function getFunction() {
        String option;
        Scanner in = new Scanner(System.in);
        System.out.println("\nEscolha uma função");
        System.out.println("1 - "+Function.FUNCTION_A_STRING);
        System.out.println("2 - "+Function.FUNCTION_B_STRING);
        System.out.println("3 - "+Function.FUNCTION_C_STRING);
        System.out.println("4 - "+Function.FUNCTION_D_STRING);
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
                return new Function(Function.FunctionType.FUNCTION_A);
            case "2":
                return new Function(Function.FunctionType.FUNCTION_B);
            case "3":
                return new Function(Function.FunctionType.FUNCTION_C);
            case "4":
                return new Function(Function.FunctionType.FUNCTION_D);
            default:
                return new Function(Function.FunctionType.FUNCTION_A);
        }
    }
    
    public static Function parseFunction(String function) {
        function = function.toUpperCase();
        switch(function) {
            case "FUNCTION_A":
                return new Function(Function.FunctionType.FUNCTION_A);
            case "FUNCTION_B":
                return new Function(Function.FunctionType.FUNCTION_B);
            case "FUNCTION_C":
                return new Function(Function.FunctionType.FUNCTION_C);
            case "FUNCTION_D":
                return new Function(Function.FunctionType.FUNCTION_D);
            default:
                throw new RuntimeException("Função não encontrada");
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
    
    public static int parseInt(String field, int value, int start, int end) {    
        if(value < start || value > end) {
            throw new RuntimeException(field+" não está no intervalo de "+start+" e "+end);
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
    
    public static double parseDouble(String field, double value, double start, double end) {
        if(value < start || value > end) {
            throw new RuntimeException(field+" não está no intervalo de "+start+" e "+end);
        }
        return value;
    }
    
}
