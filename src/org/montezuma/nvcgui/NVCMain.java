package org.montezuma.nvcgui;

import org.montezuma.nvcgui.NVCFileNamePair;
import org.montezuma.nvcgui.NVCOptions;

import org.kohsuke.args4j.*;

import java.util.*;
import java.io.*;

public class NVCMain {
	
	final static int version = 18;
	byte[] BUFFER1 = new byte[0xFFFFF];
	byte[] BUFFER2 = new byte[0xFFFFF];
	
	NVCOptions op = new NVCOptions();
	NVCGui gui;

	LinkedList<NVCFileNamePair> mFiles;
	HashMap<String, Integer> mCountMap;
	
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

	enum eCapState 
	{
		START,
		IN_A_WORD
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
		        System.err.println("java -jar nvc.jar -i INPUT [-o OUTPUT -l LEVEL -n FILECOUNT -c CAPITALIZATION -d DIR_CAPITALIZATION -f -s] suffix1 [suffix2 ... suffixn]");
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
			mFiles = new LinkedList<NVCFileNamePair>();
			mCountMap = new HashMap<String, Integer>();
			
			if(gui == null)
			{
				System.err.println("Parsing input directories...");
			}
			getFileList(new File(op.from));
			Collections.sort(mFiles, new Comparator<NVCFileNamePair>() {
				public int compare(NVCFileNamePair p1, NVCFileNamePair p2) {
					return p1.fileName.compareTo(p2.fileName);
				}
			});
			if(gui == null)
			{
				System.err.println("Copying files...");
			}
			ListIterator<NVCFileNamePair> it = mFiles.listIterator();
			char last_char = 0;
			int dup_counter = 1;
			int file_count = mFiles.size();
			int file_counter = 0;

			while (it.hasNext()) {
				file_counter++;
				NVCFileNamePair theFile = it.next();
				String file_name = theFile.fileName;
				
				if (last_char != file_name.toUpperCase().charAt(0)) {
					last_char = file_name.toUpperCase().charAt(0);
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

				if(op.short_file_names)
				{
					int liold = file_name.lastIndexOf('.');
					String name = file_name.substring(0,liold);
					String suffix = file_name.substring(liold+1);
					name = name.replaceAll("[^a-zA-Z0-9]", "");
					// the user knows what suffix he wants, so we only trim it
					file_name = name.substring(0,Math.min(8,name.length())) 
								+ "." 
								+ suffix.substring(0,Math.min(3,suffix.length()));
				}
				
				final int index_of_last_dot = file_name.lastIndexOf('.');
				int max_val = Math.min(index_of_last_dot, op.level);
				if(op.filecount!=0)
				{
					Integer fcount = Integer.MAX_VALUE;
					int i = max_val;
					while(i<=index_of_last_dot)
					{
						String key = file_name.substring(0, i);
						if(mCountMap.containsKey(key))
						{
							fcount = mCountMap.get(key);
						}
						else
						{
							fcount = countFiles(key);
							mCountMap.put(key, fcount);
						}
						if(fcount <= op.filecount)
						{
							max_val = i;
							break;
						}
						i++;
					}
					if(i>index_of_last_dot)
					{
						max_val = index_of_last_dot;
					}
				}
				
				StringBuffer tmp = new StringBuffer();
				int i = 0;
				while (i < max_val) {
					do {
						if(op.dir_capitalization == 0) // UPPER CASE
						{
							tmp.append(file_name.toUpperCase().charAt(i));
						}
						else
						{
							tmp.append(file_name.toLowerCase().charAt(i));
						}
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
				File output_file = new File(output_file_path + file_name);
				
				if(output_file.exists())
				{
					boolean create_file_with_NVCVER_suffix = false;
					int dup_counter_index = dup_counter;
					if( 0 != BinFileCompare(input_file, output_file) )
					{
						// files with the same names have different content
						create_file_with_NVCVER_suffix = true;
					}
					
					// check if any existing file with the same name has identical content and if yes -> skip current file
					while(dup_counter_index > 1)
					{
						String n = generateFileName(output_file_path, file_name, dup_counter_index);
						output_file = new File(n);
						if( 0 == BinFileCompare(input_file, output_file) )
						{
							create_file_with_NVCVER_suffix = false;
							break;
						}
						dup_counter_index--;
					}
					
					if(create_file_with_NVCVER_suffix)
					{
						// generated file name may be identical to the name of an existing file :( 
						do
						{
							String n = generateFileName(output_file_path, file_name, ++dup_counter);
							output_file = new File(n);
						}
						while(output_file.exists());
						
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
	
	private String generateFileName(String output_file_path, String file_name, int index)
	{
		int ind_tmp = file_name.lastIndexOf('.');
		StringBuffer sb_tmp = new StringBuffer(output_file_path);
		if(op.short_file_names)
		{
			String index_str = String.valueOf(index);
			sb_tmp.append(file_name.substring(0,ind_tmp-index_str.length()));
			sb_tmp.append(index_str);
		}
		else
		{	
			sb_tmp.append(file_name.substring(0,ind_tmp));
			sb_tmp.append(" NVCVER");
			sb_tmp.append(index);
		}
		sb_tmp.append(file_name.substring(ind_tmp));
		return sb_tmp.toString();
	}

	private boolean isUnique(String prefix)
	{
		int count=0;
		ListIterator<NVCFileNamePair> iterator = mFiles.listIterator();
		while ( iterator.hasNext() && count < 2)
		{
			String tmp = iterator.next().fileName.toUpperCase();
			if(tmp.startsWith(prefix.toUpperCase()))
			{
				count++;
			}
		}
		return (count==1);
	}

	private boolean hasUniqueNVC(String prefix) {
		Collection<Character> nvc = new HashSet<Character>();
		ListIterator<NVCFileNamePair> iterator = mFiles.listIterator();
		while (iterator.hasNext()) {
			String tmp = iterator.next().fileName.toUpperCase();
			if (tmp.startsWith(prefix.toUpperCase())) {
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
						mFiles.add(new NVCFileNamePair(f.getPath(), f.getName()));
						break;
					case 1: // UPPER CASE
						mFiles.add(new NVCFileNamePair(f.getPath(), f.getName().toUpperCase()));
						break;
					case 2: // lower case
						mFiles.add(new NVCFileNamePair(f.getPath(), f.getName().toLowerCase()));
						break;
					case 3: // Capitalized
						mFiles.add(new NVCFileNamePair(f.getPath(), Character.toUpperCase(f.getName().charAt(0)) + f.getName().substring(1).toLowerCase()));
						break;
					case 4: // Title Like
						{
							eCapState state = eCapState.START;
							
							StringBuffer sb = new StringBuffer();
							int title_length = f.getName().length();
							for(int j=0 ; j<title_length ; j++)
							{
								char c = f.getName().charAt(j);
								switch(state)
								{
									case START:
										c = Character.toUpperCase(c);
										if(Character.isLetter(c))
										{
											state = eCapState.IN_A_WORD;
										}
										break;
									case IN_A_WORD:
										c = Character.toLowerCase(c);
										if(c!='\'' && !Character.isLetter(c))
										{
											state = eCapState.START;	
										}
										break;
								}
								sb.append(c);
							}
							
							String file_name = sb.toString();
							final int index_of_last_dot = file_name.lastIndexOf('.');
							mFiles.add(new NVCFileNamePair(f.getPath(), file_name.substring(0, index_of_last_dot) + file_name.substring(index_of_last_dot).toLowerCase()));
						}
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
	
	private int countFiles(String prefix)
	{
		int count = 0;
		String PREFIX = prefix.toUpperCase();
		for(NVCFileNamePair pair : mFiles)
		{
			if(pair.fileName.toUpperCase().startsWith(PREFIX))
			{
				count++;	
			}
		}
		return count;
	}
	

	private void BinFileCopy(File inputFile, File outputFile) throws Exception {
		FileInputStream in = new FileInputStream(inputFile);
		FileOutputStream out = new FileOutputStream(outputFile);
		byte[] b = BUFFER1;
		Arrays.fill(b, (byte) 0);
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
		byte[] b1 = BUFFER1;
		byte[] b2 = BUFFER2;
		Arrays.fill(b1, (byte) 0);
		Arrays.fill(b2, (byte) 0);
		
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
