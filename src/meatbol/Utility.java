package meatbol;

import java.util.*;

/** Utility holds all the functions used to alter operands with an operator.
 * <p>
 * Utility holds all the operator math and logic for Meatbol. Some operators
 * are binary, which need to operands to evaluate. Some are unaray and only
 * need one operand. There are also builtin functions included in Utility.
 *
 * @author Riley Marfin
 * @author Gregory Pugh (modified 25-3-2019)
 * @author Mason Pohler (modified 27-3-2019)
 */
public class Utility {

    /** Print prints the value passed to the Meatbol print function.
     * <p>
     * Converts arguments to the correct value, then prints the value.
     * @param parser the Parser object calling this function.
     * @param scan the Scanner that belongs to the Parser object.
     * @param symbolTable the SymbolTable belonging to the Scanner.
     * @throws Exception if anyhting goes wrong.
     *
     * @author Gregory Pugh
     */
    public static void print(Parser parser, Scanner scan, SymbolTable symbolTable) throws Exception
    {
        System.out.println("doing: print");
        /*int startLine, startCol, endLine, endCol;
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
        }*/

    }

    /** doUnaryMinus flips the sign of an operand.
     * <p>
     * This function keeps the type of the operand, and flips its sign.
     * @param opLeft The ResultValue with the value to flip.
     * @param iSourceLineNr The line number of the token.
     * @return a ResultValue that contains the value to flip.
     * @throws ParserException if something bad happens.
     *
     * @author Gregory Pugh
     * @author Mason Pohler (modified 28-3-2019)
     */
    public static ResultValue doUnaryMinus(ResultValue opLeft, int iSourceLineNr) throws ParserException
    {
        // Switch on the type of the left operator
        switch (opLeft.type)
        {
            // How to convert if the left operator is a float
            case FLOAT:
                // opLeft.value = "-".concat(opLeft.value);
                float originalFloat = Numeric.toFloat(opLeft);
                float flippedFloat = -1 * originalFloat;
                opLeft.value = Numeric.floatToString(flippedFloat);
                break;
            // How to convert if the left operator is an int
            case INTEGER:
                // opLeft.value = "-".concat(opLeft.value);
                int originalInt = Numeric.toInt(opLeft);
                int flippedInt = -1 * originalInt;
                opLeft.value = Numeric.intToString(flippedInt);
                break;
            //boolean flips
            case BOOLEAN:
                if(opLeft.value.equals("T"))
                   {
                    opLeft.value = "F";
                   }
                else if(opLeft.value.equals("F"))
                {
                    opLeft.value = "T";
                }
                else
                {
                    throw new ParserException(iSourceLineNr
                            ,"***Error: Invalid boolean value - " + opLeft.value + "***"
                            , Meatbol.filename);
                }
            // Non-numbers cannot be used for this operator
            default:
                throw new ParserException(iSourceLineNr
                        ,"***Error: Illegal operation with type***"
                        , Meatbol.filename);
        }
        return opLeft;
    }

    /** Calculates an exponet of x ^ y where x is the left operand and y is the
     * right operand.
     * <p>
     * Calculates an exponet of x ^ y where x is the left operand and y is the
     * right operand.
     * @param opLeft The ResultValue containing the left operand.
     * @param opRight The ResultValue containing the right operand.
     * @param iSourceLineNr The line number of the Token.
     * @return A ResultValue containing the result of the operation.
     * @throws ParserException if something goes wrong.
     *
     * @author Mason Pohler
     */
    public static ResultValue doExponent(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException
    {
        // switch on left operand type
        switch (opLeft.type)
        {
            // how to perform the operation if the left operand is a float.
            case FLOAT:
                float leftFloatValue = Numeric.toFloat(opLeft);
                float rightFloatValue = Numeric.toFloat(opRight);
                double leftFloatToDoubleValue = Numeric.floatToDouble(leftFloatValue);
                double rightFloatToDoubleValue = Numeric.floatToDouble(rightFloatValue);
                double doubleResult = Math.pow(leftFloatToDoubleValue, rightFloatToDoubleValue);
                float floatResult = (float) doubleResult;
                opLeft.value = Numeric.floatToString(floatResult);
                break;
            // how to perform the operation if the left operand is an int.
            case INTEGER:
                int leftIntValue = Numeric.toInt(opLeft);
                int rightIntValue = Numeric.toInt(opRight);
                int intResult = (int) Math.pow(leftIntValue, rightIntValue);
                opLeft.value = Numeric.intToString(intResult);
                break;
            // non-numerics cannot use this operator.
            default:
                throw new ParserException(iSourceLineNr
                        ,"***Error: Illegal operation with type***"
                        , Meatbol.filename);
        }
        return opLeft;
    }

    /** Multiplies 2 numbers.
     * <p>
     * Multiplies the left operand and right operand. The result keeps the type
     * of the left operand.
     * @param opLeft The ResultValue containing the left operand.
     * @param opRight The ResultValue containing the right operand.
     * @param iSourceLineNr The line number of the Token.
     * @return A ResultValue containing the result of the operation.
     * @throws ParserException if something goes wrong.
     *
     * @author Mason Pohler
     */
    public static ResultValue doMultiply(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException
    {
        //System.out.println("opLeft: " + opLeft.value + " opRight: " + opRight.value);
        //System.out.println("opLeft: " + opLeft.type + " opRight: " + opRight.type);
        // switch on left operand type
        switch (opLeft.type)
        {
            // how to perform the operation if the left operand is a float.
            case FLOAT:
                float leftFloatValue = Numeric.toFloat(opLeft);
                float rightFloatValue = Numeric.toFloat(opRight);
                float floatResult = leftFloatValue * rightFloatValue;
                opLeft.value = Numeric.floatToString(floatResult);
                break;
            // how to perform the operation if the left operand is an int.
            case INTEGER:
                int leftIntValue = Numeric.toInt(opLeft);
                int rightIntValue = Numeric.toInt(opRight);
                int intResult = leftIntValue * rightIntValue;
                opLeft.value = Numeric.intToString(intResult);
                break;
            // non-numerics cannot use this operator.
            default:
                throw new ParserException(iSourceLineNr
                        ,"***Error: Illegal operation with type***"
                        , Meatbol.filename);
        }
        //System.out.println("returning: " + opLeft.value);
        return opLeft;
    }

    /** Divides 2 numbers.
     * <p>
     * Divides the left operand by the right operand. The result keeps the type
     * of the left operand.
     * @param opLeft The ResultValue containing the left operand.
     * @param opRight The ResultValue containing the right operand.
     * @param iSourceLineNr The line number of the Token.
     * @return A ResultValue containing the result of the operation.
     * @throws ParserException if something goes wrong.
     *
     * @author Mason Pohler
     */
    public static ResultValue doDivision(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException
    {
        // switch on left operand type
        switch (opLeft.type)
        {
            // how to perform the operation if the left operand is a float.
            case FLOAT:
                float leftFloatValue = Numeric.toFloat(opLeft);
                float rightFloatValue = Numeric.toFloat(opRight);
                float floatResult = leftFloatValue / rightFloatValue;
                opLeft.value = Numeric.floatToString(floatResult);
                break;
            // how to perform the operation if the left operand is an int.
            case INTEGER:
                int leftIntValue = Numeric.toInt(opLeft);
                int rightIntValue = Numeric.toInt(opRight);
                int intResult = leftIntValue / rightIntValue;
                opLeft.value = Numeric.intToString(intResult);
                break;
            // non-numerics cannot use this operator.
            default:
                throw new ParserException(iSourceLineNr
                        ,"***Error: Illegal operation with type***"
                        , Meatbol.filename);
        }
        return opLeft;
    }

    /** Adds 2 numbers.
     * <p>
     * Adds the right operand to the left operand. The result keeps the type
     * of the left operand.
     * @param opLeft The ResultValue containing the left operand.
     * @param opRight The ResultValue containing the right operand.
     * @param iSourceLineNr The line number of the Token.
     * @return A ResultValue containing the result of the operation.
     * @throws ParserException if something goes wrong.
     *
     * @author Mason Pohler
     */
    public static ResultValue doAddition(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException
    {
        // switch on left operand type
        switch (opLeft.type)
        {
            // how to perform the operation if the left operand is a float.
            case FLOAT:
                float leftFloatValue = Numeric.toFloat(opLeft);
                float rightFloatValue = Numeric.toFloat(opRight);
                float floatResult = leftFloatValue + rightFloatValue;
                opLeft.value = Numeric.floatToString(floatResult);
                break;
            // how to perform the operation if the left operand is an int.
            case INTEGER:
                int leftIntValue = Numeric.toInt(opLeft);
                int rightIntValue = Numeric.toInt(opRight);
                int intResult = leftIntValue + rightIntValue;
                opLeft.value = Numeric.intToString(intResult);
                break;
            // non-numerics cannot use this operator.
            default:
                throw new ParserException(iSourceLineNr
                        ,"***Error: Illegal operation with type***"
                        , Meatbol.filename);
        }
        return opLeft;
    }

    /** Subtracts 1 number from the other.
     * <p>
     * Subtracts the right operand from the left operand. The result keeps the type
     * of the left operand.
     * @param opLeft The ResultValue containing the left operand.
     * @param opRight The ResultValue containing the right operand.
     * @param iSourceLineNr The line number of the Token.
     * @return A ResultValue containing the result of the operation.
     * @throws ParserException if something goes wrong.
     *
     * @author Mason Pohler
     */
    public static ResultValue doSubtraction(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException
    {
        // switch on left operand type
        switch (opLeft.type)
        {
            // how to perform the operation if the left operand is a float.
            case FLOAT:
                float leftFloatValue = Numeric.toFloat(opLeft);
                float rightFloatValue = Numeric.toFloat(opRight);
                float floatResult = leftFloatValue - rightFloatValue;
                opLeft.value = Numeric.floatToString(floatResult);
                break;
            // how to perform the operation if the left operand is an int.
            case INTEGER:
                int leftIntValue = Numeric.toInt(opLeft);
                int rightIntValue = Numeric.toInt(opRight);
                int intResult = leftIntValue - rightIntValue;
                opLeft.value = Numeric.intToString(intResult);
                break;
            // non-numerics cannot use this operator.
            default:
                throw new ParserException(iSourceLineNr
                        ,"***Error: Illegal operation with type***"
                        , Meatbol.filename);
        }
        return opLeft;
    }

    /** Concatenates two strings.
     * Appends the right operand to the end of the left operand. This
     * can only be done when the left operand is a string.
     * @param opLeft
     * @param opRight
     * @param iSourceLineNr
     * @return
     * @throws ParserException
     *
     * @author Mason Pohler
     */
    public static ResultValue doConcatonate(ResultValue opLeft, ResultValue opRight, int iSourceLineNr) throws ParserException
    {
        // switch on left operand type
        switch (opLeft.type)
        {
            // how to concatenate if the left operand is a String
            case STRING:
                opLeft.value += opRight.value;
                break;
            // this operator cannot be done if the left operand is a String
            default:
                throw new ParserException(iSourceLineNr
                        ,"***Error: Illegal operation with type***"
                        , Meatbol.filename);
        }
        // System.out.println(opLeft.value);
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
            if (fOpLeft.equals(fOpRight))
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
            if (iOpLeft.equals(iOpRight))
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
            if (opLeft.value.equals(opRight.value))
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
            if (!fOpLeft.equals(fOpRight))
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
            if (!iOpLeft.equals(iOpRight))
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
            if (!opLeft.value.equals(opRight.value))
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
            if (opLeft.value.equals("T"))
            {
                resCond = new ResultValue(SubClassif.BOOLEAN, "F", 0, null);
            }
            else
            {
                resCond = new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
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

            // do comparison
            if (opLeft.value.equals("T") && opRight.value.equals("T"))
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

            // do comparison
            if (opLeft.value.equals("T") || opRight.value.equals("T"))
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

    public static void buildStringFromArray(StringBuilder sb, ArrayList<ResultValue> arrayValues) {
        int i;
        for (i = 0; i < arrayValues.size(); i++) {
            sb.append(arrayValues.get(i).value);
            if (i != arrayValues.size() - 1) {
                sb.append(", ");
            }
        }
    }

    public static void print(ArrayList<ResultValue> parmList) {
        Stack<ResultValue> stack = new Stack<ResultValue>();
        //need to reverse the list
        for(ResultValue parameter : parmList)
        {
            stack.push(parameter);
        }
        //print in order
        while(! stack.empty())
        {
            System.out.print(stack.pop().value + " ");
        }
        System.out.println();
    }

    public static ResultValue maxElement(ArrayList<ResultValue> parmList) {
        StringTokenizer st = new StringTokenizer(parmList.get(0).value, ",");
        return new ResultValue(SubClassif.INTEGER,Integer.toString(st.countTokens()),0,null);
    }

    public static ResultValue length(ArrayList<ResultValue> parmList) {
//        System.out.println("doing LENGTH");
        return new ResultValue(SubClassif.INTEGER, Integer.toString(parmList.get(0).value.length()),0,null);
    }

    public static ResultValue spaces(ArrayList<ResultValue> parmList) {
        if (parmList.get(0).value.equals(""))
        {
            return new ResultValue(SubClassif.BOOLEAN, "T", 0, null);
        }

        Boolean foundOnlySpaces = true;
        String s = parmList.get(0).value;
        for (int i=0; i < s.length(); i++)
        {
            if (s.charAt(i) != ' ')
            {
                foundOnlySpaces = false;
            }
        }

        if (foundOnlySpaces)
        {
            return new ResultValue(SubClassif.BOOLEAN,"T",0,null);
        } else
        {
            return new ResultValue(SubClassif.BOOLEAN,"F",0,null);
        }
    }

    public static ResultValue element(ArrayList<ResultValue> parmList) {
        StringTokenizer st = new StringTokenizer(parmList.get(0).value, ",");
        int i = 0;
        while(st.hasMoreTokens())
        {
            String temp = st.nextToken();
            temp = temp.trim();
            if (temp.equals("null"))
            {
                break;
            }
            i++;
        }
        return new ResultValue(SubClassif.INTEGER,Integer.toString(i),0,null);
    }

    public static String charInString(String string, int index)
    {
        return "" + string.toCharArray()[index];
    }

    public static String changeSubstringInString(String string, int index, String value)
    {
        String left = string.substring(0, index);
        String right = string.substring(index+1, string.length());
        return left + value + right;
    }

    public static ResultValue dateDiff(ArrayList<ResultValue> parmList, int line) throws ParserException {
        int firstDate, lastDate;

        if(parmList.size() != 2)
            throw new ParserException(line
                    ,"***Error: Invalid number of function parameters***"
                    , Meatbol.filename);
        firstDate = Numeric.dateToEpoch(parmList.get(0).value, line);
        lastDate = Numeric.dateToEpoch(parmList.get(1).value, line);

        return new ResultValue(SubClassif.INTEGER,Integer.toString(lastDate - firstDate),0,null);
    }

    public static ResultValue dateAdj(ArrayList<ResultValue> parmList, int line) throws ParserException {
        int date;
        String d;

        if(parmList.size() != 2)
            throw new ParserException(line
                    ,"***Error: Invalid number of function parameters***"
                    , Meatbol.filename);

        date = Numeric.dateToEpoch(parmList.get(1).value, line) + Numeric.toInt(parmList.get(0));
        d = Numeric.epochToDate(date);

        return new ResultValue(SubClassif.DATE,d,0,null);
    }

    public static ResultValue dateAge(ArrayList<ResultValue> parmList, int line) throws ParserException {
        StringTokenizer st1 = new StringTokenizer(parmList.get(1).value,"-");
        StringTokenizer st2 = new StringTokenizer(parmList.get(0).value,"-");
        int year1, month1, day1;
        int year2, month2, day2;
        int age = 0;

        System.out.println(parmList.get(0).value + " " +parmList.get(1).value);
        if(parmList.size() != 2)
            throw new ParserException(line
                    ,"***Error: Invalid number of function parameters***"
                    , Meatbol.filename);
        try
        {
            year1 = Integer.parseInt(st1.nextToken());
            month1 = Integer.parseInt(st1.nextToken());
            day1 = Integer.parseInt(st1.nextToken());
        }
        catch (Exception e)
        {
            throw new ParserException(line
                    ,"***Error: Invalid date - " + parmList.get(0).value + "***"
                    , Meatbol.filename);
        }

        try
        {
            year2 = Integer.parseInt(st2.nextToken());
            month2 = Integer.parseInt(st2.nextToken());
            day2 = Integer.parseInt(st2.nextToken());
        }
        catch (Exception e)
        {
            throw new ParserException(line
                    ,"***Error: Invalid date - " + parmList.get(0).value + "***"
                    , Meatbol.filename);
        }
        age = year1 - year2;
        if(day1<day2)
        {
            if(month1<=month2)
            {
                age--;
            }
        }

        return new ResultValue(SubClassif.INTEGER,Integer.toString(age),0,null);
    }


}