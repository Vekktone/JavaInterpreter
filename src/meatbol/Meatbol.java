package meatbol;

import java.io.IOException;

/** This is a simple driver for the program.
  Command Arguments:
      java Meatbol arg1
             arg1 is the meatbol source file name.
             arg2 is -f flag to print to output file.
  Output:
      Prints EOF to indicate end of source.
  Notes:
      1. This creates a SymbolTable object for symbol recognition.
      2. This uses the Scanner class to get each token from the input file and
               loads the SymbolTable.
      3. Uses the Parser and the getNext method of Scanner to parse input file
              until it returns an empty string.
      3. If an exception is raised, this driver prints information about the
              exception and terminates.

              @author Larry Clark
              @author Gregory Pugh (last modified: 03-28-2019)

 */
public class Meatbol
{
    /** Input filename */
    public static String filename;

    /** Initializes application, determines file output (console or text file),
     *  handles exceptions, and monitors for EOF.
     *  <p>
     *
     * @param args
     * 			Command line parameters
     */
    public static void main(String[] args)
    {
        // Print a column heading - LEGACY
        //System.out.printf("%-11s %-12s %s\n", "primClassif", "subClassif", "tokenStr");
        try {
            filename = args[0];
            // Create the SymbolTable
            SymbolTable symbolTable = new SymbolTable();
            // Create scanner (reads file on creation)
            Scanner scan = new Scanner(args[0], symbolTable);
            // Create parser
            Parser parse = new Parser();

            // Check for flag to print to file instead of console
            if ((args.length == 2) && (args[1].equals("-f")))
            {
                // Let user know what is happening
                System.out.println("Output placed in: './p1Out" + args[0].substring(7) + "'");
                FileHandler.printToFile("./p1Out" + args[0].substring(7));
            }

            // Process file
            while (!scan.getNext().isEmpty())
            {
                //scan.currentToken.printToken(); - LEGACY
                parse.stmt(scan, symbolTable, true);
            }

            // print EOF token so user knows we got to end of source
            scan.currentToken.printToken();

            // Restore output to console
            if ((args.length == 2) && (args[1].equals("-f")))
            {
                FileHandler.printToScreen();
                System.out.println("Output restored to console");
            }
        }

        // Handle ScannerException
        catch(ScannerException e){
            e.toString();
            e.printStackTrace();
        }

        // Handle ParserException
        catch(ParserException e){
            e.toString();
            e.printStackTrace();
        }

        // expected if user fails to provide filename in command arguments
        catch (ArrayIndexOutOfBoundsException e)
        {
            System.out.println("Invalid usage: input filename required.\n"
                    + "Usage: $ ./ [source filemane] <-f>\n"
                    + "\tsource filename is name of file containg source code\n"
                    + "\t-f option redirects output to file, by default, 'p#Input*' becomes 'p#Out*'\n"
                    + "\tdo not use if filename is not in 'p#Input*' form; use command line aurgument\n");
            e.printStackTrace();
        }

        // expected on a syntax errors in meatbol code
        catch (IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        // Handle all other exceptions
        catch (Exception e)
        {
            System.out.println("\nUnknown Error: unexpected error occurred.\n");
            e.printStackTrace();
        }
    }
}
