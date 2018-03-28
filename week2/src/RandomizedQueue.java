import edu.princeton.cs.algs4.StdRandom;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *    @author Marty Ross
 *    @see <a
 *    href="http://coursera.cs.princeton.edu/algs4/assignments/queues.html"
 *    >Programming Assignment 2: Deques and Randomized Queues</a>
 *    @param <Item> type of item to be maintained by the {@link RandomizedQueue}
 */
public class RandomizedQueue<Item> implements Iterable<Item> {


    //
    //  Private instance data
    //

    /** array of items, providing direct access by index */
    private Item[] mItemArray = createNewArray(2);

    /** number of items currently maintained in the array */
    private int mItemCount;


    //
    //  Private class methods
    //

    /**
     *  @param inSize size of the array to be created
     *  @param <Item> type of array to be created
     *  @return a new array of the given size
     */
    private static <Item> Item[] createNewArray(int inSize) {
        //noinspection unchecked
        return (Item[]) new Object[inSize];
    }

    /**
     *  @param inArray source array
     *  @param inCount number of entries to be returned
     *  @return a copy of a proper subset of the source array
     */
    private static <Item> Item[] arraySubset(
        final Item[] inArray,
        final int inCount
    ) {
        if (inCount > inArray.length) {
            throw new IllegalArgumentException();
        }
        final Item[] itemsCopy = createNewArray(inCount);
        // done manually since requirements ban use of library calls
        for (int i = 0; i < inCount; i++) {
            itemsCopy[i] = inArray[i];
        }
        return itemsCopy;
    }


    //
    //  Private instance methods
    //

    /**
     *  @param inNewCapacity new capacity of the underlying array; must
     *  be at least as big as the value {@link #mItemCount}
     */
    private void resizeItemArray(final int inNewCapacity) {
        if (inNewCapacity < mItemCount) {
            throw new IllegalArgumentException();
        }
        final Item[] newItemArray = createNewArray(inNewCapacity);
        // done manually since requirements ban use of library calls
        for (int i = 0; i < mItemCount; i++) {
            newItemArray[i] = mItemArray[i];
        }
        mItemArray = newItemArray;
    }


    //
    //   Public instance methods
    //

    /**
     *  @return number of items currently in the {@link RandomizedQueue}
     */
    public int size() {
        return mItemCount;
    }

    /**
     *  @return {@code true} iff the {@link RandomizedQueue} is empty
     */
    public boolean isEmpty()  {
         return mItemCount == 0;
    }

    /**
     *  @param inItem item which is added to the {@link RandomizedQueue}
     */
    public void enqueue(final Item inItem) {
        if (inItem == null) {
            throw new IllegalArgumentException();
        }
        if (mItemCount == mItemArray.length) {
            resizeItemArray(mItemArray.length * 2);
        }
        mItemArray[mItemCount++] = inItem;
    }
   
    /**
     *  @return random {@link Item}, which is removed from
     *  the {@link RandomizedQueue}
     */
    public Item dequeue() {
        if (mItemCount == 0) {
            throw new NoSuchElementException();
        }
        // pick a random item to return
        final int inIndex = StdRandom.uniform(mItemCount);
        final Item itemToReturn = mItemArray[inIndex];

        // move the last item maintained down to replace the one being returned
        mItemArray[inIndex] = mItemArray[mItemCount - 1];
        mItemArray[mItemCount - 1] = null;  // clear loitering reference
        mItemCount--;

        if (mItemCount > 0 && mItemCount == (mItemArray.length / 4)) {
            // shrink array if possible, but leave some growing room
            resizeItemArray(mItemArray.length / 2);
        }

        return itemToReturn;
    }
   
    /**
     *  @return random {@link Item}, which remains in
     *  the {@link RandomizedQueue}
     */
    public Item sample() {
        if (mItemCount == 0) {
            throw new NoSuchElementException();
        }
        return mItemArray[StdRandom.uniform(mItemCount)];
    }
   
    /**
     *  @return an independent iterator over items in random order
     */
    public Iterator<Item> iterator() {

        final Item[] itemsToIterate = arraySubset(mItemArray, mItemCount);
        StdRandom.shuffle(itemsToIterate);

        return new Iterator<Item>() {

            int iteratorIndex = 0;

            @Override
            public boolean hasNext() {
                return iteratorIndex < itemsToIterate.length;
            }

            @Override
            public Item next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return itemsToIterate[iteratorIndex++];
            }
        };
    }
   
    /**
     *  Unit testing (optional)
     */
    public static void main(final String[] inArgs) {

        final RandomizedQueue<Integer> rq1 = new RandomizedQueue<>();
        if (!rq1.isEmpty()) throw new RuntimeException("rq1 not empty");
        if (rq1.size() != 0) throw new RuntimeException("rq1 not size 0");

        for (int i = 0; i < 10; i++) {
            rq1.enqueue(i);
        }

        if (rq1.isEmpty()) throw new RuntimeException("rq1 is empty");
        if (rq1.size() != 10) throw new RuntimeException("rq1 not size 0");

        // test APIs for reading data with replacement
        final int initialSample = rq1.sample();
        int matchingSampleCount = 0;
        for (int i = 0; i < 100; i++) {
            if (initialSample == rq1.sample()) {
                matchingSampleCount++;
            }
        }
        // check for reasonable number of matching samples
        if (matchingSampleCount < 1 || matchingSampleCount > 50) {
            throw new RuntimeException("sample randomness in question");
        }

        // test APIs for reading data without replacement
        final int[] dequeueCounts = new int[10];
        final int[] iterator1Counts = new int[10];
        final int[] iterator2Counts = new int[10];
        int countIndexSameAsValue = 0;
        int countSameValueReturned = 0;
        final Iterator<Integer> iterator1 = rq1.iterator();
        final Iterator<Integer> iterator2 = rq1.iterator();
        for (int i = 0; i < 10; i++) {

            final int dqv = rq1.dequeue();
            if (dqv == i) {
                countIndexSameAsValue++;
            }
            dequeueCounts[dqv]++;

            final int i1v = iterator1.next();
            if (i1v == i) {
                countIndexSameAsValue++;
            }
            iterator1Counts[i1v]++;

            final int i2v = iterator2.next();
            if (i2v == i) {
                countIndexSameAsValue++;
            }
            iterator2Counts[i2v]++;

            if (dqv == i1v || dqv == i2v || i1v == i2v) {
                countSameValueReturned++;
            }

        }

        // should be empty now
        if (!rq1.isEmpty()) throw new RuntimeException("rq1 not empty2");

        // unlikely that > 6 would "randomly" be the same
        if (countIndexSameAsValue > 6) {
            throw new RuntimeException("randomness in question");
        }

        // unlikely that > 6 would "randomly" be the same
        if (countSameValueReturned > 6) {
            throw new RuntimeException("independence in question");
        }

        // check that all values were returned from dequeue(), it1 & it2
        for (int i = 0; i < 10; i++) {
            if (dequeueCounts[i] != 1) throw new RuntimeException("deq spread");
            if (iterator1Counts[i] != 1) throw new RuntimeException("i1spread");
            if (iterator2Counts[i] != 1) throw new RuntimeException("i2spread");
        }

        // test exceptional behavior for edge cases
        try {
            rq1.enqueue(null);
            throw new RuntimeException("can enqueue null");
        } catch(final IllegalArgumentException e) {
            // expected
        }

        try {
            rq1.dequeue();
            throw new RuntimeException("can dequeue empty");
        } catch(final NoSuchElementException e) {
            // expected
        }

        try {
            rq1.sample();
            throw new RuntimeException("can sample empty");
        } catch(final NoSuchElementException e) {
            // expected
        }

        // check for java.lang.ArrayIndexOutOfBoundsException
        final RandomizedQueue<Integer> rq2 = new RandomizedQueue<>();
        for (int i = 0; i < 100; i++) {
            rq2.enqueue(i);
        }
        for (int i = 0; i < 100; i++) {
            rq2.dequeue();
        }
        for (int i = 0; i < 100; i++) {
            rq2.enqueue(i);
            rq2.dequeue();
        }
    }

}

