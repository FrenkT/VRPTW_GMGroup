package com.GMGroup.Genetic;

import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

import com.mdvrp.Instance;

public class MyFitnessFunction extends FitnessFunction {

	public final static double penaltyScale = 10; // TODO
	
	
	/**
	 * Simply calling the Objective function in an inverse way
	 */
	@Override
	protected double evaluate(IChromosome arg) {
		
		Gene[] gens = arg.getGenes(); 
		Accelerator acc = Accelerator.getInstance();
		double actualCost = 0;
		int numbOfPenalties = 0;
		// Penalizzazione su capacità
		for (int i = 0;i<gens.length;i++)
		{
			// Controllo che l'elemento attuale non sia un DEPOT. In tal caso
			// procedo in avanti
			int idCustomerA=(int)gens[i].getAllele();
			if (idCustomerA<=0 || i==(gens.length-1))
			{
				if (i==(gens.length-1) && idCustomerA>0)
					actualCost+=acc.getCustomerDemand(idCustomerA);
				
				// Ho raggiunto la fine di un route o la fine del cromosoma
				// Controllo che fino ad ora non abbia sforato
				// e resetto
				if (actualCost>Instance.getInstance().getCapacity(0, 0))
				{
					// Abbiamo ecceduto la capacità massima
					// Penalizzo 
					numbOfPenalties++;
				}
				actualCost=0;
				continue;
			}
			actualCost+=acc.getCustomerDemand(idCustomerA);
		}
		
		
		double res=0;
		if (numbOfPenalties>0)
			res = 1/(GMObjectiveFunction.evaluate(arg)*numbOfPenalties*penaltyScale);
		else
		{
			res = 1/GMObjectiveFunction.evaluate(arg);
		}
		return res;
	}	
	
}
