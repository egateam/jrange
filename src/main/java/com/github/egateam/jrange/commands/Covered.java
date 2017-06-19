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
import com.github.egateam.commons.Ovlp;
import com.github.egateam.commons.Utils;
import com.github.egateam.jrange.util.StaticUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings({"CanBeFinal"})
@Parameters(commandDescription = "(Unfinished) Covered regions from .ovlp.tsv files")
public class Covered {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Parameter(description = "<infiles>", required = true)
    private List<String> files;

    @Parameter(names = {"--coverage", "-c"}, description = "minimal coverage")
    private int coverage = 3;

    @Parameter(names = {"--len", "-l"}, description = "minimal length of overlaps")
    private int minOvlpLen = 1000;

    @Parameter(names = {"--idt", "-i"}, description = "minimal length of overlaps")
    private double minOvlpIdt = 0;

    @Parameter(names = {"--paf"}, description = "input format as PAF")
    private boolean isPaf = false;

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
            outfile = files.get(0) + ".pos.txt";
        }
    }

    public void execute() throws Exception {
        validateArgs();

        //----------------------------
        // Loading
        //----------------------------

        // seq_name => tier_of => IntSpan
        Map<String, Map<Integer, IntSpan>> covered = new HashMap<>();

        // load overlaps and build coverages
        Set<String> seen = new HashSet<>();
        for ( String inFile : files ) {
            List<String> lines = Utils.readLines(inFile);

            for ( String line : lines ) {
                Ovlp ovlp = new Ovlp();

                if (isPaf) {
                    ovlp.parsePafLine(line);
                }
                else {
                    ovlp.parseOvlpLine(line);
                }

                String fId = ovlp.getfId();
                String gId = ovlp.getgId();

                // ignore self overlapping
                if ( Objects.equals(fId, gId) ) {
                    continue;
                }

                // ignore poor overlaps
                if ( ovlp.getLen() < minOvlpLen ) {
                    continue;
                }
                if ( ovlp.getIdt() < minOvlpIdt ) {
                    continue;
                }

                // skip duplicated overlaps
                String pair = String.join("\t", fId, gId);
                if ( seen.contains(pair) ) {
                    continue;
                }
                seen.add(pair);

                { // first seq
                    if ( !covered.containsKey(fId) ) {
                        Map<Integer, IntSpan> tier_of = new HashMap<>();
                        tier_of.put(-1, new IntSpan(1, ovlp.getfLen()));

                        for ( int i = 1; i <= coverage; i++ ) {
                            tier_of.put(i, new IntSpan());
                        }

                        covered.put(fId, tier_of);
                    }

                    StaticUtils.bumpCoverage(covered.get(fId), ovlp.getfB(), ovlp.getfE());
                }

                { // second seq
                    if ( !covered.containsKey(gId) ) {
                        Map<Integer, IntSpan> tier_of = new HashMap<>();
                        tier_of.put(-1, new IntSpan(1, ovlp.getgLen()));

                        for ( int i = 1; i <= coverage; i++ ) {
                            tier_of.put(i, new IntSpan());
                        }

                        covered.put(gId, tier_of);
                    }

                    StaticUtils.bumpCoverage(covered.get(gId), ovlp.getgB(), ovlp.getgE());
                }
            }
        }

        //----------------------------
        // Output
        //----------------------------
        List<String> lines = new ArrayList<>();

        List<String> keys = new ArrayList<>(covered.keySet());
        Collections.sort(keys);
        for ( String key : keys ) {
            String line = String.format("%s:%s", key, covered.get(key).get(coverage));
            lines.add(line);
        }

        Utils.writeLines(outfile, lines);
    }
}
