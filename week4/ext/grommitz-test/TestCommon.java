import edu.princeton.cs.algs4.In;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *  @author Martin Charlesworth (grommitz)
 */
public class TestCommon {

	protected Board from(String fileOrUrl) {
		if (fileOrUrl.startsWith("http://")) {
			In in;
			try {
				in = new In(new URL(fileOrUrl));
				return load(in);
			} catch (MalformedURLException e) {
				fail(e.getMessage());
				return null;
			}
		} else {
			File f = new File("ext/8puzzle/" + fileOrUrl);
			if (!f.exists()) 
				fail("Does the file exist? (" + f + ")");
			return load(new In(f));
		}
	}

	private Board load(In in) {
		int N = in.readInt();
		int[][] blocks = new int[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				blocks[i][j] = in.readInt();
		return new Board(blocks);
	}

	protected Board board(String str) {
		int[][] blocks = getBlocks(str);
		return new Board(blocks);
	}
	
	protected int[][] getBlocks(String str) {
		Scanner s = new Scanner(str);
		List<Integer> row = new ArrayList<>();
		int[][] blocks = null;
		int rowCount = 0;
		while (s.hasNext()) {
			String tok = s.next();
			if (tok.equals("/")) {
				if (blocks == null) {
					blocks = new int[row.size()][row.size()];
				}
				for (int j = 0; j < row.size(); ++j) {
					blocks[rowCount][j] = row.get(j);
				}
				rowCount++;
				row.clear();
			} else {
				row.add(Integer.parseInt(tok));
			}
		}
		if (!row.isEmpty()) {
			for (int j = 0; j < row.size(); ++j) {
				blocks[rowCount][j] = row.get(j);
			}
		}
		s.close();
		return blocks;
	}
}