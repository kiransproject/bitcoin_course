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
            sig = Signature.getInstance("SHA256withRSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sig.initVerify(pubKey); // tell the signature object that its going to be uses for versification of a signature using the public key
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try {
            sig.update(message); //Updates the data to be signed or verified by a byte., with message the bytes used for the update https://docs.oracle.com/javase/7/docs/api/java/security/Signature.html#update(byte)
            return sig.verify(signature); //verifies the passed in signature ref: https://docs.oracle.com/javase/7/docs/api/java/security/Signature.html#verify(byte[]), using the public key in the initVerify function
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return false;

    }
}
