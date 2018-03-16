import edu.princeton.cs.algs4.StdOut;

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
        for (int p = 0; p < points.length - 1; p++) {
            if (points[p].compareTo(points[p + 1]) == 0) {
                throw new IllegalArgumentException("repeated point");
            }
        }

        // find all sets of 4 collinear points via brute force permutation

        final Map<PointSlope, Point> maxPtSlopeSegs = new HashMap<>();
        for (int p = 0; p < points.length - 3; p++) {
            final Point pointP = points[p];

            for (int q = p + 1; q < points.length - 2; q++) {
                final Point pointQ = points[q];

                for (int r = q + 1; r < points.length - 1; r++) {
                    final Point pointR = points[r];

                    final double p2qSlope = pointP.slopeTo(pointQ);
                    if (p2qSlope != pointP.slopeTo(pointR)) {
                        continue;
                    }

                    for (int s = r + 1; s < points.length; s++) {
                        final Point pointS = points[s];

                        if (p2qSlope != pointP.slopeTo(pointS)) {
                            continue;
                        }

                        final PointSlope ps = new PointSlope(pointP, p2qSlope);
                        final Point psMaxPt = maxPtSlopeSegs.get(ps);
                        if (psMaxPt == null || psMaxPt.compareTo(pointS) < 0) {
                            maxPtSlopeSegs.put(ps, pointS);
                        }
                    }
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