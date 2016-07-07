package com.github.egateam.jrange.util;

import com.github.egateam.commons.ChrRange;

import java.util.Map;

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

}
