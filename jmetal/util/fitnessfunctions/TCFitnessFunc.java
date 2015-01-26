package jmetal.util.fitnessfunctions;

import jmetal.core.FitnessFunction;
import jmetal.core.Solution;

public class TCFitnessFunc implements FitnessFunction {

	double[] lambda_;

	double[] zidea_;

	public TCFitnessFunc(double[] lambda, double[] zidea) {
		this.lambda_ = lambda;
		this.zidea_ = zidea;
	}
	
	public double getEvaluateFitness(Solution individual) {

		double maxFun = -1.0e+30;

		for (int n = 0; n < individual.numberOfObjectives(); n++) {
			double diff = Math.abs(individual.getObjective(n) - zidea_[n]);

			double feval;
			if (lambda_[n] == 0) {
				feval = diff / 0.0001;
				// feval = diff * 0.0001;
			} else {
				feval = diff / lambda_[n];
				// feval = diff * lambda_[n];
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
			double diff = Math.abs(individual.getObjective(n) - zidea_[n]);

			double feval;
			if (lambda_[n] == 0) {
				feval = diff / 0.0001;
				// feval = diff * 0.0001;
			} else {
				feval = diff / lambda_[n];
				// feval = diff * lambda_[n];
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

	public double[] getIdeaPoint() {
		return zidea_;
	}
}
