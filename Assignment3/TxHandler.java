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
	
	// added for assignment 3
	public UTXOPool getUTXOPool() {
		return ledger;
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
				temputxo = new UTXO(in.prevTxHash, in.outputIndex); // create UTXO
				if (!( ledger.contains(temputxo))){ // check if its within the UTXOpool
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
				sigUTXO = new UTXO(in.prevTxHash, in.outputIndex); //get a UTXO
				out = ledger.getTxOutput(sigUTXO); // get the output Transaction associated with the UTXO
				if (!( Crypto.verifySignature(out.address, tx.getRawDataToSign(i), in.signature))){ // pass in the public address from Transacation Output, the Signature which is from Transaction Input, getting the message is from the current transaction
					return false;
				}
			}
			return true;
	}

	public boolean checkForDuplicateUTXO(Transaction tx){
			
			UTXO curutxo;
			Set<UTXO> usedTxs = new HashSet<>();
			
			for(Transaction.Input in : tx.getInputs()) { // cycle through each input, create a UTXO, then check if its within the SET pool which doesnt take duplicates and if not adds it
				curutxo = new UTXO(in.prevTxHash, in.outputIndex);
				if(!(usedTxs.contains(curutxo))){
						usedTxs.add(curutxo);
				}
				else {
						return false;
				}
				
			}
			return true;
	}

	public boolean checkOutputValues(Transaction tx) {
			
			Transaction.Output outv;
			for(int i=0; i<tx.numOutputs();i++) { // checks if any output values are negative
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
						totalinput += outp.value; // adds together the values associated with the inputs in our current tx
							
			}
			
			for(Transaction.Output k : tx.getOutputs()){
				totaloutput += k.value; // gets the output transaction value
			}
			return (totalinput >= totaloutput);
	}
			


    /*
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
   */ 
    public Transaction[] handleTxs(Transaction[] possibleTxs) { // hanldestxs in an array composed of transcation objects, same for possibleTx
			if (possibleTxs == null){
					return new Transaction[0]; // return array of 0 transactions if null
			}

			ArrayList<Transaction> acceptedTxs = new ArrayList<Transaction>();
			ArrayList<Transaction> possTxs = new ArrayList<Transaction>(Arrays.asList(possibleTxs));
			Transaction.Input inp;
			Transaction[] fTx;
			int j =0;
			UTXO tempUTXO;
			
			while (possTxs.size() > j) { // doesnt stop until j is greate then the number of transactions
					if (isValidTx(possTxs.get(j))){
							acceptedTxs.add(possTxs.get(j)); // if its a valid transaction add it to the local pool
							for (int k=0;k<((possTxs.get(j)).numInputs());k++){ // remove the priorly asscoiated Transacation from the current UTXO pool
									inp = possTxs.get(j).getInput(k);
									tempUTXO = new UTXO(inp.prevTxHash, inp.outputIndex);
									ledger.removeUTXO(tempUTXO);
							}
							for( int f =0;f<((possTxs.get(j)).numOutputs());f++){ // add the new valid transaction to the UTXO pool 
									Transaction.Output out = possTxs.get(j).getOutput(f);
									UTXO utxo = new UTXO(possTxs.get(j).getHash(), f);
									ledger.addUTXO(utxo, out);
							}
							possTxs.remove(j); // remove from the array list if valid
					}
					else {
							j++; // if not valid add one to move onto the next one
					}
					
			}

			fTx = acceptedTxs.toArray(new Transaction[acceptedTxs.size()]);
			return fTx;
    } 

}
