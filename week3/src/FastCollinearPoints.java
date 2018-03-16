import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 *  Princeton/Coursera Algorithms Part 1
 *  <p />
 *  Week 3 Assignment: "Pattern Recognition"
 *  <p />
 *  Determination of 4-collinear points via Sorted Slopes
 *  <pre>
 *      It's possible to solve the collinear point detection problem much
 *      faster than the brute-force solution implemented in
 *      {@code BruteCollinearPoints.java}. Given a point p, the following
 *      method determines whether p participates in a set of 4 or more
 *      collinear points:
 *
 *      1. Think of p as the origin.
 *      2. For each other point q, determine the s it makes with p.
 *      3. Sort the points according to the slopes they makes with p.
 *      4. Check if any 3 (or more) adjacent points in the sorted order
 *         have equal slopes with respect to p. If so, these points, together
 *         with p, are collinear.
 *
 *      Applying this method for each of the n points in turn yields an
 *      efficient algorithm to the problem. The algorithm solves the problem
 *      because points that have equal slopes with respect to p are collinear,
 *      and sorting brings such points together. The algorithm is fast because
 *      the bottleneck operation is sorting.
 *
 *      Write a program FastCollinearPoints.java that implements this algorithm.
 *
 *          public class FastCollinearPoints {
 *              // finds all line segments containing 4 or more points
 *              public FastCollinearPoints(Point[] points);
 *
 *              // the number of line segments
 *              public int numberOfSegments();
 *
 *              // the line segments
 *              public LineSegment[] segments();
 *          }
 *
 *      The method segments() should include each maximal line segment
 *      containing 4 (or more) points exactly once. For example, if 5 points
 *      appear on a line segment in the order p → q → r → s → t, then do not
 *      include the subsegments p → s or q → t.
 *
 *  </pre>
 *  Performance requirement:
 *  <pre>
 *      The order of growth of the running time of your program should be
 *      N^2 log N in the worst case and it should use space proportional to N
 *      plus the number of line segments returned.  FastCollinearPoints should
 *      work properly even if the input has 5 or more collinear points.
 *  </pre>
 *  @author Marty Ross
 *  @see "external test harness: CollinearPointsTester"
 */
public class FastCollinearPoints {

    /* line segment instances being managed */
    private final LineSegment[] segments;

    /**
     *  Finds all maximal sets of n > 4 collinear points
     *  @param inPoints set of input points to be considered
     */
    public FastCollinearPoints(final Point[] inPoints) {

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

        // find all sets of >= 4 collinear points by sorting relative slopes

        final List<Point[]> colPointsList = new ArrayList<>();
        for (final Point refPoint : points) {

            // create copy of points, sorted by slope to "refPoint"
            final Point[] soPoints = points.clone();
            Arrays.sort(soPoints, refPoint.slopeOrder());

            // identify "runs" of 3 (or more) points having the same slope
            identifyRuns(
                soPoints,
                refPoint.slopeOrder(),
                3,
                r -> {
                    // for each run identified, created and add a sorted array
                    // containing all collinear points to "colPointsList"
                    final Point[] colPoints = new Point[1 + r.length];
                    colPoints[0] = refPoint;
                    for (int i = 0; i < r.length; i++) {
                        colPoints[1 + i] = r[i];
                    }
                    Arrays.sort(colPoints);
                    colPointsList.add(colPoints);
                }
            );
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
	        new FastCollinearPoints(null);
	        throw new RuntimeException("didn't throw for null point");
        } catch(final IllegalArgumentException ie) {
	        StdOut.println("OK: null constructor arg");
        }
	    try {
	        new FastCollinearPoints(new Point[] { null });
	        throw new RuntimeException("didn't throw for null point");
        } catch(final IllegalArgumentException ie) {
	        StdOut.println("OK: null point");
        }
	    try {
            final Point point = new Point(1, 2);
            new FastCollinearPoints(new Point[] {point, point});
	        throw new RuntimeException("didn't throw for duplicate point");
        } catch(final IllegalArgumentException ie) {
	        StdOut.println("OK: duplicate point");
        }
    }

}