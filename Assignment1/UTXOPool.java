import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class UTXOPool {

    /**
     * The current collection of UTXOs, with each one mapped to its corresponding transaction output
     */
    private HashMap<UTXO, Transaction.Output> H; //hashmap is basically a hashtable (maps keys to values) except it accepts null and is unsynchronized

    /** Creates a new empty UTXOPool */
    public UTXOPool() {
        H = new HashMap<UTXO, Transaction.Output>(); //transaction.output links to public key and value, UTXO links to the hash of the transaction that that the UTXO originates from
    }

    /** Creates a new UTXOPool that is a copy of {@code uPool} */
    public UTXOPool(UTXOPool uPool) {
        H = new HashMap<UTXO, Transaction.Output>(uPool.H);
    }

    /** Adds a mapping from UTXO {@code utxo} to transaction output @code{txOut} to the pool */
    public void addUTXO(UTXO utxo, Transaction.Output txOut) {
        H.put(utxo, txOut);
    }

    /** Removes the UTXO {@code utxo} from the pool */
    public void removeUTXO(UTXO utxo) {
        H.remove(utxo);
    }

    /**
     * @return the transaction output corresponding to UTXO {@code utxo}, or null if {@code utxo} is
     *         not in the pool.
     */
    public Transaction.Output getTxOutput(UTXO ut) {
        return H.get(ut);
    }

    /** @return true if UTXO {@code utxo} is in the pool and false otherwise */
    public boolean contains(UTXO utxo) {
        return H.containsKey(utxo);
    }

    /** Returns an {@code ArrayList} of all UTXOs in the pool */
    public ArrayList<UTXO> getAllUTXO() {
        Set<UTXO> setUTXO = H.keySet(); //The keySet() method is used to get a Set view of the keys contained in this map. ref: https://www.tutorialspoint.com/java/util/hashmap_keyset.htm, A collection that contains no duplicate elements. More formally, sets contain no pair of elements e1 and e2 such that e1.equals(e2), and at most one null element. As implied by its name, this interface models the mathematical set abstraction, ref : https://docs.oracle.com/javase/7/docs/api/java/util/Set.html
        ArrayList<UTXO> allUTXO = new ArrayList<UTXO>();
        for (UTXO ut : setUTXO) {
            allUTXO.add(ut);
        }
        return allUTXO;
    }
}
