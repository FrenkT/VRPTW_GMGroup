package com.GMGroup.Genetic;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;













import java.util.UUID;

import org.jgap.BaseChromosome;
import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.IGeneConstraintChecker;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.IntegerGene;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.mdvrp.Customer;
import com.mdvrp.Depot;
import com.mdvrp.Instance;
import com.mdvrp.Parameters;
import com.mdvrp.Vehicle;

public class MyChromosomeFactory {

	private static final boolean DEBUG = false;
	private static MyChromosomeFactory instance;
	private Random rnd;
	private double MAX_WAITABLE_TIME_RATIO = 0.2;
	private double MAX_WAITING_VEHICLE_NUMBER_RATIO = 2;
	
	public static MyChromosomeFactory getInstance()
	{
		if (instance==null)
			instance = new MyChromosomeFactory();
		return instance;
	}
	
	private MyChromosomeFactory()
	{
		rnd= new Random((int)(UUID.randomUUID()).hashCode());
	}
	
	//public static final int VEICHLE_MARKER = 0;
	//private static MyChromosomeContraintChecker constraintChecker = new MyChromosomeContraintChecker();
	/** 
	 * 
	 * Genera una soluzione iniziale in modo pseudo-randomico: al momento si rispettano tutti i constraints.
	 * @throws Exception 
	 *  
	 **/
	public IChromosome generateInitialFeasibleChromosome(Configuration conf) throws Exception
	{
		
		Point2D depot = Accelerator.getInstance().getDepotLocaltion();
		double depotDueTime = Accelerator.getInstance().getDepotDueTime();
		
		// Copia i clienti in una nuova struttura randomizzandone l'ordine
		ArrayList<Customer> customers = new ArrayList<Customer>();
		for (Customer c : Instance.getInstance().getDepot(0).getAssignedcustomers())
		{
			if (customers.size()!=0)
			{
				int posToInsert = rnd.nextInt(customers.size());
				customers.add(posToInsert,c);
			}
			else
				customers.add(c);
		}
		
		float totalDemand = 0;
		for (int i=0;i<customers.size();i++)
			totalDemand+=customers.get(i).getCapacity();
		int maxVeichles = Instance.getInstance().getVehiclesNr();
		int minVeichles = (int)Math.ceil( totalDemand / Instance.getInstance().getCapacity(0, 0));
		double capacityPerVeichle = Instance.getInstance().getCapacity(0, 0);
		if ((maxVeichles-minVeichles)<1)
			throw new Exception("Capacity constraints too strict. No possible solution could be found.");
		
		// Crea una lista di veicoli, per ognuno di questi crea una lista di clienti da trasportare
		Object[] veichles = new Object[maxVeichles];
		for (int i=0;i<maxVeichles;i++)
			veichles[i]=new ArrayList<Integer>();
		
		ArrayList<Customer> toRemove = new ArrayList<Customer>();
		// Per ogni veicolo, scegliamo un insieme di clienti da caricare fino a riempire le capacità e a rispettare le tw
		if (DEBUG)
			System.out.println("Cliente #\tFROM\tTO\tDISTANCE\tElapsed\tService\tAttesa");
		for (int i=0;i<maxVeichles && customers.size()>0;i++)
		{
			double maxWaitableInterval= Accelerator.getInstance().getDepotDueTime();
			if (i<(maxVeichles/MAX_WAITING_VEHICLE_NUMBER_RATIO))
				maxWaitableInterval *= MAX_WAITABLE_TIME_RATIO ;
			
			if (DEBUG)
				System.out.println("============Veicolo "+i+" =============");
			ArrayList<Integer> veichle = (ArrayList<Integer>)veichles[i];
			float cost = 0;
			float elapsedTime = 0;
			
			// Scegli un cliente a caso considerando il suo peso
			int nextCustomerId = rnd.nextInt(customers.size());
			Customer nextCustomer = customers.get(nextCustomerId);
			Point2D lastCustomerPosition = null;
			
			double distanceOrTime = 0;
			// Scansione circolare
			for (int w = nextCustomerId;w<customers.size()+nextCustomerId;w++)
			{
				nextCustomer = customers.get(w % customers.size());
				if ((nextCustomer.getCapacity() + cost ) < capacityPerVeichle && !toRemove.contains(nextCustomer.getNumber()+1))
				{
					String toPrint = "";
					
					// Se è il primo customer, parti dal depot
					if (veichle.size()==0)
					{
						// Calcolo distanza dal depot
						lastCustomerPosition = new Point2D.Double(nextCustomer.getXCoordinate(),nextCustomer.getYCoordinate());
						distanceOrTime = depot.distance(lastCustomerPosition);
						//double time = distanceOrTime * 1;
						toPrint = (nextCustomer.getNumber()+1)+";"+depot+";"+lastCustomerPosition+";"+distanceOrTime+";"+elapsedTime;
					}
					else
					{
						// Altrimenti parti dall'elemento precedentemente salvato
						Point2D tmpPoint = new Point2D.Double(nextCustomer.getXCoordinate(),nextCustomer.getYCoordinate());
						distanceOrTime = lastCustomerPosition.distance(tmpPoint);
						toPrint = (nextCustomer.getNumber()+1)+";"+lastCustomerPosition+";"+tmpPoint+";"+distanceOrTime+";"+elapsedTime;
						lastCustomerPosition = tmpPoint;
					}
					
					double timeToWait = nextCustomer.getStartTw() - elapsedTime - distanceOrTime;
					if (timeToWait<0)
						timeToWait=0;
					
					// Aggiungilo solo se rispetta le tw
					if ((elapsedTime + distanceOrTime) <= nextCustomer.getEndTw() && 
							// Abbiamo tempo per tornare indietro al depot?
							(elapsedTime + distanceOrTime + timeToWait + nextCustomer.getServiceDuration() + depot.distance(new Point2D.Double(nextCustomer.getXCoordinate(), nextCustomer.getYCoordinate()))<depotDueTime))
					{
						toPrint += ";" + nextCustomer.getServiceDuration()+";"+((timeToWait>0 ? timeToWait :"No wait"));
						if(DEBUG)
							System.out.println(toPrint);
						if (timeToWait > 0) 
						{
							// Se il customer corrente richiede un'attesa maggiore della massima concepita, passa al prossimo
							if (timeToWait > maxWaitableInterval)
							{
								if (DEBUG)
									System.err.println("Veichle "+i+" can't wait for customer "+w+". Moving forward...");
								continue;
							}
							// Siamo in attesa
							elapsedTime += timeToWait;							
						}
						elapsedTime+=nextCustomer.getServiceDuration();
						
						
						veichle.add(nextCustomer.getNumber()+1);
						cost+=nextCustomer.getCapacity();
						
						// Remove the added customer
						toRemove.add(nextCustomer);
					}
					else
						continue;
					
					
				}
			}
			customers.removeAll(toRemove);
			toRemove.clear();
		}
		
		if (customers.size()>0)
		{
			System.err.println("Unassigned "+customers.size());
			throw new Exception("Some customers haven't been assigned to any vehichle");
		}
		
		
		int k = 0;
		Gene[] genes = new IntegerGene[Instance.getInstance().getCustomersNr()+Instance.getInstance().getVehiclesNr()-1];
		for (int i=0;i<veichles.length;i++)
		{
			for (Integer customNumber : (ArrayList<Integer>)veichles[i])
			{
				genes[k] = new IntegerGene(conf);
				genes[k].setAllele(customNumber);
				k++;
			}
			if (i<(veichles.length-1))
			{
				// Imposta un marker per il veicolo. Lo riconosciamo perchè negativo rispetto agli altri
				genes[k] = new IntegerGene(conf);
				genes[k].setAllele(-i);
				k++;
			}
		}
		veichles = null;
		
		// Costruisco l'oggetto chromosome a partire dai risultati ottenuti.
		Chromosome res = new Chromosome(conf,genes);
		//res.setConstraintChecker(constraintChecker);
		return res;
		
	}
	
	public static String PrintChromosome(IChromosome chromosome)
	{
		StringBuffer fb = new StringBuffer();
		Gene[] genes = chromosome.getGenes();
		for(int i = 0; i < genes.length; i++)
		{
			int geneVal = (int)genes[i].getAllele();
			if (geneVal<=0)
				fb.append(" | ");
			else
				fb.append(" "+geneVal+" ");
		}
		
		return fb.toString();
	}

	
}
