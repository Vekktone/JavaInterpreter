package meatbol;

/** ResultValue class is a data storage structure to assist in the handling of calculations.
 * <p>
 *
 * Stores:
 * 1. variable type (underlying type for arrays)
 * 2. value as a String (convert as necessary)
 * 3. variable structure (primitive/array)
 * 4. terminating string
 *
 * @author Gregory Pugh
 */
public class ResultValue {
    /** Data type of the result */
    public SubClassif type;
    /** Value of the result
     * <p>
     * value is saved as a string regardless of actual type.
     * type cast value for primitives; delimiter separated for
     * arrays. */
    public String value;
    /** Identifier for value type (primitive, fixed array, unbound array)*/
    public int structure;
    /** terminating string for control functions */
    public String terminatingStr;

    //Constants for use in structure
    /** Type for ResultValue structure */
    public final int PRIMITIVE = 0;
    /** Type for ResultValue structure */
    public final int FIXED_ARRAY = 1;
    /** Type for ResultValue structure */
    public final int UNBOUND_ARRAY = 2;

    /** Empty Constructor */
    public ResultValue()
    {

    }

    /** Known value constructor */
    public ResultValue(SubClassif type, String value, int struct, String terminal)
    {
        this.type = type;
        this.value = value;
        this.structure = struct;
        this.terminatingStr = terminal;
    }

    /** Prints ResultValue data for error checking */
    public void printRes()
    {
        System.out.println("Result value: " + type.toString() + " " + value
                + " " + structure + " " + terminatingStr);
    }

    public String makeSimlifiedValue()
    {
        String thisSimplifiedValue = this.value;

        switch (this.type)
        {
            // These cases are already as
            case BOOLEAN: case INTEGER: case STRING:
            break;

            // trim trailing zeros, or even get rid of decimal if it is a whole number
            case FLOAT:
                // TODO
                break;

            // not sure what can be done yet
            case DATE:
                break;
        }

        return thisSimplifiedValue;
    }
}
