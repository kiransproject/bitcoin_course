// Block Chain should maintain only limited block nodes to satisfy the functions
// You should not have all the blocks added to the block chain in memory 
// as it would cause a memory overflow.
// https://github.com/msilb/coursera-cryptocurrency/blob/master/assignment-3-blockchain/BlockChain.java - was used for reference

import java.util.ArrayList;
import java.util.HashMap;

public class BlockChain {
    public static final int CUT_OFF_AGE = 10;

	
	public class BlockNode{
		
		private UTXOPool uPool;
		public Block b;
		public BlockNode parent;
		public ArrayList<BlockNode> children;
		public int height;

		public BlockNode(Block b, BlockNode parent, UTXOPool uPool) {
				this.b = b; //https://docs.oracle.com/javase/tutorial/java/javaOO/thiskey.html, use of this avoids extra variables in constructor
				this.parent = parent;
				this.uPool = uPool;
				children = new ArrayList<>();
				if (parent != null) {
						height = parent.height + 1;
						parent.children.add(this);
				} else {
						height =1;
				}
		}


		public UTXOPool getUTXOPoolCopy () {
			return new UTXOPool(uPool);
		}
	}

	private HashMap<ByteArrayWrapper, BlockNode> blockChain;
	private BlockNode maxHeightNode;
	private TransactionPool txPool; 
	
	 /**
     * create an empty block chain with just a genesis block. Assume {@code genesisBlock} is a valid
     * block
     */
	 
    public BlockChain(Block genesisBlock) {
			blockChain = new HashMap<>(); //create empty hash map
			UTXOPool utxoPool = new UTXOPool(); // create emtpty utxopool as intial TX
			addCoinbasetoUTXOPOOL(genesisBlock, utxoPool); // add the coinbase within the genesis block to the UTXOPool
			BlockNode gennode = new BlockNode(genesisBlock, null, utxoPool);
			blockChain.put((wrap(genesisBlock.getHash())),gennode); // put is how we add to a HashMap
			txPool = new TransactionPool();
			maxHeightNode = gennode; // should be the first block at this point
    }

    /** Get the maximum height block */
    public Block getMaxHeightBlock() {
			return maxHeightNode.b;
    }

    /** Get the UTXOPool for mining a new block on top of max height block */
    public UTXOPool getMaxHeightUTXOPool() {
			return maxHeightNode.getUTXOPoolCopy();
    }

    /** Get the transaction pool to mine a new block */
    public TransactionPool getTransactionPool() {
		return txPool;
    }

    /**
     * Add {@code block} to the block chain if it is valid. For validity, all transactions should be
     * valid and block should be at {@code height > (maxHeight - CUT_OFF_AGE)}.
     * 
     * <p>
     * For example, you can try creating a new block over the genesis block (block height 2) if the
     * block chain height is {@code <=
     * CUT_OFF_AGE + 1}. As soon as {@code height > CUT_OFF_AGE + 1}, you cannot create a new block
     * at height 2.
     * 
     * @return true if block is successfully added
     */
    public boolean addBlock(Block block) {
		byte[] prevblock = block.getPrevBlockHash();
        if (prevblock == null)
            return false;
        BlockNode parentnode = blockChain.get(wrap(prevblock));
        if (parentnode == null) {
            return false;
        }
        TxHandler txhandler = new TxHandler(parentnode.getUTXOPoolCopy());
        Transaction[] txs = block.getTransactions().toArray(new Transaction[0]); // get the transaction and convert from array list into byte array, with new Transaction meaning that the objects within the byte array will be on type transation instead of just objects, and the [0] denoting to match the size the number of transactions - https://www.codenameone.com/blog/the-toarraynew-array-antipattern.html
        Transaction[] validTxs = txhandler.handleTxs(txs);
        if (validTxs.length != txs.length) {
            return false;
        }
        if (parentnode.height + 1 <= maxHeightNode.height - CUT_OFF_AGE) {
            return false;
        }
        UTXOPool utxopoool = txhandler.getUTXOPool();
        addCoinbasetoUTXOPOOL(block, utxopoool);
        BlockNode node = new BlockNode(block, parentnode, utxopoool);
        blockChain.put(wrap(block.getHash()), node);
        if (parentnode.height + 1 > maxHeightNode.height) {
            maxHeightNode = node;
        }
		return true;
    }

    /** Add a transaction to the transaction pool */
    public void addTransaction(Transaction tx) {
			txPool.addTransaction(tx);
    }

	private void addCoinbasetoUTXOPOOL(Block block, UTXOPool utpool) {
			Transaction coinbase = block.getCoinbase(); // as coinbase is a TX, see block class
			for (int i =0;i<coinbase.numOutputs();i++) {
				Transaction.Output out = coinbase.getOutput(i);
				UTXO utxo = new UTXO(coinbase.getHash(),i); //use new if objects involved arent already initialised/created, i.e. getOutput(i) already exists as an object, where as the UTXO being assisnged to utxo doent currently exist
				utpool.addUTXO(utxo, out);
			}
	}

	private static ByteArrayWrapper wrap(byte[] b){
			return new ByteArrayWrapper(b);
	}
}
