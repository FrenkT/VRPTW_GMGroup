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
		
		// TIP: We used 0 as Marker. This means we can simply calculate distances between depot and customers directly accessing the pre-calculated distances matrix.
		for (int i=0;i<(genes.length-1);i++)
		{
			// Case: double depot
			if (genes[i].equals(genes[i+1]))
				continue;
			// Otherwise: calculate distance
			totalLen+=acc.getDistanceBetween((int)genes[i].getAllele(), (int)genes[i+1].getAllele());
		}
	
		return totalLen;
	}
	
	
}
