package pa1;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.System.nanoTime;

public class DriverTies {
    public static String filename;
    public static boolean testBruteForce;
    public static boolean testGS;

    public static void main(String[] args) throws Exception {
        parseArgs(args);

        Matching problem = parseMatchingProblem(filename);
/*
        long startTime = System.nanoTime();
*/
        testRun(problem);
/*        long totalTime = System.nanoTime() - startTime;
        System.out.println("Total runtime in ms: " + totalTime/(1000000));*/
    }

    private static void usage() {
        System.err.println("usage: java Driver [-g] [-b] <filename>");
        System.err.println("\t-b\tTest Brute Force implementation");
        System.err.println("\t-g\tTest Gale-Shapley implementation");
        System.exit(1);
    }

    public static void parseArgs(String[] args) {
        if (args.length == 0) {
            usage();
        }

        filename = "";
        testBruteForce = false;
        testGS = false;
        boolean flagsPresent = false;

        for (String s : args) {
            if(s.equals("-g")) {
                flagsPresent = true;
                testGS = true;
            } else if(s.equals("-b")) {
                flagsPresent = true;
                testBruteForce = true;
            } else if(!s.startsWith("-")) {
                filename = s;
            } else {
                System.err.printf("Unknown option: %s\n", s);
                usage();
            }
        }

        if(!flagsPresent) {
            testBruteForce = true;
            testGS = true;
        }
    }

    public static Matching parseMatchingProblem(String inputFile)
            throws Exception {
        int m = 0;
        int n = 0;
        ArrayList<ArrayList<Integer>> jobPrefs, workerPrefs;
        jobPrefs = new ArrayList<>();
        workerPrefs = new ArrayList<>();

        Scanner sc = new Scanner(new File(inputFile));
        String[] inputSizes = sc.nextLine().split(" ");

        m = Integer.parseInt(inputSizes[0]);
        n = Integer.parseInt(inputSizes[1]);

        ArrayList<Boolean> jobFulltime = new ArrayList<>();
        ArrayList<Boolean> workerHardworking = new ArrayList<>();
        readPreferenceLists(sc, m, jobPrefs, jobFulltime);
        readPreferenceLists(sc, n, workerPrefs, workerHardworking);

        Matching problem = new Matching(m, n, jobPrefs, workerPrefs, jobFulltime, workerHardworking);

        return problem;
    }

    private static void readPreferenceLists(Scanner sc, int m, ArrayList<ArrayList<Integer>> preferenceLists,
                                            ArrayList<Boolean> status) {
        for (int i = 0; i < m; i++) {
            String line = sc.nextLine();
            String[] preferences = line.split(" ");
            ArrayList<Integer> preferenceList = new ArrayList<Integer>(0);

            status.add(Integer.parseInt(preferences[0]) == 1);
            for (Integer j = 1; j < preferences.length; j++) {
                preferenceList.add(Integer.parseInt(preferences[j]));
            }
            preferenceLists.add(preferenceList);
        }
    }

    public static void testRun(Matching problem) {
        Program1Ties program = new Program1Ties();
        boolean isStable;

        if (testGS) {
            Matching GSMatching = program.stableHiringGaleShapley(problem);
            System.out.println(GSMatching);
            isStable = program.isStableMatching(GSMatching);
            System.out.printf("%s: stable? %s\n", "Gale-Shapley", isStable);
            System.out.println();
        }

        if (testBruteForce) {
            Matching BFMatching = program.stableMarriageBruteForce(problem);
            System.out.println(BFMatching);
            isStable = program.isStableMatching(BFMatching);
            System.out.printf("%s: stable? %s\n", "Brute Force", isStable);
            System.out.println();
        }
    }
}
