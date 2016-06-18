/**
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam.jrange.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.github.egateam.IntSpan;
import com.github.egateam.commons.ChrRange;
import com.github.egateam.commons.Utils;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings({"CanBeFinal"})
@Parameters(commandDescription = "Merge overlapped ranges via overlapping graph")
public class Merge {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Parameter(description = "<infiles>", required = true)
    private List<String> files;

    @Parameter(names = {"--coverage", "-c"}, description = "When larger than this ratio, merge ranges.")
    private double coverage = 0.9;

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

        //----------------------------
        // Coverages
        //----------------------------
        List<String> chrList = new ArrayList<>(graphOfChr.keySet());
        Collections.sort(chrList);
        for ( String chr : chrList ) {
            if ( verbose ) {
                System.err.println("Chromosome " + chr);
            }

            UndirectedGraph<String, DefaultEdge> graph = graphOfChr.get(chr);

            List<String> rangeList = new ArrayList<>(graph.vertexSet());
            Collections.sort(rangeList);

            for ( int i = 0; i < rangeList.size(); i++ ) {
                IntSpan intSpanI = objectOfRange.get(rangeList.get(i)).getIntSpan();
                if ( verbose ) {
                    System.err.println(
                        String.format(
                            "    Range %d / %d\t%s",
                            i, rangeList.size(), rangeList.get(i)
                        )
                    );
                }

                for ( int j = i + 1; j < rangeList.size(); j++ ) {
                    IntSpan intSpanJ = objectOfRange.get(rangeList.get(j)).getIntSpan();

                    IntSpan intersect = intSpanI.intersect(intSpanJ);
                    if ( !intersect.isEmpty() ) {
                        double coverageI = (double) intersect.size() / intSpanI.size();
                        double coverageJ = (double) intersect.size() / intSpanJ.size();

                        if ( coverageI > coverage && coverageJ > coverage ) {
                            if ( verbose ) {
                                System.err.println(
                                    String.format(
                                        "        Merge with Range %d / %d\t%s",
                                        j, rangeList.size(), rangeList.get(j)
                                    )
                                );
                            }
                            graph.addEdge(rangeList.get(i), rangeList.get(j));
                        }
                    }
                }
            }
        }

        //----------------------------
        // Merging
        //----------------------------
        List<String> lines = new ArrayList<>();
        for ( String chr : chrList ) {
            UndirectedGraph<String, DefaultEdge> graph = graphOfChr.get(chr);

            ConnectivityInspector cci              = new ConnectivityInspector(graph);
            List<Set<String>>     connectedSetList = cci.connectedSets();
            for ( Set<String> connectedSet : connectedSetList ) {
                if ( connectedSet.size() > 1 ) {
                    if ( verbose ) {
                        System.err.println(
                            String.format(
                                "    Merge %s ranges", connectedSet.size()
                            )
                        );
                    }

                    // connected ranges
                    List<String> rangeList = new ArrayList<>(connectedSet);
                    Collections.sort(rangeList);

                    // collect info for merged range
                    IntSpan     intSpan = new IntSpan();
                    Set<String> strands = new TreeSet<>();
                    String      strand;
                    boolean     change;

                    for ( String range : rangeList ) {
                        ChrRange chrRange = objectOfRange.get(range);

                        intSpan.merge(chrRange.getIntSpan());
                        strands.add(chrRange.getStrand());
                    }

                    if ( strands.size() == 1 ) {
                        strand = strands.iterator().next();
                        change = false;
                    } else {
                        strand = "+";
                        change = true;
                    }

                    ChrRange firstChrRange = objectOfRange.get(rangeList.get(0));
                    String mergedRange = String.format(
                        "%s(%s):%s",
                        firstChrRange.getChr(), strand, intSpan.toString()
                    );

                    for ( String range : rangeList ) {
                        if ( range.equals(mergedRange) ) {
                            continue;
                        }

                        boolean rangeChange = false;
                        if ( change ) {
                            String rangeStrand = objectOfRange.get(range).getStrand();
                            if ( !Objects.equals(rangeStrand, strand) ) {
                                rangeChange = true;
                            }
                        }

                        String outString = String.format(
                            "%s\t%s\t%d",
                            range, mergedRange, (rangeChange ? 1 : 0)
                        );
                        if ( verbose ) {
                            System.err.println(outString);
                        }
                        lines.add(outString);
                    }
                }
            }
        }

        //----------------------------
        // Output
        //----------------------------
        Utils.writeLines(outfile, lines);
    }
}
