/*
 The some of the following code was provided by the instructor for the completion of PHASE 2 
of the compiler project for CS4100. See notes above some methods for which ones were.

 */
package ADT;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 * @author abrouill
 */
import java.io.*;

public class Lexical {

    private File file;                        //File to be read for input
    private FileReader filereader;            //Reader, Java reqd
    private BufferedReader bufferedreader;    //Buffered, Java reqd
    private String line;                      //Current line of input from file   
    private int linePos;                      //Current character position
                                              //  in the current line
    private SymbolTable saveSymbols;          //SymbolTable used in Lexical
                                              //  sent as parameter to construct
    private boolean EOF;                      //End Of File indicator
    private boolean echo;                     //true means echo each input line
    private boolean printToken;               //true to print found tokens here
    private int lineCount;                    //line #in file, for echo-ing
    private boolean needLine;                 //track when to read a new line
    //Tables to hold the reserve words and the mnemonics for token codes
    private ReserveTable reserveWords = new ReserveTable(50); //a few more than # reserves
    private ReserveTable mnemonics = new ReserveTable(50); //a few more than # reserves
     
    //constructor
    public Lexical(String filename, SymbolTable symbols, boolean echoOn) {
        saveSymbols = symbols;  //map the initialized parameter to the local ST
        echo = echoOn;          //store echo status
        lineCount = 0;          //start the line number count
        line = "";              //line starts empty
        needLine = true;        //need to read a line
        printToken = false;     //default OFF, do not print tokesn here
                                //  within GetNextToken; call setPrintToken to
                                //  change it publicly.
        linePos = -1;           //no chars read yet
        //call initializations of tables
        initReserveWords(reserveWords);
        initMnemonics(mnemonics);

        //set up the file access, get first character, line retrieved 1st time
        try {
            file = new File(filename);    //creates a new file instance  
            filereader = new FileReader(file);   //reads the file  
            bufferedreader = new BufferedReader(filereader);  //creates a buffering character input stream  
            EOF = false;
            currCh = GetNextChar();
        } catch (IOException e) {
            EOF = true;
            e.printStackTrace();
        }
    }

    public int codeFor(String mnemonic){
        return mnemonics.LookupName(mnemonic);
    }
    
    public String reserveFor(String mnemonic){
        return reserveWords.LookupCode(mnemonics.LookupName(mnemonic));
    }
    
    // token class is declared here, no accessors needed
    public class token {

        public String lexeme;
        public int code;
        public String mnemonic;

        token() {
            lexeme = "";
            code = 0;
            mnemonic = "";
        }
    }

    private token dummyGet() {
        token result = new token();
        result.lexeme = "" + currCh; //have the first char
        currCh = GetNextChar();
        result.code = 0;
        result.mnemonic = "DUMY";
        return result;
        
    }
        
    
    public final int _GRTR = 38;
    public final int _LESS = 39;
    public final int _GREQ = 40;
    public final int _LEEQ = 41;
    public final int _EQLS = 42;
    public final int _NEQL = 43;
    
 /*
    // given a mnemonic, find its token code value
    public int codeFor(String mnemonic) {
        return mnemonics.LookupName(mnemonic);
    }
    // given a mnemonic, return its reserve word
    public String reserveFor(String mnemonic) {
        return reserveWords.LookupCode(mnemonics.LookupName(mnemonic));
    }
*/
    
    // Public access to the current End Of File status
    public boolean EOF() {
        return EOF;
    }
    // DEBUG enabler, turns on token printing inside of GetNextToken
    public void setPrintToken(boolean on) {
        printToken = on;
    }

    
/* Reserve Words */    
    private void initReserveWords(ReserveTable reserveWords) {
        reserveWords.Add("GOTO", 0);
        reserveWords.Add("INTEGER", 1);
        reserveWords.Add("TO", 2);
        reserveWords.Add("DO", 3);
        reserveWords.Add("IF", 4);
        reserveWords.Add("THEN", 5);
        reserveWords.Add("ELSE", 6);
        reserveWords.Add("FOR", 7);
        reserveWords.Add("OF", 8);
        reserveWords.Add("WRITELN", 9);
        reserveWords.Add("READLN", 10);
        reserveWords.Add("BEGIN", 11);
        reserveWords.Add("END", 12);
        reserveWords.Add("VAR", 13);
        reserveWords.Add("WHILE", 14);
        reserveWords.Add("MODULE", 15);
        reserveWords.Add("LABEL", 16);
        reserveWords.Add("REPEAT", 17);
        reserveWords.Add("UNTIL", 18);
        reserveWords.Add("PROCEDURE", 19);
        reserveWords.Add("DOWNTO", 20);
        reserveWords.Add("FUNCTION", 21);
        reserveWords.Add("RETURN", 22);
        reserveWords.Add("FLOAT", 23);
        reserveWords.Add("STRING", 24);
        reserveWords.Add("ARRAY", 25);

        //1 and 2-char
        reserveWords.Add("/", 30);
        reserveWords.Add("*", 31);
        reserveWords.Add("+", 32);
        reserveWords.Add("-", 33);
        reserveWords.Add("(", 34);
        reserveWords.Add(")", 35);
        reserveWords.Add(";", 36);
        reserveWords.Add(":=", 37);
        reserveWords.Add(">", 38);
        reserveWords.Add("<", 39);
        reserveWords.Add(">=", 40);
        reserveWords.Add("<=", 41);
        reserveWords.Add("=", 42);
        reserveWords.Add("<>", 43);
        reserveWords.Add(",", 44);
        reserveWords.Add("[", 45);
        reserveWords.Add("]", 46);
        reserveWords.Add(":", 47);
        reserveWords.Add(".", 48);
//     reserveWords.Add("", );

    }
/* Mnemonics */
    private void initMnemonics(ReserveTable mnemonics) {
        mnemonics.Add("GO_TO", 0);
        mnemonics.Add("INTGE", 1);
        mnemonics.Add("___TO", 2);
        mnemonics.Add("___DO", 3);
        mnemonics.Add("___IF", 4);
        mnemonics.Add("_THEN", 5);
        mnemonics.Add("_ELSE", 6);
        mnemonics.Add("__FOR", 7);
        mnemonics.Add("___OF", 8);
        mnemonics.Add("WRTLN", 9);
        mnemonics.Add("READL", 10);
        mnemonics.Add("BEGIN", 11);
        mnemonics.Add("__END", 12);
        mnemonics.Add("__VAR", 13);
        mnemonics.Add("WHILE", 14);
        mnemonics.Add("MODUL", 15);
        mnemonics.Add("LABLE", 16);
        mnemonics.Add("RPEAT", 17);
        mnemonics.Add("UNTIL", 18);
        mnemonics.Add("PROCD", 19);
        mnemonics.Add("DOWNT", 20);
        mnemonics.Add("FUNCT", 21);
        mnemonics.Add("RETRN", 22);
        mnemonics.Add("FLOAT", 23);
        mnemonics.Add("STRNG", 24);
        mnemonics.Add("ARRAY", 25);

        //1 and 2-char
        mnemonics.Add("DIVID", 30);
        mnemonics.Add("MULTI", 31);
        mnemonics.Add("_PLUS", 32);
        mnemonics.Add("MINUS", 33);
        mnemonics.Add("LPARE", 34);
        mnemonics.Add("RPARE", 35);
        mnemonics.Add("SEMIC", 36);
        mnemonics.Add("ASSGN", 37);
        mnemonics.Add("GRATR", 38);
        mnemonics.Add("_LESS", 39);
        mnemonics.Add("GROEQ", 40);
        mnemonics.Add("LEOEQ", 41);
        mnemonics.Add("EQALS", 42);
        mnemonics.Add("NEQLS", 43);
        mnemonics.Add("COMMA", 44);
        mnemonics.Add("LBRAK", 45);
        mnemonics.Add("RBRAK", 46);
        mnemonics.Add("COLLN", 47);
        mnemonics.Add("PERID", 48);
        mnemonics.Add("IDENT", 50);
        mnemonics.Add("I_VAR", 51);
        mnemonics.Add("F_VAR", 52);
        mnemonics.Add("S_VAR", 53);
        mnemonics.Add("UNKWN", 99);

    }

    private final int UNKNOWN_CHAR = 99;

    // Character category for alphabetic chars
    private boolean isLetter(char ch) {
        return (((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z')));
    }
    // Character category for 0..9 
    private boolean isDigit(char ch) {
        return ((ch >= '0') && (ch <= '9'));
    }
    // Category for any whitespace to be skipped over
    private boolean isWhitespace(char ch) {
        return ((ch == ' ') || (ch == '\t') || (ch == '\n'));
    }

    
    // Returns the VALUE of the next character without removing it from the
    //    input line.  Useful for checking 2-character tokens that start with
    //    a 1-character token.
    private char PeekNextChar() {
        char result = ' ';
        if ((needLine) || (EOF)) {
            result = ' '; //at end of line, so nothing
        } else // 
        {
            if ((linePos + 1) < line.length()) { //have a char to peek
                result = line.charAt(linePos + 1);
            }
        }
        return result;
    }

    // Called by GetNextChar when the characters in the current line are used up.
    private void GetNextLine() {
        try {
            line = bufferedreader.readLine();
            if ((line != null) && (echo)) {
                lineCount++;
                System.out.println(String.format("%04d", lineCount) + " " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line == null) {    // The readLine returns null at EOF, set flag
            EOF = true;
        }
        linePos = -1;      // reset vars for new line if we have one
        needLine = false;  // we have one, no need
        //the line is ready for the next call to get a character
    }

    public char GetNextChar() {
        char result;
        if (needLine) //ran out last time we got a char, so get a new line
        {
            GetNextLine();
        }
        //try to get char from line buff
        if (EOF) {
            result = '\n';
            needLine = false;
        } else {
            if ((linePos < line.length() - 1)) { //have a character available
                linePos++;
                result = line.charAt(linePos);
            } else { //need a new line, but want to return eoln on this call first
                result = '\n';
                needLine = true; //will read a new line on next GetNextChar call
            }
        }
        return result;
    }

    final char commstart1 = '{';
    final char commend1 = '}';
    final char commstart2 = '(';
    final char commstartend = '*';
    final char commend2 = ')';

    public char skipComment(char curr) {
        if (curr == commstart1) {
            curr = GetNextChar();
            while ((curr != commend1) && (!EOF)) {
                curr = GetNextChar();
            }
            if (EOF) {
                System.out.println("Comment not terminated before End Of File");
            } else {
                curr = GetNextChar();
            }
        } else {
            if ((curr == commstart2) && (PeekNextChar() == commstartend)) {
                curr = GetNextChar(); // get the second
                curr = GetNextChar(); // into comment or end of comment
//            while ((curr != commstartend) && (PeekNextChar() != commend2) &&(!EOF)) {
                while ((!((curr == commstartend) && (PeekNextChar() == commend2))) && (!EOF)) {
//                if (lineCount >=4) {
                    //              System.out.println("In Comment, curr, peek: "+curr+", "+PeekNextChar());}
                    curr = GetNextChar();
                }
                if (EOF) {
                    System.out.println("Comment not terminated before End Of File");
                } else {
                    curr = GetNextChar();          //must move past close
                    curr = GetNextChar();          //must get following
                }
            }

        }
        return (curr);
    }

    //Skip white space in gnt()
    public char skipWhiteSpace() {

        do {
            while ((isWhitespace(currCh)) && (!EOF)) {
                currCh = GetNextChar();
            }
            currCh = skipComment(currCh);
        } while (isWhitespace(currCh) && (!EOF));
        return currCh;
    }

    //Some comparators have two symbols
    private boolean isPrefix(char ch) {
        return ((ch == ':') || (ch == '<') || (ch == '>'));
    }
    
    //Some comparators have two symbols
    private boolean isSuffix(char ch) {
        return ((ch == '=') || (ch == '>'));
    }
    
    //Check for start of string
    private boolean isStringStart(char ch) {
        return ch == '"';
    }

    private final int IDENT_ID = 50;
    private final int INTEGER_ID = 51;
    private final int FLOAT_ID = 52;
    private final int STRING_ID = 53;
    
    //global char
    char currCh;

    private token getIdent() {
    	token result = new token();
		
		//char currCh = GetNextChar();
		//result.lexeme = "" + currCh;

		//Check for validity
		while (isLetter(currCh)||(isDigit(currCh)||(currCh == '$')||(currCh=='_'))) {
			result.lexeme = result.lexeme + currCh;
			currCh = GetNextChar();
		}
		
		//Is it a reserve word?
		result.code = reserveWords.LookupName(result.lexeme);
		result.mnemonic = mnemonics.LookupCode(result.code);
		
		//If not, its a symbol
		if(result.code == -1) {
			result.code = IDENT_ID;
			result.mnemonic = mnemonics.LookupCode(result.code);
		}
		
		//currCh = GetNextChar();
		
        return result;
    }

    private token getNumber() {
        /* a number is:   <digit>+[.<digit>*[E[+|-]<digit>+]] */
    	
    	//new token
      	token result = new token();
      	//Assumed integer
    	result.code = INTEGER_ID;
    		
    		
    		//Get entire number
    	while ((isDigit(currCh)||(currCh == '.') || (currCh == 'E'))) {
    			
    			//If decimal -> float
    		if(currCh == '.' && PeekNextChar() != 'E') {result.code = FLOAT_ID;}
    		//Does exponent have a + or -?
    		if(currCh == 'E' && ((PeekNextChar() != '+') && (PeekNextChar() != '-'))){
    			System.out.println("Bad exponential token for " + result.lexeme);
    		}
    		
    		//Get next char
    		result.lexeme = result.lexeme + currCh;
    		currCh = GetNextChar();

    	}

    		//Is the value already in the symbol table?
    	int lookup = saveSymbols.LookupSymbol(result.lexeme);
    		
    		//Not found, then add to symbol table
    	if(lookup == -1) {
    		if(result.code == INTEGER_ID) {

    			//saveSymbols.AddSymbol(result.lexeme, 'I', result.lexeme);
    			result.mnemonic = mnemonics.LookupCode(result.code);
    		}
    		if (result.code == FLOAT_ID) {
    			//saveSymbols.AddSymbol(result.lexeme, 'F', result.lexeme);
    			result.mnemonic = mnemonics.LookupCode(result.code);
    		}
    	}
    	
        return result;
    }

    private token getString() {
    	
    	//New token
    	token result = new token();
    	//result.lexeme = "" + currCh;
    	
    	//Have not found end of string
    	while(PeekNextChar() != '"') {
    		//End of line -> bad string
    		if(needLine) {
    			System.out.println("ERROR NO STRING END FOR -" + result.lexeme);
    			currCh = GetNextChar();
    			break;
    		}
    		currCh = GetNextChar();
    		result.lexeme = result.lexeme + currCh;
    	}
    	
    	//Ignore last "
		currCh = GetNextChar();
		//result.lexeme = result.lexeme + currCh;
    	
		//Set for return
    	result.code = STRING_ID;
    	result.mnemonic = mnemonics.LookupCode(result.code);
    	
    	currCh = GetNextChar();
    	
        return result;
    }

    private token getOneTwoChar() {
    	
    	//new token
    	token result = new token();
    	result.lexeme = "" + currCh;
    	
    	//Check for Two char
    	if(isPrefix(currCh) && PeekNextChar() != ' ' && isSuffix(PeekNextChar())) {
    		currCh = GetNextChar();
    		result.lexeme = result.lexeme + currCh;
    		//System.out.println("TWO CHARS " + result.lexeme);
    	}
    	
    	result.code = reserveWords.LookupName(result.lexeme);
    	//System.out.println("CODE: ? " + result.lexeme);
    	//Token is not found
    	if(result.code == -1) {
    		result.code = UNKNOWN_CHAR; //Unknown
    		result.mnemonic = mnemonics.LookupCode(result.code);
    	}else {
    		result.mnemonic = mnemonics.LookupCode(result.code);
    	}
    	
    	//Stage for return
    	currCh = GetNextChar();
    	
        return result;
    }

    // Checks to see if a string contains a valid DOUBLE 
    public boolean doubleOK(String stin) {
        boolean result;
        Double x;
        try {
            x = Double.parseDouble(stin);
            result = true;
        } catch (NumberFormatException ex) {
            result = false;
        }
        return result;
    }

    // Checks the input string for a valid INTEGER
    public boolean integerOK(String stin) {
        boolean result;
        int x;
        try {
            x = Integer.parseInt(stin);
            result = true;
        } catch (NumberFormatException ex) {
            result = false;
        }
        return result;
    }

    //Method provided by instructor
    public token checkTruncate(token result) {
        // truncate if needed
        int ival = 0;
        double dval = 0.0;
        int len = result.lexeme.length();
        String lexemetrunc = result.lexeme;

        switch (result.code) {
            case IDENT_ID:
                if (len > 20) {
                    lexemetrunc = result.lexeme.substring(0, 20);
                    System.out.println("Identifier length > 20, truncated " + result.lexeme
                            + " to " + lexemetrunc);
                }
                saveSymbols.AddSymbol(lexemetrunc, 'v', 0);
                break;
            case INTEGER_ID:
                if (len > 6) {
                    lexemetrunc = result.lexeme.substring(0, 6);
                    System.out.println("Integer length > 6, truncated " + result.lexeme
                            + " to " + lexemetrunc);
                    ival = 0;
                } else //no trun, but is it ok
                {
                    if (integerOK(result.lexeme)) {
                        ival = Integer.valueOf(lexemetrunc);
                    } else {
                        System.out.println("Invalid integer value");
                    }
                }
                saveSymbols.AddSymbol(lexemetrunc, 'c', ival);
                break;
            case FLOAT_ID:
                if (len > 12) {
                    lexemetrunc = result.lexeme.substring(0, 12);
                    System.out.println("Float length > 12, truncated " + result.lexeme
                            + " to " + lexemetrunc);
                    dval = 0;
                } else //no trun, but is it ok
                {
                    if (doubleOK(result.lexeme)) {
                        dval = Double.valueOf(lexemetrunc);
                    } else {
                        System.out.println("Invalid float value");
                    }
                }
                saveSymbols.AddSymbol(lexemetrunc, 'c', dval);
                break;

            case STRING_ID:
                saveSymbols.AddSymbol(result.lexeme, 'c', result.lexeme);
                break;
            default:
                break; //don't add                           
        }

        return result;
    }
    
    //This method was provided by instructor
    public token GetNextToken() {
        token result = new token();

        currCh = skipWhiteSpace();
        if (isLetter(currCh)) { //is ident
            result = getIdent();
        } else if (isDigit(currCh)) { //is numeric
            result = getNumber();
        } else if (isStringStart(currCh)) { //string literal
            result = getString();
        } else //default char checks
        {
            result = getOneTwoChar();
        }

        if ((result.lexeme.equals("")) || (EOF)) {
            result = null;
        }
//set the mnemonic
        if (result != null) {
// THIS LINE REMOVED-- PUT BACK IN TO USE LOOKUP            result.mnemonic = mnemonics.LookupCode(result.code);
            result = checkTruncate(result);
            if (printToken) {
                System.out.println("\t" + result.mnemonic + " | \t" + String.format("%04d", result.code) + " | \t" + result.lexeme);
            }
        }
        return result;

    }
}
