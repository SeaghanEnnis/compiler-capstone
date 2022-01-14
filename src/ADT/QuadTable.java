//Authored by Seaghan Ennis for Senior Capstone Project

package ADT;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

//This class will hold quad words (encoded assembly instructions)
public class QuadTable {

	public int maxSize;
	int[][] quads;
	private int nextAvailable;
	
	//Constructor 
	public QuadTable(int maxSize){
		this.maxSize = maxSize;
		nextAvailable = 0;
		quads = new int[maxSize][4];
	}
	
	//Returns where the next quad will go as well as current size
	public int NextQuad(){
		return nextAvailable;
	}
	
	//Adds a quad and increments size
	public void AddQuad(int opcode, int op1, int op2, int op3) {
		quads[nextAvailable][0] = opcode;
		quads[nextAvailable][1] = op1;
		quads[nextAvailable][2] = op2;
		quads[nextAvailable][3] = op3;
		nextAvailable++;
	}
	
	//Returns quad value at given index and column
	public int GetQuad(int index, int column) {
		return quads[index][column];
	}
	
	public void setQuadOp3(int index, int op3) {
		quads[index][3] = op3;
	}
	
	//updates an existing quad word in the table
	public void UpdateQuad(int index,int opcode, int op1, int op2, int op3 ) {
		quads[index][0] = opcode;
		quads[index][1] = op1;
		quads[index][2] = op2;
		quads[index][3] = op3;
	}
	
	
	//Print Quad table in a nice format at given filename
	public void PrintQuadTable(String fileName) {
		try {
		FileWriter printToFile = new FileWriter(fileName);
		printToFile.write("  Opcode" +"   " + "  Op1    " + "Op2    "+ "Op3" + "\n \n");
		for(int i = 0; i < nextAvailable; i++) {
			String temp1 = String.format("|%7s", quads[i][0]);
			String temp2 = String.format("%7s", quads[i][1]);
			String temp3 = String.format("%7s", quads[i][2]);
			String temp4 = String.format("%7s|", quads[i][3]);
			String tempT = String.format(temp1 + temp2 +temp3 + temp4);

			printToFile.write(tempT + "\n");
		}
		printToFile.close();
		
		} catch(IOException ie) {
			ie.printStackTrace();
		}
	}
}
