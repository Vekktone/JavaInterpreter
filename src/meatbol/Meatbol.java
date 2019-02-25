/*
  This is a simple driver for the first programming assignment.
  Command Arguments:
      java Meatbol arg1
             arg1 is the meatbol source file name.
  Output:
      Prints each token in a table.
  Notes:
      1. This creates a SymbolTable object which doesn't do anything
         for this first programming assignment.
      2. This uses the student's Scanner class to get each token from
         the input file.  It uses the getNext method until it returns
         an empty string.
      3. If the Scanner raises an exception, this driver prints
         information about the exception and terminates.
      4. The token is printed using the Token::printToken() method.
 */
package meatbol;

import java.io.IOException;

public class Meatbol
{
    public static void main(String[] args)
    {
        try {
            // Create the SymbolTable
            SymbolTable symbolTable = new SymbolTable();
            // Create scanner (reads file on creation)
            Scanner scan = new Scanner(args[0], symbolTable);
            //Create parser (not for p2)
            //Parser parse = new Parser();

            //check for flag to print to file instead of console
            if ((args.length == 2) && (args[1].equals("-f")))
            {
                System.out.println("Output placed in: './p1Out" + args[0].substring(7) + "'");
                FileHandler.printToFile("./p1Out" + args[0].substring(7));
            }

            // Print a column heading
            System.out.printf("%-11s %-12s %s\n", "primClassif", "subClassif", "tokenStr");

            // Print output
            while (!scan.getNext().isEmpty())
            {
                scan.currentToken.printToken();
            }
            // print EOF token so user knows we got there
            scan.currentToken.printToken();

            // Example to restore output to console
            if ((args.length == 2) && (args[1].equals("-f")))
            {
                FileHandler.printToScreen();
                System.out.println("Output restored to console");
            }
        }

        // expected if user fails to provide filename in command arguments
        catch (ArrayIndexOutOfBoundsException e)
        {
            System.out.println("Invalid usage: input filename required./n"
                    + "Usage: $ ./ [source filemane] <-f>"
                    + "source filename is name of file containg source code"
                    + "-f option redirects output to file, by default, 'p#Input*' becomes 'p#Out*"
                    + "do not use if filename is not in 'p#Input*' form; use command line aurgument");
            e.printStackTrace();
        }

        // expected on a syntax errors in meatbol code
        catch (IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        // expected if error occurs reading or writing files
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        // this is for anything else that might happen
        catch (Exception e)
        {
            System.out.println("\nUnknown Error: unexpected error occurred.\n");
            e.printStackTrace();
        }
    }
}
