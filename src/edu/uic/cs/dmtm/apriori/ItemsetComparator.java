package edu.uic.cs.dmtm.apriori;

import java.util.Comparator;

public class ItemsetComparator implements Comparator<Itemset> {

	@Override
	public int compare(Itemset arg0, Itemset arg1) {
		return arg0.getMinMIS().compareTo(arg1.getMinMIS()) != 0 ?
				arg0.getMinMIS().compareTo(arg1.getMinMIS()) :
					arg0.toString().compareTo(arg1.toString());
	}

}
