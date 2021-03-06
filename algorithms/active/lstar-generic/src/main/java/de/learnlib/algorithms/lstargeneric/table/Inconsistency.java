/* Copyright (C) 2013-2017 TU Dortmund
 * This file is part of LearnLib, http://www.learnlib.de/.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.learnlib.algorithms.lstargeneric.table;

/**
 * A description of an inconsistency in an {@link ObservationTable}. An inconsistency consists of two short prefixes
 * <code>u</code>, <code>u'</code> with identical contents, and an input symbol <code>a</code>, such that the rows for
 * <code>ua</code> and <code>u'a</code> have different contents.
 *
 * @param <I>
 *         input symbol class
 * @param <D>
 *         output class
 *
 * @author Malte Isberner
 */
public final class Inconsistency<I, D> {

    private final Row<I> firstRow;
    private final Row<I> secondRow;
    private final int inputIndex;

    /**
     * Constructor.
     *
     * @param firstRow
     *         the first row
     * @param secondRow
     *         the second row
     * @param inputIndex
     *         the input symbol for which the successor rows differ
     */
    public Inconsistency(Row<I> firstRow, Row<I> secondRow, int inputIndex) {
        this.firstRow = firstRow;
        this.secondRow = secondRow;
        this.inputIndex = inputIndex;
    }

    /**
     * Retrieves the first row.
     *
     * @return the first row
     */
    public Row<I> getFirstRow() {
        return firstRow;
    }

    /**
     * Retrieves the second row.
     *
     * @return the second row
     */
    public Row<I> getSecondRow() {
        return secondRow;
    }

    /**
     * Retrieves the index of the input symbol for which the successor rows differ.
     *
     * @return the input symbol index
     */
    public int getInputIndex() {
        return inputIndex;
    }
}
