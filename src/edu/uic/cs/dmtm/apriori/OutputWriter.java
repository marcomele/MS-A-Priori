package edu.uic.cs.dmtm.apriori;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Provides a {@link #write(File, ArrayList)} method to generate an output file of the
 * frequent itemsets according to the required format. 
 *
 * @author Marco Mele
 * @author Massimo Piras
 */

public class OutputWriter {
	
	/**
	 * Prints out the {@code frequentItemsets} on the {@code outputFile}
	 * with the required format. Frequent itemsets are printed with the
	 * items sorted as they are, comma separated, curly braces embraced.
	 * Itemsets are listed grouped by dimension, leaded by their support
	 * count; higher-than-1-level itemsets are followed by their tail
	 * count. 
	 *
	 * @param	outputFile	the File class referring the file to print on.
	 * @param	frequentItemsets	an ArrayList of TreeSets of Itemsets.
	 * @throws	IOException
	 */
	
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
