package org.montezuma.nvcgui;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.*; 

public class NVCOptions {
    @Option(name="-i",usage="input directory, example: \"input\"", metaVar="INPUT", required=true)
    public String from = ".";
	
	@Option(name="-o",usage="output directory, example: \"ATARI\"", metaVar="OUTPUT", required=true)
    public String to = "output";

    @Option(name="-l",usage="preferred output directory depth, example: 3", metaVar="DEPTH")
    public int level = 3;
    
    @Option(name="-n",usage="preferred number of files in output directories", metaVar="FILECOUNT")
    public int filecount = 0;

    @Option(name="-c",usage="capitalization: 0-none, 1-UPPER CASE, 2-lower case, 3-Capitalized, 4-Title Like, example: 1", metaVar="CAP")
    public int capitalization = 1;
    
    @Option(name="-f",usage="force directory creation for files with unique names")
    public boolean force = false;

    @Argument(usage="list of suffixes, for example: ATR XEX XFD", metaVar="suffix", required=true, multiValued=true)
    public List<String> suffixes = new ArrayList<String>();
}
