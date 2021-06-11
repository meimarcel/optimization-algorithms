/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author meimarcel
 */
public class Function {
    private FunctionType functionType;
    
    public Function(FunctionType functionType) {
        this.functionType = functionType;
    }
    
    public double fit(double values[]) {
        switch(this.functionType) {
            case FUNCTION_A:
                return this.functionA(values[0], values[1]);
            case FUNCTION_B:
                return this.functionB(values[0], values[1]);
            default:
                return this.functionA(values[0], values[1]);
        }
    }
    
    public int getNumberOfVariables() {
        switch(this.functionType) {
            case FUNCTION_A:
                return this.FUNCTION_A_VARIABLES;
            case FUNCTION_B:
                return this.FUNCTION_B_VARIABLES;
            default:
                return this.FUNCTION_A_VARIABLES;
        }
    }
    
    public FunctionType getFunctionType() {
        return this.functionType;
    }
            
            
            
    
    private final int FUNCTION_A_VARIABLES = 2;
    private double functionA(double x1, double x2) {
        double penalty = 0.0;
        /*if(x1 < 1 || x1 > 3) {
            penalty += (x1*x1);
        }
        if(x2 < 0 || x2 > 2){
            penalty += (x2*x2);
        }*/
        double A = x1 - 2.0;
        double B = x1 - (2.0 * x2);
                
        return Math.pow(A, 4) + Math.pow(B, 2) + (1000 * penalty);
    }
    
    private final int FUNCTION_B_VARIABLES = 2;
    private double functionB(double x1, double x2) {
        double A = x1 - 1.0;
        
        return Math.pow(A, 2) - (10 * Math.cos(2 * Math.PI * x1)) + Math.pow(x2, 2) - (10 * Math.cos(2 * Math.PI * x2));
    }
    
    public enum FunctionType {
        FUNCTION_A,
        FUNCTION_B;
    }
}
