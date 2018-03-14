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
 *  @see "external test harness: CollinearPointsTester"
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

}