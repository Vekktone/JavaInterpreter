package meatbol;

import java.util.StringTokenizer;

/** Numeric is used to standardize all of our conversions.
 * <p>
 * Numeric is a class that has a collection of static functions used to
 * convert between ints, floats, Stings, and Doubles. It also converts
 * Toknes to resultValues.
 *
 * @author Mason Pohler
 */
public class Numeric {

    /** Converts a token to a result value.
     * <p>
     * Uses ResultValue's constructor and passes the relevant information
     * from token.
     * @param token a Token to convert to a ResultValue.
     * @return a ResultValue containing relevant information for the Token.
     *
     * @author Mason Pohler
     */
    public static ResultValue convertToken(Token token) {
        return new ResultValue(token.subClassif,token.tokenStr,0,null);
    }

    /** Converts a ResultValue to a float.
     * <p>
     * Uses Float's parseFloat method to parse a float from the ResultValue's
     * value String.
     * @param resultValue a ResultValue to convert to a Float.
     * @return a float value representing the converted value of the ResultValue.
     *
     * @author Mason Pohler
     */
    public static float toFloat(ResultValue resultValue) {
        float value = Float.parseFloat(resultValue.value);
        return value;
    }

    /** Converts a ResultValue to an int.
     * <p>
     * Uses Float's parseFloat method to parse a float from the ResultValue's
     * value String. Then it truncates the float into an int using casting.
     * @param resultValue a ResultValue to convert to an int.
     * @return an int value representing the converted value of the ResultValue.
     *
     * @author Mason Pohler
     */
    public static int toInt(ResultValue resultValue) {
        int value = (int) Float.parseFloat(resultValue.value);
        return value;
    }

    /** Converts an int to a String.
     * <p>
     * Converts an int to a String by concatenating the int to an empty String.
     * @param intValue an int to convert to a String.
     * @return a String representation of an int.
     *
     * @authot Mason Pohler
     */
    public static String intToString(int intValue) {
        String value = "" + intValue;
        return value;
    }

    /** Converts a float to a String.
     * <p>
     * Converts a float to a String by concatenating the float to an empty String.
     * @param floatValue a float to convert to a String.
     * @return a String representation of a float.
     *
     * @author Mason Pohler
     */
    public static String floatToString(float floatValue) {
        String value = "" + floatValue;
        return value;
    }

    /** Converts a float to a double without changing the precision.
     * <p>
     * Creates a String representation of the float, then creates a double from
     * the String using Double.valueOf(String).
     * @param floatValue a float value to convert to a double with the same precision.
     * @return a double value with the same precision as the original float value.
     *
     * @author Mason Pohler
     */
    public static double floatToDouble(float floatValue) {
        //return Double.valueOf(Float.valueOf(value).toString()).doubleValue();
        String floatString = Float.valueOf(floatValue).toString();
        double doubleValue = Double.valueOf(floatString);
        return doubleValue;
    }

    public static int dateToEpoch(String value, int line) throws ParserException {
        StringTokenizer st = new StringTokenizer(value,"-");
        int year, day, month;
        int epoch, i = 0;
        int[] maxDays = {0,31,59,90,120,151,181,212,243,273,304,334};

        //attempt to parse, failure means this is not a valid date
        try
        {
            year = Integer.parseInt(st.nextToken());
            month = Integer.parseInt(st.nextToken());
            day = Integer.parseInt(st.nextToken());
        }
        catch (Exception e)
        {
            throw new ParserException(line
                    ,"***Error: Invalid date - " + value + "***"
                    , Meatbol.filename);
        }
        //epoch for year
        epoch = (year/4)*1461;
        epoch += ((year%4) * 365);
        //full months
        epoch += maxDays[month - 1];
        //leap year
        if((year%4) == 0 && (month > 2 || (month == 2 && day == 29)))
        {
            epoch++;
        }
        epoch += day;
        return epoch;
    }

    public static String epochToDate(int epoch){
        int year = 0, day = 0, month = 0, i;
        int[] maxDays = {0,31,59,90,120,151,181,212,243,273,304,334,365};
        String string;
        //year increments
        year = (epoch/1461) * 4;
        epoch -= (year/4 * 1461);

        while (epoch > 366)
        {
                year++;
                epoch -= 365;
        }

        //find month and day
        for(i = 0; i<13; i++)
        {
            if (year % 4 == 0 && epoch > 59)
            {
                if(epoch <= (maxDays[i] + 1))
                {
                    month = i;
                    day = epoch - maxDays[i-1] - 1;
                    break;
                }
            }
            else
            {
                if(epoch <= maxDays[i])
                {
                    month = i;
                    day = epoch - maxDays[i-1];
                    break;
                }
            }
        }
        string = String.format("%d-",year);
        if(month<10)
        {
            string = string.concat(String.format("0%d-",month));
        }
        else
        {
            string =string.concat(String.format("%d-",month));
        }
        if(day<10)
        {
            string =string.concat(String.format("0%d",day));
        }
        else
        {
            string =string.concat(String.format("%d",day));
        }
        return string;
    }
}
