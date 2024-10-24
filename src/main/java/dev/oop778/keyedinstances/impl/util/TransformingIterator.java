package dev.oop778.keyedinstances.impl.util;

import java.util.Iterator;
import java.util.function.Function;

public class TransformingIterator<IN, OUT> implements Iterator<OUT> {
    private final Function<IN, OUT> transformer;
    private final Iterator<IN> iterator;

    public TransformingIterator(Function<IN, OUT> transformer, Iterator<IN> iterator) {
        this.transformer = transformer;
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override
    public OUT next() {
        return this.transformer.apply(this.iterator.next());
    }

    @Override
    public void remove() {
        this.iterator.remove();
    }
}
