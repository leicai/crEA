package jmetal.metaheuristics.crEA;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.qualityIndicator.fastHypervolume.FastHypervolume;
import jmetal.util.Configuration;
//import jmetal.util.GeometryGD;
import jmetal.util.JMException;
import jmetal.util.comparators.RankComparator;

public class crEA_main {
	public static Logger logger_; // Logger object
	public static FileHandler fileHandler_; // FileHandler object

	public static void main(String[] args) throws JMException,
			SecurityException, IOException, ClassNotFoundException {
		Problem problem; // The problem to solve
		Algorithm algorithm; // The algorithm to use
		Operator crossover; // Crossover operator
		Operator mutation; // Mutation operator
		Operator selection; // Selection operator

		HashMap parameters; // Operator parameters

		QualityIndicator indicators; // Object to get quality indicators

		int objs = 10;
		String probName="WFG5";
		
		// Logger object and file to store log messages
		Object[] params = { "Real", objs - 1, 25 - objs, objs };
		problem = (new ProblemFactory()).getProblem(probName, params);

		algorithm = new crEA(problem);
		
		if (objs == 2){
			algorithm.setInputParameter("divisions", 119);
		}
		else if (objs == 3){
			algorithm.setInputParameter("divisions", 14);
		}
		else if (objs == 4) {
			algorithm.setInputParameter("divisions", 7);
		}
		else if (objs == 5) {
			algorithm.setInputParameter("divisions", 5);
		}
		else if (objs == 6) {
			algorithm.setInputParameter("divisions", 4);
		}
		else if (objs == 7) {
			algorithm.setInputParameter("divisions", 3);
		}
		else if (objs == 8) {
			algorithm.setInputParameter("divisions", 3);
		}
		else if (objs == 9) {
			algorithm.setInputParameter("divisions", 3);    //165
		}
		else if (objs == 10) {
			algorithm.setInputParameter("divisions", 3);
		}
		else {
			algorithm.setInputParameter("divisions", 2);
		}
		
		// Algorithm parameters
		algorithm.setInputParameter("maxEvaluations",50000);
		algorithm.setInputParameter("doNormalize", true);
		
		// Mutation and Crossover for Real codification
		parameters = new HashMap();
		parameters.put("probability", 0.9);
		parameters.put("distributionIndex", 20.0);
		crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover",
				parameters);

		parameters = new HashMap();
		parameters.put("probability", 1.0 / problem.getNumberOfVariables());
		parameters.put("distributionIndex", 20.0);
		mutation = MutationFactory.getMutationOperator("PolynomialMutation",
				parameters);


		parameters = null;
		selection = SelectionFactory.getSelectionOperator("RandomSelection",
				parameters);

		// Add the operators to the algorithm
		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("mutation", mutation);
		algorithm.addOperator("selection", selection);

		  // Execute the Algorithm
	    long initTime = System.currentTimeMillis();
	    SolutionSet population = algorithm.execute();
	    long estimatedTime = System.currentTimeMillis() - initTime;
	    
	    System.out.println(estimatedTime+"ms");
	    population.printObjectivesToFile("FUN");

	} // main
}
