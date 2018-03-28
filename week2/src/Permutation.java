
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

/**
 *  @author Marty Ross
 *  @see <a
 *  href="http://coursera.cs.princeton.edu/algs4/assignments/queues.html"
 *  >Programming Assignment 2: Deques and Randomized Queues</a>
 */
public class Permutation {


    //
    //  Public class methods
    //

    /**
     *  Reads in a sequence of (space-delimited) string tokens from the first
     *  line of standard input and prints to standard output a valid permutation
     *  of them (each on its own line) containing the number of items specified
     *  as the single argument supplied on the command line.
     *  @param inArgs single command line argument
     *  @see <a href="https://math.stackexchange.com/questions/890272/
     *  how-to-pick-a-random-sample-of-given-size-from-a-set-of-unknown-size">
     *  How to pick a random sample of given size from a set of unknown size
     *  </a>
     */
    public static void main(final String[] inArgs) {

        if (inArgs.length != 1) {
            throw new IllegalArgumentException("specify number to take");
        }

        final int nTake = Integer.parseInt(inArgs[0]);

        final RandomizedQueue<String> randQueue = new RandomizedQueue<>();
        int nSamples = 0;
        while(!StdIn.isEmpty()) {

            // read and count a sample
            final String token = StdIn.readString();
            nSamples++;

            // take tokens until we've got the desired number
            if (nSamples <= nTake) {
                randQueue.enqueue(token);
                continue;
            }

            // probabilistically replace incoming tokens to keep desired number
            if (StdRandom.uniform() <= ((double) nTake / (double) nSamples)) {
                randQueue.dequeue();    // randomly toss out one of the samples
                randQueue.enqueue(token);   // replacing it with the incoming
            }

        }

        for (int i = 0; i < nTake; i++) {
            StdOut.println(randQueue.dequeue());
        }

    }

}
