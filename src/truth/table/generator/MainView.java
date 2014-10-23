package truth.table.generator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

public class MainView extends JFrame {

	private JPanel contentPane;
	private static PostfixExpression postFix1,postFix2;
	private static Parser parser;
	private static Vector history;
	private static JTextArea textView;
	private static JLabel evauation;
	private static double startTime;
	private static JLabel time;
	private static JLabel equivalentStatus;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		postFix1 = null;
		postFix2 = null;
		parser = new Parser();
		history = new Vector();
		
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try {
					MainView frame = new MainView();
					frame.setVisible(true);
					frame.setSize(800, 600);
					frame.setMinimumSize(new Dimension(600, 500));
				} catch (Exception e) {e.printStackTrace();}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainView() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainView.class.getResource("/pics/generate.png")));
		setTitle("MenTrix  Tab-Gen");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 820, 443);
		
		equivalentStatus = new JLabel("");
		
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		JMenuItem saveOption = new JMenuItem("Save Text");
		fileMenu.add(saveOption);
		saveOption.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showSaveDialog(MainView.this);
				if(returnVal == JFileChooser.APPROVE_OPTION) 
				{
					File file = fc.getSelectedFile();
					String path = file.getAbsolutePath();
					if(path.contains(".txt"))
	                {
	                	path=path.replaceAll(".txt", "");
	                }
					path+=".txt";
					try (BufferedWriter savedFile = new BufferedWriter(new FileWriter(path))) 
					{
							    try 
							    {
							    	textView.write(savedFile);
							    	JOptionPane.showMessageDialog(null,"File Saved Successfully.","Save File",JOptionPane.INFORMATION_MESSAGE );
							    }
							    catch (IOException e) {e.printStackTrace();}
					} catch (IOException e1) 
					{
						JOptionPane.showMessageDialog(null,"Unable to save file.","Save File",JOptionPane.ERROR_MESSAGE );
					}
				}
				
				
			}
		});
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		evauation = new JLabel("");
		time = new JLabel();
		textView = new JTextArea();
		textView.setEditable(false);
		textView.setFont(new Font("Courier New", Font.PLAIN, 12));
		
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, SystemColor.window, new Color(67, 78, 84), SystemColor.window, new Color(109, 109, 109)));
		panel.setBackground(SystemColor.inactiveCaption);
		contentPane.add(panel, BorderLayout.NORTH);
////////////////////////////////////////////////////////////////////// combo box history list ///////////////////////////////////
		final DefaultComboBoxModel model1 = new DefaultComboBoxModel(history);
		model1.setSelectedItem("");
		final DefaultComboBoxModel model2 = new DefaultComboBoxModel(history);
		model2.setSelectedItem("");
///////////////////////////////////////////////////////////////////// 1st and 2nd statement label /////////////////////////////////////
		JLabel firstStatment_L = new JLabel("1st Statment:");
		firstStatment_L.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel.add(firstStatment_L);
		
		JLabel secondStatment_L = new JLabel("2nd Statment:");
		secondStatment_L.setFont(new Font("Tahoma", Font.BOLD, 11));
		
/////////////////////////////////////////////////////////////////////// 1st and 2nd statement combo box //////////////////////////////////
		final JComboBox firstStatment_CB = new JComboBox(model1);
		firstStatment_CB.setEditable(true);
		panel.add(firstStatment_CB);
		
		final JComboBox secondStatment_CB = new JComboBox(model2);
		secondStatment_CB.setEditable(true);
////////////////////////////////////////////////////////////////////////Generate Button////////////////////////////////////////////////////
						JButton generate_B = new JButton("");
						generate_B.setPreferredSize(new Dimension(42, 33));
						generate_B.setContentAreaFilled(false);
						generate_B.setIcon(new ImageIcon(MainView.class.getResource("/pics/generate.png")));
						
						generate_B.addActionListener(new ActionListener() 
						{
							public void actionPerformed(ActionEvent arg0) 
							{
							    startTime = System.nanoTime();
								String firstStatment = firstStatment_CB.getSelectedItem().toString();
								String secondStatment = secondStatment_CB.getSelectedItem().toString();
								String whichOne = "";
								switch(settedComboBox())
								{
									case 0: break;
									case 1:
											String statment = (!firstStatment.equals(""))? firstStatment : secondStatment;
											whichOne = (!firstStatment.equals(""))? "Statment 1" : "Statment 2";
											parser.setInfix(statment,whichOne);
											if(parser.checkValidationAndParseInfix())
											{
												
												//System.out.println("infix = "+parser.getInfix());
												postFix1 = parser.generatePostfix();
												postFix1.evaluateTruthTable();
												//System.out.println("postfix = "+postFix1.getPostfixExpression());
												
												
												if(model1.getIndexOf(parser.getInfix())==-1)
												{model1.addElement(parser.getInfix());}
												
												textView.setText("");
												evauation.setText("");
												equivalentStatus.setText("");
												
											//	System.out.println("End Evaluation Time = "+(System.nanoTime() - startTime) / Math.pow(10,9));
												
												printer(true,whichOne);
											}
											else postFix1 = null;
											break;
									case 2:
									        int numberOfValid = 0;
											// 1st statement
											parser.setInfix(firstStatment,"Statment 1");
											if(parser.checkValidationAndParseInfix())
											{
												postFix1 = parser.generatePostfix();
												postFix1.evaluateTruthTable();
												
												numberOfValid++;
												if(model1.getIndexOf(parser.getInfix())==-1)
												{model1.addElement(parser.getInfix());}
												
												textView.setText("");
												evauation.setText("");
												whichOne = "Statment 1";
											}
											else postFix1 = null;
											
											// 2nd statement
											parser.setInfix(secondStatment,"Statment 2");
											if(parser.checkValidationAndParseInfix())
											{
												postFix2 = parser.generatePostfix();
												postFix2.evaluateTruthTable();
												
												numberOfValid++;
												if(model1.getIndexOf(parser.getInfix())==-1)
												{model1.addElement(parser.getInfix());}
												
												textView.setText("");
												evauation.setText("");
												whichOne = "Statment 2";
											}
											else postFix2 = null;
											
											if(numberOfValid!=0)
											{
												equivalentStatus.setText("");
												//System.out.println("End Evaluation Time = "+(System.nanoTime() - startTime) / Math.pow(10,9));
											}
											//print
											if(numberOfValid==1) printer(true,whichOne);
											else if(numberOfValid == 2) 
											{
											    if(postFix2.compareWith(postFix1))
											    equivalentStatus.setText("<html><font color='green'>Equivalent</font></html>");
											    else	equivalentStatus.setText("<html><font color='red'>Not Equivalent</font></html>"); 
											    printer(false,"");
											}
											break;
								}
							}
							public int settedComboBox()
							{
								String firstStatment = firstStatment_CB.getSelectedItem().toString();
								String secondStatment = secondStatment_CB.getSelectedItem().toString();
								if((!firstStatment.equals("") && secondStatment.equals("")) || (firstStatment.equals("") && !secondStatment.equals("")) )	
								return 1;
								else if(!firstStatment.equals("") && !secondStatment.equals(""))	return 2;
								return 0;
							}
						});
						
						panel.add(generate_B);
///////////////////////////////////////////////////////////////////// 2nd statement label addition ////////////////////////////////
		panel.add(secondStatment_L);
/////////////////////////////////////////////////////////////////////// 2nd statement combo box addition/////////////////////////
		panel.add(secondStatment_CB);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, SystemColor.window, new Color(109, 109, 109), SystemColor.window, new Color(105, 105, 105)));
		contentPane.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3, BorderLayout.WEST);
		
		JLabel status = new JLabel("Status:");
		status.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_3.add(status);
		
		
		panel_3.add(evauation);
		
		JPanel panel_4 = new JPanel();
		panel_1.add(panel_4, BorderLayout.EAST);
		
		JLabel lblTime = new JLabel("Time:");
		lblTime.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_4.add(lblTime);
		
		panel_4.add(time);
		
		
		equivalentStatus.setFont(new Font("Tahoma", Font.BOLD, 11));
		equivalentStatus.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(equivalentStatus, BorderLayout.CENTER);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_2.setBackground(new Color(230, 230, 250));
		contentPane.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);
		
		
		
		scrollPane.setViewportView(textView);
	}
	
	public void printer(boolean oneStatment,String whichOne)
	{
		double startPrintTime = System.nanoTime();
		
		if(oneStatment)
		{
		    PostfixExpression postFix = (postFix1==null)? postFix2 :postFix1;
		    boolean [][] truthTable = postFix.getTruthTableArray()  ;
		    boolean [] output = postFix.getResultArray();
			ArrayList<String> variables = postFix.getVariablesNames();
			int numberOfVariables = variables.size();
			String inputsAndOutput = "";
			
			// get variables input and outputs
			for(int i = 0 ; i < numberOfVariables+1 ; i++ )
			{
			    inputsAndOutput+=createInAndOutLine(i,numberOfVariables,variables,postFix);
			}
			
			String dashes = "";
			// print upper line
			for(int i = 0 ; i < inputsAndOutput.length() ; i++ )    dashes+="-";
			
			// print dashes, pass a line , print input , output and pass line , print dashes
			textView.setText(dashes+"\n"+inputsAndOutput+"\n"+dashes);
			
			String result;
			String row;
			long z =  (long) Math.pow(2,numberOfVariables);
			
			for(int i = 0 ; i < z ; i++) // row
			{
			    row =""; 
			    row = createRow(i,truthTable,output,numberOfVariables,variables,postFix);
			    textView.setText(textView.getText()+"\n"+row);
			}
			textView.setText(textView.getText()+"\n"+dashes);
			
			
			if(postFix.checkTautology().equalsIgnoreCase("Tautology")) evauation.setText("<html>"+whichOne+": "+"<font color='green'>Tautology</font></html>");
			else if (postFix.checkTautology().equalsIgnoreCase("Contradiction")) evauation.setText("<html>"+whichOne+": "+"<font color='red'>Contradiction</font></html>"); 
			else evauation.setText("<html>"+whichOne+": "+"<font color='black'>Neither</font></html>"); 
		}
		else
		{
		    // first expression
		    boolean [][] truthTable1 = postFix1.getTruthTableArray()  ;
		    boolean [] output1 = postFix1.getResultArray();
			ArrayList<String> variables1 = postFix1.getVariablesNames();
			int numberOfVariables1 = variables1.size();
			// second expression
		    boolean [][] truthTable2 = postFix2.getTruthTableArray()  ;
		    boolean [] output2 = postFix2.getResultArray();
			ArrayList<String> variables2 = postFix2.getVariablesNames();
			int numberOfVariables2 = variables2.size();
			
			// input and ouput line
			String inputsAndOutput = "";
			
			int indexOfSeparator = 0;
			// get variables input and outputs
			for(int i = 0 ; i < numberOfVariables1+numberOfVariables2+2 ; i++ )
			{
			    if(i < numberOfVariables1+1)
			    {
    				inputsAndOutput+=createInAndOutLine(i,numberOfVariables1,variables1,postFix1) ;
    				if(i == numberOfVariables1)
    				{
    				    indexOfSeparator = inputsAndOutput.length();
    				    inputsAndOutput+="    *    ";
    				}
			    } 
			    else
			    {inputsAndOutput+=createInAndOutLine(i-numberOfVariables1 -1 , numberOfVariables2  , variables2 , postFix2);}
			}
			
			String dashes = "";
			// print upper line
			for(int i = 0 ; i < inputsAndOutput.length() ; i++ )
			{
			    if(i<indexOfSeparator ||i>indexOfSeparator+8)  dashes+="-";
			    else if(i==indexOfSeparator) dashes+="    *    ";
			}
			
			// print dashes, pass a line , print input , output and pass line , print dashes
			textView.setText(dashes+"\n"+inputsAndOutput+"\n"+dashes);
			
			
			String row;
			long z1 =  (long) Math.pow(2,numberOfVariables1);
			long z2 =  (long) Math.pow(2,numberOfVariables2);
			long z = Math.max(z1,z2);
            
            String dashes1 = "",dashes2 = "";     
            String spaces1="",spaces2="";
           
            for(int j = 0; j<indexOfSeparator ; j++)
            {
            	dashes1+="-";
            	spaces1+=" ";
            }
            
		    for(int j = indexOfSeparator+9; j<inputsAndOutput.length() ; j++)
		    {
		    	dashes2+="-";
		    	spaces2+=" ";
		    }
			 
			
			for(int i = 0 ; i < z+1 ; i++) // row
			{
			    row = "";
			    if (i<z1) row+=createRow(i,truthTable1,output1,numberOfVariables1,variables1,postFix1);
			    else if(i==z1)row += dashes1;// print dashes
			    else  row+=spaces1;// print spaces
			    
			    row+="    *    ";
			    
			    if (i<z2) row+=createRow(i,truthTable2,output2,numberOfVariables2,variables2,postFix2);
			    else if(i==z2) row+=dashes2;// print dashes
			    else row+=spaces2; //print spaces
			    
			    textView.setText(textView.getText()+"\n"+row);
			}
			
			if(postFix1.checkTautology().equals("Tautology")) 
			{
				if(postFix2.checkTautology().equals("Tautology"))
				evauation.setText("<html>"+"Statmen 1: "+"<font color='green'>Tautology</font>"+", Statment 2: "+"<font color='green'>Tautology</font>"+"</html>");
				else if(postFix2.checkTautology().equals("Contradiction")) evauation.setText("<html>"+"Statmen 1: "+"<font color='green'>Tautology</font>"+", Statment 2: "+"<font color='red'>Contradiction</font>"+"</html>");
				else evauation.setText("<html>"+"Statmen 1: "+"<font color='green'>Tautology</font>"+", Statment 2: "+"<font color='black'>Neither</font>"+"</html>");
			}
			else if (postFix1.checkTautology().equals("Contradiction")) 
			{
				if(postFix2.checkTautology().equals("Tautology"))
				evauation.setText("<html>"+"Statmen 1: "+"<font color='red'>Contradiction</font>"+", Statment 2: "+"<font color='green'>Tautology</font>"+"</html>");
				else if(postFix2.checkTautology().equals("Contradiction")) evauation.setText("<html>"+"Statmen 1: "+"<font color='red'>Contradiction</font>"+", Statment 2: "+"<font color='red'>Contradiction</font>"+"</html>");
				else evauation.setText("<html>"+"Statmen 1: "+"<font color='red'>Contradiction</font>"+", Statment 2: "+"<font color='black'>Neither</font>"+"</html>");
			}
			else // niether
			{
			    if(postFix2.checkTautology().equals("Tautology"))
				evauation.setText("<html>"+"Statmen 1: "+"<font color='black'>Niether</font>"+", Statment 2: "+"<font color='green'>Tautology</font>"+"</html>");
				else if(postFix2.checkTautology().equals("Contradiction")) evauation.setText("<html>"+"Statmen 1: "+"<font color='black'>Neither</font>"+", Statment 2: "+"<font color='red'>Contradiction</font>"+"</html>");
				else evauation.setText("<html>"+"Statmen 1: "+"<font color='black'>Niether</font>"+", Statment 2: "+"<font color='black'>Neither</font>"+"</html>");
			    
			}
		}
		
		//System.out.println("End Print Time = "+ ((System.nanoTime() - startPrintTime) / Math.pow(10,9)));
		// show total time
		time.setText(((System.nanoTime() - startTime) / Math.pow(10,9))+" sec");
		//System.out.println("Total Time = " + time.getText().toString());
	}
	
	public String createInAndOutLine(int i,int numberOfVariables,ArrayList<String> variables, PostfixExpression postFix)
	{
	    String inputsAndOutput="";
	    if(i < numberOfVariables)
		{
			if(i==0) inputsAndOutput+="| "+variables.get(i);
			else inputsAndOutput+=" | "+variables.get(i);
		}
		else
		{
		    if(postFix.getInfix().length()%2 == 0) inputsAndOutput+="  ||  ";
		    else inputsAndOutput+=" || ";
		    inputsAndOutput+=postFix.getInfix()+" |";
	    }
	    return inputsAndOutput;
	}
	
	public String createRow(int i, boolean[][] truthTable,boolean[] output, int numberOfVariables,ArrayList<String> variables, PostfixExpression postFix)
	{
		    String row = "",result;
    			for(int j = 0 ; j < numberOfVariables+1 ; j++) // col
    			{
    			    if(j < numberOfVariables)
    			    {
    			        if(j==0)
    			        {
    			            result = (truthTable[i][j])? "T" : "F" ;      
    			            row+="| "+result;
    			        }
    					else
    					{
    					    result = (truthTable[i][j])? "T" : "F" ;      
    					    row+=" | "+result;
    					}
    					if(variables.get(j).length() == 2) row+=" ";
    			    }
    			    else
    			    {
    			        
    			        
    			        int deal = 0;
    			        if(postFix.getInfix().length()%2 == 0)
    			        {
    			            row+="  ||  ";
    			            deal = 1;
    			        }
    			        else row+=" || ";
    			        
    			        for(int k = 0 ; k < postFix.getInfix().length()/2 - deal ; k++)
    			        {
    			            row+=" ";
    			        }
    			        result = (output[i])? "T" : "F" ;
    			        row+=result;
    			        for(int k = 0 ; k < (postFix.getInfix().length()/2) ; k++)
    			        {
    			            row+=" ";
    			        }
    			        row+=" |";
    			    }
    			}
    			return row;
	}
}
