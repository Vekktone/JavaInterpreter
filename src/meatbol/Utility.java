package meatbol;

public class Utility {

        //U-, *, /, ^, ==, <=, >=, <, >, !=, #
    public static void print(Parser parser, Scanner scan, SymbolTable symbolTable) throws Exception
    {
        int startLine, startCol, endLine, endCol;
        ResultValue res;

        try
        {
            //locate opening paren position
            scan.getNext();
            if(!scan.currentToken.tokenStr.equals("("))
            {
                System.out.println("***Function missing opening parenthesis***");
            }
            startLine = scan.currentToken.iSourceLineNr;
            startCol = scan.currentToken.iColPos;

            //locate closing paren position
            scan.getNext();
            while(!scan.nextToken.tokenStr.equals(";"))
            {
                scan.getNext();
            }
            //make sure function has closing paren
            if(!scan.currentToken.tokenStr.equals(")"))
            {
                System.out.println("***Function missing closing parenthesis***");
            }
            endLine = scan.currentToken.iSourceLineNr;
            endCol = scan.currentToken.iColPos;

            //reset to begining of arguements and process
            scan.lineIndex = startLine;
            scan.columnIndex = startCol;

            //System.out.println("Print function: ");
            scan.getNext();
            scan.getNext();
            //scan.currentToken.printToken();
            //System.out.println(scan.currentToken.iColPos +" "+ scan.currentToken.iSourceLineNr+" "+ endCol+" "+ endLine);
            while(scan.currentToken.iColPos < endCol && scan.currentToken.iSourceLineNr <= endLine)
            {
                //scan.currentToken.printToken();
                res = parser.expression(scan, symbolTable);
                System.out.print(res.value + " ");
                //scan.getNext();
            }
            System.out.print("\n");


        }
        catch (Exception e){
            throw e;
        }

    }

    public static ResultValue doUnaryMinus(ResultValue opLeft, int iSourceLineNr) throws ParserException {
        switch (opLeft.type)
        {
            case FLOAT:
                opLeft.value = "-".concat(opLeft.value);
                break;
            case INTEGER:
                opLeft.value = "-".concat(opLeft.value);
                break;
            default:
                throw new ParserException(iSourceLineNr
                        ,"***Error: Illegal operation with type***"
                        , Meatbol.filename);
        }
        return opLeft;
    }

    public static ResultValue doExponent(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException {
        switch (opLeft.type)
        {
            case FLOAT:
                float leftFloatValue = Numeric.toFloat(opLeft);
                float rightFloatValue = Numeric.toFloat(opRight);
                double leftFloatToDoubleValue = Numeric.floatToDouble(leftFloatValue);
                double rightFloatToDoubleValue = Numeric.floatToDouble(rightFloatValue);
                double doubleResult = Math.pow(leftFloatToDoubleValue, rightFloatToDoubleValue);
                float floatResult = (float) doubleResult;
                opLeft.value = Numeric.floatToString(floatResult);
                break;
            case INTEGER:
                int leftIntValue = Numeric.toInt(opLeft);
                int rightIntValue = Numeric.toInt(opRight);
                int intResult = (int) Math.pow(leftIntValue, rightIntValue);
                opLeft.value = Numeric.intToString(intResult);
                break;
            default:
                throw new ParserException(iSourceLineNr
                        ,"***Error: Illegal operation with type***"
                        , Meatbol.filename);
        }

        return opLeft;

    }
    public static ResultValue doMultiply(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException {
        switch (opLeft.type)
        {
            case FLOAT:
                float leftFloatValue = Numeric.toFloat(opLeft);
                float rightFloatValue = Numeric.toFloat(opRight);
                float floatResult = leftFloatValue * rightFloatValue;
                opLeft.value = Numeric.floatToString(floatResult);
                break;
            case INTEGER:
                int leftIntValue = Numeric.toInt(opLeft);
                int rightIntValue = Numeric.toInt(opRight);
                int intResult = leftIntValue * rightIntValue;
                opLeft.value = Numeric.intToString(intResult);
                break;
            default:
                throw new ParserException(iSourceLineNr
                        ,"***Error: Illegal operation with type***"
                        , Meatbol.filename);
        }
        return opLeft;

    }
    public static ResultValue doDivision(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException {
        switch (opLeft.type)
        {
            case FLOAT:
                float leftFloatValue = Numeric.toFloat(opLeft);
                float rightFloatValue = Numeric.toFloat(opRight);
                float floatResult = leftFloatValue / rightFloatValue;
                opLeft.value = Numeric.floatToString(floatResult);
                break;
            case INTEGER:
                int leftIntValue = Numeric.toInt(opLeft);
                int rightIntValue = Numeric.toInt(opRight);
                int intResult = leftIntValue / rightIntValue;
                opLeft.value = Numeric.intToString(intResult);
                break;
            default:
                throw new ParserException(iSourceLineNr
                        ,"***Error: Illegal operation with type***"
                        , Meatbol.filename);
        }
        return opLeft;

    }
    public static ResultValue doAddition(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException {
        switch (opLeft.type)
        {
            case FLOAT:
                float leftFloatValue = Numeric.toFloat(opLeft);
                float rightFloatValue = Numeric.toFloat(opRight);
                float floatResult = leftFloatValue + rightFloatValue;
                opLeft.value = Numeric.floatToString(floatResult);
                break;
            case INTEGER:
                int leftIntValue = Numeric.toInt(opLeft);
                int rightIntValue = Numeric.toInt(opRight);
                int intResult = leftIntValue + rightIntValue;
                opLeft.value = Numeric.intToString(intResult);
                break;
            default:
                throw new ParserException(iSourceLineNr
                        ,"***Error: Illegal operation with type***"
                        , Meatbol.filename);
        }
        return opLeft;

    }

    public static ResultValue doSubtraction(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException {
        switch (opLeft.type)
        {
            case FLOAT:
                float leftFloatValue = Numeric.toFloat(opLeft);
                float rightFloatValue = Numeric.toFloat(opRight);
                float floatResult = leftFloatValue - rightFloatValue;
                opLeft.value = Numeric.floatToString(floatResult);
                break;
            case INTEGER:
                int leftIntValue = Numeric.toInt(opLeft);
                int rightIntValue = Numeric.toInt(opRight);
                int intResult = leftIntValue - rightIntValue;
                opLeft.value = Numeric.intToString(intResult);
                break;
            default:
                throw new ParserException(iSourceLineNr
                        ,"***Error: Illegal operation with type***"
                        , Meatbol.filename);
        }
        return opLeft;

    }

    public static ResultValue doConcatonate(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException {
        switch (opLeft.type)
        {
            case STRING:
                opLeft.value += opRight.value;
                break;
            default:
                throw new ParserException(iSourceLineNr
                        ,"***Error: Illegal operation with type***"
                        , Meatbol.filename);
        }
        return opLeft;

    }

    /**
     * This method evaluates a less than (<) condition and returns a boolean ResultValue based on the left
     * operand being less than the right operand
     * 
     * @param opLeft
     * 			ResultValue for left operand
     * @param opRight
     * 			ResultValue for right operand
     * @param iSourceLineNr
     * 			Source line from Scanner
     * @return
     * 			Boolean ResultValue representing conditional result
     * @throws ParserException
     * 			For Illegal Operation
     * 
     * @author Riley Marfin
     */
    public static ResultValue doLess(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException
    {
    	// initialize our resCond for the returned result
    	ResultValue resCond = new ResultValue();
    	
    	// check type for left operand
        switch (opLeft.type)
        {
        case DATE:
            break;
        case FLOAT:
        	// use float variables for comparison
        	Float fOpLeft = Numeric.toFloat(opLeft);
        	Float fOpRight = Numeric.toFloat(opRight);
        	
        	// do comparison
        	if (fOpLeft < fOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case INTEGER:
        	// use integer variables for comparison
        	Integer iOpLeft = Numeric.toInt(opLeft);
        	Integer iOpRight = Numeric.toInt(opRight);
        	
        	// do comparison
        	if (iOpLeft < iOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case STRING:
        	// just use the ResultValues for comparison, no need to convert, but use compareTo function for strings
        	if (opLeft.value.compareTo(opRight.value) < 0)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return resCond;
    }

    /**
     * This method evaluates a greater than (>) condition and returns a boolean ResultValue based on the left
     * operand being greater than the right operand
     * @param opLeft
     * 			ResultValue for left operand
     * @param opRight
     * 			ResultValue for right operand
     * @param iSourceLineNr
     * 			Source line from Scanner
     * @return
     * 			Boolean ResultValue representing conditional result
     * @throws ParserException
     * 			For Illegal Operation
     * 
     * @author Riley Marfin
     */
    public static ResultValue doGreater(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException
    {
    	// initialize our resCond for the returned result
    	ResultValue resCond = new ResultValue();
    	
    	// check type for left operand
        switch (opLeft.type)
        {
        case DATE:
            break;
        case FLOAT:
        	// use float variables for comparison
        	Float fOpLeft = Numeric.toFloat(opLeft);
        	Float fOpRight = Numeric.toFloat(opRight);
        	
        	// do comparison
        	if (fOpLeft > fOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case INTEGER:
        	// use integer variables for comparison
        	Integer iOpLeft = Numeric.toInt(opLeft);
        	Integer iOpRight = Numeric.toInt(opRight);
        	
        	// do comparison
        	if (iOpLeft > iOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case STRING:
        	// no need to convert, but use compareTo function for strings
        	if (opLeft.value.compareTo(opRight.value) > 0)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return resCond;
    }

    /**
     * This method evaluates a less than or equal to (<=) condition and returns a boolean ResultValue based on the left
     * operand being less than or equal to the right operand
     * 
     * @param opLeft
     * 			ResultValue for left operand
     * @param opRight
     * 			ResultValue for right operand
     * @param iSourceLineNr
     * 			Source line from Scanner
     * @return
     * 			Boolean ResultValue representing conditional result
     * @throws ParserException
     * 			For Illegal Operation
     * 
     * @author Riley Marfin
     */
    public static ResultValue doLessEqual(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException
    {
    	// initialize our resCond for the returned result
    	ResultValue resCond = new ResultValue();
    	
    	// check type for left operand
        switch (opLeft.type)
        {
        case DATE:
            break;
        case FLOAT:
        	// use floats
        	Float fOpLeft = Numeric.toFloat(opLeft);
        	Float fOpRight = Numeric.toFloat(opRight);
        	
        	// do comparison
        	if (fOpLeft <= fOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case INTEGER:
        	// use integers
        	Integer iOpLeft = Numeric.toInt(opLeft);
        	Integer iOpRight = Numeric.toInt(opRight);
        	
        	// do comparison
        	if (iOpLeft <= iOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case STRING:
        	// no need to convert, but use compareTo method for strings
        	if (opLeft.value.compareTo(opRight.value) <= 0)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return resCond;
    }
    
    /**
     * This method evaluates a greater than or equal to (>=) condition and returns a boolean ResultValue based on the left
     * operand being greater than or equal to the right operand
     * 
     * @param opLeft
     * 			ResultValue for left operand
     * @param opRight
     * 			ResultValue for right operand
     * @param iSourceLineNr
     * 			Source line from Scanner
     * @return
     * 			Boolean ResultValue representing conditional result
     * @throws ParserException
     * 			For Illegal Operation
     * 
     * @author Riley Marfin
     */
    public static ResultValue doGreaterEqual(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException
    {
    	// init return variable
    	ResultValue resCond = new ResultValue();
    	
    	// check left operand type
        switch (opLeft.type)
        {
        case DATE:
            break;
        case FLOAT:
        	// use floats
        	Float fOpLeft = Numeric.toFloat(opLeft);
        	Float fOpRight = Numeric.toFloat(opRight);
        	
        	// do comparison
        	if (fOpLeft >= fOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case INTEGER:
        	// use ints
        	Integer iOpLeft = Numeric.toInt(opLeft);
        	Integer iOpRight = Numeric.toInt(opRight);
        	
        	// do comparison
        	if (iOpLeft >= iOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case STRING:
        	// no need to convert, use compareTo for strings
        	if (opLeft.value.compareTo(opRight.value) >= 0)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return resCond;
    }

    /**
     * This method evaluates an equal to (==) condition and returns a boolean ResultValue based on the left
     * operand being equal to the right operand
     * 
     * @param opLeft
     * 			ResultValue for left operand
     * @param opRight
     * 			ResultValue for right operand
     * @param iSourceLineNr
     * 			Source line from Scanner
     * @return
     * 			Boolean ResultValue representing conditional result
     * @throws ParserException
     * 			For Illegal Operation
     * 
     * @author Riley Marfin
     */
    public static ResultValue doEqual(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException
    {
    	// init return condition
    	ResultValue resCond = new ResultValue();
    	
    	// check left operand
        switch (opLeft.type)
        {
        case DATE:
            break;
        case FLOAT:
        	// use floats
        	Float fOpLeft = Numeric.toFloat(opLeft);
        	Float fOpRight = Numeric.toFloat(opRight);
        	
        	// do comparison
        	if (fOpLeft == fOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case INTEGER:
        	// use ints
        	Integer iOpLeft = Numeric.toInt(opLeft);
        	Integer iOpRight = Numeric.toInt(opRight);
        	
        	// do comparison
        	if (iOpLeft == iOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case STRING:
        	// no need to convert, but use compareTo for strings
        	if (opLeft.value == opRight.value)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return resCond;
    }

    /**
     * This method evaluates a not equal to (!=) condition and returns a boolean ResultValue based on the left
     * operand being not equal to the right operand
     * 
     * @param opLeft
     * 			ResultValue for left operand
     * @param opRight
     * 			ResultValue for right operand
     * @param iSourceLineNr
     * 			Source line from Scanner
     * @return
     * 			Boolean ResultValue representing conditional result
     * @throws ParserException
     * 			For Illegal Operation
     * 
     * @author Riley Marfin
     */
    public static ResultValue doNotEqual(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException
    {
    	// init return condition
    	ResultValue resCond = new ResultValue();
    	
    	// check left operand type
        switch (opLeft.type)
        {
        case DATE:
            break;
        case FLOAT:
        	// use floats
        	Float fOpLeft = Numeric.toFloat(opLeft);
        	Float fOpRight = Numeric.toFloat(opRight);
        	
        	// do comparison
        	if (fOpLeft != fOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case INTEGER:
        	// use ints
        	Integer iOpLeft = Numeric.toInt(opLeft);
        	Integer iOpRight = Numeric.toInt(opRight);
        	
        	// do comparison
        	if (iOpLeft != iOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case STRING:
        	// no need to convert, but use compareTo for strings
        	if (opLeft.value != opRight.value)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return resCond;
    }

    /**
     * This method evaluates a conditional not (!) and returns a boolean ResultValue based on the left
     * operand being false
     * 
     * @param opLeft
     * 			ResultValue for left operand
     * @param iSourceLineNr
     * 			Source line from Scanner
     * @return
     * 			Boolean ResultValue representing conditional result
     * @throws ParserException
     * 			For Illegal Operation
     * 
     * @author Riley Marfin
     */
    public static ResultValue doNot(ResultValue opLeft, int iSourceLineNr) throws ParserException
    {
    	// init return condition
    	ResultValue resCond = new ResultValue();
    	
    	// check left operand type
        switch (opLeft.type)
        {
        // only case is boolean
        case BOOLEAN:
        	// use boolean
        	Boolean bOpLeft = Boolean.parseBoolean(opLeft.value);
        	
        	// do comparison
        	if (!bOpLeft)
        	{
				resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
        	}
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return resCond;
    }

    /**
     * This method evaluates a conditional and (&&) and returns a boolean ResultValue based on the left
     * operand being and right operand being true
     * 
     * @param opLeft
     * 			ResultValue for left operand
     * @param opRight
     * 			ResultValue for right operand
     * @param iSourceLineNr
     * 			Source line from Scanner
     * @return
     * 			Boolean ResultValue representing conditional result
     * @throws ParserException
     * 			For Illegal Operation
     * 
     * @author Riley Marfin
     */
    public static ResultValue doAnd(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException
    {
    	// init return condition
    	ResultValue resCond = new ResultValue();
    	
    	// check left operand
        switch (opLeft.type)
        {
        // only case should be boolean
        case BOOLEAN:
        	// use booleans
        	Boolean bOpLeft = Boolean.parseBoolean(opLeft.value);
        	Boolean bOpRight = Boolean.parseBoolean(opRight.value);
        	
        	// do comparison
        	if (bOpLeft && bOpRight)
        	{
				resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
        	}
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return resCond;
    }

    /**
     * This method evaluates a conditional or (||) and returns a boolean ResultValue based on the left
     * operand being or right operand being true
     * 
     * @param opLeft
     * 			ResultValue for left operand
     * @param opRight
     * 			ResultValue for right operand
     * @param iSourceLineNr
     * 			Source line from Scanner
     * @return
     * 			Boolean ResultValue representing conditional result
     * @throws ParserException
     * 			For Illegal Operation
     * 
     * @author Riley Marfin
     */
    public static ResultValue doOr(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException
    {
    	// init return condition
    	ResultValue resCond = new ResultValue();
    	
    	// check left operand
        switch (opLeft.type)
        {
        case BOOLEAN:
        	// use booleans
        	Boolean bOpLeft = Boolean.parseBoolean(opLeft.value);
        	Boolean bOpRight = Boolean.parseBoolean(opRight.value);
        	
        	// do comparison
        	if (bOpLeft || bOpRight)
        	{
				resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
        	}
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return resCond;
    }
}