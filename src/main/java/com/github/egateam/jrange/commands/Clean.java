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
public class Clean {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Parameter(description = "<infiles>", required = true)
    private List<String> files;

    @Parameter(names = {"--coverage", "-c"}, description = "When larger than this ratio, merge ranges.")
    private double coverage = 0.95;

    @Parameter(names = {"--replace", "-r"}, description = "Two-column tsv file, normally produced by command merge.")
    private String replaceFile;

    @Parameter(names = {"--outfile", "-o"}, description = "Output filename. [stdout] for screen.")
    private String outfile;

    @Parameter(names = {"--verbose", "-v"}, description = "Verbose mode.")
    private boolean verbose;

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
            outfile = files.get(0) + ".replace.tsv";
        }
    }

    public void execute() throws Exception {
        validateArgs();

        //----------------------------
        // Loading
        //----------------------------

        // cache chrRange
        Map<String, ChrRange> objectOfRange = new HashMap<>();

        if ( verbose ) {
            System.err.println("==> Incorporating strands");
        }

        Set<String> lineSet = new TreeSet<>();
        for ( String inFile : files ) {
            for ( String line : Utils.readLines(inFile) ) {
                StaticUtils.buildChrRange(line, objectOfRange);

                String parts[] = line.split("\\t");
                int    count   = parts.length;

                if ( !(count == 2 || count == 3) ) {
                    continue;
                } else if ( !objectOfRange.containsKey(parts[0]) ) {
                    continue;
                } else if ( !objectOfRange.containsKey(parts[1]) ) {
                    continue;
                }

                // incorporating
                Set<String> strands = new TreeSet<>();
                if ( count == 3 ) {
                    if ( parts[2].equals("+") || parts[2].equals("-") ) {
                        strands.add(parts[2]);
                    }
                }

                for ( int i : new int[]{0, 1} ) {
                    strands.add(objectOfRange.get(parts[i]).getStrand());
                }

                ChrRange range0 = objectOfRange.get(parts[0]);
                ChrRange range1 = objectOfRange.get(parts[1]);

                // skip identical ranges
                if ( Objects.equals(range0.getChr(), range1.getChr())
                    && Objects.equals(range0.getStart(), range1.getStart())
                    && Objects.equals(range0.getEnd(), range1.getEnd())) {
                    continue;
                }

                if ( strands.size() == 1 ) {
                    range0.setStrand("+");
                    range1.setStrand("+");
                } else {
                    range0.setStrand("+");
                    range1.setStrand("-");
                }

                String newLine = range0.toString() + "\t" + range1.toString();
                StaticUtils.buildChrRange(newLine, objectOfRange);
                lineSet.add(newLine);
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
