package edu.uic.cs.dmtm.apriori;

import java.util.Comparator;

/**
 * Implements a {@link Comparator} for {@link Item}s
 * based on their {@code MIS} (ascending), then lexicographically.
 * @author Marco Mele
 * @author Massimo Piras.
 *
 */

public class ItemComparator implements Comparator<Item> {

	/**
	 * Compares two {@link Item}s based on their {@code MIS} (ascending), then lexicographically.
	 */
	@Override
	public int compare(Item arg0, Item arg1) {
		return arg0.getMis().compareTo(arg1.getMis()) != 0 ?
				arg0.getMis().compareTo(arg1.getMis()) :
				arg0.getId().compareTo(arg1.getId());
	}

}
