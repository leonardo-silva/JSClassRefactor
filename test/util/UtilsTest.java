/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author leonardosilva
 */
public class UtilsTest {
    
    public UtilsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of argumentsBetweenParentesis method, of class Utils.
     */
    @Test
    public void testArgumentsBetweenParentesis() {
        System.out.println("Testing argumentsBetweenParentesis...");
        String result = Utils.argumentsBetweenParentesis("function Point (x, y) {");
        assertEquals("(x, y) ", result);
        //fail("The test case is a prototype.");
        result = Utils.argumentsBetweenParentesis("function Point(x, y) {");
        assertEquals("(x, y)", result);
        result = Utils.argumentsBetweenParentesis("function Point (x, y){");
        assertEquals("(x, y)", result);
        result = Utils.argumentsBetweenParentesis("function Point(x, y){");
        assertEquals("(x, y)", result);
    }
    
}
