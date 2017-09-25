package edu.uic.cs.dmtm.apriori;

/**
 * Signals an attempt to join two {@link Itemset}s of different size.
 * @see {@link #Itemset.isJoinable(Itemset)}
 * @author Marco Mele
 * @author Massimo Piras
 *
 */

public class DifferentItemsetSizeException extends Exception {

	private static final long serialVersionUID = 1L;

}
