package com.GMGroup.Genetic;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IGeneConstraintChecker;
import org.jgap.RandomGenerator;
import org.jgap.UnsupportedRepresentationException;

import com.mdvrp.Instance;

public class MyIntGene implements Gene{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4427233500501658649L;
	private int customerOrDepotId;
	//private UUID myId;
	
	public MyIntGene(int customerOrDepotId)
	{
		if (customerOrDepotId > Instance.getInstance().getCustomersNr())
			throw new IllegalArgumentException("CustomerOrDepotId "+customerOrDepotId+" is not valid.");
		
		//myId = UUID.randomUUID();
		
		this.customerOrDepotId = customerOrDepotId;
	}
	
	@Override
	public int compareTo(Object o) {
		try{
			MyIntGene another = (MyIntGene)o;
			return this.customerOrDepotId - another.customerOrDepotId;
		}
		catch(ClassCastException ex)
		{
			throw new IllegalArgumentException("MyIntGene only supports comparision with another MyIntGene object.");
		}
	}

	@Override
	public boolean equals(Object o)
	{
		try{
			MyIntGene another = (MyIntGene)o;
			return this.customerOrDepotId == another.customerOrDepotId;
		}
		catch(ClassCastException ex)
		{
			throw new IllegalArgumentException("MyIntGene only supports equality comparision if compared to another MyIntGene");
		}
	}
	
	@Override
	public String getUniqueID() {
		//return myId.toString();
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

	//TODO
	@Override
	public void applyMutation(int arg0, double arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanup() {
		// Nothing to clean
	}

	@Override
	public Object getAllele() {
		return customerOrDepotId;
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
	public double getEnergy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getPersistentRepresentation()
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCompareApplicationData() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Gene newGene() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAllele(Object customerOrDepotId) {
		try {
			int target = (int)customerOrDepotId;
			this.customerOrDepotId = target;
		}
		catch(ClassCastException ex)
		{
			throw new IllegalArgumentException("Cannot set Gene Value with a non integer value");
		}
		
	}

	@Override
	public void setApplicationData(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCompareApplicationData(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConstraintChecker(IGeneConstraintChecker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEnergy(double arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setToRandomValue(RandomGenerator arg0) {
		customerOrDepotId = arg0.nextInt(Instance.getInstance().getCustomersNr());
	}

	@Override
	public void setValueFromPersistentRepresentation(String arg0)
			throws UnsupportedOperationException,
			UnsupportedRepresentationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

}
