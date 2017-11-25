/*
 *  Copyright 2017 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.util.stream.longimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalLong;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;
import org.teavm.classlib.java.util.stream.TDoubleStream;
import org.teavm.classlib.java.util.stream.TIntStream;
import org.teavm.classlib.java.util.stream.TLongStream;
import org.teavm.classlib.java.util.stream.TStream;

public abstract class TSimpleLongStreamImpl implements TLongStream {
    @Override
    public TLongStream filter(LongPredicate predicate) {
        return new TFilteringLongStreamImpl(this, predicate);
    }

    @Override
    public TLongStream map(LongUnaryOperator mapper) {
        return new TMappingLongStreamImpl(this, mapper);
    }

    @Override
    public <U> TStream<U> mapToObj(LongFunction<? extends U> mapper) {
        return new TMappingToObjStreamImpl<>(this, mapper);
    }

    @Override
    public TIntStream mapToInt(LongToIntFunction mapper) {
        return new TMappingToIntStreamImpl(this, mapper);
    }

    @Override
    public TDoubleStream mapToDouble(LongToDoubleFunction mapper) {
        return null;
    }

    @Override
    public TLongStream flatMap(LongFunction<? extends TLongStream> mapper) {
        return new TFlatMappingLongStreamImpl(this, mapper);
    }

    @Override
    public TLongStream distinct() {
        return new TDistinctLongStreamImpl(this);
    }

    @Override
    public TLongStream sorted() {
        long[] array = toArray();
        Arrays.sort(array);
        return TLongStream.of(array);
    }

    @Override
    public TLongStream peek(LongConsumer action) {
        return new TPeekingLongStreamImpl(this, action);
    }

    @Override
    public TLongStream limit(long maxSize) {
        return new TLimitingLongStreamImpl(this, (int) maxSize);
    }

    @Override
    public TLongStream skip(long n) {
        return new TSkippingLongStreamImpl(this, (int) n);
    }

    @Override
    public void forEach(LongConsumer action) {
        forEachOrdered(action);
    }

    @Override
    public void forEachOrdered(LongConsumer action) {
        next(e -> {
            action.accept(e);
            return true;
        });
    }

    @Override
    public long[] toArray() {
        int estimatedSize = estimateSize();
        if (estimatedSize < 0) {
            List<Long> list = new ArrayList<>();
            next(list::add);
            long[] array = new long[list.size()];
            for (int i = 0; i < array.length; ++i) {
                array[i] = list.get(i);
            }
            return array;
        } else {
            long[] array = new long[estimatedSize];
            ArrayFillingConsumer consumer = new ArrayFillingConsumer(array);
            boolean wantsMore = next(consumer);
            assert !wantsMore : "next() should have reported done status";
            if (consumer.index < array.length) {
                array = Arrays.copyOf(array, consumer.index);
            }
            return array;
        }
    }

    @Override
    public long reduce(long identity, LongBinaryOperator accumulator) {
        TReducingLongConsumer consumer = new TReducingLongConsumer(accumulator, identity, true);
        boolean wantsMore = next(consumer);
        assert !wantsMore : "next() should have returned true";
        return consumer.result;
    }

    @Override
    public OptionalLong reduce(LongBinaryOperator accumulator) {
        TReducingLongConsumer consumer = new TReducingLongConsumer(accumulator, 0, false);
        boolean wantsMore = next(consumer);
        assert !wantsMore : "next() should have returned true";
        return consumer.initialized ? OptionalLong.of(consumer.result) : OptionalLong.empty();
    }

    @Override
    public <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> accumulator, BiConsumer<R, R> combiner) {
        R collection = supplier.get();
        next(e -> {
            accumulator.accept(collection, e);
            return true;
        });
        return collection;
    }

    @Override
    public OptionalLong min() {
        return reduce(Math::min);
    }

    @Override
    public OptionalLong max() {
        return reduce(Math::max);
    }

    @Override
    public long count() {
        TCountingLongConsumer consumer = new TCountingLongConsumer();
        next(consumer);
        return consumer.count;
    }

    @Override
    public long sum() {
        TSumLongConsumer consumer = new TSumLongConsumer();
        next(consumer);
        return consumer.sum;
    }

    @Override
    public boolean anyMatch(LongPredicate predicate) {
        return next(predicate.negate());
    }

    @Override
    public boolean allMatch(LongPredicate predicate) {
        return !next(predicate);
    }

    @Override
    public boolean noneMatch(LongPredicate predicate) {
        return !anyMatch(predicate);
    }

    @Override
    public OptionalLong findFirst() {
        TFindFirstLongConsumer consumer = new TFindFirstLongConsumer();
        next(consumer);
        return consumer.hasAny ? OptionalLong.of(consumer.result) : OptionalLong.empty();
    }

    @Override
    public OptionalLong findAny() {
        return findFirst();
    }

    @Override
    public PrimitiveIterator.OfLong iterator() {
        return new TSimpleLongStreamIterator(this);
    }

    @Override
    public Spliterator.OfLong spliterator() {
        return null;
    }

    @Override
    public TStream<Long> boxed() {
        return new TBoxedLongStream(this);
    }

    @Override
    public boolean isParallel() {
        return false;
    }

    @Override
    public TLongStream sequential() {
        return this;
    }

    @Override
    public TLongStream parallel() {
        return this;
    }

    @Override
    public TLongStream unordered() {
        return this;
    }

    @Override
    public TLongStream onClose(Runnable closeHandler) {
        return new TCloseHandlingLongStream(this, closeHandler);
    }

    @Override
    public void close() throws Exception {
    }

    protected int estimateSize() {
        return -1;
    }

    public abstract boolean next(LongPredicate consumer);

    class ArrayFillingConsumer implements LongPredicate {
        long[] array;
        int index;

        ArrayFillingConsumer(long[] array) {
            this.array = array;
        }

        @Override
        public boolean test(long t) {
            array[index++] = t;
            return true;
        }
    }
}