package meatbol;

/** Scanner errors
*
* @author Gregory M Pugh
*/
public class ScannerException extends Exception
{
     public int lineNum;
     public int colNum;
     public String diagnostic;
     public String sourceFileName;

     /** constructor
      *
      * @param lineNum			line number of source file containing error
      * @param colNum			column position of source line containing error
      * @param diagnostic		description of the error
      * @param sourceFileName	source file containing the error
      */
     public ScannerException(int lineNum, int colNum, String diagnostic, String sourceFileName)
     {
           this.lineNum = lineNum;
           this.colNum = lineNum;
           this.diagnostic = diagnostic;
           this.sourceFileName = sourceFileName;
     }

     /** Provides string representation of Exception for printing */
     public String toString()
     {
         StringBuffer sb = new StringBuffer();
         sb.append("Line ");
         sb.append(Integer.toString(lineNum));
         sb.append(" Column ");
         sb.append(Integer.toString(colNum));
         sb.append(" ");
         sb.append(diagnostic);
         sb.append(", File: ");
         sb.append(sourceFileName);
         return sb.toString();
     }
}
