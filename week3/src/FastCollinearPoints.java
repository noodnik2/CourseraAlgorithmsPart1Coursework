import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;

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

        final Point[] points = inPoints.clone();
        Arrays.sort(points);    // sort the points in ascending order

        // check for invalid points
        for (int p = 0; p < points.length - 1; p++) {
            final Point point = points[p];
            if (point == null) {
                throw new IllegalArgumentException("null point");
            }
            if (point.compareTo(points[p + 1]) == 0) {
                throw new IllegalArgumentException("repeated point");
            }
        }
        if (points.length > 0 && points[points.length - 1] == null) {
            throw new IllegalArgumentException("null point");
        }

        // find all sets of 4 collinear points by sorting slopes


        final List<LineSegment> foundSegments = new ArrayList<>();
        for (final Point point : points) {
            final Point[] slopeOrderedPoints = points.clone();
            Arrays.sort(slopeOrderedPoints, point.slopeOrder());
            for (int j = 1; j < slopeOrderedPoints.length - 2; j++) {
                final double si2j = point.slopeTo(slopeOrderedPoints[j]);
                if (
                    // have three (or more) equal slopes
                    (si2j == point.slopeTo(slopeOrderedPoints[j + 1]))
                        && (si2j == point.slopeTo(slopeOrderedPoints[j + 2]))
                        // and points are in strict order
                        && point.compareTo(slopeOrderedPoints[j]) < 1
                        && slopeOrderedPoints[j].compareTo(slopeOrderedPoints[j + 1]) < 1
                        && slopeOrderedPoints[j + 1].compareTo(slopeOrderedPoints[j + 2]) < 1
                    ) {
                    // then it's a valid segment to record
                    foundSegments.add(new LineSegment(point, slopeOrderedPoints[j + 2]));
                }
            }

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
     *  @param args file name
     */
    public static void main(final String[] args) {

        if (args.length != 1) {
            throw new IllegalArgumentException("no file specified");
        }

        final In in = new In(args[0]);
        final int nPoints = in.readInt();
        final Point[] points = new Point[nPoints];
        for (int i = 0; i < nPoints; i++) {
            final Point p = new Point(in.readInt(), in.readInt());
//            StdOut.println(p);
            points[i] = p;
        }

        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);

        final FastCollinearPoints fcp = new FastCollinearPoints(points);
        final LineSegment[] segments = fcp.segments();
//        StdOut.println("nSegments = " + segments.length);

        for (final LineSegment segment : segments) {
//            StdOut.println(segment);
            final int r = StdRandom.uniform(50, 200);
            final int g = StdRandom.uniform(50, 200);
            final int b = StdRandom.uniform(50, 200);
            // try to distinguish segments from one-another
            StdDraw.setPenColor(r, g, b);
            segment.draw();
        }

        StdDraw.setPenColor(0, 0, 0);
        StdDraw.setPenRadius(0.01);
        for (final Point p : points) {
            p.draw();
        }

        StdDraw.show();

    }

}