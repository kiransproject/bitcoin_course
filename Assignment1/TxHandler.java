import java.util.*;

public class TxHandler {


	private UTXOPool ledger;
	
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        ledger = new UTXOPool(utxoPool);
    }

	public TxHandler(){ // if no arguemnts passed
        ledger = new UTXOPool();
    }
			

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
		
		if(!(checkAllOutputsClaimed(tx))){
			return false;
		}
		else if (!(checkAllSignatures(tx))){
				return false;
		}		
		else if(!(checkForDuplicateUTXO(tx))){
				return false;
		}
		else if(!(checkOutputValues(tx))){
				return false;
		}
		else if(!(checkSumValues(tx))){
				return false;
		}
		return true;
	}
	
	public boolean checkAllOutputsClaimed(Transaction tx) {
			UTXO temputxo;	
			for(Transaction.Input in : tx.getInputs()) { // cycle through all inputs, assigning each one to the object in which is of class Transaction.Input
				temputxo = new UTXO(in.prevTxHash, in.outputIndex);
				if (!( ledger.contains(temputxo))){
					return false;
				}
			}
			
			return true;
	}

	public boolean checkAllSignatures(Transaction tx){
			
			Transaction.Output out;
			Transaction.Input in;
			UTXO sigUTXO;

			for(int i=0; i<tx.numInputs();i++) {
				in = tx.getInput(i);
				sigUTXO = new UTXO(in.prevTxHash, in.outputIndex);
				out = ledger.getTxOutput(sigUTXO);
				if (!( Crypto.verifySignature(out.address, tx.getRawDataToSign(i), in.signature))){
					return false;
				}
			}
			return true;
	}

	public boolean checkForDuplicateUTXO(Transaction tx){

			ArrayList<Transaction.Input> allInputs = new ArrayList<Transaction.Input>(tx.getInputs());
			Set<Transaction.Input> setInputs = new HashSet<Transaction.Input>(allInputs);

			if(setInputs.size() < allInputs.size()) {
					return false;
			}
			return true;
	}

	public boolean checkOutputValues(Transaction tx) {
			
			Transaction.Output outv;
			for(int i=0; i<tx.numOutputs();i++) {
				outv = tx.getOutput(i);
				if (outv.value < 0) {
					return false;
				}
			}
			return true;
	}

	public boolean checkSumValues(Transaction tx) {
			UTXO tempsumUTXO;
			Transaction.Input inp;
			Transaction.Output outp;
			double totalinput,totaloutput;
			totalinput = 0;
			totaloutput = 0;

			for(int i=0; i<tx.numInputs();i++) {
						inp = tx.getInput(i);
						tempsumUTXO = new UTXO(inp.prevTxHash, inp.outputIndex);
						outp = ledger.getTxOutput(tempsumUTXO); // returns the transacation output in relation to that UTXO
						totalinput += outp.value;
							
			}
			
			for(Transaction.Output k : tx.getOutputs()){
				totaloutput += k.value;
			}
			/*
			if ((Double.compare (totalinput ,totaloutput))==0){
					return true;
			}
			else {
					return false;
			}
			*/
			return (totalinput >= totaloutput);
	}
			


    /*
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
   */ 
    public Transaction[] handleTxs(Transaction[] possibleTxs) { // hanldestxs in an array composed of transcation objects, same for possibleTx
			ArrayList<Transaction> acceptedTxs = new ArrayList<Transaction>();
			ArrayList<Transaction> possTxs = new ArrayList<Transaction>(Arrays.asList(possibleTxs));
			Transaction.Input inp;
			Transaction[] fTx;
			
			UTXO tempUTXO;
			
			while (possTxs.size() > 0) {
					for (int j =0;j<possTxs.size();j++){
							//Transaction tempTX = new Transaction[possTxs.size()];
							//Transaction tempTX = possTxs.get(j);
							if (isValidTx(possTxs.get(j))){
									acceptedTxs.add(possTxs.get(j));
									for (int k=0;k<((possTxs.get(j)).numInputs());k++){
											inp = possTxs.get(j).getInput(k);
											tempUTXO = new UTXO(inp.prevTxHash, inp.outputIndex);
											ledger.removeUTXO(tempUTXO);
									}
							}
							possTxs.remove(j);
					}
			}

			fTx = acceptedTxs.toArray(new Transaction[acceptedTxs.size()]);
			return fTx;
    } 

}
