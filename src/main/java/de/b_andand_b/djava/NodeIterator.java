package de.b_andand_b.djava;

import com.github.javaparser.ast.Node;

public class NodeIterator {
    public interface NodeHandler {
        boolean handle(Node node);
    }
    private final NodeHandler nodeHandler;
    public NodeIterator(NodeHandler nodeHandler) {
        this.nodeHandler = nodeHandler;
    }
    public void explore(Node node) {
        if (nodeHandler.handle(node)) {
            for (Node child : node.getChildNodes()) {
                explore(child);
            }
        }
    }
}