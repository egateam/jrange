package com.github.egateam.jrange.util;

import com.github.egateam.commons.ChrRange;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StaticUtils {

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
