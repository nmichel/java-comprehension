import java.util.AbstractList;
import java.util.RandomAccess;

public class RandomAccessComprehension extends AbstractList<Object[]> implements RandomAccess {

    public RandomAccessComprehension(final AbstractList<AbstractList<? extends Object>> cartesian) {
        _cartesian = cartesian;

        int sampleCount = 1;
        for (int i = 0; i < cartesian.size(); ++i) {
            sampleCount *= cartesian.get(i).size();
        }
        _size = sampleCount;

        _dimensionSize = new int[cartesian.size()];
        _dimensionSize[0] = 1;
        for (int i = 1; i < cartesian.size(); ++i) {
            _dimensionSize[i] = _dimensionSize[i - 1] * cartesian.get(i - 1).size();
        }
    }

    @Override
    public Object[] get(final int pos) {
        if (pos < 0 || pos >= size()) {
            throw new IndexOutOfBoundsException(); // <== 
        }

        final int[] indices = new int[_cartesian.size()];
        int acc = pos;
        for (int j = _cartesian.size() - 1; j > 0; --j) {
            indices[j] = acc / _dimensionSize[j];
            acc = acc % _dimensionSize[j];
        }
        indices[0] = acc;

        final Object[] sample = new Object[_cartesian.size()];
        for (int j = 0; j < indices.length; ++j) {
            sample[j] = _cartesian.get(j).get(indices[j]);
        }

        return sample;
    }

    @Override
    public int size() {
        return _size; // <== 
    }

    final AbstractList<AbstractList<? extends Object>> _cartesian;
    private final int _size;
    private final int[] _dimensionSize;

}
