package meatbol;

public class ResultValue {

    /** Type for ResultValue structure */
    public final int PRIMITIVE = 0;
    /** Type for ResultValue structure */
    public final int FIXED_ARRAY = 1;
    /** Type for ResultValue structure */
    public final int UNBOUND_ARRAY = 2;

    /** Data type of the result */
    public SubClassif type;
    /** Value of the result
     * value is saved as a string regardless of actual type.
     * type cast value for primitives; delimiter separated for
     * arrays.
     */
    public String value;
    /** Identifier for value type (primitive, fixed array, unbound array)*/
    public int structure;
    /** terminating string for control functions */
    public String terminatingStr;

    /** Empty Constructor */
    public ResultValue() {
        // TODO Auto-generated constructor stub
    }

    /** Known value constructor */
    public ResultValue(SubClassif type, String value, int struct, String terminal)
    {
        this.type = type;
        this.value = value;
        this.structure = struct;
        this.terminatingStr = terminal;
    }

    public void printRes()
    {
        System.out.println(type + value + structure + terminatingStr);
    }
}
