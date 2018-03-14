import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
 *      2. For each other point q, determine the slope it makes with p.
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
     *  Finds all line segments containing 4 collinear points
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
        for (int p = 0; p < points.length - 1; p++) {
            if (points[p].compareTo(points[p + 1]) == 0) {
                throw new IllegalArgumentException("repeated point");
            }
        }

        // find all maximel sets of n > 4 collinear points by sorting slopes

        final List<LineSegment> foundSegments = new ArrayList<>();
        for (final Point point : points) {
            final Point[] soPoints = points.clone();
            Arrays.sort(soPoints, point.slopeOrder());
            Point maxPoint = null;
            for (int j = 1; j < soPoints.length - 2; j++) {
                final Point soPointJp0 = soPoints[j];
                final Point soPointJp1 = soPoints[j + 1];
                final Point soPointJp2 = soPoints[j + 2];
                final double sp2jp0 = point.slopeTo(soPointJp0);
                final double sp2jp1 = point.slopeTo(soPointJp1);
                final double sp2jp2 = point.slopeTo(soPointJp2);
                if (
                    // have three (or more) equal slopes
                    (sp2jp0 == sp2jp1)
                 && (sp2jp0 == sp2jp2)
                    // and points are in strict order
                 && point.compareTo(soPointJp0) < 1
                 && soPointJp0.compareTo(soPointJp1) < 1
                 && soPointJp1.compareTo(soPointJp2) < 1
                 && (maxPoint == null || maxPoint.compareTo(soPointJp2) > 0)
                ) {
                    // then it's a valid segment to record
                    maxPoint = soPointJp2;
                }
            }
            if (maxPoint != null) {
                foundSegments.add(new LineSegment(point, maxPoint));
            }

        }
    // see "CollinearPointsTester" class for

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

}