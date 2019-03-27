package meatbol;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

/** FileHandler contains all file/disk operations for programmatic use at runtime
 * (vs command line input).
 * <p>
 * Currently, this includes: readFile() - which loads a source text file from
 * the path/filename of the file. printToFile - which prints the output to the
 * specified filename. Provides user with indication that operation was
 * performed. Note, on an error stack trace does not get redirected and prints
 * directly to console. Using -f on command line will generate an output
 * filename in which p#Input*.txt is replaced with p#Out*.txt. printToConsole()
 * - Returns output to console after file operation is completed.
 *
 * @author Gregory Pugh
 */
public class FileHandler
{
    /** Stream for standard out (screen) */
    public static PrintStream ps_Console = System.out;
    /** Stream for file output */
    public static PrintStream ps_File;

    /** Reads meatbol code from text file. Reads each line until EOF from the
     * named text file and places it in the Scanner's line array.
     *
     * @param fileName
     *            Name of the meatbol text file.
     *
     * @throws IOException
     *             Created if any error occurs while accessing or reading from
     *             file.
     *
     * @author Gregory Pugh
     */
    public static ArrayList<String> readFile(String fileName) throws IOException
    {
        // array list for lines
        ArrayList<String> lineList = new ArrayList<String>();
        // Temporary variable for single line from file														//
        String lineData = null;
        // line index
        int i = 0;

        try
        {
            // open file for reading
            FileInputStream fileStream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fileStream);
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(in));

            // Read all lines from File
            while ((lineData = bufferRead.readLine()) != null)
            {
                lineList.add(lineData);
                i++;
            }
            // close all objects we don't need anymore
            bufferRead.close();
            in.close();
            fileStream.close();
        }
        // Catch exception if any.
        catch (Exception e)
        {
            System.out.println("Error reading file: " + fileName + "; Line " + i);
            e.printStackTrace();
        }
        return lineList;
    }

    /** Switches output to file at runtime.
     * <p>
     * Used to redirect standard out to a file. Note: if file exists,
     * overwrites; TODO: add a confirmation message to avoid accidental
     * overwriting.
     *
     * @param fileName
     *            Output filename.
     *
     * @author Gregory Pugh
     */
    public static void printToFile(String fileName)
    {
        try
        {
            // set file to output to
            File outputFile = new File(fileName);
            ps_File = new PrintStream(outputFile);
        }
        // catch system errors
        catch (Exception e)
        {
            e.printStackTrace();
        }
        // switch output
        System.setOut(ps_File);
    }

    /** Switches output to screen at runtime.
     * <p>
     * Redirects output to console. Call this after printing to file to restore
     * normal output to screen.
     *
     * @author Gregory Pugh
     */
    public static void printToScreen()
    {
        System.setOut(ps_Console);
    }
}
