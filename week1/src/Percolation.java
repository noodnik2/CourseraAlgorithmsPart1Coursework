
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 *  First Programming Assignment - "Percolation"
 *  Coursera Princeton "Algorithms" course (Feb 2018)
 *  <p />
 *  Implementation of the "Percolation" API, with built-in test "main"
 *  method.
 *  <p />
 *  Invocation example: "{@code java Percolation < input.file}",
 *  where "{@code input.file}" is a file containing the size of
 *  an "edge" of the percolation matrix (in the first line), followed
 *  by a list of (space separated) row, col pairs.  The program will
 *  open the indicated cell(s) within the "percolation matrix", and
 *  then print out whether or not the system "percolates" or not.
 *  @author Marty Ross
 *  @see <a
 *  href="http://coursera.cs.princeton.edu/algs4/assignments/percolation.html"
 *  >Programming Assignment 1: Percolation</a>
 */
public class Percolation {


    //
    //  Private functional interfaces
    //  (needed since grader doesn't allow builtins)
    //

    /**
     *  @param <T> type of first argument
     *  @param <U> type of second argument
     */
    @FunctionalInterface
    private interface BiConsumer<T, U> {

        /**
         *  Performs this operation on the given arguments.
         *  @param t the first input argument
         *  @param u the second inmUfFullput argument
         */
        void accept(T t, U u);
    }

    /**
     *  @param <T> type of first argument
     *  @param <U> type of second argument
     *  @param <R> type of return value
     */
    @FunctionalInterface
    private interface BiFunction<T, U, R> {

        /**
         *  Applies this function to the given arguments.
         *  @param t the first function argument
         *  @param u the second function argument
         *  @return the function result
         */
        R apply(T t, U u);
    }

    /**
     *  @param <R> type of return value
     */
    @FunctionalInterface
    private interface IntFunction<R> {

        /**
         *  Applies this function to the given argument.
         *  @param value the function argument
         *  @return the function result
         */
        R apply(int value);
    }

    /**
     *  @param <T> type of argument
     */
    @FunctionalInterface
    private interface Supplier<T> {

        /**
         *  Gets a result.
         *  @return a result
         */
        T get();
    }


    //
    //  Private instance data
    //

    /** size of each edge of the percolation matrix. */
    private final int mEdgeLength;

    /** "open" status of each site corresponding to a "cell" in the matrix. */
    private final boolean[] mPsiteOpen;

    /** tracked number of currently open sites. */
    private int mNopenSites;

    /** helper object for tracking percolation. */
    private final WeightedQuickUnionUF mUfPerc;

    /** helper object for tracking "full" state. */
    private final WeightedQuickUnionUF mUfFull;


    //
    //  Private class data
    //

    /** id of the "virtual site" to represent the top of the grid. */
    private static final int SITEPFX_ROOT_TOP = 0;

    /** id of the "virtual site" to represent the bottom of the grid. */
    private static final int SITEPFX_ROOT_BOTTOM = 1;

    /** id of the first site representing linearized matrix cells. */
    private static final int SITEPFX_START_PMATRIX = 2;

    /** return code indicating "success". */
    private static final int RC_SUCCESS = 0;

    /** return code indicating error number 1. */
    private static final int RC_ERR1 = 1;

    /** return code indicating error number 2. */
    private static final int RC_ERR2 = 2;

    /** return code indicating error number 3. */
    private static final int RC_ERR3 = 3;

    /** return code indicating error number 4. */
    private static final int RC_ERR4 = 4;


    //
    // Things to keep in mind in general:
    //
    //  "rows" and "columns" are 1-based
    //  "sites" are 0-based
    //


    //
    //  Public class methods
    //

    /**
     *  Test client.
     *  @param inArgs input arguments (ignored)
     */
    public static void main(final String[] inArgs) {

        // test method 1: test percolation
        final BiFunction<Integer,Integer, Boolean> percTest = (from, to) -> {
            final Percolation p = new Percolation(10);
            for (int row = from; row <= to; row++) {
                p.open(row, 1);
            }
            return p.percolates();
        };

        final int N = 10;
        int nFailures = 0;
        if (!percTest.apply(1, N)) {
            System.out.println("open column didn't percolate as expected");
            nFailures++;
        }
        if (percTest.apply(2, N)) {
            System.out.println("closed top row percolated unexpectedly");
            nFailures++;
        }
        if (percTest.apply(1, N - 1)) {
            System.out.println("closed bottom row percolated unexpectedly");
            nFailures++;
        }

        // test method 2: count the number of open cells
        final IntFunction<Integer> openCellCountTest = (nToOpen) -> {
            final Percolation p = new Percolation(N);
            int nCellsOpened = 0;
            for (int row = 1; row <= N; row++) {
                for (int col = 1; col <= N; col++) {
                    // ensure initial state is not opened
                    if (p.isOpen(row,  col)) {
                        break;
                    }
                    p.open(row,  col);
                    // open again to confirm "duplicates" aren't counted
                    p.open(row,  col);
                    // ensure it's now open
                    if (!p.isOpen(row,  col)) {
                        break;
                    }
                    nCellsOpened++;
                    if (nCellsOpened >= nToOpen) {
                        break;
                    }
                }
                if (nCellsOpened >= nToOpen) {
                    break;
                }
            }
            return p.numberOfOpenSites();
        };

        if (openCellCountTest.apply(0) != 1) {
            System.out.println("cell count(0) failed");
            nFailures++;
        }

        if (openCellCountTest.apply(1) != 1) {
            System.out.println("cell count(1) failed");
            nFailures++;
        }

        if (openCellCountTest.apply(N + 1) != (N + 1)) {
            System.out.println("cell count(N+1) failed");
            nFailures++;
        }

        if (openCellCountTest.apply((N * N) + 1) != (N * N)) {
            System.out.println("cell count((N*N)+1) failed");
            nFailures++;
        }

        // test method 3: test what it means to be "full"
        final Supplier<Integer> fullCellTest = () -> {
            final Percolation p = new Percolation(N);

            if (p.isFull(1,  1)) {
                return RC_ERR1;   // top row should not initially be "full"
            }
            p.open(1, 1);
            if (!p.isFull(1,  1)) {
                return RC_ERR2;   // top row should now be "full"
            }

            if (p.isFull(2,  1)) {
                return RC_ERR3;   // second row should not initially be "full"
            }
            p.open(2, 1);
            if (!p.isFull(2,  1)) {
                return RC_ERR4;   // second row should now be "full"
            }

            return RC_SUCCESS;
        };

        if (fullCellTest.get() != 0) {
            System.out.println("full cell test failed");
            nFailures++;
        }

        // test 4: some edge cases
        final Percolation p4s1 = new Percolation(1);
        if (p4s1.percolates()) {
            System.out.println("size 1 system initially percolates");
            nFailures++;
        }

        final Percolation p4s2 = new Percolation(2);
        if (p4s2.percolates()) {
            System.out.println("size 2 system initially percolates");
            nFailures++;
        }

        for (final int n : new int[] { -10, -1, 0 }) {
            try {
                new Percolation(n);
                System.out.println("constructor(" + n + ") didn't throw");
                nFailures++;
            } catch(final IllegalArgumentException e) {
                // expected
            }
        }

        System.out.format("failures logged(%s)\n", nFailures);

        // pass the number of failures to the system
        // to enable scripts to take appropriate action
        // (this operation suppressed by request of the client)
        // System.exit(nFailures);
    }


    //
    //  Public instance construction
    //


    /**
     *  @param inEdgeLength length of an edge of a square matrix
     *  in which cells are modeled as "percolation sites" for
     *  tracking flows (connections) from the top edge to the
     *  bottom edge.
     *  @throws IllegalArgumentException if {@code inEdgeLength} ≤ 0
     */
    public Percolation(final int inEdgeLength) {

        if (inEdgeLength <= 0) {
            throw new IllegalArgumentException();
        }

        // record the edge length for use by instance
        mEdgeLength = inEdgeLength;

        // allocate array to track "percolation site open" state
        mPsiteOpen = new boolean[mEdgeLength * mEdgeLength];

        // allocate a "union-find" object used to model "flow" connections
        // within percolation grid, including allocation for "virtual sites"
        final int nSitesTotal = SITEPFX_START_PMATRIX + mPsiteOpen.length;
        mUfPerc = new WeightedQuickUnionUF(nSitesTotal);

        final BiConsumer<WeightedQuickUnionUF, Integer> initVs = (uf, vs) -> {
            final int row = vs.equals(SITEPFX_ROOT_TOP) ? 1 : mEdgeLength;
            final int rowSite = SITEPFX_START_PMATRIX + getRcOffset(row, 1);
            for (int i = 0; i < mEdgeLength; i++) {
                uf.union(vs, rowSite + i);
            }
        };

        // initialize the "virtual sites" for each of the top & bottom rows
        initVs.accept(mUfPerc, SITEPFX_ROOT_TOP);
        initVs.accept(mUfPerc, SITEPFX_ROOT_BOTTOM);

        // initialize a second "union-find" object to track "full" cells
        mUfFull = new WeightedQuickUnionUF(nSitesTotal);
        initVs.accept(mUfFull, SITEPFX_ROOT_TOP);

    }


    //
    //  Public instance methods
    //

    /**
     *  Open site (row, col) if it is not open already.
     *  @param inRow row within the percolation matrix
     *  @param inCol column within the percolation matrix
     *  @throws IllegalArgumentException thrown if the either the specified
     *  row or column coordinates are not valid
     */
    public void open(final int inRow, final int inCol) {

        assertValidRc(inRow, inCol);

        final int rcOffset = getRcOffset(inRow, inCol);
        if (mPsiteOpen[rcOffset]) {
            // ignore redundant "open" calls for a site/cell
            return;
        }

        mPsiteOpen[rcOffset] = true;     // mark site for the cell as open
        mNopenSites++;      // track the number of open sites

        // Connect the newly opened point to its neighbors to model the
        // physical property of "connection" (i.e., flows up, down, left,right)

        unionRcSites(rcOffset, getRcOffset(inRow + 1, inCol));  // up
        unionRcSites(rcOffset, getRcOffset(inRow - 1, inCol));  // down
        unionRcSites(rcOffset, getRcOffset(inRow, inCol - 1));  // left
        unionRcSites(rcOffset, getRcOffset(inRow, inCol + 1));  // right

    }

    /**
     *  Is site (row, col) open?
     *  @param inRow row within the percolation matrix
     *  @param inCol column within the percolation matrix
     *  @return {@code true} if the site indicated by (row, col) is open,
     *  or {@code false} otherwise
     *  @throws IllegalArgumentException thrown if the either the specified
     *  row or column coordinates are not valid
     */
    public boolean isOpen(final int inRow, final int inCol) {
        assertValidRc(inRow, inCol);
        return mPsiteOpen[getRcOffset(inRow, inCol)];
    }

    /**
     *  Is site (row, col) full?  I.e., is it connected to an open site in
     *  the top row?
     *  @param inRow row within the percolation matrix
     *  @param inCol column within the percolation matrix
     *  @return {@code true} iff the site identified by {@code inRow, inCol}
     *  is open, and is connected to the "top" container
     *  @throws IllegalArgumentException thrown if the either the specified
     *  row or column coordinates are not valid
     */
    public boolean isFull(final int inRow, final int inCol) {
        assertValidRc(inRow, inCol);
        final int rcOffset = getRcOffset(inRow, inCol);
        if (!mPsiteOpen[rcOffset]) {  // an unopened site can't be "full"
            return false;
        }
        return mUfFull.connected(
            SITEPFX_ROOT_TOP,
            SITEPFX_START_PMATRIX + rcOffset
        );
    }

    /**
     *  @return number of open sites
     */
    public int numberOfOpenSites() {
        return mNopenSites;
    }

    /**
     *  Does the system percolate?
     *  <p />
     *  A system percolates if we fill all open sites connected to the top
     *  row and that process fills some open site on the bottom row.
     *  @return {@code true} if the system percolates, or {@code false}
     *  otherwise
     */
    public boolean percolates() {
        return (
            mNopenSites > 0
         && mUfPerc.connected(SITEPFX_ROOT_TOP,  SITEPFX_ROOT_BOTTOM)
        );
    }


    //
    //  Private instance methods
    //

    /**
     *  @param inEdgeCoord an "edge coordinate"; i.e., either a "row" or
     *  "column" value, which is constrained to be 1 ≤ {@code inEdgeCoord}
     *  ≤ {@code _edgeLength}
     *  @return {@code true} if the passed "edge coordinate" is invalid
     *  (i.e., doesn't meet constraints), else returns {@code false}
     */
    private boolean isInvalidEdgeCoordinate(final int inEdgeCoord) {
        return inEdgeCoord <= 0 || inEdgeCoord > mEdgeLength;
    }

    /**
     *  @param inRow row number to validate
     *  @param inCol column n            umber to validate
     *  @throws IllegalArgumentException thrown if the either the specified
     *  row or column coordinates are not valid
     */
    private void assertValidRc(final int inRow, final int inCol) {
        if (isInvalidEdgeCoordinate(inRow) || isInvalidEdgeCoordinate(inCol)) {
            throw new IllegalArgumentException();
        }
    }

    /**
     *  @param inRow row number (1-based)
     *  @param inCol column number (1-based)
     *  @return positive linearized offset corresponding to the specified
     *  row, column (in row-most-significant order), or a negative number
     *  (i.e., invalid offset) if either {@code inRow} or {@code inCol}
     *  is outside of the valid range
     */
    private int getRcOffset(final int inRow, final int inCol) {
        if (isInvalidEdgeCoordinate(inRow) || isInvalidEdgeCoordinate(inCol)) {
            return -1;
        }
        return (mEdgeLength * (inRow - 1)) + inCol - 1;
    }

    /**
     *  Connect a "target" matrix site to the specified "candidate" (site),
     *  which may or may not represent a valid site.
     *  @param inTrcOffset offset of the target matrix site to connect to
     *  @param inCrcOffset offset of the candidate matrix site to be connected
     *  to the site represented by {@code inTrcOffset}, if both valid and open
     */
    private void unionRcSites(final int inTrcOffset, final int inCrcOffset) {
        if (
            inCrcOffset >= 0
         && inCrcOffset < mPsiteOpen.length
         && mPsiteOpen[inCrcOffset]
        ) {
            // connect the newly opened site to its open neighbor
            mUfPerc.union(
                SITEPFX_START_PMATRIX + inTrcOffset,
                SITEPFX_START_PMATRIX + inCrcOffset
            );
            // do also for the "full" cell tracker object
            mUfFull.union(
                SITEPFX_START_PMATRIX + inTrcOffset,
                SITEPFX_START_PMATRIX + inCrcOffset
            );
        }

    }

}
