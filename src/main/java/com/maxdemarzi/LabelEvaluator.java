package com.maxdemarzi;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

public class LabelEvaluator implements Evaluator {
    private Label label;

    public LabelEvaluator(Label label) {
        this.label = label;
    }

    @Override
    public Evaluation evaluate(Path path) {
        if (path.length() > 0) {
            Node lastNode = path.endNode();
            if (lastNode.hasLabel(label)) {
                return Evaluation.INCLUDE_AND_PRUNE;
            }
        }
        return Evaluation.EXCLUDE_AND_CONTINUE;
    }
}