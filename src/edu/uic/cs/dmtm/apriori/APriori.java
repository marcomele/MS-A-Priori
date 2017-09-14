package edu.uic.cs.dmtm.apriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

public class APriori {

	public static void main(String[] args) throws IOException {
		File inputFile = new File("inputdata.txt");
		File parametersFile = new File("parameters.txt");
		
        BufferedReader inputBuffer = new BufferedReader(new FileReader(inputFile));
        BufferedReader parameterBuffer = new BufferedReader(new FileReader(parametersFile));
        
        Double SDC;
        String s;
        
        ArrayList<Itemset> itemsets = new ArrayList<>();
        ArrayList<Itemset> cannotBeTogetherItemsets = new ArrayList<>();
        ArrayList<Item> mustHaveItems = new ArrayList<>();
        TreeMap<Integer, Item> items = new TreeMap<>();
        
        while((s = parameterBuffer.readLine()) != null) {
        	if(s.contains("MIS")) {
        		String id = s.split("\\(")[1].split("\\)")[0];
        		String mis = s.split("= ")[1];
        		items.put(Integer.valueOf(id), new Item(Integer.valueOf(id), Double.valueOf(mis)));
        	} else if(s.contains("SDC")) {
        		SDC = Double.valueOf(s.split("= ")[1]);
        	} else if(s.contains("cannot")) {
        		String listOfItemset = s.split(": ")[1];
        		String[] cannotBeTogether = listOfItemset.split("\\{");
        		
        		for(String string : cannotBeTogether) {
        			if(!string.isEmpty()) {
        				Itemset cbtItemset = new Itemset(null);
        				String[] ids = string.split("\\}")[0].split(", ");
        				//System.out.println(string.split("\\}")[0]);
        				for(String i : ids) {
        					cbtItemset.getItemset().add(items.get(Integer.valueOf(i)));
        				}
        				cannotBeTogetherItemsets.add(cbtItemset);        				
        			}
        		}
        	} else if(s.contains("must")) {
        		String[] st = s.split(": ")[1].split(" [a-z ]*");
        		for(String stt : st)
        			mustHaveItems.add(items.get(Integer.valueOf(stt)));
        	} else
        		System.err.println("Che cazzo hai scritto in questo file");
        }
        
        while((s = inputBuffer.readLine()) != null) {
        	String[] values = s.split("\\{|\\}")[1].split(", ");
        	Itemset temp = new Itemset(null);
            for (String value : values)
            	temp.getItemset().add(items.get(Integer.valueOf(value)));
            itemsets.add(temp);
        }
        	
	}

}
