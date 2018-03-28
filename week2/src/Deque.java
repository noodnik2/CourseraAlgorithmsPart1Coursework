
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Marty Ross
 * @see <a
 * href="http://coursera.cs.princeton.edu/algs4/assignments/queues.html"
 * >Programming Assignment 2: Deques and Randomized Queues</a>
 * @param <Item> type of item to be maintained by the {@link Deque}
 */
public class Deque<Item> implements Iterable<Item> {


    //
    //   Private instance data
    //

    /** pointer to the first element of the {@link Deque}, or {@code null} */
    private ItemListEntry<Item> mHeadEntry;

    /** pointer to the last element of the {@link Deque}, or {@code null} */
    private ItemListEntry<Item> mTailEntry;

    /** number of elements currently maintained in the {@link Deque} */
    private int mSize;


    //
    //  Private inner classes
    //

    /**
     *  @param <Item> type of item to be maintained
     */
    private static class ItemListEntry<Item> {
        Item mItem;
        ItemListEntry<Item> mPrevEntry;
        ItemListEntry<Item> mNextEntry;

        @Override
        public String toString() {
            return (
                "item:" + mItem
            );
        }
    }


    //
    //   Public instance methods
    //

    /**
     *  @return {@code true} iff the {@link Deque} is empty
     */
    public boolean isEmpty() {
       return mHeadEntry == null;
    }
 
    /**
     *  @return the number of items on the {@link Deque}
     */
    public int size() {
       return mSize;
    }
 
    /**
     *  @param inItem to be added to the front of the {@link Deque}
     */
    public void addFirst(final Item inItem) {
        if (inItem == null) {
            throw new IllegalArgumentException();
        }
        final ItemListEntry<Item> entry = new ItemListEntry<>();
        entry.mItem = inItem;
        entry.mNextEntry = mHeadEntry;
        if (mHeadEntry != null) {
            mHeadEntry.mPrevEntry = entry;
        }
        mHeadEntry = entry;
        if (mTailEntry == null) {
            mTailEntry = entry;
        }
        mSize++;
    }
 
    /**
     *  @param inItem to be added to the end of the {@link Deque}
     */
    public void addLast(final Item inItem) {
        if (inItem == null) {
            throw new IllegalArgumentException();
        }
        final ItemListEntry<Item> entry = new ItemListEntry<>();
        entry.mItem = inItem;
        entry.mPrevEntry = mTailEntry;
        if (mTailEntry != null) {
            mTailEntry.mNextEntry = entry;
        }
        mTailEntry = entry;
        if (mHeadEntry == null) {
            mHeadEntry = entry;
        }
        mSize++;
    }
 
    /**
     *  @return item that was removed from the front of the {@link Deque}
     */
    public Item removeFirst() {

        final ItemListEntry<Item> firstEntry = mHeadEntry;
        if (firstEntry == null) {
            throw new NoSuchElementException();
        }

        mHeadEntry = firstEntry.mNextEntry;
        if (mHeadEntry == null) {
            mTailEntry = null;
        } else {
            mHeadEntry.mPrevEntry = null;
        }
        mSize--;
        return firstEntry.mItem;
    }
 
    /**
     *  @return item that was removed from the end of the {@link Deque}
     */
    public Item removeLast()  {

        final ItemListEntry<Item> lastEntry = mTailEntry;
        if (lastEntry == null) {
            throw new NoSuchElementException();
        }

        mTailEntry = lastEntry.mPrevEntry;
        if (mTailEntry == null) {
            mHeadEntry = null;
        } else {
            mTailEntry.mNextEntry = null;
        }
        mSize--;
        return lastEntry.mItem;
    }
 
    /**
     *  @return iterator over items in order from front to end
     */
    public Iterator<Item> iterator()  {
       return new Iterator<Item>() {

           private ItemListEntry<Item> nextEntry = mHeadEntry;

           @Override
           public boolean hasNext() {
               return nextEntry != null;
           }

           @Override
           public Item next() {
               if (!hasNext()) {
                   throw new NoSuchElementException();
               }
               final Item itemToReturn = nextEntry.mItem;
               nextEntry = nextEntry.mNextEntry;
               return itemToReturn;
           }
       };
    }

    @Override
    public String toString() {
        return (
            "size:" + mSize + ", "
          + "head:" + mHeadEntry + ", "
          + "tail:" + mTailEntry
        );
    }
 
    /**
     *  Unit testing
     *  @param inArgs command line arguments
     */
    public static void main(final String[] inArgs) {

        final Deque<Object> d1 = new Deque<>();
        final Object d1oFirst = "d1 first";
        if (!d1.isEmpty()) throw new RuntimeException("d1 should be empty");
        d1.addFirst(d1oFirst);
        if (d1.isEmpty()) throw new RuntimeException("d1 should not be empty");
        if (d1.size() != 1) throw new RuntimeException("d1 size should be 1");
        if (d1.removeFirst() != d1oFirst) throw new RuntimeException("d1 no f");
        if (d1.size() != 0) throw new RuntimeException("d1 size should be 0");
        try {
            d1.removeFirst();
            throw new RuntimeException("d1 removed first while empty");
        } catch(final NoSuchElementException e) {
            // expected
        }
        try {
            d1.removeLast();
            throw new RuntimeException("d1 removed last while empty");
        } catch(final NoSuchElementException e) {
            // expected
        }

        final Deque<Object> d2 = new Deque<>();
        final Object d2oFirst = "d2 first";
        final Object d2oLast = "d2 last";
        d2.addLast(d2oLast);
        if (d2.isEmpty()) throw new RuntimeException("d2 should not be empty");
        d2.addFirst(d2oFirst);
        if (d2.size() != 2) throw new RuntimeException("d2 size should be 2");
        if (d2.removeFirst() != d2oFirst) throw new RuntimeException("d2 no f");
        if (d2.size() != 1) throw new RuntimeException("d2 size should be 1");
        if (d2.removeFirst() != d2oLast) throw new RuntimeException("d2 no f2");
        d2.addFirst(d2oLast);
        if (d2.removeLast() != d2oLast) throw new RuntimeException("d2 no l");
        d2.addLast(d2oLast);
        if (d2.removeFirst() != d2oLast) throw new RuntimeException("d2 no f3");
        d2.addFirst(d2oFirst);
        if (d2.removeLast() != d2oFirst) throw new RuntimeException("d2 no l2");

        final Deque<Integer> d3 = new Deque<>();
        d3.addFirst(3);
        d3.addFirst(2);
        d3.addFirst(1);
        d3.addLast(4);
        d3.addFirst(0);
        if (d3.size() != 5) throw new RuntimeException("d3 size should be 5");
        final Iterator<Integer> itemIterator1 = d3.iterator();
        final Iterator<Integer> itemIterator2 = d3.iterator();
        for (int i = 0; i <= 4; i++) {
            if (!itemIterator1.next().equals(i)) {
                throw new RuntimeException("iterator1 failed on " + i);
            }
            if (!itemIterator2.next().equals(i)) {
                throw new RuntimeException("iterator2 failed on " + i);
            }
        }
        try {
            itemIterator1.next();
            throw new RuntimeException("d3 next didn't throw");
        } catch(final NoSuchElementException e) {
            // expected
        }
        try {
            d3.iterator().remove();
            throw new RuntimeException("d3 remove didn't throw");
        } catch(final UnsupportedOperationException e) {
            // expected
        }
    }

}
