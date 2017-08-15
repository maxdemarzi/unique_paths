package com.maxdemarzi;

import com.maxdemarzi.results.NodeResult;
import com.maxdemarzi.schema.Labels;
import com.maxdemarzi.schema.RelationshipTypes;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;

import java.io.IOException;
import java.util.stream.Stream;


public class Unique {

    // This evaluator only accepts paths that end in a Product node.
    private static final LabelEvaluator labelEvaluator = new LabelEvaluator(Labels.PRODUCT);

    // This expander only traverses [:NEEDS]->
    private static final PathExpander pathExpander = PathExpanderBuilder
            .empty()
            .add(RelationshipTypes.NEEDS, Direction.OUTGOING).build();

    // This field declares that we need a GraphDatabaseService
    // as context when any procedure in this class is invoked
    @Context
    public GraphDatabaseService db;
    // This gives us a log instance that outputs messages to the
    // standard log, normally found under `data/log/console.log`
    @Context
    public Log log;

    @Procedure(name = "com.maxdemarzi.unique", mode = Mode.READ)
    @Description("CALL com.maxdemarzi.unique(id) - find unique products")
    public Stream<NodeResult> unique(@Name("id") String id) throws IOException {
        // We start by finding the starting node
        Node node = db.findNode(Labels.PRODUCT, "id", id);
        if (node != null) {
            // Then build our traversal
            TraversalDescription td = db.traversalDescription()
                    .breadthFirst()
                    .evaluator(labelEvaluator)
                    .expand(pathExpander)
                    .uniqueness(Uniqueness.NODE_GLOBAL);

            // Finally we return just the distinct end nodes of our paths
            return td.traverse(node).iterator().stream().map(Path::endNode).distinct().map(NodeResult::new);
        }
        return null;
    }
}
