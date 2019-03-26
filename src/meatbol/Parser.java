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

    public void stmt(Scanner scan, SymbolTable symbolTable, Boolean bExec) throws Exception{
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
                conStmt(scan, symbolTable, bExec);
                break;
            case FUNCTION:
                System.out.println("\n***Function Statement***");
                funcStmt(scan, symbolTable, bExec);
                break;
            case OPERAND:
                System.out.println("\n***Assignment Statement***");
                assignStmt(scan, symbolTable, bExec);
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
                    defStmt(scan, symbolTable, bExec);
                    break;
                case "for":
                    forStmt(scan, symbolTable, bExec);
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
    	resOp1 = expression();

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
    	resOp2 = expression();

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
    			if (resOp1.value == resOp2.value)
    			{
    				return new ResultValue(SubClassif.EMPTY, "true", 0, null);
    			}
    			else
    			{
    				return new ResultValue(SubClassif.EMPTY, "false", 0, null);
    			}
    		case "!=":
    			if (resOp1.value != resOp2.value)
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
			while (!scan.currentToken.tokenStr.equals("else") && !scan.currentToken.tokenStr.equals("endif"))
			{
				System.out.println("execing stmts..." + scan.currentToken.tokenStr);
//				stmt(scan, symbolTable, true);
				scan.getNext();
			}
			ResultValue res = new ResultValue(SubClassif.END, "testVal", 0, scan.currentToken.tokenStr);
			return res;
		}
		else
		{
			while (!scan.currentToken.tokenStr.equals("else") && !scan.currentToken.tokenStr.equals("endif"))
			{
				System.out.println("execing stmts...");
//				stmt(scan, symbolTable, false);
				scan.getNext();
			}
			ResultValue res = new ResultValue(SubClassif.END, "testVal", 0, scan.currentToken.tokenStr);
			return res;	
		}
	}

	private void whileStmt(Scanner scan, SymbolTable symbolTable, Boolean bExec) throws Exception
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

    private void defStmt(Scanner scan, SymbolTable symbolTable, Boolean bExec) throws Exception {
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

    private void forStmt(Scanner scan, SymbolTable symbolTable, Boolean bExec) throws Exception {
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

    public void funcStmt(Scanner scan, SymbolTable symbolTable, Boolean bExec) throws Exception
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
    public void assignStmt(Scanner scan, SymbolTable symbolTable, Boolean bExec) throws Exception
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
