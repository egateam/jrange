/**
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam.jrange.commands;

import com.github.egateam.jrange.Cli;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class MergeTest {
    // Store the original standard out before changing it.
    private final PrintStream originalStdout = System.out;
    private final PrintStream originalStderr = System.err;
    private ByteArrayOutputStream stdoutContent = new ByteArrayOutputStream();
    private ByteArrayOutputStream stderrContent = new ByteArrayOutputStream();

    @BeforeMethod
    public void beforeTest() {
        // Redirect all System.out to stdoutContent.
        System.setOut(new PrintStream(this.stdoutContent));
        System.setErr(new PrintStream(this.stderrContent));
    }

    @Test
    public void testMergeFailed() throws Exception {
        String[] args = {"merge"};
        Cli.main(args);

        Assert.assertTrue(this.stderrContent.toString().contains("Main parameters are required"),
            "Except parameters");
    }

//    @Test(description = "Test command with I.yml and II.yml")
//    public void testExecute() throws Exception {
//        String fileName1 = Utils.expendResource("I.yml");
//        String[] args = {"merge", fileName1, "--outfile", "stdout"};
//        Cli.main(args);
//
//        Assert.assertEquals(this.stdoutContent.toString().split("\r\n|\r|\n").length, 5, "line count");
//        Assert.assertTrue(this.stdoutContent.toString().contains("28547-29194"), "runlist exists");
//        Assert.assertTrue(this.stdoutContent.toString().matches("(?s).*I:.+II:.*"), "chromosomes exist");
//    }

    @AfterMethod
    public void afterTest() {
        // Put back the standard out.
        System.setOut(this.originalStdout);
        System.setErr(this.originalStderr);

        // Clear the stdoutContent.
        this.stdoutContent = new ByteArrayOutputStream();
        this.stderrContent = new ByteArrayOutputStream();
    }
}
