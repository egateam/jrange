/**
 * <tt>jrunlist</tt> operates chromosome runlist files.
 * <p>
 * <strong>AUTHOR</strong>
 * Qiang Wang, wang-q@outlook.com
 * <p>
 * <strong>COPYRIGHT AND LICENSE</strong>
 * This software is copyright (c) 2016 by Qiang Wang.
 * <p>
 * This is free software; you can redistribute it and/or modify it under the same terms as the Perl
 * 5 programming language system itself.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * @author Qiang Wang
 * @since 1.7
 */

package com.github.egateam.jrange;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.github.egateam.jrange.commands.*;
import com.github.egateam.jrange.util.StaticUtils;

import java.io.IOException;

@SuppressWarnings("WeakerAccess")
@Parameters
public class Cli {
    /**
     * The only global option
     */
    @SuppressWarnings("CanBeFinal")
    @Parameter(names = {"--help", "-h"}, description = "Print this help and quit", help = true)
    private boolean help = false;

    public void execute(String[] args) {

        JCommander jc = new JCommander(this);
        jc.addCommand("path", new Path());
        jc.addCommand("sort", new Sort());
        jc.addCommand("merge", new Merge());
        jc.addCommand("clean", new Clean());
        jc.addCommand("connect", new Connect());
        jc.addCommand("covered", new Covered());

        String parsedCommand;
        try {
            jc.parse(args);
            parsedCommand = jc.getParsedCommand();

            if ( help ) {
                jc.usage();
                return;
            }

            if ( parsedCommand == null ) {
                String prompt = String.format("java -jar %s --help", StaticUtils.getJarPath());
                throw new ParameterException("No command specified. For help, type\n" + prompt);
            }
        } catch ( ParameterException e ) {
            System.err.println(e.getMessage());
            return;
        } catch ( Exception e ) {
            e.printStackTrace();
            return;
        }

        Object command = jc.getCommands().get(parsedCommand).getObjects().get(0);

        try {
            if ( command instanceof Path ) {
                Path commandNew = (Path) command;
                commandNew.execute();
            } else if ( command instanceof Sort ) {
                Sort commandNew = (Sort) command;
                commandNew.execute();
            } else if ( command instanceof Merge ) {
                Merge commandNew = (Merge) command;
                commandNew.execute();
            } else if ( command instanceof Clean ) {
                Clean commandNew = (Clean) command;
                commandNew.execute();
            } else if ( command instanceof Connect ) {
                Connect commandNew = (Connect) command;
                commandNew.execute();
            } else if ( command instanceof Covered ) {
                Covered commandNew = (Covered) command;
                commandNew.execute();
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new Cli().execute(args);
    }
}
