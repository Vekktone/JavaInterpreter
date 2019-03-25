package meatbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Parser
{
    public HashMap<String, Integer> precedenceMap;

    public Parser(){
        initPrecedenceMap();
    }

    public void stmt(Scanner scan, SymbolTable symbolTable) throws Exception{
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
            case CONTROL:
                System.out.println("\n***Control Statement***");
                conStmt(scan, symbolTable);
                break;
            case FUNCTION:
                System.out.println("\n***Function Statement***");
                funcStmt(scan, symbolTable);
                break;
            case OPERAND:
                System.out.println("\n***Assignment Statement***");
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

    private void initPrecedenceMap()
    {
        precedenceMap = new HashMap<String, Integer>();
        precedenceMap.put("(", 8);
        precedenceMap.put("u-", 7);
        precedenceMap.put("^", 6);
        precedenceMap.put("*", 5);
        precedenceMap.put("/", 5);
        precedenceMap.put("+", 4);
        precedenceMap.put("-", 4);
        precedenceMap.put("#", 3);
        precedenceMap.put("<", 2);
        precedenceMap.put(">", 2);
        precedenceMap.put("<=", 2);
        precedenceMap.put(">=", 2);
        precedenceMap.put("==", 2);
        precedenceMap.put("!=", 2);
        precedenceMap.put("not", 1);
        precedenceMap.put("and", 0);
        precedenceMap.put("or", 0);
    }

    /** Determines what type of control statement we have.
     *
     * @param scan
     * @param symbolTable
     *
     * @throws Exception
     */
    public void conStmt(Scanner scan, SymbolTable symbolTable) throws Exception
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
                    ifStmt(scan, symbolTable);
                    break;
                case "while":
                    whileStmt(scan, symbolTable);
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
    private void ifStmt(Scanner scan, SymbolTable symbolTable) throws Exception {
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

    private void whileStmt(Scanner scan, SymbolTable symbolTable) throws Exception {
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
                    System.out.println("***Do print function***");
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
        System.out.println("***Do assignment to "+scan.currentToken.tokenStr+"***");
        ResultValue res;
        if(scan.currentToken.subClassif != SubClassif.IDENTIFIER)
        {
            throw new ParserException(scan.currentToken.iSourceLineNr
                    ,"***Error: No value identifier for assignment***"
                    , Meatbol.filename);
        }
        Token variable = scan.currentToken;
        scan.getNext();
        switch(scan.currentToken.tokenStr)
        {
            case "=":
                res = expression(scan);
                break;
            case "-=":
                if(!StorageManager.values.containsKey(variable.tokenStr))
                {
                    throw new ParserException(variable.iSourceLineNr
                            ,"***Error: Illegal assignment: " + variable + " not initialized***"
                            , Meatbol.filename);
                }
                res = expression(scan);
                break;
            case "+=":
                if(!StorageManager.values.containsKey(variable))
                {
                    throw new ParserException(scan.currentToken.iSourceLineNr
                            ,"***Error: Illegal assignment: " + variable + " not initialized***"
                            , Meatbol.filename);
                }
                res = expression(scan);
                break;
            default:
                throw new ParserException(scan.currentToken.iSourceLineNr
                        ,"***Error: Expected valid assignment operator***"
                        , Meatbol.filename);
        }
        StorageManager.values.put(variable.tokenStr, res.value);
        while(!scan.currentToken.tokenStr.equals(";"))
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
        //scan.currentToken.printToken();
    }

    private ResultValue expression(Scanner scan) throws Exception {
        ArrayList<Token> infixExpression = makeInfixExpression(scan);
        ArrayList<Token> postfixExpression = convertInfixExpressionToPostfix(infixExpression);
        ResultValue evaluatedExpressionValue = evaluatePostfixExpression(postfixExpression);
        return evaluatedExpressionValue;
    }

    private ArrayList<Token> makeInfixExpression(Scanner scan) throws Exception {
        ArrayList<Token> infixExpression = new ArrayList<Token>();
        while(!scan.currentToken.tokenStr.equals(";"))
        {
            infixExpression.add(scan.currentToken);
            scan.getNext();
        }
        return infixExpression;
    }

    private ArrayList<Token> convertInfixExpressionToPostfix(ArrayList<Token> infixExpression)
    {
        ArrayList<Token> postfixExpression = new ArrayList<Token>();
        Stack<Token> operatorStack = new Stack<Token>();
        boolean foundParent = false;

        for (Token token : infixExpression)
        {
            switch (token.primClassif)
            {
                case OPERAND:
                    postfixExpression.add(token);
                    break;

                case OPERATOR:
                    convertInfixOperatorToPostfix(token, operatorStack, postfixExpression);
                    break;

                case SEPARATOR:
                    convertInfixSeparatorToPostfix(token, operatorStack, postfixExpression);
                    break;

                case FUNCTION:
                    // TODO: EXECUTE FUNCTION AND GET RESULT
                    break;

                case EMPTY: case EOF: case CONTROL: default:
                    // TODO: THESE ARE ALL ERRORS
                    break;
            }
        }

        return postfixExpression;
    }

    private ResultValue evaluatePostfixExpression(ArrayList<Token> postfixExpression)
    {
        Stack<ResultValue> operandStack = new Stack<ResultValue>();

        for (Token token : postfixExpression)
        {
            switch (token.primClassif)
            {
                case OPERAND:
                    ResultValue value = new ResultValue(token.subClassif, token.tokenStr, 0, "");
                    operandStack.push(value);
                    break;

                case OPERATOR:
                    evaluatePostfixOperator(token, operandStack);
                    break;

                default:
                    // TODO: ERROR
                    break;
            }
        }

        if (operandStack.size() != 1)
        {
            // TODO: ERROR
        }

        return operandStack.pop();
    }

    private void convertInfixOperatorToPostfix(Token token, Stack<Token> operatorStack
            , ArrayList<Token> postfixExpression)
    {
        while(!operatorStack.empty())
        {
            if (precedenceMap.get(token.tokenStr) > precedenceMap.get(operatorStack.peek().tokenStr))
                break;

            postfixExpression.add(operatorStack.pop());
        }
        operatorStack.push(token);
    }

    private void convertInfixSeparatorToPostfix(Token token, Stack<Token> operatorStack
            , ArrayList<Token> postfixExpression)
    {
        switch (token.tokenStr)
        {
            case "(":
                operatorStack.push(token);
                break;

            case ")":
                boolean foundParent = false;
                while (!operatorStack.empty())
                {
                    Token poppedToken = operatorStack.pop();
                    if (poppedToken.tokenStr.equals("("))
                    {
                        foundParent = true;
                        break;
                    }
                    else
                        postfixExpression.add(poppedToken);
                }
                if (!foundParent)
                {
                    // TODO: ERROR
                }
                break;

            default:
                // TODO: ERROR
                break;
        }
    }

    private void evaluatePostfixOperator(Token operator, Stack<ResultValue> operandStack)
    {
        switch (operator.tokenStr)
        {
            case "+":
                // TODO: NUMERIC ADD
                break;

            case "-":
                // TODO: NUMERIC SUBTRACT
                break;

            case "*":
                // TODO: NUMERIC MULTIPLICATION
                break;

            case "/":
                // TODO: NUMERIC DIVISION
                break;
        }
    }
}
