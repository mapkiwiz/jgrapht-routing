package fr.gouv.agriculture.geo;

import java.util.Comparator;

/**
 * Compares node by longitude (x) then latitude(y),
 * in ascending order.
 *
 */
public class LexicographicNodeComparator implements Comparator<Node> {

	public int compare(Node o1, Node o2) {

		if (o1.lon == o2.lon) {
			if (o1.lat > o2.lat) {
				return 1;
			} else if (o1.lat == o2.lat) {
				return 0;
			} else {
				return -1;
			}
		} else {
			if (o1.lon > o2.lon) {
				return 1;
			} else {
				return -1;
			}
		}

	}

}