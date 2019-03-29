package meatbol;

public enum Classif
{
    EMPTY, // empty
    OPERAND, // constants, identifier
    OPERATOR, // + - * / < > = !
    SEPARATOR, // ( ) , : ; [ ]
    FUNCTION, // TBD
    CONTROL, // TBD
    DEBUG, // debugger tokens
    EOF // EOF encountered
}
