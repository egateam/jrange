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
@Parameters(commandDescription = "Merge runlist yaml files")
public class Merge {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Parameter(description = "<infiles>", required = true)
    private List<String> files;

    @Parameter(names = {"--coverage", "-c"}, description = "When larger than this ratio, merge nodes.")
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

            List<String> nodes = new ArrayList<>(graph.vertexSet());
            Collections.sort(nodes);

            for ( int i = 0; i < nodes.size(); i++ ) {
                IntSpan intSpanI = objectOfRange.get(nodes.get(i)).getIntSpan();
                if ( verbose ) {
                    System.err.println(
                        String.format(
                            "    Node %d / %d\t%s",
                            i, nodes.size(), nodes.get(i)
                        )
                    );
                }

                for ( int j = i + 1; j < nodes.size(); j++ ) {
                    IntSpan intSpanJ = objectOfRange.get(nodes.get(j)).getIntSpan();

                    IntSpan intersect = intSpanI.intersect(intSpanJ);
                    if ( !intersect.isEmpty() ) {
                        double coverageI = (double) intersect.size() / intSpanI.size();
                        double coverageJ = (double) intersect.size() / intSpanJ.size();

                        if ( coverageI > coverage && coverageJ > coverage ) {
                            if ( verbose ) {
                                System.err.println(
                                    String.format(
                                        "        Merge with Node %d / %d\t%s",
                                        j, nodes.size(), nodes.get(j)
                                    )
                                );
                            }
                            graph.addEdge(nodes.get(i), nodes.get(j));
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
                                "    Merge %s nodes", connectedSet.size()
                            )
                        );
                    }

                    // nodes in this connection
                    List<String> nodes = new ArrayList<>(connectedSet);
                    Collections.sort(nodes);

                    // collect info for merged range
                    IntSpan     intSpan = new IntSpan();
                    Set<String> strands = new TreeSet<>();
                    String      strand;
                    boolean     change;

                    for ( String node : nodes ) {
                        ChrRange chrRange = objectOfRange.get(node);

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

                    ChrRange firstChrRange = objectOfRange.get(nodes.get(0));
                    String mergedNode = String.format(
                        "%s(%s):%s",
                        firstChrRange.getChr(), strand, intSpan.toString()
                    );

                    for ( String node : nodes ) {
                        if ( node.equals(mergedNode) ) {
                            continue;
                        }

                        boolean nodeChange = false;
                        if ( change ) {
                            String nodeStrand = objectOfRange.get(node).getStrand();
                            if ( !Objects.equals(nodeStrand, strand) ) {
                                nodeChange = true;
                            }
                        }

                        String outString = String.format(
                            "%s\t%s\t%d",
                            node, mergedNode, (nodeChange ? 1 : 0)
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
