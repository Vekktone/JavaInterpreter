package meatbol;

public class Utility {

        //U-, *, /, ^, ==, <=, >=, <, >, !=, #
    public static void print(Scanner scan) throws Exception
    {
        try
        {
            scan.getNext();
            if(!scan.currentToken.tokenStr.equals("("))
            {
                System.out.println("***Function missing opening parenthesis***");
            }
            scan.getNext();
            while(!scan.currentToken.tokenStr.equals(")"))
            switch(scan.currentToken.primClassif)
            {
                //shouldn't happen, but if it does just ignore
                case EMPTY:
                    break;
            //if we see this then print is incomplete, throw error
            case EOF:
                System.out.println("***Incomplete print statement***");
                break;
            //nested function, need to print the return value
            //for now, just throwing error
            case FUNCTION:
                System.out.println("***Nested function, not handling this yet***");
                break;
            //print this
            case OPERAND:
                //variable, print value rather than token
                if(scan.currentToken.subClassif == SubClassif.IDENTIFIER)
                {

                }
                //this is a constant, print it
                else
                {
                    System.out.print(scan.currentToken.tokenStr);
                }
                break;
            case SEPARATOR:
            //function must start with operand or value of some kind(infix)
            case CONTROL: case OPERATOR:
            default:
                System.out.println("***Missing operand***");
                break;

            }


        }
        catch (Exception e){
            throw e;
        }

    }

    public static ResultValue doUnaryMinus(ResultValue opLeft) {
        return opLeft;

    }
    public static ResultValue doExponent(ResultValue opLeft, ResultValue opRight) {
        // TODO Auto-generated method stub
        return opLeft;

    }
    public static ResultValue doMultiply(ResultValue opLeft, ResultValue opRight) {
        // TODO Auto-generated method stub
        return opLeft;

    }
    public static ResultValue doDivision(ResultValue opLeft, ResultValue opRight) {
        // TODO Auto-generated method stub
        return opLeft;

    }
    public static ResultValue doAddition(ResultValue opLeft, ResultValue opRight) {
        // TODO Auto-generated method stub
        return opLeft;

    }

    public static ResultValue doSubtraction(ResultValue opLeft, ResultValue opRight) {
        // TODO Auto-generated method stub
        return opLeft;

    }

    public static ResultValue doConcatonate(ResultValue opLeft, ResultValue opRight) {
        // TODO Auto-generated method stub
        return opLeft;

    }

    public static ResultValue doLessThan(ResultValue opLeft, ResultValue opRight) {
        // TODO Auto-generated method stub
        return opLeft;
    }

    public static ResultValue doGreaterThan(ResultValue opLeft, ResultValue opRight) {
        // TODO Auto-generated method stub
        return opLeft;
    }

    public static ResultValue doLessEqual(ResultValue opLeft, ResultValue opRight) {
        // TODO Auto-generated method stub
        return opLeft;
    }

    public static ResultValue doGreaterEqual(ResultValue opLeft, ResultValue opRight) {
        // TODO Auto-generated method stub
        return opLeft;
    }

    public static ResultValue doLess(ResultValue opLeft, ResultValue opRight) {
        // TODO Auto-generated method stub
        return opLeft;
    }

    public static ResultValue doGreater(ResultValue opLeft, ResultValue opRight) {
        // TODO Auto-generated method stub
        return opLeft;
    }

    public static ResultValue doEqual(ResultValue opLeft, ResultValue opRight) {
        // TODO Auto-generated method stub
        return opLeft;
    }

    public static ResultValue doNotEqual(ResultValue opLeft, ResultValue opRight) {
        // TODO Auto-generated method stub
        return opLeft;
    }

    public static ResultValue doNot(ResultValue opLeft) {
        // TODO Auto-generated method stub
        return opLeft;
    }

    public static ResultValue doAnd(ResultValue opLeft, ResultValue opRight) {
        // TODO Auto-generated method stub
        return opLeft;
    }

    public static ResultValue doOr(ResultValue opLeft, ResultValue opRight) {
        // TODO Auto-generated method stub
        return opLeft;
    }
}
