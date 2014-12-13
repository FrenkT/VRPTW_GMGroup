package com.GMGroup.GeneticUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SpringLayout;
import javax.swing.JTextField;
import javax.swing.JButton;

import com.GMGroup.Genetic.SearchProgram;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComboBox;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTextField populationSizeParam;
	private JTextField crossOverParam;
	private JTextField mutationParam;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("GiudaMorto UI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 274, 321);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		JLabel lblTimeElapsed = new JLabel("Time elapsed:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblTimeElapsed, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblTimeElapsed, 10, SpringLayout.WEST, contentPane);
		contentPane.add(lblTimeElapsed);
		
		final JLabel lblElapsedTimeVal = new JLabel("NA");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblElapsedTimeVal, 0, SpringLayout.NORTH, lblTimeElapsed);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblElapsedTimeVal, -10, SpringLayout.EAST, contentPane);
		contentPane.add(lblElapsedTimeVal);
		
		JLabel lblNewLabel_1 = new JLabel("Started At:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblNewLabel_1, 6, SpringLayout.SOUTH, lblTimeElapsed);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNewLabel_1, 10, SpringLayout.WEST, contentPane);
		contentPane.add(lblNewLabel_1);
		
		final JLabel lblStartedAtValue = new JLabel("NA");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblStartedAtValue, 6, SpringLayout.SOUTH, lblElapsedTimeVal);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblStartedAtValue, 0, SpringLayout.EAST, lblElapsedTimeVal);
		contentPane.add(lblStartedAtValue);
		
		JLabel lblEvolveStatus = new JLabel("Evolve Status:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblEvolveStatus, 6, SpringLayout.SOUTH, lblNewLabel_1);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblEvolveStatus, 0, SpringLayout.WEST, lblTimeElapsed);
		contentPane.add(lblEvolveStatus);
		
		JLabel lblEvolveStatusValue = new JLabel("NA");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblEvolveStatusValue, 6, SpringLayout.SOUTH, lblStartedAtValue);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblEvolveStatusValue, 0, SpringLayout.EAST, lblElapsedTimeVal);
		contentPane.add(lblEvolveStatusValue);
		
		JLabel lblCurrentTop = new JLabel("Current TOP:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrentTop, 6, SpringLayout.SOUTH, lblEvolveStatus);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentTop, 0, SpringLayout.WEST, lblTimeElapsed);
		contentPane.add(lblCurrentTop);
		
		JLabel lblCurrentTopVal = new JLabel("NA");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrentTopVal, 6, SpringLayout.SOUTH, lblEvolveStatusValue);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblCurrentTopVal, 0, SpringLayout.EAST, lblElapsedTimeVal);
		contentPane.add(lblCurrentTopVal);
		
		JButton btnStart = new JButton("Start");
		
		sl_contentPane.putConstraint(SpringLayout.WEST, btnStart, 46, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnStart, -33, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnStart, -31, SpringLayout.EAST, contentPane);
		contentPane.add(btnStart);
		
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnStop, 6, SpringLayout.SOUTH, btnStart);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnStop, 0, SpringLayout.WEST, btnStart);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnStop, -31, SpringLayout.EAST, contentPane);
		contentPane.add(btnStop);
		
		final JComboBox cbFileList = new JComboBox();
		sl_contentPane.putConstraint(SpringLayout.NORTH, cbFileList, 6, SpringLayout.SOUTH, lblCurrentTop);
		sl_contentPane.putConstraint(SpringLayout.WEST, cbFileList, 0, SpringLayout.WEST, lblTimeElapsed);
		sl_contentPane.putConstraint(SpringLayout.EAST, cbFileList, 0, SpringLayout.EAST, lblElapsedTimeVal);
		contentPane.add(cbFileList);
		
		JLabel lblCrossoverParam = new JLabel("Population Size:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCrossoverParam, 6, SpringLayout.SOUTH, cbFileList);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCrossoverParam, 10, SpringLayout.WEST, contentPane);
		contentPane.add(lblCrossoverParam);
		
		JLabel label = new JLabel("CrossOver Param:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, label, 6, SpringLayout.SOUTH, lblCrossoverParam);
		sl_contentPane.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, lblTimeElapsed);
		contentPane.add(label);
		
		JLabel lblMutationparam = new JLabel("MutationParam:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMutationparam, 6, SpringLayout.SOUTH, label);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMutationparam, 0, SpringLayout.WEST, lblTimeElapsed);
		contentPane.add(lblMutationparam);
		
		populationSizeParam = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, populationSizeParam, 6, SpringLayout.SOUTH, cbFileList);
		sl_contentPane.putConstraint(SpringLayout.EAST, populationSizeParam, 0, SpringLayout.EAST, lblElapsedTimeVal);
		contentPane.add(populationSizeParam);
		populationSizeParam.setColumns(10);
		
		crossOverParam = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, crossOverParam, 0, SpringLayout.NORTH, label);
		sl_contentPane.putConstraint(SpringLayout.WEST, crossOverParam, 0, SpringLayout.WEST, populationSizeParam);
		contentPane.add(crossOverParam);
		crossOverParam.setColumns(10);
		
		mutationParam = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, mutationParam, 0, SpringLayout.NORTH, lblMutationparam);
		sl_contentPane.putConstraint(SpringLayout.EAST, mutationParam, 0, SpringLayout.EAST, lblElapsedTimeVal);
		contentPane.add(mutationParam);
		mutationParam.setColumns(10);
		
		// Fill up the combobox
		File f = new File("input");
		for(String s : f.list())
			cbFileList.addItem(s);
		
		populationSizeParam.setText(""+SearchProgram.INITIAL_POPULATION_SIZE);
		crossOverParam.setText(""+SearchProgram.CROSS_OVER_LIMIT_RATIO);
		mutationParam.setText(""+SearchProgram.MUTATION_LIMIT_RATIO);
		
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SearchProgram sp;
				try {
					int initialPopSize = Integer.parseInt(populationSizeParam.getText());
					double cop=Double.parseDouble(crossOverParam.getText());
					double mop = Double.parseDouble(mutationParam.getText());
					
					sp = new SearchProgram(cbFileList.getSelectedItem().toString());
					sp.setInitialPopulationSize(initialPopSize);
					sp.setCrossOverParam(cop);
					sp.setMutationParam(mop);
					
					sp.start();
					lblStartedAtValue.setText((new Date()).toLocaleString());
					final long now = (new Date()).getTime()/1000;
					Timer t = new Timer();
					t.schedule(new TimerTask() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							long n = (new Date()).getTime()/1000;
							long span = (n-now);
							int hours = (int)span/3600;
							int min = (int) ((span % 3600)/60);
							int sec = (int) ((span % 3600)%60);
							lblElapsedTimeVal.setText(hours+":"+min+":"+sec);
						}
					}, 0, 1000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	}
}
