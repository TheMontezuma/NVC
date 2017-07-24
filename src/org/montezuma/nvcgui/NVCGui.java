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
	private JPanel panel1;
	private JPanel panel2;
	private JPanel panel3a;
	private JPanel panel3b;
	private JPanel panel4;
	private JPanel panel5;
	private JPanel panel6;
	private JPanel panel7;
	private JPanel panel8;
	private JTextField input_directory_name;
	private JTextField output_directory_name;
	private JComboBox<Integer> output_directory_depth;
	private JComboBox<String> file_name_capitalization;
	private JTextField suffix;
	private JLabel suffix_list;
	private JButton browse_for_input_directory;
	private JButton browse_for_output_directory;
	private JButton add_new_suffix;
	private JButton reset_suffix_list;
	private JButton start;
	private JCheckBox force;
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
       
       panel1 = new JPanel();
       panel2 = new JPanel();
       panel3a = new JPanel();
       panel3b = new JPanel();
       panel4 = new JPanel();
       panel5 = new JPanel();
       panel6 = new JPanel();
       panel7 = new JPanel();
       panel8 = new JPanel();
       
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

       file_name_capitalization = new JComboBox<String>();
       file_name_capitalization.addItem("none");
       file_name_capitalization.addItem("UPPER CASE");
       file_name_capitalization.addItem("lower case");
       file_name_capitalization.addItem("Capitalized");
       file_name_capitalization.setSelectedIndex(nvc.op.capitalization);
       file_name_capitalization.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			nvc.op.capitalization = file_name_capitalization.getSelectedIndex();
		} } );

       
       force = new JCheckBox("Force directory creation at the specified depth", nvc.op.force);
       force.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			nvc.op.force = (ItemEvent.SELECTED == e.getStateChange());
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
       
       ((FlowLayout)panel1.getLayout()).setAlignment(FlowLayout.TRAILING);
       ((FlowLayout)panel2.getLayout()).setAlignment(FlowLayout.TRAILING);
       ((FlowLayout)panel3a.getLayout()).setAlignment(FlowLayout.TRAILING);
       ((FlowLayout)panel3b.getLayout()).setAlignment(FlowLayout.TRAILING);
       ((FlowLayout)panel4.getLayout()).setAlignment(FlowLayout.TRAILING);
       ((FlowLayout)panel5.getLayout()).setAlignment(FlowLayout.TRAILING);
       ((FlowLayout)panel6.getLayout()).setAlignment(FlowLayout.LEADING);
       ((FlowLayout)panel8.getLayout()).setAlignment(FlowLayout.TRAILING);
       
       panel1.add(new JLabel("Input directory:"));
       panel1.add(input_directory_name);
       panel1.add(browse_for_input_directory);
       panel2.add(new JLabel("Output directory:"));
       panel2.add(output_directory_name);
       panel2.add(browse_for_output_directory);
       panel3a.add(new JLabel("Max output directory structure depth:"));
       panel3a.add(output_directory_depth);
       panel3b.add(new JLabel("Output file names capitalization:"));
       panel3b.add(file_name_capitalization);
       panel4.add(force);
       panel5.add(new JLabel("New Suffix: "));
       panel5.add(suffix);
       panel5.add(add_new_suffix);
       panel5.add(reset_suffix_list);
       panel6.add(new JLabel("Suffix list: "));
       StringBuffer sb = new StringBuffer();
       for(String s:nvc.op.suffixes)
       {
    	   sb.append(s);
    	   sb.append(" ");
       }
       suffix_list.setText(sb.toString());
       panel6.add(suffix_list);
       panel7.add(start);
       panel8.add(new JLabel("Ver." + NVCMain.version + "   Montezuma 2011-2017"));
       
       frame.add(panel1);
       frame.add(panel2);
       frame.add(panel3a);
       frame.add(panel3b);
       frame.add(panel4);
       frame.add(panel5);
       frame.add(panel6);
       frame.add(panel7);
       frame.add(panel8);

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
