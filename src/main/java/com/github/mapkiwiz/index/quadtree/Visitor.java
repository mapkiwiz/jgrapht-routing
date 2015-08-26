package com.github.mapkiwiz.index.quadtree;

public interface Visitor<T> {
    public void visit(QuadTree<T> quadTree, Node<T> node);
}
