package edu.uic.cs.dmtm.apriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Provides a {@link #read(File, File, ArrayList, ArrayList, ArrayList, TreeMap)}
 * method to read input data and specifications
 * according to the required format. 
 *
 * @author Marco Mele
 * @author Massimo Piras
 */

public class InputReader {
	/**
	 * A reader that parses input files for the application
	 * to load formatted data and parameters.
	 * <ul>
	 * <li>parses a list of {@link Item}s with their {@code MIS} from a parameter file, and builds a {@link TreeMap} of all the Items contained in the file, indexed by their {@code id}
	 * <li>reads the value of the {@code SDC} parameter
	 * <li>parses a list of cannot_be_together and must_have rules filling two {@link ArrayList}s of {@link Item}s and {@link Itemset}s
	 * <li>reads and parses a list of transactions from a transaction base file and fills in an {@link ArrayList} of {@link Itemset}s
	 * All collection insertions follow the related collection native policies,
	 * plus the optionally given ordering rules.
	 *
	 * @param	inputFile A {@link File} object referring to the transaction base file
	 * reflecting given format rules.
	 * @param	parametersFile	A {@link File} object referring to a file of parameters and rules
	 * reflecting given format rules.
	 * @param transactions	An empty {@link ArrayList} of {@link Itemset}.
	 * @param cannotBeTogetherItemsets	An empty {@link ArrayList} of {@link Itemset}.
	 * @param mustHaveItems	An empty {@link ArrayList} of {@link Item}.
	 * @param items	An empty {@link TreeMap} of {@link Item}.
	 * @return the read value of {@code SDC}.
	 * @throws	IOException for system I/O errors or bad formatted lines.
	 */
	public double read(File inputFile,
			File parametersFile,
			ArrayList<Itemset> transactions,
			ArrayList<Itemset> cannotBeTogetherItemsets,
			ArrayList<Item> mustHaveItems,
			TreeMap<String, Item> items) throws IOException {
		
		BufferedReader inputBuffer = new BufferedReader(new FileReader(inputFile));
        BufferedReader parameterBuffer = new BufferedReader(new FileReader(parametersFile));
        
        String s;
        double SDC = 0.0;
        try {        
        	while((s = parameterBuffer.readLine()) != null) {
        		if(s.contains("MIS")) {
        			String id = s.split("\\(")[1].split("\\)")[0];
        			String mis = s.split("= ")[1];
        			items.put(id, new Item(id, Double.valueOf(mis)));
        		} else if(s.contains("SDC")) {
        			SDC = Double.valueOf(s.split("= ")[1]);
        		} else if(s.contains("cannot")) {
        			String listOfItemset = s.split(": ")[1];
        			String[] cannotBeTogether = listOfItemset.split("\\{");
        		
        			for(String string : cannotBeTogether) {
        				if(!string.isEmpty()) {
        					Itemset cbtItemset = new Itemset(null);
        					String[] ids = string.split("\\}")[0].split(", ");
        					for(String i : ids)
        						cbtItemset.getItemset().add(items.get(i));
        					cannotBeTogetherItemsets.add(cbtItemset);        				
        				}
        			}
        		} else if(s.contains("must")) {
        			String[] st = s.split(": ")[1].split(" [a-z ]*");
        			for(String stt : st)
        				mustHaveItems.add(items.get(stt));
        		} else
        			throw new IOException("Bad line format.");
        	}	
        
        	while((s = inputBuffer.readLine()) != null) {
        		String[] values = s.split("\\{|\\}")[1].split(", ");
        		Itemset temp = new Itemset(null);
        		for (String value : values) {
        			Item i = items.get(value.trim());
        			i.increaseSupportCount();
        			temp.addItem(i);
        		}
        		transactions.add(temp); 
        	}
        	int N = transactions.size();
        	items.values().stream().forEach(i -> i.setSupport((double) (i.getSupportCount()) / N));
        } finally {
        	inputBuffer.close();
        	parameterBuffer.close();
        }
        
        return SDC;
	}

}
