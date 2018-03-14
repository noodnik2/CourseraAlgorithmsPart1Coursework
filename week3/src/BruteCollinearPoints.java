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
 */
public class BruteCollinearPoints {

    /* line segment instances being managed */
    private final LineSegment[] segments;

    /**
     *  Finds all line segments containing 4 collinear points
     *  @param inPoints set of input points to be considered
     */
    public BruteCollinearPoints(final Point[] inPoints) {

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

        // find all sets of 4 collinear points via brute force permutation

        final List<LineSegment> foundSegments = new ArrayList<>();
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

                    Point maxPoint = null;
                    for (int s = r + 1; s < points.length; s++) {
                        final Point pointS = points[s];

                        if (p2qSlope == pointP.slopeTo(pointS)) {
                            if (maxPoint == null || pointS.compareTo(maxPoint) > 0) {
                                maxPoint = pointS;
                            }
                        }

                    }
                    if (maxPoint != null) {
                        foundSegments.add(new LineSegment(pointP, maxPoint));
                    }

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

        final BruteCollinearPoints bcp = new BruteCollinearPoints(points);
        final LineSegment[] segments = bcp.segments();
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