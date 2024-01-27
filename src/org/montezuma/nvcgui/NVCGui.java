package org.montezuma.nvcgui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.*;

public class NVCGui implements	Runnable,
								PropertyChangeListener
{

	private JFrame frame;
	private JPanel panelInDir;
	private JPanel panelOutDir;
	private JPanel panelFileCount;
	private JPanel panelDirDepth;
	private JPanel panelCapitalization;
	private JPanel panelDirCapitalization;
	private JPanel panelForceDir;
	private JPanel panelShortFileNames;
	private JPanel panelNewSuffix;
	private JPanel panelSuffixes;
	private JPanel panelStart;
	private JPanel panelNVCVersion;
	private JTextField input_directory_name;
	private JTextField output_directory_name;
	private JTextField output_directory_file_count;
	private JComboBox<Integer> output_directory_depth;
	private JComboBox<String> file_name_capitalization;
	private JComboBox<String> dir_name_capitalization;
	private JTextField suffix;
	private JLabel suffix_list;
	private JButton browse_for_input_directory;
	private JButton browse_for_output_directory;
	private JButton add_new_suffix;
	private JButton reset_suffix_list;
	private JButton start;
	private JCheckBox force;
	private JCheckBox short_file_names;
	private ProgressMonitor progressMonitor;
	private NVCWorker nvcworker;
	
	public NVCGui(NVCMain nvc) {
		this.nvc = nvc;
	}

	NVCMain nvc;

	public void run()
	{
		NVCPersistence.install(nvc.op);
		NVCPersistence.read();
		createAndShowGUI();
	}
	
	private void createAndShowGUI()
	{
       frame = new JFrame("NVC - Next Valid Character");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.addWindowListener(new NVCWindowListener());
       frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
       
       panelInDir = new JPanel();
       panelOutDir = new JPanel();
       panelFileCount = new JPanel();
       panelDirDepth = new JPanel();
       panelCapitalization = new JPanel();
       panelDirCapitalization = new JPanel();
       panelForceDir = new JPanel();
       panelShortFileNames = new JPanel();
       panelNewSuffix = new JPanel();
       panelSuffixes = new JPanel();
       panelStart = new JPanel();
       panelNVCVersion = new JPanel();
       
       input_directory_name = new JTextField(nvc.op.from, 30);
       input_directory_name.addFocusListener(new FocusListener() {
		public void focusLost(FocusEvent e) {
   			String fnametmp = input_directory_name.getText().trim();
   			if(!fnametmp.isEmpty())
   			{
   				nvc.op.from = fnametmp; 
   			}
   			input_directory_name.setText(nvc.op.from);
		}
		public void focusGained(FocusEvent e) {
		} } );
       output_directory_name = new JTextField(nvc.op.to, 30);
       output_directory_name.addFocusListener(new FocusListener() {
   		public void focusLost(FocusEvent e) {
   			String fnametmp = output_directory_name.getText().trim();
   			if(!fnametmp.isEmpty())
   			{
   				nvc.op.to = fnametmp; 
   			}
   			output_directory_name.setText(nvc.op.to);
   		}
   		public void focusGained(FocusEvent e) {
   		} } );
       
       browse_for_input_directory = new JButton("Browse");
       browse_for_input_directory.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser(input_directory_name.getText());
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                nvc.op.from = fc.getSelectedFile().getPath();
                input_directory_name.setText(nvc.op.from);
            }
		} } );
       
       browse_for_output_directory = new JButton("Browse");
       browse_for_output_directory.addActionListener(new ActionListener() {
   		public void actionPerformed(ActionEvent e) {
   			JFileChooser fc = new JFileChooser(output_directory_name.getText());
   			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
               if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                   nvc.op.to = fc.getSelectedFile().getPath();
                   output_directory_name.setText(nvc.op.to);
               }
   		} } );
       
       output_directory_depth = new JComboBox<Integer>();
       for(int i=0 ; i<=10 ; i++)
       {
    	   output_directory_depth.addItem(new Integer(i));
       }
       output_directory_depth.setSelectedIndex(nvc.op.level);
       output_directory_depth.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			nvc.op.level = output_directory_depth.getSelectedIndex(); 
		} } );
       
       output_directory_file_count = new JTextField(String.valueOf(nvc.op.filecount),4);
       output_directory_file_count.setHorizontalAlignment(JTextField.TRAILING);
       output_directory_file_count.addFocusListener(new FocusListener() {
   		public void focusLost(FocusEvent e) {
   			String fcount = output_directory_file_count.getText().trim();
   			if(!fcount.isEmpty())
   			{
   				try{
   				nvc.op.filecount = Integer.parseInt(fcount);
   				}
   				catch(java.lang.NumberFormatException nfe)
   				{
   				}
   			}
   			output_directory_file_count.setText(String.valueOf(nvc.op.filecount));
   		}
   		public void focusGained(FocusEvent e) {
   		} } );
       
       file_name_capitalization = new JComboBox<String>();
       file_name_capitalization.addItem("none");
       file_name_capitalization.addItem("UPPER CASE");
       file_name_capitalization.addItem("lower case");
       file_name_capitalization.addItem("Capitalized");
       file_name_capitalization.addItem("Like In A Title");
       file_name_capitalization.setSelectedIndex(nvc.op.capitalization);
       file_name_capitalization.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			nvc.op.capitalization = file_name_capitalization.getSelectedIndex();
		} } );
       
       dir_name_capitalization = new JComboBox<String>();
       dir_name_capitalization.addItem("UPPER CASE");
       dir_name_capitalization.addItem("lower case");
       dir_name_capitalization.setSelectedIndex(nvc.op.dir_capitalization);
       dir_name_capitalization.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			nvc.op.dir_capitalization = dir_name_capitalization.getSelectedIndex();
		} } );
       
       force = new JCheckBox("Force directory creation for files with unique names", nvc.op.force);
       force.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			nvc.op.force = (ItemEvent.SELECTED == e.getStateChange());
		} } );
       
       short_file_names = new JCheckBox("Trim file names to 8.3", nvc.op.short_file_names);
       short_file_names.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			nvc.op.short_file_names = (ItemEvent.SELECTED == e.getStateChange());
		} } );
       
       suffix = new JTextField(3);
       suffix.addActionListener(new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
   			if(		(!(suffix.getText().isEmpty())) &&
   					(!(suffix.getText().contains(" "))) &&
   					(nvc.op.suffixes.indexOf(suffix.getText().toUpperCase()) == -1))
   			{
   				nvc.op.suffixes.add(suffix.getText().toUpperCase());
   				StringBuffer sb = new StringBuffer();
   				for(String s:nvc.op.suffixes)
   				{
   					sb.append(s);
   					sb.append(" ");
   				}
   				suffix_list.setText(sb.toString());
   			}
   			suffix.setText(null);
		} } );
       
       suffix_list = new JLabel();
       add_new_suffix = new JButton("Add");
       add_new_suffix.addActionListener(new ActionListener() {
   		public void actionPerformed(ActionEvent e) {
   			if(		(!(suffix.getText().isEmpty())) &&
   					(!(suffix.getText().contains(" "))) &&
   					(nvc.op.suffixes.indexOf(suffix.getText().toUpperCase()) == -1))
   			{
   				nvc.op.suffixes.add(suffix.getText().toUpperCase());
   				StringBuffer sb = new StringBuffer();
   				for(String s:nvc.op.suffixes)
   				{
   					sb.append(s);
   					sb.append(" ");
   				}
   				suffix_list.setText(sb.toString());
   			}
   			suffix.setText(null);
   		} } );
       reset_suffix_list = new JButton("Reset suffix list");
       reset_suffix_list.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			nvc.op.suffixes.clear();
			suffix_list.setText(null);
		} } );
       
       start = new JButton("Start");
       start.addActionListener(new ActionListener() {
      		public void actionPerformed(ActionEvent e) {
      			if(!(new File(nvc.op.from)).isDirectory())
      			{
      				Toolkit.getDefaultToolkit().beep();
      				JOptionPane.showMessageDialog(frame, "\"" + nvc.op.from + "\" is not an existing diectory");
      			}
      			else if(nvc.op.suffixes.size() == 0)
      			{
      				Toolkit.getDefaultToolkit().beep();
      				JOptionPane.showMessageDialog(frame, "Add at least one suffix");
      			}
      			else
      			{
      		        progressMonitor = new ProgressMonitor(frame, "Please wait...","Parsing directories...", 0, 100);
      		        progressMonitor.setMillisToDecideToPopup(0);
      		        progressMonitor.setMillisToPopup(0);
      		        progressMonitor.setProgress(0);
      		        nvcworker = new NVCWorker(nvc, NVCGui.this);
      		        nvcworker.addPropertyChangeListener(NVCGui.this);
  					frame.setEnabled(false);
  					nvcworker.execute();
      			}
     		} } );
       
       ((FlowLayout)panelInDir.getLayout()).setAlignment(FlowLayout.TRAILING);
       ((FlowLayout)panelOutDir.getLayout()).setAlignment(FlowLayout.TRAILING);
       ((FlowLayout)panelFileCount.getLayout()).setAlignment(FlowLayout.TRAILING);
       ((FlowLayout)panelDirDepth.getLayout()).setAlignment(FlowLayout.TRAILING);
       ((FlowLayout)panelCapitalization.getLayout()).setAlignment(FlowLayout.TRAILING);
       ((FlowLayout)panelDirCapitalization.getLayout()).setAlignment(FlowLayout.TRAILING);
       ((FlowLayout)panelForceDir.getLayout()).setAlignment(FlowLayout.TRAILING);
       ((FlowLayout)panelShortFileNames.getLayout()).setAlignment(FlowLayout.TRAILING);
       ((FlowLayout)panelNewSuffix.getLayout()).setAlignment(FlowLayout.TRAILING);
       ((FlowLayout)panelSuffixes.getLayout()).setAlignment(FlowLayout.LEADING);
       ((FlowLayout)panelNVCVersion.getLayout()).setAlignment(FlowLayout.TRAILING);
       
       panelInDir.add(new JLabel("Input directory:"));
       panelInDir.add(input_directory_name);
       panelInDir.add(browse_for_input_directory);
       panelOutDir.add(new JLabel("Output directory:"));
       panelOutDir.add(output_directory_name);
       panelOutDir.add(browse_for_output_directory);

       panelFileCount.add(new JLabel("Preferred number of files in output directories (0 = no limit):"));
       panelFileCount.add(output_directory_file_count);
            
       panelDirDepth.add(new JLabel("Preferred output directory structure depth:"));
       panelDirDepth.add(output_directory_depth);
       panelCapitalization.add(new JLabel("Output file names capitalization:"));
       panelCapitalization.add(file_name_capitalization);
       panelDirCapitalization.add(new JLabel("Output directory capitalization:"));
       panelDirCapitalization.add(dir_name_capitalization);
       panelForceDir.add(force);
       panelShortFileNames.add(short_file_names);
       panelNewSuffix.add(new JLabel("New Suffix: "));
       panelNewSuffix.add(suffix);
       panelNewSuffix.add(add_new_suffix);
       panelNewSuffix.add(reset_suffix_list);
       panelSuffixes.add(new JLabel("Suffix list: "));
       StringBuffer sb = new StringBuffer();
       for(String s:nvc.op.suffixes)
       {
    	   sb.append(s);
    	   sb.append(" ");
       }
       suffix_list.setText(sb.toString());
       panelSuffixes.add(suffix_list);
       panelStart.add(start);
       panelNVCVersion.add(new JLabel("Ver." + NVCMain.version + "   Montezuma 2011-2024"));
       
       frame.add(panelInDir);
       frame.add(panelOutDir);
       frame.add(panelFileCount);
       frame.add(panelDirDepth);
       frame.add(panelForceDir);
       frame.add(panelShortFileNames);
       frame.add(panelCapitalization);
       frame.add(panelDirCapitalization);
       frame.add(panelNewSuffix);
       frame.add(panelSuffixes);
       frame.add(panelStart);
       frame.add(panelNVCVersion);

       frame.pack();
       frame.setResizable(false);
       frame.setVisible(true);
	}
	
	public void onJobFinished()
	{
		frame.setEnabled(true);
		progressMonitor.setProgress(100);
	}

	public void propertyChange(PropertyChangeEvent evt) {
			if ("progress" == evt.getPropertyName() ) 
			{
	            int progress = (Integer) evt.getNewValue();
	            progressMonitor.setProgress(progress);
	            String message =
	                String.format("Copying files %d%%.\n", progress);
	            progressMonitor.setNote(message);
				
	            if (progressMonitor.isCanceled() || nvcworker.isDone()) {
	                Toolkit.getDefaultToolkit().beep();
	                if (progressMonitor.isCanceled()) {
	                	nvcworker.cancel(true);
	                }
	                frame.setEnabled(true);
	            }
			}
	}

}
