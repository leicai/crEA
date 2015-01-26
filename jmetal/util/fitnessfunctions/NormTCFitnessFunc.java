package jmetal.util.fitnessfunctions;

import jmetal.core.FitnessFunction;
import jmetal.core.Solution;

public class NormTCFitnessFunc implements FitnessFunction {
	double[] lambda_;
	
	public NormTCFitnessFunc(double[] lambda){
		this.lambda_ = lambda;
	}
	// just return the fitness value
	public double getEvaluateFitness(Solution individual){
		double maxFun = -1.0e+30;

		for (int n = 0; n < individual.numberOfObjectives(); n++) {
			double diff = Math.abs(individual.getNormalizedObjective(n));
		//	diff = Math.pow(diff, 4);
			double feval;
			if (lambda_[n] == 0) {
				feval = diff / 0.0001;
			} else {
				feval = diff / lambda_[n];
			}
			if (feval > maxFun) {
				maxFun = feval;
			}
		} // for
		return maxFun;
	}
	@Override
	public void evaluateFitness(Solution individual) {
		double maxFun = -1.0e+30;

		for (int n = 0; n < individual.numberOfObjectives(); n++) {
			double diff = Math.abs(individual.getNormalizedObjective(n));
		//	diff = Math.pow(diff, 4);
			double feval;
			if (lambda_[n] == 0) {
				feval = diff / 0.0001;
			} else {
				feval = diff / lambda_[n];
			}
			if (feval > maxFun) {
				maxFun = feval;
			}
		} // for

		individual.setFitness(maxFun);

	}
	
	@Override
	public double[] getLambda() {
		// TODO Auto-generated method stub
		return lambda_;
	}

	@Override
	public double[] getIdeaPoint() {
		// TODO Auto-generated method stub
		return null;
	}
}
