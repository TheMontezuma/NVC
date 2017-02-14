package org.montezuma.nvcgui;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.*; 

public class NVCOptions {
    @Option(name="-i",usage="input directory, example: \"input\"", metaVar="INPUT", required=true)
    public String from = ".";
	
	@Option(name="-o",usage="output directory, example: \"ATARI\"", metaVar="OUTPUT", required=true)
    public String to = "output";

    @Option(name="-l",usage="output directory depth, example: 3", metaVar="DEPTH")
    public int level = 3;

    @Option(name="-f",usage="force directory creation for files with unique names")
    public boolean force = true;

    @Argument(usage="list of suffixes, example: ATR XEX XFD", metaVar="suffix", required=true, multiValued=true)
    public List<String> suffixes = new ArrayList<String>();
}
