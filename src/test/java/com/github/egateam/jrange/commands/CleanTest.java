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

public class CleanTest {
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
    public void testArgumentsFailed() throws Exception {
        String[] args = {"clean"};
        Cli.main(args);

        Assert.assertTrue(this.stderrContent.toString().contains("Main parameters are required"),
            "Except parameters");
    }

    @Test
    public void testExecute() throws Exception {
        String fileName1 = Utils.expendResource("II.sort.tsv");
        String[] args = {"clean", fileName1, "--outfile", "stdout"};
        Cli.main(args);

        Assert.assertEquals(this.stdoutContent.toString().split("\r\n|\r|\n").length, 11, "line count");
        Assert.assertTrue(this.stdoutContent.toString().contains("892-4684"), "runlist exists");
    }

    @Test
    public void testExecuteBundle() throws Exception {
        String fileName1 = Utils.expendResource("II.sort.tsv");
        String[] args = {"clean", fileName1, "--bundle", "500", "--outfile", "stdout"};
        Cli.main(args);

        Assert.assertEquals(this.stdoutContent.toString().split("\r\n|\r|\n").length, 10, "line count");
        Assert.assertFalse(this.stdoutContent.toString().contains("892-4684"), "runlist bundled");
    }

    @Test
    public void testExecuteMerge() throws Exception {
        String fileName1 = Utils.expendResource("II.sort.tsv");
        String fileName2 = Utils.expendResource("II.merge.tsv");
        String[] args = {"clean", fileName1, "-r", fileName2, "--outfile", "stdout"};
        Cli.main(args);

        Assert.assertEquals(this.stdoutContent.toString().split("\r\n|\r|\n").length, 8, "line count");
        Assert.assertFalse(this.stdoutContent.toString().contains("892-4684"), "runlist merged");
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
