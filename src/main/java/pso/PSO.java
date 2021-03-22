/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import utils.Logger;
import utils.Plotter;

/**
 *
 * @author meimarcel
 */
public class PSO {
    public final static double DEFAULT_INERTIA_WEIGHT = 0.729844;
    public final static double DEFAULT_COGNITIVE_WEIGHT = 1.496180;
    public final static double DEFAULT_SOCIAL_WEIGHT = 1.496180;
    public final static int DEFAULT_BEGIN_RANGE = -100;
    public final static int DEFAULT_END_RANGE = 100;
    public final Logger LOGGER = new Logger();
    
    private boolean plotGraph = false;
    private int particleNuber, epochs, neighborhoodSize;
    private double inertiaWeight, cognitiveWeight, socialWeight, beginRange, endRange;
    private double[] globalBestPosition;
    private double globalBestEval = Double.MAX_VALUE;
    private Function function;
    
    private StopConditionType stopCondition;
    private double conditionTarget;
    private double conditionError;
    private int conditionWindow;
    private List<Double> conditionList;
    private double conditionBeginDiameter;
    private double conditionFit;
    private double conditionMaxRadius;
    
    private static Random rand = null;
    
    
    public PSO(int particleNumber, int epochs, Function function) {
        if(rand == null) {
            rand = new Random();
            rand.setSeed(200);
        }
        
        this.particleNuber = particleNumber;
        this.epochs = epochs;
        this.function = function;
        this.inertiaWeight = DEFAULT_INERTIA_WEIGHT;
        this.cognitiveWeight = DEFAULT_COGNITIVE_WEIGHT;
        this.socialWeight = DEFAULT_SOCIAL_WEIGHT;
        this.beginRange = DEFAULT_BEGIN_RANGE;
        this.endRange = DEFAULT_END_RANGE;
        
    }
    
    public PSO(int particleNumber, int epochs, double inertiaWeight, 
            double cognitiveWeight, double socialWeight, Function function) {
        if(rand == null) {
            rand = new Random();
            rand.setSeed(200);
        }
        
        this.particleNuber = particleNumber;
        this.epochs = epochs;
        this.function = function;
        this.inertiaWeight = inertiaWeight;
        this.cognitiveWeight = cognitiveWeight;
        this.socialWeight = socialWeight;
        this.beginRange = DEFAULT_BEGIN_RANGE;
        this.endRange = DEFAULT_END_RANGE;
    }
    
    public PSO(int particleNumber, int epochs, double inertiaWeight, 
            double cognitiveWeight, double socialWeight, Function function,
            double beginRange, double endRange) {
        if(rand == null) {
            rand = new Random();
            rand.setSeed(200);
        }
        
        this.particleNuber = particleNumber;
        this.epochs = epochs;
        this.function = function;
        this.inertiaWeight = inertiaWeight;
        this.cognitiveWeight = cognitiveWeight;
        this.socialWeight = socialWeight;
        this.beginRange = Math.min(beginRange, endRange);
        this.endRange = Math.max(beginRange, endRange);
    }
    
    public String runGBestPSO() {
        StringBuilder log = new StringBuilder();
        long startTime = System.nanoTime();
        List<Double> dataFit = new ArrayList<>();
        List<double[]> dataMean = new ArrayList<>();
        List<double[]> dataStandardDeviation = new ArrayList<>();
        
        
        if(this.stopCondition == StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT || this.stopCondition == StopConditionType.FUNCTION_SLOPE) {
            this.conditionList = new ArrayList<>();
        }
        
        Particle[] particles = this.initialize();
        
        log.append("\n");
        log.append(LOGGER.white("---------------------------EXECUTING---------------------------\n"));
        int i = 0;
        for(; i < this.epochs && this.stopConditionEvaluete(); ++i) {
            double particleDist[] = new double[function.getNumberOfVariables()];
            double standardDeviation[] =  new double[function.getNumberOfVariables()];
            
            log.append(LOGGER.white("Epoch :"+i+" BestEval = "+this.globalBestEval+" BestPositions = ["));
            dataFit.add(this.globalBestEval);
            int end = this.globalBestPosition.length - 1;
            for(int j = 0; j < end; ++j) {
                log.append(LOGGER.white(this.globalBestPosition[j]+", "));
            }
            log.append(LOGGER.white(this.globalBestPosition[end]+"]\n"));
            
            for(Particle p : particles) {
                p.calculatePersonalBest();
                if(p.getBestEval() < this.globalBestEval) {
                    this.globalBestEval = p.getBestEval();
                    this.globalBestPosition = p.getBestPosition();
                }
            }
            
            this.conditionFit = this.globalBestEval;
            if(this.stopCondition == StopConditionType.FUNCTION_SLOPE) {
                this.conditionList.add(this.globalBestEval);
            }
            
            for(Particle p : particles) {
                double position[] = p.getPosition();
                for(int j = 0; j < particleDist.length; ++j) {
                    particleDist[j] += position[j];
                }
            }
            
            for(int j = 0; j < particleDist.length; ++j) {
                particleDist[j] = particleDist[j] / (double) this.particleNuber;
            }
            dataMean.add(particleDist);
            
            double sumPositions = 0;
            for(Particle p : particles) {
                double velocity[] = p.getVelocity();
                double personalBestPosition[] = p.getBestPosition();
                double position[] = p.getPosition();
                
                for(int j = 0; j < velocity.length; ++j) {
                    double c1r1 = this.cognitiveWeight * rand.nextDouble();
                    double c2r2 = this.socialWeight * rand.nextDouble();
                    velocity[j] = (this.inertiaWeight * velocity[j]) + 
                            (c1r1 * (personalBestPosition[j] - position[j])) +
                            (c2r2 * (this.globalBestPosition[j] - position[j]));
                    
                    standardDeviation[j] += (position[j] - particleDist[j]) * (position[j] - particleDist[j]); 
                }
                
                p.setVelocity(velocity);
                p.movePositions();
                
                if(this.stopCondition == StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT) {
                    double dist = 0;
                    double currentPosition[] = p.getPosition();
                    for(int j = 0; j < currentPosition.length; ++j) {
                         dist += ((currentPosition[j] - position[j]) * (currentPosition[j] - position[j]));
                    }
                    dist = Math.sqrt(dist);
                    sumPositions += dist;
                }
            }
            
            for(int j = 0; j < standardDeviation.length; ++j) {
                standardDeviation[j] = Math.sqrt(standardDeviation[j] / (double) this.particleNuber);
            }
            dataStandardDeviation.add(standardDeviation);
            
            
            if(this.stopCondition == StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT) {
                this.conditionList.add(sumPositions / (double) this.particleNuber);
            }
            
            if(this.stopCondition == StopConditionType.NORMALIZED_RADIUS) {
                this.conditionMaxRadius = Double.MIN_VALUE;
                for(Particle p : particles) {
                    double radius = 0;
                    double positions[] = p.getPosition(); 
                    for(int j = 0; j < positions.length; ++j) {
                         radius += ((positions[j] - this.globalBestPosition[j]) * (positions[j] - this.globalBestPosition[j]));
                    }
                    radius = Math.sqrt(radius);
                    if(this.conditionMaxRadius < radius) {
                        this.conditionMaxRadius = radius;
                    }
                }
            }
            
        }
        
        long stopTime = System.nanoTime();
        System.out.println("");
        log.append("\n");
        log.append(LOGGER.message("---------------------------RESULT---------------------------\n"));
        log.append(LOGGER.message("Epoch = "+i+"\n"));
        log.append(LOGGER.message("Best Eval = "+this.globalBestEval+"\n"));
        log.append(LOGGER.message("Final Best Positions = ["));
        int end = this.globalBestPosition.length - 1;
        System.out.print(LOGGER.ANSI_CYAN);
        for(int j = 0; j < end; ++j) {
            log.append(this.globalBestPosition[j]).append(", ");
            System.out.print(this.globalBestPosition[j]+", ");
        }
        log.append(this.globalBestPosition[end]).append("]\n");
        System.out.print(this.globalBestPosition[end]+"]\n");
        log.append(LOGGER.message("Execution time: "+ ((stopTime - startTime) / 1000000) + " ms\n"));
        log.append(LOGGER.message("---------------------------COMPLETE---------------------------\n"));
        
        if(this.plotGraph) {
            Plotter plotter = new Plotter(dataFit, dataMean, dataStandardDeviation);
            plotter.start();
        }
        
        return log.toString();
    }
    
    public String runLBestPSO() {
        StringBuilder log = new StringBuilder();
        long startTime = System.nanoTime();
        List<Double> dataFit = new ArrayList<>();
        List<double[]> dataMean = new ArrayList<>();
        List<double[]> dataStandardDeviation = new ArrayList<>();
        
        
        if(this.stopCondition == StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT || this.stopCondition == StopConditionType.FUNCTION_SLOPE) {
            this.conditionList = new ArrayList<>();
        }
        
        Particle[] particles = this.initialize();
        
        log.append("\n");
        log.append(LOGGER.white("---------------------------EXECUTING---------------------------\n"));
        int i = 0;
        for(; i < this.epochs && this.stopConditionEvaluete(); ++i) {
            double particleDist[] = new double[function.getNumberOfVariables()];
            double standardDeviation[] =  new double[function.getNumberOfVariables()];
            
            log.append(LOGGER.white("Epoch :"+i+" BestEval = "+this.globalBestEval+" BestPositions = ["));
            dataFit.add(this.globalBestEval);
            int end = this.globalBestPosition.length - 1;
            for(int j = 0; j < end; ++j) {
                log.append(LOGGER.white(this.globalBestPosition[j]+", "));
            }
            log.append(LOGGER.white(this.globalBestPosition[end]+"]\n"));
            
            for(Particle p : particles) {
                p.calculatePersonalBest();
                if(p.getBestEval() < this.globalBestEval) {
                    this.globalBestEval = p.getBestEval();
                    this.globalBestPosition = p.getBestPosition();
                }
            }
            
            this.conditionFit = this.globalBestEval;
            if(this.stopCondition == StopConditionType.FUNCTION_SLOPE) {
                this.conditionList.add(this.globalBestEval);
            }
            
            for(Particle p : particles) {
                double position[] = p.getPosition();
                for(int j = 0; j < particleDist.length; ++j) {
                    particleDist[j] += position[j];
                }
            }
            
            for(int j = 0; j < particleDist.length; ++j) {
                particleDist[j] = particleDist[j] / (double) this.particleNuber;
            }
            dataMean.add(particleDist);
            
            double sumPositions = 0;
            int particleIndex = 0;
            for(Particle p : particles) {
                double velocity[] = p.getVelocity();
                double personalBestPosition[] = p.getBestPosition();
                double position[] = p.getPosition();
                double neighborhoodBestPosition[] = new double[p.getNumberOfVariables()];
                double neighborhoodBestFit = Integer.MAX_VALUE;
                
                int neighborhoodStart = (this.neighborhoodSize%2 == 0) ? (particleIndex - (this.neighborhoodSize / 2) + 1) : (particleIndex - (this.neighborhoodSize / 2)); 
                neighborhoodStart = Math.max(0, neighborhoodStart);
                int neighborhoodEnd = Math.min(this.particleNuber-1, particleIndex + (this.neighborhoodSize / 2));
                
                for(int j = neighborhoodStart; j < particleIndex; ++j) {
                    if(particles[j].getBestEval() < neighborhoodBestFit) {
                        neighborhoodBestFit = particles[j].getBestEval();
                        neighborhoodBestPosition = particles[j].getBestPosition();
                    }
                }
                
                for(int j = particleIndex; j < neighborhoodEnd; ++j) {
                    if(particles[j].getBestEval() < neighborhoodBestFit) {
                        neighborhoodBestFit = particles[j].getBestEval();
                        neighborhoodBestPosition = particles[j].getBestPosition();
                    }
                }
                
                for(int j = 0; j < velocity.length; ++j) {
                    double c1r1 = this.cognitiveWeight * rand.nextDouble();
                    double c2r2 = this.socialWeight * rand.nextDouble();
                    velocity[j] = (this.inertiaWeight * velocity[j]) + 
                            (c1r1 * (personalBestPosition[j] - position[j])) +
                            (c2r2 * (neighborhoodBestPosition[j] - position[j]));
                    
                    standardDeviation[j] += (position[j] - particleDist[j]) * (position[j] - particleDist[j]); 
                }
                
                p.setVelocity(velocity);
                p.movePositions();
                
                if(this.stopCondition == StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT) {
                    double dist = 0;
                    double currentPosition[] = p.getPosition();
                    for(int j = 0; j < currentPosition.length; ++j) {
                         dist += ((currentPosition[j] - position[j]) * (currentPosition[j] - position[j]));
                    }
                    dist = Math.sqrt(dist);
                    sumPositions += dist;
                }
                
                ++particleIndex;
            }
            
            for(int j = 0; j < standardDeviation.length; ++j) {
                standardDeviation[j] = Math.sqrt(standardDeviation[j] / (double) this.particleNuber);
            }
            dataStandardDeviation.add(standardDeviation);
            
            
            if(this.stopCondition == StopConditionType.NUMBER_OF_ITERATION_IMPROVEMENT) {
                this.conditionList.add(sumPositions / (double) this.particleNuber);
            }
            
            if(this.stopCondition == StopConditionType.NORMALIZED_RADIUS) {
                this.conditionMaxRadius = Double.MIN_VALUE;
                for(Particle p : particles) {
                    double radius = 0;
                    double positions[] = p.getPosition(); 
                    for(int j = 0; j < positions.length; ++j) {
                         radius += ((positions[j] - this.globalBestPosition[j]) * (positions[j] - this.globalBestPosition[j]));
                    }
                    radius = Math.sqrt(radius);
                    if(this.conditionMaxRadius < radius) {
                        this.conditionMaxRadius = radius;
                    }
                }
            }
            
        }
        
        long stopTime = System.nanoTime();
        System.out.println("");
        log.append("\n");
        log.append(LOGGER.message("---------------------------RESULT---------------------------\n"));
        log.append(LOGGER.message("Epoch = "+i+"\n"));
        log.append(LOGGER.message("Best Eval = "+this.globalBestEval+"\n"));
        log.append(LOGGER.message("Final Best Positions = ["));
        int end = this.globalBestPosition.length - 1;
        System.out.print(LOGGER.ANSI_CYAN);
        for(int j = 0; j < end; ++j) {
            log.append(this.globalBestPosition[j]).append(", ");
            System.out.print(this.globalBestPosition[j]+", ");
        }
        log.append(this.globalBestPosition[end]).append("]\n");
        System.out.print(this.globalBestPosition[end]+"]\n");
        log.append(LOGGER.message("Execution time: "+ ((stopTime - startTime) / 1000000) + " ms\n"));
        log.append(LOGGER.message("---------------------------COMPLETE---------------------------\n"));
        
        if(this.plotGraph) {
            Plotter plotter = new Plotter(dataFit, dataMean, dataStandardDeviation);
            plotter.start();
        }
        
        return log.toString();
    }
    
    public Particle[] initialize() {
        Particle[] particles = new Particle[this.particleNuber];
        for(int i = 0; i < this.particleNuber; ++i) {
            particles[i] = new Particle(this.function, this.beginRange, this.endRange);
            if(particles[i].getBestEval() < this.globalBestEval) {
                this.globalBestEval = particles[i].getBestEval();
                this.globalBestPosition = particles[i].getBestPosition();
            }
        }
        this.conditionFit = this.globalBestEval;
        
        if(this.stopCondition == StopConditionType.NORMALIZED_RADIUS) {
            this.conditionBeginDiameter = Double.MIN_VALUE;
            for(int i = 0; i < this.particleNuber; ++i) {
                double radius = 0;
                double positions[] = particles[i].getPosition(); 
                for(int j = 0; j < positions.length; ++j) {
                     radius += ((positions[j] - this.globalBestPosition[j]) * (positions[j] - this.globalBestPosition[j]));
                }
                radius = 2 * Math.sqrt(radius);
                if(this.conditionBeginDiameter < radius) {
                    this.conditionBeginDiameter = radius;
                }
            }
            this.conditionMaxRadius = this.conditionBeginDiameter;
        }
        if(this.stopCondition == StopConditionType.FUNCTION_SLOPE) {
            this.conditionList.add(this.globalBestEval);
        }
                
        return particles;
    }
    
    public void setPlotGraph(boolean plotGraph) {
        this.plotGraph = plotGraph;
    }
    
    public void setNeighborhoodSize(int neighborhoodSize) {
        this.neighborhoodSize = neighborhoodSize;
    }
    
    public void setStopConditionType(StopConditionType stopCondition) {
        this.stopCondition = stopCondition;
    }
    
    public void setConditionError(double error) {
        this.conditionError = error;
    }
    
    public void setConditionTarget(double target) {
        this.conditionTarget = target;
    }
    
    public void setConditionWindow(int window) {
        this.conditionWindow = window;
    }
    
    public boolean stopConditionEvaluete() {
        switch(this.stopCondition) {
            case ONLY_EPOCH:
                return StopCondition.onlyEpoch();
            case ACCEPTABLE_ERROR:
                return StopCondition.acceptableError(this.conditionTarget, this.conditionError, this.conditionFit);
            case NUMBER_OF_ITERATION_IMPROVEMENT:
                return StopCondition.numberOfIterationImprovment(this.conditionWindow, this.conditionError, this.conditionList);
            case NORMALIZED_RADIUS:
                return StopCondition.normalizedRadius(this.conditionBeginDiameter, this.conditionMaxRadius, this.conditionError);
            case FUNCTION_SLOPE:
                return StopCondition.functionSlope(this.conditionWindow, this.conditionError, this.conditionList);
            default:
                return StopCondition.onlyEpoch();
        }
    }
    
    public enum StopConditionType {
        ONLY_EPOCH,
        ACCEPTABLE_ERROR,
        NUMBER_OF_ITERATION_IMPROVEMENT,
        NORMALIZED_RADIUS,
        FUNCTION_SLOPE;
    }
}
