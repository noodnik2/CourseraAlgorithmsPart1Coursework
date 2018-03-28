import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  Princeton/Coursera Algorithms Part 1
 *  <p />
 *  Week 4 Assignment: "8 Puzzle"
 *  <p />
 *  Solve the 8-puzzle problem (and its natural generalizations) using the A*
 *  search algorithm.
 *  <p />
 *  Organize your program by creating an immutable data
 *  type Board with the following API:
 *  <pre>
 *      public class Board {
 *
 *          // construct a board from an n-by-n array of blocks
 *          // (where blocks[_row][_col] = block in row _row, column _col)
 *          public Board(int[][] blocks)
 *
 *          // board dimension n
 *          public int dimension()
 *
 *          // number of blocks out of placebelow)
 *          public int hamming()
 *
 *          // sum of Manhattan distances between blocks and goal
 *          public int manhattan()
 *
 *          // is this the goal board?
 *          public boolean isGoal()
 *
 *          // a board that is obtained by exchanging any pair of blocks
 *          public Board twin()
 *
 *          // does this board equal y?
 *          public boolean equals(Object y)
 *
 *          // all neighboring boards
 *          public Iterable<Board> neighbors()
 *
 *          // string representation of this board (in specified output format)
 *          public String toString()
 *
 *          // unit tests (not graded)
 *          public static void main(String[] args)
 *      }
 *  </pre>
 *  Your program should work correctly for arbitrary n-by-n boards (for
 *  any 2 ≤ n < 128), even if it is too slow to solve some of them in a
 *  reasonable amount of time.
 *  <p />
 *  Corner cases:
 *  <pre>
 *      You may assume that the constructor receives an n-by-n array containing
 *      the n2 integers between 0 and n2 − 1, where 0 represents the blank
 *      square.
 *  </pre>
 *  Performance requirements:
 *  <pre>
 *      Your implementation should support all Board methods in time
 *      proportional to n2 (or better) in the worst case.
 *  </pre>
 *  Input and Output Formats:
 *  <pre>
 *      The input and output format for a board is the board dimension
 *      n followed by the n-by-n initial board, using 0 to represent the
 *      blank square. As an example:
 *
 *          % more puzzle04.txt
 *          3
 *            0  1  3
 *            4  2  5
 *            7  8  6
 *  </pre>
 *  @author Marty Ross
 */
public class Board {

    private final byte[] _values;
    private final int _hammingDistance;
    private final int _manhattanDistance;

    /** cache of information regarding a given board size */
    private static final BoardSizeInfo[] BOARDSIZEINFO_CACHE = (
        new BoardSizeInfo[BoardSizeInfo.MAX_BOARD_SIZE]
    );

    private static class BoardSizeInfo {

        final short[] _manhattanDistances;
        final byte[] _posToRow;
        final byte[] _posToCol;
        final int _dim;

        static final int MAX_BOARD_SIZE = 128;
        private static final int[] BOARDSIZE2DIM = newBoardSize2Dim();

        BoardSizeInfo(final byte[] values) {
            final int maxPos = values.length;
            _dim = lookupDim(maxPos);
            final short[] manhattanDistances = new short[maxPos * maxPos];
            final byte[] posToRow = new byte[maxPos];
            final byte[] posToCol = new byte[maxPos];
            int lhsPosBase = 0;
            for (int lhsPos = 0; lhsPos < maxPos; lhsPos++) {
                final int lhsRow = lhsPos / _dim;
                final int lhsCol = lhsPos % _dim;
                posToRow[lhsPos] = (byte) lhsRow;
                posToCol[lhsPos] = (byte) lhsCol;
                for (int rhsPos = 0; rhsPos < maxPos; rhsPos++) {
                    manhattanDistances[lhsPosBase + rhsPos] = (short) (
                        calculateManhattanDistance(
                            new int[] { lhsRow, lhsCol },
                            new int[] { rhsPos / _dim, rhsPos % _dim }
                        )
                    );
                }
                lhsPosBase += maxPos;
            }
            _manhattanDistances = manhattanDistances;
            _posToRow = posToRow;
            _posToCol = posToCol;
        }

        static int lookupDim(final int maxPos) {
            for (int i = 0; i < BOARDSIZE2DIM.length; i++) {
                if (BOARDSIZE2DIM[i] == maxPos) {
                    return i;
                }
            }
            return (int) Math.sqrt(maxPos);
        }

        /**
         *  @param lhsRowCol first position (row, col)
         *  @param rhsRowCol second position (row, col)
         *  @return manhattan distance between the given positions
         */
        private static int calculateManhattanDistance(
            final int[] lhsRowCol,
            final int[] rhsRowCol
        ) {
            int distance = 0;
            for (int axis = 0; axis < 2; axis++) {
                distance += Math.abs(lhsRowCol[axis] - rhsRowCol[axis]);
            }
            return distance;
        }

        private static int[] newBoardSize2Dim() {
            final int[] boardsize2Dim = new int[MAX_BOARD_SIZE + 1];
            for (int i = 0; i < boardsize2Dim.length; i++) {
                boardsize2Dim[i] = i * i;
            }
            return boardsize2Dim;
        }

    }

    /**
     *  @param valueMatrix matrix of board values
     */
    public Board(final int[][] valueMatrix) {
        this(linearizeMatrix(valueMatrix));
    }

    /**
     *  @param values array of board values
     */
    private Board(final byte[] values) {

        _values = values;

        final BoardSizeInfo bsi = getBoardSizeInfo(_values);

        int hammingDistance = 0;
        int manhattanDistance = 0;

        final short[] posForValue = new short[_values.length];
        for (int pos = 0; pos < _values.length; pos++) {
            posForValue[_values[pos]] = (short) pos;
        }
        for (int pos = 0; pos < _values.length - 1; pos++) {
            final int currentBlockPos = posForValue[pos + 1];
            if (currentBlockPos != pos) {   // not where it's supposed to be
                hammingDistance++;
                manhattanDistance += bsi._manhattanDistances[
                    pos * _values.length + currentBlockPos
                ];
            }
        }

        _hammingDistance = hammingDistance;
        _manhattanDistance = manhattanDistance;

    }

    /**
     *  @return size of an edge of the matrix
     */
    public int dimension() {
        return BoardSizeInfo.lookupDim(_values.length);
    }

    /**
     *  @return number of blocks out of place
     */
    public int hamming() {
        return _hammingDistance;
    }

    /**
     *  @return sum of Manhattan (vertical and horizontal) distances
     *  between blocks to their goal positions, plus the number of
     *  moves made so far to get to the search node
     */
    public int manhattan() {
        return _manhattanDistance;
    }

    /**
     * @return {@code true} if we've reached the goal
     */
    public boolean isGoal() {
        return _hammingDistance == 0;
    }

    /**
     *  @return a copy of this board having a pair of blocks exchanged
     */
    public Board twin() {
        final int zeroPos = findValuePos(0);
        final BoardSizeInfo bsi = getBoardSizeInfo(_values);
        final int swapRowPos = bsi._posToRow[zeroPos] == 0 ? bsi._dim : 0;
        return createSwappedBoard(swapRowPos, 1);
    }

    /**
     *  @return {@code true} iff this board equal {@code boardObject}
     */
    public boolean equals(final Object boardObject) {
        if (!(boardObject instanceof Board)) {
            return false;
        }
        return Arrays.equals(_values, ((Board) boardObject)._values);
    }

    /**
     *  @return possible child boards as an {@link Iterable}
     */
    public Iterable<Board> neighbors() {

        final List<Board> calculatedNeighbors = new ArrayList<>(4);

        final int zeroPos = findValuePos(0);
        final BoardSizeInfo bsi = getBoardSizeInfo(_values);
        final int row = bsi._posToRow[zeroPos];
        final int col = bsi._posToCol[zeroPos];

        if (row > 0) {
            // swap with neighbor above
            calculatedNeighbors.add(createSwappedBoard(zeroPos, -bsi._dim));
        }
        if (row < bsi._dim - 1) {
            // swap with neighbor below
            calculatedNeighbors.add(createSwappedBoard(zeroPos, bsi._dim));
        }
        if (col > 0) {
            // swap with neighbor on the left
            calculatedNeighbors.add(createSwappedBoard(zeroPos, -1));
        }
        if (col < bsi._dim - 1) {
            // swap with neighbor on the right
            calculatedNeighbors.add(createSwappedBoard(zeroPos, 1));
        }

        return calculatedNeighbors;
    }

    /**
     *  @return string representation of this board's values
     *  (in the output format specified in this class' Javadoc)
     */
    public String toString() {
        final int dim = dimension();
        final StringBuilder s = new StringBuilder(dim + "\n");
        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                s.append(String.format("%2d ", _values[row * dim + col]));
            }
            s.append("\n");
        }
        return s.toString();
    }


    //
    //  Private class methods
    //

    /**
     *  @return information about the current board based upon its size
     */
    private static BoardSizeInfo getBoardSizeInfo(final byte[] values) {

        final int dim = BoardSizeInfo.lookupDim(values.length);

        final BoardSizeInfo bsi = BOARDSIZEINFO_CACHE[dim];
        if (bsi != null) {
            return bsi;
        }

        final BoardSizeInfo newBsce = new BoardSizeInfo(values);
        BOARDSIZEINFO_CACHE[dim] = newBsce;
        return newBsce;
    }

    private static byte[] linearizeMatrix(final int[][] valueMatrix) {

        if (valueMatrix == null) {
            throw new IllegalArgumentException();
        }

        final int size = valueMatrix.length;
        final byte[] values = new byte[size * size];

        int boardPos = 0;
        for (final int[] valueMatrixRow : valueMatrix) {
            if (valueMatrixRow == null || valueMatrixRow.length != size) {
                throw new IllegalArgumentException();
            }
            for (int col = 0; col < size; col++) {
                values[boardPos++] = (byte) valueMatrixRow[col];
            }
        }

        return values;
    }


    //
    //  Private instance methods
    //

    /**
     *  @param value value being sought
     *  @return position where the value was found within the current board
     */
    private int findValuePos(final int value) {
        final byte byteValue = (byte) value;
        for (int pos = 0; pos < _values.length; pos++) {
            if (_values[pos] == byteValue) {
                return pos;
            }
        }
        throw new IllegalArgumentException(
            String.format("value(%s) for maxPos(%s)", value, _values.length)
        );
    }

    /**
     *  @param boardPos position of block with which to swap
     *  @param offset position-relative offset of the block to swap
     *  @return a new board, with values swapped from first & second coordinates
     */
    private Board createSwappedBoard(final int boardPos, final int offset) {
        final byte[] newBoard = _values.clone();
        final byte tmp = newBoard[boardPos];
        newBoard[boardPos] = newBoard[boardPos + offset];
        newBoard[boardPos + offset] = tmp;
        return new Board(newBoard);
    }

}