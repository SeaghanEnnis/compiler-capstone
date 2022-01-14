//Authored by Seaghan Ennis for Senior Capstone Project

package ADT;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

//Class to hold symbol table
public class SymbolTable {
	
	
	int maxSize;
	int currentSize;
	List<SymbolRow> symbols;
	char genSymbolChar;
	int genSymbolCount;
	
	//Constructor to initialize
	public SymbolTable(int maxSize) {
		this.maxSize = maxSize;
		symbols = new ArrayList<SymbolRow>();
		currentSize = 0;
		genSymbolCount = 0;
		genSymbolChar = '@';
	}
	
	//Inner class representing a row of the table
	class SymbolRow{
		String symbol;
		char kind;
		char dataType;
		
		int iValue;
		double dValue;
		String sValue;
		
		//3 Overloaded constructors to create the correct type of row
		public SymbolRow(String symbol, char kind, int iValue) {
			this.symbol = symbol;
			this.kind = kind;
			this.dataType = 'I';
			this.iValue = iValue;
		}
		public SymbolRow(String symbol, char kind, double dValue) {
			this.symbol = symbol;
			this.kind = kind;
			this.dataType = 'F';
			this.dValue = dValue;
		}
		public SymbolRow(String symbol, char kind, String sValue) {
			this.symbol = symbol;
			this.kind = kind;
			this.dataType = 'S';
			this.sValue = sValue;
		}
		
	}
	
	//Adds a symbol - with an int value, returns -1 if Symbol table is full, the index if it already has symbol,
	//	or the index where it adds the new row.
	public int AddSymbol(String symbol, char kind, int iValue){
		if(currentSize == maxSize || currentSize > maxSize) {
			return -1;
		}
		
		for(int i = 0; i < currentSize; i++) {
			String currentName = symbols.get(i).symbol;
			if (currentName.compareToIgnoreCase(symbol) == 0) {
				return i;
			}
		}
		
		SymbolRow row = new SymbolRow(symbol, kind, iValue);
		symbols.add(row);
		currentSize++;
		return currentSize - 1;
	}
	
	//Adds a symbol - with an double value, returns -1 if Symbol table is full, the index if it already has symbol,
	//	or the index where it adds the new row.
	public int AddSymbol(String symbol, char kind, double dValue) {
		if(currentSize == maxSize || currentSize > maxSize) {
			return -1;
		}
		
		for(int i = 0; i < currentSize; i++) {
			String currentName = symbols.get(i).symbol;
			if (currentName.compareToIgnoreCase(symbol) == 0) {
				return i;
			}
		}
		
		SymbolRow row = new SymbolRow(symbol, kind, dValue);
		symbols.add(row);
		currentSize++;
		return currentSize - 1;
	}
	
	//Adds a symbol - with an string value, returns -1 if Symbol table is full, the index if it already has symbol,
	//	or the index where it adds the new row.
	public int AddSymbol(String symbol, char kind, String sValue) {
		if(currentSize == maxSize || currentSize > maxSize) {
			return -1;
		}
		
		for(int i = 0; i < currentSize; i++) {
			String currentName = symbols.get(i).symbol;
			if (currentName.compareToIgnoreCase(symbol) == 0) {
				return i;
			}
		}
		
		SymbolRow row = new SymbolRow(symbol, kind, sValue);
		symbols.add(row);
		currentSize++;
		return currentSize - 1;
	}
	
	
	//Tries to find a matching symbol and returns its index, or just returns -1
	public int LookupSymbol(String symbol) {
		for(int i = 0; i < currentSize; i++) {
			String currentName = symbols.get(i).symbol;
			if (currentName.compareToIgnoreCase(symbol) == 0) {
				return i;
			}
		}
		return -1;
	}
	
	//Get symbol at index
	public String GetSymbol(int index) {
		return symbols.get(index).symbol;
	}
	
	//Get char kind at index
	public char GetKind(int index) {
		return symbols.get(index).kind;
	}
	
	//Get data type char from index
	public char GetDataType(int index) {
		return symbols.get(index).dataType;
	}
	
	//get String value from index
	public String GetString(int index) {
		return symbols.get(index).sValue;
	}
	
	//Get int value from index
	public int GetInteger(int index) {
		return symbols.get(index).iValue;
	}
	
	//Gets float value from index
	public double GetFloat(int index) {
		return symbols.get(index).dValue;
	}
	
	//Overloaded update symbols which changes values at given index based on passed 'value'
	public void UpdateSymbol(int index, char kind, int value) {
		symbols.get(index).kind = kind;
		symbols.get(index).iValue = value;
		symbols.get(index).dataType = 'I';
	}
	public void UpdateSymbol(int index, char kind, double value) {
		symbols.get(index).kind = kind;
		symbols.get(index).dValue = value;
		symbols.get(index).dataType = 'F';
	}
	public void UpdateSymbol(int index, char kind, String value) {
		symbols.get(index).kind = kind;
		symbols.get(index).sValue = value;
		symbols.get(index).dataType = 'S';
	}
	
	public int GenSymbol() {
        String name = genSymbolChar + Integer.toString(genSymbolCount);
        genSymbolCount++;
        return this.AddSymbol(name, 'I', 0);
    }
	
	//Opens and writes a formatted file
	public void PrintSymbolTable(String fileName){
		try {
			String temp4 = "Err";
			FileWriter printToFile = new FileWriter(fileName);
			printToFile.write("Index          Symbol" +"   " + "      Kind  " + "   "+ "      DataType" + "           Value  \n \n");
			for(int i = 0; i < currentSize; i++) {
				String temp1 = String.format("|%20s  ", symbols.get(i).symbol);
				String temp2 = String.format("%10s  ", symbols.get(i).kind);
				String temp3 = String.format("%15s  ", symbols.get(i).dataType);
				
				//Gets value based on datatype
				if(symbols.get(i).dataType == 'I') { temp4 = String.format("%25s|", String.valueOf(symbols.get(i).iValue));}
				if(symbols.get(i).dataType == 'F') { temp4 = String.format("%25s|", String.valueOf(symbols.get(i).dValue));}
				if(symbols.get(i).dataType == 'S') { temp4 = String.format("%25s|", String.valueOf(symbols.get(i).sValue));}
				String tempT = String.format(i + temp1 + temp2 + temp3 + temp4);

				printToFile.write(tempT + "\n");
			}
			printToFile.close();
			
			} catch(IOException ie) {
				ie.printStackTrace();
			}
	}
}

