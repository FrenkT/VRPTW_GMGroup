package com.GMGroup.Genetic;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;



import com.mdvrp.Customer;
import com.mdvrp.Depot;
import com.mdvrp.Instance;
import com.mdvrp.Parameters;

public class MyChromosome {

	private static final boolean DEBUG = true;
	public static final int VEICHLE_MARKER = 0;
	
	private int[] genes;
	
	/**
	 * Costruttore: prende in ingresso una lista di veicoli e costruisce lo "stringone"
	 */
	public MyChromosome(Object[] veichles)
	{
		// Crea un array di dimensione maxCustomers+maxVeichles e riempilo con i risultati
		this.genes = new int[Instance.getInstance().getVehiclesNr() + Instance.getInstance().getCustomersNr()];
		int k = 0;
		for (int i=0;i<veichles.length;i++)
		{
			for (Integer customNumber : (ArrayList<Integer>)veichles[i])
			{
				genes[k]=customNumber;
				k++;
			}
			genes[k]=VEICHLE_MARKER;
			k++;
		}
	}
	
	public int[] getGenes()
	{
		return this.genes;
	}
	
	@Override
	public String toString()
	{
		StringBuffer fb = new StringBuffer();
		for(int i : genes)
		{
			if (i==VEICHLE_MARKER)
				fb.append(" | ");
			else
				fb.append(" "+i+" ");
		}
		
		return fb.toString();
	}
	
	public static void main(String[] args) throws Exception
	{
		Parameters parameters = new Parameters();
		
		parameters.updateParameters(args);
		if(parameters.getInputFileName() == null){
			System.out.println("You must specify an input file name");
			return;
		}
		Instance.setInstance(parameters);
		Instance instance = Instance.getInstance();
		instance.populateFromHombergFile(parameters.getInputFileName());
		
		// Genero una popolazione iniziale
		Point2D depPos = new Point2D.Double(instance.getDepot(0).getXCoordinate(),instance.getDepot(0).getYCoordinate());
		Date dti = new Date();
		MyChromosome cw = generateInitialChromosome();
		String res = cw.toString();
		Date dte = new Date();
		System.out.println("\nImpiegato: "+(dte.getTime()-dti.getTime())/1000d + " s");
		System.out.println(res);
		
		//System.out.println(Accelerator.getInstance().getDistanceBetween(100,100));
		System.out.println("Total len: "+GMObjectiveFunction.evaluate(cw));
		
	}
	
	/** 
	 * 
	 * Genera una soluzione iniziale in modo pseudo-randomico: al momento si rispettano tutti i constraints.
	 * @throws Exception 
	 *  
	 **/
	public static MyChromosome generateInitialChromosome() throws Exception
	{
		Random rnd = new Random();
		rnd.setSeed((new Date()).getTime());
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
		}
		
		if (customers.size()>0)
		{
			System.err.println("Unassigned "+customers.size());
			throw new Exception("Some customers haven't been assigned to any vehichle");
		}
		
		toRemove.clear();
		
		// Costruisco l'oggetto chromosome a partire dai risultati ottenuti.
		return new MyChromosome(veichles);
		
	}
	
}
