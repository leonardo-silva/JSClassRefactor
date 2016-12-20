/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ini4j.Wini;

/**
 *
 * @author leonardosilva
 */
public class JSClassRefactor {
    private static final int FILE_NAME = 0;
    private static final int CLASS_NAME = 1;
    private static final int FUNCTION_NAME = 2;
    private static final int START_LINE = 3;
    private static final int END_LINE = 4;
    private static String indentation; 

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String csvFile = args[0];
        String line = "";
        
        JSClassRefactor.indentation = readIndentationPreference("./refactorconfig.ini");
   
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // The first line (header) is ignored
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                JSClassRefactor.parseClasses(line);   
            }

        } catch (IOException e) {
            e.printStackTrace();
        }   
    }
    
    private static void parseClasses (String line) throws IOException {
        String cvsSplitBy = ",";
        String lastFileName = null;
        BufferedReader classBR = null;
        PrintWriter writer = null;
        String newFileName = null;
        String lineClass = "";
        int lineClassNumber = 0;

        // use comma as separator
        String[] es5class = line.split(cvsSplitBy);

        try {
            if (lastFileName == null || (!lastFileName.equals(es5class[FILE_NAME]))) {
                if (classBR != null)
                    classBR.close();
                lastFileName = es5class[FILE_NAME]; 
                classBR = new BufferedReader(new FileReader(lastFileName));
                lineClassNumber = 0;
                // File to be created
                if (writer != null)
                    writer.close();
                newFileName = lastFileName.replaceAll(".js","-es6cl.js");
                writer = new PrintWriter(newFileName, "UTF-8");
            }
            
            if (classBR != null) {
                while ((lineClass = classBR.readLine()) != null) {
                    lineClassNumber = JSClassRefactor.parseEachClass(lineClass, lineClassNumber, es5class, writer);   
                }
            }    
        } finally {
            if (classBR != null) {
                try {
                    classBR.close();
                } catch (IOException ce) {
                    ce.printStackTrace();
                }
            }
            if (writer != null) {
                writer.close();
            }
        }        
        
    }

    private static int parseEachClass (String lineClass, int lineClassNumber, String[] es5class, PrintWriter writer) throws IOException {
        lineClassNumber++;
        if (lineClassNumber >= Integer.parseInt(es5class[START_LINE]) && 
                lineClassNumber <= Integer.parseInt(es5class[END_LINE])) {
            // Condition for class constructors
            if (es5class[CLASS_NAME].equals(es5class[FUNCTION_NAME])) {
                JSClassRefactor.migrateConstructorFunction(lineClass, lineClassNumber, 
                        Integer.parseInt(es5class[START_LINE]), Integer.parseInt(es5class[END_LINE]), writer);
            } else {
                // Methods
                writer.println();
                writer.print(lineClass);

            }    
            System.out.println(lineClass);
        } else {
            // Write into the new file
            if (lineClassNumber > 1)  // If it is not the 1st line
                writer.println();
            writer.print(lineClass);
        }    
        
        return lineClassNumber;
    }

    public static String readIndentationPreference(String iniFileName) {
        String indentationSequence = "";
        Wini ini;
        try {
            ini = new Wini(new FileReader(iniFileName));
            String type = ini.get("indentation", "type");
            if (type != null) {
                if (type.equals("spaces")) {
                    int size = ini.get("indentation", "size", int.class);
                    for(int i=0;i<size;i++)
                        indentationSequence += " ";
                } else {
                    indentationSequence = ""+'\t';
                }
            }    
        } catch (IOException ex) {
            Logger.getLogger(JSClassRefactor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return indentationSequence;   
    }

    public static void migrateConstructorFunction(String lineClass, int lineClassNumber, 
                        int startLine, int endLine, PrintWriter writer) {
        String[] contentToWrite = new String[8];
        
        if (lineClassNumber == startLine) { 
            String arguments;
            arguments = util.Utils.argumentsBetweenParentesis(lineClass);
            // Changing text
            lineClass = lineClass.replace("function ", "class ");
            lineClass = lineClass.replace(arguments, ""); 
            // Write into the new file
            if (lineClassNumber > 1) { // If it is not the 1st line
                //writer.println();
                contentToWrite[0] = "/n";
            }    
            //writer.println(lineClass);
            //contentToWrite[1] = 
            writer.print(JSClassRefactor.indentation + "constructor" + arguments + "{");
        } else {
            // Closing curly brackets at the end of the class constructor
            if (lineClassNumber == endLine) { 
                writer.println();
                writer.println(JSClassRefactor.indentation + "}");
                writer.print(lineClass);
            } else {
                writer.println();
                writer.print(JSClassRefactor.indentation + lineClass);
            }
        }   
    }
}
