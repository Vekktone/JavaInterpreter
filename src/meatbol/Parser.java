package meatbol;

public class Parser
{
    public Parser(){

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
                System.out.println("***Error: Illegal start to statement***");
                break;
            //Unknown state, throw error
            default:
                System.out.println("***Error: unknown state***");
                break;
        }
    }

    public void conStmt(Scanner scan, SymbolTable symbolTable) throws Exception
    {
        //check what type of control statement we have
        switch(scan.currentToken.subClassif)
        {
            //this is a new identifier
            //Make sure Scanner has added it to SymbolTable,
            //then treat as regular assignment
            case DECLARE:
                //scan.currentToken.printToken();
                if(symbolTable.getSymbol(scan.nextToken.tokenStr) == null)
                {
                    System.out.println("***Error: unknown identifier***");
                }
                scan.getNext();
                //scan.currentToken.printToken();
                System.out.print("***As Declare:***");
                assignStmt(scan, symbolTable);
                break;
            //if we see an end here, then we don't have the flow that
            //starts the control, throw error
            //this won't work right without Flow statements since
            //those statements consume the matching end statements
            case END:
                System.out.println("***Error: END without a FLOW***");
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
                break;
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
                System.out.println("***Error: unknown state***");
                break;
        }


    }
    private void ifStmt(Scanner scan, SymbolTable symbolTable) throws Exception {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
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
                    System.out.println("***Error: Undefined built-in function***");
                }
                break;
            //not handling user functions yet, so this is an error
            case USER:
                System.out.println("***Error: Undefined user function***");
                break;
                //something went wrong
                default:
                    System.out.println("***Error: unknown state***");
                    break;
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
}
