package meatbol;

/** Parser errors
 *
 * @author Larry Clark
 * @author Gregory M Pugh (modified 24-03-2019)
 */
public class ParserException extends Exception
{
  public int lineNum;
  public String diagnostic;
  public String sourceFileName;

  /** constructor
   *
   * @param lineNum			line number of source file containing error
   * @param diagnostic		description of the error
   * @param sourceFileName	source file containing the error
   */
  public ParserException(int lineNum, String diagnostic, String sourceFileName)
  {
    this.lineNum = lineNum;
    this.diagnostic = diagnostic;
    this.sourceFileName = sourceFileName;
  }

  /** Provides string representation of Exception for printing */
  public String toString()
  {
      StringBuffer sb = new StringBuffer();
      sb.append("Line ");
      sb.append(Integer.toString(lineNum));
      sb.append(" ");
      sb.append(diagnostic);
      sb.append(", File: ");
      sb.append(sourceFileName);
      return sb.toString();
  }
}
