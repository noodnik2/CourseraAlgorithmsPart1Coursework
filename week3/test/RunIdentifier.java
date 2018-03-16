import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/*************************************************************************
* Name: 
* Login: 
* Precept: 

* Partner Name: 
* Partner Login: 
* Partner Precept: 
*
* It is not necessary to include partners for this challenge.
* Make sure you include your partner on your submission for
* the collinear assignment.
* 
* Compilation:  javac RunIdentifier
* Example Execution: java RunIdentifier [input file]
*					   [input file] is optional
* 
* Prints all runs in an array above some minimum size
* 
* Starter code by: jhug@cs.princeton.edu
*
*************************************************************************/

public class RunIdentifier<T> {

	// prints all runs in a that contain at least minNumPoints

	public void identifyRuns(
        final T[] a,
        final Comparator<T> tc,
        final int minNumPoints,
        final BiConsumer<Integer, Integer> runConsumer
    ) {
	    if (a == null || a.length == 0) {
	        return;
        }
		int runStartIndex = 0;
	    T cv = a[0];
		for (int i = 0; i < a.length; i++) {
		    if (cv != null && tc.compare(cv, a[i]) == 0) {
		        // running
		        continue;
            }
            if (i - runStartIndex >= minNumPoints) {
		        runConsumer.accept(runStartIndex, i);
            }
            runStartIndex = i;
		    cv = a[i];
        }
        if (a.length - runStartIndex >= minNumPoints) {
            runConsumer.accept(runStartIndex, a.length);
        }
	}


    // If an input file is provided as a command line argument, the
	// input from that file is used

	// If no input file is provided, then input is taken from StdIn

	public static void main(String[] args) {
	    final RunIdentifier<Integer> intRunIdentifier = new RunIdentifier<>();
	    StdOut.println("ex1");
	    final BiConsumer runConsumer = (start, end) -> StdOut.printf("%s-%s\n", start, end);
		intRunIdentifier.identifyRuns(
		    new Integer[] {7, 7, 7, 8, 8, 2, 2, 2, 3, 3, 3, 3, 7, 7, 7, 7, 7},
            Comparator.naturalOrder(),
            3,
            runConsumer
        );
	    StdOut.println("ex2");
		intRunIdentifier.identifyRuns(
		    new Integer[] {5, 5, 5, 7, 8, 9, 9, 9, 10, 11, 12, 12, 12, 12, 12, 12},
            Comparator.naturalOrder(),
            3,
            runConsumer
        );
	    StdOut.println("ex3");
		intRunIdentifier.identifyRuns(
		    new Integer[] {0, 1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 0, 1, 2, 2, 3, 3, 3},
            Comparator.naturalOrder(),
            0,
            runConsumer
        );
	}
}