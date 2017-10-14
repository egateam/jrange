package com.github.egateam.jrange.commands;

import com.github.egateam.jrange.Cli;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class PathTest {
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

    @Test(description = "Test command path")
    public void testExecute() throws Exception {
        String[] args = {"path"};
        Cli.main(args);

        // /Users/wangq/Scripts/java/jrange/target/classes
        Assert.assertEquals(this.stdoutContent.toString().split("\r\n|\r|\n").length, 1, "line count");

        // The test env isn't inside a JAR file, so we can't get the path of jar
        Assert.assertTrue(
            this.stdoutContent.toString().contains("classes"),
            "prompt filename"
        );
        Assert.assertTrue(
            this.stdoutContent.toString().contains("/") || this.stdoutContent.toString().contains("\\"),
            "directory separator"
        );
    }

    @Test(description = "Test command path --file")
    public void testExecuteFile() throws Exception {
        String[] args = {"path", "--file"};
        Cli.main(args);

        Assert.assertEquals(this.stdoutContent.toString().split("\r\n|\r|\n").length, 1, "line count");

        // The test env isn't inside a JAR file, so we can't get the path of jar
        Assert.assertTrue(
            this.stdoutContent.toString().contains("classes"),
            "prompt filename"
        );
        Assert.assertFalse(
            this.stdoutContent.toString().contains("/") || this.stdoutContent.toString().contains("\\"),
            "directory separator"
        );
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
