package com.github.mapkiwiz.geo;

public interface CrossProduct<V> {
	
	/**
	 * Ternary cross-product operator.
	 * 
	 * Given three points O (this vertex), A (vertex a), B (vertex b),
	 * returns the cross product of vectors OA and OB.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public double cross(V a, V b);

}
