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
import org.jgap.Population;
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
	private double MAX_WAITABLE_TIME_RATIO = 0.08;
	private double MAX_WAITING_VEHICLE_NUMBER_RATIO = 2;
	private Configuration conf;
	
	public static MyChromosomeFactory getInstance(Configuration conf) throws InvalidConfigurationException
	{
		if (instance==null)
			instance = new MyChromosomeFactory(conf);
		return instance;
	}
	
	
	final private Gene[] initialGenes; 
	private MyChromosomeFactory(Configuration conf) throws InvalidConfigurationException
	{
		this.conf = conf;
		rnd= new Random((int)(UUID.randomUUID()).hashCode());
		int genLen = Instance.getInstance().getCustomersNr()+Instance.getInstance().getVehiclesNr()-1;
		initialGenes = new IntegerGene[genLen];
		
		int startAllele=-Instance.getInstance().getVehiclesNr()+2;
		for (int i=0;i<genLen;i++)
		{
			initialGenes[i]=new IntegerGene(conf);
			initialGenes[i].setAllele(startAllele);
			startAllele++;
		}
	}
	
	public static double DEBUG_MEDIA_POPOLAZIONE(Population pop)
	{
		double media = 0;
		int count = 0;
		for (IChromosome cc : pop.getChromosomes())
		{
			for (org.jgap.Gene gg : cc.getGenes())
			{
				media+=(int)gg.getAllele();
				count++;
			}
			
		}
		return media/count;
	}
	
	
	public IChromosome generateInitialRandomChromosome() throws InvalidConfigurationException
	{
		ShuffleArray(initialGenes);
		
		// Costruisco l'oggetto chromosome a partire dai risultati ottenuti.
		Chromosome res = new Chromosome(conf,initialGenes);
		//res.setConstraintChecker(constraintChecker);
		return res;	
	}
	
	private void ShuffleArray(Gene[] array)
	{
	    Gene temp;
	    int index;
	    Random random = new Random();
	    for (int i = array.length - 1; i > 0; i--)
	    {
	        index = random.nextInt(i + 1);
	        temp = array[index];
	        array[index] = array[i];
	        array[i] = temp;
	    }
	}
	
	//public static final int VEICHLE_MARKER = 0;
	//private static MyChromosomeContraintChecker constraintChecker = new MyChromosomeContraintChecker();
	/** 
	 * 
	 * Genera una soluzione iniziale in modo pseudo-randomico: al momento si rispettano tutti i constraints.
	 * @throws Exception 
	 *  
	 **/
	public IChromosome generateInitialFeasibleChromosome() throws Exception
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
			throw new IncompleteSolutionException("Capacity constraints too strict. No possible solution could be found.");
		
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
			double cost = 0;
			double elapsedTime = 0;
			
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
							(elapsedTime + distanceOrTime + timeToWait + nextCustomer.getServiceDuration() + depot.distance(new Point2D.Double(nextCustomer.getXCoordinate(), nextCustomer.getYCoordinate()))<=depotDueTime))
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
						elapsedTime += nextCustomer.getServiceDuration();
						elapsedTime += distanceOrTime;
						
						veichle.add(nextCustomer.getNumber()+1);
						cost += nextCustomer.getCapacity();
						
						// Remove the added customer
						toRemove.add(nextCustomer);
					}			
				}
			}
			customers.removeAll(toRemove);
			toRemove.clear();
		}
		
		if (customers.size()>0)
		{
			System.err.println("Unassigned "+customers.size());
			throw new IncompleteSolutionException("Some customers haven't been assigned to any vehichle");
		}
		
		
		int k = 0;
		Gene[] genes = new Gene[initialGenes.length];
		for (int i=0;i<veichles.length;i++)
		{
			for (Integer customNumber : (ArrayList<Integer>)veichles[i])
			{
				boolean found = false;
				for(Gene g : initialGenes)
				{
					if ((int)g.getAllele()==customNumber)
					{
						genes[k]=g;
						found = true;
						k++;
					}
				}
				if (!found)
					throw new Exception("Gene "+customNumber+" not found into primitive gene generation.");
				//genes[k] = new IntegerGene(conf);
				//genes[k].setAllele(customNumber);
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

	// forse scazza per il magicnumber
	/**
	 * Metodo che controlla se un cromosoma e' feasible oppure no
	 * @param chrom
	 * @return true or false
	 */
	public static boolean getIsChromosomeFeasible(IChromosome chrom)
	{
		double violations[] = getEntitiesOfViolations(chrom);
		return (violations[0] == 0 && violations[2] == 0) ? true : false;
		/*Gene[] gens = chrom.getGenes(); 
		Accelerator acc = Accelerator.getInstance();
		double actualCost = 0;*/
		// Controllo la capacità
		/*for (int i = 0; i < gens.length; i++)
		{
			int idCustomerA = (int)gens[i].getAllele();
			
			if (idCustomerA <= 0 || i == (gens.length - 1))
			{
				if (i == (gens.length - 1) && idCustomerA > 0)
				{
					actualCost += acc.getCustomerDemand(idCustomerA);
				}*/
				// Ho raggiunto la fine di una route o la fine del cromosoma
				// Controllo che fino ad ora non abbia sforato e resetto o ritorno false
				/*if (actualCost > Instance.getInstance().getCapacity(0, 0))
				{
					return false;
				}
				
				actualCost = 0;
				continue;
			}
			
			actualCost += acc.getCustomerDemand(idCustomerA);
		}
		
		Point2D lastCustomerPosition = null;
		Point2D depotPosition = Accelerator.getInstance().getDepotLocaltion();
		double depotDueTime = Accelerator.getInstance().getDepotDueTime();
		double elapsedTime = 0;
		double distanceOrTime = 0;
		double timeToWait = 0;
		int numVeicoli = 1;*/
		// Controllo il rispetto delle time windows
		/*for (int i = 0; i < gens.length; i++)
		{
			int idCustomer = (int)gens[i].getAllele();*/
			//Customer idCustomer = customers.get(nextCustomerId);
			//Instance.getInstance().getDepot(0).getAssignedcustomers()
			
			//if (idCustomer <= 0 /*|| i == (gens.length - 1)*/)
			/*{
				numVeicoli++;
				elapsedTime = 0;
				lastCustomerPosition = null;
				continue;
			}
			
			Customer c = acc.getCustomer(idCustomer);
			double serviceDuration = c.getServiceDuration();
			
			if (lastCustomerPosition == null)
			{
				lastCustomerPosition = new Point2D.Double(c.getXCoordinate(), c.getYCoordinate());
				distanceOrTime = depotPosition.distance(lastCustomerPosition);
			}
			else
			{
				Point2D tmpPoint = new Point2D.Double(c.getXCoordinate(), c.getYCoordinate());
				distanceOrTime = lastCustomerPosition.distance(tmpPoint);
				lastCustomerPosition = tmpPoint;
			}
			
			if ((elapsedTime + distanceOrTime) <= c.getEndTw())
			{	
				timeToWait = c.getStartTw() - elapsedTime - distanceOrTime;
				timeToWait = (timeToWait < 0) ?	0 : timeToWait;
				
				if ((elapsedTime + distanceOrTime + timeToWait + serviceDuration + depotPosition.distance(lastCustomerPosition)) <= depotDueTime)
				{
					elapsedTime += distanceOrTime;
					elapsedTime += timeToWait;
					elapsedTime += serviceDuration;
				}
				else
				{
					return false;
				}
			
			}
			else
			{
				return false;
			}
		}
		
		return true;*/
	}
	
	/**
	 * Metodo che crea un vettore che contiene info sulla natura del cromosoma
	 * 0 -> Numero di veicoli che violano il vincolo sulla capacita'
	 * 1 -> Entita' della violazione totale del cromosoma
	 * 2 -> Numero di veicoli che violano il vincolo sulle time windows
	 * 3 -> Entita' della violazione totale del cromosoma
	 * @param chrom
	 * @return il vettore di double sopra descritto
	 */
	public static double[] getEntitiesOfViolations(IChromosome chrom)
	{
		Gene[] gens = chrom.getGenes(); 
		Accelerator acc = Accelerator.getInstance();
		double actualCost = 0;
		double[] entitiesOfViolations = new double[4];
		
		for (int i = 0; i < entitiesOfViolations.length; i++)
		{
			entitiesOfViolations[i] = 0;
		}
		
		// Controllo la capacita'
		for (int i = 0; i < gens.length; i++)
		{
			int idCustomerA = (int)gens[i].getAllele();
			
			if (idCustomerA <= 0 || i == (gens.length - 1))
			{
				if (i == (gens.length - 1) && idCustomerA > 0)
				{
					actualCost += acc.getCustomerDemand(idCustomerA);
				}
				// Ho raggiunto la fine di una route o la fine del cromosoma
				// Controllo che fino ad ora non abbia sforato e resetto o ritorno false
				if (actualCost > Instance.getInstance().getCapacity(0, 0))
				{
					entitiesOfViolations[0] += 1;
					entitiesOfViolations[1] += (actualCost - Instance.getInstance().getCapacity(0, 0));
				}
				
				actualCost = 0;
				continue;
			}
			
			actualCost += acc.getCustomerDemand(idCustomerA);
		}
		
		Point2D lastCustomerPosition = null;
		Point2D depotPosition = Accelerator.getInstance().getDepotLocaltion();
		double depotDueTime = Accelerator.getInstance().getDepotDueTime();
		double elapsedTime = 0;
		double distanceOrTime = 0;
		double timeToWait = 0;
		int numVeicoli = 1;
		// Controllo il rispetto delle time windows
		for (int i = 0; i < gens.length; i++)
		{
			int idCustomer = (int)gens[i].getAllele();
			
			if (idCustomer <= 0 /*|| i == (gens.length - 1)*/)
			{
				numVeicoli++;
				elapsedTime = 0;
				lastCustomerPosition = null;
				continue;
			}
			
			Customer c = acc.getCustomer(idCustomer);
			double serviceDuration = c.getServiceDuration();
			
			if (lastCustomerPosition == null)
			{
				lastCustomerPosition = new Point2D.Double(c.getXCoordinate(), c.getYCoordinate());
				distanceOrTime = depotPosition.distance(lastCustomerPosition);
			}
			else
			{
				Point2D tmpPoint = new Point2D.Double(c.getXCoordinate(), c.getYCoordinate());
				distanceOrTime = lastCustomerPosition.distance(tmpPoint);
				lastCustomerPosition = tmpPoint;
			}
			
			if ((elapsedTime + distanceOrTime) <= c.getEndTw())
			{	
				timeToWait = c.getStartTw() - elapsedTime - distanceOrTime;
				timeToWait = (timeToWait < 0) ?	0 : timeToWait;
				
				if ((elapsedTime + distanceOrTime + timeToWait + serviceDuration + depotPosition.distance(lastCustomerPosition)) <= depotDueTime)
				{
					elapsedTime += distanceOrTime;
					elapsedTime += timeToWait;
					elapsedTime += serviceDuration;
				}
				else
				{
					entitiesOfViolations[2] += 1;
					entitiesOfViolations[3] += (elapsedTime + distanceOrTime + timeToWait + serviceDuration + depotPosition.distance(lastCustomerPosition) - depotDueTime);
				}
			
			}
			else
			{
				entitiesOfViolations[2] += 1;
				entitiesOfViolations[3] += (elapsedTime + distanceOrTime - c.getEndTw());
			}
		}
		
		return entitiesOfViolations;
	}
}
