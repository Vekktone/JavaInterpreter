package meatbol;

public class Utility {

        //+, -, U-, *, /, ^, ==, <=, >=, <, >, !=, #
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
    public static String addition(String operandLeft, String operandRight)
    {
        String x;
        x = operandLeft;
        return x;
    }

    public static String subtraction(String operandLeft, String operandRight)
    {
        String x;
        x = operandLeft;
        return x;
    }
}
