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
    private static final String END_OF_STRING = "\0";
    private static final String NEW_LINE = "\n";
    public static String indentation; 

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String csvFile = args[0];
        String line = "";
        BufferedReader classBR = null;
        PrintWriter writer = null;
        String cvsSplitBy = ",";
        String lastFileName = null;
        String newFileName = null;
        String lineClass = "";
        int lineClassNumber = 0;
        
        JSClassRefactor.indentation = readIndentationPreference("./refactorconfig.ini");
   
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // The first line (header) is ignored
            br.readLine();

            try {
                while ((line = br.readLine()) != null) {
                    // use comma as separator
                    String[] es5class = line.split(cvsSplitBy);

                    if (lastFileName == null || (!lastFileName.equals(es5class[FILE_NAME]))) {
                        if (classBR != null) {
                            // Copy the rest of the file
                            copyTheRestOfTheLines(lineClass, writer, classBR);
                            // Close the file
                            classBR.close();
                        }    
                        lastFileName = es5class[FILE_NAME]; 
                        classBR = new BufferedReader(new FileReader(lastFileName));
                        lineClassNumber = 0;
                        // File to be created
                        if (writer != null)
                            writer.close();
                        newFileName = lastFileName.replaceAll(".js","-es6cl.js");
                        writer = new PrintWriter(newFileName, "UTF-8");
                    } else {
                        // Treat the last lineClass
                        if (lineClassNumber == Integer.parseInt(es5class[START_LINE])) {
                           lineClassNumber = JSClassRefactor.parseEachClass(lineClass, lineClassNumber, es5class, writer); 
                        } else {
                            // Just copy the line
                            writer.println();
                            writer.print(lineClass);
                        }
                    }

                    if (classBR != null) {
                        while ((lineClass = classBR.readLine()) != null && 
                                lineClassNumber < Integer.parseInt(es5class[END_LINE])) {
                            lineClassNumber = JSClassRefactor.parseEachClass(lineClass, lineClassNumber, es5class, writer);   
                        }
                    }    
                }
            } finally {
                if (classBR != null) {
                    classBR.close();
                }
                if (writer != null) {
                    writer.close();
                }
            }        
                

        } catch (IOException e) {
            e.printStackTrace();
        }   
    }

    private static int parseEachClass (String lineClass, int lineClassNumber, String[] es5class, PrintWriter writer) throws IOException {
        String[] contentToWrite;
        
        lineClassNumber++;
        if (lineClassNumber >= Integer.parseInt(es5class[START_LINE]) && 
                lineClassNumber <= Integer.parseInt(es5class[END_LINE])) {
            // Condition for class constructors
            if (es5class[CLASS_NAME].equals(es5class[FUNCTION_NAME])) {
                contentToWrite = JSClassRefactor.migrateConstructorFunction(lineClass, lineClassNumber, 
                        Integer.parseInt(es5class[START_LINE]), Integer.parseInt(es5class[END_LINE]));
                // Write strings to a file
                JSClassRefactor.writeStringArrayToFile(contentToWrite, writer);
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

    public static String[] migrateConstructorFunction(String lineClass, int lineClassNumber, 
                        int startLine, int endLine) {
        // This method does not generated more than 8 strings
        String[] contentToWrite = new String[8];
        int numberOfStringsToWrite = 0;
        
        if (lineClassNumber == startLine) { 
            String arguments;
            arguments = util.Utils.argumentsBetweenParentesis(lineClass);
            // Changing text
            lineClass = lineClass.replace("function ", "class ");
            lineClass = lineClass.replace(arguments, ""); 
            // Write into the new file
            if (lineClassNumber > 1) { // If it is not the 1st line
                //writer.println();
                contentToWrite[numberOfStringsToWrite] = NEW_LINE;
                numberOfStringsToWrite++;
            }    
            //writer.println(lineClass);
            contentToWrite[numberOfStringsToWrite] = lineClass + NEW_LINE;
            numberOfStringsToWrite++;
            //writer.print(JSClassRefactor.indentation + "constructor" + arguments + "{");
            contentToWrite[numberOfStringsToWrite] = JSClassRefactor.indentation + "constructor" + arguments + "{";
            numberOfStringsToWrite++;
        } else {
            // Closing curly brackets at the end of the class constructor
            if (lineClassNumber == endLine) { 
                //writer.println();
                contentToWrite[numberOfStringsToWrite] = NEW_LINE;
                numberOfStringsToWrite++;
                //writer.println(JSClassRefactor.indentation + "}");
                contentToWrite[numberOfStringsToWrite] = JSClassRefactor.indentation + "}" + NEW_LINE;
                numberOfStringsToWrite++;
                //writer.print(lineClass);
                contentToWrite[numberOfStringsToWrite] = lineClass;
                numberOfStringsToWrite++;
            } else {
                //writer.println();
                contentToWrite[numberOfStringsToWrite] = NEW_LINE;
                numberOfStringsToWrite++;
                //writer.print(JSClassRefactor.indentation + lineClass);
                contentToWrite[numberOfStringsToWrite] = JSClassRefactor.indentation + lineClass;
                numberOfStringsToWrite++;
            }
        }   
        // End of the array
        contentToWrite[numberOfStringsToWrite] = END_OF_STRING;  

        return contentToWrite;
    }

    private static void writeStringArrayToFile(String[] contentToWrite, PrintWriter writer) {
        for(int i = 0; ! contentToWrite[i].equals(END_OF_STRING);i++) {
            writer.print(contentToWrite[i]);
        }
    }

    private static void copyTheRestOfTheLines(String lineClass, PrintWriter writer, BufferedReader classBR) 
            throws IOException {
        while (lineClass != null) {
            writer.println();
            writer.print(lineClass);

            lineClass = classBR.readLine();
        }
    }
}
