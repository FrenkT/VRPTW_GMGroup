package com.GMGroup.Genetic;

import java.awt.geom.Point2D;

import org.jgap.Gene;
import org.jgap.IChromosome;

import com.mdvrp.Customer;
import com.mdvrp.Depot;
import com.mdvrp.Instance;

public class GMObjectiveFunction {

	// int[] genes = {0,14,17,0,12,85,23,0};
	// [0,14,17,0,12,85,23,0]	
	// 0 - 14  > 35.35
	// 14 - 17 > 5 
	// 17 - 0  > 40.31
	// 0 - 12  > 32.38
	// 12 - 85 > 59.23
	// 85 - 23 > 30.80
	// 23 - 0 > 45.04
	// 248.71
	
	/**
	 * Evaluates the cost of the routes, given a Chromosome.
	 * @param e
	 * @return
	 */
	public static double evaluate(IChromosome e)
	{
		
		double totalLen = 0;
		Gene[] genes = e.getGenes();
		Accelerator acc = Accelerator.getInstance();
		
		// If we have at least one item, add the distance from depot to customer
		int fromIndex = 0;
		int toIndex = (int)genes[0].getAllele();
		if (toIndex<0)
			toIndex=0;
		
		if (genes.length>0)
			totalLen+=acc.getDistanceBetween(fromIndex, toIndex);		
		
		for (int i=0;i<(genes.length-1);i++)
		{
			fromIndex = (int)genes[i].getAllele();
			toIndex = (int)genes[i+1].getAllele();
			
			if (fromIndex<0)
				fromIndex = 0;
			
			if (toIndex<0)
				toIndex = 0;
			
			totalLen+=acc.getDistanceBetween(fromIndex, toIndex);
		}
		
		// Add the distance to get back to bepot only if the last separator wasn't a depot itself
		fromIndex=(int)genes[genes.length-1].getAllele();
		toIndex = 0;
		
		if (fromIndex<0)
			fromIndex=0;
		
		totalLen+=acc.getDistanceBetween(fromIndex, 0);
		
	
		return totalLen;
	}
	
	
}
