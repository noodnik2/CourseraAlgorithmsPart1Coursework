import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

/**
 *  Princeton/Coursera Algorithms Part 1
 *  <p />
 *  Test Harness for Week 3 Assignment: "Pattern Recognition"
 *  @author Marty Ross
 *  @see "java classes to be submitted: Points, {Brute, Fast}CollinearPoints"
 */
public class CollinearPointsTester {

    private enum ProcessorType {
        brute,  // use the "brute force" method
        fast    // use the "fast" method
    }

    /**
     *  @param args processor type and case name(s) to process, where
     *  <em>processor type</em> is one of:<ul>
     *      <li>{@code brute}</li>
     *      <li>{@code fast}</li>
     *  </ul>
     *  and <em>case name(s)</em> is/are the name(s) of the case(s) to process
     *  (e.g. name(s) of the {@code .txt} file(s) in the supplied
     *  {@code collinear.zip} file)
     */
    public static void main(final String[] args) {

        if (args.length < 1) {
            throw new IllegalArgumentException("no processor type specified");
        }

        final ProcessorType processorType = ProcessorType.valueOf(args[0]);
        for (int i = 1; i < args.length; i++) {
            final String caseName = args[i];
            StdOut.print(caseName);
            final Stopwatch sw = new Stopwatch();
            processFile(processorType, caseName);
            StdOut.printf(" [%3.3f sec]\n", sw.elapsedTime());
        }

        // gee, I wish I could kill that dialog by any other means...
        System.exit(0);
    }

    /**
     *  @param processorType type of processor to use
     *  @param caseName name of the case to process
     */
    private static void processFile(
        final ProcessorType processorType,
        final String caseName
    ) {

        final String inputFilename = "week3/ext/collinear/" + caseName + ".txt";
        final In in = new In(inputFilename);
        final int nPoints = in.readInt();
        final Point[] points = new Point[nPoints];
        for (int i = 0; i < nPoints; i++) {
            final Point p = new Point(in.readInt(), in.readInt());
//            StdOut.println(p);
            points[i] = p;
        }

        StdOut.print(" (" + points.length + " points) ");
        StdOut.print(inputFilename);

        StdDraw.setCanvasSize();    // (re-) initialize canvas
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0d, 32768d);
        StdDraw.setYscale(0d, 32768d);
        StdDraw.setPenColor(StdDraw.GRAY);
        StdDraw.rectangle(
            32768 / 2,
            32768 / 2,
            0.9999 * 32768 / 2,
            0.9999 * 32768 / 2
        );

        final LineSegment[] segments;
        switch(processorType) {
            case brute:
                final BruteCollinearPoints b = new BruteCollinearPoints(points);
                segments = b.segments();
                break;
            case fast:
                final FastCollinearPoints f = new FastCollinearPoints(points);
                segments = f.segments();
                break;
            default:
                // programmer error
                throw new IllegalArgumentException("unknown: " + processorType);

        }
//        StdOut.println("nSegments = " + segments.length);

        StdDraw.setPenColor(StdDraw.RED);
        for (final LineSegment segment : segments) {
//            StdOut.println(segment);
            segment.draw();
        }

        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.setPenRadius(0.01);
        for (final Point p : points) {
            p.draw();
        }

        StdDraw.show();

        final String outputFilename = String.format(
            "week3/out/%s/%s.png",
            processorType,
            caseName
        );

        StdOut.print(" -> " + outputFilename);
        StdDraw.save(outputFilename);

    }

}
