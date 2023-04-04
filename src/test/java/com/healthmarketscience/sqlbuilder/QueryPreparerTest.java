/*
Copyright (c) 2008 Health Market Science, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.healthmarketscience.sqlbuilder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.healthmarketscience.sqlbuilder.Conditions.and;
import static com.healthmarketscience.sqlbuilder.Conditions.customCond;
import static com.healthmarketscience.sqlbuilder.Conditions.equalTo;
import static com.healthmarketscience.sqlbuilder.Conditions.isNotNull;
import static com.healthmarketscience.sqlbuilder.Conditions.isNull;
import static com.healthmarketscience.sqlbuilder.Conditions.lessThan;
import static com.healthmarketscience.sqlbuilder.Conditions.like;
import static com.healthmarketscience.sqlbuilder.Conditions.not;
import static com.healthmarketscience.sqlbuilder.Conditions.notEqualTo;
import static com.healthmarketscience.sqlbuilder.Conditions.notIn;
import static com.healthmarketscience.sqlbuilder.Conditions.or;

/**
 * @author james
 */
public class QueryPreparerTest extends BaseSqlTestCase {

    public QueryPreparerTest(String name) {
        super(name);
    }

    public void testPreparer() throws Exception {
        doTestPreparer(1);
        doTestPreparer(13);
    }

    private void doTestPreparer(int startIndex) throws Exception {
        Object obj1 = new Object();
        Object obj2 = new Object();
        Object obj3 = new Object();

        QueryPreparer prep = new QueryPreparer(startIndex);
        QueryPreparer.PlaceHolder ph1 = prep.getNewPlaceHolder();
        QueryPreparer.PlaceHolder ph2 = prep.getNewPlaceHolder();
        QueryPreparer.PlaceHolder ph3 = prep.getNewPlaceHolder();
        QueryPreparer.PlaceHolder sph1 = prep.addStaticPlaceHolder(42);
        QueryPreparer.PlaceHolder sph2 = prep.addStaticPlaceHolder("place");
        QueryPreparer.PlaceHolder sph3 = prep.addStaticPlaceHolder(13L);
        QueryPreparer.PlaceHolder sph4 = prep.addStaticPlaceHolder(true);
        QueryPreparer.PlaceHolder sph5 = prep.addStaticPlaceHolder((Long) null);
        QueryPreparer.PlaceHolder sph6 = prep.addStaticPlaceHolder(obj1);
        QueryPreparer.PlaceHolder sph7 = prep.addStaticPlaceHolder(
                obj2, Types.VARCHAR);
        QueryPreparer.MultiPlaceHolder mph1 = prep.getNewMultiPlaceHolder();
        QueryPreparer.ListPlaceHolder lph1 = prep.getNewListPlaceHolder()
                .addStaticLongs(1L, 2L, 3L, 4L, 5L);
        QueryPreparer.ListPlaceHolder lph2 = prep.getNewListPlaceHolder()
                .addPlaceHolders(3);
        QueryPreparer.ListPlaceHolder lph3 = prep.getNewListPlaceHolder()
                .addPlaceHolders(2);

        SelectQuery query = new SelectQuery()
                .addCustomColumns(sph2, sph3, sph4, sph5, sph6, sph7)
                .addCondition(
                        and(
                                lessThan(_table1_col1, ph1, false),
                                lessThan(_table1_col2, mph1, true),
                                isNotNull(_defTable1_col_id),
                                or(customCond("IM REALLY SNAZZY"),
                                   not(
                                           like(_defTable2_col5,
                                                ph2)),
                                   equalTo(new CustomSql("YOU"),
                                           sph1)),
                                notIn(_table1_col2, lph2),
                                or(
                                        isNull(_table1_col2),
                                        notEqualTo(mph1, mph1),
                                        new InCondition(_defTable1_col_id, lph1))));
        String queryStr = query.toString();
        checkResult(queryStr,
                    "SELECT ?,?,?,?,?,? FROM Schema1.Table1 t0,Table1 t1,Table2 t2 WHERE ((t0.col1 < ?) AND (t0.col2 <= ?) AND (t1.col_id IS NOT NULL) AND ((IM REALLY SNAZZY) OR (NOT (t2.col5 LIKE ?)) OR (YOU = ?)) AND (t0.col2 NOT IN (?,?,?) ) AND ((t0.col2 IS NULL) OR (? <> ?) OR (t1.col_id IN (?,?,?,?,?) )))");

        assertEquals((0 + startIndex), sph2.getIndex());
        assertEquals((1 + startIndex), sph3.getIndex());
        assertEquals((2 + startIndex), sph4.getIndex());
        assertEquals((3 + startIndex), sph5.getIndex());
        assertEquals((4 + startIndex), sph6.getIndex());
        assertEquals((5 + startIndex), sph7.getIndex());
        assertEquals((6 + startIndex), ph1.getIndex());
        assertEquals((8 + startIndex), ph2.getIndex());
        assertEquals(false, ph3.isInQuery());
        assertEquals((9 + startIndex), sph1.getIndex());
        checkIndexes(mph1.getIndexes(), (7 + startIndex), (13 + startIndex),
                     (14 + startIndex));
        checkIndexes(lph1.getIndexes(), (15 + startIndex), (16 + startIndex),
                     (17 + startIndex), (18 + startIndex), (19 + startIndex));
        checkIndexes(lph2.getIndexes(), (10 + startIndex), (11 + startIndex),
                     (12 + startIndex));
        assertEquals(false, lph3.isInQuery());

        MockPreparedStatement mockStmt = new MockPreparedStatement();
        PreparedStatement stmt = (PreparedStatement)
                Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                                       new Class<?>[]{PreparedStatement.class},
                                       mockStmt);

        prep.setStaticValues(stmt);
        ph1.setNull(Types.BLOB, stmt);
        ph2.setObject(obj3, stmt);
        ph3.setObject(new Object(), Types.DATE, stmt);
        mph1.setString("foo", stmt);
        lph2.setStrings(stmt, "lph1", null, "lph3");

        @SuppressWarnings("unchecked")
        List<List<Object>> expected = Arrays.asList(
                Arrays.<Object>asList("setInt", (9 + startIndex), 42),
                Arrays.<Object>asList("setString", (0 + startIndex), "place"),
                Arrays.<Object>asList("setLong", (1 + startIndex), 13L),
                Arrays.<Object>asList("setBoolean", (2 + startIndex), true),
                Arrays.<Object>asList("setNull", (3 + startIndex), Types.BIGINT),
                Arrays.<Object>asList("setObject", (4 + startIndex), obj1),
                Arrays.<Object>asList("setObject", (5 + startIndex), obj2,
                                      Types.VARCHAR),
                Arrays.<Object>asList("setLong", (15 + startIndex), 1L),
                Arrays.<Object>asList("setLong", (16 + startIndex), 2L),
                Arrays.<Object>asList("setLong", (17 + startIndex), 3L),
                Arrays.<Object>asList("setLong", (18 + startIndex), 4L),
                Arrays.<Object>asList("setLong", (19 + startIndex), 5L),
                Arrays.<Object>asList("setNull", (6 + startIndex), Types.BLOB),
                Arrays.<Object>asList("setObject", (8 + startIndex), obj3),
                Arrays.<Object>asList("setString", (7 + startIndex), "foo"),
                Arrays.<Object>asList("setString", (13 + startIndex), "foo"),
                Arrays.<Object>asList("setString", (14 + startIndex), "foo"),
                Arrays.<Object>asList("setString", (10 + startIndex), "lph1"),
                Arrays.<Object>asList("setNull", (11 + startIndex), Types.VARCHAR),
                Arrays.<Object>asList("setString", (12 + startIndex), "lph3")
        );

        assertEquals(expected, mockStmt._calls);

        try {
            query.toString();
            fail("IllegalStateException should have been thrown");
        } catch (IllegalStateException e) {
        }

    }

    private void checkIndexes(List<Integer> idxs, Integer... expectedIdxs) {
        assertEquals(Arrays.asList(expectedIdxs), idxs);
    }

    private static class MockPreparedStatement
            implements InvocationHandler {
        private final List<List<Object>> _calls = new ArrayList<List<Object>>();

        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            List<Object> call = new ArrayList<Object>();
            call.add(method.getName());
            call.addAll(Arrays.asList(args));
            _calls.add(call);
            return null;
        }
    }

}
