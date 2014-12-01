package com.GMGroup.Genetic;

import org.jgap.BaseGene;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IGeneConstraintChecker;
import org.jgap.InvalidConfigurationException;
import org.jgap.RandomGenerator;
import org.jgap.UnsupportedRepresentationException;
import org.jgap.impl.IntegerGene;

import com.mdvrp.Instance;

public class MyIntGene extends BaseGene {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4427233500501658649L;
	private int customerOrDepotId;
	//private UUID myId;
	
	public MyIntGene(Configuration conf, int customerOrDepotId) throws InvalidConfigurationException
	{
		super(conf);
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

	//TODO
	@Override
	public void applyMutation(int arg0, double arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getAllele() {
		return customerOrDepotId;
	}


	@Override
	public String getPersistentRepresentation()
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gene newGene() {
		try {
			return new MyIntGene(getConfiguration(), customerOrDepotId);
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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
	public void setToRandomValue(RandomGenerator arg0) {
		customerOrDepotId = arg0.nextInt(Instance.getInstance().getCustomersNr());
	}

	@Override
	public void setValueFromPersistentRepresentation(String arg0)
			throws UnsupportedOperationException,
			UnsupportedRepresentationException {
		throw new UnsupportedOperationException();
		
	}


	@Override
	protected Object getInternalValue() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected Gene newGeneInternal() {
		return null;
	}

}
