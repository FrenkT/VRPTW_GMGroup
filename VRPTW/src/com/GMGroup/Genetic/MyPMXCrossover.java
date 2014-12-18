package com.GMGroup.Genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.ICompositeGene;
import org.jgap.IGeneticOperatorConstraint;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.RandomGenerator;
import org.jgap.impl.CrossoverOperator;
import org.jgap.impl.IntegerGene;

public class MyPMXCrossover extends CrossoverOperator{
	
	private double crossoverRate=0.5; 
	private int windowlength;
	
	
//	public MyPMXCrossover(Configuration a_configuration)
//			throws InvalidConfigurationException {
//		super(a_configuration);
//		m_crossoverRate
//	}
	
	public MyPMXCrossover(Configuration a_configuration,double rate, int windowlength) throws InvalidConfigurationException {
		super(a_configuration);
		if (rate<0)
			throw new IllegalArgumentException("Invalid rate specified");
		if (windowlength<0)
			throw new IllegalArgumentException("Invalid windowlength specified");
		if(windowlength>a_configuration.getChromosomeSize())
			throw new IllegalArgumentException("Windowlength too large");
		
		
		
		
		this.crossoverRate=rate;
		this.windowlength=windowlength;
	}
	
	@Override
	public void operate(final Population a_population, final List a_candidateChromosomes) {
		
		//eseguo il crossover solo sui parents
		int size = Math.min(getConfiguration().getPopulationSize(),a_population.size());
		int numCrossovers = 0;
		numCrossovers = (int) (size/2 * crossoverRate); //numero di volte che eseguo la crossover
//		if (m_crossoverRate >= 0) {
//			numCrossovers = size / m_crossoverRate;
//		} else if (m_crossoverRateCalc != null) {
//			numCrossovers = size / m_crossoverRateCalc.calculateCurrentRate();
//		} else {
//			numCrossovers = (int) (size * m_crossoverRatePercent);
//		}
		RandomGenerator generator = getConfiguration().getRandomGenerator();
		IGeneticOperatorConstraint constraint = getConfiguration().getJGAPFactory().getGeneticOperatorConstraint();
		// For each crossover, grab two random chromosomes, pick a random
		// locus (gene location), and then swap that gene and all genes
		// to the "right" (those with greater loci) of that gene between
		// the two chromosomes.
		// --------------------------------------------------------------
		int index1, index2;
		//seleziono due cromosomi da accoppiare in modo random su tutta la popolazione
		//faccio questa operazione numCrossovers volte
		for (int i = 0; i < numCrossovers; i++) {
			index1 = generator.nextInt(size);
			index2 = generator.nextInt(size);
			IChromosome chrom1 = a_population.getChromosome(index1);
			IChromosome chrom2 = a_population.getChromosome(index2);
			// Verify that crossover is allowed.
			// ---------------------------------
			if (!isXoverNewAge() && chrom1.getAge() < 1 && chrom2.getAge() < 1) {
				// Crossing over two newly created chromosomes is not seen as
				// helpful
				// here.
				// ------------------------------------------------------------------
				continue;
			}
			if (constraint != null) {
				List v = new Vector();
				v.add(chrom1);
				v.add(chrom2);
				if (!constraint.isValid(a_population, v, this)) {
					// Constraint forbids crossing over.
					// ---------------------------------
					continue;
				}
			}
			// Clone the chromosomes.
			// ----------------------
			IChromosome firstMate = (IChromosome) chrom1.clone();//primo figlio
			IChromosome secondMate = (IChromosome) chrom2.clone();//secondo figlio
			// In case monitoring is active, support it.
			// -----------------------------------------
//			if (m_monitorActive) {
//				firstMate.setUniqueIDTemplate(chrom1.getUniqueID(), 1);
//				firstMate.setUniqueIDTemplate(chrom2.getUniqueID(), 2);
//				secondMate.setUniqueIDTemplate(chrom1.getUniqueID(), 1);
//				secondMate.setUniqueIDTemplate(chrom2.getUniqueID(), 2);
//			}
			// Cross over the chromosomes.
			// ---------------------------
			doCrossover(firstMate, secondMate, a_candidateChromosomes,generator);
		}
	}
	
	
	@Override
	protected void doCrossover(IChromosome firstMate, IChromosome secondMate,
			List a_candidateChromosomes, RandomGenerator generator) {
		

        Gene[] firstGenes = firstMate.getGenes();//lista di geni del primo parent
		Gene[] secondGenes = secondMate.getGenes();//lista di geni del secondo parent
		
		Gene[] firstChildGenes = new IntegerGene[firstGenes.length];
		Gene[] secondChildGenes = new IntegerGene[secondGenes.length];
		for(int i=0; i<firstGenes.length; i++){
			try {
				firstChildGenes[i] = new IntegerGene(getConfiguration());
				secondChildGenes[i] = new IntegerGene(getConfiguration());
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		int begin = generator.nextInt(firstGenes.length-windowlength); //prima linea di cross
		int end= begin + windowlength; 
		//int end = begin + generator.nextInt(firstGenes.length - begin); //seconda linea di cross
		
		System.out.println(begin);
		System.out.println(end);
		
		for(int i=begin; i<end; i++){
			firstChildGenes[i].setAllele((int) firstGenes[i].getAllele());
			secondChildGenes[i].setAllele((int) secondGenes[i].getAllele());
		}
		
		List<Integer> tmp= new ArrayList<Integer>();
		for(int i=end; i<firstGenes.length+end; i++){
			int source=(int) firstGenes[i%firstGenes.length].getAllele();
			if(!isContained(source,secondChildGenes,begin,end)){
				tmp.add(source);
			}
		}
		
		for (int i=0;i<tmp.size();i++)
		{
			secondChildGenes[(end+i)%secondChildGenes.length].setAllele(tmp.get(i));
		}
		
		tmp.clear();
		for(int i=end; i<secondGenes.length+end; i++){
			int source=(int) secondGenes[i%secondGenes.length].getAllele();
			if(!isContained(source,firstChildGenes,begin,end)){
				tmp.add(source);
			}
		}
		
		for (int i=0;i<tmp.size();i++)
		{
			firstChildGenes[(end+i)%firstChildGenes.length].setAllele(tmp.get(i));
		}
		

		try {
			firstMate.setGenes(firstChildGenes);
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			secondMate.setGenes(secondChildGenes);
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		a_candidateChromosomes.add(firstMate);
		a_candidateChromosomes.add(secondMate);
		
		
	}
	
	private boolean isContained(int source, Gene[] genes, int start, int end){
		for(int i=start; i<end; i++){
			if((int) genes[i].getAllele()==source){
				return true;
			}
		}
		return false;
	}

	public double getCrossoverRate() {
		return crossoverRate;
	}

	public void setCrossoverRate(double crossoverRate) {
		if (crossoverRate<0)
			throw new IllegalArgumentException("Invalid rate specified");
		this.crossoverRate = crossoverRate;
	}

	public int getWindowlength() {
		return windowlength;
	}

	public void setWindowlength(int windowlength) {
		if (windowlength<0)
			throw new IllegalArgumentException("Invalid windowlength specified");
		if(windowlength>getConfiguration().getChromosomeSize())
			throw new IllegalArgumentException("Windowlength too large");
		this.windowlength = windowlength;
	}

}
