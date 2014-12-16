package com.GMGroup.Genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.IUniversalRateCalculator;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.RandomGenerator;
import org.jgap.impl.GABreeder;
import org.jgap.impl.SwappingMutationOperator;

public class KChainMutationOperator extends SwappingMutationOperator{
	
	/**
	 * Expresses which divergence threshold
	 */
	private double alfa=5;
	private double highValue=0.4;
	private double lowValue=0.06;

	public KChainMutationOperator() throws InvalidConfigurationException {
		super();
	}

	public KChainMutationOperator(Configuration a_config,
			int a_desiredMutationRate) throws InvalidConfigurationException {
		super(a_config, a_desiredMutationRate);
		// TODO Auto-generated constructor stub
	}

	public KChainMutationOperator(Configuration a_config,
			IUniversalRateCalculator a_mutationRateCalculator)
			throws InvalidConfigurationException {
		super(a_config, a_mutationRateCalculator);
		// TODO Auto-generated constructor stub
	}

	public KChainMutationOperator(Configuration a_config)
			throws InvalidConfigurationException {
		super(a_config);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void operate(final Population a_population, List a_candidateChromosomes) {
		
		System.out.println("---- Invoked KChainMutationOperator with Params:");
		System.out.println("---- alfa: "+alfa);
		System.out.println("---- Numofswaps: "+getMutationRate());
		
		//MyFitnessFunction valuta= new MyFitnessFunction();
		double bestFit=0,worstFit=0,parameter1=0,teta=0,mutationProbability;
		List<Double> fitnessFunctions=new ArrayList<Double>();
		for(int i=0; i<a_candidateChromosomes.size(); i++){
			IChromosome c= (IChromosome) a_candidateChromosomes.get(i);
			fitnessFunctions.add(c.getFitnessValue());
			//System.out.println("Fitness:" + c.getFitnessValue());
			}
		Collections.sort(fitnessFunctions);
		worstFit=fitnessFunctions.get(0);
		bestFit=fitnessFunctions.get(fitnessFunctions.size()-1);
		parameter1=1-((worstFit-bestFit)/bestFit)*alfa;
		teta=Math.max(0, parameter1);
		mutationProbability=teta*(highValue-lowValue)+lowValue;
		//System.out.println("mutationProbability:" + mutationProbability);
		//media= media/a_candidateChromosomes.size(); //ho la media di tutte le fitness function di un tutti i cromosomi della lista
													// (parents + offspings)
		//System.out.println("Media:" + media);
		
		// this was a private variable, now it is local reference.
		final IUniversalRateCalculator m_mutationRateCalc = getMutationRateCalc();
		// If the mutation rate is set to zero and dynamic mutation rate is
		// disabled, then we don't perform any mutation.
		// ----------------------------------------------------------------
		if (getMutationRate() == 0 && m_mutationRateCalc == null) {
			return;
		}
		// Determine the mutation rate. If dynamic rate is enabled, then
		// calculate it based upon the number of genes in the chromosome.
		// Otherwise, go with the mutation rate set upon construction.
		// --------------------------------------------------------------
		int currentRate;
		if (m_mutationRateCalc != null) {
			currentRate = m_mutationRateCalc.calculateCurrentRate();
		} else {
			currentRate = getMutationRate();
		}
		RandomGenerator generator = getConfiguration().getRandomGenerator();
		// It would be inefficient to create copies of each Chromosome just
		// to decide whether to mutate them. Instead, we only make a copy
		// once we've positively decided to perform a mutation.
		// ----------------------------------------------------------------
		int size = a_population.size();
		int addedCounter=0;
		if (mutationProbability < 1) {
			for (int i = 0; i < size; i++) {
				IChromosome x = a_population.getChromosome(i);
				IChromosome xm = operate(x, currentRate, generator);
				if (xm != null) {
					a_candidateChromosomes.add(xm);
					addedCounter++;
				}
			}
		}
		
		System.out.println("---- KChainMutation Operator mutated and added "+addedCounter+" elements");
	}
	
	
	/**
	   * Operate on the given chromosome with the given mutation rate.
	   *
	   * @param a_chrom chromosome to operate
	   * @param a_rate how many swap in k-chain mutation
	   * @param a_generator random generator to use (must not be null)
	   * @return mutated chromosome of null if no mutation has occured.
	   *
	   */
	@Override
	protected IChromosome operate(final IChromosome a_chrom, final int a_rate, final RandomGenerator a_generator){
		
		
	    int from, first, to;
	    Gene[] genes = a_chrom.getGenes(); //TODO: check a_rate == param set before

	    first = a_generator.nextInt(a_chrom.size()); //scelgo a caso il primp gene della k-chain
	    
	    from= first;
	    Gene from_value = genes[from];

	    
	    //Qui effettuo il k-chain, con k=a_rate
	    for(int i=0; i<a_rate-1; i++){
	      do {
	    	  to = a_generator.nextInt(a_chrom.size());
	      }
	      while (to==first);
	      
	      //valutare se fare in controllo di uguaglianza tra from e to o eventuali altre euristiche
	      Gene to_value = genes[to];
	      genes[to] = from_value;
	      from_value=to_value;
	      from=to;
	    }
	    genes[first]=from_value;

	    return a_chrom;
	}
	
	/**
	 * Set the number of genes to be mutated into the chromosome. Param = 4 means 
	 * 4 genes are going to be swapped.
	 */
	@Override
	public void setMutationRate(int param){
		super.setMutationRate(param); // TODO check param = a_rate into operate
	}
	
	public void setAlpha(double alpha)
	{
		this.alfa=alpha;
	}
}