package meatbol;

import java.util.ArrayList;
import java.util.HashMap;


/** Scanner is responsible for accessing the input file.
 * <p>
 * Scanner reads all lines of the the input file into an array, pre-process
 * data into Tokens and populates the initial Symbol Table. Checks Tokens for
 * type, finds invalid syntax, and makes the current and next Token visible
 * to the Parser for processing.
 *
 * @author Gregory Pugh
 * @author (last modified: 3-28-2019)
 * @author Reviewed by Riley Marfin, Mason Pohler, Gregory Pugh
 */
public class Scanner {

    /** Valid token being processed by the parser */
    public Token currentToken = new Token();

    /** next valid token for parser lookahead */
    public Token nextToken = new Token();

    /** location in the array list of source code lines */
    public int lineIndex = 0;

    /** location in the line string */
    public int columnIndex = 0;

    /** Array list of source code lines */
    public ArrayList<String> lineList = new ArrayList<String>();

    /** Symbol Table object */
    public SymbolTable symbolTable = null;

    /** List of all valid delimiters */
    private final static String delimiters = " \t;:()\'\"=!<>+-*/[]#,^\n";

    /** List of all valid numbers */
    private final static String integers = "0123456789";

    /** Determines when new line has been started */
    private static Boolean newLineDetected = false;

    /** Stores debugger text strings */
    public HashMap<String, Boolean> debugOptionsMap;

    /** Constructor
     * <p>
     * Creates the Scanner object, loads it with the line data from the file,
     * and provides the symbolTable.
     *
     * @param fileName
     *            String representing source filename (include path if located
     *            elsewhere)
     * @param symbolTable
     *            initialized symbol table.
     *
     * @throws Exception
     *            IOException or other Exception for errors while attempting to
     *            read or load file.
     *
     * @author Gregory Pugh
     *
     */
    public Scanner(String fileName, SymbolTable symbolTable) throws Exception
    {
        //TODO: initDebugOptions should probably be in SymbolTable?
        initDebugOptions();
        this.symbolTable = symbolTable;

        try
        {
            // loads each line into the line array list
            this.lineList = FileHandler.readFile(fileName);
            getNext();
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    /** Gets the next token in the source file.
     * <p>
     * Until the end of the file is reached, attempts to create a token
     * starting at the indexed position based on the char that exists at
     * that location. If it cannot find a valid token, throws exception.
     * Tokens are stored as the current token and the next Token for
     * lookahead.
     *
     * @return String
     * 			   representation of token created
     *
     * @throws		Exception if an STFunction is not a user or built-in
     *
     * @author Gregory Pugh
     * @author Riley Marfin (modified: 24-2-2019)
     * @author GregoryPugh (modified: 10-2-2019)
     */
    public String getNext() throws Exception
    {
        //shift next to current
        if(!newLineDetected) {
            currentToken = nextToken;
        }

        // check if we are at end of file
        if (this.lineIndex >= lineList.size())
        {
            // create an EOF token and return empty
            currentToken = setToken("", Classif.EOF, SubClassif.EMPTY, lineIndex, columnIndex);
            return this.currentToken.tokenStr;
        }

        // Prepare to parse a new token
        char[] lineData = this.lineList.get(lineIndex).toCharArray();
        this.nextToken = new Token();
        boolean validToken = false;

        // iterate through line from last position
        for (; columnIndex < lineData.length; columnIndex++)
        {
            // decide the appropriate action based on the next char
            switch (lineData[columnIndex])
            {
                // skip white space
                case '\t': case '\n': case ' ':
                    break;
                // check for comment
                case '/':
                    // see if this is a comment or an error
                    if (lineData[columnIndex + 1] == '/')
                    {
                        // comment skip rest of line
                        columnIndex = lineData.length;
                        break;
                    }
                    // otherwise this must be a division operator
                    else
                    {
                        //create token
                        this.nextToken = setToken(String.valueOf(lineData[columnIndex])
                                , Classif.OPERATOR
                                , SubClassif.EMPTY
                                , lineIndex
                                , columnIndex);
                        validToken = true;
                    }
                    break;
                //create operator for valid operator not inside quotes
                case '+': case '-': case '*': case '<': case '>': case '!': case '=': case '#': case '^':
                    //if we have a minus and it doesn't follow an operand, it must be a unary
                    if(lineData[columnIndex] == '-' && currentToken.primClassif != Classif.OPERAND)
                    {
                        this.nextToken = setToken("u-"
                                , Classif.OPERATOR
                                , SubClassif.EMPTY
                                , lineIndex
                                , columnIndex);
                    }
                    //otherwise, it is an operator
                    else
                    {
                    //create token
                    this.nextToken = setToken(String.valueOf(lineData[columnIndex])
                            , Classif.OPERATOR
                            , SubClassif.EMPTY
                            , lineIndex
                            , columnIndex);
                    }
                    validToken = true;
                    break;
                // create separator for valid separator not inside quotes
                case '(': case ')': case ':': case ';': case '[': case ']': case ',':
                    //create token
                    this.nextToken = setToken(String.valueOf(lineData[columnIndex])
                            , Classif.SEPARATOR
                            , SubClassif.EMPTY
                            , lineIndex
                            , columnIndex);
                    validToken = true;
                    break;
                // opening quote indicates a literal string follows
                case '"': case '\'':
                    //create string Token
                    columnIndex = createStringToken(lineList.get(lineIndex).substring(columnIndex)
                            , lineIndex
                            , columnIndex);
                    validToken = true;
                    break;
                // if we haven't found anything else then this must be an operand
                default:
                    //create operand Token
                    try
                    {
                        columnIndex = createOperand(lineList.get(lineIndex).substring(columnIndex)
                                , lineIndex
                                , columnIndex);
                        validToken = true;
                    }
                    catch (Exception e)
                    {
                        throw e;
                    }
            }
            //verify that we created a new token
            if(validToken)
            {
                //if current token is a potential 2 char operator
                if (currentToken.tokenStr.equals("<")
                        || currentToken.tokenStr.equals(">")
                        || currentToken.tokenStr.equals("!")
                        || currentToken.tokenStr.equals("^")
                        || currentToken.tokenStr.equals("="))
                {
                    //if the next token is an =
                    if (nextToken.tokenStr.equals("="))
                    {
                        this.currentToken = setToken((currentToken.tokenStr + nextToken.tokenStr)
                            , Classif.OPERATOR
                            , SubClassif.EMPTY
                            , lineIndex
                            , columnIndex);

                        //get next token after combining 2 operator, return current Token
                        columnIndex++;
                        newLineDetected = false;
                        this.nextToken = this.currentToken;
                        getNext();
                        return this.currentToken.tokenStr;
                    }
                }
                // Debugging for printing the current token
                if (debugOptionsMap.get(DebuggerTypes.TOKEN))
                {
                    System.out.print("... ");
                    currentToken.printToken();
                }

                //increment position and return the string representation of the token
                columnIndex++;
                newLineDetected = false;
                return this.nextToken.tokenStr;
            }

        }

        // if we reach the end of a line, reset column index and increment line index
        this.columnIndex = 0;
        this.lineIndex++;
        newLineDetected = true;

        //then try to get the first token on the new line
        return this.getNext();
    }

    /** Creates operand that are not a string literal.
     * <p>
     * Checks the remaining line to find the next delimiter. Checks to see if
     * the operand is a valid numeric. Then creates the appropriate token.
     *
     * @param substring
     *            String containing the remaining line of text
     * @param lineNum
     *            current line index
     * @param index
     *            current column index of first char in the string
     *
     * @return column index
     * 			  end of the token's string (used to update position)
     *
     * @throws IllegalArgumentException
     *             for an invalid numeric
     *
     * @throws				IllegalArgumentException for an invalid numeric
     * @author Mason Pohler
     * @author Riley Marfin (modified 17-2-2019)
     * @author Mason Pohler (modified 25-2-2019)
     * @author Mason Pohler (modified 28-3-2019)
     */
    private int createOperand(String substring, int lineNum, int index) throws Exception {
        int i = 0; // loop counter
        SubClassif sub = SubClassif.IDENTIFIER; // SubClassif type (default)
        char[] lineData = substring.toCharArray(); // converts string to char[]

        // check each char in string
        for (i = 0; i < lineData.length; i++)
        {
            // if it is a delimiter, this is the end of the operand
            if (delimiters.contains(String.valueOf(lineData[i])) || lineData[i] == ' ')
                break;
        }

        // trim the string to contain just the operand
        substring = substring.substring(0, i);

        //if the first char is a 0-9, this is a numeric
        if(integers.contains(String.valueOf(lineData[0])))
        {
            // check if the numeric is an integer
            if (isValidInteger(substring))
                sub = SubClassif.INTEGER;
            // check if the numeric is a float
            else if (isValidFloat(substring))
                sub = SubClassif.FLOAT;
            // if it is not an valid numeric, throw exception
            else
                throw new ScannerException(lineNum
                        , index
                        , "Syntax error - Invalid Numeric:  " + substring
                        , Meatbol.filename);

            //create and return token
            nextToken = setToken(substring, Classif.OPERAND, sub, lineNum, index);
            return index + i - 1;
        }

        //Otherwise
        switch(substring)
        {
            // boolean values
            case "T": case "F":
                sub = SubClassif.BOOLEAN;
                nextToken = setToken(substring, Classif.OPERAND, sub, lineNum, index);
                break;

            // either a variable, function, control, or operator. SymbolTable is needed.
            default:
                STEntry entry = symbolTable.getSymbol(substring);

                // If the entry is in the table
                if (entry != null)
                {
                    nextToken = setToken(substring, entry.primClassif, SubClassif.EMPTY, lineNum, index);

                    // Entries need to be handled differently depending on their primary classification.
                    switch (entry.primClassif)
                    {
                        case CONTROL:
                            nextToken.subClassif = ((STControl) entry).subClassif;
                            break;
                        case FUNCTION:
                            nextToken.subClassif = ((STFunction) entry).definedBy;
                            break;
                        case OPERAND:
                            nextToken.subClassif = SubClassif.IDENTIFIER;
                            break;
                        default:
                            // operator or debug
                            break;
                    }
                }
                // not within symbol table
                else
                {
                    // if the symbol is not within the table, then it is either a variable
                    // or a user function and needs to be declared. If the previous token
                    // was not a control declare, then this is an error.
                    if (currentToken.subClassif != SubClassif.DECLARE)
                    {
                        throw new ScannerException(lineNum + 1
                                , index
                                , "Syntax error: Undeclared identifier " + substring
                                , Meatbol.filename);
                    }
                    // if the delimiter is a '(' then this must be a user function.
                    else if (lineData[i] == '(')
                    {
                        SymbolTable functionTable = new SymbolTable();
                        // TODO: Logic for paramlist
                        entry = new STFunction(substring, Classif.FUNCTION, SubClassif.USER, 0
                                , null, functionTable);
                        symbolTable.putSymbol(entry);
                        nextToken = setToken(substring, Classif.FUNCTION, SubClassif.USER, lineNum, index);

                        // TODO: ACTUALLY HANDLE PARAMLIST
                        // placeholder logic for user functions, which do not exist in program 2
                        while (lineData[i-1] != ')')
                        {
                            i++;
                        }
                    }
                    // if the delimiter is not a '(' and this is not in the symbol table already, then
                    // this has to be a variable.
                    else
                    {
                        STEntry declareEntry = symbolTable.getSymbol(currentToken.tokenStr);

                        // Check if the current token has an entry in the symbol table.
                        if (declareEntry != null)
                        {

                            // If the previous token was not a declare, then this variable needs to be declared.
                            if (currentToken.subClassif != SubClassif.DECLARE)
                                throw new ScannerException(currentToken.iSourceLineNr
                                        , currentToken.iColPos
                                        , "Syntax error: Variable not declared " + currentToken.tokenStr
                                        , Meatbol.filename);
                        }
                        // The previous token does not have a symbol within the symbol table and thus cannot be
                        // a declaration type
                        else
                        {
                            throw new ScannerException(currentToken.iSourceLineNr
                                    , currentToken.iColPos
                                    , "Syntax error: Invalid declaration " + currentToken.tokenStr
                                    , Meatbol.filename);
                        }
                        // TODO: structure and param type logic
                        entry = new STIdentifier(substring, Classif.OPERAND
                                , ((STControl) declareEntry).type, null, null, 0);
                        symbolTable.putSymbol(entry);
                        nextToken = setToken(substring, Classif.OPERAND, SubClassif.IDENTIFIER, lineNum, index);
                    }
                }
                break;
        }
        return index + i - 1;
    }

    /** Initializes string to boolean values for use in Debug
     *
     * @author Mason Pohler
     */
    private void initDebugOptions()
    {
        //create and populate
        debugOptionsMap = new HashMap<String, Boolean>();
        debugOptionsMap.put(DebuggerTypes.TOKEN, false);
        debugOptionsMap.put(DebuggerTypes.EXPRESSION, false);
        debugOptionsMap.put(DebuggerTypes.ASSIGNMENT, false);
        debugOptionsMap.put(DebuggerTypes.STATEMENT, false);
    }

    /** Checks if a string is a valid float.
     * <p>
     * Checks each char to see if it is 0-9 or a decimal point. Also makes sure
     * there is exactly one decimal point.
     *
     * @param fValue
     *            String containing potential float value
     *
     * @return boolean true - String is a valid float false - String is not a
     *         valid float
     *
     * @author Riley Marfin
     */
    private boolean isValidFloat(String fValue)
    {
        int i = 0; // loop counter
        char[] iCharacters = fValue.toCharArray(); // converts string to char[]
        boolean hasDecimal = false; // counter for decimal points

        // check if string has at least one decimal
        // redundant if integer is checked first.
        if (!fValue.contains("."))
            return false;
        // check each char in string
        for (i = 0; i < fValue.length(); i++)
        {
            // check if the char is not a 0-9
            if (!(integers.contains(String.valueOf(iCharacters[i]))))
            {
                // System.out.println("problem: " + iCharacters[i]);
                // check if this is the first decimal we have found
                if (iCharacters[i] == '.' && !(hasDecimal))
                    // set decimal flag and continue searching string
                    hasDecimal = true;
                // otherwise, this is not a valid float
                else
                    return false;
            }
        }
        // if we reach the end of the string without chars that are not 0-9 and
        // exactly one decimal, this is a float
        if (hasDecimal)
            return true;
        return false;
    }

    /** Checks if a string is a valid integer value.
     * <p>
     * Checks each char in string to see if it is a 0-9.
     *
     * @param iValue
     *            String containing potential integer value.
     *
     * @return boolean true - string is a valid integer. false - string is not a
     *         valid integer.
     *
     * @author Riley Marfin
     */
    private boolean isValidInteger(String iValue)
    {
        int i = 0; // loop counter
        char[] iCharacters = iValue.toCharArray(); // converts string to char[]

        // check each char in the string
        for (i = 0; i < iValue.length(); i++)
        {
            // if a char is not 0-9, return false
            if (!(integers.contains(String.valueOf(iCharacters[i]))) || iCharacters[i] == '.')
                return false;
        }
        // if the end of string is reached without an invalid char, it is an
        // integer
        return true;
    }

    /** Creates a string literal token.
     * <p>
     * After an opening quotation mark is detected, searches the remaining line
     * string for the first matching closing quotation which is not escaped. If
     * the matching quotation is found, creates a string token with all text
     * between, excluding the beginning and ending quotation marks and removes
     * escape characters. Finds the column position of the end of the string.
     * Throws an exception if no matching quotation is found before the end of
     * the line.
     *
     * @param substring
     *            string beginning with the opening quotation
     * @param lineNum
     *            current line index
     * @param index
     *            current column index
     *
     * @return column index of the end of the token's string (used to update
     *         position)
     *
     * @throws IllegalArgumentException
     *             containing line and text being evaluated
     *
     * @author	Gregory Pugh
     * @author  GregoryPugh (modified: 10-2-2019)
     * @author  Riley Marfin (modified 17-2-2019)
     * @throws ScannerException
     */
    private int createStringToken(String substring, int lineNum, int index) throws ScannerException {
        int trimEscapes = 0;							//counter for escape chars
        int i = 0;										//loop counter
        char[] lineData = substring.toCharArray();		//converts String to char[]
        //char constants for conversion
        char tab = 0x09;
        char newLine = 0x0a;
        char alarmBell = 0x07;

        //search string one char at a time
        for(i = 1; i < lineData.length;i++)
        {
            // if the character is an escape, handle next char
            if (lineData[i] == '\\')
            {
                // check next char
                switch (lineData[i + 1])
                {
                    // if it is a tab
                    case 't':
                        // replace and increment lineData and trimEscape count
                        substring = substring.substring(0, i - trimEscapes) + tab
                                + substring.substring((i - trimEscapes) + 2, lineData.length - trimEscapes);
                        i++;
                        trimEscapes++;
                        break;
                    // this is a new line
                    case 'n':
                        //replace and increment lineData and trimEscape count
                        substring = substring.substring(0,i-trimEscapes) + newLine
                + substring.substring((i-trimEscapes) + 2,lineData.length-trimEscapes);
                        i++;
                        trimEscapes++;
                        break;
                    //this is an alarm bell
                    case 'a':
                        //replace and increment lineData and trimEscape count
                         substring = substring.substring(0,i-trimEscapes) + alarmBell
                 + substring.substring((i-trimEscapes) + 2,lineData.length-trimEscapes);
                         i++;
                         trimEscapes++;
                         break;
                    //this is an escaped quote
                    case '\'': case '"':
                        //replace and increment lineData and trimEscape count
                        substring = substring.substring(0,i-trimEscapes) + lineData[(i+1)]
                + substring.substring((i-trimEscapes)+2,lineData.length-trimEscapes);
                        i++;
                        trimEscapes++;
                        break;
                    //this is a backslash
                    case '\\':
                        //replace and increment lineData and trimEscape count
                        substring = substring.substring(0,i-trimEscapes) + lineData[(i+1)]
                + substring.substring((i-trimEscapes)+2,lineData.length-trimEscapes);
                        i++;
                        trimEscapes++;
                        break;
                    // for other escapes, do nothing yet
                    default:
                        break;
                }
            }
            // if it is not an escape, check if it is the matching quote
            else if (lineData[i] == lineData[0])
            {
                //System.out.println("esacpes "+trimEscapes);
                nextToken = setToken(substring.substring(1, i-trimEscapes)
            , Classif.OPERAND, SubClassif.STRING, lineNum, index);
                break;
            }

        }
        //if we reached the end of the line without finding a match, throw exception and exit
        if(i == lineData.length)
        {
            throw new ScannerException(lineNum + 1
                    , index
                    , "Syntax error - Missing closing quotation: " + substring
                    , Meatbol.filename);
        }
        //Otherwise, create the token and return the new column position after the matching quote
        nextToken = setToken(substring.substring(1, i-trimEscapes)
        , Classif.OPERAND, SubClassif.STRING, lineNum, index);
        return index + i;
    }

    /** Creates new token and sets it to desired values.
     * <p>
     * Allows new token to be created in single line if all values are known.
     *
     * @param value
     *            String value to assign to tokenStr.
     * @param primary
     *            Classif value to assign.
     * @param subclass
     *            SubClassif value to assign.
     * @param lineNum
     *            Index (relative to line array, starts at 0) of the line
     *            containing the token.
     * @param columnNum
     *            Column position of the first char of the token in the line.
     *
     * @return Token with values assigned.
     *
     * @author Gregory Pugh
     */
    public Token setToken(String value, Classif primary, SubClassif subclass, int lineNum, int columnNum)
    {
        Token currentToken = new Token(value);
        currentToken.primClassif = primary;
        currentToken.subClassif = subclass;
        currentToken.iSourceLineNr = lineNum;
        currentToken.iColPos = columnNum;
        return currentToken;
    }

}
