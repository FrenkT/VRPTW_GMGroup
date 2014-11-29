package com.GMGroup.Genetic;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.mdvrp.Customer;
import com.mdvrp.Depot;

public class Accelerator {

	private static Accelerator instance;
	private double[][] distances;
	private Customer[] customers;
	private Depot depot;
	
	
	public static Accelerator getInstance()
	{
		if (instance==null)
			instance = new Accelerator();
		return instance;
	}
	
	// Matrice dei costi
	public double getDistanceBetween(int idA,int idB)
	{		
		if (distances == null)
		{
			// Mai stata inizializzata, inizializzala
			precalculateDistances();
		}
		
		return distances[idA][idB];
		
	}

	private void precalculateDistances()
	{		
		// To be invoked only if customers and depot are filled up
		if (customers == null || depot == null)
			throw new NullPointerException("Cannot calculate distances. Please first fill in customers and depot by using SetData()");
		
		// Alloca abbastanza spazio
		int width = customers.length+1;
		distances = new double[width][width];
		
		for (int i=0;i<width;i++)
		{
			Point2D from = null;
			if (i==0)
			{
				from = depot.getLocation();
			}
			else
				from = customers[i-1].getLocation();
			
			for (int j=0;j<width;j++)
			{
				Point2D to = null;
				// Prima iterazione: calcola la distanza tra depot e customers
				if (i==j)
					distances[i][j]=Double.MAX_VALUE;
				else if (j==0)
				{
					to = depot.getLocation();
					distances[i][j]=from.distance(to);
				}
				else
				{
					to = customers[j-1].getLocation();
					distances[i][j]=from.distance(to);
				}
			}			
		}
	}

	public void setData(ArrayList<Customer> customers, Depot depot) {
		// Store a local copy for customers and depot
		this.customers = (Customer[])customers.toArray(new Customer[0]);
		this.depot = depot;
	}


	public Point2D getDepotLocaltion() {
		return depot.getLocation();
	}

	public double getDepotDueTime() {
		return depot.getEndTw();
	}
	
	
}
