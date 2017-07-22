package org.montezuma.nvcgui;

import org.montezuma.nvcgui.NVCFileNamePair;
import org.montezuma.nvcgui.NVCOptions;

import org.kohsuke.args4j.*;

import java.util.*;
import java.io.*;

public class NVCMain {
	
	final static int version = 9;
	
	NVCOptions op = new NVCOptions();
	NVCGui gui;

	LinkedList<NVCFileNamePair> files;
	File output_directory;
	FileWriter content_writer;
	String line_separator = System.getProperty("line.separator");
	
	final String invalid_names[] = {     
			"CON",
		    "PRN",
		    "AUX",
		    "NUL",
		    "COM1",
		    "COM2",
		    "COM3",
		    "COM4",
		    "COM5",
		    "COM6",
		    "COM7",
		    "COM8",
		    "COM9",
		    "LPT1",
		    "LPT2",
		    "LPT3",
		    "LPT4",
		    "LPT5",
		    "LPT6",
		    "LPT7",
		    "LPT8",
		    "LPT9"
 };

	public static void main(String[] args) {
		
		NVCMain nvc = new NVCMain();
		if(args.length != 0)
		{
		    CmdLineParser parser = new CmdLineParser(nvc.op);
		    try 
		    {
		        parser.parseArgument(args);
			    if(!((new File(nvc.op.from)).isDirectory()))
			    {
			    	throw new CmdLineException(parser,nvc.op.from+" is not an existing directory!");
			    }	        
		    } 
		    catch( CmdLineException e ) 
		    {
		        System.err.println(e.getMessage());
		        System.err.println("java -jar nvc.jar -i INPUT [-o OUTPUT -l LEVEL -c CAPITALIZATION -f] suffix1 [suffix2 ... suffixn]");
		        parser.printUsage(System.err);
		        return;
		    }
		    nvc.restructure(null);
		}
		else
		{
			nvc.gui = new NVCGui(nvc);
			javax.swing.SwingUtilities.invokeLater(nvc.gui);
		}
	}

	void restructure(NVCWorker worker) {
		try {
			output_directory = new File(op.to);
			output_directory.mkdirs();
			content_writer = new FileWriter(op.to+"/content.txt");
			files = new LinkedList<NVCFileNamePair>();
			if(gui == null)
			{
				System.err.println("Parsing input directories...");
			}
			getFileList(new File(op.from));
			Collections.sort(files, new Comparator<NVCFileNamePair>() {
				public int compare(NVCFileNamePair p1, NVCFileNamePair p2) {
					return p1.fileName.compareTo(p2.fileName);
				}
			});
			if(gui == null)
			{
				System.err.println("Copying files...");
			}
			ListIterator<NVCFileNamePair> it = files.listIterator();
			char last_char = 0;
			int dup_counter = 1;
			int file_count = files.size();
			int file_counter = 0;
			
			while (it.hasNext()) {
				file_counter++;
				NVCFileNamePair theFile = it.next();
				String input_file_name = theFile.fileName;
				if (last_char != input_file_name.charAt(0)) {
					last_char = input_file_name.charAt(0);
					if(gui == null)
					{
						System.err.print(last_char); // progress tracking for command line
					}
				}
				StringBuffer output_path = new StringBuffer(op.to);
				if(!Character.isLetter(last_char))
				{
					// put all files which names don't start with a letter in the '#' directory 
					output_path.append("/#");
				}
				StringBuffer tmp = new StringBuffer();
				final int max_val = Math.min(input_file_name.length() - 4, op.level);
				int i = 0;
				while (i < max_val) {
					do {
						tmp.append(input_file_name.charAt(i));
						i++;
					} while ((i < max_val) && hasUniqueNVC(tmp.toString()));
					if(!(op.force) && isUnique(tmp.toString()))
					{
						break;
					}
					boolean append_ = false;
					for (int inv = 0; inv < invalid_names.length; ++inv) {
						if (tmp.toString().compareToIgnoreCase(
								invalid_names[inv]) == 0) {
							append_ = true;
							break;
						}
					}
					output_path.append('/');
					output_path.append(tmp.toString().replace(' ','_'));
					if (append_) {
						output_path.append('_');
					}
				}
				output_path.append('/');
				String output_file_path = output_path.toString();
				File output_dir = new File(output_file_path);
				output_dir.mkdirs();
				
				File input_file = new File(theFile.filePath);
				File output_file = new File(output_file_path + input_file_name);
				
				if(output_file.exists())
				{
					if( 0 != BinFileCompare(input_file, output_file) )
					{
						int ind_tmp = input_file_name.lastIndexOf('.');
						StringBuffer sb_tmp = new StringBuffer(output_file_path);
						sb_tmp.append(input_file_name.substring(0,ind_tmp));
						sb_tmp.append(" NVCVER");
						sb_tmp.append(++dup_counter);
						sb_tmp.append(input_file_name.substring(ind_tmp));
						output_file = new File( sb_tmp.toString() );
						output_file.createNewFile();
						BinFileCopy(input_file, output_file);
					}
				}
				else
				{
					output_file.createNewFile();
					BinFileCopy(input_file, output_file);
					dup_counter = 1;
				}
				if(content_writer != null)
				{
					content_writer.write(output_file.getName()+line_separator);
				}
				if(worker != null)
				{
					if(worker.isWorkerCancelled())
					{
						if(content_writer != null)
						{
							content_writer.close();
						}
						return;
					}
					worker.setWorkerProgress((file_counter*100)/file_count);
				}
			}
			if(content_writer != null)
			{
				content_writer.close();
			}
			if(gui == null)
			{
				System.err.println();
			}

		} catch (Exception e) {
			System.err.println(line_separator+e);
			System.exit(-1);
		}
	}

	private boolean isUnique(String prefix)
	{
		int count=0;
		ListIterator<NVCFileNamePair> iterator = files.listIterator();
		while ( iterator.hasNext() && count < 2)
		{
			String tmp = iterator.next().fileName;
			if(tmp.startsWith(prefix))
			{
				count++;
			}
		}
		return (count==1);
	}

	private boolean hasUniqueNVC(String prefix) {
		Collection<Character> nvc = new HashSet<Character>();

		ListIterator<NVCFileNamePair> iterator = files.listIterator();
		while (iterator.hasNext()) {
			String tmp = iterator.next().fileName;
			if (tmp.startsWith(prefix)) {
				int prefix_length = prefix.length();
				if (tmp.length() > prefix_length) {
					nvc.add(tmp.charAt(prefix_length));
				}
			}
		}
		return (nvc.size() == 1);
	}

	private void getFileList(File f) {
		try{
		if (!f.isDirectory()) {
			String tmp = f.getName();
			for (int i = 0; i < op.suffixes.size(); ++i) {
				if (tmp.toUpperCase().endsWith(op.suffixes.get(i).toUpperCase())) {
					switch(op.capitalization)
					{
					case 0: // none
						files.add(new NVCFileNamePair(f.getPath(), f.getName()));
						break;
					case 1: // UPPER CASE
						files.add(new NVCFileNamePair(f.getPath(), f.getName().toUpperCase()));
						break;
					case 2: // lower case
						files.add(new NVCFileNamePair(f.getPath(), f.getName().toLowerCase()));
						break;
					case 3: // Capitalized
						files.add(new NVCFileNamePair(f.getPath(), Character.toUpperCase(f.getName().charAt(0)) + f.getName().substring(1).toLowerCase()));
						break;					
					}
					break;
				}
			}
			
		} else if(!output_directory.getCanonicalPath().equals(f.getCanonicalPath()))
			{
				File fList[] = f.listFiles();
				for (int i = 0; i < fList.length; i++)
					getFileList(fList[i]);
			}
		}
		catch (Exception e) {
			// skip the file or directory and continue
		}
	}

	private void BinFileCopy(File inputFile, File outputFile) throws Exception {
		FileInputStream in = new FileInputStream(inputFile);
		FileOutputStream out = new FileOutputStream(outputFile);
		byte[] b = new byte[0xFFFF];
		int count;

		while ((count = in.read(b)) != -1)
			out.write(b, 0, count);

		in.close();
		out.close();
	}

	private int BinFileCompare(File f1, File f2) throws Exception {
		int retval = 0;
		FileInputStream fis1 = new FileInputStream(f1);
		FileInputStream fis2 = new FileInputStream(f2);
		byte[] b1 = new byte[0xFFFF];
		byte[] b2 = new byte[0xFFFF];
		int count1;
		int count2;
		do
		{
			count1 = fis1.read(b1);
			count2 = fis2.read(b2);
			if((count1 != count2) || !(Arrays.equals(b1, b2)))
			{
				retval = -1;
				break;
			}
		} while ( (count1!=-1) && (count2!=-1) );
		fis1.close();
		fis2.close();
		return retval;
	}

}