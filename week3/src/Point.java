/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *  
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.StdDraw;

import java.util.Comparator;

public class Point implements Comparable<Point> {


    //
    //  Private instance data
    //

    private final int x;     // x-coordinate of this point
    private final int y;     // y-coordinate of this point


    //
    //  Public instance construction
    //

    /**
     * Initializes a new point.
     *
     * @param  x the <em>x</em>-coordinate of the point
     * @param  y the <em>y</em>-coordinate of the point
     */
    public Point(final int x, final int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }


    //
    //  Public instance methods
    //

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(final Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completeness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertical;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param  that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(final Point that) {
        if (this.x == that.x && this.y == that.y) {
            return Double.NEGATIVE_INFINITY;
        }
        if (this.x == that.x) {
            return Double.POSITIVE_INFINITY;
        }
        if (this.y == that.y) {
            return 0d;
        }
        return (double) (that.y - this.y) / (that.x - this.x);
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param  that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument
     *         point (x0 = x1 and y0 = y1);
     *         a negative integer if this point is less than the argument
     *         point; and a positive integer if this point is greater than the
     *         argument point
     */
    public int compareTo(final Point that) {
        if (this.y == that.y) {
            return this.x - that.x;
        }
        return this.y - that.y;
    }

    /**
     *  Compares two points by the slope they make with this point.
     *  The slope is defined as in the slopeTo() method.
     *  @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder() {
        return Comparator.comparingDouble(this::slopeTo);
    }


    /**
     *  Returns a string representation of this point.
     *  This method is provide for debugging;
     *  your program should not rely on the format of the string representation.
     *  @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }


    // functional interface used only for testing purposes

    /**
     *  Two-parameter consumer
     *  @param <P1T> type of first parameter
     *  @param <P2T> type of second parameter
     */
    @FunctionalInterface
    private interface BiConsumer<P1T, P2T> {

        /**
         *  Performs operation on given arguments
         *  @param p1v first parameter value
         *  @param p2v first parameter value
         */
        void accept(P1T p1v, P2T p2v);
    }

    /**
     *  Unit tests the Point data type.
     */
    public static void main(final String[] args) {

        final BiConsumer<Point, Point> ssfn = (
            (p0, p1) -> System.out.format(
                "slope between %s and %s is %s\n",
                p0,
                p1,
                p0.slopeTo(p1)
            )
        );
        ssfn.accept(new Point(50, 50), new Point(100, 100));
        ssfn.accept(new Point(100, 100), new Point(50, 50));
        ssfn.accept(new Point(100, 100), new Point(0, 50));
        ssfn.accept(new Point(100, 100), new Point(50, 0));
        ssfn.accept(new Point(100, 100), new Point(10, 10));
        ssfn.accept(new Point(100, 100), new Point(10, 100));
        ssfn.accept(new Point(100, 100), new Point(100, 10));
        ssfn.accept(new Point(100, 100), new Point(100, 100));

        final BiConsumer<Point, Point> scfn = (
            (p0, p1) -> System.out.format(
                "compare between %s and %s is %s\n",
                p0,
                p1,
                p0.compareTo(p1)
            )
        );
        scfn.accept(new Point(50, 50), new Point(100, 100));
        scfn.accept(new Point(50, 50), new Point(50, 50));
        scfn.accept(new Point(50, 50), new Point(50, 100));
        scfn.accept(new Point(50, 50), new Point(100, 50));
        scfn.accept(new Point(50, 50), new Point(20, 20));

        final Point ref = new Point(100, 100);
        final BiConsumer<Point, Point> ssrcfn = (
            (p0, p1) -> System.out.format(
                "slope to %s compare between %s and %s is %s\n",
                ref,
                p0,
                p1,
                ref.slopeOrder().compare(p0, p1)
            )
        );
        ssrcfn.accept(new Point(50, 50), new Point(100, 100));
        ssrcfn.accept(new Point(50, 50), new Point(50, 50));
        ssrcfn.accept(new Point(50, 50), new Point(50, 100));
        ssrcfn.accept(new Point(50, 50), new Point(100, 50));
        ssrcfn.accept(new Point(50, 50), new Point(20, 20));

    }

}