/**
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam.jrange.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.egateam.jrange.util.StaticUtils;

@SuppressWarnings({"CanBeFinal"})
@Parameters(commandDescription = "Replace ranges within links, incorporate hit strands and remove nested links")
public class Path {

    @Parameter(names = {"--file"}, description = "output filename instead of full path")
    private boolean wantFile = false;

    public void execute() throws Exception {
        String prompt = String.format("%s", StaticUtils.getJarPath());

        if ( wantFile ) {
            prompt = String.format("%s", StaticUtils.getJarName());
        }

        System.out.println(prompt);
    }
}
