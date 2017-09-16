package edu.uic.cs.dmtm.apriori;

import java.util.Comparator;

public class ItemComparator implements Comparator<Item> {

	@Override
	public int compare(Item arg0, Item arg1) {
		return arg0.getMis().compareTo(arg1.getMis()) != 0 ?
				arg0.getMis().compareTo(arg1.getMis()) :
				arg0.getId().compareTo(arg1.getId());
	}

}
