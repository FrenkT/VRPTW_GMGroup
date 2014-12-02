package com.GMGroup.Genetic;

import java.util.List;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.ICompositeGene;
import org.jgap.InvalidConfigurationException;
import org.jgap.RandomGenerator;
import org.jgap.impl.CrossoverOperator;

public class MyCrossoverOperator extends CrossoverOperator {

	public MyCrossoverOperator(Configuration a_configuration,
			double a_crossoverRatePercentage)
			throws InvalidConfigurationException {
		super(a_configuration, a_crossoverRatePercentage);
	}

	@Override
	protected void doCrossover(IChromosome firstMate, IChromosome secondMate,
			List a_candidateChromosomes, RandomGenerator generator) {

		Gene[] firstGenes = firstMate.getGenes();
		Gene[] secondGenes = secondMate.getGenes();
		int locus = generator.nextInt(firstGenes.length);
		// Swap the genes.
		// ---------------
		Gene gene1;
		Gene gene2;
		Object firstAllele;
		for (int j = locus; j < firstGenes.length; j++) {
			// Make a distinction for ICompositeGene for the first gene.
			// ---------------------------------------------------------
			int index = 0;
			gene1 = firstGenes[j];
			gene2 = secondGenes[j];
			if (m_monitorActive) {
				gene1.setUniqueIDTemplate(gene2.getUniqueID(), 1);
				gene2.setUniqueIDTemplate(gene1.getUniqueID(), 1);
			}
			firstAllele = gene1.getAllele();
			gene1.setAllele(gene2.getAllele());
			gene2.setAllele(firstAllele);
			
		}
		// Add the modified chromosomes to the candidate pool so that
		// they'll be considered for natural selection during the next
		// phase of evolution.
		// -----------------------------------------------------------
		a_candidateChromosomes.add(firstMate);
		a_candidateChromosomes.add(secondMate);
	}
}
