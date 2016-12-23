/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import static core.JSClassRefactor.readIndentationPreference;
import java.io.PrintWriter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author leonardosilva
 */
public class JSClassRefactorTest {
    
    public JSClassRefactorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of main method, of class JSClassRefactor.
     */
    @Test
    public void testMain() {
        //System.out.println("main");
        //String[] args = null;
        //JSClassRefactor.main(args);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
    @Test
    public void testreadIndentationPreference() {
        System.out.println("Testing readIndentationPreference...");
        String result = JSClassRefactor.readIndentationPreference("./test/core/refactorconfig2spaces.ini");
        assertEquals("  ", result);
        result = JSClassRefactor.readIndentationPreference("./test/core/refactorconfig4spaces.ini");
        assertEquals("    ", result);
        result = JSClassRefactor.readIndentationPreference("./test/core/refactorconfigtab.ini");
        assertEquals(""+'\t', result);
        result = JSClassRefactor.readIndentationPreference("./test/core/refactorconfigerror.ini");
        assertEquals("", result);
    }
    
    @Test
    public void testmigrateConstructorFunction() {
        System.out.println("Testing migrateConstructorFunction part 1...");
        String[] contentToWrite;
        //String indentation 
        JSClassRefactor.indentation = readIndentationPreference("./refactorconfig.ini");
        // Tests
        contentToWrite = JSClassRefactor.migrateConstructorFunction("function Point (x, y) {", 5, 5, 8);
        assertEquals("\n", contentToWrite[0]);
        assertEquals("class Point {"+"\n", contentToWrite[1]);
        assertEquals(JSClassRefactor.indentation + "constructor(x, y) {", contentToWrite[2]);
        System.out.println("Testing migrateConstructorFunction part 2...");
        contentToWrite = JSClassRefactor.migrateConstructorFunction("    this.x = x;", 6, 5, 8);
        assertEquals("\n", contentToWrite[0]);
        assertEquals(JSClassRefactor.indentation +"    this.x = x;", contentToWrite[1]);
        System.out.println("Testing migrateConstructorFunction part 3...");
        contentToWrite = JSClassRefactor.migrateConstructorFunction("}", 8, 5, 8);
        assertEquals("\n", contentToWrite[0]);
        assertEquals(JSClassRefactor.indentation +"}\n", contentToWrite[1]);
        assertEquals("}", contentToWrite[2]);
    }
}
