//Authored by Seaghan Ennis for completion of capstone project, outline provided by instructor


package ADT;

public class Syntactic {
	//Class is a syntatic analyzer
	
	private String filein;              //The full file path to input file
	private SymbolTable symbolList;     //Symbol table storing ident/const
	private QuadTable quads;
	private Interpreter interp;
	private Lexical lex;                //Lexical analyzer
	private Lexical.token token;        //Next Token retrieved
	private boolean traceon;            //Controls tracing mode
	private int level = 0;              //Controls indent for trace mode
	private boolean anyErrors;          //Set TRUE if an error happens
	private final int symbolSize = 250;
	private final int quadSize = 1500;
	private int Minus1Index;
	private int Plus1Index;
    
    public Syntactic(String filename, boolean traceOn) {
    	filein = filename;
    	traceon = traceOn;
    	symbolList = new SymbolTable(symbolSize);
    	Minus1Index = symbolList.AddSymbol("-1", 'I', -1);
    	Plus1Index = symbolList.AddSymbol("1", 'I', 1);
    	quads = new QuadTable(quadSize);
    	interp = new Interpreter();
    	lex = new Lexical(filein, symbolList, true);
    	lex.setPrintToken(traceOn);
    	anyErrors = false;
    }
    
//The interface to the syntax analyzer, initiates parsing
// Uses variable RECUR to get return values throughout the non-terminal methods    
    public void parse() {
    	// make filename pattern for symbol table and quad table output later
    	String filenameBase = filein.substring(0, filein.length() - 4);
    	System.out.println(filenameBase);
    	int recur = 0;
    	// prime the pump, get first token
    	token = lex.GetNextToken();
    	// call program
    	recur = Program();
    	// done, so add the final STOP quad
    	quads.AddQuad(interp.opcodeFor("STOP"), 0, 0, 0);
    	
    	//print ST, QUAD before execute
    	symbolList.PrintSymbolTable(filenameBase + "ST-before.txt");
    	quads.PrintQuadTable(filenameBase + "QUADS.txt");
    	
    	//interpret
    	if (!anyErrors) {
    		interp.InterpretQuads(quads, symbolList, false, filenameBase + "TRACE.txt");
    	} else {
    		System.out.println("Errors, unable to run program.");
    	}
    	symbolList.PrintSymbolTable(filenameBase + "ST-after.txt");
    }
    
//Non Terminal     
    private int ProgIdentifier() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        // This non-term is used to uniquely mark the program identifier
        if (token.code == lex.codeFor("IDENT")) {
            // Because this is the progIdentifier, it will get a 'p' type to prevent re-use as a var
            symbolList.UpdateSymbol(symbolList.LookupSymbol(token.lexeme), 'p', 0);
            //move on
            token = lex.GetNextToken();
        }
        return recur;
    }
//Non Terminals
    //This method provided by instructor
    private int Program() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Program", true);
        if (token.code == lex.codeFor("MODUL")) {
            token = lex.GetNextToken();
            recur = ProgIdentifier();
            if (token.code == lex.codeFor("SEMIC")) {
                token = lex.GetNextToken();
                recur = Block();
                if (token.code == lex.codeFor("PERID")) {
                    if (!anyErrors) {
                        System.out.println("Success.");
                    } else {
                        System.out.println("Compilation failed.");
                    }
                } else {
                    error(lex.reserveFor("PERID"), token.lexeme);
                }
            } else {
                error(lex.reserveFor("SEMIC"), token.lexeme);
            }
        } else {
            error(lex.reserveFor("MODUL"), token.lexeme);
        }
        trace("Program", false);
        return recur;
    }
    
    //Non Terminal
    //Block-> block body
    private int Block() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Block", true);
        recur = BlockBody();
        trace("Block", false);
        return recur;
    }
    
    //Block body
    //<block-body> -> $BEGIN <STATEMENT> {$SEMICOLON <STATEMENT>}* $END
    private int BlockBody() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("BlockBody", true);
        if (token.code == lex.codeFor("BEGIN")) {
            token = lex.GetNextToken();
            recur = Statement();
            while ((token.code == lex.codeFor("SEMIC")) && (!lex.EOF()) && (!anyErrors)) {
                token = lex.GetNextToken();
                recur = Statement();
            }
            if (token.code == lex.codeFor("__END")) {
                token = lex.GetNextToken();
            } else {
                error(lex.reserveFor("__END"), token.lexeme);
            }
        } else {
            error(lex.reserveFor("BEGIN"), token.lexeme);
        }
        trace("BlockBody", false);
        return recur;
    }
    //Not a NT, but used to shorten Statement code body   
    //<variable> $COLON-EQUALS <simple expression>
    
    //REMOVED AND MERGED WITH STATEMENT
    /*private int handleAssignment() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("handleAssignment", true);
        //have ident already in order to get to here, handle as Variable
        recur = Variable();  //Variable moves ahead, next token ready
        if (token.code == lex.codeFor("ASSGN")) {
            token = lex.GetNextToken();
            recur = SimpleExpression();
        } else {
            error(lex.reserveFor("ASSGN"), token.lexeme);
        }
        trace("handleAssignment", false);
        return recur;
    }*/
    
    //<simple expression> -> [<sign>]  <term>  {<addop>  <term>}*
    private int SimpleExpression() {
        int recur = 0;
        int left = 0, right = 0, signval = 0, temp, opcode;
        
        if (anyErrors) {//Return immediately if any errors
            return -1;
        }
        trace("SimpleExpression", true);
        //If starts with a + or minus goto sign, signval is baed on plus or minus
        if(token.code == lex.codeFor("_PLUS") || (token.code == lex.codeFor("MINUS"))) {
        	signval = Sign();
        }
        
        left = Term();
        
        if (signval == -1) {
        	   quads.AddQuad(interp.opcodeFor("MUL"),left,Minus1Index,left);
        }
        
        //If term is followed by an addop, run addop and see goto term again
        while(token.code == lex.codeFor("_PLUS") || token.code == lex.codeFor("MINUS")) {
        	//This follows the example code, handled differently than the sign() which returns a + or - 1.
            if(token.code == lex.codeFor("_PLUS")) {
            	opcode = interp.opcodeFor("ADD");
            }else  {
            	opcode = interp.opcodeFor("SUB");
            }
        	recur = Addop(); //GNT (addop will get next token as in example code, but can also throw an error if something goes wrong)
        	
        	//Right = term
        	right = Term();
        	
        	temp = symbolList.GenSymbol();
        	quads.AddQuad(opcode,left,right,temp);
        	left = temp;
        }
        
        trace("SimpleExpression", false);
        return left;
    }
    
    // <sign> -> $PLUS | $MINUS
    private int Sign() {
        int sign = 1;
        
        if (anyErrors) { //if errors exit
            return -1;
        }
        trace("Addop", true);
        //Get plus or minus and move to next token, else error
        if( token.code == lex.codeFor("MINUS")) {
        	sign = -1;
        	token = lex.GetNextToken();
        }else if(token.code == lex.codeFor("_PLUS")) {
        	token = lex.GetNextToken();
        }
        
        trace("Addop", false);
    	return sign;
    }
    
    //<mulop> -> $MULTIPLY | $DIVIDE
    private int Addop() {
        int recur = 0;
        if (anyErrors) { //if errors exit
            return -1;
        }
        trace("Addop", true);
        //Get plus or minus and move to next token, else error
        //Funationally the same as sign()
        if(token.code == lex.codeFor("_PLUS") || token.code == lex.codeFor("MINUS")) {
        	token = lex.GetNextToken();
        }else {
        	System.out.println("Error in Addop");
        	error(lex.reserveFor("_PLUS"),token.lexeme);
        }
        trace("Addop", false);
    	return recur;
    }
    
//Non Terminal
   /* <statement>-> [
                   <block-body> |
                   <variable>  $ASSIGN  <simple expression> |
                   $IF  <relexpression>  $THEN  <statement> [$ELSE  <statement>] |
                   $WHILE  <relexpression>  $DO  <statement> |
                   $WRITELN  $LPAR (<simple expression> | <stringconst>)  $RPAR
                  ]+*/
    //Given statement code would not allow statement to be done more than once as the + sign would suggest
    //Example code was followed. <block_body> implements any number of statements since since it has {$SCOLN  <statement>} so this is a mute point
    private int Statement() {
        int recur = 0;
        int left, right;
        
        if (anyErrors) {
            return -1;
        }
        trace("Statement", true);
        if (token.code == lex.codeFor("BEGIN")) { //Begin means a new block, gnt will be called in block body not here
        	recur = BlockBody();
        } else if (token.code == lex.codeFor("IDENT")) { 
        	
            //have ident already in order to get to here, handle as Variable
        	left = Variable();  //Variable moves ahead, next token ready
            if (token.code == lex.codeFor("ASSGN")) {
                token = lex.GetNextToken();
                right = SimpleExpression();
                quads.AddQuad(interp.opcodeFor("MOV"),right,0,left);
            } else {
                error(lex.reserveFor("ASSGN"), token.lexeme);
            }
        } else  if (token.code == lex.codeFor("___IF")) { //found an if statement
        	//Follows example code given
        	int branchQuad, patchElse;
        	//System.out.println("FOUND IF!");
        	
        	token = lex.GetNextToken(); //Move past IF
        	branchQuad = Relexpression();
        	if(token.code == lex.codeFor("_THEN")) {
        		//Found THEN after relational/Condition then get statement
        		token = lex.GetNextToken();
        		recur = Statement();
        		if (token.code == lex.codeFor("_ELSE")) {
        			//After statement, get else, jump location, and elses statements
        			token = lex.GetNextToken();
        			patchElse = quads.NextQuad();
        			quads.AddQuad(interp.opcodeFor("BR"), 0, 0, 0);
        			quads.setQuadOp3(branchQuad, quads.NextQuad());
        			recur = Statement();
        			quads.setQuadOp3(patchElse, quads.NextQuad());
        		} else {// If no else then find branch to end target
        			quads.setQuadOp3(branchQuad, quads.NextQuad());
        		}		
        	} else {
        		error(lex.reserveFor("_THEN"),token.lexeme);
        	}
        	
  
        } else  if(token.code == lex.codeFor("WHILE")){ //Found while statement
        	int saveTop, branchQuad;
        	//System.out.println("FOUND WHILE!");
        	
        	token = lex.GetNextToken();
        	saveTop = quads.NextQuad();	//Save the top so we can jump to it
        	branchQuad = Relexpression(); //Get while condition
        	if(token.code == lex.codeFor("___DO")) {
        		//Check for do after conditional
        		token = lex.GetNextToken();
        		//Get statement to do during while loop
        		recur = Statement();
        		quads.AddQuad(interp.opcodeFor("BR"), 0, 0, saveTop);
        		quads.setQuadOp3(branchQuad,quads.NextQuad());
        	} else {
        		error(lex.reserveFor("___DO"), token.lexeme);
        	}
        	
        }else if(token.code == lex.codeFor("WRTLN")){
        	//Was given writeline code directly, see below
        	recur = handleWriteln();
        }else{
                error("Statement start", token.lexeme);
        	}
        trace("Statement", false);
        return recur;
    }
    
    //<relexpression> -> <simple expression>  <relop>  <simple expression>
    private int Relexpression() {
    	int recur = 0;
    	int left, right, result, temp;
    	
    	String relop = "";
    	
    	if (anyErrors) {
    		return -1;
    	}
    	
    	left = SimpleExpression();
    	relop = Relop();
    	right = SimpleExpression();
    	temp = symbolList.GenSymbol();
    	quads.AddQuad(interp.opcodeFor("SUB"), left, right, temp);
    	result = quads.NextQuad();
    	quads.AddQuad(relopToOpcode(relop),temp,0,0);
    	return result;
    }
    
    //Checks relop is a comparator, generated from given table
    //<relop>    ->   $EQ | $LSS | $GTR | $NEQ | $LEQ | $GEQ
    private String Relop() {
    	String result = "0";
    	if (token.code == lex.codeFor("GRATR")) {
    		result = ">";
    	}else if(token.code == lex.codeFor("_LESS")){
    		result = "<";
    	}else if(token.code == lex.codeFor("GROEQ")){
    		result = ">=";
    	}else if(token.code == lex.codeFor("LEOEQ")){
    		result = "<=";
    	}else if(token.code == lex.codeFor("EQALS")){
    		result = "=";
    	}else if(token.code == lex.codeFor("NEQLS")){
    		result = "<>";
    	}else {
    		error("Relop", token.lexeme);
    	}
    	
    	token = lex.GetNextToken();
    	
    	return result;
    }
    
    //changes relop to opcode for false branch
    public int relopToOpcode(String relop){
    	int result = 0;
     	switch (relop){
     		case "=": result = interp.opcodeFor("BNZ"); break;
     		case "<>": result = interp.opcodeFor("BZ"); break;
     		case "<": result = interp.opcodeFor("BNN"); break;
     		case ">": result = interp.opcodeFor("BNP"); break;
     		case "<=": result = interp.opcodeFor("BP"); break;
     		case ">=": result = interp.opcodeFor("BN"); break;
     	}
     	return result;
    }
    
    //<variable> -> <identifier>
    private int Variable() {
    	int recur = 0;
    	if (anyErrors) {
    		return -1;
    	}
    	
    	//Specification said variable did not need to call a identifier()
    	trace("Variable", true);
    	if ((token.code == lex.codeFor("IDENT"))) {
    	//return the location of this variable for Quad use
    		recur = symbolList.LookupSymbol(token.lexeme);
    	// bookkeeping and move on
    		token = lex.GetNextToken();
    	} else {
    		error("Variable", token.lexeme);
    	}
    	trace("Variable", false);
    	
    	return recur;
    }
    
    //Terminal unsigned constant
	private int UnsignedConstant() {
		int recur = 0;
		if (anyErrors) {
			return -1;
		}
		trace("UnsignedConstant", true);
		// only accepting a number, can call and will hit error later if theres a problem
		recur = UnsignedNumber();
		trace("UnsignedConstant", false);
		return recur;
	}
   
    
    //<term> -> <factor> {<mulop>  <factor> }*
	//This follows simple expression example, except is has mul/divid
    private int Term() {
        //int recur = 0;
        int left = 0, right = 0, signval = 0, temp, opcode;
        
        if (anyErrors) {
            return -1;
        }
        
        
        trace("Term", true);
        //Term always starts with factor
        left = Factor();
        
        //After factor if given a mulop, get mulop, then look for recuring factor
        while(token.code == lex.codeFor("MULTI") || token.code == lex.codeFor("DIVID")) {
        	signval = Mulop(); // 1 for mul, -1 for divide
        	if(signval == 1)
        		{opcode = interp.opcodeFor("MUL");}
        	else
        		{opcode = interp.opcodeFor("DIV");}
        	
        	//Always factors
        	right = Factor();
        
        	temp = symbolList.GenSymbol();
        	quads.AddQuad(opcode,left,right,temp);
        	left = temp; 
        }
        trace("Term", false);
        
        return left;
    }
    
    //<mulop> -> $MULTIPLY | $DIVIDE
    private int Mulop() {
        int returnVal = 1;
        if (anyErrors) { //return on error
            return -1;
        }
        trace("Addop", true);
        
        //Get Multiply or divide and move to next token, else error
        if(token.code == lex.codeFor("MULTI")) {
        	returnVal = 1;
        	token = lex.GetNextToken();
        }else if(token.code == lex.codeFor("DIVID")) {
        	returnVal = -1;
        	token = lex.GetNextToken();
        }
        else {
        	error(lex.reserveFor("MULTI"),token.lexeme);
        }
        trace("Addop", false);
    	return returnVal;
    }
    
    //<factor> -> <unsigned constant> |<variable> | $LPAR    <simple expression>    $RPAR
    private int Factor() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        
        trace("Factor", true);
        if(token.code == lex.codeFor("IDENT")) { //If factor is ident, then goto variable
        	recur = Variable();
        }
        else if(token.code == lex.codeFor("I_VAR") || token.code == lex.codeFor("F_VAR")) { //If factor is a number goto unsigned number
        	recur = UnsignedNumber();
        }
        else if(token.code == lex.codeFor("LPARE")) { //If factor is left paren, goto simple expression
        	token = lex.GetNextToken();
        	recur = SimpleExpression();
        	//System.out.println("START SIMPLE IN PAREN");
        	if(token.code == lex.codeFor("RPARE")) { //upon return from simple expression, needs a right paren
        		token = lex.GetNextToken();
        	}else {error(lex.reserveFor("RPARE"), token.lexeme);}
        }
        else {
        	error(lex.reserveFor("IDENT"), token.lexeme);
        }
        	
        trace("Factor", false);
        
        return recur;
    }
    
    //<unsigned number>-> $FLOAT | $INTEGER
    private int UnsignedNumber() {
    	int recur = 0;
    	
    	if (anyErrors) {
    		return -1;
    	}
    	
    	trace("UnsignedNumber", true);
    	// float or int or ERROR
    	// unsigned constant starts with integer or float number
    	if ((token.code == lex.codeFor("I_VAR") || (token.code == lex.codeFor("F_VAR")))) {
    	// return the s.t. index
    		recur = symbolList.LookupSymbol(token.lexeme);
    		token = lex.GetNextToken();
    	} else {
    		error("Integer or Floating Point Number", token.lexeme);
    	}
    	
    	trace("UnsignedNumber", false);
    	
    	return recur;
    }
    
    private int handleWriteln() {
    	int recur = 0;
    	int toprint = 0;
    	if (anyErrors) {
    		return -1;
    	}
    	
    	trace("handleWriteln", true);
    	//got here from a WRITELN token, move past it...
    	token = lex.GetNextToken();
    	
    	//look for ( stringconst, ident, simpleexp )
    	if (token.code == lex.codeFor("LPARE")) {
    	//move on
    		token = lex.GetNextToken();
    		if ((token.code == lex.codeFor("S_VAR"))) { //|| (token.code == lex.codeFor("IDNT"))) {
    			// save index for string literal or identifier
    			toprint = symbolList.LookupSymbol(token.lexeme);
    			//move on
    			token = lex.GetNextToken();
    		} else {
    			toprint = SimpleExpression();
    		}
    		
    		quads.AddQuad(interp.opcodeFor("PRINT"), toprint, 0, 0);
    		
    		//now need right ")"
    		if (token.code == lex.codeFor("RPARE")) {
    			//move on
    			token = lex.GetNextToken();
    		} else {
    			error(lex.reserveFor("RPARE"), token.lexeme);
    		}
    	} else {
    		error(lex.reserveFor("LPARE"), token.lexeme);
    	}
    	// end lpar group
    	trace("handleWriteln", false);
    	return recur;
    	}

    
    /*     UTILITY FUNCTIONS USED THROUGHOUT THIS CLASS */
// error provides a simple way to print an error statement to standard output
//     and avoid reduncancy
    private void error(String wanted, String got) {
        anyErrors = true;
        System.out.println("ERROR: Expected " + wanted + " but found " + got);
    }
// trace simply RETURNs if traceon is false; otherwise, it prints an
    // ENTERING or EXITING message using the proc string
    private void trace(String proc, boolean enter) {
        String tabs = "";
        if (!traceon) {
            return;
        }
        if (enter) {
            tabs = repeatChar(" ", level);
            System.out.print(tabs);
            System.out.println("--> Entering " + proc);
            level++;
        } else {
            if (level > 0) {
                level--;
            }
            tabs = repeatChar(" ", level);
            System.out.print(tabs);
            System.out.println("<-- Exiting " + proc);
        }
    }
// repeatChar returns a string containing x repetitions of string s; 
//    nice for making a varying indent format
    private String repeatChar(String s, int x) {
        int i;
        String result = "";
        for (i = 1; i <= x; i++) {
            result = result + s;
        }
        return result;
    }
/*  Template for all the non-terminal method bodies
private int exampleNonTerminal(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("NameOfThisMethod", true);
// unique non-terminal stuff
        trace("NameOfThisMethod", false);
        return recur;
}  
    
    */    
}
