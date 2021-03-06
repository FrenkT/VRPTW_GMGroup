package com.GMGroup.GeneticUI;

import java.awt.EventQueue;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activity.InvalidActivityException;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.GMGroup.Genetic.InfeasibilityPolicy;
import com.GMGroup.Genetic.MyChromosomeFactory;
import com.GMGroup.Genetic.MyFitnessFunction;
import com.GMGroup.Genetic.MySearchParameters;
import com.GMGroup.Genetic.SearchProgram;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JComboBox;

import org.coinor.opents.TabuSearchEvent;
import org.coinor.opents.TabuSearchListener;
import org.jgap.Configuration;
import org.jgap.Population;
import org.jgap.audit.IEvolutionMonitor;
import org.jgap.eval.PopulationHistoryIndexed;

import javax.swing.JSplitPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

import java.awt.Label;

import javax.swing.ImageIcon;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements IEvolutionMonitor{

	private JPanel contentPane;
	private JTextField initialPopulationInput;
	private JTextField crossOverLimitRatioInput;
	private JTextField alphaParamInput;
	private JComboBox<String> cbFileList;
	private TimerTask gameOverTT;
	private Timer gameOver;
	private JLabel lblEvolveStatusValue;
	private static boolean customFnameSet = false;
	private static MainFrame instance;
	public static final int MAX_TIMEOUT = 300000;
	
	public static MainFrame getInstance()
	{
		return instance;
	}
	
	private static String inputFileName=null;						// Default: null
	public static String outputFileName="output/solutions.csv";		// Default: "output/solutions.csv"
	private static int randomSeed=-1;								// Default: random seed
	private static boolean autoMode = false;
	private static String emailDest = null;
	private static String smtpHost = "localhost";
	/**
	 * Launch the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 * @throws InvalidActivityException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, FileNotFoundException {
		// Parsing dei parametri di input
		if(args.length % 2 == 0){
			for(int i = 0; i < args.length; i += 2){
				switch (args[i]) {
					case "-if":
						inputFileName = args[i+1];
						File f = new File(inputFileName);
						if (!f.exists())
						{
							System.err.println("Input file "+inputFileName+" doesn't exists. Please check the input filename and try again.");
							throw new FileNotFoundException("Input file "+inputFileName+" doesn't exists. Please check the input filename and try again.");
						}
						
						break;
					case "-of":
						outputFileName = args[i+1];
						customFnameSet = true;
						break;
					case "-auto":
						if (Integer.parseInt(args[i+1])==1)
							autoMode = true;
						else
							autoMode = false;
						break;
					case "-email":
						emailDest  = args[i+1];
						break;
					case "-smtp":
						smtpHost = args[i+1];
						break;
					case "-rs":
						randomSeed = Integer.parseInt(args[i+1]);
						break;
					default: {
						System.out.println("Unknown type of argument: " + args[i]);
						throw new IllegalArgumentException("Unknown type of argument: " + args[i]);
					}
				}
			}
		}else {
			System.out.println("Parameters are not in correct format");
			throw new IllegalArgumentException("Parameters are not in correct format");
		}
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					instance= new MainFrame(inputFileName,outputFileName,randomSeed);
					instance.setVisible(true);
					if (autoMode)
						instance.fireStart();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void fireStart() {
		btnStart.doClick();
	}

	/**
	 * Create the frame.
	 */
	private final JButton btnStart = new JButton("Start");
	private JLabel lblCurrentTopVal=null;
	private MySearchParameters currentParams;
	private JCheckBox chkbxStopAt5;
	
	public MainFrame(String inputFileName, String outputFileName, int randomSeed) {
		currentParams=new MySearchParameters();
		setTitle("Alg Conf.");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 328, 717);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblTimeElapsed = new JLabel("Time elapsed:");
		
		final JLabel lblElapsedTimeVal = new JLabel("NA");
		
		JLabel lblNewLabel_1 = new JLabel("Started At:");
		
		final JLabel lblStartedAtValue = new JLabel("NA");
		
		JLabel lblEvolveStatus = new JLabel("Evolve Status:");
		
		lblEvolveStatusValue = new JLabel("NA");
		
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
		
		chkbxStopAt5 = new JCheckBox("Stop at 5 mins");
		chkbxStopAt5.setSelected(true);
		
		JLabel lblInitialPopFeas = new JLabel("Initial Pop Feas. Ratio:");
		
		feasibilityPercentageInput = new JTextField();
		feasibilityPercentageInput.setColumns(10);
		
		JLabel label = new JLabel("%");
		
		JLabel label_1 = new JLabel("%");
		
		JLabel label_2 = new JLabel("0...1");
		
		JLabel label_3 = new JLabel("#");
		
		JLabel label_4 = new JLabel("#");
		
		JLabel label_5 = new JLabel("#");
		
		JLabel label_6 = new JLabel("#");
		
		JLabel label_7 = new JLabel("#");
		
		final JCheckBox chckbxBatchRun = new JCheckBox("Batch run:");
		chckbxBatchRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				runsInput.setEnabled(chckbxBatchRun.isSelected());
				cbFileList.setEnabled(!chckbxBatchRun.isSelected());
			}
		});
		
		runsInput = new JTextField();
		runsInput.setEnabled(false);
		runsInput.setText("10");
		runsInput.setColumns(10);
		
		JLabel lblCrossoverWindowWidth = new JLabel("CrossOver Window:");
		crossoverWindowInput = new JTextField();
		crossoverWindowInput.setColumns(10);
		
		JLabel label_8 = new JLabel("#");
		
		factoryWaitableRatioInput = new JTextField();
		factoryWaitableRatioInput.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Factory Waitable Ratio:");
		
		waitingVehicleInput = new JTextField();
		waitingVehicleInput.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Factory MAX Waiting Vehicle:");
		
		JLabel label_9 = new JLabel("0...1");
		
		JLabel label_10 = new JLabel("#");
		
		rdbtnRelaxTabuConstr = new JRadioButton("Relax Tabu Constr.");
		buttonGroup.add(rdbtnRelaxTabuConstr);
		
		JLabel lblWhenPopIs = new JLabel("When pop is unfeasible:");
		
		JLabel lblNewLabel_3 = new JLabel("Feasibility:");
		
		feasibilityIcon = new JLabel("");
		
		JLabel lblBest = new JLabel("Best:");
		
		lblBestFeasible = new JLabel("...");
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
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblCurrentTopVal)
								.addComponent(lblEvolveStatusValue)
								.addComponent(lblElapsedTimeVal)
								.addComponent(lblStartedAtValue, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED, 141, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(splitPane, GroupLayout.PREFERRED_SIZE, 236, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(chkbxStopAt5)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(chckbxBatchRun)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(runsInput, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
										.addComponent(lblInitialPopFeas)
										.addComponent(lblCrossover)
										.addComponent(lblTabuNonImproving, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblKchainSwaps, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(lblMaxEvolveIterations, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
										.addComponent(lblMutationparam)
										.addComponent(lblCrossoverWindowWidth)
										.addGroup(gl_contentPane.createSequentialGroup()
											.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
												.addComponent(lblCrossoverParam, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
												.addComponent(lblTabuDeltaThreshold)
												.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
												.addComponent(lblNewLabel_2)
												.addComponent(lblWhenPopIs))
											.addPreferredGap(ComponentPlacement.RELATED)
											.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
												.addComponent(rdbtnRelaxTabuConstr)
												.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
													.addComponent(maxEvolveIterationsInput, Alignment.LEADING)
													.addComponent(initialPopulationInput, Alignment.LEADING)
													.addComponent(crossOverLimitRatioInput, Alignment.LEADING)
													.addComponent(alphaParamInput, Alignment.LEADING, 105, 105, Short.MAX_VALUE)
													.addComponent(numOfKChainSwapsInput, Alignment.LEADING, 105, 105, Short.MAX_VALUE)
													.addComponent(tabuNonImprovingThresholdInput, Alignment.LEADING)
													.addComponent(tabuDeltaThresholdInput, Alignment.LEADING)
													.addComponent(feasibilityPercentageInput, Alignment.LEADING)
													.addComponent(crossoverWindowInput, Alignment.LEADING)
													.addComponent(factoryWaitableRatioInput, Alignment.LEADING)
													.addComponent(waitingVehicleInput, Alignment.LEADING)))))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(label_8, GroupLayout.PREFERRED_SIZE, 8, GroupLayout.PREFERRED_SIZE)
										.addComponent(label_7, GroupLayout.PREFERRED_SIZE, 8, GroupLayout.PREFERRED_SIZE)
										.addComponent(label_6, GroupLayout.PREFERRED_SIZE, 8, GroupLayout.PREFERRED_SIZE)
										.addComponent(label_5, GroupLayout.PREFERRED_SIZE, 8, GroupLayout.PREFERRED_SIZE)
										.addComponent(label_4, GroupLayout.PREFERRED_SIZE, 8, GroupLayout.PREFERRED_SIZE)
										.addComponent(label_3)
										.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 11, GroupLayout.PREFERRED_SIZE)
										.addComponent(label)
										.addComponent(label_2, GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
										.addComponent(label_9, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
										.addComponent(label_10, GroupLayout.PREFERRED_SIZE, 8, GroupLayout.PREFERRED_SIZE)))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
										.addComponent(cbFileList, GroupLayout.PREFERRED_SIZE, 223, GroupLayout.PREFERRED_SIZE)
										.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
											.addComponent(lblBest)
											.addComponent(lblNewLabel_3)))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(feasibilityIcon)
										.addComponent(lblBestFeasible))))))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(19)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblTimeElapsed)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblNewLabel_1)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblEvolveStatus)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblCurrentTop))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblElapsedTimeVal)
										.addComponent(lblNewLabel_3))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblStartedAtValue)
										.addComponent(lblBest)
										.addComponent(lblBestFeasible))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblEvolveStatusValue)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblCurrentTopVal)))
							.addGap(18)
							.addComponent(cbFileList, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(feasibilityIcon)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(18)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblCrossoverParam)
								.addComponent(initialPopulationInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(label_6))
							.addGap(32)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblCrossover)
								.addComponent(crossOverLimitRatioInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(label_2))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblMutationparam)
								.addComponent(alphaParamInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(label_7))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblKchainSwaps)
								.addComponent(numOfKChainSwapsInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(label_4)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(44)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblMaxEvolveIterations)
								.addComponent(maxEvolveIterationsInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(label_5))))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTabuNonImproving, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
						.addComponent(tabuNonImprovingThresholdInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_3))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(tabuDeltaThresholdInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTabuDeltaThreshold)
						.addComponent(label_1))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblInitialPopFeas)
						.addComponent(feasibilityPercentageInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCrossoverWindowWidth)
						.addComponent(crossoverWindowInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_8))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(factoryWaitableRatioInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel)
						.addComponent(label_9))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(waitingVehicleInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_2)
						.addComponent(label_10))
					.addGap(22)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblWhenPopIs)
						.addComponent(rdbtnRelaxTabuConstr))
					.addPreferredGap(ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(chkbxStopAt5)
						.addComponent(chckbxBatchRun)
						.addComponent(runsInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(splitPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
		
		// Fill up the combobox
		File f = new File("input");
		for(String s : f.list())
			cbFileList.addItem(s);
		
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (gameOverTT!=null)
				{
					gameOverTT.run();
					gameOver.cancel();
				}
				runs=0;
			}
		});

		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				Thread t = new Thread(){
					private static final int BUFFER = 2048;
					
					@SuppressWarnings("deprecation")
					@Override
					public void run()
					{
						List<String> resultsOut = new ArrayList<String>();
						
						try {
							runs = 1;
							String[] fnames = null;
							// Batch run
							if (chckbxBatchRun.isSelected())
							{
								runs = Integer.parseInt(runsInput.getText());
								 fnames=new String[]{
										"RC101.txt",
										"RC102.txt",
										"RC103.txt",
										"RC104.txt",
										"RC105.txt",
										"RC106.txt",
										"RC107.txt",
										"RC108.txt",
										"RC201.txt",
										"RC202.txt",
										"RC203.txt",
										"RC204.txt",
										"RC205.txt",
										"RC206.txt",
										"RC207.txt",
										"RC208.txt"
								};
								System.out.println("*************** GM Will run for "+runs+" times. ***************");
								System.out.println("*************** Random Seed will be incremented at each time. ***************");
							}
							else
								fnames = new String[]{cbFileList.getSelectedItem().toString()};
							
							for (String s:fnames)
							{
								if (!customFnameSet)
									MainFrame.outputFileName=s+".csv";
								
								for (int r = 0;r<runs;r++)
								{
									if (MainFrame.randomSeed>=0)
									{
										MainFrame.randomSeed++;
										System.out.println("-- Setting Random seed to "+MainFrame.randomSeed+" --");
									}
									
									// Setting up parameters
									// Static Stuff
									MyChromosomeFactory.MAX_WAITING_VEHICLE_NUMBER_RATIO = Double.parseDouble(waitingVehicleInput.getText());
									MyChromosomeFactory.MAX_WAITABLE_TIME_RATIO = Double.parseDouble(factoryWaitableRatioInput.getText());
									
									// Params obj
									currentParams.setAlphaParameterKChain(Double.parseDouble(alphaParamInput.getText()));
									currentParams.setCrossOverLimitRatio(Double.parseDouble(crossOverLimitRatioInput.getText()));
									currentParams.setInitialPopulationSize(Integer.parseInt(initialPopulationInput.getText()));
									currentParams.setMaxEvolveIterations(Integer.parseInt(maxEvolveIterationsInput.getText()));
									currentParams.setNumOfKChainSwap(Integer.parseInt(numOfKChainSwapsInput.getText()));
									currentParams.setTabuNonImprovingThresold(Integer.parseInt(tabuNonImprovingThresholdInput.getText()));
									currentParams.setTabuDeltaRatio(Double.parseDouble(tabuDeltaThresholdInput.getText()));
									currentParams.setInitialPopFeasibleChromosomesRatio(Double.parseDouble(feasibilityPercentageInput.getText()));
									currentParams.setCrossOverWindowWidth(Integer.parseInt(crossoverWindowInput.getText()));
									if (rdbtnRelaxTabuConstr.isSelected()) 
										currentParams.setInfeasibilityPolicy(InfeasibilityPolicy.RELAX_TABU);
									
									double penaltiesBck = MyFitnessFunction.TimeWPenalty;
									sp = new SearchProgram(s,MainFrame.randomSeed,currentParams);
									
									btnStart.setEnabled(false);
									btnStop.setEnabled(true);
									
									sp.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
										@Override
										public void uncaughtException(Thread t, Throwable e) {
											btnStop.setEnabled(false);
											btnStart.setEnabled(true);
											if (!(e.toString().compareTo("java.lang.ThreadDeath")==0))
												e.printStackTrace();
											try {
												sp.PrintStatus();
												synchronized (sp) {
													sp.notify();
												}
											} catch (IOException e1) {
												e1.printStackTrace();
											}
										}
									});
									sp.setEvolutionMonitor(MainFrame.this);
									sp.start();
									lblStartedAtValue.setText((new Date()).toLocaleString());
									final long now = (new Date()).getTime()/1000;
									final Timer t = new Timer();
									t.schedule(new ClockUpdater(now,lblElapsedTimeVal), 0, 1000);
									
									// Stop this Thread after 5 minutes
									if (chkbxStopAt5.isSelected())
									{
										gameOver = new Timer();
										gameOverTT = new TimerTask(){
											@Override
											public void run() {
												// TODO Auto-generated method stub
												sp.halt();
												t.cancel();
											}
										};
										gameOver.schedule(gameOverTT, MAX_TIMEOUT); // Stop after 
									}
									
									synchronized (sp) {
										sp.wait();
										System.out.println("Search run "+r+" ended.");
									}
									// Restoring prev value
									MyFitnessFunction.TimeWPenalty=penaltiesBck;
								} // End for run
								
								if (!resultsOut.contains(MainFrame.outputFileName))
									resultsOut.add(MainFrame.outputFileName);
								
							}
							
							// If an email is planned, ship it out now!
							if (emailDest!=null)
							{
								System.out.println("Creating ZipFile and sending Email to "+emailDest);
								try
								{
									// PREPARE VARIABLES
									long now = (new Date()).getTime(); 
									File attachmentFile = new File("results_"+now+".zip");
									String attachmentFileName = attachmentFile.getName();
									
									
									String htmlBody = "<p>Hi,<br/> "
											+ "I've completed all the work."
											+ "Please look at the attachment, save it and run another test against me!</p>"
											+ "<p>Cheers!</p>";
									
									// PREPARE ZIP FILE
									FileOutputStream fos = new FileOutputStream(attachmentFile);
									ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(fos));
									
									for(String s : resultsOut)
									{
										ZipEntry entry = new ZipEntry(s);
										out.putNextEntry(entry);
										FileInputStream fi = new FileInputStream(s);
										byte data[] = new byte[BUFFER ];
										int count;
										while((count = fi.read(data, 0, BUFFER)) != -1) {
										   out.write(data, 0, count);
										}
										fi.close();
									}
									out.close();
									
							        Multipart mp = new MimeMultipart();
							        MimeBodyPart htmlPart = new MimeBodyPart();
							        htmlPart.setContent(htmlBody, "text/html");
							        mp.addBodyPart(htmlPart);
							        MimeBodyPart attachment = new MimeBodyPart();
							        DataSource source = new FileDataSource(attachmentFile);
							        attachment.setDataHandler(new DataHandler(source));
							        attachment.setFileName(attachmentFileName);
							        mp.addBodyPart(attachment);
							        
							        Properties props = new Properties();
							        props.setProperty("mail.smtp.host", smtpHost);
									Session session = Session.getDefaultInstance(props,null);
									Message msg = new MimeMessage(session);
									msg.setContent(mp);
									
									msg.setFrom(new InternetAddress("albertogeniola@gmail.com", "Alberto Geniola - BOT"));
						            msg.addRecipient(Message.RecipientType.TO,new InternetAddress(emailDest, "GM Admin"));
						            msg.setSubject("GMAlg has done!");
						            Transport.send(msg);
						            attachmentFile.delete();
								}
								catch(Exception e){
									System.err.println("Error during email shipment/zip compression");
									e.printStackTrace();
								}
							} // End Send Mail
							
							// When here, if the -auto param was set, exit
							if (autoMode)
								System.exit(0);
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							JOptionPane.showMessageDialog(MainFrame.this, e.getMessage());
						}
					}
				};
				t.start();
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
		feasibilityPercentageInput.setText(""+currentParams.getInitialPopFeasibleChromosomesRatio());
		crossoverWindowInput.setText(""+currentParams.getCrossOverWindowWidth());
		factoryWaitableRatioInput.setText(""+MyChromosomeFactory.MAX_WAITABLE_TIME_RATIO);
		waitingVehicleInput.setText(""+MyChromosomeFactory.MAX_WAITING_VEHICLE_NUMBER_RATIO);
		rdbtnRelaxTabuConstr.setSelected(currentParams.getInfeasibilityPolicy()==InfeasibilityPolicy.RELAX_TABU);
		if (inputFileName!=null)
			cbFileList.setSelectedItem(inputFileName);
	}

	private SearchProgram sp = null;
	private JTextField maxEvolveIterationsInput;
	private JTextField numOfKChainSwapsInput;
	private JTextField tabuNonImprovingThresholdInput;
	private JTextField tabuDeltaThresholdInput;
	private JTextField feasibilityPercentageInput;
	private JTextField runsInput;
	private int runs=0;
	private JTextField crossoverWindowInput;
	private JTextField factoryWaitableRatioInput;
	private JTextField waitingVehicleInput;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton rdbtnRelaxTabuConstr;
	private JLabel feasibilityIcon;
	private JLabel lblBestFeasible;
	private final static DecimalFormat resultFormatter = new DecimalFormat("##0.00");
	
	public void setFeasibility(boolean fesbility)
	{
		if (fesbility)
			feasibilityIcon.setIcon(new ImageIcon(getClass().getResource("/icon/ok.png")));
		else
			feasibilityIcon.setIcon(new ImageIcon(getClass().getResource("/icon/error.png")));
	}
	
	public void setBestFeasible(double best)
	{
		lblBestFeasible.setText(resultFormatter.format(best));
	}
	
	public void setBestResult(double bestValue) {
		lblCurrentTopVal.setText(resultFormatter.format(bestValue));
	}

	@Override
	public void start(Configuration a_config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean nextCycle(Population a_pop, List<String> a_messages) {
		System.out.println("new Cycle");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void event(String a_monitorEvent, int a_evolutionNo,
			Object[] a_information) {
		lblEvolveStatusValue.setText(""+a_evolutionNo);
		
	}

	@Override
	public PopulationHistoryIndexed getPopulations() {
		// TODO Auto-generated method stub
		return null;
	}
}
