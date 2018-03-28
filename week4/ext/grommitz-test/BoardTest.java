import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Iterator;

import org.junit.Test;

/**
 *  @author Martin Charlesworth (grommitz)
 */
public class BoardTest extends TestCommon {

	@Test
	public void createBoard() {
		Board b = board("1 2 3 / 5 7 8 / 0 6 4");
		System.out.println(b.toString());
	}

	@Test
	public void testIsGoal() {
		assertThat(board("1 2 3 / 5 7 8 / 0 6 4").isGoal(), is(false));
		assertThat(board("1 2 3 / 4 5 6 / 7 8 0").isGoal(), is(true));
	}

	@Test
	public void testHamming() {
		assertThat(board("1 2 3 / 5 4 6 / 7 8 0").hamming(), is(2));
		assertThat(board("1 2 3 / 4 5 6 / 7 8 0").hamming(), is(0));
		assertThat(board("8 1 3 / 4 0 2 / 7 6 5").hamming(), is(5));
		assertThat(board("3 1 2 / 6 0 5 / 4 7 8").hamming(), is(8));
	}

	@Test
	public void testEquals() {
		assertThat(board("0 1 3 / 4 2 5 / 7 8 6").equals(board("1 0 3 / 4 2 5 / 7 8 6")), is(false));
		assertThat(board("0 1 3 / 4 2 5 / 7 8 6").equals(board("0 1 3 / 4 2 5 / 7 8 6")), is(true));		
	}
	
	@Test
	public void testManhattan4b() {
		assertThat(board("0 1 3 / 4 2 5 / 7 8 6").manhattan(), is(4));
	}

	@Test
	public void testManhattan5() {
		assertThat(board("4 1 3 / 0 2 5 / 7 8 6").manhattan(), is(5));
	}

	@Test
	public void testManhattan3() {
		assertThat(board("1 0 3 / 4 2 5 / 7 8 6").manhattan(), is(3));
	}

	@Test
	public void testManhattan4a() {
		assertThat(board("1 2 3 / 4 5 6 / 7 8 0").manhattan(), is(0));
	}

	@Test
	public void testManhattan10() {
		assertThat(board("8 1 3 / 4 0 2 / 7 6 5").manhattan(), is(10));
	}

	@Test
	public void testNeighbors() {
		Board initial = board("0 1 3 / 4 2 5 / 7 8 6");
		Board neighbour1 = board("1 0 3 / 4 2 5 / 7 8 6");
		Board neighbour2 = board("4 1 3 / 0 2 5 / 7 8 6");

		Iterable<Board> neighbours = initial.neighbors();
		assertThat(count(neighbours), is(2));
		
		Iterator<Board> it = neighbours.iterator();
		while (it.hasNext()) {
			Board neighbour = it.next();
			assertThat(neighbour.equals(neighbour1) || neighbour.equals(neighbour2), is(true));
		}
	}

	@Test
	public void testTwin() {
		assertThat(board("0 1 3 / 4 2 5 / 7 8 6").twin(), is(board("0 1 3 / 2 4 5 / 7 8 6")));
		assertThat(board("6 1 3 / 4 2 5 / 7 8 0").twin(), is(board("1 6 3 / 4 2 5 / 7 8 0")));
	}
	
	@Test
	public void testToString() {
        assertThat(board("2 0 3 4 / 1 10 6 8 / 5 9 7 12 / 13 14 11 15").toString(), is("4\n"
        		+ " 2  0  3  4 \n"
        		+ " 1 10  6  8 \n"
        		+ " 5  9  7 12 \n"
        		+ "13 14 11 15 \n"));
	}
	
	private <T> int count(Iterable<T> iterable) {
		int n = 0;
		Iterator<T> i = iterable.iterator();
		while (i.hasNext()) {
			++n;
			i.next();
		}
		return n;
	}
	

	
}