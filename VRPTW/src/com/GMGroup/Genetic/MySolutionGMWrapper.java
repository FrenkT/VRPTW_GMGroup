package com.GMGroup.Genetic;

import java.util.List;

import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.IntegerGene;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.TabuSearch.MySolution;
import com.mdvrp.Cost;
import com.mdvrp.Customer;
import com.mdvrp.Instance;
import com.mdvrp.Route;
import com.mdvrp.Vehicle;

/**
 * Wrapper class: adapts a chromosome to the MySolution object and vice-versa.
 * It's able to incapsulate a chromosome, and return the given chromosome back.
 * @author Webking
 *
 */
public class MySolutionGMWrapper extends MySolution {

	private IChromosome chromosome;
	/**
	 * This constructor will create a solution object to be used into the TabuSearch
	 * by looking at a given chromosome. Note that, applying a tabusearch on this object,
	 * won't make any change to the chromosome unless the <b>toChromosome()</b> method is
	 * called.
	 * @param chrom
	 */
	public MySolutionGMWrapper(IChromosome chrom)
	{
		if (chrom==null)
			throw new IllegalArgumentException("Error: cannot create a solution wrapper with a null chromosome");
		
		if (chrom.getGenes().length<1)
			throw new IllegalArgumentException("Error: the chromosome must contain at least one gene");
		
		this.chromosome=chrom;
		cost = new Cost();
		setInstance(Instance.getInstance());
		initializeRoutes(instance);  // Simply initialize data structures: we'll set up a set of routes for each depot. For each depot we have j veichles and so j routes. Each route is then assigned
									 // to a veichle.
		fillRoutes(chrom);
		// used for input routes from file
		alpha 	= 1;
    	beta 	= 1;
    	gamma	= 1;
    	delta	= 0.005;
    	upLimit = 10000000;
    	resetValue = 0.1;
    	feasibleIndex = 0;
    	MySolution.setIterationsDone(0);
    	Bs = new int[instance.getCustomersNr()][instance.getVehiclesNr()][instance.getDepotsNr()];		
	}
	
	private void fillRoutes(IChromosome chrom) {
		
		Gene[] g = chrom.getGenes();
		
		int routeN = 0;
		Route currentRoute = routes[0][routeN];
		for (int i =0;i<g.length;i++)
		{
			if ((int)g[i].getAllele()<=0)
			{
				routeN++;
				currentRoute = routes[0][routeN];
				continue;
			}
			// Ottengo il customer dalla lista del depot	
			Customer customer = Accelerator.getInstance().getCustomer((int)g[i].getAllele());
			currentRoute.addCustomer(customer);
		}
		evaluateRoute(currentRoute);
	}

	@Override
	public void initializeRoutes(Instance instance) {
		// Ci occupiamo di creare le strutture dati Route per questo oggetto
		routes = new Route[1][instance.getVehiclesNr()];
		
		// Abbiamo un solo depot per definizione
		for (int i=0;i<instance.getVehiclesNr();i++)
		{
			// Creo la route
			routes[0][i]= new Route();
			routes[0][i].setIndex(i);
			routes[0][i].setDepot(instance.getDepot(0));
			
			// TODO
			Cost c = new Cost();
			routes[0][i].setCost(c);
			
			Vehicle v = new Vehicle();
			v.setCapacity(instance.getCapacity(0, 0));
			v.setDuration(instance.getDuration(0, 0));
			routes[0][i].setAssignedVehicle(v);
		}
	}
	
	@Override
	@Deprecated
	public void buildInitialRoutes1(Instance instance) {
		// This method shouldn't be called in this context. Use the fillRoute method
		throw new RuntimeException("This method shouldn't be called in this context. Use the fillRoutes method.");
	}
	
	/**
	 * This method allows you to retrieve a chromosome matching this solution object. All the routes will be
	 * represented into a chromosome according to our representation. The original chromosome (used to create
	 * this wrapper) is accordingly modified so that all the genes are reordered and then it is returned.
	 * @param targetChromosome
	 * @throws IllegalArgumentException
	 * @throws InvalidConfigurationException 
	 */
	public IChromosome toChromosome() throws IllegalArgumentException, InvalidConfigurationException
	{
		/*
		// By accessing the genes directly we will modify the chromosome itself. So be aware:
		// we are changing the original chromosome => THIS IS A MUTATION!
		Gene[] genes = chromosome.getGenes();
		// Start by looking at the routes
		int geneCount = 0;
		for (int i = 0; i < routes[0].length; i++)
		{
			Route r = routes[0][i];
			List<Customer> customers = r.getCustomers();
			for (int j=0;j<customers.size();j++)
			{
				Customer c = customers.get(j);
				genes[geneCount].setAllele(c.getNumber()+1); // Check this!! //TODO //FIXME !!
				geneCount++;
			}
			if (i<(routes[0].length-1)) // Add the separator only if it's not the last vehicle
			{
				// When you reach this point, a vehicle-route has been scanned, so add a separator (<=0)
				genes[geneCount].setAllele(-i);
				geneCount++;
			}
		}
		// Redundant, but clearer.
		try {
			chromosome.setGenes(genes);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		*/
		
		//Gene[] genes = chromosome.getGenes();
		Gene[] genes = new Gene[chromosome.size()];
		// Start by looking at the routes
		int geneCount = 0;
		for (int i = 0; i < routes[0].length; i++)
		{
			Route r = routes[0][i];
			List<Customer> customers = r.getCustomers();
			for (int j=0;j<customers.size();j++)
			{
				Customer c = customers.get(j);
				genes[geneCount] = new IntegerGene(chromosome.getConfiguration());
				genes[geneCount].setAllele(c.getNumber()+1);
						//.setAllele(c.getNumber()+1); // Check this!! //TODO //FIXME !!
				geneCount++;
			}
			if (i<(routes[0].length-1)) // Add the separator only if it's not the last vehicle
			{
				// When you reach this point, a vehicle-route has been scanned, so add a separator (<=0)
				genes[geneCount] = new IntegerGene(chromosome.getConfiguration());
				genes[geneCount].setAllele(-i);
				geneCount++;
			}
		}
		// Redundant, but clearer.
		try {
			chromosome.setGenes(genes);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		return chromosome;
	}
}
