package com.maxdemarzi;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.harness.junit.Neo4jRule;
import org.neo4j.test.server.HTTP;

import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static junit.framework.TestCase.assertEquals;

public class UniqueTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Rule
    public final Neo4jRule neo4j = new Neo4jRule()
            .withFixture(MODEL_STATEMENT)
            .withProcedure(Unique.class);

    @Test
    public void testUnique() throws Exception {
        HTTP.Response response = HTTP.POST(neo4j.httpURI().resolve("/db/data/transaction/commit").toString(), QUERY1);
        int count = response.get("results").get(0).get("data").size();
        assertEquals(4, count);
    }

    private static final Map QUERY1 =
            singletonMap("statements", singletonList(singletonMap("statement",
                    "CALL com.maxdemarzi.unique('p1') yield node return node")));

    private static final String MODEL_STATEMENT =
            "CREATE (p1:PRODUCT {id:'p1'})" +
            "CREATE (p2:PRODUCT {id:'p2'})" +
            "CREATE (p3:PRODUCT {id:'p3'})" +
            "CREATE (p4:PRODUCT {id:'p4'})" +
            "CREATE (p5:PRODUCT {id:'p5'})" +
            "CREATE (np1:NotProduct {id:'np1'})" +
            "CREATE (np2:NotProduct {id:'np2'})" +
            "CREATE (np3:NotProduct {id:'np3'})" +
            "CREATE (np4:NotProduct {id:'np4'})" +
            "CREATE (np5:NotProduct {id:'np5'})" +
            "CREATE (np6:NotProduct {id:'np6'})" +
                    "CREATE (p1)-[:NEEDS]->(p2)" +
                    "CREATE (p1)-[:NEEDS]->(np1)" +
                    "CREATE (np1)-[:NEEDS]->(np2)" +
                    "CREATE (np2)-[:NEEDS]->(p3)" +
                    "CREATE (np2)-[:NEEDS]->(np3)" +
                    "CREATE (np3)-[:NEEDS]->(p4)" +
                    "CREATE (np3)-[:NEEDS]->(np4)" +
                    "CREATE (np4)-[:NEEDS]->(np5)" +
                    "CREATE (np5)-[:NEEDS]->(np6)" +
                    "CREATE (np6)-[:NEEDS]->(p5)";

}
