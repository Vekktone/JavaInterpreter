package meatbol;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * The scanner class is used for storing essential information about the file we are scanning. It includes the
 * source file name, an ArrayList consisting of the source lines, a SymbolTable object, a char array used to store each
 * line of text, line number and column number, current token, and next token.
 */
class Scanner {

    // main attributes
    private String sourceFileNm;
    private ArrayList<String> sourceLineM;
    private SymbolTable symbolTable;
    private char textCharM[];
    private int iSourceLineNr;
    private int iColPos;
    public Token currentToken;
    private Token nextToken;


    //static arrays used for testing and classifying tokens
    private final static String delimiters = " \t;:()\'\"=!<>+-*/[]#,^\n";
    private final static String operators = "+-*/<>!=#^";
    private final static String separators = "():;[],";
    private final String numerics = "0123456789";
    private final String floats = "0123456789.";
    private boolean openString = false;
    private boolean closeString = false;


    /**
     * The Scanner constructor takes in a source file name and a SymbolTable object as parameters and initializes the
     * variables.
     * @param sourceFileNm name of source code file
     * @param symbolTable SymbolTable object
     */
    Scanner(String sourceFileNm, SymbolTable symbolTable) {

        this.sourceFileNm = sourceFileNm;
        this.symbolTable = symbolTable;
        this.sourceLineM = new ArrayList<>();

        // Read the specified source file and populate sourceLineM.
        java.util.Scanner scanner = null;
        try {
            scanner = new java.util.Scanner(new File(this.sourceFileNm));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (scanner != null) {
            while(scanner.hasNextLine()) {
                sourceLineM.add(scanner.nextLine());
            }
        }

        // Initializes textCharM, iSourceLineNr, and iColPos.
        this.textCharM = sourceLineM.get(0).toCharArray();
        this.iSourceLineNr = 0;
        this.iColPos = 0;

        // It initializes currentToken and nextToken to new objects (so that the parser doesn't have to check for null).
        this.currentToken = new Token();
        this.nextToken = new Token();

        // It gets the first token into nextToken.

    }

    /**
     * This method gets the next token.  Automatically advances to the next source line when necessary. If there are no more tokens, it returns ""; otherwise, it returns the tokenStr for the current token.  It also sets these attributes in scan.currentToken:
     * tokenStr - the string representation of the token
     * primClassif - the primary classification of the token (OPERAND, OPERATOR)
     * subClassif - the sub classification of the token (e.g., IDENTIFIER, INTEGER)
     * iSourceLineNr - source line number where the token was found
     * iColPos - beginning column position for the token
     *
     * @return a string representation of the current token
     */
    String getNext(){

        //check to see if we are at EOF. If so, set the current token's primClassif to EOF and return the empty string
        if (iSourceLineNr >= sourceLineM.size()){
            currentToken.primClassif = Classif.EOF;
            return "";
        }else {
            textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
        }

        //initialize our stringBuilder object for tokens
        StringBuilder stringBuilder = new StringBuilder();

        //print the line number if we are at the beginning of a new line
        if(iColPos == 0){
            System.out.println("  " + (iSourceLineNr + 1) + " " + sourceLineM.get(iSourceLineNr));
        }

        //skip blank lines and advance iSourceLineM
        while (textCharM.length == 0){
            iSourceLineNr++;
            textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
            System.out.println("\t" + (iSourceLineNr + 1) + " " + sourceLineM.get(iSourceLineNr));
        }

        // Read char array and add to string builder until we get a delimiter
        for (int i = iColPos; i < textCharM.length; i++) {

            //check for delimiter
            if (delimiters.contains(Character.toString(textCharM[i]))) {

                //check if delimiter is double quote for string literal
                if (textCharM[i] == '\"') {
                    openString = true;
                    iColPos++;
                    stringBuilder.setLength(0);
                    // build the double quoted string
                    while (textCharM[iColPos] != '\"') {
                        try {
                            //call the string scanner helper function
                            scanStringLiteral(stringBuilder);
                        } catch (Exception e) {
                            //the string was not terminated
                            System.out.println(e);
                            System.exit(-1);
                        }
                    }
                    closeString = true;
                    iColPos++;
                }

                //check if delimiter is single quote for string literal
                if (textCharM[i] == '\'') {
                    openString = true;
                    iColPos++;
                    stringBuilder.setLength(0);
                    // build the single quoted string
                    while (textCharM[iColPos] != '\'') {
                        try {
                            //call the string scanner helper function
                            scanStringLiteral(stringBuilder);
                        } catch (Exception e) {
                            //the string was not terminated
                            System.out.println(e);
                            System.exit(-1);
                        }
                    }
                    closeString = true;
                    iColPos++;
                }

                //check if string is blank space, and advance column position
                if (textCharM[i] == ' ' || textCharM[i] == '\n' || textCharM[i] == '\t') {
                    iColPos++;
                //else if not blank space char, it is a regular char, and if stringBuilder is empty, it is a separator
                }else if (stringBuilder.toString().isEmpty() && textCharM[i] != '\"' && textCharM[i] != '\''){
                    stringBuilder.append(textCharM[i]);
                    iColPos++;
                }

                //if stringBuilder is not empty, start setting attributes for token
                if (! stringBuilder.toString().isEmpty()) {
                    //convert token to string and classify primary
                    currentToken.tokenStr = stringBuilder.toString();
                    classifyPrimary(currentToken);
                    //attempt to classify secondary, but could throw a syntax error for incorrect code
                    try {
                        classifySecondary(currentToken);
                    } catch (Exception e) {
                        //there was a syntax error
                        System.out.println(e);
                        System.exit(-1);
                    }
                    //set more attributes
                    currentToken.iSourceLineNr = iSourceLineNr;
                    currentToken.iColPos = iColPos;

                    //check if we have a new line
                    if (iColPos >= textCharM.length) {
                        iColPos = 0;
                        iSourceLineNr++;
                    }

                    //reset string detected variables
                    openString = false;
                    closeString = false;

                    //finally return our string
                    return stringBuilder.toString();
                }
            } else {
                stringBuilder.append(Character.toString(textCharM[i]));
                iColPos++;
            }
        }

        //if we have nothing
        return "";
    }

    /**
     * This method classifies a token by using the static arrays defining operators, separators, and operands
     * @param token the current token we are classifying
     */
    private void classifyPrimary(Token token) {

        if (operators.contains(token.tokenStr)){
            token.primClassif = Classif.OPERATOR;
        } else if (separators.contains(token.tokenStr)) {
            token.primClassif = Classif.SEPARATOR;
        } else {
            token.primClassif = Classif.OPERAND;
        }
    }

    /**
     *
     * @param token the current token we are classifying
     * @throws Exception if the numeric value is incorrectly defined (syntax error)
     */
    private void classifySecondary(Token token) throws Exception {

        //if token is numeric, check if it is valid numeric using helper functions
        if (numerics.contains(Character.toString(token.tokenStr.charAt(0)))) {
            if (!isInteger(token.tokenStr) && !isFloat(token.tokenStr)) {
                throw new Exception("Line " + (iSourceLineNr + 1) + " Invalid numeric constant: '" + token.tokenStr + "', File: " + sourceFileNm);
            }
            //else if a quote is detected and it is an operand, it is a string
        } else if (openString && token.primClassif == Classif.OPERAND){
            token.subClassif = SubClassif.STRING;
            //else if it isn't a string and an operand, it is an identifier
        } else if (!openString && token.primClassif == Classif.OPERAND){
            token.subClassif = SubClassif.IDENTIFIER;
            //else it has no secondary classification
        } else {
            token.subClassif = SubClassif.EMPTY;
        }
    }

    /**
     * This method checks if a numeric value is a valid float and returns a boolean value
     * @param tokenStr the current token (possibly float) we are checking
     * @return true if valid float, false if not
     * @param tokenStr the current token (possibly float) we are checking
     * @return true if valid float, false if not
     */
    private boolean isFloat(String tokenStr) {

        //used to verify only one decimal in potential float
        int decimalCount = 0;

        //iterate through token string as char array
        for (char c: tokenStr.toCharArray()) {
            //check if static float array contains every value in the token string. If not, return false.
            if (! floats.contains(Character.toString(c))) {
                return false;
            }

            //increment decimal count when we encounter a '.' character
            if (c == '.'){
                decimalCount++;
                if (decimalCount > 1) return false;
            }
        }
        //if true, classify as float and return
        currentToken.subClassif = SubClassif.FLOAT;
        return true;
    }

    /**
     * This method checks if a numeric value is a valid integer and returns a boolean value
     * @param tokenStr the current token (possibly integer) we are checking
     * @return true if valid int, false if not
     */
    private boolean isInteger(String tokenStr) {

        //iterate through token string as char array
        for (char c: tokenStr.toCharArray()) {
            //check if static numeric array contains every value in the token string. If not, return false.
            if (! numerics.contains(Character.toString(c))) {
                return false;
            }
        }
        //if true, classify as integer and return
        currentToken.subClassif = SubClassif.INTEGER;
        return true;
    }

    /**
     * This method is a helper function for string literals. It scans a token enclosed in single or double quotes
     * and builds the string
     *
     * @param stringBuilder stringBuilder object that is creating our token
     * @throws Exception if the string is not terminated on the appropriate line
     */
    private void scanStringLiteral(StringBuilder stringBuilder) throws Exception {

        //check if the string literal was terminated on the same line. If not, throw exception
        if (iColPos >= textCharM.length-1) {
            throw new Exception("Line " + (iSourceLineNr + 1) + " String literal not terminated: '" + stringBuilder + "', File: " + sourceFileNm);
        }

        //check if we have an escaped character
        if (textCharM[iColPos] == '\\') {
            //switch statement to see what escaped character it is
            switch (textCharM[iColPos + 1]) {
                case '\'':
                    stringBuilder.append('\'');
                    iColPos += 2;
                    break;
                case '\"':
                    stringBuilder.append('\"');
                    iColPos += 2;
                    break;
                case 't':
                    stringBuilder.append('\t');
                    iColPos += 2;
                    break;
                case 'n':
                    stringBuilder.append('\n');
                    iColPos += 2;
                    break;
                default:
                    stringBuilder.append('\\');
                    iColPos++;
                    break;
            }
            //if char is a regular character, append to stringBuilder
        } else {
            stringBuilder.append(textCharM[iColPos]);
            iColPos++;
        }
    }
}
