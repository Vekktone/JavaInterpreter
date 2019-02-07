package meatbol;

import meatbol.SymbolTable;

import java.io.File;
import java.util.*;

public class Scanner
{

    private final static String delimiters = "\t;:()\'\"=!<>+-*/[]#,^\n";
    private final static String operators = "=!<>+-*/";
    private final static String separators = "(),:;[]";
    private final static String numbers = "0123456789";
    private final static String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String sourceFileNm;
    public ArrayList<String> sourceLineM;
    public SymbolTable symbolTable;
    public char[] textCharM;
    public int iSourceLineNr;
    public int iColPos;
    public Token currentToken;
    public Token nextToken;
    public int lastPrintedLine;

    /**
     * The constructor for the scanner object. It has a side effect of putting the first token in
     * the next token.
     * @param sourceFileNm The filename for the input file.
     * @param symbolTable The symbol table used by the interpreter.
     * @throws Exception In order to provide error messages about invalid syntax,
     * and in case the input file is not found.
     */
    public Scanner(String sourceFileNm, SymbolTable symbolTable) throws Exception
    {
        this.sourceFileNm = sourceFileNm;
        this.symbolTable = symbolTable;
        sourceLineM = new ArrayList<String>();

        File file = new File(sourceFileNm);
        java.util.Scanner fileScanner = new java.util.Scanner(file);

        // Put all the lines from the input file into the source line ArrayList.
        while (fileScanner.hasNextLine())
        {
            sourceLineM.add(fileScanner.nextLine());
        }

        iSourceLineNr = 0;
        lastPrintedLine = -1;
        iColPos = 0;
        textCharM = sourceLineM.get(iSourceLineNr).toCharArray();

        currentToken = new Token();
        nextToken = new Token();

        getNext();
    }

    /**
     * Gets the information for the next token and returns the string for the current token.
     * @return The current token's token string.
     * @throws Exception In order to provide error messages about invalid syntax.
     */
    public String getNext() throws Exception
    {

        // Print any lines between the current token and next token. Also prints the first line.
        while (nextToken.iSourceLineNr > lastPrintedLine && nextToken.iSourceLineNr < sourceLineM.size())
        {
            lastPrintedLine++;
            System.out.printf("%3d %s\n", (lastPrintedLine+1), sourceLineM.get(lastPrintedLine));
        }

        currentToken.primClassif = nextToken.primClassif;
        currentToken.subClassif = nextToken.subClassif;
        currentToken.tokenStr = nextToken.tokenStr;
        currentToken.iSourceLineNr = nextToken.iSourceLineNr;
        currentToken.iColPos = nextToken.iColPos;

        // There is no next token, the current token is the last one.
        if (currentToken.primClassif == Classif.EOF)
        {
            return "";
        }

        // The column position might have been incremented past the current line. If so
        // we have to move to the next line.
        if (iColPos >= textCharM.length)
        {
            iSourceLineNr++;
            if (iSourceLineNr == sourceLineM.size())
            {
                nextToken.primClassif = Classif.EOF;
                nextToken.subClassif = SubClassif.EMPTY;
                nextToken.iColPos = iColPos;
                nextToken.iSourceLineNr = iSourceLineNr;
                nextToken.tokenStr = "";
                return currentToken.tokenStr;
            }
            textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
            iColPos = 0;
        }

        textCharM = sourceLineM.get(iSourceLineNr).toCharArray();

        // skip white space and blank lines.
        while (textCharM.length == 0 || (iColPos < textCharM.length
                && (textCharM[iColPos] == ' ' || textCharM[iColPos] == '\t' || textCharM[iColPos] == '\n')))
        {

            iColPos++;

            // The column position might have been incremented past the current line. If so
            // we have to move to the next line.
            if (iColPos >= textCharM.length && iSourceLineNr < sourceLineM.size())
            {
                iSourceLineNr++;

                // We might have blank space leading to the end of the file. If so,
                // we know the next token is EOF.
                if (iSourceLineNr == sourceLineM.size())
                {
                    nextToken.primClassif = Classif.EOF;
                    nextToken.subClassif = SubClassif.EMPTY;
                    nextToken.iColPos = iColPos;
                    nextToken.iSourceLineNr = iSourceLineNr;
                    nextToken.tokenStr = "";
                    return currentToken.tokenStr;
                }
                textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
                iColPos = 0;
            }
        }

        int delimiterIndex = delimiters.indexOf(textCharM[iColPos]);
        int operatorIndex = operators.indexOf(textCharM[iColPos]);
        int separatorIndex = separators.indexOf(textCharM[iColPos]);
        int numberIndex = numbers.indexOf(textCharM[iColPos]);
        int letterIndex = letters.indexOf(textCharM[iColPos]);

        // How to treat the token if it is a delimiter
        if (delimiterIndex >= 0)
        {
            // How to treat the token if it is an operator
            if (operatorIndex >= 0)
            {
                nextToken.primClassif = Classif.OPERATOR;
                nextToken.subClassif = SubClassif.EMPTY;
                nextToken.iColPos = iColPos;
                nextToken.iSourceLineNr = iSourceLineNr;
                nextToken.tokenStr = "" + textCharM[iColPos];
                iColPos++;
            }

            // How to treat the token if it is a separator
            else if (separatorIndex >= 0)
            {
                nextToken.primClassif = Classif.SEPARATOR;
                nextToken.subClassif = SubClassif.EMPTY;
                nextToken.iColPos = iColPos;
                nextToken.iSourceLineNr = iSourceLineNr;
                nextToken.tokenStr = "" + textCharM[iColPos];
                iColPos++;
            }

            // How to treat the token if it is a double quote
            else if (textCharM[iColPos] == '\"')
            {
                nextToken.primClassif = Classif.OPERAND;
                nextToken.subClassif = SubClassif.STRING;
                int iStartPos = iColPos;
                iColPos++;
                nextToken.iColPos = iColPos;
                nextToken.iSourceLineNr = iSourceLineNr;
                boolean found = false;

                // skip all non double quote characters to find the end of the string
                while (iColPos < textCharM.length && textCharM[iColPos] != '\"')
                {

                    // skip escape sequences so we dont confuse them for double quotes
                    if (textCharM[iColPos] == '\\')
                    {
                        iColPos++;
                    }
                    iColPos++;

                    // mark the double quote as found if it was found.
                    if (iColPos < textCharM.length && textCharM[iColPos] == '\"')
                    {
                        found = true;
                    }
                }

                nextToken.tokenStr = new String(textCharM).substring(iStartPos+1, iColPos);
                iColPos++;

                // the string is not properly terminated if the end of line is encountered
                // before a second double quote
                if (!found)
                {
                    throw new Exception("Line " + (iSourceLineNr+1) + " non-terminated string " + nextToken.tokenStr);
                }
            }

            // How to treat the token if it is a single quote
            else if (textCharM[iColPos] == '\'')
            {
                nextToken.primClassif = Classif.OPERAND;
                nextToken.subClassif = SubClassif.STRING;
                int iStartPos = iColPos;
                iColPos++;
                nextToken.iColPos = iColPos;
                nextToken.iSourceLineNr = iSourceLineNr;
                boolean found = false;

                // skip all non single quote characters to find the end of the string
                while (iColPos < textCharM.length && textCharM[iColPos] != '\'')
                {

                    // skip escape sequences so we dont confuse them for single quotes
                    if (textCharM[iColPos] == '\\')
                    {
                        iColPos++;
                    }
                    iColPos++;

                    // mark the single quote as found if it was found.
                    if (iColPos < textCharM.length && textCharM[iColPos] == '\'')
                    {
                        found = true;
                    }
                }

                // the string is not properly terminated if the end of line is encountered
                // before a second single quote
                if (!found)
                {
                    throw new Exception("Line " + (iSourceLineNr+1) + " non-terminated string " + nextToken.tokenStr);
                }

                nextToken.tokenStr = new String(textCharM).substring(iStartPos+1, iColPos);
                iColPos++;
            }
        }

        // How to treat the token if it begins with a number
        else if (numberIndex >= 0)
        {
            nextToken.primClassif = Classif.OPERAND;
            nextToken.iColPos = iColPos;
            nextToken.iSourceLineNr = iSourceLineNr;
            int iStartPos = iColPos;
            int numDecimals = 0;
            boolean invalid = false;

            // Skip column positions until the next white space or terminator in order to
            // find the end of the number
            while (iColPos < textCharM.length && delimiters.indexOf(textCharM[iColPos]) == -1
                    && textCharM[iColPos] != ' ')
            {

                // count the number of decimals
                if (textCharM[iColPos] == '.')
                {
                    numDecimals++;
                }

                // make sure only numbers or decimals are within this token
                else if (numbers.indexOf(textCharM[iColPos]) < 0)
                {
                    invalid = true;
                }
                iColPos++;
            }

            // if a number has more than one decimal, it is invalid
            if (numDecimals > 1)
            {
                invalid = true;
            }

            // determine if the token is an integer or float based on if it has a decimal within it
            nextToken.subClassif = numDecimals == 0 ? SubClassif.INTEGER : SubClassif.FLOAT;

            nextToken.tokenStr = new String(textCharM).substring(iStartPos, iColPos);

            // An error must be given if the numeric constant is invalid
            if (invalid)
            {
                throw new Exception("Line " + (iSourceLineNr+1) + " invalid numeric constant " + nextToken.tokenStr);
            }
        }

        // How to treat the token if it begins with a letter
        else if (letterIndex >= 0)
        {
            nextToken.primClassif = Classif.OPERAND;
            nextToken.subClassif = SubClassif.IDENTIFIER;
            nextToken.iColPos = iColPos;
            nextToken.iSourceLineNr = iSourceLineNr;
            int iStartPos = iColPos;

            // skip column positions until the next space or delimiter
            while (iColPos < textCharM.length && delimiters.indexOf(textCharM[iColPos]) == -1
                    && textCharM[iColPos] != ' ')
            {
                iColPos++;
            }
            nextToken.tokenStr = new String(textCharM).substring(iStartPos, iColPos);
        }

        // Could not recognize the token, so we need an error
        else
        {
            throw new Exception("Line " + (iSourceLineNr+1) + " unrecognized token "
                    + new String(textCharM).substring(iColPos));
        }

        return currentToken.tokenStr;
    }
}