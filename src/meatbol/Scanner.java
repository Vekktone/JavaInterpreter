package meatbol;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;


class Scanner {

    private String sourceFileNm;
    private ArrayList<String> sourceLineM;
    private SymbolTable symbolTable;
    private char textCharM[];
    private int iSourceLineNr;
    private int iColPos;
    public Token currentToken;
    private Token nextToken;


    private final static String delimiters = " \t;:()\'\"=!<>+-*/[]#,^\n";
    private final static String operators = "+-*/<>!=#^";
    private final static String separators = "():;[],";
    private final String numerics = "0123456789";
    private final String floats = "0123456789.";
    private boolean openString = false;
    private boolean closeString = false;


    Scanner(String sourceFileNm, SymbolTable symbolTable) {

        this.sourceFileNm = sourceFileNm;
        this.symbolTable = symbolTable;
        this.sourceLineM = new ArrayList<>();

        // Read the specified source file and populate sourceLineM.
        java.util.Scanner scanner = null;
        try {
            scanner = new java.util.Scanner(new File(this.sourceFileNm));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (scanner != null) {
            while(scanner.hasNextLine()) {
                sourceLineM.add(scanner.nextLine());
            }
        }

        // Initializes textCharM, iSourceLineNr, and iColPos.
        this.textCharM = sourceLineM.get(0).toCharArray();
        this.iSourceLineNr = 0;
        this.iColPos = 0;

        // It initializes currentToken and nextToken to new objects (so that the parser doesn't have to check for null).
        this.currentToken = new Token();
        this.nextToken = new Token();

        // It gets the first token into nextToken.

    }

    String getNext(){

        if (iSourceLineNr >= sourceLineM.size()){
            currentToken.primClassif = Classif.EOF;
            return "";
        }else {
            textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
        }

        StringBuilder stringBuilder = new StringBuilder();

        if(iColPos == 0){
            System.out.println("\t" + (iSourceLineNr + 1) + " " + sourceLineM.get(iSourceLineNr));
        }

        while (textCharM.length == 0){
            iSourceLineNr++;
            textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
            System.out.println("\t" + (iSourceLineNr + 1) + " " + sourceLineM.get(iSourceLineNr));
        }

        // Read char array and add to string builder until we get a delimiter
        for (int i = iColPos; i < textCharM.length; i++) {

            if (delimiters.contains(Character.toString(textCharM[i]))) {

                if (textCharM[i] == '\"') {
                    openString = true;
                    iColPos++;
                    stringBuilder.setLength(0);
                    // build the double quoted string
                    while (textCharM[iColPos] != '\"') {
                        try {
                            scanStringLiteral(stringBuilder);
                        } catch (Exception e) {
                            System.out.println(e);
                            System.exit(-1);
                        }
                    }
                    closeString = true;
                    iColPos++;
                }

                if (textCharM[i] == '\'') {
                    openString = true;
                    iColPos++;
                    stringBuilder.setLength(0);
                    // build the single quoted string
                    while (textCharM[iColPos] != '\'') {
                        try {
                            scanStringLiteral(stringBuilder);
                        } catch (Exception e) {
                            System.out.println(e);
                            System.exit(-1);
                        }
                    }
                    closeString = true;
                    iColPos++;
                }

                if (textCharM[i] == ' ' || textCharM[i] == '\n' || textCharM[i] == '\t') {
                    iColPos++;
                }else if (stringBuilder.toString().isEmpty() && textCharM[i] != '\"' && textCharM[i] != '\''){
                    stringBuilder.append(textCharM[i]);
                    iColPos++;
                }

                if (! stringBuilder.toString().isEmpty()) {
                    currentToken.tokenStr = stringBuilder.toString();
                    classifyPrimary(currentToken);
                    try {
                        classifySecondary(currentToken);
                    } catch (Exception e) {
                        System.out.println(e);
                        System.exit(-1);
                    }
                    currentToken.iSourceLineNr = iSourceLineNr;
                    currentToken.iColPos = iColPos;

                    if (iColPos >= textCharM.length) {
                        iColPos = 0;
                        iSourceLineNr++;
                    }

                    openString = false;
                    closeString = false;
                    return stringBuilder.toString();
                }
            } else {
                stringBuilder.append(Character.toString(textCharM[i]));
                iColPos++;
            }
        }

        return null;
    }

    private void classifyPrimary(Token token) {

        if (operators.contains(token.tokenStr)){
            token.primClassif = Classif.OPERATOR;
        } else if (separators.contains(token.tokenStr)) {
            token.primClassif = Classif.SEPARATOR;
        } else {
            token.primClassif = Classif.OPERAND;
        }
    }

    private void classifySecondary(Token token) throws Exception {

        if (numerics.contains(Character.toString(token.tokenStr.charAt(0)))) {
            if (!isInteger(token.tokenStr) && !isFloat(token.tokenStr)) {
                throw new Exception("Line " + (iSourceLineNr + 1) + " Invalid numeric constant: '" + token.tokenStr + "', File: " + sourceFileNm);
            }
        } else if (openString && token.primClassif == Classif.OPERAND){
            token.subClassif = SubClassif.STRING;
        } else if (!openString && token.primClassif == Classif.OPERAND){
            token.subClassif = SubClassif.IDENTIFIER;
        } else {
            token.subClassif = SubClassif.EMPTY;
        }
    }

    private boolean isFloat(String tokenStr) {

        int decimalCount = 0;
        for (char c: tokenStr.toCharArray()) {
            if (! floats.contains(Character.toString(c))) {
                return false;
            }

            if (c == '.'){
                decimalCount++;
                if (decimalCount > 1) return false;
            }
        }
        currentToken.subClassif = SubClassif.FLOAT;
        return true;
    }

    private boolean isInteger(String tokenStr) {

        for (char c: tokenStr.toCharArray()) {
            if (! numerics.contains(Character.toString(c))) {
                return false;
            }
        }
        currentToken.subClassif = SubClassif.INTEGER;
        return true;
    }

    private void scanStringLiteral(StringBuilder stringBuilder) throws Exception {

        if (iColPos >= textCharM.length-1) {
            throw new Exception("Line " + (iSourceLineNr + 1) + " String literal not terminated: '" + stringBuilder + "', File: " + sourceFileNm);
        }

        if (textCharM[iColPos] == '\\') {
            switch (textCharM[iColPos + 1]) {
                case '\'':
                    stringBuilder.append('\'');
                    iColPos += 2;
                    break;
                case '\"':
                    stringBuilder.append('\"');
                    iColPos += 2;
                    break;
                case 't':
                    stringBuilder.append('\t');
                    iColPos += 2;
                    break;
                case 'n':
                    stringBuilder.append('\n');
                    iColPos += 2;
                    break;
                default:
                    stringBuilder.append('\\');
                    iColPos++;
                    break;
            }
        } else {
            stringBuilder.append(textCharM[iColPos]);
            iColPos++;
        }
    }
}
