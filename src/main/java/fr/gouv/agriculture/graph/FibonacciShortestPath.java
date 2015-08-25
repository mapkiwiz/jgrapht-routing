package fr.gouv.agriculture.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.traverse.ClosestFirstIterator;

public class FibonacciShortestPath {

	public static <V,E> double shortestPathLength(Graph<V, E> graph, V source, V target) {

		ClosestFirstIterator<V, E> iterator =
				new ClosestFirstIterator<V, E>(graph, source);

		while (iterator.hasNext()) {
			V next = iterator.next();
			if (next.equals(target)){
				break;
			}
		}

		return iterator.getShortestPathLength(target);

	}

	public static <V,E> Path<V> shortestPath(Graph<V,E> graph, V source, V target) {

		ClosestFirstIterator<V, E> forwardIterator =
				new ClosestFirstIterator<V, E>(graph, source);
		ClosestFirstIterator<V, E> reverseIterator =
				new ClosestFirstIterator<V, E>(graph, target);

		Set<V> forwardSet = new HashSet<V>();
		Set<V> reverseSet = new HashSet<V>();
		V middlePoint = null;

		while (forwardIterator.hasNext() && reverseIterator.hasNext()) {

			if (forwardIterator.hasNext()) {
				V next = forwardIterator.next();
				if (reverseSet.contains(next)) {
					middlePoint = next;
					break;
				} else {
					forwardSet.add(next);
				}
			}

			if (reverseIterator.hasNext()) {
				V next = reverseIterator.next();
				if (forwardSet.contains(next)) {
					middlePoint = next;
					break;
				} else {
					reverseSet.add(next);
				}
			}
		}

		if (middlePoint != null) {

			Path<V> path = getShortestPath(graph, forwardIterator, source, middlePoint);
			path.elements.remove(0);
			Collections.reverse(path.elements);
			Path<V> reversePath = getShortestPath(graph, reverseIterator, target, middlePoint);
			path.elements.addAll(reversePath.elements);

			return path;

		} else {

			return new Path<V>();

		}

	}

	private static <V,E> Path<V> getShortestPath(Graph<V,E> graph, ClosestFirstIterator<V, E> iterator, V source, V target) {

		Path<V> path = new Path<V>();

		V currentNode = target;
		path.elements.add(new PathElement<V>(currentNode, 0.0, 0.0));

		while (!currentNode.equals(source)) {
			E edge = iterator.getSpanningTreeEdge(currentNode);
			double weight = graph.getEdgeWeight(edge);
			double distance = weight;
			currentNode = getOtherNodeOfEdge(graph, edge, currentNode);
			path.elements.add(new PathElement<V>(currentNode, distance, weight));
		}

		return path;

	}

	public static <V,E> double bidirectionalShortestPathLength(Graph<V, E> graph, V source, V target) {

		ClosestFirstIterator<V, E> forwardIterator =
				new ClosestFirstIterator<V, E>(graph, source);
		ClosestFirstIterator<V, E> reverseIterator =
				new ClosestFirstIterator<V, E>(graph, target);

		Set<V> forwardSet = new HashSet<V>();
		Set<V> reverseSet = new HashSet<V>();
		V middlePoint = null;

		while (forwardIterator.hasNext() && reverseIterator.hasNext()) {

			if (forwardIterator.hasNext()) {
				V next = forwardIterator.next();
				if (reverseSet.contains(next)) {
					middlePoint = next;
					break;
				} else {
					forwardSet.add(next);
				}
			}

			if (reverseIterator.hasNext()) {
				V next = reverseIterator.next();
				if (forwardSet.contains(next)) {
					middlePoint = next;
					break;
				} else {
					reverseSet.add(next);
				}
			}
		}

		if (middlePoint != null) {
			return forwardIterator.getShortestPathLength(middlePoint) + reverseIterator.getShortestPathLength(middlePoint);
		} else {
			return 0.0;
		}
	}

	public static <V,E> List<V> getAdjacentNodes(Graph<V, E> graph, V node) {

		List<V> nodes = new ArrayList<V>();
		for (E edge : graph.edgesOf(node)) {
			V other = getOtherNodeOfEdge(graph, edge, node);
			nodes.add(other);
		}

		return nodes;

	}

	public static <V,E> V getOtherNodeOfEdge(Graph<V,E> graph, E edge, V node) {

		V source = graph.getEdgeSource(edge);
		V target = graph.getEdgeTarget(edge);

		if (source.equals(node)) {
			return target;
		} else {
			assert(target.equals(node));
			return source;
		}

	}

	public static <V, E> List<V> searchByDistance(Graph<V, E> graph, V source, double distance) {

		ClosestFirstIterator<V, E> iterator =
				new ClosestFirstIterator<V, E>(graph, source);
		List<V> results = new ArrayList<V>();

		while (iterator.hasNext()) {

			V currentNode = iterator.next();
			double currentDistance = iterator.getShortestPathLength(currentNode);

			if (currentDistance > distance) {
				break;
			}

			results.add(currentNode);

		}

		return results;

	}

}
