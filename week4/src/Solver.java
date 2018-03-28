import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import edu.princeton.cs.algs4.Stopwatch;
import java.util.Comparator;

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

    private Node solution;

    private static class Node {
        private final Board board;
        private final Node prev;
        private final short moves;
        private final boolean isTwin;
        Node(Board board, Node prev, int moves, boolean isTwin) {
            this.board = board;
            this.prev = prev;
            this.moves = (short) moves;
            this.isTwin = isTwin;
        }

    }

    /**
     *  Find a solution to the initial board (using the A* algorithm)
     */
    public Solver(final Board initial) {
        final Comparator<Node> manhattanComparator = (
            (lhsNode, rhsNode) -> {
                final int lhsNodeCost = getCost(lhsNode);
                final int rhsNodeCost = getCost(rhsNode);
                if (lhsNodeCost == rhsNodeCost) {
                    return lhsNode.board.hamming() - rhsNode.board.hamming();
                }
                return lhsNodeCost - rhsNodeCost;
            }
        );
        solution = getSolution(initial, manhattanComparator);
    }

    /**
     *  Is the initial board solvable?
     */
    public boolean isSolvable() {
        return solution != null;
    }

    /**
     *  Min number of moves to solve initial board; -1 if unsolvable
     */
    public int moves() {
        return solution == null ? -1 : solution.moves;
    }

    /**
     *  @return sequence of boards in a shortest solution,
     *  or {@code null} if unsolvable
     */
    public Iterable<Board> solution() {
        if (solution == null) {
            return null;
        }
        final Stack<Board> boardStack = new Stack<>();
        for (Node curNode = solution; curNode != null; curNode = curNode.prev) {
            boardStack.push(curNode.board);
        }
        return boardStack;
    }

    /**
     *  Test Client
     *  </p >
     *  <pre>
     *      Solver test client. Use the following test client to read a puzzle
     *      from a file (specified as a command-line argument) and print the
     *      solution to standard output.
     *  </pre>
     */
    public static void main(final String[] args) {

        if (args == null || args.length < 1) {
            throw new IllegalArgumentException("no filename(s) specified");
        }

        // create initial board from file
        final In in = new In(args[0]);
        final int n = in.readInt();
        final int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                blocks[i][j] = in.readInt();
            }
        }

        final Stopwatch stopwatch = new Stopwatch();
        final Solver solver = new Solver(new Board(blocks));
        StdOut.printf("solver returned in %.2fs\n", stopwatch.elapsedTime());

        // print solution to standard output
        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
            return;
        }

        StdOut.printf("Minimum number of moves = %s\n", solver.moves());
        for (final Board board : solver.solution()) {
            StdOut.println(board);
        }
    }

    private int getCost(final Node node) {
        return node.moves + node.board.manhattan();
    }

    private Node getSolution(Board initial, Comparator<Node> comparator) {

        final MinPQ<Node> minPq = new MinPQ<>(comparator);
        minPq.insert(new Node(initial, null, 0, false));
        minPq.insert(new Node(initial.twin(), null, 0, true));

        while(true) {

            final Node curNode = minPq.delMin();
            if (curNode.board.isGoal()) {
                if (curNode.isTwin) {
                    StdOut.println("unsolveable");
                    return null;
                }
                return curNode;
            }

            for (final Board n : curNode.board.neighbors()) {
                if (curNode.prev == null || !n.equals(curNode.prev.board)) {
                    minPq.insert(
                        new Node(n, curNode, curNode.moves + 1, curNode.isTwin)
                    );
                }
            }
        }

    }

}