import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     *  Composite key used to find longest segments including a point
     */
    private class PointSlope {

        /** point */
        final Point p;

        /** slope of a segment passing through the point */
        final double s;

        PointSlope(final Point p, final double s) {
            this.p = p;
            this.s = s;
        }

        public int hashCode() {
            return p.hashCode() + Double.hashCode(s);
        }

        public boolean equals(final Object o) {
            if (!(o instanceof PointSlope)) {
                return false;
            }
            final PointSlope otherPs = (PointSlope) o;
            return p.equals(otherPs.p) && s == otherPs.s;
        }
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
        for (int p = 0; p < points.length - 1; p++) {
            if (points[p].compareTo(points[p + 1]) == 0) {
                throw new IllegalArgumentException("repeated point");
            }
        }

        // find all sets of >= 4 collinear points by sorting relative slopes
        final Map<PointSlope, Point> maxPtSlopeSegs = new HashMap<>();
        for (final Point point : points) {
            final Point[] soPoints = points.clone();

            Arrays.sort(soPoints, point.slopeOrder());
            for (int j = 1; j < soPoints.length - 2; j++) {

                // get the three next points with closest s to "point"
                final Point soPointJp0 = soPoints[j];
                final Point soPointJp1 = soPoints[j + 1];
                final Point soPointJp2 = soPoints[j + 2];

                // compare the points to each-other
                final int pctj0 = point.compareTo(soPointJp0);
                final int j0ctj1 = soPointJp0.compareTo(soPointJp1);
                final int j1ctj2 = soPointJp1.compareTo(soPointJp2);
                if (pctj0 >= 0 || j0ctj1 >= 0 || j1ctj2 >= 0) {
                    // ignore point sequences not in canonical order
                    continue;
                }

                // compare the points' s relative to "point"
                final double sp2jp0 = point.slopeTo(soPointJp0);
                final double sp2jp1 = point.slopeTo(soPointJp1);
                final double sp2jp2 = point.slopeTo(soPointJp2);
                // ignore segments not sharing the same s with "point"
                if (sp2jp0 != sp2jp1 || sp2jp0 != sp2jp2) {
                    continue;
                }

                // collect the longest segment sharing the same point and s
                final PointSlope ps = new PointSlope(point, sp2jp0);
                final Point psMaxPt = maxPtSlopeSegs.get(ps);
                if (psMaxPt == null || psMaxPt.compareTo(soPointJp2) < 0) {
                    maxPtSlopeSegs.put(ps, soPointJp2);
                }
            }

        }

        // construct the longest line segment(s) using the minimum points
        final Map<PointSlope, Point> minPtSlopeSegs = new HashMap<>();
        for (final Map.Entry<PointSlope, Point> e : maxPtSlopeSegs.entrySet()) {
            final PointSlope ps = new PointSlope(e.getValue(), e.getKey().s);
            final Point psMinPt = minPtSlopeSegs.get(ps);
            if (psMinPt == null || psMinPt.compareTo(e.getKey().p) > 0) {
                minPtSlopeSegs.put(ps, e.getKey().p);
            }
        }

        // construct the longest line segment(s) using the maximum points
        final List<LineSegment> foundSegments = new ArrayList<>();
        for (final Map.Entry<PointSlope, Point> e : minPtSlopeSegs.entrySet()) {
//            StdOut.printf("\n%s-%s", e.getKey().p, e.getValue());
            foundSegments.add(new LineSegment(e.getKey().p, e.getValue()));
        }

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