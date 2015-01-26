package jmetal.metaheuristics.crEA;

import java.io.FileNotFoundException;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.operators.mutation.Mutation;
import jmetal.qualityIndicator.fastHypervolume.FastHypervolume;

import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.Permutation;

import jmetal.util.comparators.CrowdingComparator;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.comparators.FitnessComparator;

import jmetal.util.ranking.ClusteringRanking;
import jmetal.util.ranking.NondominatedRanking;
import jmetal.util.ranking.Ranking;
import jmetal.util.*;
import jmetal.util.vector.*;

public class crEA extends Algorithm {
	private int populationSize_;
	
	private int numOfRefPoints_;

	private SolutionSet population_;
	SolutionSet offspringPopulation_;
	SolutionSet union_;

	int evaluations_;

	int divisions_;
	
	double[] zidea_; 
	
	Operator crossover_;
	Operator mutation_;
	Operator selection_;
		
	boolean doNormalize_;

	double[][] lambda_; // reference points
	
	public crEA(Problem problem) {
		super(problem);
	} // NSGAII
	
	/* (non-Javadoc)
	 * @see jmetal.core.Algorithm#execute()
	 */
	@Override
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		int maxEvaluations;

		evaluations_ = 0;
		
		maxEvaluations = ((Integer) this.getInputParameter("maxEvaluations")).intValue();
		
		divisions_ = ((Integer) this.getInputParameter("divisions")).intValue();
		
		doNormalize_ = ((Boolean) this.getInputParameter("doNormalize")).booleanValue();
		
		Solution reference = new Solution(problem_.getNumberOfObjectives());
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			reference.setObjective(i, 200);
		
		VectorGenerator vg = new WeightVectorGeneratorV1(divisions_, problem_.getNumberOfObjectives());
		lambda_ = vg.getVectors();  // The reference points
		populationSize_ = vg.getVectors().length;
		numOfRefPoints_ =vg.getVectors().length;
		
		mutation_ = operators_.get("mutation");
		crossover_ = operators_.get("crossover");
		selection_ = operators_.get("selection");
		
		Distance distance = new Distance();
		
		initIdeaPoint();
		initPopulation();
		
		while (evaluations_ < maxEvaluations) {
	
			offspringPopulation_ = new SolutionSet(populationSize_);
			Solution[] parents = new Solution[2];
			for (int i = 0; i < (populationSize_ / 2); i++) {
				if (evaluations_ < maxEvaluations) {
    				parents = (Solution[]) selection_.execute(population_);
				
					Solution[] offSpring = (Solution[]) crossover_.execute(parents);
					
					
					mutation_.execute(offSpring[0]);
					mutation_.execute(offSpring[1]);
					
					problem_.evaluate(offSpring[0]);
					problem_.evaluateConstraints(offSpring[0]);
					problem_.evaluate(offSpring[1]);
					problem_.evaluateConstraints(offSpring[1]);

					updateIdeaPoint(offSpring[0]);
					updateIdeaPoint(offSpring[1]);
					
					offspringPopulation_.add(offSpring[0]);
					offspringPopulation_.add(offSpring[1]);
					evaluations_ += 2;
				} // if
			} // for

			union_ = ((SolutionSet)population_).union(offspringPopulation_);
			
			Ranking ranking = new ClusteringRanking(union_, lambda_, zidea_, doNormalize_);
						
			int remain = populationSize_;
			int index = 0;
			SolutionSet front = null;
			population_.clear();

			// Obtain the next front
			front = ranking.getSubfront(index);

			while ((remain > 0) && (remain >= front.size())) {
				
				
				for (int k = 0; k < front.size(); k++) {
					population_.add(front.get(k));
				} // for

				// Decrement remain
				remain = remain - front.size();

				// Obtain the next front
				index++;
				if (remain > 0) {
					front = ranking.getSubfront(index);
				} // if
			} // while

			if (remain > 0) { // front contains individuals to insert
				
				
				int[] perm = new Permutation().intPermutation(remain);
				for (int k = 0; k < remain; k++) {
					population_.add(front.get(perm[k]));
				} // for
				remain = 0;
				
			} // if

		} // while

		// Return the first non-dominated front
		Ranking ranking = new NondominatedRanking(population_);


		return ranking.getSubfront(0);
			
	}
	
	
	public void initPopulation() throws JMException, ClassNotFoundException {
		
		population_= new SolutionSet(populationSize_);
		
		for (int i = 0; i < populationSize_; i++) {
			Solution newSolution = new Solution(problem_);

			problem_.evaluate(newSolution);
			problem_.evaluateConstraints(newSolution);
			
			updateIdeaPoint(newSolution);
			evaluations_++;
			population_.add(newSolution);
		} // for
	} // initpop
	
	public void initIdeaPoint() {
		zidea_ = new double[problem_.getNumberOfObjectives()];
		for (int i = 0; i < zidea_.length; i++)
			zidea_[i] = 1.0e+30;
	}
	
	
	public void updateIdeaPoint(Solution individual) {
		for (int j = 0; j < problem_.getNumberOfObjectives(); j++) {
			double val = individual.getObjective(j);
			if (val < zidea_[j])
				zidea_[j] = val;
		}
	}
}
