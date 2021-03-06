/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pso;

import utils.Function;
import java.util.Arrays;
import java.util.Random;

/**
 * 
 * @author meimarcel
 */
public class Particle {
    private double position[];
    private double bestPosition[];
    private double bestEval;
    private double velocity[];
    private int numerOfVariables;
    private Function function;
    private static Random rand = null;
    
    public Particle(Function function, double beginRange, double endRange, Random random) {
        rand = random;
        
        this.function = function;
        this.numerOfVariables = this.function.getNumberOfVariables();
        this.position = new double[this.numerOfVariables];
        this.bestPosition = new double[this.numerOfVariables];
        this.velocity = new double[this.numerOfVariables];
        
        this.setUniformDistribution(beginRange, endRange, this.numerOfVariables);
        this.bestPosition = Arrays.copyOf(this.position, this.numerOfVariables);
        this.bestEval = this.function.fit(this.position);
    }
    
    
    private void setUniformDistribution(double beginRange, double endRange, int variables) {
        for(int i = 0; i < variables; ++i) {
            this.position[i] = (rand.nextDouble() * (endRange - beginRange))+ beginRange;
        }
    }
    
    
    public void calculatePersonalBest() {
        double eval = this.function.fit(this.position);
        if(eval < this.bestEval) {
            this.bestEval = eval;
            this.bestPosition = Arrays.copyOf(this.position, this.position.length);
        }
    }
    
    public void movePositions() {
        for(int i = 0; i < this.position.length; ++i) {
            this.position[i] += this.velocity[i];
        }
    }
    
    public int getNumberOfVariables() {
        return this.numerOfVariables;
    }
    
    public double[] getPosition() {
        return Arrays.copyOf(this.position, this.position.length);
    }
    
    
    public double[] getBestPosition() {
        return Arrays.copyOf(this.bestPosition, this.position.length);
    }
    
    public double[] getVelocity() {
        return Arrays.copyOf(this.velocity, this.position.length);
    }
    
    public void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }
    
    public double getBestEval() {
        return this.bestEval;
    }
}
