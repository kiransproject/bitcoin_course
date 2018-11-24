import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class Crypto {

    /**
     * @return true is {@code signature} is a valid digital signature of {@code message} under the
     *         key {@code pubKey}. Internally, this uses RSA signature, but the student does not
     *         have to deal with any of the implementation details of the specific signature
     *         algorithm
     */
    public static boolean verifySignature(PublicKey pubKey, byte[] message, byte[] signature) {
        Signature sig = null;
        try {
            sig = Signature.getInstance("SHA256withRSA"); //Returns a Signature object that implements the specified signature algorithm.  - https://docs.oracle.com/javase/7/docs/api/java/security/Signature.html#getInstance(java.lang.String)
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sig.initVerify(pubKey); // checks if the key is valid, through initializing the object for verification - https://docs.oracle.com/javase/7/docs/api/java/security/Signature.html#initVerify(java.security.PublicKey)
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try {
            sig.update(message); //signs the message
            return sig.verify(signature); //verifies the passed in signature, assuming it compares it to the one created in sig
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return false;

    }
}
