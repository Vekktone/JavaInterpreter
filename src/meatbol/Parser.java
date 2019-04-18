package meatbol;

import java.lang.reflect.Array;
import java.util.*;

public class Parser
{
    public Parser(){

    }

    /** Parser reads tokens from Scanner and executes the source file.
     *
     *
     * @param scan
     * 			Scanner object providing tokens to be executed
     * @param symbolTable
     * 			Contains additional token information
     * @param bExec
     * 			Determines whether or not each statement is executed during
     * 			certain control statements
     *
     * @throws Exception
     *
     * @author Gregory Pugh
     * @author Riley Marfin (modified 27-3-2019)
     * @author Mason Pohler (modified 28-3-2019)
     */
    public void stmt(Scanner scan, SymbolTable symbolTable, Boolean bExec) throws Exception
    {
        //print current source file line being process
        if (scan.debugOptionsMap.get(DebuggerTypes.STATEMENT))
        {
            printStatement(scan, scan.currentToken.iSourceLineNr);
        }

        String dataType = scan.currentToken.tokenStr;
        //Determine type of token
        switch(scan.currentToken.primClassif)
        {
            // shouldn't see this, but if it occurs skip it
            case EMPTY:
                System.out.println("***Warning: empty token detected***");
                break;
            // shouldn't see this, but if it occurs skip it
            case EOF:
                System.out.println("***Warning: EOF token detected***");
                break;
            // control statement
            case CONTROL:
                conStmt(scan, symbolTable, bExec, dataType);
                break;
            // function statement
            case FUNCTION:
                funcStmt(scan, symbolTable, bExec);
                break;
            // assignment statement
            case OPERAND:
                assignStmt(scan, symbolTable, bExec);
                break;
            // debug statement
            case DEBUG:
                debugStmt(scan, symbolTable, bExec);
                break;
            // statements can't begin with these, throw error
            case OPERATOR:
            case SEPARATOR:
                throw new ParserException(scan.currentToken.iSourceLineNr + 1
                        ,"***Error: Illegal start to statement***"
                        , Meatbol.filename);
            // Unknown state, throw error
            default:
                throw new ParserException(scan.currentToken.iSourceLineNr + 1
                        ,"***Error: unknown state***"
                        , Meatbol.filename);
        }
    }

    /** Determines what type of control statement we have.
     * <p>
     * Currently allows DECLARE, IF, and WHILE.
     * FOR and DEF not yet implemented.
     *
     * @param scan
     * 			Scanner object containing providing source file tokens
     * @param symbolTable
     *			Contains additional token information
     *
     * @throws Exception
     *
     * @author Gregory Pugh
     */
    public void conStmt(Scanner scan, SymbolTable symbolTable, Boolean bExec, String dataType) throws Exception
    {
        //check what type of control statement we have
        switch(scan.currentToken.subClassif)
        {
        //declaring new primitive identifier
        case DECLARE:
            //Verify scanner added it to symbolTable
            if(symbolTable.getSymbol(scan.nextToken.tokenStr) == null)
            {
                throw new ParserException(scan.currentToken.iSourceLineNr + 1
                        ,"***Error: unknown identifier***"
                        , Meatbol.filename);
            }
            scan.getNext();
            //see if a value was assigned
            if(scan.nextToken.tokenStr.equals("="))
            {
                //treat this like any other assignment
                assignStmt(scan, symbolTable, bExec);
            }
            else if(scan.nextToken.tokenStr.equals(";"))
            {
                //declared only, we are done
                scan.getNext();

            }
            else if(scan.nextToken.tokenStr.equals("["))
            {
                // this is an array declaration
//                System.out.println("ARRAY DETECTED BEEP BOOP");
                Token arrayIdentifier = scan.currentToken;
                scan.getNext();
                // case 1: if it is an array declaration with empty set of brackets
                if (scan.nextToken.tokenStr.equals("]"))
                {
//                    System.out.println("case 1: if it is an array declaration with empty set of brackets");
                    scan.getNext();
                    // there must be an equals after the brackets or we have an error
                    if (scan.nextToken.tokenStr.equals("="))
                    {
                        assignArray(scan, symbolTable, bExec, arrayIdentifier, null, 1, dataType);
                    }
                    else
                    {
                        //anything else is a syntax error
                        throw new ParserException(scan.currentToken.iSourceLineNr
                                ,"***Error: Expected list of values after array declaration with empty brackets***"
                                , Meatbol.filename);
                    }

                }
                else
                {
                    // evaluate condition inside array brackets
                    ResultValue resultValue = expression(scan, symbolTable);

                    // case 2: if it is an array declaration with a valid size and no valueList
                    if (scan.nextToken.tokenStr.equals(";"))
                    {
//                        System.out.println("case 2: if it is an array declaration with a valid size and no valueList");
                        // assignment with no valueList
                        assignArray(scan, symbolTable, bExec, arrayIdentifier, resultValue, 2, dataType);
                    } else if (scan.nextToken.tokenStr.equals("="))
                    {
                        // case 3: if it is an array declaration with a valid size and a valueList
//                        System.out.println("case 3: if it is an array declaration with a valid size and a valueList");
                        assignArray(scan, symbolTable, bExec, arrayIdentifier, resultValue, 3, dataType);
                    }
                }
            }
            else
            {
                //anything else is a syntax error
                throw new ParserException(scan.currentToken.iSourceLineNr + 1
                        ,"***Error: Expected end of statement or assignment***"
                        , Meatbol.filename);
            }
            break;
        //if we are here, then we are missing matching FLOW
        case END:
            switch(scan.currentToken.tokenStr)
            {
            case "enddef":
                throw new ParserException(scan.currentToken.iSourceLineNr + 1
                        ,"***Error: enddef without def***"
                        , Meatbol.filename);
            case "endif":
                throw new ParserException(scan.currentToken.iSourceLineNr + 1
                        ,"***Error: endif without if***"
                        , Meatbol.filename);
            case "else":
                throw new ParserException(scan.currentToken.iSourceLineNr + 1
                        ,"***Error: else without if***"
                        , Meatbol.filename);
            case "endfor":
                throw new ParserException(scan.currentToken.iSourceLineNr + 1
                        ,"***Error: endfor without for***"
                        , Meatbol.filename);
            case "endwhile":
                throw new ParserException(scan.currentToken.iSourceLineNr + 1
                        ,"***Error: endwhile without while***"
                        , Meatbol.filename);
            default:
                throw new ParserException(scan.currentToken.iSourceLineNr + 1
                        ,"***Error: unknown state***"
                        , Meatbol.filename);
            }
        //begin new control statement
        case FLOW:
            switch(scan.currentToken.tokenStr)
            {
            case "if":
                ifStmt(scan, symbolTable, bExec);
                break;
            case "while":
                whileStmt(scan, symbolTable, bExec);
                break;
            case "def":
                defStmt(scan, symbolTable);
                break;
            case "for":
                forStmt(scan, symbolTable);
                break;
            default:
                break;
            }
            break;
        //something went wrong
        default:
            throw new ParserException(scan.currentToken.iSourceLineNr + 1
                    ,"***Error: Unknown state***"
                    , Meatbol.filename);
        }
    }

    private void assignArray(Scanner scan, SymbolTable symbolTable, Boolean bExec, Token arrayIdentifier, ResultValue arraySize, int declarationType, String dataType) throws Exception {
        STIdentifier arraySTEntry = (STIdentifier) symbolTable.getSymbol(arrayIdentifier.tokenStr);
        arraySTEntry.structure = SubClassif.ARRAY;
        symbolTable.table.put(arrayIdentifier.tokenStr, arraySTEntry);

        int i;
        StringBuilder sb;
        SubClassif arrayType;

        if (bExec)
        {

            ArrayList<ResultValue> arrayValues;

            // check data type for array
            if (dataType.equals("Int"))
            {
                arrayType = SubClassif.INTEGER;
            } else if (dataType.equals("Float"))
            {
                arrayType = SubClassif.FLOAT;
            } else if (dataType.equals("Boolean"))
            {
                arrayType = SubClassif.BOOLEAN;
            } else if (dataType.equals("String"))
            {
                arrayType = SubClassif.STRING;
            } else
            {
                arrayType = SubClassif.EMPTY;
            }

            // use storage manager to put array values in
            switch (declarationType) {
                case 1:
                    // set position to first value in value list
                    scan.getNext();
                    scan.getNext();
                    // case 1: if it is an array declaration with empty set of brackets
                    arrayValues = new ArrayList<>();
                    while (!scan.currentToken.tokenStr.equals(";")) {
                        if (scan.currentToken.tokenStr.equals(",")) {
                            scan.getNext();
                        } else {
                            ResultValue arrayElement = new ResultValue(arrayType, scan.currentToken.tokenStr, 0, null);
                            arrayValues.add(arrayElement);
                            scan.getNext();
                        }
                    }
                    sb = new StringBuilder();
                    Utility.buildStringFromArray(sb, arrayValues);
                    StorageManager.values.put(arrayIdentifier.tokenStr, sb.toString());
                    break;
                case 2:
                    // set position to first value in value list
                    scan.getNext();
                    // case 2: if it is an array declaration with a valid size and no valueList
                    arrayValues = new ArrayList<>(Integer.parseInt(arraySize.value));
                    i = 0;
                    while(i != Integer.parseInt(arraySize.value)) {
                        ResultValue arrayElement = new ResultValue(arrayType, "null", 0, null);
                        arrayValues.add(arrayElement);
                        i++;
                    }

                    sb = new StringBuilder();
                    Utility.buildStringFromArray(sb, arrayValues);

                    StorageManager.values.put(arrayIdentifier.tokenStr, sb.toString());
                    break;
                case 3:
                    // set position to first value in value list
                    scan.getNext();
                    scan.getNext();
                    // case 3: if it is an array declaration with a valid size and a valueList
                    arrayValues = new ArrayList<>(Integer.parseInt(arraySize.value));
                    i = 0;
                    while (!scan.currentToken.tokenStr.equals(";")) {
                        if (scan.currentToken.tokenStr.equals(",")) {
                            scan.getNext();
                        } else {
                            ResultValue arrayElement = new ResultValue(arrayType, scan.currentToken.tokenStr, 0, null);
                            arrayValues.add(arrayElement);
                            i++;
                            scan.getNext();
                        }

                    }
                    while(i != Integer.parseInt(arraySize.value)) {
                        ResultValue arrayElement = new ResultValue(arrayType, null, 0, null);
                        arrayValues.add(arrayElement);
                        i++;
                    }

                    sb = new StringBuilder();
                    Utility.buildStringFromArray(sb, arrayValues);
                    StorageManager.values.put(arrayIdentifier.tokenStr, sb.toString());
                    break;
                default:
                    break;
            }

        } else
        {
            skipTo(";", scan);
        }
    }

    /** Process for 'if'/'if-else'.
     *
     * @param scan
     * 			Scanner object providing tokens to be executed
     * @param symbolTable
     * 			Contains additional token information
     * @param bExec
     * 			Determines whether or not each statement is executed during
     * 			certain control statements
     *
     * @throws Exception
     *
     * @author Riley Marfin
     */
    private void ifStmt(Scanner scan, SymbolTable symbolTable, Boolean bExec) throws Exception {

        if (bExec) {
            // we are executing, not ignoring
            ResultValue resCond = expression(scan, symbolTable);

            // Check for ending ':'
            if (!scan.currentToken.tokenStr.equals(":"))
            {
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: expected ':' after 'if'***"
                        , Meatbol.filename);
            }
            else if (scan.currentToken.tokenStr.equals(";"))
            {
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: expected ':' after 'if'***"
                        , Meatbol.filename);
            }
            // Condition is true, execute true part
            if (resCond.value.equals("T"))
            {
                ResultValue resTemp = executeStatements(scan, symbolTable, true);

                // end expected: else
                if (resTemp.terminatingStr.equals("else"))
                {
                    scan.getNext();
                    // Check for ending ':'
                    if (!scan.currentToken.tokenStr.equals(":"))
                    {
                        throw new ParserException(scan.currentToken.iSourceLineNr
                                ,"***Error: expected ':' after 'else'***"
                                , Meatbol.filename);
                    }

                    //since cond was true, ignore else part
                    resTemp = executeStatements(scan, symbolTable, false);
                }

                // end expected: endif (not optional)
//                if (!resTemp.terminatingStr.equals("endif"))
//                {
//                    throw new ParserException(scan.currentToken.iSourceLineNr
//                            ,"***Error: expected 'endif' for an 'if'***"
//                            , Meatbol.filename);
//                }
                scan.getNext();

                // check for ';'
                if (!scan.currentToken.tokenStr.equals(";"))
                {
                    throw new ParserException(scan.currentToken.iSourceLineNr
                            ,"***Error: expected ';' after 'endif'***"
                            , Meatbol.filename);
                }
            }

            else
            {
                // Condition false, ignore true part
                ResultValue resTemp = executeStatements(scan, symbolTable, false);
                if (resTemp.terminatingStr.equals("else"))
                {
                    scan.getNext();
                    // Check for ending ':'
                    if (!scan.currentToken.tokenStr.equals(":"))
                    {
                        throw new ParserException(scan.currentToken.iSourceLineNr
                                ,"***Error: expected ':' after 'else'***"
                                , Meatbol.filename);
                    }
                    //since cond was false, execute else part (optional)
                    resTemp = executeStatements(scan, symbolTable, true);
                }
                /*
                if (!resTemp.terminatingStr.equals("endif"))
                {
                    throw new ParserException(scan.currentToken.iSourceLineNr
                            ,"***Error: expected 'endif' for an 'if'***"
                            , Meatbol.filename);
                }*/

                scan.getNext();
                // check for ';'
                if (!scan.currentToken.tokenStr.equals(";"))
                {
                    throw new ParserException(scan.currentToken.iSourceLineNr
                            ,"***Error: expected ';' after 'endif'***"
                            , Meatbol.filename);
                }
            }
        }
        // we are ignoring execution
        else
        {
            // ignore the conditional
            skipTo(":", scan);
            // we ignoring
            ResultValue resTemp = executeStatements(scan, symbolTable, false);
            if (resTemp.terminatingStr.equals("else"))
            {

                scan.getNext();
                // Check for ending ':'
                if (!scan.currentToken.tokenStr.equals(":"))
                {
                    throw new ParserException(scan.currentToken.iSourceLineNr
                            ,"***Error: expected ':' after 'else'***"
                            , Meatbol.filename);
                }
                // we are still ignoring
                resTemp = executeStatements(scan, symbolTable, false);
            }
            /*
            if (!resTemp.terminatingStr.equals("endif"))
            {
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: expected 'endif' for an 'if'***"
                        , Meatbol.filename);
            }
            */
            scan.getNext();

            // Check for ending ';'
            if (!scan.currentToken.tokenStr.equals(";"))
            {
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: expected ';' after 'endif'***"
                        , Meatbol.filename);
            }
        }
    }



    /** This method executes statements in between an if or while loop based on a boolean for execution.
     * @param scan
     * 			Scanner providing tokens
     * @param symbolTable
     * 			Contains additional token information
     * @param bExec
     * 			Boolean for execution decision
     * @return
     * 			ResultValue of the ending string from the loop (else, endif, endwhile)
     * @throws Exception
     *
     * @author Riley Marfin
     */
    private ResultValue executeStatements(Scanner scan, SymbolTable symbolTable, boolean bExec) throws Exception
    {
        // set position to first line in if
        scan.getNext();

        if (bExec)
        {
            while (!scan.currentToken.tokenStr.equals("else") && !scan.currentToken.tokenStr.equals("endif")
                    && !scan.currentToken.tokenStr.equals("endwhile") && !scan.currentToken.tokenStr.equals("endfor"))
            {
                stmt(scan, symbolTable, true);
                scan.getNext();
            }
            ResultValue res = new ResultValue(SubClassif.END, "testVal", 0, scan.currentToken.tokenStr);
            return res;
        }
        else
        {
            while (!scan.currentToken.tokenStr.equals("else") && !scan.currentToken.tokenStr.equals("endif")
                    && !scan.currentToken.tokenStr.equals("endwhile") && !scan.currentToken.tokenStr.equals("endfor"))
            {
                stmt(scan, symbolTable, false);
                scan.getNext();
            }
            ResultValue res = new ResultValue(SubClassif.END, "testVal", 0, scan.currentToken.tokenStr);
            return res;
        }
    }

    /**
     * This method processes a while statement
     * @param scan
     * 			Scanner object providing tokens to be executed
     * @param symbolTable
     * 			Contains additional token information
     * @param bExec
     * 			Determines whether or not each statement is executed during
     * 			certain control statements
     * @throws Exception
     *
     * @author Riley Marfin
     */
    private void whileStmt(Scanner scan, SymbolTable symbolTable, Boolean bExec) throws Exception
    {
        ResultValue resTemp, resCond;

        // save current line number
        int iColPosition = scan.columnIndex;
        int iRowPos = scan.lineIndex;
        Token currentToken = scan.currentToken;
        Token nextToken = scan.nextToken;

        if (bExec) {
            // we are executing, not ignoring
            resCond = expression(scan, symbolTable);
            //System.out.println("Evaluated while condition");
            // Did the condition return True?
            if (resCond.value.equals("T"))
            {

                while (resCond.value.equals("T"))
                {
                    // Cond returned True, continue executing
                    resTemp = executeStatements(scan, symbolTable, true);
                    // skip back to current line and evalCond
                    scan.columnIndex = iColPosition;
                    scan.lineIndex = iRowPos;
                    scan.currentToken = currentToken;
                    scan.nextToken = nextToken;
                    resCond = expression(scan, symbolTable);
                }
                // exec stmts as false
                resTemp = executeStatements(scan, symbolTable, false);

                if (!resTemp.terminatingStr.equals("endwhile"))
                {
                    throw new ParserException(scan.currentToken.iSourceLineNr
                            ,"***Error: expected 'endwhile' for a 'while'***"
                            , Meatbol.filename);
                }

                scan.getNext();
                if (!scan.currentToken.tokenStr.equals(";"))
                {
                    throw new ParserException(scan.currentToken.iSourceLineNr
                            ,"***Error:expected ';' after 'endwhile'***"
                            , Meatbol.filename);
                }
            }
            else
            {
                // Cond returned False, ignore execution
                resTemp = executeStatements(scan, symbolTable, false); //not exec'ing true part

                if (!resTemp.terminatingStr.equals("endwhile"))
                {
                    throw new ParserException(scan.currentToken.iSourceLineNr
                            ,"***Error: expected 'endwhile' for a 'while'***"
                            , Meatbol.filename);
                }

                scan.getNext();
                if (!scan.currentToken.tokenStr.equals(";"))
                {
                    throw new ParserException(scan.currentToken.iSourceLineNr
                            ,"***Error: expected ';' after 'endwhile'***"
                            , Meatbol.filename);
                }
            }
        }
        else
        {
            // we are ignoring execution
            // we want to ignore the conditional, true part, and false part
            // should we execute evalCond?
            skipTo(":", scan);
            resTemp = executeStatements(scan, symbolTable, false);

            if (!resTemp.terminatingStr.equals("endwhile"))
            {
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: expected 'endwhile' for a 'while'***"
                        , Meatbol.filename);
            }

            scan.getNext();
            if (!scan.currentToken.tokenStr.equals(";"))
            {
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: expected ';' after 'endwhile'***"
                        , Meatbol.filename);
            }
        }
    }

    /** Placeholder: def statements not part of p4 */
    private void defStmt(Scanner scan, SymbolTable symbolTable) throws Exception
    {
        while(!scan.nextToken.tokenStr.equals(";"))
        {
            try
            {
                scan.getNext();
                //scan.currentToken.printToken();
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        scan.getNext();
    }

    /** Placeholder: for statements not part of p3 */
    private void forStmt(Scanner scan, SymbolTable symbolTable) throws Exception
    {
        scan.getNext();

        // verify structure of For Statement is correct
        if (scan.currentToken.primClassif != Classif.OPERAND || scan.nextToken.primClassif != Classif.OPERATOR)
        {
            // TODO: ERROR
        }

        // determine what kind of for statement this is and call the function for it
        switch (scan.nextToken.tokenStr)
        {
            // This is a basic For Statement
            case "=":
                handleBasicForStatement(scan, symbolTable);
                break;

            // This is the Meatbol equivalent of a For Each Statement
            case "in":
                handleForEachStatement(scan, symbolTable);
                break;

            // This is a String For Statement
            case "from":
                handleStringForStatement(scan, symbolTable);
                break;

            // This is an error.
            default:
                // TODO: ERROR
                break;
        }

        scan.getNext();
    }

    /**
     * This method processes a function statement
     * @param scan
     * 			Scanner object providing tokens to be executed
     * @param symbolTable
     * 			Contains additional token information
     * @param bExec
     * 			Determines whether or not each statement is executed during
     * 			certain function statements
     * @throws Exception
     *
     * @author Gregory Pugh
     */
    public void funcStmt(Scanner scan, SymbolTable symbolTable, Boolean bExec) throws Exception
    {
        if (bExec)
        {
            expression(scan, symbolTable);

            if(! (scan.currentToken.tokenStr.equals(";") || scan.currentToken.tokenStr.equals(":")))
            {
                throw new ParserException(scan.currentToken.iSourceLineNr + 1
                        ,"***Error: Missing end of statement: ';'***"
                        , Meatbol.filename);
            }
        }
        else
        {
            // we are not executing the statement, so skip to end
            skipTo(";", scan);
        }
    }

    /**
     * This method processes an assignment statement
     * @param scan
     * 			Scanner object providing tokens to be executed
     * @param symbolTable
     * 			Contains additional token information
     * @param bExec
     * 			Determines whether or not each statement is executed during
     * 			certain assignment statements
     * @throws Exception
     *
     * @author Gregory Pugh
     */
    public void assignStmt(Scanner scan, SymbolTable symbolTable, Boolean bExec) throws Exception
    {
        if (bExec)
        {
            ResultValue res;
            if(scan.currentToken.subClassif != SubClassif.IDENTIFIER)
            {
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: No value identifier for assignment***"
                        , Meatbol.filename);
            }
            Token variable = scan.currentToken;
            ResultValue Op1;
            ResultValue Op2;
            Boolean arrayAssignment = false;
            ResultValue arrayIndex = null;
            String arrayIdentifier = null;
            String arrayAsString;
            String str[];
            String result = null;
            List<String> array = null;
            STIdentifier temp = null;

            if (scan.nextToken.tokenStr.equals("[")) {
                arrayAssignment = true;
                if(!StorageManager.values.containsKey(variable.tokenStr))
                {
                    throw new ParserException(variable.iSourceLineNr
                            ,"***Error: Illegal assignment: " + variable + " not initialized***"
                            , Meatbol.filename);
                }
                scan.getNext();

                arrayIndex = expression(scan, symbolTable);
                temp = (STIdentifier) symbolTable.getSymbol(variable.tokenStr);
                if (temp.declareType == SubClassif.STRING)
                {
                    result = Utility.charInString(StorageManager.values.get(variable.tokenStr), Integer.parseInt(arrayIndex.value));
                } else {
                    arrayIdentifier = variable.tokenStr;
                    arrayAsString = StorageManager.values.get(arrayIdentifier);
                    str = arrayAsString.split("\\s*,\\s*");
                    array = Arrays.asList(str);
                }
            }

            scan.getNext();
            //scan.currentToken.printToken();
            switch(scan.currentToken.tokenStr)
            {
            case "=":
                res = expression(scan, symbolTable);
                break;
            case "-=":
                if(!StorageManager.values.containsKey(variable.tokenStr))
                {
                    throw new ParserException(variable.iSourceLineNr + 1
                            ,"***Error: Illegal assignment: " + variable + " not initialized***"
                            , Meatbol.filename);
                }
                Op1 = new ResultValue(variable.subClassif
                        , StorageManager.values.get(variable.tokenStr)
                        , 0, null);
                Op2 = expression(scan, symbolTable);
                res = Utility.doSubtraction(Op1, Op2, variable.iSourceLineNr);
                break;
            case "+=":
                if(!StorageManager.values.containsKey(variable))
                {
                    throw new ParserException(scan.currentToken.iSourceLineNr + 1
                            ,"***Error: Illegal assignment: " + variable + " not initialized***"
                            , Meatbol.filename);
                }
                Op1 = new ResultValue(variable.subClassif
                        , StorageManager.values.get(variable.tokenStr)
                        , 0, null);

                Op2 = expression(scan, symbolTable);
                res = Utility.doAddition(Op1, Op2, variable.iSourceLineNr);
                break;
            default:
                throw new ParserException(scan.currentToken.iSourceLineNr + 1
                        ,"***Error: Expected valid assignment operator***"
                        , Meatbol.filename);
            }
            if (arrayAssignment){
                if (temp.declareType == SubClassif.STRING)
                {
                    String finalResult = Utility.changeSubstringInString(StorageManager.values.get(variable.tokenStr), Integer.parseInt(arrayIndex.value), res.value);
                    StorageManager.values.put(variable.tokenStr, finalResult);
                } else {
                    array.set(Integer.parseInt(arrayIndex.value), res.value);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < array.size(); i++) {
                        sb.append(array.get(i));
                        if (i != array.size() - 1) {
                            sb.append(", ");
                        }
                    }
                    StorageManager.values.put(arrayIdentifier, sb.toString());
                }
            } else {
                StorageManager.values.put(variable.tokenStr, res.value);
            }

            // Debugging for Assign
            if (scan.debugOptionsMap.get(DebuggerTypes.ASSIGNMENT))
            {
                System.out.println("... Assign result into '" + variable.tokenStr + "' is '" + res.value + "'");
            }
        }
        else
        {
            // we are not executing the statement, so skip to end
            skipTo(";", scan);
        }
    }

    /**
     * This method processes a debug statement
     * @param scan
     * 			Scanner object providing tokens to be executed
     * @param symbolTable
     * 			Contains additional token information
     * @param bExec
     * 			Determines whether or not each statement is executed during
     * 			certain assignment statements
     * @throws Exception
     *
     * @author Mason Pohler
     */
    public void debugStmt(Scanner scan, SymbolTable symbolTable, boolean bExec) throws Exception
    {
        // make sure the debug statement is made of at least two debug Tokens
        if (scan.currentToken.primClassif != Classif.DEBUG || scan.nextToken.primClassif != Classif.DEBUG)
        {
            // TODO: ERROR
        }

        scan.getNext();

        String debugType = scan.currentToken.tokenStr;
        String onOrOffString = scan.nextToken.tokenStr;

        scan.getNext();

        // make sure the semicolon is included
        if (scan.nextToken.primClassif != Classif.SEPARATOR)
        {
            // TODO: ERROR
        }
        // clear semicolon token
        scan.getNext();

        boolean setValue = onOrOffString.equals("on");
        scan.debugOptionsMap.put(debugType, setValue);
    }

    // for k = 0 to ELEM(iCM) by 2:
    public void handleBasicForStatement(Scanner scan, SymbolTable symbolTable) throws Exception {
        // current should be k, next should be =

        Token controlVariableToken = new Token();
        controlVariableToken.copyToken(scan.currentToken);

        // TODO: VERIFY EXPRESSION ENDS AT "to"
        assignStmt(scan, symbolTable, true);

        // current should be to, next should be ELEM(iCM)
        // TODO: VERIFY EXPRESSION ENDS AT "by"
        ResultValue terminationResultValue = expression(scan, symbolTable);
        int terminationValue = Integer.parseInt(terminationResultValue.value);

        // current is either by or :
        ResultValue incrementValue = new ResultValue();

        // Determine the value for the increment value
        switch (scan.currentToken.tokenStr)
        {
            // The increment value is specified
            case "by":
                // TODO: VERIFY EXPRESSION ENDS AT ":"
                incrementValue = expression(scan, symbolTable);
                break;

            // The increment value is defaulted to 1
            case ":":
                incrementValue = new ResultValue(SubClassif.INTEGER, "1", 0, null);
                break;

            // Incomplete For Statement
            default:
                // TODO: ERROR
                break;
        }

//        scan.getNext();
//        // current token should be start of the line right after the For Statement

        // save state of the scanner so we can loop back
        int columnIndex = scan.columnIndex;
        int lineIndex = scan.lineIndex;
        Token currentToken = new Token();
        currentToken.copyToken(scan.currentToken);
        Token nextToken = new Token();
        nextToken.copyToken(scan.nextToken);

        int controlVariableValue = Integer.parseInt(StorageManager.values.get(controlVariableToken.tokenStr));

        // loop while increment value is less than termination value
        while (controlVariableValue < terminationValue)
        {
            executeStatements(scan, symbolTable, true);

            // return to top of statements
            scan.columnIndex = columnIndex;
            scan.lineIndex = lineIndex;
            scan.currentToken.copyToken(currentToken);
            scan.nextToken.copyToken(nextToken);

            // increase the current value of the control variable by the increment value and store it
            controlVariableValue = Integer.parseInt(StorageManager.values.get(controlVariableToken.tokenStr));
            ResultValue controlResultValue = new ResultValue(SubClassif.INTEGER, "" + controlVariableValue, 0
                    , null);
            controlResultValue = Utility.doAddition(controlResultValue, incrementValue, scan.lineIndex);
            controlVariableValue = Integer.parseInt(controlResultValue.value);
            StorageManager.values.put(controlVariableToken.tokenStr, "" + controlVariableValue);
        }

        // Get scanner aligned to the end of the For statement without executing it
        executeStatements(scan, symbolTable, false);
    }

    public void handleForEachStatement(Scanner scan, SymbolTable symbolTable) throws Exception
    {
        Token controlVariableToken = new Token();
        controlVariableToken.copyToken(scan.currentToken);

        scan.getNext();
        scan.getNext();
        // current = array or string, next = :

        Token structureToken = new Token();
        structureToken.copyToken(scan.currentToken);
        STIdentifier structureSTEnty = (STIdentifier) symbolTable.getSymbol(structureToken.tokenStr);

        scan.getNext();
//        scan.getNext();
//        // current = start of execute statements

        switch (structureSTEnty.structure)
        {
            // for variable in array:
            case ARRAY:
                handleForItemInArrayStatement(scan, symbolTable, controlVariableToken, structureToken);
                break;

            // for char in string:
            case PRIMITIVE:
                handleForCharInStringStatement(scan, symbolTable, controlVariableToken, structureToken);
                break;

            default:
                // TODO: ERROR
                break;
        }
    }

    // for stringCV from string by delimiter:
    public void handleStringForStatement(Scanner scan, SymbolTable symbolTable)
    {
        // TODO: THIS
    }

    // for char in string:
    private void handleForCharInStringStatement(Scanner scan, SymbolTable symbolTable, Token controlVariableToken
            , Token stringToken) throws Exception
    {
        // create entry for control variable so it can be accessed
        STIdentifier controlVariableIdentifier = new STIdentifier(controlVariableToken.tokenStr, Classif.OPERAND
                , SubClassif.STRING, null, null, 1);
        symbolTable.putSymbol(controlVariableIdentifier);

        String stringValue = StorageManager.values.get(stringToken.tokenStr);

        // save state of the scanner so we can loop back
        int columnIndex = scan.columnIndex;
        int lineIndex = scan.lineIndex;
        Token currentToken = new Token();
        currentToken.copyToken(scan.currentToken);
        Token nextToken = new Token();
        nextToken.copyToken(scan.nextToken);

        for (char item : stringValue.toCharArray())
        {
            StorageManager.values.put(controlVariableToken.tokenStr, "" + item);
            executeStatements(scan, symbolTable, true);

            // return to top of statements
            scan.columnIndex = columnIndex;
            scan.lineIndex = lineIndex;
            scan.currentToken.copyToken(currentToken);
            scan.nextToken.copyToken(nextToken);
        }

        // Get scanner aligned to the end of the For statement without executing it
        executeStatements(scan, symbolTable, false);
    }

    // for item in array:
    private void handleForItemInArrayStatement(Scanner scan, SymbolTable symbolTable, Token controlVariableToken
            , Token arrayToken) throws Exception
    {
        String arrayValue = StorageManager.values.get(arrayToken.tokenStr);

        // create entry for control variable so it can be accessed
        STIdentifier arrayIdentifier = (STIdentifier) symbolTable.getSymbol(arrayToken.tokenStr);
        STIdentifier controlVariableIdentifier = new STIdentifier(controlVariableToken.tokenStr, Classif.OPERAND
                , arrayIdentifier.declareType, null, null, 1);
        symbolTable.putSymbol(controlVariableIdentifier);

        String[] stringArray = arrayValue.split(", ");

        // save state of the scanner so we can loop back
        int columnIndex = scan.columnIndex;
        int lineIndex = scan.lineIndex;
        Token currentToken = new Token();
        currentToken.copyToken(scan.currentToken);
        Token nextToken = new Token();
        nextToken.copyToken(scan.nextToken);

        // The core of the for each loop
        for (String string : stringArray)
        {
            if (!string.equals("null")) {
                StorageManager.values.put(controlVariableToken.tokenStr, string);
                executeStatements(scan, symbolTable, true);

                // return to top of statements
                scan.columnIndex = columnIndex;
                scan.lineIndex = lineIndex;
                scan.currentToken.copyToken(currentToken);
                scan.nextToken.copyToken(nextToken);
            }
        }

        // Get scanner aligned to the end of the For statement without executing it
        executeStatements(scan, symbolTable, false);
    }

    /**
     * This method processes an expression by converting it from infix to postfix, and then evaluating it
     * @param scan
     * 			Scanner object providing tokens to be executed
     * @param symbolTable
     * 			Contains additional token information
     * @throws Exception
     *
     * @author Gregory Pugh
     */
    public ResultValue expression(Scanner scan, SymbolTable symbolTable) throws Exception
    {
        //collect tokens for expression
        ArrayList<Token> infix = new ArrayList<Token>();
        boolean atLeastOneBinaryOperator = false;
        boolean endExpression = false;

        //new token for infix expression
        Token token = new Token();
        token.copyToken(scan.currentToken);

        //scan.currentToken.printToken();
        //if we are executing from stmt, first token is function
        if(token.primClassif == Classif.FUNCTION)
        {
            if(! scan.nextToken.tokenStr.equals("("))
            {
                throw new ParserException(token.iSourceLineNr
                        ,"***Error: Function missing open parenthesis - '" + scan.currentToken.tokenStr + "' ***"
                        , Meatbol.filename);
            }
            infix.add(token);
            scan.getNext();
        }
        scan.getNext();

        //should be on first token of expression
        //scan.currentToken.printToken();
        token = new Token();
        token.copyToken(scan.currentToken);

        //build infix
        while(! (token.tokenStr.equals(";") || token.tokenStr.equals(":") || token.tokenStr.equals("]")
                || token.primClassif == Classif.CONTROL)) // && !endExpression)
        {
            switch(token.primClassif)
            {
            //for functions
            case FUNCTION:
                //check opening paren
                if(! scan.nextToken.tokenStr.equals("("))
                {
                    throw new ParserException(token.iSourceLineNr
                            ,"***Error: Function missing open parenthesis - '" + scan.currentToken.tokenStr + "' ***"
                            , Meatbol.filename);
                }
                //push function
                infix.add(token);
                scan.getNext();
                break;
            case SEPARATOR:
                //skip comma
                if (token.tokenStr.equals(","))
                {
                    infix.add(token);
                    token = new Token();
                    token.copyToken(scan.currentToken);
                }
                //only parenthesis allowed in infix expression
                else if (token.tokenStr.equals("(") || token.tokenStr.equals(")"))
                {
                    infix.add(token);
                    token = new Token();
                    token.copyToken(scan.currentToken);
                }
                //shouldn't be anything else
                else
                {
                    throw new ParserException(token.iSourceLineNr
                            ,"***Error: Invalid symbol - '" + scan.currentToken.tokenStr + "' ***"
                            , Meatbol.filename);
                }
                break;
                case OPERAND:
                    //if this is an identifier, we need to retrieve its value and type
                    if(token.subClassif == SubClassif.IDENTIFIER){
                        // we have an array reference
                        if (scan.nextToken.tokenStr.equals("[")) {
                            scan.getNext();
                            if (token.tokenStr == null) {
                                throw new ParserException(token.iSourceLineNr + 1
                                        , "***Error: Uninitialized array element - '" + scan.currentToken.tokenStr + "' ***"
                                        , Meatbol.filename);
                            }

                            ResultValue arrayIndex = expression(scan, symbolTable);
                            String arrayIdentifier = token.tokenStr;
                            STIdentifier variable = (STIdentifier) symbolTable.getSymbol(arrayIdentifier);
                            String result;
                            if (variable.declareType == SubClassif.STRING)
                            {
                                result = Utility.charInString(StorageManager.values.get(arrayIdentifier), Integer.parseInt(arrayIndex.value));
                                token.tokenStr = result;
                            } else {
                                String arrayAsString = StorageManager.values.get(arrayIdentifier);
                                String str[] = arrayAsString.split("\\s*,\\s*");
                                List<String> array = Arrays.asList(str);
                                token.tokenStr = array.get(Integer.parseInt(arrayIndex.value));
                                token.subClassif = variable.declareType;
                            }
                        } else {
                            token.tokenStr = StorageManager.values.get(scan.currentToken.tokenStr);
                            if (token.tokenStr == null) {
                                throw new ParserException(token.iSourceLineNr + 1
                                        , "***Error: Uninitialized variable - '" + scan.currentToken.tokenStr + "' ***"
                                        , Meatbol.filename);
                            }
                            STIdentifier variable = (STIdentifier) symbolTable.getSymbol(scan.currentToken.tokenStr);
                            token.subClassif = variable.declareType;
                        }
                    }
                    infix.add(token);
                    break;

            case OPERATOR:

                if (!token.tokenStr.equals("u-"))
                    atLeastOneBinaryOperator = true;

                infix.add(token);
                break;
            default:
                break;
            }
            scan.getNext();
            token = new Token();
            token.copyToken(scan.currentToken);
        }

//        for (Token test: infix)
//        {
//            System.out.print("\"" + test.tokenStr + "\" ");
//        }
//        System.out.println();

        ResultValue resultValue = infixToPostfix(infix);

        // Debugging for Expr
        if (scan.debugOptionsMap.get(DebuggerTypes.EXPRESSION) && atLeastOneBinaryOperator)
        {
            printExpression(infix, resultValue);
        }

        //scan.currentToken.printToken();
        return resultValue;
    }

    /**
     * This methods converts an expression from infix to postfix, and then calls the evaluation function
     * @param
     * 		infix ArrayList of tokens from expression
     * @return
     * 		Result value
     * @throws
     * 		ParserException
     *
     * @author Gregory Pugh
     */
    public ResultValue infixToPostfix(ArrayList<Token> infix) throws ParserException{
        //stack for holding operands while advancing to next operator
        Stack<Token> stack = new Stack<Token>();
        //array list for postfix expression
        ArrayList<Token> postfix = new ArrayList<Token>();

        //iterate through infix expression
        for(Token token : infix)
        {

            //based on what next token is
            switch(token.primClassif)
            {
            //out operands
            case OPERAND:
                postfix.add(token);
                break;
                //try to put on stack
            case OPERATOR:
                //if stack isn't empty
                while(! stack.empty())
                {
                    //check precedence with stack
                    if(token.prec() <= stack.peek().stackPrec())
                    {
                        //pop and out higher precedence operators
                        postfix.add(stack.pop());
                    }
                    else
                    {
                        break;
                    }
                }
                //push operator to stack
                stack.push(token);
                break;
                //try to put on stack
            case FUNCTION:
                while(! stack.empty())
                {
                    //check precedence with stack
                    if(token.prec() <= stack.peek().stackPrec())
                    {
                        //pop and out higher precedence operators
                        postfix.add(stack.pop());
                    }
                    else
                    {
                        break;
                    }
                }
                //push operator to stack
                stack.push(token);
                postfix.add(new Token("PARM"));
                break;

            case SEPARATOR:

                //left parens, always push
                if(token.tokenStr.equals("("))
                {
                    stack.push(token);
                }
                else if (token.tokenStr.equals(","))
                {
                    while(! stack.empty() && stack.peek().primClassif != Classif.FUNCTION)
                    {
                        postfix.add(stack.pop());
                    }
                }
                //right paren
                else if(token.tokenStr.equals(")"))
                {
                    try
                    {
                        //pop and out until we reach a right paren or function
                        while(! (stack.peek().tokenStr.equals("(")) && stack.peek().primClassif != Classif.FUNCTION)
                        {
                            postfix.add(stack.pop());
                        }
                    }
                    //stack cannot be empty
                    catch (EmptyStackException e)
                    {
                        throw new ParserException(token.iSourceLineNr + 1
                                ,"***Error: Missing left paranthesis***"
                                , Meatbol.filename);
                    }
                    //discard left paren
                    if(stack.peek().tokenStr.equals("("))
                        stack.pop();
                    else
                        postfix.add(stack.pop());
                }
                //we should only have parenthesis here
                else
                {
                    throw new ParserException(token.iSourceLineNr + 1
                            ,"***Error: Invalid token - only paranthesis allowed***"
                            , Meatbol.filename);
                }
                break;
            default:
                break;
            }
        }
        //pop rest of stack
        while(! stack.empty())
        {
            //check it for unmatched parenthesis
            if(stack.peek().tokenStr == "(")
            {
                throw new ParserException(stack.peek().iSourceLineNr
                        ,"***Error: Missing right paranthesis***"
                        , Meatbol.filename);
            }
            postfix.add(stack.pop());
        }
//        System.out.print("postfix: ");
//        for (Token test: postfix)
//        {
//            System.out.print("\"" + test.tokenStr + "\" ");
//            //System.out.print(test.primClassif + ",");
//        }
//        System.out.println();

        return evalPostfix(postfix);
    }

    /** Performs arithmatic and logical operations on postfix expression.
     * <p>
     * Postfix expression is an ordered array list of Tokens with each token
     * representing a single operand or operator. Evaluation is from left to
     * right only.
     *
     * @param postfix
     * 					Array of Tokens representing expression to be evaluated
     *
     * @return			Returns a ResultValue of the expression
     *
     * @throws ParserException
     * 					Throws exception if:
     * 					-postfix contains something which is not an operand or operator
     * 					-invalid number of operands or operators
     *
     * @author Mason Pohler
     * @author Gregory Pugh (modified: 26-03-2019)
     */
    public ResultValue evalPostfix(ArrayList<Token> postfix) throws ParserException
    {
        //stack for holding operands while advancing to next operator
        Stack<ResultValue> stack = new Stack<ResultValue>();
        //operands for operation and result
        ResultValue value, opLeft = new ResultValue(), opRight;
        ArrayList<ResultValue> parmList;

        if(postfix.isEmpty()){
            throw new ParserException(00
                    ,"***Error: Invalid expression - postfix empty***"
                    , Meatbol.filename);
        }


        //iterate through postfix expression
        for(Token token : postfix)
        {
            //token.printToken();
            if(token.tokenStr.equals("PARM"))
            {
                stack.push(new ResultValue(SubClassif.EMPTY,"PARM",0,null));
                continue;
            }
            //determine what to do with next token
            switch (token.primClassif)
            {
            //convert Token to ResultValue and push onto stack
            case OPERAND:
                //get the current value of identifiers
                if(token.subClassif == SubClassif.IDENTIFIER)
                {
//                    System.out.println(StorageManager.values.get(token.tokenStr));
                }
                value = Numeric.convertToken(token);
                stack.push(value);
                break;

                //get last two operands, calculate and push back onto stack
            case OPERATOR:
                //attempt to get last two operands
                try
                {
                    opRight = stack.pop();
                    if(!token.tokenStr.equals("u-") && !token.tokenStr.equals("not"))
                    {
                        opLeft = stack.pop();
                    }
                }
                catch (Exception e)
                {
                    throw new ParserException(postfix.get(0).iSourceLineNr
                            ,"***Error: Invalid expression - missing operand***"
                            , Meatbol.filename);
                }

                //do operation
                switch(token.tokenStr)
                {
                case "u-":
                    //single operand, put the other one back on stack
                    value = Utility.doUnaryMinus(opRight,token.iSourceLineNr);
                    break;
                case "^":
                    value = Utility.doExponent(opLeft,opRight,token.iSourceLineNr);
                    break;
                case "*":
                    value = Utility.doMultiply(opLeft,opRight,token.iSourceLineNr);
                    break;
                case "/":
                    value = Utility.doDivision(opLeft,opRight,token.iSourceLineNr);
                    break;
                case "+":
                    value = Utility.doAddition(opLeft,opRight,token.iSourceLineNr);
                    break;
                case "-":
                    value = Utility.doSubtraction(opLeft,opRight,token.iSourceLineNr);
                    break;
                case "#":
                    value = Utility.doConcatonate(opLeft,opRight,token.iSourceLineNr);
                    break;
                case "<":
                    value = Utility.doLess(opLeft,opRight,token.iSourceLineNr);
                    break;
                case ">":
                    value = Utility.doGreater(opLeft,opRight,token.iSourceLineNr);
                    break;
                case "<=":
                    value = Utility.doLessEqual(opLeft,opRight,token.iSourceLineNr);
                    break;
                case ">=":
                    value = Utility.doGreaterEqual(opLeft,opRight,token.iSourceLineNr);
                    break;
                case "==":
                    value = Utility.doEqual(opLeft,opRight,token.iSourceLineNr);
                    break;
                case "!=":
                    value = Utility.doNotEqual(opLeft,opRight,token.iSourceLineNr);
                    break;
                case "not":
                    //single operand, put the other one back on stack
                    value = Utility.doNot(opRight,token.iSourceLineNr);
                    break;
                case "and":
                    value = Utility.doAnd(opLeft,opRight,token.iSourceLineNr);
                    break;
                case "or":
                    value = Utility.doOr(opLeft,opRight,token.iSourceLineNr);
                    break;
                default:
                    throw new ParserException(token.iSourceLineNr
                            ,"***Error: Invalid expression - unknown operation***"
                            , Meatbol.filename);
                }
                stack.push(value);
                break;

            case FUNCTION:
                parmList = new ArrayList<ResultValue>();

                while (! stack.empty())
                    if(stack.peek().value.equals("PARM"))
                    {
                        stack.pop();
                        break;
                    }
                    else
                    {
                        parmList.add(stack.pop());
                    }
                switch(token.tokenStr)
                {
                case "print":
                    Utility.print(parmList);
                    stack.push(new ResultValue());
                    break;
                case "MAXELEM":
                    stack.push(Utility.maxElement(parmList));
                    break;
                case "LENGTH":
                    stack.push(Utility.length(parmList));
                    break;
                case "SPACES":
                    stack.push(Utility.spaces(parmList));
                    break;
                case "ELEM":
                    stack.push(Utility.element(parmList));
                    break;
                default:
                    throw new ParserException(token.iSourceLineNr
                            ,"***Error: Undefined function - " + token.tokenStr + "***"
                            , Meatbol.filename);
                }

                break;
            //if token is not operand or operator, something went wrong
            default:
                throw new ParserException(token.iSourceLineNr + 1
                        ,"***Error: Invalid expression - contains invalid operand or operator***"
                        , Meatbol.filename);
            }
        }
        //result should be only thing on stack
        value = stack.pop();

        //if stack is not empty now, something went wrong
        if(!stack.empty())
        {
//            System.out.println(stack.pop().value);
            throw new ParserException(postfix.get(0).iSourceLineNr
                    ,"***Error: Invalid expression - Unhandled operand in expression***"
                    , Meatbol.filename);
        }
        return value;
    }

    /** Advanced print function. - DEPRICATED
     * <p>
     * Prints entire code line even if the line spans multiple lines in the
     * source file. Never implemented and tested, as sample indicated this was
     * not the desired method.
     *
     * @param scan
     * 			Scanner object with source file lines
     * @param lineNumber
     * 			index of the current print location (avoids duplicate printing)
     *
     * @author Mason Pohler
     */
    private void printStatement(Scanner scan, int lineNumber)
    {
        //print until we reach end of file or ';'
        while(lineNumber < scan.lineList.size() && !scan.lineList.get(lineNumber).contains(";"))
        {
            System.out.printf("%3d %s\n", (lineNumber + 1)
                    , scan.lineList.get(lineNumber));
            lineNumber++;
        }
        System.out.printf("%3d %s\n", (lineNumber + 1)
                , scan.lineList.get(lineNumber));
    }

    /** Convenience function to skip to specific token.
     * <p>
     * Parses through each token until it finds one that matches the value
     * provided.
     *
     * @param skip
     * 			Token string to match
     * @param scan
     * 			Scanner providing Tokens
     *
     * @throws Exception
     *
     * @author Riley Marfin
     */
    private void skipTo(String skip, Scanner scan) throws Exception
    {
        //Skip tokens until we hit end of line
        while (!scan.currentToken.tokenStr.equals(skip))
        {
            scan.getNext();
        }
    }

    /**
     * This method prints an expression for debugging purposes
     * @param infixExpression
     * 			ArrayList of tokens from expression
     * @param resultValue
     * 			ResultValue from evaluation
     *
     * @author Mason Pohler
     */
    private void printExpression(ArrayList<Token> infixExpression, ResultValue resultValue)
    {
        StringBuffer expressionDebugStringBuffer = new StringBuffer();
        expressionDebugStringBuffer.append("... ");

        // add each token's tokenstr in the infix representation to the StringBuffer
        for (Token token : infixExpression)
        {
            expressionDebugStringBuffer.append(token.tokenStr);
            expressionDebugStringBuffer.append(" ");
        }
        expressionDebugStringBuffer.append(" is ");

        // If the result is a String, wrap it in quotes for readability
        if (resultValue.type == SubClassif.STRING)
        {
            expressionDebugStringBuffer.append("'");
            expressionDebugStringBuffer.append(resultValue.value);
            expressionDebugStringBuffer.append("'");
        }
        // otherwise append the result on its own
        else
            expressionDebugStringBuffer.append(resultValue.value);

        System.out.println(expressionDebugStringBuffer.toString());
    }
}
