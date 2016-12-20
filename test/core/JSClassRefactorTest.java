/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

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
        System.out.println("Testing migrateConstructorFunction...");
        String lineClass;
        int lineClassNumber, startLine, endLine;
        PrintWriter writer;
        // 
        
        
        
    }
}
