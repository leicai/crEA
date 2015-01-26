package jmetal.core;

public interface FitnessFunction {
	public void evaluateFitness(Solution individual);
	public double[] getLambda();
	public double[] getIdeaPoint();
}
