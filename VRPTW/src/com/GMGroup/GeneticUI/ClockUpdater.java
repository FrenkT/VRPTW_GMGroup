package com.GMGroup.GeneticUI;
import java.util.Date;
import java.util.TimerTask;

import javax.swing.JLabel;


public class ClockUpdater extends TimerTask {

	private long startTimeStamp;
	private JLabel lblElapsedTimeVal;
	
	public ClockUpdater(long startTimeStamp, JLabel labelToUpdate)
	{
		this.startTimeStamp = startTimeStamp;	
		this.lblElapsedTimeVal = labelToUpdate;
	}
	
	@Override
	public void run() {
		
		// TODO Auto-generated method stub
		long n = (new Date()).getTime()/1000;
		long span = (n-startTimeStamp);
		int hours = (int)span/3600;
		int min = (int) ((span % 3600)/60);
		int sec = (int) ((span % 3600)%60);
		lblElapsedTimeVal.setText(hours+":"+min+":"+sec);
		
	}

}
