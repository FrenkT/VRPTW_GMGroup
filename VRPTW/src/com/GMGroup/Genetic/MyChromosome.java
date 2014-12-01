package com.GMGroup.Genetic;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;









import org.jgap.BaseChromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.IGeneConstraintChecker;
import org.jgap.InvalidConfigurationException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.mdvrp.Customer;
import com.mdvrp.Depot;
import com.mdvrp.Instance;
import com.mdvrp.Parameters;

public class MyChromosome extends BaseChromosome{

	private static final boolean DEBUG = true;
	public static final int VEICHLE_MARKER = 0;
	
	/** 
	 * 
	 * Genera una soluzione iniziale in modo pseudo-randomico: al momento si rispettano tutti i constraints.
	 * @throws Exception 
	 *  
	 **/
	public static MyChromosome generateInitialChromosome(Configuration conf) throws Exception
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
		return new MyChromosome(conf,veichles);
		
	}
	
	
	public Object clone()
	{
		try {
			MyChromosome cloned = new MyChromosome(getConfiguration(), new Object[] {});
			for(int i=0;i<getGenes().length;i++)
			{
				cloned.setGene(i, getGene(i));
			}
			return cloned;
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Constructor: takes as argument an array of Vehicles. Each one is an arraylist of customers.
	 * @throws InvalidConfigurationException 
	 */
	public MyChromosome(Configuration conf, Object[] veichles) throws InvalidConfigurationException
	{
		super(conf);
		// Crea un array di dimensione maxCustomers+maxVeichles e riempilo con i risultati
		Gene[] genes = new MyIntGene[Instance.getInstance().getVehiclesNr() + Instance.getInstance().getCustomersNr()];
		int k = 0;
		for (int i=0;i<veichles.length;i++)
		{
			for (Integer customNumber : (ArrayList<Integer>)veichles[i])
			{
				genes[k] = new MyIntGene(conf,customNumber);
				k++;
			}
			genes[k] = new MyIntGene(conf,VEICHLE_MARKER);
			k++;
		}
		
		super.setGenes(genes);
	}
	
	@Override
	public String toString()
	{
		StringBuffer fb = new StringBuffer();
		Gene[] genes = super.getGenes();
		for(int i = 0; i < genes.length; i++)
		{
			int geneVal = (int)genes[i].getAllele();
			if (geneVal==VEICHLE_MARKER)
				fb.append(" | ");
			else
				fb.append(" "+geneVal+" ");
		}
		
		return fb.toString();
	}


	@Override
	public void cleanup() {
		try {
			super.setGenes(null);
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	public Object getApplicationData() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public double getFitnessValue() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public double getFitnessValueDirectly() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public boolean isSelectedForNextGeneration() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void setApplicationData(Object arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setConstraintChecker(IGeneConstraintChecker arg0)
			throws InvalidConfigurationException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setFitnessValue(double arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setFitnessValueDirectly(double arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setIsSelectedForNextGeneration(boolean arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public boolean isHandlerFor(Object arg0, Class arg1) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public Object perform(Object arg0, Class arg1, Object arg2)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	
}
