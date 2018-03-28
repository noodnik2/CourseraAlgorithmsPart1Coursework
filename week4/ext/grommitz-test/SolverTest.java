import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Iterator;

import org.junit.Test;

/**
 *  @author Martin Charlesworth (grommitz)
 */
public class SolverTest extends TestCommon {

	@Test
	public void testUnsolveable() {
		assertThat(solvable("1 2 3 / 4 5 6 / 8 7 0"), is(false));
		assertThat(solvable("0 1 3 / 4 2 5 / 7 8 6"), is(true));
		assertThat(solvable(from("http://coursera.cs.princeton.edu/algs4/testing/8puzzle/puzzle3x3-unsolvable.txt")), is(false));
		assertThat(solvable(from("http://coursera.cs.princeton.edu/algs4/testing/8puzzle/puzzle3x3-unsolvable1.txt")), is(false));
		assertThat(solvable(from("http://coursera.cs.princeton.edu/algs4/testing/8puzzle/puzzle3x3-unsolvable2.txt")), is(false));
		assertThat(solvable(from("http://coursera.cs.princeton.edu/algs4/testing/8puzzle/puzzle4x4-unsolvable.txt")), is(false));
	}

	@Test
	public void puzzle04() {
		Solver s = new Solver(from("puzzle04.txt"));
		assertThat(s.isSolvable(), is(true));
		assertThat(s.moves(), is(4));
		assertThat(move(s.solution(), 1), is(board("1 0 3 / 4 2 5 / 7 8 6")));
		assertThat(move(s.solution(), 2), is(board("1 2 3 / 4 0 5 / 7 8 6")));
		assertThat(move(s.solution(), 3), is(board("1 2 3 / 4 5 0 / 7 8 6")));
		assertThat(move(s.solution(), 4), is(board("1 2 3 / 4 5 6 / 7 8 0")));
		
	}

	@Test
	public void puzzle07() {
		Solver s = new Solver(from("http://coursera.cs.princeton.edu/algs4/testing/8puzzle/puzzle07.txt"));
		assertThat(s.moves(), is(7));
		print(s.solution());
	}
	
	@Test
	public void puzzle08() {
		Solver s = new Solver(from("http://coursera.cs.princeton.edu/algs4/testing/8puzzle/puzzle08.txt"));
		assertThat(s.moves(), is(8));
		print(s.solution());
	}
	
	@Test
	public void puzzle12() {
		Solver s = new Solver(from("http://coursera.cs.princeton.edu/algs4/testing/8puzzle/puzzle12.txt"));
		assertThat(s.moves(), is(12));
		print(s.solution());
	}
	
	private Board move(Iterable<Board> solution, int n) {
		Iterator<Board> it = solution.iterator();
		int move = 0;
		while (it.hasNext()) {
			Board b = it.next();
			if (move++ == n) return b;
		}
		throw new RuntimeException("no such move: " + n);
	}
	
	@SuppressWarnings("unused")
	private void print(Iterable<Board> solution) {
		Iterator<Board> it = solution.iterator();
		int move = 0;
		while (it.hasNext()) {
			System.out.println("Move " + move++ + "\n" + it.next());
		}
	}

	private boolean solvable(String s) {
		return solvable(board(s));
	}

	private Boolean solvable(Board b) {
		return new Solver(b).isSolvable();
	}

}