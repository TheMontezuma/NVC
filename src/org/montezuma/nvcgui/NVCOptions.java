package org.montezuma.nvcgui;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.*; 

public class NVCOptions {
    @Option(name="-i",usage="input directory, example: \"input\"", metaVar="INPUT", required=true)
    public String from = ".";
	
	@Option(name="-o",usage="output directory, example: \"ATARI\"", metaVar="OUTPUT", required=true)
    public String to = "output";

    @Option(name="-l",usage="preferred output directory depth, example: 3", metaVar="LEVEL")
    public int level = 1;
    
    @Option(name="-n",usage="preferred number of files in output directories, example: 100", metaVar="FILECOUNT")
    public int filecount = 100;

    @Option(name="-c",usage="0-none, 1-UPPER CASE, 2-lower case, 3-Capitalized, 4-Title Like, example: 1", metaVar="CAPITALIZATION")
    public int capitalization = 1;

    @Option(name="-d",usage="0-UPPER CASE, 1-lower case, example: 0", metaVar="DIR_CAPITALIZATION")
    public int dir_capitalization = 0;
    
    @Option(name="-f",usage="force directory creation for files with unique names")
    public boolean force = false;

    @Option(name="-s",usage="trim file names to 8.3")
    public boolean short_file_names = false;
    
    @Argument(usage="list of suffixes, example: ATR XEX XFD", metaVar="suffix", required=true, multiValued=true)
    public List<String> suffixes = new ArrayList<String>();
}
