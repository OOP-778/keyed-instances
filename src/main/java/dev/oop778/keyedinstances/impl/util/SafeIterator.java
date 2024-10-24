package dev.oop778.keyedinstances.impl.util;

import lombok.RequiredArgsConstructor;

import java.util.Iterator;

@RequiredArgsConstructor
public class SafeIterator<T> implements Iterator<T> {
    private final Iterator<T> iterator;

    protected T current;
    private boolean advanced;

    @Override
    public boolean hasNext() {
        if (!this.advanced) {
            this.advance();
            this.advanced = true;
        }

        return this.current != null;
    }

    @Override
    public T next() {
        if (!this.advanced) {
            this.advance();
        }

        this.advanced = false;
        return this.current;
    }

    private void advance() {
        this.current = null;
        while (this.iterator.hasNext()) {
            final T next = this.iterator.next();
            if (this.validate(next)) {
                this.current = next;
                break;
            }
        }
    }

    protected boolean validate(T value) {
        return value != null;
    }
}
