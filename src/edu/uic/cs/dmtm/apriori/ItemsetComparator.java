package edu.uic.cs.dmtm.apriori;

import java.util.Comparator;

/**
 * Implements a {@link Comparator} for {@link Itemset}s
 * based on the minimum {@code MIS} of their {@link Item}s (ascending),
 * then lexicographically.
 * @author Marco Mele
 * @author Massimo Piras
 *
 */

public class ItemsetComparator implements Comparator<Itemset> {

	/**
	 * Compares two {@link Itemset}s based on the minimum {@code MIS} of their {@link Item}s (ascending),
	 * then lexicographically.
	 */
	@Override
	public int compare(Itemset arg0, Itemset arg1) {
		return arg0.getMinMIS().compareTo(arg1.getMinMIS()) != 0 ?
				arg0.getMinMIS().compareTo(arg1.getMinMIS()) :
					arg0.toString().compareTo(arg1.toString());
	}

}
