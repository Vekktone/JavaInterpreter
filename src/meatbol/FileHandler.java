package meatbol;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

public class FileHandler {

    /** Stream for standard out (screen)*/
    public static PrintStream ps_Console = System.out;
    /** Stream for file output */
    public static PrintStream ps_File;

    /** Reads meatbol code from text file.
     *  Reads each line until EOF from the named text file and places it in the Scanner's line array.
     *
     * @param fileName    	Name of the meatbol text file.
     *
     * @throws IOException  Created if any error occurs while accessing or reading from file.
     */
    public static ArrayList<String> readFile(String fileName) throws IOException
    {
        ArrayList<String> lineList = new ArrayList<String>();    //array list for lines
        String lineData = null;                                  //Temporary variable for single line from file
        int i = 0;                                               //line index

        try
        {
            //open file for reading
            FileInputStream fileStream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fileStream);
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(in));

            //Read all lines from File
            while ((lineData = bufferRead.readLine()) != null)   {
                lineList.add(lineData);
                i++;
            }

            //close all objects we don't need anymore
            bufferRead.close();
            in.close();
            fileStream.close();
        }

        //Catch exception if any.
        catch (Exception e){
            throw new IOException("Error reading file: " + fileName + "; Line " + i);
        }
        return lineList;
    }

    /** Switches output to file at runtime.
     * <p>
     * Used to redirect standard out to a file. Note: if file exists, overwrites;
     * TODO: add a confirmation message to avoid accidental overwriting.
     *
     * @param fileName		Output filename.
     */
    public static void printToFile(String fileName){
        try
        {
            //set file to output to
            File outputFile = new File(fileName);
            ps_File = new PrintStream(outputFile);
        }
        //catch system errors
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //switch output
        System.setOut(ps_File);
    }

    /** Switches output to screen at runtime.
     * <p>
     * Redirects output to console. Call this after printing to file to restore
     * normal output to screen.
     */
    public static void printToScreen(){
        System.setOut(ps_Console);
    }
}
