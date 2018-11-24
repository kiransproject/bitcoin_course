
import java.util.Arrays;

/** a wrapper for byte array with hashCode and equals function implemented */
public class ByteArrayWrapper {

    private byte[] contents;

    public ByteArrayWrapper(byte[] b) {
        contents = new byte[b.length];
        for (int i = 0; i < contents.length; i++)
            contents[i] = b[i];
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (getClass() != other.getClass()) { - returns run time class - https://www.tutorialspoint.com/java/lang/object_getclass.htm
            return false;
        }

        ByteArrayWrapper otherB = (ByteArrayWrapper) other; // byte array wrapper in brackets is a cast, telling the compiler its okay to cast other as a Byte array wrapper  - https://stackoverflow.com/questions/11728356/why-are-parentheses-used-around-a-class-name-in-java
        byte[] b = otherB.contents;
        if (contents == null) {
            if (b == null)
                return true;
            else
                return false;
        } else {
            if (b == null)
                return false;
            else {
                if (contents.length != b.length)
                    return false;
                for (int i = 0; i < b.length; i++)
                    if (contents[i] != b[i])
                        return false;
                return true;
            }
        }
    }

    public int hashCode() { // - https://www.tutorialspoint.com/java/util/arrays_hashcode_int.htm , provides hash of array 
        return Arrays.hashCode(contents);
    }
}
