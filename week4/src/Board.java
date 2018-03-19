/**
 *  Princeton/Coursera Algorithms Part 1
 *  <p />
 *  Week 4 Assignment: "8 Puzzle"
 *  <p />
 *  Solve the 8-puzzle problem (and its natural generalizations) using the A*
 *  search algorithm.  Organize your program by creating an immutable data
 *  type Board with the following API:
 *  <pre>
 *      public class Board {
 *
 *          // construct a board from an n-by-n array of blocks
 *          // (where blocks[i][j] = block in row i, column j)
 *          public Board(int[][] blocks)
 *
 *          // board dimension n
 *          public int dimension()
 *
 *          // number of blocks out of place
 *          public int hamming()
 *
 *          // sum of Manhattan distances between blocks and goal
 *          public int manhattan()
 *
 *          // is this board the goal board?
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
 *          // string representation of this board (in the output format specified below)
 *          public String toString()
 *
 *          // unit tests (not graded)
 *          public static void main(String[] args)
 *      }
 *  </pre>
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
 *  @author Marty Ross
 */
public class Board {

    /** construct a board from an n-by-n array of blocks
        (where blocks[i][j] = block in row i, column j)
     */
    public Board(int[][] blocks) {

    }

    /** board dimension n */
    public int dimension() {
        return -1;
    }

    /** number of blocks out of place */
    public int hamming() {
        return -1;
    }

    /** sum of Manhattan distances between blocks and goal */
    public int manhattan() {
        return -1;
    }

    /** is this board the goal board? */
    public boolean isGoal() {
        return false;
    }

    /** a board that is obtained by exchanging any pair of blocks */
    public Board twin() {
        return null;
    }

    /** does this board equal y? */
    public boolean equals(Object y) {
        return false;
    }

    /** all neighboring boards */
    public Iterable<Board> neighbors() {
        return null;
    }

    /** string representation of this board (in the output format specified below) */
    public String toString() {
        return null;
    }

    /** unit tests (not graded) */
    public static void main(String[] args) {

    }

}