package com.github.egateam.jrange.util;

import com.github.egateam.IntSpan;
import com.github.egateam.commons.ChrRange;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StaticUtils {

    public static String getJarPath() throws IOException {
        return new java.io.File(
            StaticUtils.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath()
        ).getCanonicalPath();
    }

    public static String getJarName() throws IOException {
        return new java.io.File(
            StaticUtils.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath()
        ).getName();
    }

    public static void buildChrRange(String line, Map<String, ChrRange> objectOfRange) {
        for ( String part : line.split("\\t") ) {
            ChrRange chrRange = new ChrRange(part);
            if ( chrRange.isValid() ) {
                String range = chrRange.toString();
                if ( !objectOfRange.containsKey(range) ) {
                    objectOfRange.put(range, chrRange);
                }
            }
        }
    }

    public static String changeStrand(String strand) {
        switch ( strand ) {
            case "+":
                return "-";
            case "-":
                return "+";
            default:
                return strand;
        }
    }

    public static int[] begEnd(int beg, int end) {
        if ( beg > end ) {
            int temp = beg;
            beg = end;
            end = temp;
        }

        if ( beg == 0 ) {
            beg = 1;
        }

        return new int[]{beg, end};
    }

    public static void bumpCoverage(Map<Integer, IntSpan> tier_of, int beg, int end) {
        int[]   begEnd     = StaticUtils.begEnd(beg, end);
        IntSpan intSpanNew = new IntSpan(begEnd[0], begEnd[1]);

        int max_tier = Collections.max(tier_of.keySet());

        // reach max coverage in full sequence
        if ( tier_of.get(-1).equals(tier_of.get(max_tier)) ) {
            return;
        }

        // remove intSpanNew from uncovered regions
        tier_of.get(0).subtract(intSpanNew);

        for ( int i = 1; i <= max_tier; i++ ) {
            IntSpan intSpanI = tier_of.get(i).intersect(intSpanNew);
            tier_of.get(i).add(intSpanNew);

            int j = i + 1;
            if ( j > max_tier ) {
                break;
            }

            intSpanNew = intSpanI.copy();
        }
    }

    /**
     * @param tier_of tiers of covered regions
     */
    public static void uniqCoverage(Map<Integer, IntSpan> tier_of) {
        int max_tier = Collections.max(tier_of.keySet());

        for ( int i = 1; i < max_tier; i++ ) {
            IntSpan intSpanCur = tier_of.get(i);
            IntSpan intSpanNext = tier_of.get(i + 1);
            intSpanCur.subtract(intSpanNext);
        }
    }

    public static List<String> sortLinks(List<String> lines) {

        Map<String, ChrRange> objectOfRange = new HashMap<>();

        //----------------------------
        // Sort within links
        //----------------------------
        Set<String> withinLinks = new TreeSet<>();
        for ( String line : lines ) {
            buildChrRange(line, objectOfRange);

            List<String> parts = new ArrayList<>(Arrays.asList(line.split("\\t")));
            List<String> ranges = parts.stream()
                .filter((key) -> objectOfRange.containsKey(key))
                .collect(Collectors.toList());
            List<String> invalids = parts.stream()
                .filter((key) -> !objectOfRange.containsKey(key))
                .collect(Collectors.toList());

            // chromosome strand
            ranges = ranges.stream()
                .sorted(
                    Comparator.comparing((key) -> objectOfRange.get(key).getStrand())
                ).collect(Collectors.toList());

            // start point on chromosomes
            ranges = ranges.stream()
                .sorted(
                    Comparator.comparing((key) -> objectOfRange.get(key).getStart())
                ).collect(Collectors.toList());

            // chromosome name
            ranges = ranges.stream()
                .sorted(
                    Comparator.comparing((key) -> objectOfRange.get(key).getChr())
                ).collect(Collectors.toList());

            String newLine = Stream.concat(ranges.stream(), invalids.stream())
                .collect(Collectors.joining("\t"));
            withinLinks.add(newLine);
        }

        //----------------------------
        // Sort by first range's chromosome order among links
        //----------------------------
        List<String> amongLinks = new ArrayList<>(withinLinks);

        {
            // chromosome strand
            amongLinks = amongLinks.stream()
                .sorted(
                    Comparator.comparing((key) -> {
                        String parts[] = key.split("\\t");
                        return objectOfRange.get(parts[0]).getStrand();
                    })
                ).collect(Collectors.toList());

            // start point on chromosomes
            amongLinks = amongLinks.stream()
                .sorted(
                    Comparator.comparing((key) -> {
                        String parts[] = key.split("\\t");
                        return objectOfRange.get(parts[0]).getStart();
                    })
                ).collect(Collectors.toList());

            // chromosome name
            amongLinks = amongLinks.stream()
                .sorted(
                    Comparator.comparing((key) -> {
                        String parts[] = key.split("\\t");
                        return objectOfRange.get(parts[0]).getChr();
                    })
                ).collect(Collectors.toList());
        }

        //----------------------------
        // Sort by copy number among links (desc)
        //----------------------------
        {
            amongLinks = amongLinks.stream()
                .sorted(
                    Comparator.comparing((key) -> {
                        String parts[] = key.split("\\t");
                        return Arrays.stream(parts)
                            .filter((s) -> objectOfRange.containsKey(s))
                            .count();
                    })
                ).collect(Collectors.toList());
        }

        return amongLinks;
    }
}
