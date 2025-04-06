import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.File;


/**
 * This class contains three JUnit tests to test executeSingleCommand method. All types of interface
 * commands are tested
 */
public class FrontendTests {
    /**
     * frontendTest1 tests the functions of load and year command
     */
    @Test
    public void frontendTest1() {
        // use Placeholder for now
        BackendInterface backend = new Backend_Placeholder(new Tree_Placeholder());
        Scanner scanner = new Scanner("test");
        Frontend frontend = new Frontend(scanner, backend);
        // Test load
        TextUITester tester = new TextUITester("", true);
        frontend.executeSingleCommand("load File");
        String output = tester.checkOutput();
        assertTrue(output.contains("Successfully loaded the path"), "Load command test failed.");
        // Test year command with only MAX
        tester = new TextUITester("", true);
        frontend.executeSingleCommand("year 2025");
        output = tester.checkOutput();
        assertTrue(output.contains("Year range set to 0 through 2025"), "Year command test failed" +
                ".");
        // Test year command with a range
        tester = new TextUITester("", true);
        frontend.executeSingleCommand("year 2022 to 2025");
        output = tester.checkOutput();
        assertTrue(output.contains("Year range set to 2022 through 2025"),
                "Year command with a range test failed.");
    }

    /**
     * frontendTest2 tests the function of loudness command
     */
    @Test
    public void frontendTest2() {
        BackendInterface backend = new Backend_Placeholder(new Tree_Placeholder());
        Scanner scanner = new Scanner("test");
        Frontend frontend = new Frontend(scanner, backend);
        TextUITester tester = new TextUITester("", true);
        frontend.executeSingleCommand("loudness 10");
        String output = tester.checkOutput();
        assertTrue(output.contains("Loudness threshold set to 10"), "Loudness command test failed" +
                ".");
    }

    /**
     * frontendTest3 tests the function of show and help command
     */
    @Test
    public void frontendTest3() {
        BackendInterface backend = new Backend_Placeholder(new Tree_Placeholder());
        Scanner scanner = new Scanner("test");
        Frontend frontend = new Frontend(scanner, backend);
        // Test show most danceable command
        TextUITester tester = new TextUITester("", true);
        frontend.executeSingleCommand("show most danceable");
        String output = tester.checkOutput();
        assertTrue(output.contains("Most danceable songs:"), "Show command for most danceable " +
                "test failed.");
        // Test show command with number
        tester = new TextUITester("", true);
        frontend.executeSingleCommand("show 2");
        output = tester.checkOutput();
        assertTrue(output.contains("Showing up to 2 songs:"), "Show command with number test " +
                "failed.");
        // Test help command
        tester = new TextUITester("", true);
        frontend.executeSingleCommand("help");
        output = tester.checkOutput();
        assertTrue(output.contains("load FILEPATH") || output.contains("year MAX"),
                "Help command test failed.");
    }

    /**
     * Integration test for the load command using SongsTest.csv file.
     */
    @Test
    public void integrationTestLoad() {
        // make sure that the file exits
        File testFile = new File("SongsTest.csv");
        assertTrue(testFile.exists(), "SongsTest.csv does not exist.");
        IterableSortedCollection<Song> tree = new IterableRedBlackTree<>();
        Backend backend = new Backend(tree);
        Scanner scanner = new Scanner("dummy");
        Frontend frontend = new Frontend(scanner, backend);
        // catch output and store it
        TextUITester tester = new TextUITester("", true);
        frontend.executeSingleCommand("load " + testFile.getAbsolutePath());
        String output = tester.checkOutput();
        // check the output
        assertTrue(output.contains("Successfully loaded the path"),
                "Output of command 'load' is incorrect.");
    }

    /**
     * Integration test for the year command using SongsTest.cvs file.
     */
    @Test
    public void integrationTestYearCommand() {
        File testFile = new File("SongsTest.csv");
        assertTrue(testFile.exists(), "SongsTest.csv does not exist.");
        IterableSortedCollection<Song> tree = new IterableRedBlackTree<>();
        Backend backend = new Backend(tree);
        Scanner scanner = new Scanner("dummy");
        Frontend frontend = new Frontend(scanner, backend);
        // catch output and store it
        TextUITester tester = new TextUITester("", true);
        frontend.executeSingleCommand("load " + testFile.getAbsolutePath());
        // set year range from 2015 to 2016
        frontend.executeSingleCommand("year 2015 to 2016");
        // show 2 most dancable songs
        frontend.executeSingleCommand("show 2");
        String output = tester.checkOutput();
        System.out.println(output);
        // check the title of these two songs
        assertTrue(output.contains("Dangerous"), "Year test failed, no Dangerous");
        assertTrue(output.contains("Cake By The Ocean"), "Year test failed, no Cake By The Ocean");
    }

    /**
     * Integration test for the loudness command using SongsTest.cvs file.
     */
    @Test
    public void integrationTestLoudnessCommand() {
        File testFile = new File("SongsTest.csv");
        assertTrue(testFile.exists(), "SongsTest.csv does not exist.");
        IterableSortedCollection<Song> tree = new IterableRedBlackTree<>();
        Backend backend = new Backend(tree);
        Scanner scanner = new Scanner("dummy");
        Frontend frontend = new Frontend(scanner, backend);
        // catch output and store it
        TextUITester tester = new TextUITester("", true);
        frontend.executeSingleCommand("load " + testFile.getAbsolutePath());
        // set year range from 2010 to 2019
        frontend.executeSingleCommand("year 2010 to 2019");
        // all songs with loudness higher than -6 are filtered out
        frontend.executeSingleCommand("loudness -6");
        frontend.executeSingleCommand("show 10");
        String output = tester.checkOutput();
        //System.out.println(output);
        // None of the following songs should present in the output
        assertFalse(output.contains("Hey, Soul Sister") || output.contains("Muny - Album Version " +
                        "(Edited)") || output.contains("Cake By The Ocea"),
                "Loudness Test failed.");
        // reset the loudness
        tester = new TextUITester("", true);
        frontend.executeSingleCommand("loudness -4");
        frontend.executeSingleCommand("show 10");
        output = tester.checkOutput();
        System.out.println(output);
        // following are the most dancable songs within the range
        assertTrue(output.contains("Dangerous"), "Failed, no Dangerous");
        assertTrue(output.contains("Muny - Album Version (Edited)"), "Failed, no Muny - Album " +
                "Version (Edited)");
        assertTrue(output.contains("ust Give Me a Reason (feat. Nate Ruess)"), "Failed, no ust " +
                "Give Me a Reason (feat. Nate Ruess)");
    }

    /**
     * Integration test for the "show most danceable" command using SongsTest.cvs file.
     */
    @Test
    public void integrationTestShowMostDanceable() {
        File testFile = new File("SongsTest.csv");
        assertTrue(testFile.exists(), "SongsTest.csv does not exist.");
        IterableSortedCollection<Song> tree = new IterableRedBlackTree<>();
        Backend backend = new Backend(tree);
        Scanner scanner = new Scanner("dummy");
        Frontend frontend = new Frontend(scanner, backend);
        // catch output and store it
        TextUITester tester = new TextUITester("", true);
        frontend.executeSingleCommand("load " + testFile.getAbsolutePath());
        // execute show most danceable command
        frontend.executeSingleCommand("show most danceable");
        String output = tester.checkOutput();
        // check the order that the following songs are printed
        int indexBO = output.indexOf("Muny - Album Version (Edited)");
        int indexCake = output.indexOf("One Kiss (with Dua Lipa)");
        int indexAlien = output.indexOf("Dangerous");
        assertTrue(indexBO != -1 && indexCake != -1 && indexAlien != -1,
                "show most danceable command failed-not being printed correctly");
        assertTrue(indexAlien < indexCake && indexBO < indexCake,
                "show most danceable command failed-order is incorrect");
    }
}