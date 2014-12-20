package com.GMGroup.Genetic;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import com.mdvrp.Customer;
import com.mdvrp.Depot;

/**
 * This class is an helper object to accelerate execution of the scripts
 * It contains some pre-calculated values, like the distances among customers / depot,
 * an ordered version of customers.
 * @author Webking
 *
 */
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
	
	/**
	 * Returns the distance between customer with ID idA and customer with id idB
	 * @param idA
	 * @param idB
	 * @return
	 */
	public double getDistanceBetween(int idA,int idB)
	{		
		if (distances == null)
		{
			// Mai stata inizializzata, inizializzala
			precalculateDistances();
		}
		
		if (idA<=0)
			idA = 0;
		if (idB <=0)
			idB=0;
		
		return distances[idA][idB];
		
	}

	/**
	 * Dato un ID utente, ritorna la sua DOMANDA. 
	 * Nota che internamente la lista di utenti non contiene il depot. Quindi memorizziamo in posizione
	 * 0 della lista il cliente con id 1.
	 * @param customerIndex
	 * @return
	 */
	public double getCustomerDemand(int customerIndex)
	{
		if (customerIndex<0)
			throw new IllegalArgumentException();

		return customers[customerIndex-1].getCapacity();
	}
	
	/**
	 * Simply pre-caclulates distances among customers/depot. Those distances are stored within the accelerator and can be grabbed 
	 * by using getDistanceBetween() method.
	 */
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
					distances[i][j]=0;
				else if (j==0)
				{
					to = depot.getLocation();
					//distances[i][j]=from.distance(to); //FIXME Changing this to the Prof version. I disagree, but let's be consistent
					distances[i][j] = Math.sqrt(Math.pow(from.getX() - to.getX(), 2)
							+ Math.pow(from.getY() - to.getY(), 2));
					distances[i][j] = Math.floor(distances[i][j] * 10) / 10;
				}
				else
				{
					to = customers[j-1].getLocation();
					//distances[i][j]=from.distance(to);
					distances[i][j] = Math.sqrt(Math.pow(from.getX() - to.getX(), 2)
							+ Math.pow(from.getY() - to.getY(), 2));
					distances[i][j] = Math.floor(distances[i][j] * 10) / 10;
				}
			}			
		}
	}

	/**
	 * Initializes the Accelerator: sets the list of customers ordered by their ID and the main depot.
	 * @param customers
	 * @param depot
	 */
	public void setData(ArrayList<Customer> customers, Depot depot) {
		// Store a local copy for customers and depot
		this.customers = (Customer[])customers.toArray(new Customer[0]);
		this.depot = depot;
	}


	/**
	 * Returns the Point2D location, pointing to the depot.
	 * @return
	 */
	public Point2D getDepotLocaltion() {
		return depot.getLocation();
	}

	/**
	 * Returns the depot "closing" time. All vehicles must get back within this time.
	 * @return
	 */
	public double getDepotDueTime() {
		return depot.getEndTw();
	}

	public Customer getCustomer(int i) {
		return customers[i-1];
	}
}
