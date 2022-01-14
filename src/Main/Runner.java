
//Authored by Seaghan Ennis for completion of UCCS Senior Capstone
//Some code snippets were provided by instructor 

package Main;
import ADT.*;


public class Runner {

    public static void main(String[] args) {
    	//Open File
    	String filePath = "CodeGenFULL.txt";
        System.out.println("Parsing "+filePath);
        boolean traceon = false; //true; //false;
        Syntactic parser = new Syntactic(filePath, traceon);
        parser.parse();
        
        System.out.println("Done.");
    }
        
}

    

