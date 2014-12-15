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

import com.GMGroup.Genetic.SearchProgram;
import com.sun.org.apache.xml.internal.utils.StopParseException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
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

public class MainFrame extends JFrame implements TabuSearchListener{

	private JPanel contentPane;
	private JTextField populationSizeParam;
	private JTextField crossOverParam;
	private JTextField mutationParam;
	private JComboBox<String> cbFileList;
	
	private static MainFrame instance;
	
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
	public MainFrame() {
		setTitle("GiudaMorto UI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 254, 331);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblTimeElapsed = new JLabel("Time elapsed:");
		
		final JLabel lblElapsedTimeVal = new JLabel("NA");
		
		JLabel lblNewLabel_1 = new JLabel("Started At:");
		
		final JLabel lblStartedAtValue = new JLabel("NA");
		
		JLabel lblEvolveStatus = new JLabel("Evolve Status:");
		
		JLabel lblEvolveStatusValue = new JLabel("NA");
		
		JLabel lblCurrentTop = new JLabel("Current TOP:");
		
		lblCurrentTopVal = new JLabel("NA");
		
		JLabel lblCrossoverParam = new JLabel("Population Size:");
		
		JLabel label = new JLabel("CrossOver Param:");
		
		JLabel lblMutationparam = new JLabel("MutationParam:");
		
		populationSizeParam = new JTextField();
		populationSizeParam.setColumns(10);
		
		crossOverParam = new JTextField();
		crossOverParam.setColumns(10);
		
		mutationParam = new JTextField();
		mutationParam.setColumns(10);
		
		populationSizeParam.setText(""+SearchProgram.INITIAL_POPULATION_SIZE);
		crossOverParam.setText(""+SearchProgram.CROSS_OVER_LIMIT_RATIO);
		mutationParam.setText(""+SearchProgram.MUTATION_LIMIT_RATIO);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		final JButton btnStart = new JButton("Start");
		splitPane.setLeftComponent(btnStart);
		
		final JButton btnStop = new JButton("Stop");
		splitPane.setRightComponent(btnStop);
		
		cbFileList = new JComboBox();
			
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(10)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblTimeElapsed)
									.addGap(121)
									.addComponent(lblElapsedTimeVal))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblNewLabel_1)
									.addGap(133)
									.addComponent(lblStartedAtValue))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblEvolveStatus)
									.addGap(117)
									.addComponent(lblEvolveStatusValue))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblCurrentTop)
									.addGap(123)
									.addComponent(lblCurrentTopVal))
								.addComponent(cbFileList, GroupLayout.PREFERRED_SIZE, 201, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblCrossoverParam)
									.addGap(39)
									.addComponent(populationSizeParam, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(label)
									.addGap(27)
									.addComponent(crossOverParam, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblMutationparam)
									.addGap(39)
									.addComponent(mutationParam, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(splitPane, GroupLayout.PREFERRED_SIZE, 209, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(673, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(19)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblTimeElapsed)
						.addComponent(lblElapsedTimeVal))
					.addGap(6)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_1)
						.addComponent(lblStartedAtValue))
					.addGap(6)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblEvolveStatus)
						.addComponent(lblEvolveStatusValue))
					.addGap(6)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblCurrentTop)
						.addComponent(lblCurrentTopVal))
					.addGap(6)
					.addComponent(cbFileList, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(6)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(5)
							.addComponent(lblCrossoverParam))
						.addComponent(populationSizeParam, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(2)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(3)
							.addComponent(label))
						.addComponent(crossOverParam, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(3)
							.addComponent(lblMutationparam))
						.addComponent(mutationParam, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(splitPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(63))
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
					sp.stop();
					btnStop.setEnabled(false);
					btnStart.setEnabled(true);
				}
			}
		});

			
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					int initialPopSize = Integer.parseInt(populationSizeParam.getText());
					double cop=Double.parseDouble(crossOverParam.getText());
					double mop = Double.parseDouble(mutationParam.getText());
					
					sp = new SearchProgram(cbFileList.getSelectedItem().toString());
					sp.setInitialPopulationSize(initialPopSize);
					sp.setCrossOverParam(cop);
					sp.setMutationParam(mop);
					
					btnStart.setEnabled(false);
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
					JOptionPane.showMessageDialog(MainFrame.this, e.getMessage());
				}
				
			}
		});		
	}

	private SearchProgram sp = null;
	
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
