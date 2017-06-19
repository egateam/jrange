package com.github.egateam.jrange.commands;

import com.github.egateam.commons.Utils;
import com.github.egateam.jrange.Cli;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class CoveredTest {
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
        String[] args = {"covered"};
        Cli.main(args);

        Assert.assertTrue(this.stderrContent.toString().contains("Main parameters are required"),
            "Except parameters");
    }

    @Test(description = "Test command covered")
    public void testExecute() throws Exception {
        String   fileName1 = Utils.expendResource("1_4.pac.paf.ovlp.tsv");
        String[] args      = {"covered", fileName1, "--outfile", "stdout"};
        Cli.main(args);

        Assert.assertEquals(this.stdoutContent.toString().split("\r\n|\r|\n").length, 8, "line count");
        Assert.assertTrue(this.stdoutContent.toString().contains("pac4745_7148"), "original names");
        Assert.assertFalse(this.stdoutContent.toString().contains("pac4745_7148:1"), "uncovered region");
    }

    @Test(description = "Test command covered --paf")
    public void testExecutePaf() throws Exception {
        String   fileName1 = Utils.expendResource("11_2.long.paf");
        String[] args      = {"covered", fileName1, "--paf", "--outfile", "stdout"};
        Cli.main(args);

        Assert.assertEquals(this.stdoutContent.toString().split("\r\n|\r|\n").length, 15, "line count");
        Assert.assertTrue(this.stdoutContent.toString().contains("long/13141/0_10011"), "original names");
        Assert.assertFalse(this.stdoutContent.toString().contains("long/13141/0_10011:1"), "uncovered region");
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