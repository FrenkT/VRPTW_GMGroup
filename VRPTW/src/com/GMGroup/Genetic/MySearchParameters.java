package com.GMGroup.Genetic;

public class MySearchParameters {

	/**
	 * Describes how many chromosomes should be used as initial population. 
	 * Default value is 50.
	 */
	private int initialPopulationSize=50;
	
	/**
	 * Describes how many iteration of Genetic algorithm should be performed. A value of 0 means INFINITE,
	 * so the algorithm will continue until the stopping timer is invoked. 
	 * Default value is 0.
	 */
	private int maxEvolveIterations=0; 
	
	/**
	 * Makes the crossover operator to operate on a limited number of parents.
	 * If INITIAL_SIZE_POPULATION is 90, there will be 90 * CROSS_OVER_RATIO 
	 * crossovers. The input parameter should be positive. 
	 * Default value is 0.3.
	 */
	private double crossOverLimitRatio = 1;
	
	/**
	 * Defines the AlphaParameter of the KChainMutation operator.
	 * Default value is 5.
	 */
	private double alphaParameterKChain = 5;
	
	/**
	 * Defines how many swaps must be performed in a key-chain mutation operator.
	 * A value of 4 means a->b, b->c, c->d, d->a.
	 * Default value is 4.
	 */
	private int numOfKChainSwap = 4;
	
	/**
	 * Represents the number of consecutive non-improving moves allowed before dropping
	 * a tabu session. 
	 * Default value is 50.
	 */
	private int tabuNonImprovingThresold=100;
	
	/**
	 * Represents the PERCENTAGE of minimum improvement which causes the tabu to continue.
	 * If noImprovingCounter reaches noImprovingThreshold, abort. A value of 5 means that
	 * a new solution is considered IMPROVING when the delta is greater than 5%.
	 * Default value is 0.1 => 0.1%.
	 */
	private double tabuDeltaRatio=0.00001;

	
	/**
	 * Represents the window size of the PMXCrossOver. The larger, the less genes will be moved within
	 * a single crossover.
	 */
	private int crossOverWindowWidth=40;
	
	
	/**
	 * Defines the percentage of feasible chromosomes that should compose the initial population.
	 * The Factory tries to generate as many feasible chromosome as requested, but if not-feasible 
	 * solution were found, it would generate a random one.
	 */
	private double initialPopFeasibleChromosomesRatio = 50;
	
	/**
	 * Describes the policy to apply when no feasible solution is found at the end of each evolve
	 * iteration. Default value is INCREASE_POPULATION.
	 */
	private InfeasibilityPolicy infeasibilityPolicy = InfeasibilityPolicy.RELAX_TABU;
	
	
	/**
	 * @return the tabuDeltaRatio
	 */
	public double getTabuDeltaRatio() {
		return tabuDeltaRatio;
	}

	/**
	 * @param tabuDeltaRatio the tabuDeltaRatio to set
	 */
	public void setTabuDeltaRatio(double tabuDeltaRatio) {
		this.tabuDeltaRatio = tabuDeltaRatio;
	}

	/**
	 * @return the tabuNonImprovingThresold
	 */
	public int getTabuNonImprovingThresold() {
		return tabuNonImprovingThresold;
	}

	/**
	 * @param tabuNonImprovingThresold the tabuNonImprovingThresold to set
	 */
	public void setTabuNonImprovingThresold(int tabuNonImprovingThresold) {
		this.tabuNonImprovingThresold = tabuNonImprovingThresold;
	}

	/**
	 * @return the numOfKChainSwap
	 */
	public int getNumOfKChainSwap() {
		return numOfKChainSwap;
	}

	/**
	 * @param numOfKChainSwap the numOfKChainSwap to set
	 */
	public void setNumOfKChainSwap(int numOfKChainSwap) {
		this.numOfKChainSwap = numOfKChainSwap;
	}

	/**
	 * @return the alphaParameterKChain
	 */
	public double getAlphaParameterKChain() {
		return alphaParameterKChain;
	}

	/**
	 * @param alphaParameterKChain the alphaParameterKChain to set
	 */
	public void setAlphaParameterKChain(double alphaParameterKChain) {
		this.alphaParameterKChain = alphaParameterKChain;
	}

	/**
	 * @return the crossOverLimitRatio
	 */
	public double getCrossOverLimitRatio() {
		return crossOverLimitRatio;
	}

	/**
	 * @param crossOverLimitRatio the crossOverLimitRatio to set
	 */
	public void setCrossOverLimitRatio(double crossOverLimitRatio) {
		this.crossOverLimitRatio = crossOverLimitRatio;
	}

	/**
	 * @return the maxEvolveIterations
	 */
	public int getMaxEvolveIterations() {
		return maxEvolveIterations;
	}

	/**
	 * @param maxEvolveIterations the maxEvolveIterations to set
	 */
	public void setMaxEvolveIterations(int maxEvolveIterations) {
		this.maxEvolveIterations = maxEvolveIterations;
	}

	/**
	 * @return the initialPopulationSize
	 */
	public int getInitialPopulationSize() {
		return initialPopulationSize;
	}

	/**
	 * @param initialPopulationSize the initialPopulationSize to set
	 */
	public void setInitialPopulationSize(int initialPopulationSize) {
		this.initialPopulationSize = initialPopulationSize;
	}

	/**
	 * @return the initialPopFeasibleChromosomesRatio
	 */
	public double getInitialPopFeasibleChromosomesRatio() {
		return initialPopFeasibleChromosomesRatio;
	}

	/**
	 * @param initialPopFeasibleChromosomesRatio the initialPopFeasibleChromosomesRatio to set
	 */
	public void setInitialPopFeasibleChromosomesRatio(
			double initialPopFeasibleChromosomesRatio) {
		if (initialPopFeasibleChromosomesRatio<0 || initialPopFeasibleChromosomesRatio>100)
			throw new IllegalArgumentException("Invalid initialPopFeasibleChromosomesRatio: the value should be between 0 and 100.");
		this.initialPopFeasibleChromosomesRatio = initialPopFeasibleChromosomesRatio;
	}

	/**
	 * @return the crossOverWindowWidth
	 */
	public int getCrossOverWindowWidth() {
		return crossOverWindowWidth;
	}

	/**
	 * @param crossOverWindowWidth the crossOverWindowWidth to set
	 */
	public void setCrossOverWindowWidth(int crossOverWindowWidth) {
		if (crossOverWindowWidth<0)
			throw new IllegalArgumentException("CrossOverWindowWidth cannot be a negative value");
		this.crossOverWindowWidth = crossOverWindowWidth;
	}

	/**
	 * @return the infeasibilityPolicy
	 */
	public InfeasibilityPolicy getInfeasibilityPolicy() {
		return infeasibilityPolicy;
	}

	/**
	 * @param infeasibilityPolicy the infeasibilityPolicy to set
	 */
	public void setInfeasibilityPolicy(InfeasibilityPolicy infeasibilityPolicy) {
		this.infeasibilityPolicy = infeasibilityPolicy;
	}
	
	
	
}
