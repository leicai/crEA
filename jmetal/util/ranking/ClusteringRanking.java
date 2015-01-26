package jmetal.util.ranking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.comparators.FitnessComparator;
import jmetal.util.fitnessfunctions.NormTCFitnessFunc;
import jmetal.util.fitnessfunctions.TCFitnessFunc;

//used in INSGAIII

public class ClusteringRanking implements Ranking {

	private SolutionSet solutionSet_;

	private List<SolutionSet> ranking_;

	private SolutionSet[] refSets_;

	double[][] lambda_;

	double theta_;

	int obj_;

	double[] zidea_;
	
//	double[] zmin_;

	public ClusteringRanking(SolutionSet solutionSet, double[][] lambda,
			double[] zidea, boolean doNormalize) {
		this.solutionSet_ = solutionSet;
		this.lambda_ = lambda;
		this.zidea_ = zidea; 	// The ideal points
		this.obj_ = solutionSet.get(0).numberOfObjectives();

		ranking_ = new ArrayList<SolutionSet>();

		refSets_ = new SolutionSet[lambda_.length];  // The i-th elements of refSets indicates the solutionset which associate to i-th reference point
		for (int i = 0; i < refSets_.length; i++)
			refSets_[i] = new SolutionSet();
		
		clustering(doNormalize);
		ranking();
	}
	
	void clustering(boolean doNormalize){
		if (doNormalize)
			normalize();
		
		for (int k = 0; k < solutionSet_.size(); k++) {

			Solution sol = solutionSet_.get(k);

			double d2 = Double.MAX_VALUE;
			int index = -1;

			for (int j = 0; j < lambda_.length; j++) {
		
				double[] dists = null;
				
				if (doNormalize)
					dists = getDistancesWithNormalize(sol, lambda_[j]);
				else
					dists = getDistancesWithoutNormalize(sol, lambda_[j], zidea_);


				if (dists[1] < d2) {
					// To seek the minimum distance to reference points
					d2 = dists[1];
					index = j;
				}
			}
			sol.setVDistance(d2);

			refSets_[index].add(sol);
			sol.setClusterID(index);
			
			if (doNormalize) {
				NormTCFitnessFunc func = new NormTCFitnessFunc(lambda_[index]);
				func.evaluateFitness(sol);
			}
			else {
				TCFitnessFunc func = new TCFitnessFunc(lambda_[index], zidea_);
				func.evaluateFitness(sol);
			}
		}	
	}
	
	
	double[] getDistancesWithoutNormalize(Solution sol, double[] ref, double[] z){
		double[] d = new double[2];
		
		double d1, d2, nl;

		d1 = d2 = nl = 0.0;

		for (int i = 0; i < sol.numberOfObjectives(); i++) {
			d1 += (sol.getObjective(i) - z[i]) * ref[i];
			nl += (ref[i] * ref[i]);
		}
		nl = Math.sqrt(nl);
		d1 = Math.abs(d1) / nl;
		
	
		d2 =0;
		for (int i = 0; i < sol.numberOfObjectives(); i++) {
						d2 += ((sol.getObjective(i) - z[i]) - d1
					* (ref[i] / nl)) * ((sol.getObjective(i) - z[i]) - d1
							* (ref[i] / nl));
		}
		d2 = Math.sqrt(d2);
		
		d[0] = d1;
		d[1] = d2;
		
		return d;
	}
	
	

	
	double[] getDistancesWithNormalize(Solution sol, double[] ref){
		double ip = 0;
		double refLenSQ = 0;
//		double solLenSQ = 0;
		
		double[] d = new double[2];
		for (int j = 0; j < obj_; j++) {

			ip += sol.getNormalizedObjective(j) * ref[j];
			refLenSQ += (ref[j] * ref[j]);
		}
		refLenSQ = Math.sqrt(refLenSQ);
		
		d[0] = Math.abs(ip) / refLenSQ;   //d_{j,1}(x)
		
	
		d[1] = 0;
		for (int i = 0; i < sol.numberOfObjectives(); i++) {
			d[1] += (sol.getNormalizedObjective(i) - d[0] * (ref[i] / refLenSQ))
					* (sol.getNormalizedObjective(i) - d[0]
							* (ref[i] / refLenSQ));
		}
		d[1] = Math.sqrt(d[1]); // d_{j,2}(x)
		
		
		return d;
	}
	

	void normalize() {
		double[] zmax = new double[obj_];
	//	double[] zmin = new double[obj_];

		for (int j = 0; j < obj_; j++) {
			zmax[j] = Double.MIN_VALUE;
			
			for (int k = 0; k < solutionSet_.size(); k++) {

				if (solutionSet_.get(k).getObjective(j) > zmax[j])
					zmax[j] = solutionSet_.get(k).getObjective(j);
				
			}
		}

		for (int j = 0; j < obj_; j++) {

			for (int k = 0; k < solutionSet_.size(); k++) {
				Solution sol = solutionSet_.get(k);
				double val = (sol.getObjective(j) - zidea_[j])
						/ (zmax[j] - zidea_[j]);

				sol.setNormalizedObjective(j, val);
			}
		}
	}

	void ranking() {

		int maxLen = Integer.MIN_VALUE;
		for (int i = 0; i < refSets_.length; i++) {
			
			if (refSets_[i].size() > maxLen)
				maxLen = refSets_[i].size();
			refSets_[i].sort(new FitnessComparator());
		}


		for (int i = 0; i < maxLen; i++) {
			SolutionSet set = new SolutionSet();
			for (int j = 0; j < refSets_.length; j++) {
				if (refSets_[j].size() > i) {
					refSets_[j].get(i).setRank(i);
					set.add(refSets_[j].get(i));
				}
			}
			ranking_.add(set);
		}
	}

	public SolutionSet getSubfront(int rank) {
		return ranking_.get(rank);
	} // getSubFront

	/**
	 * Returns the total number of subFronts founds.
	 */
	public int getNumberOfSubfronts() {
		return ranking_.size();
	} // getNumberOfSubfronts
}
