package com.GMGroup.Genetic;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;






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

public class MyChromosome implements IChromosome{

	private static final boolean DEBUG = true;
	public static final int VEICHLE_MARKER = 0;
	
	private Gene[] genes;
	
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
	
	
	public Object clone()
	{
		return null;
	}
	
	/**
	 * Constructor: takes as argument an array of Vehicles. Each one is an arraylist of customers.
	 */
	public MyChromosome(Object[] veichles)
	{
		// Crea un array di dimensione maxCustomers+maxVeichles e riempilo con i risultati
		this.genes = new MyIntGene[Instance.getInstance().getVehiclesNr() + Instance.getInstance().getCustomersNr()];
		int k = 0;
		for (int i=0;i<veichles.length;i++)
		{
			for (Integer customNumber : (ArrayList<Integer>)veichles[i])
			{
				genes[k] = new MyIntGene(customNumber);
				k++;
			}
			genes[k] = new MyIntGene(VEICHLE_MARKER);
			k++;
		}
	}
	
	@Override
	public String toString()
	{
		StringBuffer fb = new StringBuffer();
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
		
	
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getUniqueID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUniqueIDTemplate(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void setUniqueIDTemplate(String arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getAge() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getApplicationData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Configuration getConfiguration() {
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
	public Gene getGene(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gene[] getGenes() {
		return this.genes;
	}

	@Override
	public void increaseAge() {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void increaseOperatedOn() {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSelectedForNextGeneration() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int operatedOn() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void resetAge() {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetOperatedOn() {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAge(int arg0) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setApplicationData(Object arg0) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
		
	}

	@Override
	public void setConstraintChecker(IGeneConstraintChecker arg0)
			throws InvalidConfigurationException {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFitnessValue(double arg0) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFitnessValueDirectly(double arg0) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
		
	}

	@Override
	public void setGenes(Gene[] arg0) throws InvalidConfigurationException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
		
	}

	@Override
	public void setIsSelectedForNextGeneration(boolean arg0) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
		
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
