package meatbol;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

public class Parser
{
    public Parser(){

    }

    public void stmt(Scanner scan, SymbolTable symbolTable, Boolean bExec) throws Exception{
        //System.out.println("\n***start statement***");
        //scan.currentToken.printToken();
        switch(scan.currentToken.primClassif)
        {
            //shouldn't see this, but if it occurs skip it
            case EMPTY:
                System.out.println("***Warning: empty token detected***");
                break;
            //shouldn't see this, but if it occurs skip it
            case EOF:
                System.out.println("***Warning: EOF token detected***");
                break;
            //control statement
            case CONTROL:
                //System.out.println("***Control Statement***");
                conStmt(scan, symbolTable, bExec);
                break;
            //function statement
            case FUNCTION:
                //System.out.println("***Function Statement***");
                funcStmt(scan, symbolTable);
                break;
            //assignment statement
            case OPERAND:
                //System.out.println("***Assignment Statement***");
                assignStmt(scan, symbolTable);
                break;
            //statements can't begin with these, throw error
            case OPERATOR:
            case SEPARATOR:
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: Illegal start to statement***"
                        , Meatbol.filename);
            //Unknown state, throw error
            default:
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: unknown state***"
                        , Meatbol.filename);
        }
    }

    /** Determines what type of control statement we have.
     *
     * @param scan
     * @param symbolTable
     *
     * @throws Exception
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
                    throw new ParserException(scan.currentToken.iSourceLineNr
                            ,"***Error: unknown identifier***"
                            , Meatbol.filename);
                }
                scan.getNext();
                //see if a value was assigned
                if(scan.nextToken.tokenStr.equals("="))
                {
                    //treat this like any other assignment
                    assignStmt(scan, symbolTable);
                }
                else if(scan.nextToken.tokenStr.equals(";"))
                {
                    //System.out.println("***Empty declare of: "+scan.currentToken.tokenStr+"***");
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
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: Unknown state***"
                        , Meatbol.filename);
        }
    }

    private void ifStmt(Scanner scan, SymbolTable symbolTable, Boolean bExec) throws Exception {
         System.out.println("I'M IN THE IF YALL");
         System.out.println("The token is " + scan.currentToken.tokenStr);


         if (bExec) {
             // we are executing, not ignoring
             ResultValue resCond = evalCond(scan, symbolTable);
             // Did the condition return True?
             if (resCond.value.equals("true"))
             {
                 // Cond returned True, continue executing
                 ResultValue resTemp = executeStatements(scan, symbolTable, true);
                 // what ended the statements after the true part? else: or endif;
                 if (resTemp.terminatingStr.equals("else"))
                 {
                     scan.getNext();
                     if (!scan.currentToken.tokenStr.equals(":"))
                     {
                         errorWithCurrent("expected ':' after 'else'");
                     }


                     resTemp = executeStatements(scan, symbolTable, false); //since cond was true, ignore else part
                 }


                 if (!resTemp.terminatingStr.equals("endif"))
                 {
                     errorWithCurrent("expected 'endif' for an 'if'");
                 }


                 scan.getNext();
                 if (!scan.currentToken.tokenStr.equals(";"))
                 {
                     errorWithCurrent("expected ';' after 'endif'");
                 }
             }
             else
             {
                 // Cond returned False, ignore execution
                 ResultValue resTemp = executeStatements(scan, symbolTable, false); //not exec'ing true part
                 if (resTemp.terminatingStr.equals("else"))
                 {
                     scan.getNext();
                     if (!scan.currentToken.tokenStr.equals(":"))
                     {
                         errorWithCurrent("expected ':' after 'else'");
                     }
                     resTemp = executeStatements(scan, symbolTable, true); //since cond was false, exec else part
                 }


                 if (!resTemp.terminatingStr.equals("endif"))
                 {
                     errorWithCurrent("expected 'endif' for an 'if'");
                 }


                 scan.getNext();
                 if (!scan.currentToken.tokenStr.equals(";"))
                 {
                     errorWithCurrent("expected ';' after 'endif'");
                 }
             }
         }
         else
         {
             // we are ignoring execution
             // we want to ignore the conditional, true part, and false part
             // should we execute evalCond?
             skipTo(":", scan);
             ResultValue resTemp = executeStatements(scan, symbolTable, false);
             if (resTemp.terminatingStr.equals("else"))
             {
                 scan.getNext();
                 if (!scan.currentToken.tokenStr.equals(":"))
                 {
                     errorWithCurrent("expected ':' after 'else'");
                 }
                 resTemp = executeStatements(scan, symbolTable, false);
             }


             if (!resTemp.terminatingStr.equals("endif"))
             {
                 errorWithCurrent("expected 'endif' for an 'if'");
             }


             scan.getNext();
             if (!scan.currentToken.tokenStr.equals(";"))
             {
                 errorWithCurrent("expected ';' after 'endif'");
             }
         }
     }


     private ResultValue evalCond(Scanner scan, SymbolTable symbolTable) throws Exception
     {
         System.out.println("I will evalCond.");
         ResultValue resOp1, resOp2;


         // get the first operator
         scan.getNext();


         if (scan.currentToken.primClassif != Classif.OPERAND) {
             throw new ParserException(scan.currentToken.iSourceLineNr
                     ,"***Error: Expected Operand***"
                     , Meatbol.filename);
         }


         if (scan.currentToken.subClassif == SubClassif.IDENTIFIER) {
             if(!StorageManager.values.containsKey(scan.currentToken.tokenStr))
             {
                 throw new ParserException(scan.currentToken.iSourceLineNr
                         ,"***Error: Illegal condition: " + scan.currentToken.tokenStr + " not initialized***"
                         , Meatbol.filename);
             }
         }
         resOp1 = expression(scan, symbolTable);


         // get the cond operator to check
         scan.getNext();

         if (scan.currentToken.primClassif != Classif.OPERATOR)
         {
             throw new ParserException(scan.currentToken.iSourceLineNr
                     ,"***Error: Expected Operator***"
                     , Meatbol.filename);
         }
         String cond = scan.currentToken.tokenStr;


         // get the second operator
         scan.getNext();


         if (scan.currentToken.primClassif != Classif.OPERAND)
         {
             throw new ParserException(scan.currentToken.iSourceLineNr
                     ,"***Error: Expected Operand***"
                     , Meatbol.filename);
         }


         if (scan.currentToken.subClassif == SubClassif.IDENTIFIER)
         {
             if(!StorageManager.values.containsKey(scan.currentToken.tokenStr))
             {
                 throw new ParserException(scan.currentToken.iSourceLineNr
                         ,"***Error: Illegal condition: " + scan.currentToken.tokenStr + " not initialized***"
                         , Meatbol.filename);
             }
         }
         resOp2 = expression(scan, symbolTable);


         System.out.println("Expr is " + resOp1.value + cond + resOp2.value);


         // skip past ':'
         skipTo(":", scan);

         // check for types
         if (resOp1.type != resOp2.type) {
             throw new ParserException(scan.currentToken.iSourceLineNr
                     ,"***Error: Type mismatch***"
                     , Meatbol.filename);
         }

        if (resOp1.type == SubClassif.STRING || resOp1.type == SubClassif.BOOLEAN) {
             switch (cond) {
             case "==":
                 if (resOp1.value.equals(resOp2.value))
                 {
                     return new ResultValue(SubClassif.EMPTY, "true", 0, null);
                 }
                 else
                 {
                     return new ResultValue(SubClassif.EMPTY, "false", 0, null);
                 }
             case "!=":
                 if (!resOp1.value.equals(resOp2.value))
                 {
                     return new ResultValue(SubClassif.EMPTY, "true", 0, null);
                 }
                 else
                 {
                     return new ResultValue(SubClassif.EMPTY, "false", 0, null);
                 }
             }
         }
         else {
             if (resOp1.type == SubClassif.INTEGER)
             {
                 int integerOp1 = Integer.parseInt(resOp1.value);
                 int integerOp2 = Integer.parseInt(resOp1.value);


                 // evaluate it
                 switch (cond) {
                 case ">":
                     if (integerOp1 > integerOp2)
                     {
                         return new ResultValue(SubClassif.EMPTY, "true", 0, null);
                     }
                     else
                     {
                         return new ResultValue(SubClassif.EMPTY, "false", 0, null);
                     }
                 case "<":
                     if (integerOp1 < integerOp2)
                     {
                         return new ResultValue(SubClassif.EMPTY, "true", 0, null);
                     }
                     else
                     {
                         return new ResultValue(SubClassif.EMPTY, "false", 0, null);
                     }
                 case ">=":
                     if (integerOp1 >= integerOp2)
                     {
                         return new ResultValue(SubClassif.EMPTY, "true", 0, null);
                     }
                     else
                     {
                         return new ResultValue(SubClassif.EMPTY, "false", 0, null);
                     }
                 case "<=":
                     if (integerOp1 <= integerOp2)
                     {
                         return new ResultValue(SubClassif.EMPTY, "true", 0, null);
                     }
                     else
                     {
                         return new ResultValue(SubClassif.EMPTY, "false", 0, null);
                     }
                 case "==":
                     if (integerOp1 == integerOp2)
                     {
                         return new ResultValue(SubClassif.EMPTY, "true", 0, null);
                     }
                     else
                     {
                         return new ResultValue(SubClassif.EMPTY, "false", 0, null);
                     }
                 case "!=":
                     if (integerOp1 != integerOp2)
                     {
                         return new ResultValue(SubClassif.EMPTY, "true", 0, null);
                     }
                     else
                     {
                         return new ResultValue(SubClassif.EMPTY, "false", 0, null);
                     }
                 }
             }
             else if (resOp1.type == SubClassif.FLOAT)
             {
                 float floatOp1 = Float.parseFloat(resOp1.value);
                 float floatOp2 = Float.parseFloat(resOp1.value);


                 // evaluate it
                 switch (cond) {
                 case ">":
                     if (floatOp1 > floatOp2)
                     {
                         return new ResultValue(SubClassif.EMPTY, "true", 0, null);
                     }
                     else
                     {
                         return new ResultValue(SubClassif.EMPTY, "false", 0, null);
                     }
                 case "<":
                     if (floatOp1 < floatOp2)
                     {
                         return new ResultValue(SubClassif.EMPTY, "true", 0, null);
                     }
                     else
                     {
                         return new ResultValue(SubClassif.EMPTY, "false", 0, null);
                     }
                 case ">=":
                     if (floatOp1 >= floatOp2)
                     {
                         return new ResultValue(SubClassif.EMPTY, "true", 0, null);
                     }
                     else
                     {
                         return new ResultValue(SubClassif.EMPTY, "false", 0, null);
                     }
                 case "<=":
                     if (floatOp1 <= floatOp2)
                     {
                         return new ResultValue(SubClassif.EMPTY, "true", 0, null);
                     }
                     else
                     {
                         return new ResultValue(SubClassif.EMPTY, "false", 0, null);
                     }
                 case "==":
                     if (floatOp1 == floatOp2)
                     {
                         return new ResultValue(SubClassif.EMPTY, "true", 0, null);
                     }
                     else
                     {
                         return new ResultValue(SubClassif.EMPTY, "false", 0, null);
                     }
                 case "!=":
                     if (floatOp1 != floatOp2)
                     {
                         return new ResultValue(SubClassif.EMPTY, "true", 0, null);
                     }
                     else
                     {
                         return new ResultValue(SubClassif.EMPTY, "false", 0, null);
                     }
                 }
             }
         }


         throw new ParserException(scan.currentToken.iSourceLineNr
                 ,"***Error: Unknown evalCond error***"
                 , Meatbol.filename);
     }


     private void skipTo(String skip, Scanner scan) throws Exception {
         while (!scan.currentToken.tokenStr.equals(skip))
         {
             scan.getNext();
         }


     }


     private void errorWithCurrent(String message) {
         // TODO Auto-generated method stub
         System.out.println(message);


     }


     private ResultValue executeStatements(Scanner scan, SymbolTable symbolTable, boolean bExec) throws Exception
     {
         // set position to first line in if
         scan.getNext();


         if (bExec)
         {
             while (!scan.currentToken.tokenStr.equals("else") && !scan.currentToken.tokenStr.equals("endif") && !scan.currentToken.tokenStr.equals("endwhile"))
             {
                 System.out.println("execing stmts..." + scan.currentToken.tokenStr);
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
                 System.out.println("execing stmts...");
                 stmt(scan, symbolTable, false);
                 scan.getNext();
             }
             ResultValue res = new ResultValue(SubClassif.END, "testVal", 0, scan.currentToken.tokenStr);
             return res;
         }
     }


     private void whileStmt(Scanner scan, SymbolTable symbolTable, Boolean bExec) throws Exception
     {
         System.out.println("I'M IN THE WHILE YALL");
         System.out.println("The token is " + scan.currentToken.tokenStr);
         ResultValue resTemp, resCond;

         // save current line number
         int iColPosition = scan.columnIndex;
         int iRowPos = scan.lineIndex;
         Token currentToken = scan.currentToken;
         Token nextToken = scan.nextToken;

         if (bExec) {
             // we are executing, not ignoring
             resCond = evalCond(scan, symbolTable);
             // Did the condition return True?
             if (resCond.value.equals("true"))
             {

                 while (resCond.value.equals("true"))
                 {
                     // Cond returned True, continue executing
                     resTemp = executeStatements(scan, symbolTable, true);
                     // skip back to current line and evalCond
                     scan.columnIndex = iColPosition;
                     scan.lineIndex = iRowPos;
                     scan.currentToken = currentToken;
                     scan.nextToken = nextToken;
                     resCond = evalCond(scan, symbolTable);
                 }
                 // exec stmts as false
                 resTemp = executeStatements(scan, symbolTable, false);


                 if (!resTemp.terminatingStr.equals("endwhile"))
                 {
                     errorWithCurrent("expected 'endwhile' for a 'while'");
                 }


                 scan.getNext();
                 if (!scan.currentToken.tokenStr.equals(";"))
                 {
                     errorWithCurrent("expected ';' after 'endwhile'");
                 }
             }
             else
             {
                 // Cond returned False, ignore execution
                 resTemp = executeStatements(scan, symbolTable, false); //not exec'ing true part

                 if (!resTemp.terminatingStr.equals("endwhile"))
                 {
                     errorWithCurrent("expected 'endwhile' for a 'while'");
                 }


                 scan.getNext();
                 if (!scan.currentToken.tokenStr.equals(";"))
                 {
                     errorWithCurrent("expected ';' after 'endwhile'");
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

             if (resTemp.terminatingStr.equals("else"))
             {
                 scan.getNext();
                 if (!scan.currentToken.tokenStr.equals(":"))
                 {
                     errorWithCurrent("expected ':' after 'else'");
                 }
                 resTemp = executeStatements(scan, symbolTable, false);
             }


             if (!resTemp.terminatingStr.equals("endif"))
             {
                 errorWithCurrent("expected 'endif' for an 'if'");
             }


             scan.getNext();
             if (!scan.currentToken.tokenStr.equals(";"))
             {
                 errorWithCurrent("expected ';' after 'endif'");
             }
         }
     }


    private void defStmt(Scanner scan, SymbolTable symbolTable) throws Exception {
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

    private void forStmt(Scanner scan, SymbolTable symbolTable) throws Exception {
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

    public void funcStmt(Scanner scan, SymbolTable symbolTable) throws Exception
    {
        switch(scan.currentToken.subClassif)
        {
            //use the utility function
            case BUILTIN:
                switch(scan.currentToken.tokenStr)
                {
                case "print":
                    //System.out.println("***Do print function***");
                    Utility.print(this,scan,symbolTable);
                    break;
                default:
                    throw new ParserException(scan.currentToken.iSourceLineNr
                            ,"***Error: Undefined built-in function***"
                            , Meatbol.filename);
                }
                break;
            //not handling user functions yet, so this is an error
            case USER:
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: Undefined user function***"
                        , Meatbol.filename);
                //break;
            //something went wrong
            default:
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: Unknown state***"
                        , Meatbol.filename);
        }
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
    public void assignStmt(Scanner scan, SymbolTable symbolTable) throws Exception
    {
        //System.out.println("***Do assignment to "+scan.currentToken.tokenStr+"***");
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
        //System.out.println(variable.tokenStr +" = " + res.value);
    }

    public ResultValue expression(Scanner scan, SymbolTable symbolTable) throws Exception
    {
        //collect tokens for expression
        ArrayList<Token> infix = new ArrayList<Token>();
        Boolean endExpression = false;
        Token token = new Token();

        scan.getNext();
        //scan.currentToken.printToken();
        token.copyToken(scan.currentToken);

        //build infix
        while(!token.tokenStr.equals(";") && !token.tokenStr.equals(":") && !endExpression)
        {
            switch(token.primClassif)
            {
                //not handling functions yet, throw error
                //the result of function will be treated as an operand later
                case FUNCTION:
                    throw new ParserException(token.iSourceLineNr
                            ,"***Error: Functions not possible yet***"
                            , Meatbol.filename);

                case SEPARATOR:
                    //only parenthesis allowed in infix expression
                    if (! (token.tokenStr.equals("(") || scan.currentToken.tokenStr.equals(")")))
                    {
                        endExpression = true;
                        break;
                    }
                case OPERAND:
                    //if this is an identifier, we need to retrieve its value and type
                    if(token.subClassif == SubClassif.IDENTIFIER){
                        token.tokenStr = StorageManager.values.get(scan.currentToken.tokenStr);
                        STIdentifier variable = (STIdentifier)symbolTable.getSymbol(scan.currentToken.tokenStr);
                        token.subClassif = variable.declareType;
                    }
                case OPERATOR:
                    infix.add(token);
                    try
                    {
                        scan.getNext();
                        token = new Token();
                        token.copyToken(scan.currentToken);
                    }
                    catch (Exception e)
                    {
                        throw e;
                    }
                    break;
                default:
                    //anything else is the end of the expression
                    endExpression = true;
                    break;
            }
        }
        /*for (Token test: infix)
        {
            System.out.print(test.tokenStr + ",");
        }
        System.out.println();*/
        return infixToPostfix(infix);
    }

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
                        //pop and out higher precedence operators
                        postfix.add(stack.pop());
                }
                //push operator to stack
                   stack.push(token);
                break;
            //try to put on stack
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
                        //pop and out until we reach a right paren
                        while(stack.peek().tokenStr.equals("("))
                        {
                            postfix.add(stack.pop());
                            //stack cannot be empty
                        }
                    }
                    catch (EmptyStackException e)
                    {
                        e.printStackTrace();
                        throw new ParserException(token.iSourceLineNr
                                ,"***Error: Missing left paranthesis***"
                                , Meatbol.filename);
                    }
                    //discard left paren
                    stack.pop();
                }
                //we should only have parenthesis here
                else
                {
                    throw new ParserException(token.iSourceLineNr
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
            if(stack.peek().tokenStr.equals("("))
            {
                throw new ParserException(stack.peek().iSourceLineNr
                        ,"***Error: Missing right paranthesis***"
                        , Meatbol.filename);
            }
            postfix.add(stack.pop());
        }
        /*for (Token test: postfix)
        {
            System.out.print(test.tokenStr + ",");
        }
        System.out.println();*/
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
        /*for (Token test: postfix)
        {
            System.out.print(test.tokenStr + ",");
        }
        System.out.println();*/
        //stack for holding operands while advancing to next operator
        Stack<ResultValue> stack = new Stack<ResultValue>();
        //operands for operation and result
        ResultValue value, opLeft = new ResultValue(), opRight;

        if(postfix.isEmpty()){
            throw new ParserException(00
                    ,"***Error: Invalid expression - postfix empty***"
                    , Meatbol.filename);
        }
        //iterate through postfix expression
        for(Token token : postfix)
        {
            //determine what to do with next token
            switch (token.primClassif)
            {
                //convert Token to ResultValue and push onto stack
                case OPERAND:
                    //get the current value of identifiers
                    if(token.subClassif == SubClassif.IDENTIFIER)
                    {
                        System.out.println(StorageManager.values.get(token.tokenStr));
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
                //if token is not operand or operator, something went wrong
                default:
                    throw new ParserException(token.iSourceLineNr
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
        //return new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
        return value;
    }


}
