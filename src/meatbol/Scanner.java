package meatbol;

import java.util.ArrayList;

public class Scanner {

    /** Valid token being processed by the parser */
    public Token currentToken = null;

    /** next valid token for parser lookahead */
    public Token nextToken = null;

    /** location in the array list of source code lines */
    public int lineIndex = 0;

    /** location in the line string */
    public int columnIndex = 0;

    /** Array list of source code lines */
    public ArrayList<String> lineList = new ArrayList<String>();

    /** Symbol Table object (not currently implemented) */
    public SymbolTable symbolTable = new SymbolTable();

    /** List of all valid delimiters */
    private final static String delimiters = " \t;:()\'\"=!<>+-*/[]#,^\n";

    /** List of all valid numbers */
    private final static String integers = "0123456789";
    
    private static Boolean newLineDetected = false;

    /** Constructor
     * <p>
     * Creates the Scanner object, loads it with the line data from the file,
     * and provides the symbolTable (currently not implemented)
     *
     * @param fileName		String representing source filename (include path if located elsewhere)
     * @param symbolTable	initialized symbol table.
     *
     * @throws Exception	IOException or other Exception for errors while attempting to read
     * 						or load file.
     * @author	Gregory Pugh
     * @author  Reviewed by Riley Marfin, Mason Pohler
     */
    public Scanner(String fileName, SymbolTable symbolTable) throws Exception {
        try
        {
            //loads each line into the line array list
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
     * Until the end of the file is reached, prints each line of source code.
     * Attempts to create a token starting at the indexed position based on the
     * char that exists at that location. If it cannot find a valid token, throws exception.
     *
     * @return		String representation of token created
     *
     * @throws		IllegalArgumentException if it encounters an illegal char
     * 
     * @author  Gregory Pugh
     * @author  Riley Marfin (modified 17-2-2019)
     */
    public String getNext() {
    	
    	if(!newLineDetected) {
        	currentToken = nextToken;
    	}
    	
        //check if we are at end of file
        if(this.lineIndex >= lineList.size()){
            //create an EOF token and return empty
            currentToken = setToken("",Classif.EOF,SubClassif.EMPTY,lineIndex,columnIndex);
            return this.currentToken.tokenStr;
        }

        char[] lineData = this.lineList.get(lineIndex).toCharArray();	//convert current line to char[]
        nextToken = new Token();									//create a new token
        boolean validToken = false;										//flag indicating that current token is
                                                                        //valid

        //if this is first Token
        if(columnIndex == 0){
            //print source line if this is new line
            System.out.println("### Line " + (this.lineIndex + 1) + ": "
                        + this.lineList.get(this.lineIndex));
        }

        //iterate through line from last position
        for(;columnIndex < lineData.length; columnIndex++)
        {
            //decide the appropriate action based on the next char
            switch(lineData[columnIndex])
            {
                //skip white space
                case '\t': case '\n': case ' ':
                    break;
                //check for comment
                case '/':
                    //see if this is a comment or an error
                    if(lineData[columnIndex+1] == '/')
                    {
                        //comment skip rest of line
                        columnIndex = lineData.length;
                        break;
                    }
                    //otherwise this must be a division operator
                    else
                    {
                        this.nextToken = setToken(String.valueOf(lineData[columnIndex])
                                , Classif.OPERATOR
                                , SubClassif.EMPTY
                                , lineIndex
                                , columnIndex);
                    }
                    validToken = true;
                    break;
                //create operator for valid operator not inside quotes
                case '+': case '-': case '*': case '<': case '>': case '!': case '=': case '#': case '^':
                	
                	//check if we have a 2 character operator
                	if (lineData[columnIndex] == '<' || lineData[columnIndex] == '>' || lineData[columnIndex] == '!'
                			|| lineData[columnIndex] == '^' || lineData[columnIndex] == '=') {
                		if (lineData[columnIndex + 1] == '=') {
                			//combine 2 char operator
                            this.nextToken = setToken(String.valueOf(lineData[columnIndex] + String.valueOf(lineData[columnIndex + 1]))
                                    , Classif.OPERATOR
                                    , SubClassif.EMPTY
                                    , lineIndex
                                    , columnIndex);
                            columnIndex++;
                            validToken = true;
                            break;
                		}
                	}
                	
                    this.nextToken = setToken(String.valueOf(lineData[columnIndex])
                            , Classif.OPERATOR
                            , SubClassif.EMPTY
                            , lineIndex
                            , columnIndex);
                    validToken = true;
                    break;
                //create separator for valid separator not inside quotes
                case '(': case ')': case ':': case ';': case '[': case ']': case ',':
                    this.nextToken = setToken(String.valueOf(lineData[columnIndex])
                            , Classif.SEPARATOR
                            , SubClassif.EMPTY
                            , lineIndex
                            , columnIndex);
                    validToken = true;
                    break;
                //opening quote indicates a literal string follows
                case '"': case '\'':
                    columnIndex = createStringToken(lineList.get(lineIndex).substring(columnIndex)
                            , lineIndex, columnIndex);
                    validToken = true;
                    break;
                //if we haven't found anything else then this must be some other type of operand
                default:
                    try
                    {
                        columnIndex = createOperand(lineList.get(lineIndex).substring(columnIndex)
                                , lineIndex, columnIndex);
                        validToken = true;
                    }
                    //if that fails, re-throw throw exception to be caught in main
                    catch(IllegalArgumentException e)
                    {
                        throw e;
                    }
            }

            //verify that we created a new token
            if(validToken)
            {
                //increment position and return the string representation of the token
                columnIndex++;
                newLineDetected = false;
                return this.nextToken.tokenStr;
            }

        }
        //if we reach the end of a line, reset column index and increment line index
        this.columnIndex = 0;
        this.lineIndex++;
        newLineDetected = true;
        //then try to get the first token on the new line
        return this.getNext();
    }

    /** Creates operand that are not a string literal.
     * <p>
     * Checks the remaining line to find the next delimiter. Checks to see
     * if the operand is a valid numeric. Then creates the appropriate token.
     *
     * @param substring		String containing the remaining line of text
     * @param lineNum		current line index
     * @param index			current column index of first char in the string
     *
     * @return				column index of the end of the token's string (used to update position)
     *
     * @throws				IllegalArgumentException for an invalid numeric
     * @author Riley Marfin (modified 17-2-2019)
     */
    private int createOperand(String substring, int lineNum, int index) {
        int i = 0;									//loop counter
        SubClassif sub = SubClassif.IDENTIFIER;		//SubClassif type (default)
        char[] lineData = substring.toCharArray();	//converts string to char[]

        //System.out.println("createOperand reached");

        //check each char in string
        for(i = 0; i < lineData.length;i++)
        {
            //if it is a delimiter, this is the end of the operand
            if(delimiters.contains(String.valueOf(lineData[i]))){
                break;
            }
        }

        //trim the string to contain just the operand
        substring = substring.substring(0, i);
        switch(substring) {
        case "if": case "else": case "for": case "while":
        	sub = SubClassif.FLOW;
            nextToken = setToken(substring, Classif.CONTROL, sub, lineNum, index);
            return index + i - 1;
        case "endif": case "endwhile": case "endfor":
        	sub = SubClassif.END;
            nextToken = setToken(substring, Classif.CONTROL, sub, lineNum, index);
            return index + i - 1;
        case "Int": case "Float": case "String": case "Bool":
        	sub = SubClassif.DECLARE;
            nextToken = setToken(substring, Classif.CONTROL, sub, lineNum, index);
            return index + i - 1;
        case "and": case "or": case "not": case "in": case "notin":
        	sub = SubClassif.EMPTY;
            nextToken = setToken(substring, Classif.OPERATOR, sub, lineNum, index);
            return index + i - 1;
        case "T": case "F":
        	sub = SubClassif.BOOLEAN;
            nextToken = setToken(substring, Classif.OPERATOR, sub, lineNum, index);
            return index + i - 1;
        }
        //if the first char is a 0-9, this is a numeric
        if(integers.contains(String.valueOf(lineData[0])))
        {
            //check if the numeric is an integer
            if(isValidInteger(substring))
            {
                sub = SubClassif.INTEGER;
            }
            //check if the numeric is a float
            else if (isValidFloat(substring))
            {
                sub = SubClassif.FLOAT;
            }
            //if it is not an valid numeric, throw exception
            else
            {
                throw new IllegalArgumentException("Invalid Numeric: Line " + (lineNum + 1) + " - " + substring);
            }
        }
        //Otherwise, create the token and return the new column position after the operand
        nextToken = setToken(substring, Classif.OPERAND, sub, lineNum, index);
        return index + i - 1;
    }

    /** Checks if a string is a valid float.
     * <p>
     * Checks each char to see if it is 0-9 or a decimal point. Also makes sure there
     * is exactly one decimal point.
     *
     * @param fValue	String containing potential float value
     *
     * @return boolean	true - String is a valid float
     * 					false - String is not a valid float
     * @author
     */
    private boolean isValidFloat(String fValue) {
        int i = 0;									//loop counter
        char[] iCharacters = fValue.toCharArray();	//converts string to char[]
        boolean hasDecimal = false;					//counter for decimal points

        //check if string has at least one decimal
        //redundant if integer is checked first.
        if(!fValue.contains("."))
        {
            return false;
        }
        //check each char in string
        for(i = 0; i < fValue.length(); i++)
        {
            //check if the char is not a 0-9
            if(!(integers.contains(String.valueOf(iCharacters[i]))))
            {
                //check if this is the first decimal we have found
                if(iCharacters[i] == '.' && !(hasDecimal))
                {
                    //set decimal flag and continue searching string
                    hasDecimal = true;
                }
                //otherwise, this is not a valid float
                else
                {
                    return false;
                }
            }
        }
        //if we reach the end of the string without chars that are not 0-9 and
        //exactly one decimal, this is a float
        if(hasDecimal)
        {
            return true;
        }
        return false;
    }

    /** Checks if a string is a valid integer value.
     * <p>
     * Checks each char in string to see if it is a 0-9.
     *
     * @param iValue	String containing potential integer value.
     *
     * @return boolean	true - string is a valid integer.
     * 					false - string is not a valid integer.
     */
    private boolean isValidInteger(String iValue) {
        int i = 0;									//loop counter
        char[] iCharacters = iValue.toCharArray();	//converts string to char[]

        //check each char in the string
        for(i = 0; i < iValue.length(); i++)
        {
            //if a char is not 0-9, return false
            if(!(integers.contains(String.valueOf(iCharacters[i]))) || iCharacters[i] == '.')
                return false;
        }
        //if the end of string is reached without an invalid char, it is an integer
        return true;
    }

    /** Creates a string literal token.
     * <p>
     * After an opening quotation mark is detected, searches the remaining line string for
     * the first matching closing quotation which is not escaped. If the matching
     * quotation is found, creates a string token with all text between, excluding
     * the beginning and ending quotation marks and removes escape characters. Finds the
     * column position of the end of the string. Throws an exception if no matching
     * quotation is found before the end of the line.
     *
     * @param substring 	string beginning with the opening quotation
     * @param lineNum		current line index
     * @param index         current column index
     *
     * @return				column index of the end of the token's string (used to update position)
     *
     * @throws				IllegalArgumentException containing line and text being evaluated
     *
     * @author	Gregory Pugh
     * @author  GregoryPugh (modified: 10-2-2019)
     * @author  Riley Marfin (modified 17-2-2019)
     */
    private int createStringToken(String substring, int lineNum, int index) {
        int trimEscapes = 0;							//counter for escape chars
        int i = 0;										//loop counter
        char[] lineData = substring.toCharArray();		//converts String to char[]
        char tab = 0x09;
        char newLine = 0x0a;
        char alarmBell = 0x07;

        //System.out.println("createString reached");

        //search string one char at a time
        for(i = 1; i < lineData.length;i++)
        {
            //System.out.println("At " + i + "esacpes "+trimEscapes);
            //if the character is an escape, handle next char
            if(lineData[i] == '\\')
            {
                //check next char
                switch(lineData[i + 1])
                {
                    //if it is a tab
                    case 't':
                        //replace and increment lineData and trimEscape count
                        substring = substring.substring(0,i-trimEscapes) + tab + substring.substring((i-trimEscapes) + 2,lineData.length-trimEscapes);
                        i++;
                        trimEscapes++;
                        break;
                    //this is a new line
                    case 'n':
                        //replace and increment lineData and trimEscape count
                        substring = substring.substring(0,i-trimEscapes) + newLine + substring.substring((i-trimEscapes) + 2,lineData.length-trimEscapes);
                        i++;
                        trimEscapes++;
                        break;
                    //this is an alarm bell
                    case 'a':
                        //replace and increment lineData and trimEscape count
                         substring = substring.substring(0,i-trimEscapes) + alarmBell + substring.substring((i-trimEscapes) + 2,lineData.length-trimEscapes);
                         i++;
                         trimEscapes++;
                         break;  	
                    //this is an escaped quote
                    case '\'': case '"':
                        //replace and increment lineData and trimEscape count
                        substring = substring.substring(0,i-trimEscapes) + lineData[(i+1)] + substring.substring((i-trimEscapes)+2,lineData.length-trimEscapes);
                        i++;
                        trimEscapes++;
                        break;
                    //this is a backslash
                    case '\\':
                        //replace and increment lineData and trimEscape count
                        substring = substring.substring(0,i-trimEscapes) + lineData[(i+1)] + substring.substring((i-trimEscapes)+2,lineData.length-trimEscapes);
                        i++;
                        trimEscapes++;
                        break;
                    default:
                        break;
                }

            }

            //if it is not an escape, check if it is the matching quote
            else if (lineData[i] == lineData[0])
            {
                //System.out.println("esacpes "+trimEscapes);
                nextToken = setToken(substring.substring(1, i-trimEscapes), Classif.OPERAND, SubClassif.STRING, lineNum, index);
                break;
            }

        }
        //if we reached the end of the line without finding a match, throw exception and exit
        if(i == lineData.length)
        {
            throw new IllegalArgumentException("Syntax Error: missing closing quote at Line " + (lineNum + 1) + " - " + substring);
        }
        //Otherwise, create the token and return the new column position after the matching quote
        nextToken = setToken(substring.substring(1, i-trimEscapes), Classif.OPERAND, SubClassif.STRING, lineNum, index);
        return index + i;
    }

    /** Creates new token and sets it to desired values.
     * <p>
     * Allows new token to be created in single line if all values are known.
     *
     * @param value			String value to assign to tokenStr.
     * @param primary		Classif value to assign.
     * @param subclass		SubClassif value to assign.
     * @param lineNum		Index (relative to line array, starts at 0) of the line containing the token.
     * @param columnNum		Column position of the first char of the token in the line.
     *
     * @return				Token with values assigned.
     */
    public Token setToken(String value, Classif primary, SubClassif subclass, int lineNum, int columnNum){
        Token currentToken = new Token(value);
        currentToken.primClassif = primary;
        currentToken.subClassif = subclass;
        currentToken.iSourceLineNr = lineNum;
        currentToken.iColPos = columnNum;
        return currentToken;
    }

}
