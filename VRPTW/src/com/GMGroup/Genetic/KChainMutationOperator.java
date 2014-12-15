package com.GMGroup.Genetic;

import java.util.ArrayList;
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
	
	//default value= 0.04
	/**
	 * Expresses which divergence threshold
	 */
	private double parameter=0.015;

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
		//MyFitnessFunction valuta= new MyFitnessFunction();
		double media=0;
		for(int i=0; i<a_candidateChromosomes.size(); i++){
			IChromosome c= (IChromosome) a_candidateChromosomes.get(i);
			media+=c.getFitnessValue();
			//System.out.println("Fitness:" + c.getFitnessValue());
			}
		media= media/a_candidateChromosomes.size(); //ho la media di tutte le fitness function di un tutti i cromosomi della lista
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
		int size = a_population.size()/2;
		/**
		 * Applico la mutazione solo ai parents che hanno una fitness molto simile alla media
		 */
		int n_mutati=0;
		for (int i = 0; i < size; i++) {
			IChromosome x = a_population.getChromosome(i);
			double scarto= media/x.getFitnessValue();
			scarto=1-Math.abs(scarto);
			//System.out.println("Scarto: " + scarto);
			
			if(Math.abs(scarto)<parameter){ //bisogna parametrizzare quel valore
				// This returns null if not mutated:
				IChromosome xm = operate(x, currentRate, generator);//qui eseguo la mutazione
				if (xm != null) {
					a_candidateChromosomes.add(xm);
				}
				n_mutati++;
			}
				
		}
		System.out.println("N° cromosomi mutati: " + n_mutati);
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
	
	/**
	 * Set the number of genes to be mutated into the chromosome. Param = 4 means 
	 * 4 genes are going to be swapped.
	 */
	@Override
	public void setMutationRate(int param){
		super.setMutationRate(param);
	}
	

	/**
	 * Set the divergence parameter. This mutation will be applied
	 * to all chromosomes which lies between +divergence and -divergence.
	 * @param parameter
	 */
	public void setParameter(double parameter)
	{
		if (parameter<0 || parameter > 1)
			throw new IllegalArgumentException("Invalid parameter specififed");
		
		this.parameter=parameter;
	}
}
