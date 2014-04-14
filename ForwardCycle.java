import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

public class ForwardCycle<T> implements Iterable<T> {

    public static void main(String[] args) {
        final Iterable<Integer> d1 = Arrays.asList(1, 2, 3, 4);
        final Iterable<String> d2 = Arrays.asList("foo", "bar", "duke", "john");
        final Iterable<Double> d3 = Arrays.asList(Math.PI, Math.E);

        final Vector<Iterable<? extends Object>> param = new Vector<Iterable<? extends Object>>(Arrays.asList(d1, d2, d3));
        final Iterable<Object[]> comprehension = new ForwardAccessComprehension(param);

        final Iterable<Object[]> cycleOnComprehension = new ForwardCycle<Object[]>(comprehension);
        final Iterator<Object[]> iter = cycleOnComprehension.iterator();
        int idx = 100;
        while (idx > 0) {
            final Object[] item = iter.next();
            String log = "";
            for (int i = 0; i < item.length; ++i) {
                log += item[i].toString() + ", ";
            }
            System.out.println("At " + idx + " : " + log);
            --idx;
        }
    }

    public ForwardCycle(final Iterable<T> what) {
        _what = what;
    }

    @Override
    public Iterator<T> iterator() {
        return new CycleIterator();
    }

    private final Iterable<T> _what;

    private class CycleIterator implements Iterator<T> {

        public CycleIterator() {
            _iterator = _what.iterator();
        }

        @Override
        public boolean hasNext() {
            if (_iterator.hasNext()) {
                return true; // <== 
            }
            _iterator = _what.iterator();
            return _iterator.hasNext(); // <== Cannot return "true" because trying to cycle over an empty sequence is not forbidden. 
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException(); // <== 
            }
            return _iterator.next(); // <== 
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(); // <== 
        }

        private Iterator<T> _iterator;
    }
}
