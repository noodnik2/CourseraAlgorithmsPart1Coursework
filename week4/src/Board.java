import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 *          // construct a _values from an n-by-n array of blocks
 *          // (where blocks[_row][_col] = block in row _row, column _col)
 *          public Board(int[][] blocks)
 *
 *          // _values dimension n
 *          public int dimension()
 *
 *          // number of blocks out of placebelow)
 *          public int hamming()
 *
 *          // sum of Manhattan distances between blocks and goal
 *          public int manhattan()
 *
 *          // is this _values the goal _values?
 *          public boolean isGoal()
 *
 *          // a _values that is obtained by exchanging any pair of blocks
 *          public Board twin()
 *
 *          // does this _values equal y?below)
 *          public boolean equals(Object y)
 *
 *          // all neighboring boards
 *          public Iterable<Board> neighbors()
 *
 *          // string representation of this _values (in the output format specified below)
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
 *  </pre>_values
 *  @author Marty Ross
 */
public class Board {

    /**
     *  Immutable array to hold the board's values
     */
	private final byte[] _values;
	private final byte[] _valueMap;

    /**
     *  Size of an edge of the board
     */
	private final int _size;
	private final int _hammingDistance;
	private final int _manhattanDistance;

	private static final Map<Board, Collection<Board>> NEIGHBORS = new HashMap<>();


    /**
     *  Coord of a position within the board
     */
	private class Coord {

	    private final int _row, _col;

	    Coord(final int pos) {
			_row = pos / _size;
			_col = pos % _size;
		}

		Coord(final int row, final int col) {
			_row = row;
			_col = col;
		}

		int getManhattanDistanceTo(final Coord c) {
			return Math.abs(_row - c._row) + Math.abs(_col - c._col);
		}

		int getBoardPos() {
	        return _row * _size + _col;
        }
	}

    /**
     *  @param valueMatrix matrix of board values
     */
    public Board(final int[][] valueMatrix) {
        this(linearizeMatrixIntoArray(valueMatrix));
    }

    /**
     *  @param values array of board values
     */
    private Board(final byte[] values) {

        _values = values;
        _size = (int) Math.sqrt(_values.length);

        _valueMap = new byte[_size * _size];    // reverse-lookup map
        for (int boardPos = 0; boardPos < _size * _size; boardPos++) {
            _valueMap[_values[boardPos]] = (byte) boardPos;
        }

        int hammingDistance = 0;
        for (int pos = 0; pos < _size * _size - 1; pos++) {
            if (valueAt(pos) != pos + 1) {
                hammingDistance++;
            }
        }
        _hammingDistance = hammingDistance;

        int manhattanDistance = 0;
        for (int pos = 0; pos < _size * _size - 1; pos++) {
            if (valueAt(pos) != pos + 1) {
                manhattanDistance += new Coord(pos)
                    .getManhattanDistanceTo(findValue(pos + 1));
            }
        }
        _manhattanDistance = manhattanDistance;

    }

    /**
     *  @return size of an edge of the matrix
     */
    public int dimension() {
        return _size;
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
		final int row = findValue(0)._row == 0 ? 1 : 0;
		return swap(new Coord(row, 0), new Coord(row, 1));
    }

    /**
     *  @return {@code true} iff this board equal {@code boardObject}
     */
    public boolean equals(final Object boardObject) {
        if (!(boardObject instanceof Board)) {
			return false;
		}
		final Board board = (Board) boardObject;
		if (_size != board._size) {
			return false;
		}
		for (int boardPos = 0; boardPos < _size * _size; boardPos++) {
		    if (_values[boardPos] != board._values[boardPos]) {
		        return false;
            }
        }
		return true;
    }

    /**
     *  @return iterator to possible child boards
     */
    public Iterable<Board> neighbors() {
        final Iterable<Board> cachedNeighbors = NEIGHBORS.get(this);
        if (cachedNeighbors != null) {
            return cachedNeighbors;
        }

        final List<Board> calculatedNeighbors = new ArrayList<>(4);
        final Coord p = findValue(0);
        if (p._row > 0) {
            calculatedNeighbors.add(swap(p, new Coord(p._row - 1, p._col)));
        }
        if (p._row < _size - 1) {
            calculatedNeighbors.add(swap(p, new Coord(p._row + 1, p._col)));
        }
        if (p._col > 0) {
            calculatedNeighbors.add(swap(p, new Coord(p._row, p._col - 1)));
        }
        if (p._col < _size - 1) {
            calculatedNeighbors.add(swap(p, new Coord(p._row, p._col + 1)));
        }

        NEIGHBORS.put(this, calculatedNeighbors);
        StdOut.printf("cacheSize(%s)\n", NEIGHBORS.size());
        return calculatedNeighbors;
    }

    /**
     *  @return string representation of this board's values
     *  (in the output format specified in this class' Javadoc)
     */
    public String toString() {
        final StringBuilder s = new StringBuilder(_size + "\n");
        for (int row = 0; row < _size; row++) {
            for (int col = 0; col < _size; col++) {
                s.append(String.format("%2d ", _values[row * _size + col]));
            }
            s.append("\n");
        }
        return s.toString();
    }


    //
    //  Private instance methods
    //

    /**
     *  @param value value being sought
     *  @return coordinate where the value was found within the current board
     */
    private Coord findValue(final int value) {
        return new Coord(_valueMap[value]);
	}

    /**
     *  @param boardPos position on the board
     *  @return value at the specified position
     */
	private byte valueAt(final int boardPos) {
		return _values[boardPos];
	}

    /**
     *  @param c coordinate on the board
     *  @return value at the specified coordinate
     */
	private byte valueAt(final Coord c) {
		return valueAt(c.getBoardPos());
	}

    /**
     *  @param c1 coordinate of first value
     *  @param c2 coordinate of second value
     *  @return a new board, with values swapped from first & second coordinates
     */
	private Board swap(final Coord c1, final Coord c2) {
		final byte[] newBoard = _values.clone();
		newBoard[c1.getBoardPos()] = valueAt(c2);
		newBoard[c2.getBoardPos()] = valueAt(c1);
		return new Board(newBoard);
	}

	public static byte[] linearizeMatrixIntoArray(final int[][] valueMatrix) {

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

}