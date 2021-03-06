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
package de.learnlib.algorithms.baselinelstar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import de.learnlib.datastructure.observationtable.ObservationTable;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

/**
 * The internal storage mechanism for {@link BaselineLStar}.
 *
 * @param <I>
 *         input symbol class.
 */
public class BaselineObservationTable<I> implements ObservationTable<I, Boolean>, Serializable {

    @Nonnull
    private final List<ObservationTableRow<I>> shortPrefixRows; // S

    @Nonnull
    private final List<ObservationTableRow<I>> longPrefixRows; // SA

    @Nonnull
    private final List<Word<I>> suffixes; // E

    public BaselineObservationTable() {
        Word<I> emptyWord = Word.epsilon();

        suffixes = new ArrayList<>();
        suffixes.add(emptyWord);

        Word<I> epsiplon = Word.epsilon();
        ObservationTableRow<I> initialRow = new ObservationTableRow<>(epsiplon);
        initialRow.setShortPrefixRow();
        shortPrefixRows = new ArrayList<>();
        shortPrefixRows.add(initialRow);

        longPrefixRows = new ArrayList<>();
    }

    @Override
    public Collection<ObservationTableRow<I>> getShortPrefixRows() {
        return Collections.unmodifiableCollection(shortPrefixRows);
    }

    @Override
    public Collection<ObservationTableRow<I>> getLongPrefixRows() {
        return Collections.unmodifiableCollection(longPrefixRows);
    }

    @Override
    @Nullable
    public Row<I, Boolean> getSuccessorRow(@Nonnull Row<I, Boolean> spRow, I symbol) {
        //noinspection SuspiciousMethodCalls
        if (!shortPrefixRows.contains(spRow)) {
            throw new IllegalArgumentException("Row '" + spRow + "' is not part of short prefix rows!");
        }

        Word<I> successorLabel = spRow.getLabel().append(symbol);

        Row<I, Boolean> successor = null;

        for (Row<I, Boolean> row : getAllRows()) {
            if (row.getLabel().equals(successorLabel)) {
                successor = row;
                break;
            }
        }

        return successor;
    }

    /**
     * The set of suffixes in the observation table, often called "E".
     *
     * @return The set of candidates.
     */
    @Override
    public List<Word<I>> getSuffixes() {
        return Collections.unmodifiableList(suffixes);
    }

    void addSuffix(Word<I> suffix) {
        suffixes.add(suffix);
    }

    void addShortPrefix(@Nonnull Word<I> shortPrefix) {
        final ObservationTableRow<I> row = new ObservationTableRow<>(shortPrefix);
        row.setShortPrefixRow();
        shortPrefixRows.add(row);
    }

    void addLongPrefix(@Nonnull Word<I> longPrefix) {
        final ObservationTableRow<I> row = new ObservationTableRow<>(longPrefix);
        row.setLongPrefixRow();
        longPrefixRows.add(row);
    }

    void removeShortPrefixesFromLongPrefixes() {
        List<Word<I>> longPrefixLabels = getLongPrefixLabels();
        longPrefixLabels.retainAll(getShortPrefixLabels());

        List<ObservationTableRow<I>> rowsToRemove = Lists.newArrayListWithCapacity(longPrefixLabels.size());

        for (ObservationTableRow<I> row : longPrefixRows) {
            if (longPrefixLabels.contains(row.getLabel())) {
                rowsToRemove.add(row);
            }
        }

        longPrefixRows.removeAll(rowsToRemove);
    }

    @Nonnull
    public List<Word<I>> getLongPrefixLabels() {
        List<Word<I>> labels = Lists.newArrayListWithExpectedSize(longPrefixRows.size());
        for (ObservationTableRow<I> row : longPrefixRows) {
            labels.add(row.getLabel());
        }
        return labels;
    }

    @Nonnull
    public List<Word<I>> getShortPrefixLabels() {
        List<Word<I>> labels = Lists.newArrayListWithExpectedSize(shortPrefixRows.size());
        for (ObservationTableRow<I> row : shortPrefixRows) {
            labels.add(row.getLabel());
        }
        return labels;
    }

    /**
     * Adds the result of a membership query to this table.
     *
     * @param prefix
     *         The prefix of the {@link Word} asked with the membership query.
     * @param suffix
     *         The prefix of the {@link Word} asked with the membership query.
     * @param result
     *         The result of the query.
     */
    void addResult(@Nonnull Word<I> prefix, @Nonnull Word<I> suffix, @Nonnull Boolean result) {
        if (!suffixes.contains(suffix)) {
            throw new IllegalArgumentException("Suffix '" + suffix + "' is not part of the suffixes set");
        }

        final int suffixPosition = suffixes.indexOf(suffix);
        ObservationTableRow<I> row = getRowForPrefix(prefix);

        addResultToRow(result, suffixPosition, row);
    }

    @Nonnull
    ObservationTableRow<I> getRowForPrefix(@Nonnull Word<I> state) {
        for (ObservationTableRow<I> row : shortPrefixRows) {
            if (row.getLabel().equals(state)) {
                return row;
            }
        }

        for (ObservationTableRow<I> row : longPrefixRows) {
            if (row.getLabel().equals(state)) {
                return row;
            }
        }

        throw new IllegalArgumentException("Unable to find a row for '" + state + "'");
    }

    private void addResultToRow(@Nonnull Boolean result, int suffixPosition, @Nonnull ObservationTableRow<I> row) {
        final List<Boolean> values = row.getContents();
        if (values.size() > suffixPosition) {
            if (!values.get(suffixPosition).equals(result)) {
                throw new IllegalStateException(
                        "New result " + values.get(suffixPosition) + " differs from old result " + result);
            }
        } else {
            row.addValue(result);
        }
    }

    /**
     * Determines the next state for which the observation table needs to be closed.
     *
     * @return The next state for which the observation table needs to be closed. If the table is closed, this returns
     * {@code null}.
     */
    @Nullable
    Word<I> findUnclosedState() {
        for (ObservationTableRow<I> candidate : longPrefixRows) {
            boolean found = false;

            for (ObservationTableRow<I> stateRow : shortPrefixRows) {
                if (candidate.isContentsEqual(stateRow)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                return candidate.getLabel();
            }
        }

        return null;
    }

    /**
     * @param alphabet
     *         The {@link Alphabet} for which the consistency is checked
     *
     * @return if the observation table is consistent with the given alphabet.
     */
    boolean isConsistentWithAlphabet(@Nonnull Alphabet<I> alphabet) {
        return findInconsistentSymbol(alphabet) == null;
    }

    @Nullable
    InconsistencyDataHolder<I> findInconsistentSymbol(@Nonnull Alphabet<I> alphabet) {
        for (I symbol : alphabet) {
            for (int firstStateCounter = 0; firstStateCounter < shortPrefixRows.size(); firstStateCounter++) {
                ObservationTableRow<I> firstRow = shortPrefixRows.get(firstStateCounter);

                for (int secondStateCounter = firstStateCounter + 1;
                     secondStateCounter < shortPrefixRows.size();
                     secondStateCounter++) {
                    ObservationTableRow<I> secondRow = shortPrefixRows.get(secondStateCounter);

                    if (checkInconsistency(firstRow, secondRow, symbol)) {
                        return new InconsistencyDataHolder<>(firstRow, secondRow, symbol);
                    }
                }
            }
        }

        return null;
    }

    private boolean checkInconsistency(@Nonnull ObservationTableRow<I> firstRow,
                                       @Nonnull ObservationTableRow<I> secondRow,
                                       @Nonnull I alphabetSymbol) {

        if (!firstRow.isContentsEqual(secondRow)) {
            return false;
        }

        Word<I> extendedFirstState = firstRow.getLabel().append(alphabetSymbol);
        Word<I> extendedSecondState = secondRow.getLabel().append(alphabetSymbol);
        ObservationTableRow<I> rowForExtendedFirstState = getRowForPrefix(extendedFirstState);
        ObservationTableRow<I> rowForExtendedSecondState = getRowForPrefix(extendedSecondState);

        return !rowForExtendedFirstState.isContentsEqual(rowForExtendedSecondState);
    }

    @Nonnull
    Word<I> determineWitnessForInconsistency(@Nonnull InconsistencyDataHolder<I> dataHolder) {
        Word<I> firstState = dataHolder.getFirstState().append(dataHolder.getDifferingSymbol());
        Word<I> secondState = dataHolder.getSecondState().append(dataHolder.getDifferingSymbol());

        ObservationTableRow<I> firstRow = getRowForPrefix(firstState);
        ObservationTableRow<I> secondRow = getRowForPrefix(secondState);

        final List<Boolean> firstRowContents = firstRow.getContents();
        final List<Boolean> secondRowContents = secondRow.getContents();

        for (int i = 0; i < firstRow.getContents().size(); i++) {
            Boolean symbolFirstRow = firstRowContents.get(i);
            Boolean symbolSecondRow = secondRowContents.get(i);
            if (!symbolFirstRow.equals(symbolSecondRow)) {
                return suffixes.get(i);
            }
        }

        throw new IllegalStateException("Both rows are identical, unable to determine a witness!");
    }

    /**
     * Moves a single row from long prefix rows to short prefix rows.
     *
     * @param longPrefix
     *         A row which must be part of the long prefix rows and should be moved to the short prefix rows.
     */
    void moveLongPrefixToShortPrefixes(@Nonnull Word<I> longPrefix) {
        ObservationTableRow<I> rowToMove = null;

        for (ObservationTableRow<I> row : longPrefixRows) {
            if (row.getLabel().equals(longPrefix)) {
                rowToMove = row;
                break;
            }
        }

        if (rowToMove == null) {
            throw new IllegalArgumentException("Word '" + longPrefix + "' not part of long prefixes");
        }

        longPrefixRows.remove(rowToMove);
        rowToMove.setShortPrefixRow();
        shortPrefixRows.add(rowToMove);
    }
}
