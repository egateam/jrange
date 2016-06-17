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
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

        // store graph separately by chromosomes
        Map<String, UndirectedGraph<String, DefaultEdge>> graphOfChr = new HashMap<>();

        // cache chrRange
        Map<String, ChrRange> objectOfRange = new HashMap<>();

        for ( String inFile : files ) {
            List<String> lines = Utils.readLines(inFile);

            for ( String line : lines ) {
                for ( String part : line.split("\\t") ) {
                    ChrRange chrRange = new ChrRange(part);
                    if ( chrRange.isValid() ) {
                        chrRange.standardize(true);
                        String range = chrRange.toString();
                        if ( !objectOfRange.containsKey(range) ) {
                            String chr = chrRange.getChr();
                            if ( !graphOfChr.containsKey(chr) ) {
                                graphOfChr.put(
                                    chr,
                                    new SimpleGraph<String, DefaultEdge>(DefaultEdge.class)
                                );
                            }

                            objectOfRange.put(range, chrRange);
                            graphOfChr.get(chr).addVertex(range);
                        }
                    }

                }
            }
        }

        ArrayList<String> chrs = new ArrayList<>(graphOfChr.keySet());
        Collections.sort(chrs);

        for ( String chr : chrs ) {
            System.out.println("Chromosome " + chr);
            System.out.println(graphOfChr.get(chr));
        }

//
//        //----------------------------
//        // Output
//        //----------------------------
//        StaticUtils.writeRl(outfile, master);
    }
}
