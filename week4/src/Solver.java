import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 *  Princeton/Coursera Algorithms Part 1
 *  <p />
 *  Week 4 Assignment: "8 Puzzle"
 *  <p />
 *  Create an immutable data type Solver with the following API:
 *  <pre>
 *      public class Solver {
 *
 *          // find a solution to the initial board (using the A* algorithm)
 *          public Solver(Board initial)
 *
 *          // is the initial board solvable?
 *          public boolean isSolvable()
 *
 *          // min number of moves to solve initial board; -1 if unsolvable
 *          public int moves()
 *
 *          // sequence of boards in a shortest solution; null if unsolvable
 *          public Iterable<Board> solution()
 *
 *          // solve a slider puzzle (given below)
 *          public static void main(String[] args)
 *      }
 *  </pre>
 *  To implement the A* algorithm, you must use MinPQ from algs4.jar for
 *  the priority queue(s).
 *  <p />
 *  Corner cases:
 *  <pre>
 *      The constructor should throw a java.lang.IllegalArgumentException
 *      if passed a null argument.
 *  </pre>
 *  @author Marty Ross
 */
public class Solver {

    /**
     *  Find a solution to the initial board (using the A* algorithm)
     */
    public Solver(Board initial) {

    }

	/**
     *  Is the initial board solvable?
     */
    public boolean isSolvable() {
        return false;
    }

    /**
     *  Min number of moves to solve initial board; -1 if unsolvable
     */
    public int moves() {
        return -1;
    }

	/**
     * Sequence of boards in a shortest solution; null if unsolvable
     */
    public Iterable<Board> solution() {
        return null;
    }

	/**
     * solve a slider puzzle (given below)
     */
    public static void main(String[] args) {

        // create initial board from file
        final In in = new In(args[0]);
        final int n = in.readInt();
        final int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                blocks[i][j] = in.readInt();
            }
        }

        final Board initial = new Board(blocks);

        // solve the puzzle
        final Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        }
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}