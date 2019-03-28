package meatbol;

public class Numeric {

    public static ResultValue convertToken(Token token) {
        return new ResultValue(token.subClassif,token.tokenStr,0,null);
    }
    
    public static ResultValue convertInteger(ResultValue resultValue) {
    	
    	return null;
    	
    }

    public static float toFloat(ResultValue opRight) {

        return 0.0f;
    }

    public static int toInt(ResultValue opLeft) {
        // TODO Auto-generated method stub
        return 0;
    }



}
