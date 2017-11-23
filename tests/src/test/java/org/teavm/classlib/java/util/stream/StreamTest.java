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

import static org.junit.Assert.assertEquals;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;

@RunWith(TeaVMTestRunner.class)
public class StreamTest {
    @Test
    public void forEachWorks() {
        StringBuilder sb = new StringBuilder();
        Stream.of(1, 2, 3).forEach(appendNumbersTo(sb));
        assertEquals("1;2;3;", sb.toString());
    }

    @Test
    public void mapWorks() {
        StringBuilder sb = new StringBuilder();
        Stream.of(1, 2, 3).map(n -> n * n).forEach(appendNumbersTo(sb));
        assertEquals("1;4;9;", sb.toString());
    }

    @Test
    public void filterWorks() {
        StringBuilder sb = new StringBuilder();
        Stream.of(1, 2, 3, 4, 5, 6).filter(n -> (n & 1) == 0).forEach(appendNumbersTo(sb));
        assertEquals("2;4;6;", sb.toString());
    }

    @Test
    public void flatMapWorks() {
        StringBuilder sb = new StringBuilder();
        Stream.of(Stream.of(1, 2), Stream.of(3, 4)).flatMap(n -> n).forEach(appendNumbersTo(sb));
        assertEquals("1;2;3;4;", sb.toString());

        sb.setLength(0);
        Stream.of(Stream.of(1, 2), Stream.of(3, 4)).flatMap(n -> n).skip(1).forEach(appendNumbersTo(sb));
        assertEquals("2;3;4;", sb.toString());

        sb.setLength(0);
        Stream.of(Stream.of(1, 2), Stream.of(3, 4, 5)).flatMap(n -> n).skip(3).forEach(appendNumbersTo(sb));
        assertEquals("4;5;", sb.toString());
    }

    @Test
    public void skipWorks() {
        for (int i = 0; i <= 6; ++i) {
            StringBuilder sb = new StringBuilder();
            Stream.iterate(1, n -> n + 1).limit(5).skip(i).forEach(appendNumbersTo(sb));

            StringBuilder expected = new StringBuilder();
            for (int j = i; j < 5; ++j) {
                expected.append(j + 1).append(';');
            }
            assertEquals("Error skipping " + i + " elements", expected.toString(), sb.toString());
        }
    }

    @Test
    public void limitWorks() {
        for (int i = 0; i <= 3; ++i) {
            StringBuilder sb = new StringBuilder();
            Stream.iterate(1, n -> n + 1).limit(i).forEach(appendNumbersTo(sb));

            StringBuilder expected = new StringBuilder();
            for (int j = 0; j < i; ++j) {
                expected.append(j + 1).append(';');
            }
            assertEquals("Error limiting to " + i + " elements", expected.toString(), sb.toString());
        }
    }

    private Consumer<Integer> appendNumbersTo(StringBuilder sb) {
        return n -> sb.append(n).append(';');
    }
}
