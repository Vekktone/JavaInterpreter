package meatbol;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

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
                conStmt(scan, symbolTable, bExec);
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
    public void conStmt(Scanner scan, SymbolTable symbolTable, Boolean bExec) throws Exception
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
            else
            {
                //anything else is a syntax error
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: Expected end of statement or assignment***"
                        , Meatbol.filename);
            }
            break;
        //if we are here, then we are missing matching FLOW
        case END:
            switch(scan.currentToken.tokenStr)
            {
            case "enddef":
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: enddef without def***"
                        , Meatbol.filename);
            case "endif":
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: endif without if***"
                        , Meatbol.filename);
            case "else":
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: else without if***"
                        , Meatbol.filename);
            case "endfor":
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: endfor without for***"
                        , Meatbol.filename);
            case "endwhile":
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: endwhile without while***"
                        , Meatbol.filename);
            default:
                throw new ParserException(scan.currentToken.iSourceLineNr
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
            while (!scan.currentToken.tokenStr.equals("else") && !scan.currentToken.tokenStr.equals("endif") && !scan.currentToken.tokenStr.equals("endwhile"))
            {
                stmt(scan, symbolTable, true);
                scan.getNext();
            }
            ResultValue res = new ResultValue(SubClassif.END, "testVal", 0, scan.currentToken.tokenStr);
            return res;
        }
        else
        {
            while (!scan.currentToken.tokenStr.equals("else") && !scan.currentToken.tokenStr.equals("endif") && !scan.currentToken.tokenStr.equals("endwhile"))
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
        //scan.currentToken.printToken();

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

            scan.getNext();

            switch(scan.currentToken.tokenStr)
            {
            case "=":
                res = expression(scan, symbolTable);
                break;
            case "-=":
                if(!StorageManager.values.containsKey(variable.tokenStr))
                {
                    throw new ParserException(variable.iSourceLineNr
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
                    throw new ParserException(scan.currentToken.iSourceLineNr
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
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: Expected valid assignment operator***"
                        , Meatbol.filename);
            }
            StorageManager.values.put(variable.tokenStr, res.value);

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
        while(! (token.tokenStr.equals(";") || token.tokenStr.equals(":")) && endExpression == false)
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
                    //just need to ignore
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
                if(token.subClassif == SubClassif.IDENTIFIER)
                {
                    token.tokenStr = StorageManager.values.get(scan.currentToken.tokenStr);
                    if(token.tokenStr == null)
                    {
                        throw new ParserException(token.iSourceLineNr + 1
                                ,"***Error: Uninitialized variable - '" + scan.currentToken.tokenStr + "' ***"
                                , Meatbol.filename);
                    }
                    STIdentifier variable = (STIdentifier)symbolTable.getSymbol(scan.currentToken.tokenStr);
                    token.subClassif = variable.declareType;
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

       // for (Token test: infix)
       // {
        //    System.out.print(test.tokenStr + ",");
        //}
        //System.out.println();

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
                    if(token.prec() < stack.peek().stackPrec())
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

                //stack.elements());
                break;
                //try to put on stack
            case FUNCTION:
                while(! stack.empty())
                {
                    //check precedence with stack
                    if(token.prec() < stack.peek().stackPrec())
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
        System.out.print("postfix: ");
        for (Token test: postfix)
        {
            System.out.print(test.tokenStr + ",");
        }
        System.out.println();

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
                    if(token.tokenStr != "u-" && token.tokenStr != "not")
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
