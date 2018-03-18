import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.naturalOrder;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;

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
 *      because points that have equal slopes with respect t## o p are collinear,
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
     *  Functional interface to enable processing via lambda expression
     *  @param <T> type of argument consumed
     */
    @FunctionalInterface
    private interface Consumer<T> {
        void accept(T t);
    }

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
        if (points.length != Stream.of(points).distinct().count()) {
            throw new IllegalArgumentException("repeated point(s)");
        }

        // find all sets of >= 4 collinear points by sorting relative slopes

        this.segments = (
            Stream.of(points)
            // for each collinear segment
            .map(
                // map each "refPoint" to a list of points (as an array)
                refPoint -> (
                    // stream a copy of points
                    Stream.of(points.clone())
                    // sorted by (segment) slope, relative to "refPoint"
                    .sorted(refPoint.slopeOrder())
                    // group points by slope relative to "refPoint"
                    .collect(
                        Collectors.groupingBy(
                            refPoint::slopeTo,
                            Collectors.mapping(identity(), toList())
                        )
                    )
                    .entrySet()
                    // stream each grouped slope/points entry
                    .stream()
                    // filter out groups smaller than size 3
                    .filter(e -> e.getValue().size() >= 3)
                    // construct sorted point list for each group
                    .map(
                        e -> {
                            final List<Point> v = e.getValue();
                            final List<Point> l = new ArrayList<>(v.size() + 1);
                            l.add(refPoint);
                            l.addAll(v);
                            l.sort(naturalOrder());
                            return l;
                        }
                    )
                )
            )
            // concatenate all streams of grouped slope/points entries
            .flatMap(s -> s)
            // collect into a "super-list" of grouped slope/points entries
            .collect(Collectors.toList())
            // convert to a stream of grouped slope/points entries
            .stream()
            // remove duplicates
            .distinct()
            // map each into an instance of the target "LineSegment" type
            .map(lp -> new LineSegment(lp.get(0), lp.get(lp.size() - 1)))
            // return an array containing all streamed entries
            .toArray(LineSegment[]::new)
        );

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
	        StdOut.printf("OK: null constructor arg (%s)\n", ie);
        }
	    try {
	        new FastCollinearPoints(new Point[] { null });
	        throw new RuntimeException("didn't throw for null point");
        } catch(final IllegalArgumentException ie) {
	        StdOut.printf("OK: null point (%s)\n", ie);
        }
	    try {
            final Point point = new Point(1, 2);
            new FastCollinearPoints(new Point[] {point, point});
	        throw new RuntimeException("didn't throw for duplicate point");
        } catch(final IllegalArgumentException ie) {
	        StdOut.printf("OK: duplicate point (%s)\n", ie);
        }
    }

}