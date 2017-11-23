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
package org.teavm.classlib.java.util.stream;

import java.util.OptionalInt;
import java.util.PrimitiveIterator;
import java.util.function.BiConsumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public interface TIntStream extends TBaseStream<Integer, TIntStream> {
    interface Builder {
        void accept(int t);

        default Builder add(int t) {
            accept(t);
            return this;
        }

        TIntStream build();
    }

    TIntStream filter(IntPredicate predicate);

    TIntStream map(IntUnaryOperator mapper);

    <U> TStream<U> mapToObj(IntFunction<? extends U> mapper);

    TLongStream mapToLong(IntToLongFunction mapper);

    TDoubleStream mapToDouble(IntToDoubleFunction mapper);

    TIntStream flatMap(IntFunction<? extends TIntStream> mapper);

    TIntStream distinct();

    TIntStream sorted();

    TIntStream peek(IntConsumer action);

    TIntStream limit(long maxSize);

    TIntStream skip(long n);

    void forEach(IntConsumer action);

    void forEachOrdered(IntConsumer action);

    int[] toArray();

    int reduce(int identity, IntBinaryOperator accumulator);

    OptionalInt reduce(IntBinaryOperator op);

    <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator, BiConsumer<R, R> combiner);

    int sum();

    OptionalInt min();

    OptionalInt max();

    long count();

    boolean anyMatch(IntPredicate predicate);

    boolean allMatch(IntPredicate predicate);

    boolean noneMatch(IntPredicate predicate);

    OptionalInt findFirst();

    OptionalInt findAny();

    TStream<Integer> boxed();

    @Override
    PrimitiveIterator.OfInt iterator();

    static Builder builder() {
        return null;
    }

    static TIntStream empty() {
        return null;
    }

    static TIntStream of(int t) {
        return null;
    }

    static TIntStream of(int... values) {
        return null;
    }

    static TIntStream iterate(int seed, IntUnaryOperator f) {
        return null;
    }

    static TIntStream generate(IntSupplier s) {
        return null;
    }

    static IntStream concat(IntStream a, IntStream b) {
        return null;
    }
}
