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
            System.out.println();


        }
        catch (Exception e){
            throw e;
        }

    }

    public static ResultValue doUnaryMinus(ResultValue opLeft, int iSourceLineNr) throws ParserException {
        switch (opLeft.type)
        {
        case FLOAT:
            break;
        case INTEGER:
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
            break;
        case INTEGER:
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
            break;
        case INTEGER:
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
            break;
        case INTEGER:
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
            break;
        case INTEGER:
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
            break;
        case INTEGER:
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
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return opLeft;

    }

    public static ResultValue doLess(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException {
    	ResultValue resCond = new ResultValue();
        switch (opLeft.type)
        {
        case DATE:
            break;
        case FLOAT:
        	Float fOpLeft = Float.parseFloat(opLeft.value);
        	Float fOpRight = Float.parseFloat(opRight.value);
        	if (fOpLeft < fOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "true", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "false", 0, null);
			}
            break;
        case INTEGER:
        	Integer iOpLeft = Integer.parseInt(opLeft.value);
        	Integer iOpRight = Integer.parseInt(opRight.value);
        	if (iOpLeft < iOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "true", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "false", 0, null);
			}
            break;
        case STRING:
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return resCond;
    }

    public static ResultValue doGreater(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException {
    	ResultValue resCond = new ResultValue();
        switch (opLeft.type)
        {
        case DATE:
            break;
        case FLOAT:
        	Float fOpLeft = Float.parseFloat(opLeft.value);
        	Float fOpRight = Float.parseFloat(opRight.value);
        	if (fOpLeft > fOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "true", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "false", 0, null);
			}
            break;
        case INTEGER:
        	Integer iOpLeft = Integer.parseInt(opLeft.value);
        	Integer iOpRight = Integer.parseInt(opRight.value);
        	if (iOpLeft > iOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "true", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "false", 0, null);
			}
            break;
        case STRING:
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return resCond;
    }

    public static ResultValue doLessEqual(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException {
    	ResultValue resCond = new ResultValue();
        switch (opLeft.type)
        {
        case DATE:
            break;
        case FLOAT:
        	Float fOpLeft = Float.parseFloat(opLeft.value);
        	Float fOpRight = Float.parseFloat(opRight.value);
        	if (fOpLeft <= fOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "true", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "false", 0, null);
			}
            break;
        case INTEGER:
        	Integer iOpLeft = Integer.parseInt(opLeft.value);
        	Integer iOpRight = Integer.parseInt(opRight.value);
        	if (iOpLeft <= iOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "true", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "false", 0, null);
			}
            break;
        case STRING:
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return resCond;
    }

    public static ResultValue doGreaterEqual(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException {
    	ResultValue resCond = new ResultValue();
        switch (opLeft.type)
        {
        case DATE:
            break;
        case FLOAT:
        	Float fOpLeft = Float.parseFloat(opLeft.value);
        	Float fOpRight = Float.parseFloat(opRight.value);
        	if (fOpLeft >= fOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "true", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "false", 0, null);
			}
            break;
        case INTEGER:
        	Integer iOpLeft = Integer.parseInt(opLeft.value);
        	Integer iOpRight = Integer.parseInt(opRight.value);
        	if (iOpLeft >= iOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "true", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "false", 0, null);
			}
            break;
        case STRING:
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return resCond;
    }

    public static ResultValue doEqual(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException {
    	ResultValue resCond = new ResultValue();
        switch (opLeft.type)
        {
        case DATE:
            break;
        case FLOAT:
        	Float fOpLeft = Float.parseFloat(opLeft.value);
        	Float fOpRight = Float.parseFloat(opRight.value);
        	if (fOpLeft == fOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "true", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "false", 0, null);
			}
            break;
        case INTEGER:
        	Integer iOpLeft = Integer.parseInt(opLeft.value);
        	Integer iOpRight = Integer.parseInt(opRight.value);
        	if (iOpLeft == iOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "true", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "false", 0, null);
			}
            break;
        case STRING:
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return resCond;
    }

    public static ResultValue doNotEqual(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException {
    	ResultValue resCond = new ResultValue();
        switch (opLeft.type)
        {
        case DATE:
            break;
        case FLOAT:
        	Float fOpLeft = Float.parseFloat(opLeft.value);
        	Float fOpRight = Float.parseFloat(opRight.value);
        	if (fOpLeft != fOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "true", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "false", 0, null);
			}
            break;
        case INTEGER:
        	Integer iOpLeft = Integer.parseInt(opLeft.value);
        	Integer iOpRight = Integer.parseInt(opRight.value);
        	if (iOpLeft != iOpRight)
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "true", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "false", 0, null);
			}
            break;
        case STRING:
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return resCond;
    }

    public static ResultValue doNot(ResultValue opLeft, int iSourceLineNr) throws ParserException {
    	ResultValue resCond = new ResultValue();
        switch (opLeft.type)
        {
        case BOOLEAN:
        	Boolean bOpLeft = Boolean.parseBoolean(opLeft.value);
        	if (!bOpLeft)
        	{
				resCond = new ResultValue(SubClassif.BOOLEAN, "true", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "false", 0, null);
        	}
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return resCond;
    }

    public static ResultValue doAnd(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException {
    	ResultValue resCond = new ResultValue();
        switch (opLeft.type)
        {
        case BOOLEAN:
        	Boolean bOpLeft = Boolean.parseBoolean(opLeft.value);
        	Boolean bOpRight = Boolean.parseBoolean(opRight.value);
        	if (bOpLeft && bOpRight)
        	{
				resCond = new ResultValue(SubClassif.BOOLEAN, "true", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "false", 0, null);
        	}
            break;
        default:
            throw new ParserException(iSourceLineNr
                    ,"***Error: Illegal operation with type***"
                    , Meatbol.filename);
        }
        return resCond;
    }

    public static ResultValue doOr(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException {
    	ResultValue resCond = new ResultValue();
        switch (opLeft.type)
        {
        case BOOLEAN:
        	Boolean bOpLeft = Boolean.parseBoolean(opLeft.value);
        	Boolean bOpRight = Boolean.parseBoolean(opRight.value);
        	if (bOpLeft || bOpRight)
        	{
				resCond = new ResultValue(SubClassif.BOOLEAN, "true", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "false", 0, null);
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
