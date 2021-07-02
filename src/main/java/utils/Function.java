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
            case FUNCTION_C:
                return this.functionC(values);
            case FUNCTION_D:
                return this.functionD(values);
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
            case FUNCTION_C:
                return this.FUNCTION_C_VARIABLES;
            case FUNCTION_D:
                return this.FUNCTION_D_VARIABLES;
            default:
                return this.FUNCTION_A_VARIABLES;
        }
    }
    
    public String getStringFunction() {
        switch(this.functionType) {
            case FUNCTION_A:
                return FUNCTION_A_STRING;
            case FUNCTION_B:
                return FUNCTION_B_STRING;
            case FUNCTION_C:
                return FUNCTION_C_STRING;
            case FUNCTION_D:
                return FUNCTION_D_STRING;
            default:
                return FUNCTION_A_STRING;
        }
    }
    
    public FunctionType getFunctionType() {
        return this.functionType;
    }
            
            
            
    
    private final int FUNCTION_A_VARIABLES = 2;
    public static final String FUNCTION_A_STRING = "Function A f(x1,x2) = (x1 - 2)⁴ + (x1 - 2 * x2)²";
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
    public static final String FUNCTION_B_STRING = "Function B f(x1,x2) = (x1 - 1)² - 10 * cos(2 * π * x1) + x2² - 10 * cos(2 * π * x2)";
    private double functionB(double x1, double x2) {
        double A = x1 - 1.0;
        
        return Math.pow(A, 2) - (10 * Math.cos(2 * Math.PI * x1)) + Math.pow(x2, 2) - (10 * Math.cos(2 * Math.PI * x2));
    }
    
    private final int FUNCTION_C_VARIABLES = 20;
    public static final String FUNCTION_C_STRING = "Function C f(x1,...,xn) = ∑ xi²";
    private double functionC(double[] x) {
        double sum  = 0;
        for(int i = 0; i < this.FUNCTION_C_VARIABLES; ++i) {
            sum += (x[i] * x[i]);
        }
        
        return sum;
    }
    
    private final int FUNCTION_D_VARIABLES = 20;
    public static final String FUNCTION_D_STRING = "Function D f(x1,...,xn) = ∑ -xi * sin(√|xi|)";
    private double functionD(double[] x) {
        double sum = 0;
        for(int i = 0; i < this.FUNCTION_D_VARIABLES; ++i) {
            sum += -x[i] * Math.sin(Math.sqrt(Math.abs(x[i])));
        }
        
        return sum;
    }
    
    public enum FunctionType {
        FUNCTION_A,
        FUNCTION_B,
        FUNCTION_C,
        FUNCTION_D;
    }
}
