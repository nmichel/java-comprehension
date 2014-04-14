import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Vector;

public class ForwardAccessComprehension extends AbstractSequentialList<Object[]> {

    public static void main(String[] args) {
        final Iterable<Integer> d1 = Arrays.asList(1, 2, 3, 4);
        final Iterable<String> d2 = Arrays.asList("foo", "bar", "duke", "john");
        final Iterable<Double> d3 = Arrays.asList(Math.PI, Math.E);

        final Vector<Iterable<? extends Object>> param = new Vector<Iterable<? extends Object>>(Arrays.asList(d1, d2, d3));
        final Iterable<Object[]> comprehension = new ForwardAccessComprehension(param);

        final Iterator<Object[]> iter = comprehension.iterator();
        int idx = 0;
        while (iter.hasNext()) {
            final Object[] item = iter.next();
            String log = "";
            for (int i = 0; i < item.length; ++i) {
                log += item[i].toString() + ", ";
            }
            System.out.println("At " + idx++ + " : " + log);
        }
    }

    public ForwardAccessComprehension(final AbstractList<Iterable<? extends Object>> cartesian) {
        _cartesian = cartesian;
    }

    @Override
    public ListIterator<Object[]> listIterator(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(); // <== 
        }
        final ListIterator<Object[]> iter = new MyIterator();
        int dec = index;
        while (dec-- > 0) {
            if (iter.hasNext()) {
                iter.next();
                continue; // <== 
            }
            throw new IndexOutOfBoundsException(); // <==
        }
        return iter; // <== 
    }

    @Override
    public int size() {
        final ListIterator<Object[]> iter = listIterator();
        int size = 0;
        while (iter.hasNext()) {
            iter.next();
            ++size;
        }
        return size;
    }

    private final AbstractList<Iterable<? extends Object>> _cartesian;

    private class MyIterator implements ListIterator<Object[]> {

        public MyIterator() {
            _iterators = new Vector<Iterator<? extends Object>>(_cartesian.size());
            for (final Iterable<? extends Object> iter : _cartesian) {
                _iterators.add(iter.iterator());
            }

            _objects = new Object[_cartesian.size()];
        }

        @Override
        public void add(Object[] arg0) {
            throw new UnsupportedOperationException(); // <== 
        }

        @Override
        public boolean hasNext() {
            // If no iteration can provide a "next" element, we are finished with the comprehension.
            // 
            for (int iter = _iterators.size() - 1; iter >= 0; --iter) {
                final Iterator<? extends Object> current = _iterators.get(iter);
                if (current.hasNext()) {
                    return true; // <== 
                }
            }
            return false; // <== 
        }

        @Override
        public boolean hasPrevious() {
            throw new UnsupportedOperationException(); // <== 
        }

        @Override
        public Object[] next() {
            if (!hasNext()) {
                throw new NoSuchElementException(); // <== 
            }

            // Processing bottom up, if a level cannot provide a "next" element, we reset the iterator.
            // Stop at the first level which can provide a "next".
            // 
            for (int iter = _iterators.size() - 1; iter >= 0; --iter) {
                Iterator<? extends Object> currentLevelIterator = _iterators.get(iter);
                if (!currentLevelIterator.hasNext()) {
                    currentLevelIterator = _cartesian.get(iter).iterator();
                    _iterators.set(iter, currentLevelIterator);
                    _objects[iter] = currentLevelIterator.next();
                    continue; // <== No "next" element. Iterator reset. Go one step up. 
                }
                _objects[iter] = currentLevelIterator.next();
                break; // <== A "next" element available. leave. 
            }

            // Build the result sample, by assembling current value for each level.
            // A null value a some level means this level is just initialized (and therefore has not yet been dereferenced).
            // 
            final Object[] res = new Object[_cartesian.size()];
            for (int iter = 0; iter < _iterators.size(); ++iter) {
                Object object = _objects[iter];
                if (object == null) {
                    object = _iterators.get(iter).next();
                    _objects[iter] = object;
                }
                res[iter] = object;
            }

            return res; // <== 
        }

        @Override
        public int nextIndex() {
            throw new UnsupportedOperationException(); // <== 
        }

        @Override
        public Object[] previous() {
            throw new UnsupportedOperationException(); // <== 
        }

        @Override
        public int previousIndex() {
            throw new UnsupportedOperationException(); // <== 
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(); // <== 
        }

        @Override
        public void set(Object[] arg0) {
            throw new UnsupportedOperationException(); // <== 
        }

        private final Vector<Iterator<? extends Object>> _iterators;
        private final Object[] _objects;
    }
}
