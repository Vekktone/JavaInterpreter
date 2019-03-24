package meatbol;

public class Parser
{
    public Parser(){

    }

    public void stmt(Scanner scan, SymbolTable symbolTable) throws Exception{
        scan.currentToken.printToken();
        switch(scan.currentToken.primClassif)
        {
            //shouldn't see this, but if it occurs skip it
            case EMPTY:
                System.out.println("Warning: empty token detected");
                break;
            //shouldn't see this, but if it occurs skip it
            case EOF:
                System.out.println("Warning: EOF token detected");
                break;
            case CONTROL:
                conStmt(scan, symbolTable);
                break;
            case FUNCTION:
                funcStmt(scan, symbolTable);
                break;
            case OPERAND:
                assignStmt(scan, symbolTable);
                break;
            //statements can't begin with these, throw error
            case OPERATOR:
            case SEPARATOR:
                System.out.println("Error: Illegal start to statement");
                break;
            //Unknown state, throw error
            default:
                System.out.println("Error: unknown state");
                break;
        }
    }

    public void conStmt(Scanner scan, SymbolTable symbolTable) throws Exception
    {
        System.out.println("Control stmt");
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
    public void funcStmt(Scanner scan, SymbolTable symbolTable) throws Exception
    {
        System.out.println("function stmt");
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
    public void assignStmt(Scanner scan, SymbolTable symbolTable) throws Exception
    {
        System.out.println("assign stmt");
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
}
