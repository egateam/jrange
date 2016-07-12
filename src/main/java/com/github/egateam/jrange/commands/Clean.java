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
import com.github.egateam.jrange.util.StaticUtils;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SuppressWarnings({"CanBeFinal"})
@Parameters(commandDescription = "Replace ranges within links, incorporate hit strands and remove nested links")
public class Clean {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Parameter(description = "<infiles>", required = true)
    private List<String> files;

    @Parameter(names = {"--replace", "-r"}, description = "Two-column tsv file, normally produced by command merge.")
    private String replaceFile;

    @Parameter(names = {"--bundle", "-b"}, description = "Bundle overlapped links. This value is the overlapping size. Suggested value is [500].")
    private int bundle = 0;

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

        if ( replaceFile != null ) {
            if ( !new File(replaceFile).isFile() ) {
                throw new IOException(String.format("The merged file [%s] doesn't exist.", replaceFile));
            }
        }

        if ( outfile == null ) {
            outfile = files.get(0) + ".clean.tsv";
        }
    }

    public void execute() throws Exception {
        validateArgs();

        //----------------------------
        // Load replaces
        //----------------------------

        // cache chrRange
        Map<String, ChrRange> objectOfRange = new HashMap<>();
        Map<String, String>   replaceOf     = new HashMap<>();

        if ( replaceFile != null ) {
            if ( verbose ) {
                System.err.println("==> Load replaces");
            }
            for ( String line : Utils.readLines(replaceFile) ) {
                StaticUtils.buildChrRange(line, objectOfRange);

                String parts[] = line.split("\\t");
                if ( parts.length == 2 ) {
                    replaceOf.put(parts[0], parts[1]);
                }
            }
        }

        //----------------------------
        // Replacing and incorporating
        //----------------------------

        if ( verbose ) {
            System.err.println("==> Incorporating strands");
        }

        Set<String> lineSet = new TreeSet<>();
        for ( String inFile : files ) {
            for ( String line : Utils.readLines(inFile) ) {
                StaticUtils.buildChrRange(line, objectOfRange);

                String parts[] = line.split("\\t");
                int    count   = parts.length;

                // make sure that all lines are bilateral links
                if ( !(count == 2 || count == 3) ) {
                    continue;
                } else if ( !objectOfRange.containsKey(parts[0]) ) {
                    continue;
                } else if ( !objectOfRange.containsKey(parts[1]) ) {
                    continue;
                }

                // replacing
                for ( int i = 0; i < count; i++ ) {
                    String original = parts[i];
                    if ( replaceOf.containsKey(original) ) {
                        // create new ChrRange, use original strand
                        ChrRange newRange = objectOfRange.get(replaceOf.get(original));
                        newRange.setStrand(objectOfRange.get(original).getStrand());

                        parts[i] = newRange.toString();
                    }
                }
                String newLine = String.join("\t", Arrays.asList(parts));
                StaticUtils.buildChrRange(newLine, objectOfRange);

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
                    && Objects.equals(range0.getEnd(), range1.getEnd()) ) {
                    continue;
                }

                if ( strands.size() == 1 ) {
                    range0.setStrand("+");
                    range1.setStrand("+");
                } else {
                    range0.setStrand("+");
                    range1.setStrand("-");
                }

                newLine = range0.toString() + "\t" + range1.toString();
                StaticUtils.buildChrRange(newLine, objectOfRange);
                lineSet.add(newLine);
            }
        }

        //----------------------------
        // Remove nested links
        //----------------------------
        // now all lines (links) are without hit strands
        List<String> lines = new ArrayList<>(lineSet);
        lines = StaticUtils.sortLinks(lines);
        boolean flagNest = true;
        while ( flagNest ) {

            if ( verbose ) {
                System.err.println("==> Remove nested links");
            }

            Set<String> toRemove = new HashSet<>();
            List<String> chrPairs = lines.stream()
                .map((key) -> {
                    String parts[] = key.split("\\t");
                    return String.join(":",
                        objectOfRange.get(parts[0]).getChr(),
                        objectOfRange.get(parts[1]).getChr()
                    );
                })
                .collect(Collectors.toList());

            for ( int i = 0; i < lines.size(); i++ ) {
                String curPair = chrPairs.get(i);
                List<Integer> restIdx = IntStream.range(i + 1, chrPairs.size())
                    .filter((key) -> Objects.equals(chrPairs.get(key), curPair))
                    .boxed()
                    .collect(Collectors.toList());

                for ( int j : restIdx ) {
                    String lineI    = lines.get(i);
                    String partsI[] = lineI.split("\\t");

                    String lineJ    = lines.get(j);
                    String partsJ[] = lineJ.split("\\t");

                    IntSpan intSpan0I = objectOfRange.get(partsI[0]).getIntSpan();
                    IntSpan intSpan1I = objectOfRange.get(partsI[1]).getIntSpan();

                    IntSpan intSpan0J = objectOfRange.get(partsJ[0]).getIntSpan();
                    IntSpan intSpan1J = objectOfRange.get(partsJ[1]).getIntSpan();

                    if ( intSpan0I.superset(intSpan0J) && intSpan1I.superset(intSpan1J) ) {
                        toRemove.add(lineJ);
                    } else if ( intSpan0J.superset(intSpan0I) && intSpan1J.superset(intSpan1I) ) {
                        toRemove.add(lineI);
                    }
                }
            }

            lines = lines.stream()
                .filter((key) -> !toRemove.contains(key))
                .collect(Collectors.toList());
            flagNest = toRemove.size() > 0;
        }
        lines = StaticUtils.sortLinks(lines);

        //----------------------------
        // Bundle links
        //----------------------------
        if ( bundle != 0 ) {
            if ( verbose ) {
                System.err.println("==> Bundle overlapped links");
            }

            List<String> chrStrandPairs = lines.stream()
                .map((key) -> {
                    String parts[] = key.split("\\t");
                    return String.join(":",
                        objectOfRange.get(parts[0]).getChr(),
                        objectOfRange.get(parts[0]).getStrand(),
                        objectOfRange.get(parts[1]).getChr(),
                        objectOfRange.get(parts[1]).getStrand()
                    );
                })
                .collect(Collectors.toList());

            UndirectedGraph<String, DefaultEdge> graph =
                new SimpleGraph<>(DefaultEdge.class);

            for ( int i = 0; i < lines.size(); i++ ) {
                String curPair = chrStrandPairs.get(i);
                List<Integer> restIdx = IntStream.range(i + 1, chrStrandPairs.size())
                    .filter((key) -> Objects.equals(chrStrandPairs.get(key), curPair))
                    .boxed()
                    .collect(Collectors.toList());

                for ( int j : restIdx ) {
                    String lineI    = lines.get(i);
                    String partsI[] = lineI.split("\\t");

                    String lineJ    = lines.get(j);
                    String partsJ[] = lineJ.split("\\t");

                    if ( !graph.containsVertex(lineI) ) {
                        graph.addVertex(lineI);
                    }
                    if ( !graph.containsVertex(lineJ) ) {
                        graph.addVertex(lineJ);
                    }

                    IntSpan intSpan0I = objectOfRange.get(partsI[0]).getIntSpan();
                    IntSpan intSpan1I = objectOfRange.get(partsI[1]).getIntSpan();

                    IntSpan intSpan0J = objectOfRange.get(partsJ[0]).getIntSpan();
                    IntSpan intSpan1J = objectOfRange.get(partsJ[1]).getIntSpan();

                    if ( intSpan0I.intersect(intSpan0J).size() >= bundle ) {
                        if ( intSpan1I.intersect(intSpan1J).size() >= bundle ) {
                            graph.addEdge(lineI, lineJ);
                        }
                    }
                }
            }

            // bundle connected lines
            ConnectivityInspector cci           = new ConnectivityInspector(graph);
            List<Set<String>>     connectedList = cci.connectedSets();

            for ( Set<String> connected : connectedList ) {
                if ( verbose ) {
                    System.err.println(
                        String.format(
                            "    Merge %s lines", connected.size()
                        )
                    );
                }

                // connected lines
                List<String> lineList = new ArrayList<>(connected);
                Collections.sort(lineList);

                List<String> mergedRanges = new ArrayList<>();
                for ( int i : new int[]{0, 1} ) {
                    String  chr    = "";
                    String  strand = "";
                    IntSpan merged = new IntSpan();

                    for ( String line : lineList ) {
                        lines = lines.stream()
                            .filter((key) -> !Objects.equals(key, line))
                            .collect(Collectors.toList());

                        String parts[] = line.split("\\t");
                        chr = objectOfRange.get(parts[i]).getChr();
                        strand = objectOfRange.get(parts[i]).getStrand();
                        merged.merge(objectOfRange.get(parts[i]).getIntSpan());
                    }

                    ChrRange chrRange = new ChrRange("DUMMY");
                    chrRange.setChr(chr);
                    chrRange.setStrand(strand);
                    chrRange.setStart(merged.min());
                    chrRange.setEnd(merged.max());
                    mergedRanges.add(chrRange.toString());
                }

                String newLine = String.join("\t", mergedRanges);
                lines.add(newLine);

                if ( verbose ) {
                    System.err.println(
                        String.format(
                            "        Merge %s lines", newLine
                        )
                    );
                }
            }

            lines = StaticUtils.sortLinks(lines);
        }

        //----------------------------
        // Links of nearly identical ranges escaped from merging
        //----------------------------
        if ( replaceFile != null ) {
            if ( verbose ) {
                System.err.println("==> Remove self links");
            }

            List<String> samePairLines = lines.stream()
                .flatMap((key) -> {
                    String parts[] = key.split("\\t");
                    if ( Objects.equals(
                        objectOfRange.get(parts[0]).getChr(),
                        objectOfRange.get(parts[1]).getChr()
                    ) ) {
                        return Stream.of(key);
                    } else {
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toList());

            for ( String line : samePairLines ) {
                String parts[] = line.split("\\t");

                IntSpan intSpan0 = objectOfRange.get(parts[0]).getIntSpan();
                IntSpan intSpan1 = objectOfRange.get(parts[1]).getIntSpan();

                IntSpan intSpanI = intSpan0.intersect(intSpan1);
                if ( !intSpanI.isEmpty() ) {
                    if ( intSpanI.size() / intSpan0.size() > 0.5
                        && intSpanI.size() / intSpan1.size() > 0.5 ) {
                        lines = lines.stream()
                            .filter((key) -> !Objects.equals(key, line))
                            .collect(Collectors.toList());
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
