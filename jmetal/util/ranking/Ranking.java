package jmetal.util.ranking;

import jmetal.core.SolutionSet;

public interface Ranking {
	public SolutionSet getSubfront(int rank) ;

	/**
	 * Returns the total number of subFronts founds.
	 */
	public int getNumberOfSubfronts();
}
