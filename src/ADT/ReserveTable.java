//Authored by Seaghan Ennis for Senior Capstone Project

package ADT;
import java.util.*;
import java.io.*;

public class ReserveTable{
	
	//Needed variables
	int maxSize;
	int wordsInUse;
	List<ReserveWords> words;
	
	//Constructor
	public ReserveTable(int maxSize){
		this.maxSize = maxSize;
		wordsInUse = 0;
		words = new ArrayList<ReserveWords>();
	}

	//Private class representing one word
	class ReserveWords {
		
		String name;
		int code;
		
		public ReserveWords(String name, int code) {
			this.name = name;
			this.code = code;
		}
	}
	
	//Add a reserve word to the table
	public int Add(String name, int code){
		ReserveWords word = new ReserveWords(name, code);
		words.add(word);
		wordsInUse += 1;
		return words.indexOf(word);
	}
	
	//Get code from name
	public int LookupName(String name) {
		String nameToLookFor = name.toLowerCase();
		for(int i = 0; i < wordsInUse; i++) {
			String currentName = words.get(i).name.toLowerCase();
			if(currentName.compareToIgnoreCase(nameToLookFor) == 0)
				return words.get(i).code;
		}
		return -1;
	}
	
	//Get name from code
	public String LookupCode(int code) {
		for(int i = 0; i < wordsInUse; i++) {
			if(words.get(i).code == code)
				return words.get(i).name;
		}
		return "";
	}
	
	//Print reserve table to given file
	public void PrintReserveTable(String fileName) {
		try {
			FileWriter printToFile = new FileWriter(fileName);
			printToFile.write("     I" +"   " + "   Word" + "   "+ "Code" + "\n \n");
			for(int i = 0; i < wordsInUse; i++) {
				String temp1 = String.format("|%5s", String.valueOf(i));
				String temp2 = String.format("%10s", words.get(i).name);
				String temp3 = String.format("%7s|", String.valueOf(words.get(i).code));
				String tempT = String.format(temp1 + temp2 + temp3);

				printToFile.write(tempT + "\n");
			}
			printToFile.close();
			
			} catch(IOException ie) {
				ie.printStackTrace();
			}
	}
}
