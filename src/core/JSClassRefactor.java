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

        JSClassRefactor.indentation = readIndentationPreference("./refactorconfig.ini");
        
        generateClasses(csvFile);
        mixClassesAndFunctions(csvFile);
    }

    /**
     * Generate the class structures (ES5 -> ES6 syntax)
     */
    public static void generateClasses(String csvFile) {
        String line = "";
        BufferedReader classBR = null;
        PrintWriter writer = null;
        String cvsSplitBy = ",";
        String lastFileName = null, lastClassName = null;
        String newFileName = null;
        String lineClass = "";
        int lineClassNumber = 0;
        
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
                            //copyTheRestOfTheLines(lineClass, writer, classBR);
                            // Close the file
                            classBR.close();
                        }    
                        lastFileName = es5class[FILE_NAME]; 
                        classBR = new BufferedReader(new FileReader(lastFileName));
                        lineClassNumber = 0;
                        // File to be created
                        if (writer != null) {
                            writeClassEnding(lastClassName, writer);
                            writer.close();
                        }    
                        lastClassName = es5class[CLASS_NAME];
                        newFileName = lastFileName.replaceAll(".js","-es6classes.js");
                        writer = new PrintWriter(newFileName, "UTF-8");
                    } else {
                        if (!lastClassName.equals(es5class[CLASS_NAME])) {
                            writeClassEnding(lastClassName, writer);
                            lastClassName = es5class[CLASS_NAME];
                        }
                        // Treat the last lineClass
                        //if (lineClassNumber == Integer.parseInt(es5class[START_LINE])) {
                        //   JSClassRefactor.parseEachClass(lineClass, lineClassNumber, es5class, writer); 
                        //} else {
                            // Just copy the line
                            //writer.println();
                            //writer.print(lineClass);
                        //}
                    }

                    if (classBR != null) {
                        lineClassNumber++;
                        lineClass = classBR.readLine();
                        while ((lineClass != null) && 
                                lineClassNumber <= Integer.parseInt(es5class[END_LINE])) {
                            JSClassRefactor.parseEachClass(lineClass, lineClassNumber, es5class, writer);   

                            lineClassNumber++;
                            lineClass = classBR.readLine();
                        }
                    }    
                }
            } finally {
                if (classBR != null) {
                    classBR.close();
                }
                if (writer != null) {
                    writeClassEnding(lastClassName, writer);
                    writer.close();
                }
            }        
                

        } catch (IOException e) {
            e.printStackTrace();
        }   
    }

    /**
     * Generate the class structures (ES5 -> ES6 syntax)
     */
    public static void mixClassesAndFunctions(String csvFile) {
        String line = "";
        BufferedReader es5BR = null;
        BufferedReader es6classesBR = null;
        PrintWriter writer = null;
        String cvsSplitBy = ",";
        String lastFileName = null;
        String newFileName = null;
        String classFileName = null;
        String lineClass = "";
        int lineClassNumber = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // The first line (header) is ignored
            br.readLine();

            try {
                while ((line = br.readLine()) != null) {
                    // use comma as separator
                    String[] es5class = line.split(cvsSplitBy);

                    if (lastFileName == null || (!lastFileName.equals(es5class[FILE_NAME]))) {
                        if (es5BR != null) {
                            // Copy the rest of the file
                            copyTheRestOfTheLines(writer, es5BR);
                            // Close the file
                            es5BR.close();
                        }    
                        lastFileName = es5class[FILE_NAME]; 
                        es5BR = new BufferedReader(new FileReader(lastFileName));
                        lineClassNumber = 0;
                        // Class file
                        if (es6classesBR != null) {
                            // Close the file
                            es6classesBR.close();
                        }    
                        classFileName = lastFileName.replaceAll(".js","-es6classes.js");
                        es6classesBR = new BufferedReader(new FileReader(classFileName));
                        // File to be created
                        if (writer != null)
                            writer.close();
                        newFileName = lastFileName.replaceAll(".js","-es6final.js");
                        writer = new PrintWriter(newFileName, "UTF-8");
                    }

                    if (es5BR != null) {
                        while ((lineClass = es5BR.readLine()) != null) {
                            lineClassNumber++;
                            if (es5class[CLASS_NAME].equals(es5class[FUNCTION_NAME]) &&
                                lineClassNumber == Integer.parseInt(es5class[START_LINE])) {
                                JSClassRefactor.copyClassStructure(es5class, writer, es6classesBR);
                                // Position at the end of the constructor to avoid copying it
                                while (lineClassNumber < Integer.parseInt(es5class[END_LINE])) {
                                    lineClass = es5BR.readLine();
                                    lineClassNumber++;
                                }
                                break;
                            } else {
                                if (lineClassNumber < Integer.parseInt(es5class[START_LINE]) ||
                                 lineClassNumber > Integer.parseInt(es5class[END_LINE])   ) {
                                    // Just copy the line
                                    if (lineClassNumber > 1)
                                        writer.println();
                                    writer.print(lineClass);
                                } else {
                                    // Position at the end of the method to avoid copying it 
                                    while (lineClassNumber < Integer.parseInt(es5class[END_LINE])) {
                                        lineClass = es5BR.readLine();
                                        lineClassNumber++;
                                    }
                                    break;
                                }   
                            }
                        }
                    }    
                }
            } finally {
                if (es5BR != null) {
                    es5BR.close();
                }
                if (es6classesBR != null) {
                    es6classesBR.close();
                }
                if (writer != null) {
                    writer.close();
                }
            }        
                

        } catch (IOException e) {
            e.printStackTrace();
        }   
    }

    private static void parseEachClass (String lineClass, int lineClassNumber, String[] es5class, 
            PrintWriter writer) throws IOException {
        String[] contentToWrite;
        
//        lineClassNumber++;
        // Condition for class constructors
        if (es5class[CLASS_NAME].equals(es5class[FUNCTION_NAME])) {
            if (lineClassNumber >= Integer.parseInt(es5class[START_LINE]) && 
                    lineClassNumber <= Integer.parseInt(es5class[END_LINE])) {
                contentToWrite = JSClassRefactor.migrateConstructorFunction(lineClass, lineClassNumber, 
                        Integer.parseInt(es5class[START_LINE]), Integer.parseInt(es5class[END_LINE]));
                // Write strings to a file
                JSClassRefactor.writeStringArrayToFile(contentToWrite, writer);
            //} else {
                // Write the lines (any) that come before the class constructor
              //  if (lineClassNumber > 1)  // If it is not the 1st line
                //    writer.println();
               // writer.print(lineClass);
            }    
        } else {
            if (lineClassNumber >= Integer.parseInt(es5class[START_LINE]) && 
                    lineClassNumber <= Integer.parseInt(es5class[END_LINE])) {
                // Methods
                contentToWrite = JSClassRefactor.migrateMethods(lineClass, lineClassNumber, es5class);
                // Write strings to a file
                JSClassRefactor.writeStringArrayToFile(contentToWrite, writer);
                //writer.println();
                //writer.print(JSClassRefactor.indentation + lineClass);
            } else {
                // Do not do anything until the class structure is complete
            }  
        }    
        
        //return lineClassNumber;
    }

/*    
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
*/

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
                //contentToWrite[numberOfStringsToWrite] = lineClass;
                //numberOfStringsToWrite++;
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

    public static String[] migrateMethods(String lineClass, int lineClassNumber, 
                         String[] es5class) {
        // This method does not generated more than 8 strings
        String[] contentToWrite = new String[8];
        int numberOfStringsToWrite = 0;
        int startLine = Integer.parseInt(es5class[START_LINE]);
        //int endLine = Integer.parseInt(es5class[END_LINE]);
        String functionName = es5class[FUNCTION_NAME];
        
        if (lineClassNumber == startLine) { 
            String arguments;
            arguments = util.Utils.argumentsBetweenParentesis(lineClass);
            // Changing text
            lineClass = functionName + arguments + " {";
            // Write into the new file
            if (lineClassNumber > 1) { // If it is not the 1st line
                //writer.println();
                contentToWrite[numberOfStringsToWrite] = NEW_LINE;
                numberOfStringsToWrite++;
            }    
            //writer.println(lineClass);
            contentToWrite[numberOfStringsToWrite] = JSClassRefactor.indentation + lineClass;
            numberOfStringsToWrite++;
        } else {
            contentToWrite[numberOfStringsToWrite] = NEW_LINE;
            numberOfStringsToWrite++;
            //writer.print(JSClassRefactor.indentation + lineClass);
            contentToWrite[numberOfStringsToWrite] = JSClassRefactor.indentation + lineClass;
            numberOfStringsToWrite++;
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

    private static void copyTheRestOfTheLines(PrintWriter writer, BufferedReader classBR) 
            throws IOException {
        String lineClass;
        while ((lineClass = classBR.readLine()) != null) {
            writer.println();
            writer.print(lineClass);
        }
    }

    private static void writeClassEnding(String lastClassName, PrintWriter writer) {
        writer.println();
        writer.print("} // end of class " + lastClassName);
    }

    private static void copyClassStructure(String[] es5class, PrintWriter writer, BufferedReader es6classesBR)
        throws IOException {
        
        String lineClassFile = es6classesBR.readLine();
        boolean classFound = false, startWriting = false;

        while (lineClassFile != null && !classFound) {
            // Look for class structure
            if (!startWriting && lineClassFile.contains("class " + es5class[CLASS_NAME] + " {"))
                startWriting = true;
            
            if (startWriting) {
                writer.println();
                writer.print(lineClassFile);
            }
        
            lineClassFile = es6classesBR.readLine();
            if (lineClassFile.contains("end of class " + es5class[CLASS_NAME])) {
                JSClassRefactor.writeClassEnding(es5class[CLASS_NAME], writer);
                classFound = true;
            }    
        }
    }
}
