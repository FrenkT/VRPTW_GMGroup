package com.GMGroup.Genetic;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.IUniversalRateCalculator;
import org.jgap.InvalidConfigurationException;
import org.jgap.RandomGenerator;
import org.jgap.impl.SwappingMutationOperator;

public class KChainMutationOperator extends SwappingMutationOperator{

	public KChainMutationOperator() throws InvalidConfigurationException {
		super();
		// TODO Auto-generated constructor stub
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
	    
	    
	    Gene[] genes = a_chrom.getGenes();

	    first = a_generator.nextInt(a_chrom.size()); //scelgo a caso il primp gene della k-chain
	    
	    from= first;
	    Gene from_value = genes[from];

	    
	    //Qui effettuo il k-chain, con k=4
	    
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

	    //try {
	    	//a_chrom.setGenes(genes); // modified chromosome 
	    //}
	    //catch (InvalidConfigurationException cex) {
	    //  throw new Error("Gene type not allowed by constraint checker", cex);
	    //}
	    
	    /*
	    int toIndex = 0;
	    Gene[] genes = a_chrom.getGenes();
	    int fromIndex= a_generator.nextInt(a_chrom.size());
	    int firstIndex = fromIndex;
	    
	    // Mantain a reference to the first swapped item
	    Gene tmp = genes[fromIndex];
	    
	    for (int i=0;i<a_rate;i++)
	    {
	    	toIndex = a_generator.nextInt(a_chrom.size());
	    	tmp = genes[toIndex];
	    	genes[toIndex] = genes[fromIndex];
	    	fromIndex = toIndex;
	    	MyChromosomeFactory.PrintChromosome(a_chrom);
	    }
	    genes[firstIndex] = tmp;
	    */
	    return a_chrom;
	}
	

}
