package jmetal.util.vector;

public abstract class VectorGenerator {
	protected double[][] lambda_;

	public double[][] getVectors() {
		return this.lambda_;
	}

	public double getL2Discrepancy() {
		int n = lambda_.length;
		int m = lambda_[0].length;

		double result = Math.pow(13.0 / 12.0, m);

		double sum = 0;
		for (int k = 0; k < n; k++) {
			double pro = 1;
			for (int j = 0; j < m; j++)
				pro *= (1 + 0.5 * Math.abs(lambda_[k][j] - 0.5) - 0.5
						* (lambda_[k][j] - 0.5) * (lambda_[k][j] - 0.5));
			sum += pro;
		}

		result -= ((2 * sum) / n);

		sum = 0;
		for (int k = 0; k < n; k++) {
			for (int j = 0; j < n; j++) {
				double pro = 1;
				for (int i = 0; i < m; i++) {
					double factor = 1 + 0.5 * Math.abs(lambda_[k][i] - 0.5)
							+ 0.5 * Math.abs(lambda_[j][i] - 0.5) - 0.5
							* Math.abs(lambda_[k][i] - lambda_[j][i]);
					pro *= factor;
				}
				sum += pro;
			}
		}

		result += (sum / (n * n));

		return Math.sqrt(result);
	}

}
