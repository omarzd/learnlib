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
package de.learnlib.oracle.membership;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;

import de.learnlib.api.oracle.MembershipOracle;
import de.learnlib.api.oracle.QueryAnswerer;
import de.learnlib.api.query.Query;
import de.learnlib.util.MQUtil;

@ParametersAreNonnullByDefault
public class QueryAnswererOracle<I, D> implements MembershipOracle<I, D> {

    private final QueryAnswerer<I, D> answerer;

    public QueryAnswererOracle(QueryAnswerer<I, D> answerer) {
        this.answerer = answerer;
    }

    @Override
    public void processQueries(Collection<? extends Query<I, D>> queries) {
        MQUtil.answerQueries(answerer, queries);
    }

}
