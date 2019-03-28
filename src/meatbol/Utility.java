package meatbol;

import sun.misc.FloatingDecimal;

import java.math.BigDecimal;

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
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case INTEGER:
        	Integer iOpLeft = Integer.parseInt(opLeft.value);
        	Integer iOpRight = Integer.parseInt(opRight.value);
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
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case INTEGER:
        	Integer iOpLeft = Integer.parseInt(opLeft.value);
        	Integer iOpRight = Integer.parseInt(opRight.value);
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
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case INTEGER:
        	Integer iOpLeft = Integer.parseInt(opLeft.value);
        	Integer iOpRight = Integer.parseInt(opRight.value);
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
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case INTEGER:
        	Integer iOpLeft = Integer.parseInt(opLeft.value);
        	Integer iOpRight = Integer.parseInt(opRight.value);
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
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case INTEGER:
        	Integer iOpLeft = Integer.parseInt(opLeft.value);
        	Integer iOpRight = Integer.parseInt(opRight.value);
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
        		resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
			}
        	else
        	{
        		resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
			}
            break;
        case INTEGER:
        	Integer iOpLeft = Integer.parseInt(opLeft.value);
        	Integer iOpRight = Integer.parseInt(opRight.value);
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

    public static ResultValue doNot(ResultValue opLeft, int iSourceLineNr) throws ParserException {
    	ResultValue resCond = new ResultValue();
        switch (opLeft.type)
        {
        case BOOLEAN:
        	Boolean bOpLeft = Boolean.parseBoolean(opLeft.value);
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

    public static ResultValue doAnd(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException {
    	ResultValue resCond = new ResultValue();
        switch (opLeft.type)
        {
        case BOOLEAN:
        	Boolean bOpLeft = Boolean.parseBoolean(opLeft.value);
        	Boolean bOpRight = Boolean.parseBoolean(opRight.value);
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

    public static ResultValue doOr(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException {
    	ResultValue resCond = new ResultValue();
        switch (opLeft.type)
        {
        case BOOLEAN:
        	Boolean bOpLeft = Boolean.parseBoolean(opLeft.value);
        	Boolean bOpRight = Boolean.parseBoolean(opRight.value);
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