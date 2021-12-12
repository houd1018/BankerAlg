import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;

class Banker {
    public static final int MAX_PROCESSES = 9;
    public static final int MAX_RESOURCES = 9;
    static int totalSolutions = 0;

    public static void main(String[] args) {
        String fileToUse;
        // get the file name for input, either from args[] or prompt
        if (args.length == 0) { // no string entered as command line option
            Scanner inType = new Scanner(System.in);
            System.out.printf("Enter name of file: ");
            fileToUse = inType.nextLine();
        } else { // file name entered as command line option
            fileToUse = args[0];
        }

        FileReader f = null;

        // open the file
        try {
            f = new FileReader(fileToUse);
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }

        // prepare to read file
        Scanner inFile = new Scanner(f);
        String s; // will be used for a single line of the text file
        int[] resourceArray = new int[MAX_RESOURCES]; // total resources
        int currentResource = 0; // resources at start
        Process[] processArray = new Process[MAX_PROCESSES];
        int processNo = 0;

        // read the file and parse the data
        while (inFile.hasNextLine()) {
            s = inFile.nextLine();
            if ((s.length() > 0) && (s.charAt(0) == 'R')) { // is a resource line
                s = s.substring(4); // remove "Rx: "
                if (s.charAt(0) == ' ') {
                    s = s.substring(1); // this gets rid of any extra space
                }
                resourceArray[currentResource] = Integer.parseInt(s);
                currentResource += 1;
            }
            if ((s.length() > 0) && (s.charAt(0) == 'P')) { // process thread
                s = s.replaceAll("[^\\d]", " "); // replace non-numbers with " "
                int[] pHeld = new int[MAX_RESOURCES];
                int[] pMax = new int[MAX_RESOURCES];
                int j = 0;
                s = s.substring(2);
                s = s.replace(" ", "");
                char[] charArr = s.toCharArray();
                for (int i = 0; i < charArr.length / 2; i++) {
                    pHeld[i] = Character.getNumericValue(charArr[i]);
                }
                for (int i = charArr.length/2; i < charArr.length; i++) {
                    pMax[j] = Character.getNumericValue(charArr[i]);
                    j++;
                }
                processArray[processNo] = new Process(pHeld, pMax, processNo, currentResource);
                //processArray[processNo].printInfo();
                processNo += 1;

            }
        }

        // package the data into ArrayLists for the recursion algorithm
        ArrayList<Process> processList = new ArrayList<Process>();
        ArrayList<Process> hist = new ArrayList<Process>();
        for (int i = 0; i < processNo; i++) {
            processList.add(processArray[i]);
        }

        // calculate safe paths
        System.out.println("Safe paths: ");
        long startTime = System.currentTimeMillis();
        recursivelyCheck(resourceArray, processList, hist);
        long endTime = System.currentTimeMillis();

        // print time elapsed
        if (totalSolutions == 0) {
            System.out.println("No solutions found.");
        } else {
            long totalTime = endTime - startTime;
            if ((totalTime < 1000)) {
                System.out.println(Integer.toString(totalSolutions) + " solution" +(totalSolutions > 1 ? "s": "") + " found in " + Long.toString(totalTime) + " milliseconds.");
            } else {
                double totalTimeInSeconds = (double) totalTime / 1000;
                System.out.println(Integer.toString(totalSolutions) + " solution" +(totalSolutions > 1 ? "s": "") + " found in " + Double.toString(totalTimeInSeconds) + " seconds.");
            }
        }
    }

    static void recursivelyCheck(int[] currentResources, ArrayList<Process> processes, ArrayList<Process> hist) {
        for (Process p : processes) {
            // if the process can run and there are other processes on the list that need to be run
            if (p.canRun(currentResources) && (processes.size() > 1)) {
                int[] newResources = currentResources.clone();
                int[] proccessResources = p.getHeldResources();
                ArrayList<Process> newHist = new ArrayList<Process>(hist);
                newHist.add(p);
                ArrayList<Process> newProcesses = new ArrayList<Process>(processes);
                newProcesses.remove(p);
                for (int i = 0; i < currentResources.length; i++) {
                    newResources[i] = currentResources[i] + proccessResources[i];
                }
                recursivelyCheck(newResources, newProcesses, newHist);
                // else if this process can run and it's the last process on the list
            } else if (p.canRun(currentResources)) {
                hist.add(p);
                totalSolutions += 1;
                for (Process proc : hist) {
                    System.out.printf("P" + Integer.toString(proc.getNameAsInt()) + " -> ");
                }
                System.out.printf("finished");
                System.out.println();
            }
        }
    }
}
