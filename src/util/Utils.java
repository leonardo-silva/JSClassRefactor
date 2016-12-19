/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author leonardosilva
 */
public class Utils {
    
    /**
     *
     * @param line
     * @return
     */
    public static String argumentsBetweenParentesis(String line) {
        String arguments;
        int posOpenParentesis = line.indexOf("(");
        int posCloseParentesis = line.indexOf(")");
        // Test to avoid two spaces after the class name
        if ((line.charAt(posOpenParentesis-1) == ' ') &&
            (line.charAt(posCloseParentesis+1) == ' ')) {
           arguments = line.substring(posOpenParentesis, posCloseParentesis+2);
        }        
        else {
           arguments = line.substring(posOpenParentesis, posCloseParentesis+1);
        }    

        return arguments;
    }
    
}
