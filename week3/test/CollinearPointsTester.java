import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

/**
 *  Princeton/Coursera Algorithms Part 1
 *  <p />
 *  Test Harness for Week 3 Assignment: "Pattern Recognition"
 *  @author Marty Ross
 *  @see "java classes to be submitted: Points, {Brute, Fast}CollinearPoints"
 */
public class CollinearPointsTester {

    /** enable "animation" by setting this > 0 */
    private final static int animationDelayMs = 0;

    /** increase for more logging */
    private final static int verbosity = 0;

    /** size of the grid in the X direction */
    private static final double MAX_X = 32768d;

    /** size of the grid in the Y direction */
    private static final double MAX_Y = MAX_X;

    /**
     *  Operational "processing" modes
     */
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
     *  {@code collinear.zip} file);
     *  <p />
     *  Example:
     *  <br />
     *  <pre>
     *      fast input10 input20 input40 input50 input6 input8 input9 input56
     *      input80 input100 input200 input400 rs1423
     *  </pre>
     */
    public static void main(final String[] args) {

        if (args.length < 1) {
            throw new IllegalArgumentException("no processor type specified");
        }

        final ProcessorType processorType = ProcessorType.valueOf(args[0]);

        if (animationDelayMs > 0) {
            StdOut.printf("(animation speed is %sms)\n", animationDelayMs);
        }

        for (int i = 1; i < args.length; i++) {
            final String caseName = args[i];
            int rc;
            do {
                StdOut.print(caseName);
                rc = processCase(processorType, caseName);
                StdOut.println();
            } while(rc == 1);
            if (rc < 0) {
                break;
            }
        }

        // gee, I wish I could get rid of that dialog by other means...
        System.exit(0);

    }

    /**
     *  @param processorType type of processor to use
     *  @param caseName name of the case to process
     *  @return return code: -1 means "stop further processing",
     *  0 means "normal return", 1 means "repeat this test"
     */
    private static int processCase(
        final ProcessorType processorType,
        final String caseName
    ) {

        final String inputFilename = "week3/ext/collinear/" + caseName + ".txt";
        final In in = new In(inputFilename);
        final int nPoints = in.readInt();
        final Point[] points = new Point[nPoints];
        for (int i = 0; i < nPoints; i++) {
            final Point p = new Point(in.readInt(), in.readInt());
            if (verbosity > 10) {
                StdOut.println(p);
            }
            points[i] = p;
        }

        StdOut.print(" (" + points.length + " points) ");
        StdOut.print(inputFilename);

        StdDraw.setCanvasSize();    // (re-) initialize canvas
        if (animationDelayMs <= 0) {
            StdDraw.enableDoubleBuffering();
        }
        StdDraw.setXscale(0d, MAX_X);
        StdDraw.setYscale(0d, MAX_Y);
        StdDraw.setPenColor(StdDraw.GRAY);
        StdDraw.rectangle(
            MAX_X / 2,
            MAX_Y / 2,
            0.9999 * MAX_X / 2,
            0.9999 * MAX_Y / 2
        );

        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.setPenRadius(0.01);
        for (final Point p : points) {
            p.draw();
            if (animationDelayMs > 0) {
                StdDraw.pause(animationDelayMs);
            }
        }

        final LineSegment[] segments;
        final Stopwatch sw = new Stopwatch();
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
        StdOut.print(" [");
        StdOut.printf("%3.3f sec", sw.elapsedTime());
        StdOut.printf("/%s seg", segments.length);
        StdOut.print("]");

        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.setPenRadius(0.003);
        for (final LineSegment segment : segments) {
            if (verbosity > 5) {
                StdOut.println(segment);
            }
            segment.draw();
            if (animationDelayMs > 0) {
                StdDraw.pause(animationDelayMs);
            }
        }

        if (animationDelayMs <= 0) {
            StdDraw.show();
        }

        final String outputFilename = String.format(
            "week3/out/%s/%s.png",
            processorType,
            caseName
        );

        StdOut.print(" -> " + outputFilename);
        StdDraw.save(outputFilename);

        if (animationDelayMs <= 0) {
            return 0;
        }

        StdOut.printf("\n(click = locate, r = repeat, c = cont, q/ESC = quit)");

        while(true) {

            if (StdDraw.hasNextKeyTyped()) {
                final char nextKeyTyped = StdDraw.nextKeyTyped();
                switch(nextKeyTyped) {
                    case 'Q':
                    case 'q':
                    case 27:     // ESC
                        StdOut.printf("\n(quit)");
                        return -1;
                    case 'r':
                    case 'R':
                        StdOut.printf("\n(repeat)");
                        return 1;
                    case 'c':
                    case 'C':
                        StdOut.printf("\n(continue)");
                        return 0;
                    default:
                        StdOut.printf("\n(ignored:%s)", nextKeyTyped);
                }
            }

            if (StdDraw.isMousePressed()) {
                final double mouseX = StdDraw.mouseX();
                final double mouseY = StdDraw.mouseY();
                StdOut.printf("\npoint(%.1f,%.1f)", mouseX, mouseY);
            }

            StdDraw.pause(200);
        }

    }

}
