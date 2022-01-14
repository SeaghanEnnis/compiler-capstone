//Authored by Seaghan Ennis for Senior Capstone Project

package ADT;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;




public class Interpreter {
	//One of each type of table
	SymbolTable stable;
	QuadTable qtable;
	ReserveTable optable;
	public static int MAXOPS = 17;
	
	//Constructor
	public Interpreter() {
		optable = new ReserveTable(MAXOPS);
		
		//Initializes optable based on given specs
		initReserve(optable);
	}
	
	//Sets up part 1
	public boolean initializeFactorialTest(SymbolTable stable, QuadTable qtable) {
        InitSymbolTableFactorial(stable);
        InitQuadTableFactorial(qtable);
		return true;
	}//Sets Quad Table
	public void InitQuadTableFactorial(QuadTable qtable) {
		qtable.AddQuad(5, 3, 0, 2);
		qtable.AddQuad(5, 3, 0, 1);
		qtable.AddQuad(3, 1, 0, 4);
		qtable.AddQuad(11, 4, 0, 7);
		qtable.AddQuad(2, 2, 1, 2);
		qtable.AddQuad(4, 1, 3, 1);
		qtable.AddQuad(8, 0, 0, 2);
		qtable.AddQuad(6, 2, 0, 0);
	}//Set Symbol Table
	public void InitSymbolTableFactorial(SymbolTable stable) {
		stable.AddSymbol("n", 'v', 10);
		stable.AddSymbol("i", 'v', 0);
		stable.AddSymbol("product", 'v', 0);
		stable.AddSymbol("1", 'v', 1);
		stable.AddSymbol("temp", 'v', 0);
	}

	public boolean initializeSummationTest(SymbolTable stable, QuadTable qtable) {
        InitSymbolTableSummation(stable);
        InitQuadTableSummation(qtable);
		return true;
	}
	//Symbols given
	public void InitSymbolTableSummation(SymbolTable stable){
		stable.AddSymbol("n", 'v', 10);
		stable.AddSymbol("i", 'v', 0);
		stable.AddSymbol("product", 'v', 0);
		stable.AddSymbol("1", 'v', 1);
		stable.AddSymbol("temp", 'v', 0);
	}
	//Quads given and updated to given initReserve, changed to add sum
	public void InitQuadTableSummation(QuadTable qtable){
		qtable.AddQuad(5, 4, 0, 2);
		qtable.AddQuad(5, 3, 0, 1);
		qtable.AddQuad(3, 1, 0, 4);
		qtable.AddQuad(11, 4, 0, 7);
		qtable.AddQuad(4, 2, 1, 2);
		qtable.AddQuad(4, 1, 3, 1);
		qtable.AddQuad(8, 0, 0, 2);
		qtable.AddQuad(6, 2, 0, 0);
	}
	
	//This was provided by isntructor but doesn't match the other file and was modified
	private void initReserve(ReserveTable optable){
    	optable.Add("STOP", 0);
    	optable.Add("DIV", 1);
    	optable.Add("MUL", 2);
    	optable.Add("SUB", 3);
    	optable.Add("ADD", 4);
    	optable.Add("MOV", 5);
    	optable.Add("STI", 16); //Not for 4100?	
    	optable.Add("READ", 7);
    	optable.Add("BNZ", 13);
    	optable.Add("BNP", 14);
    	optable.Add("BNN", 15);
    	optable.Add("BZ", 10);
    	optable.Add("BP", 11);
    	optable.Add("BN", 12);
    	optable.Add("BR", 8);
    	optable.Add("BINDR", 9);
    	optable.Add("PRINT", 6);
	}
	
	public void InterpretQuads(QuadTable qtable, SymbolTable stable, boolean traceOn, String fileName) {
		int programCounter = 0;
		int opcode, op1, op2, op3;
		
		//case 0
		boolean stop = false;
		
		//case 1-4
		int result;
		char kind;
		try {
			FileWriter printToFile = new FileWriter(fileName);

		
		while(programCounter < qtable.maxSize && !stop) {
			//Load quad
			opcode = qtable.GetQuad(programCounter, 0);
			op1 = qtable.GetQuad(programCounter, 1);
			op2 = qtable.GetQuad(programCounter, 2);
			op3 = qtable.GetQuad(programCounter, 3);
			
			if(traceOn) {
				String quadTrace = makeTraceString(programCounter,opcode,op1,op2,op3)+ "\n";
				System.out.print(quadTrace);
				printToFile.write(quadTrace);
			}

			
			//Interpret based on opcode
			switch(opcode){ 
				case 0 : //Stop
					stop = true;
					System.out.print("Execution terminated by stop code!\n");
					printToFile.write("Execution terminated by stop code!\n");
					break;
				case 1 : //Divide
					result = stable.GetInteger(op1) / stable.GetInteger(op2);
					kind = stable.GetKind(op3);
					//System.out.printf("Dividing %d / %d ... Storing index: %d, Storing kind: %c, Storing result: %d\n",stable.GetInteger(op1),stable.GetInteger(op2),op3, kind, result);
					stable.UpdateSymbol(op3, kind, result);
					programCounter++;
					break;
				case 2 : //Multiply
					result = stable.GetInteger(op1) * stable.GetInteger(op2);
					kind = stable.GetKind(op3);
					//System.out.printf("Multiplying %d * %d ... Storing index: %d, Storing kind: %c, Storing result: %d\n",stable.GetInteger(op1),stable.GetInteger(op2),op3, kind, result);
					stable.UpdateSymbol(op3, kind, result);
					programCounter++;
					break;
				case 3: //Subtract
					result = stable.GetInteger(op1) - stable.GetInteger(op2);
					kind = stable.GetKind(op3);
					//System.out.printf("Subtracting %d - %d ... Storing index: %d, Storing kind: %c, Storing result: %d\n",stable.GetInteger(op1),stable.GetInteger(op2),op3, kind, result);
					stable.UpdateSymbol(op3, kind, result);
					programCounter++;
					break;
				case 4: //Add
					result = stable.GetInteger(op1) + stable.GetInteger(op2);
					kind = stable.GetKind(op3);
					//System.out.printf("Adding %d + %d ... Storing index: %d, Storing kind: %c, Storing result: %d\n",stable.GetInteger(op1),stable.GetInteger(op2),op3, kind, result);
					stable.UpdateSymbol(op3, kind, result);
					programCounter++;
					break;
				case 5: //Move
					result = stable.GetInteger(op1);
					kind = stable.GetKind(op3);
					stable.UpdateSymbol(op3, kind, result);
					programCounter++;
					break;
				case 16: //Store
					System.out.print("Storing is for CS 5100!");
					programCounter++;
					break;
				case 7: //read
					System.out.print("Loading is for CS 5100!");
					programCounter++;
					break;
				case 13: //Branch not zero
					if(stable.GetInteger(op1) != 0) {programCounter = op3;}
					else{programCounter++;}
					break;
				case 14: //Branch not positive
					if(stable.GetInteger(op1) <= 0) {programCounter = op3;}
					else{programCounter++;}
					break;
				case 15: //Branch not negative
					if(stable.GetInteger(op1) >= 0) {programCounter = op3;}
					else{programCounter++;}
					break;
				case 10: //Branch zero
					if(stable.GetInteger(op1) == 0) {programCounter = op3;}
					else{programCounter++;}
					break;
				case 11: //Branch positive
					if(stable.GetInteger(op1) > 0) {programCounter = op3;}
					else{programCounter++;}
					break;
				case 12: //Branch negative
					if(stable.GetInteger(op1) < 0) {programCounter = op3;}
					else{programCounter++;}
					break;
				case 8: //Branch Unconditional
					programCounter = op3;
					break;
				case 9: //Branch Unconditional Register
					programCounter = stable.GetInteger(op3);
					break;
				case 6: // Print
					if(1 > 0) { //Turned on so output is on always for PRINT
						//System.out.print(stable.GetSymbol(op1) + "  =   ");
						//printToFile.write(stable.GetSymbol(op1) + "  =   ");
						if(stable.GetDataType(op1) == 'I') {
							System.out.print(stable.GetSymbol(op1) + "  =   ");
							String temp = String.valueOf(stable.GetInteger(op1));
							printToFile.write(stable.GetSymbol(op1) + "  =   ");
							System.out.print(temp);
							printToFile.write(temp + '\n');
						}
						if(stable.GetDataType(op1) == 'F') {
							System.out.print(stable.GetSymbol(op1) + "  =   ");
							printToFile.write(stable.GetSymbol(op1) + "  =   ");
							String temp = String.valueOf(stable.GetFloat(op1));
							System.out.print(temp);
							printToFile.write(temp+ '\n');
						}
						if(stable.GetDataType(op1) == 'S') {
							String temp = String.valueOf(stable.GetString(op1));
							System.out.print(temp);
							printToFile.write(temp+ '\n');
						}
						System.out.print("\n");
						
					}
					programCounter++;
					break;
				default:
					System.out.print("Error, no operation found from given opcode!");
					break;
			}			
		}
		printToFile.close();
		}	catch(IOException ie) {
			ie.printStackTrace();
		}
		
	}
	
    private String makeTraceString(int pc, int opcode,int op1,int op2,int op3 ){
        String result = "";
        result = "PC = "+String.format("%04d", pc)+": "+(optable.LookupCode(opcode)+"     ").substring(0,6)+String.format("%02d",op1)+
                                ", "+String.format("%02d",op2)+", "+String.format("%02d",op3);
        return result;
    }
    
    public int opcodeFor(String op) {
    	int returnVal = -1;
    	switch(op) {
    	case "STOP":
    		returnVal = 0;
    		break;
    	case "DIV":
    		returnVal = 1;
    		break;
    	case "MUL":
    		returnVal = 2;
    		break;
    	case "SUB":
    		returnVal = 3;
    		break;
    	case "ADD":
    		returnVal = 4;
    		break;
    	case "MOV":
    		returnVal = 5;
    		break;
    	case "STI":
    		returnVal = 16;
    		break;
    	case "READ":
    		returnVal = 7;
    		break;
    	case "BNZ":
    		returnVal = 13;
    		break;
    	case "BNP":
    		returnVal = 14;
    		break;
    	case "BNN":
    		returnVal = 15;
    		break;
    	case "BZ":
    		returnVal = 10;
    		break;
    	case "BP":
    		returnVal = 11;
    		break;
    	case "BN":
    		returnVal = 12;
    		break;
    	case "BR":
    		returnVal = 8;
    		break;
    	case "BINDR":
    		returnVal = 9;
    		break;
    	case "PRINT":
			returnVal = 6;
			break;
    		
    	}

    	
    	return returnVal;
    }

    
}
