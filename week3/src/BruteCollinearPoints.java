import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 *  Princeton/Coursera Algorithms Part 1
 *  <p />
 *  Week 3 Assignment: "Pattern Recognition"
 *  <p />
 *  Determination of 4-collinear points via Brute Force
 *  <pre>
 *      Write a program BruteCollinearPoints.java that examines 4 points at a
 *      time and checks whether they all lie on the same line segment,
 *      returning all such line segments. To check whether the 4 points p,
 *      q, r, and s are collinear, check whether the three slopes between p
 *      and q, between p and r, and between p and s are all equal.
 *
 *      public class BruteCollinearPoints {
 *
 *          // finds all line segments containing 4 points
 *          public BruteCollinearPoints(Point[] points);
 *
 *          // the number of line segments
 *          public int numberOfSegments();
 *
 *          // the line segments
 *          public LineSegment[] segments();
 *
 *      }
 *
 *      The method segments() should include each line segment containing 4
 *      points exactly once. If 4 points appear on a line segment in the
 *      order p→q→r→s, then you should include either the line segment p → s
 *      or s → p (but not both) and you should not include subsegments such
 *      as p → r or q → r. For simplicity, we will not supply any input to
 *      BruteCollinearPoints that has 5 or more collinear points.
 *  </pre>
 *  Performance requirement:
 *  <pre>
 *      The order of growth of the running time of your program should be n^4
 *      in the worst case and it should use space proportional to n plus the
 *      number of line segments returned.
 *  </pre>
 *  @author Marty Ross
 *  @see "external test harness: CollinearPointsTester"
 */
public class BruteCollinearPoints {

    /* line segment instances being managed */
    private final LineSegment[] segments;

    /**
     *  Functional interface to enable processing via lambda expression
     *  @param <T> type of argument consumed
     */
    @FunctionalInterface
    private interface Consumer<T> {
        void accept(T t);
    }

    /**
     *  Finds all line segments containing 4 collinear points
     *  @param inPoints set of input points to be considered
     */
    public BruteCollinearPoints(final Point[] inPoints) {

        if (inPoints == null) {
            // an argument to the constructor is null
            throw new IllegalArgumentException();
        }

        // check for null points
        for (final Point inPoint : inPoints) {
            if (inPoint == null) {
                throw new IllegalArgumentException("null point");
            }
        }

        final Point[] points = inPoints.clone();
        Arrays.sort(points);    // sort the points in ascending order

        // check for repeated points
        identifyRuns(
            points,
            Point::compareTo,
            2,
            r -> {
                throw new IllegalArgumentException("repeated point");
            }
        );

        // find all sets of 4 collinear points via brute force permutation

        final List<Point[]> colPointsList = new ArrayList<>();
        for (int p = 0; p < points.length - 3; p++) {

            final Point pointP = points[p];

            for (int q = p + 1; q < points.length - 2; q++) {

                final Point pointQ = points[q];
                final double p2qSlope = pointP.slopeTo(pointQ);

                for (int r = q + 1; r < points.length - 1; r++) {

                    final Point pointR = points[r];
                    if (p2qSlope != pointP.slopeTo(pointR)) {
                        continue;
                    }

                    for (int s = r + 1; s < points.length; s++) {

                        final Point pointS = points[s];
                        if (p2qSlope != pointP.slopeTo(pointS)) {
                            continue;
                        }

                        final Point[] colPoints = { pointP, pointS };
                        Arrays.sort(colPoints);
                        colPointsList.add(colPoints);

                    }
                }
            }
        }

        // make an array out of "colPointsList"
        final Point[][] colPointsArray = new Point[colPointsList.size()][];
        for (int i = 0; i < colPointsArray.length; i++) {
            colPointsArray[i] = colPointsList.get(i);
        }

        // add a line segment for each unique set of points in "colPointsArray"
        final List<LineSegment> foundSegments = new ArrayList<>();
        identifyRuns(
            colPointsArray,
            (l0, l1) -> {
                final int l0Size = l0.length;
                final int l1Size = l1.length;
                for (int i = 0; i < Math.min(l0Size, l1Size); i++) {
                    final int pc = l0[i].compareTo(l1[i]);
                    if (pc != 0) {
                        return pc;
                    }
                }
                return Integer.compare(l0Size, l1Size);
            },
            1,
            r -> {
                final Point[] colPoints = r[0];
//                StdOut.printf("\ncpl(%s),", Arrays.asList(colPoints));
                foundSegments.add(
                    new LineSegment(
                        colPoints[0],
                        colPoints[colPoints.length - 1]
                    )
                );
            }
        );

        // return all found segments
        segments = foundSegments.toArray(new LineSegment[0]);

    }

    /**
     *  @return the number of line segments
     */
    public int numberOfSegments() {
        return segments.length;
    }

    /**
     * @return the line segments
     */
    public LineSegment[] segments() {
        return segments.clone();
    }


    //
    //  Private class methods
    //

    /**
     *  @param inputArray subject array
     *  @param comparator used to sort a copy of {@code inputArray}
     *  in order to detect "runs" of "equal" elements
     *  @param minRunsize minimum size of a "run"
     *  @param callback callback to receive each detected "run"
     *  @param <T> type of array
     */
    private static <T> void identifyRuns(
        final T[] inputArray,
        final Comparator<T> comparator,
        final int minRunsize,
        final Consumer<T[]> callback
    ) {

        if (inputArray == null || inputArray.length == 0) {
	        return;
        }

        final T[] sortedArray = inputArray.clone();
        Arrays.sort(sortedArray, comparator);

		int start = 0;
	    T cv = sortedArray[0];

		for (int end = 0; end < sortedArray.length; end++) {
		    if (cv != null && comparator.compare(cv, sortedArray[end]) == 0) {
		        // running
		        continue;
            }
            if (end - start >= minRunsize) {
		        callback.accept(Arrays.copyOfRange(sortedArray, start, end));
            }
            start = end;
		    cv = sortedArray[end];
        }

        if (sortedArray.length - start >= minRunsize) {
            callback.accept(
                Arrays.copyOfRange(sortedArray, start, sortedArray.length)
            );
        }
	}


    //
    //  Public class methods
    //

    /**
     *  Performs basic edge-case testing
     *  <p />
     *  For more extensive testing, see {@link CollinearPointsTester}
     *  @param args ignored
     */
    public static void main(final String[] args) {
	    try {
	        new BruteCollinearPoints(null);
	        throw new RuntimeException("didn't throw for null point");
        } catch(final IllegalArgumentException ie) {
	        StdOut.println("OK: null constructor arg");
        }
	    try {
	        new BruteCollinearPoints(new Point[] { null });
	        throw new RuntimeException("didn't throw for null point");
        } catch(final IllegalArgumentException ie) {
	        StdOut.println("OK: null point");
        }
	    try {
            final Point point = new Point(1, 2);
            new BruteCollinearPoints(new Point[] {point, point});
	        throw new RuntimeException("didn't throw for duplicate point");
        } catch(final IllegalArgumentException ie) {
	        StdOut.println("OK: duplicate point");
        }
    }

}