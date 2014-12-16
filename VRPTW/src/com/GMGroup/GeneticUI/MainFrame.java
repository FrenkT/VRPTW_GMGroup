package com.GMGroup.GeneticUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.GMGroup.Genetic.MySearchParameters;
import com.GMGroup.Genetic.SearchProgram;
import com.sun.org.apache.xml.internal.utils.StopParseException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComboBox;

import org.coinor.opents.TabuSearchEvent;
import org.coinor.opents.TabuSearchListener;

import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JSplitPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JEditorPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;

import java.awt.Color;

import javax.swing.border.LineBorder;
import javax.swing.JCheckBox;

public class MainFrame extends JFrame implements TabuSearchListener{

	private JPanel contentPane;
	private JTextField initialPopulationInput;
	private JTextField crossOverLimitRatioInput;
	private JTextField alphaParamInput;
	private JComboBox<String> cbFileList;
	
	private static MainFrame instance;
	
	public static final int MAX_TIMEOUT = 300000;
	
	public static MainFrame getInstance()
	{
		return instance;
	}
	
	/**
	 * Launch the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					instance= new MainFrame();
					instance.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	private JLabel lblCurrentTopVal=null;
	private MySearchParameters currentParams;
	
	public MainFrame() {
		currentParams=new MySearchParameters();
		setTitle("GiudaMorto UI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 281, 509);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblTimeElapsed = new JLabel("Time elapsed:");
		
		final JLabel lblElapsedTimeVal = new JLabel("NA");
		
		JLabel lblNewLabel_1 = new JLabel("Started At:");
		
		final JLabel lblStartedAtValue = new JLabel("NA");
		
		JLabel lblEvolveStatus = new JLabel("Evolve Status:");
		
		JLabel lblEvolveStatusValue = new JLabel("NA");
		
		JLabel lblCurrentTop = new JLabel("TopResult:");
		
		lblCurrentTopVal = new JLabel("NA");
		
		JLabel lblCrossoverParam = new JLabel("Population Size:");
		
		JLabel lblCrossover = new JLabel("CrossOverLimit Ratio:");
		
		JLabel lblMutationparam = new JLabel("Mutation Alpha:");
		
		initialPopulationInput = new JTextField();
		initialPopulationInput.setColumns(10);
		
		crossOverLimitRatioInput = new JTextField();
		crossOverLimitRatioInput.setColumns(10);
		
		alphaParamInput = new JTextField();
		alphaParamInput.setColumns(10);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		final JButton btnStart = new JButton("Start");
		splitPane.setLeftComponent(btnStart);
		
		final JButton btnStop = new JButton("Stop");
		splitPane.setRightComponent(btnStop);
		
		cbFileList = new JComboBox<String>();
		
		JLabel lblMaxEvolveIterations = new JLabel("Max Evolve Iterations:");
		
		maxEvolveIterationsInput = new JTextField();
		maxEvolveIterationsInput.setColumns(10);
		
		numOfKChainSwapsInput = new JTextField();
		numOfKChainSwapsInput.setColumns(10);
		
		JLabel lblKchainSwaps = new JLabel("KChain Swaps:");
		
		tabuNonImprovingThresholdInput = new JTextField();
		tabuNonImprovingThresholdInput.setColumns(10);
		JLabel lblTabuNonImproving = new JLabel("<html>Tabu Non Improving Iteration Threshold:</html>");
		
		JLabel lblTabuDeltaThreshold = new JLabel("Tabu Delta Threshold:");
		
		tabuDeltaThresholdInput = new JTextField();
		tabuDeltaThresholdInput.setColumns(10);
		
		final JCheckBox chkbxStopAt5 = new JCheckBox("Stop at 5 mins");
		chkbxStopAt5.setSelected(true);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(10)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblTimeElapsed)
								.addComponent(lblNewLabel_1)
								.addComponent(lblEvolveStatus)
								.addComponent(lblCurrentTop))
							.addGap(105)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblCurrentTopVal)
								.addComponent(lblEvolveStatusValue)
								.addComponent(lblStartedAtValue)
								.addComponent(lblElapsedTimeVal)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblCrossover)
								.addComponent(lblTabuNonImproving, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(lblKchainSwaps, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblMaxEvolveIterations, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblMutationparam))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(cbFileList, GroupLayout.PREFERRED_SIZE, 223, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblCrossoverParam, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
										.addComponent(maxEvolveIterationsInput, Alignment.LEADING)
										.addComponent(initialPopulationInput, Alignment.LEADING)
										.addComponent(crossOverLimitRatioInput, Alignment.LEADING)
										.addComponent(alphaParamInput, Alignment.LEADING, 105, 105, Short.MAX_VALUE)
										.addComponent(numOfKChainSwapsInput, Alignment.LEADING, 105, 105, Short.MAX_VALUE)
										.addComponent(tabuNonImprovingThresholdInput, Alignment.LEADING)
										.addComponent(tabuDeltaThresholdInput, Alignment.LEADING)))))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblTabuDeltaThreshold))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(chkbxStopAt5)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(19)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTimeElapsed)
						.addComponent(lblElapsedTimeVal))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1)
						.addComponent(lblStartedAtValue))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblEvolveStatus)
						.addComponent(lblEvolveStatusValue))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCurrentTop)
						.addComponent(lblCurrentTopVal))
					.addGap(18)
					.addComponent(cbFileList, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(18)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblCrossoverParam)
								.addComponent(initialPopulationInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(32)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblCrossover)
								.addComponent(crossOverLimitRatioInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblMutationparam)
								.addComponent(alphaParamInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblKchainSwaps)
								.addComponent(numOfKChainSwapsInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(44)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblMaxEvolveIterations)
								.addComponent(maxEvolveIterationsInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTabuNonImproving, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
						.addComponent(tabuNonImprovingThresholdInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTabuDeltaThreshold)
						.addComponent(tabuDeltaThresholdInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chkbxStopAt5)
					.addGap(9)
					.addComponent(splitPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
		
		// Fill up the combobox
		File f = new File("input");
		for(String s : f.list())
			cbFileList.addItem(s);
		
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (sp==null || sp.isInterrupted())
				{
					return;
				}
				else
				{
					sp.halt();
				}
			}
		});

			
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					// Setting up parameters
					currentParams.setAlphaParameterKChain(Double.parseDouble(alphaParamInput.getText()));
					currentParams.setCrossOverLimitRatio(Double.parseDouble(crossOverLimitRatioInput.getText()));
					currentParams.setInitialPopulationSize(Integer.parseInt(initialPopulationInput.getText()));
					currentParams.setMaxEvolveIterations(Integer.parseInt(maxEvolveIterationsInput.getText()));
					currentParams.setNumOfKChainSwap(Integer.parseInt(numOfKChainSwapsInput.getText()));
					currentParams.setTabuNonImprovingThresold(Integer.parseInt(tabuNonImprovingThresholdInput.getText()));
					currentParams.setTabuDeltaRatio(Double.parseDouble(tabuDeltaThresholdInput.getText()));
					
					sp = new SearchProgram(cbFileList.getSelectedItem().toString(),currentParams);
					
					btnStart.setEnabled(false);
					btnStop.setEnabled(true);
					
					sp.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
						@Override
						public void uncaughtException(Thread t, Throwable e) {
							btnStop.setEnabled(false);
							btnStart.setEnabled(true);
							//e.printStackTrace(System.err);
							sp.PrintStatus();
						}
					});
					
					sp.start();
					lblStartedAtValue.setText((new Date()).toLocaleString());
					final long now = (new Date()).getTime()/1000;
					final Timer t = new Timer();
					t.schedule(new ClockUpdater(now,lblElapsedTimeVal), 0, 1000);
					
					// Stop this Thread after 5 minutes
					if (chkbxStopAt5.isSelected())
					{
						Timer gameOver = new Timer();
						gameOver.schedule(new TimerTask(){
	
							@Override
							public void run() {
								// TODO Auto-generated method stub
								sp.halt();
								t.cancel();
							}
							
						}, MAX_TIMEOUT); // Stop after 
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(MainFrame.this, e.getMessage());
				}
				
			}
		});	
		
		
		// Default values
		alphaParamInput.setText(""+currentParams.getAlphaParameterKChain());
		initialPopulationInput.setText(""+currentParams.getInitialPopulationSize());
		maxEvolveIterationsInput.setText(""+currentParams.getMaxEvolveIterations());
		numOfKChainSwapsInput.setText(""+currentParams.getNumOfKChainSwap());
		tabuNonImprovingThresholdInput.setText(""+currentParams.getTabuNonImprovingThresold());
		tabuDeltaThresholdInput.setText(""+currentParams.getTabuDeltaRatio());
		crossOverLimitRatioInput.setText(""+currentParams.getCrossOverLimitRatio());
	}

	private SearchProgram sp = null;
	private JTextField maxEvolveIterationsInput;
	private JTextField numOfKChainSwapsInput;
	private JTextField tabuNonImprovingThresholdInput;
	private JTextField tabuDeltaThresholdInput;
	
	@Override
	public void tabuSearchStarted(TabuSearchEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tabuSearchStopped(TabuSearchEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newBestSolutionFound(TabuSearchEvent e) {
		lblCurrentTopVal.setText(""+e.getTabuSearch().getBestSolution().getObjectiveValue()[0]);
	}

	@Override
	public void newCurrentSolutionFound(TabuSearchEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unimprovingMoveMade(TabuSearchEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void improvingMoveMade(TabuSearchEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noChangeInValueMoveMade(TabuSearchEvent e) {
		// TODO Auto-generated method stub
		
	}
}
