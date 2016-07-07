/**
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam.jrange.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.github.egateam.commons.ChrRange;
import com.github.egateam.commons.Utils;
import com.github.egateam.jrange.util.StaticUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings({"CanBeFinal"})
@Parameters(commandDescription = "Replace ranges within links, incorporate hit strands and remove nested links")
public class Sort {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Parameter(description = "<infiles>", required = true)
    private List<String> files;

    @Parameter(names = {"--outfile", "-o"}, description = "Output filename. [stdout] for screen.")
    private String outfile;

    private void validateArgs() throws Exception {
        if ( files.size() < 1 ) {
            throw new ParameterException("This command need one or more input files.");
        }

        for ( String inFile : files ) {
            if ( inFile.toLowerCase().equals("stdin") ) {
                continue;
            }
            if ( !new File(inFile).isFile() ) {
                throw new IOException(String.format("The input file [%s] doesn't exist.", inFile));
            }
        }

        if ( outfile == null ) {
            outfile = files.get(0) + ".sort.tsv";
        }
    }

    public void execute() throws Exception {
        validateArgs();

        //----------------------------
        // Loading
        //----------------------------
        Set<String> lineSet = new TreeSet<>();
        for ( String inFile : files ) {
            for ( String line : Utils.readLines(inFile) ) {
                for ( String part : line.split("\\t") ) {
                    ChrRange chrRange = new ChrRange(part);
                    if ( chrRange.isValid() ) {
                        lineSet.add(line); // May produce duplicated lines
                    }
                }
            }
        }

        //----------------------------
        // Merging
        //----------------------------
        List<String> lines = new ArrayList<>(lineSet);
        lines = StaticUtils.sortLinks(lines);

        //----------------------------
        // Output
        //----------------------------
        Utils.writeLines(outfile, lines);
    }
}
