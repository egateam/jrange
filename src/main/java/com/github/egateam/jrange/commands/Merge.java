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
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@SuppressWarnings({"CanBeFinal"})
@Parameters(commandDescription = "Merge runlist yaml files")
public class Merge {

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
            outfile = files.get(0) + ".merge.tsv";
        }
    }

    public void execute() throws Exception {
        validateArgs();

        //----------------------------
        // Loading
        //----------------------------
        for ( String inFile : files ) {
            List<String> lines = Utils.readLines(inFile);

            for ( String line : lines ) {
                for ( String part : line.split("\\t") ) {
                    ChrRange chrRange = new ChrRange(part);

                    if ( chrRange.isValid() ) {
                        System.out.println(chrRange);

                    }

                }
            }
        }
//
//        //----------------------------
//        // Output
//        //----------------------------
//        StaticUtils.writeRl(outfile, master);
    }
}
