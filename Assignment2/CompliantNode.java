import java.util.ArrayList;
import java.util.Set;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node { // implements means we implement the methods defined in the Node interface - https://stackoverflow.com/questions/10839131/implements-vs-extends-when-to-use-whats-the-difference

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        // IMPLEMENT THIS
		double probgraph = p_graph;
		double probmalic = p_malicious;
		double probdist = p_txDistribution;
		int rounds = numRounds;
    }
	
	Set<Transaction> Transactions;

    public void setFollowees(boolean[] followees) {
        boolean[] node_followess = followees;
		// IMPLEMENT THIS
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        // IMPLEMENT THIS
		Transactions = pendingTransactions;
    }

    public Set<Transaction> sendToFollowers() {
        // IMPLEMENT THIS
		return Transactions;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        // IMPLEMENT THIS
		for (Candidate can: candidates) {
			Transactions.add(can.tx);
		}
    }
}
