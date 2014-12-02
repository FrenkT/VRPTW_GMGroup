package com.GMGroup.Genetic;

import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.IGeneConstraintChecker;

import com.mdvrp.Instance;

public class MyChromosomeContraintChecker implements IGeneConstraintChecker {

	@Override
	public boolean verify(Gene a_gene, Object a_alleleValue, IChromosome a_chromosome, int a_geneIndex) {
		
		Gene[] genes = a_chromosome.getGenes();

		double currentCost = 0;
		double threeshold = Instance.getInstance().getCapacity(0, 0);
		// Controlla che il gene non compaia due volte
		for(int i = 0; i<genes.length;i++)
		{
			if ((int)genes[i].getAllele() == (int)a_gene.getAllele() && i != a_geneIndex && (int)genes[i].getAllele()!=MyChromosomeFactory.VEICHLE_MARKER)
				return false;
			
			// Se mi trovo a scansionare un veicolo, controlla che il costo calcolato fino ad ora
			// sia minore della soglia massima e resettalo.
			if ((int)genes[i].getAllele()==MyChromosomeFactory.VEICHLE_MARKER)
			{
				if (currentCost>threeshold)
					return false;
				else
					currentCost=0;
			}
			
		}
		
		// Check last veichle
		if (currentCost>threeshold)
			return false;
		else
			currentCost=0;
		
		// If you get here, everything is ok!
		return true;
		
	}

}
