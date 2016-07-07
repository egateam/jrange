/**
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam.jrange.commands;

import com.github.egateam.commons.Utils;
import com.github.egateam.jrange.Cli;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class SortTest {
    // Store the original standard out before changing it.
    private final PrintStream           originalStdout = System.out;
    private final PrintStream           originalStderr = System.err;
    private       ByteArrayOutputStream stdoutContent  = new ByteArrayOutputStream();
    private       ByteArrayOutputStream stderrContent  = new ByteArrayOutputStream();

    @BeforeMethod
    public void beforeTest() {
        // Redirect all System.out to stdoutContent.
        System.setOut(new PrintStream(this.stdoutContent));
        System.setErr(new PrintStream(this.stderrContent));
    }

    @Test
    public void testArgumentsFailed() throws Exception {
        String[] args = {"sort"};
        Cli.main(args);

        Assert.assertTrue(this.stderrContent.toString().contains("Main parameters are required"),
            "Except parameters");
    }

    @Test(description = "Test command sort")
    public void testExecute() throws Exception {
        String   fileName1 = Utils.expendResource("II.links.tsv");
        String[] args      = {"sort", fileName1, "--outfile", "stdout"};
        Cli.main(args);

        Assert.assertEquals(this.stdoutContent.toString().split("\r\n|\r|\n").length, 15, "line count");
        Assert.assertFalse(this.stdoutContent.toString().matches("(?m)^[VX]\\w+\\("), "chromosome II first");
    }

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
