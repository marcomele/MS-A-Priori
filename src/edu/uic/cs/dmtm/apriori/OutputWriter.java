package edu.uic.cs.dmtm.apriori;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class OutputWriter {
	
	public void write(File outputFile,
			ArrayList<TreeSet<Itemset>> frequentItemsets) throws IOException {
		
		BufferedWriter outputBuffer = new BufferedWriter(new FileWriter(outputFile));
		
		int level = 1;
		for(TreeSet<Itemset> treeSet : frequentItemsets) {
			outputBuffer.write("Frequent " + level + "-itemsets");
			outputBuffer.newLine();
			for(Itemset itemset : treeSet) {
				outputBuffer.write("\t" + itemset.getSupportCount() + " : {");
				outputBuffer.write(itemset.getItemset()
										.stream()
										.map(item -> item.toString())
										.collect(Collectors.joining(", ")));	
				outputBuffer.write("}");
				outputBuffer.newLine();
				if(level > 1) {
					outputBuffer.write("Tailcount = " + itemset.getTailCount());
					outputBuffer.newLine();
				}
			}
			outputBuffer.write("\tTotal number of frequent " + level + "-itemsets = " + treeSet.size());
			outputBuffer.newLine();
			outputBuffer.newLine();
			outputBuffer.newLine();
			level ++;
		}
		
		outputBuffer.close();
		
	}
	
}
