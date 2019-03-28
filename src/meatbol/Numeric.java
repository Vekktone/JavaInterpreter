package meatbol;

public class Numeric {

    public static ResultValue convertToken(Token token) {
        return new ResultValue(token.subClassif,token.tokenStr,0,null);
    }

    public static float toFloat(ResultValue resultValue) {
        float value = Float.parseFloat(resultValue.value);
        return value;
    }

    public static int toInt(ResultValue resultValue) {
        int value = Integer.parseInt(resultValue.value);
        return value;
    }

    public static String intToString(int intValue) {
        String value = "" + intValue;
        return value;
    }

    public static String floatToString(float floatValue) {
        String value = "" + floatValue;
        return value;
    }

}
