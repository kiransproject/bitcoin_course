// Example of a Simulation. This test runs the nodes on a random graph.
// At the end, it will print out the Transaction ids which each node
// believes consensus has been reached upon. You can use this simulation to
// test your nodes. You will want to try creating some deviant nodes and
// mixing them in the network to fully test.

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.HashMap;

public class Simulation {

   public static void main(String[] args) {

      // There are four required command line arguments: p_graph (.1, .2, .3),
      // p_malicious (.15, .30, .45), p_txDistribution (.01, .05, .10), 
      // and numRounds (10, 20). You should try to test your CompliantNode
      // code for all 3x3x3x2 = 54 combinations.

      int numNodes = 100;
      double p_graph = Double.parseDouble(args[0]); // parameter for random graph: prob. that an edge will exist, en edge being a trusted relationship, i.e. A-> B edge means that B listen to transaction broadcast by node A
      double p_malicious = Double.parseDouble(args[1]); // prob. that a node will be set to be malicious
      double p_txDistribution = Double.parseDouble(args[2]); // probability of assigning an initial transaction to each node 
      int numRounds = Integer.parseInt(args[3]); // number of simulation rounds your nodes will run for

      // pick which nodes are malicious and which are compliant
      Node[] nodes = new Node[numNodes];
      for (int i = 0; i < numNodes; i++) {
         if(Math.random() < p_malicious) // when math.random is smaller than the probability of a malicious node then it becomes one, if not its a compliant node
            // When you are ready to try testing with malicious nodes, replace the
            // instantiation below with an instantiation of a MaliciousNode
            nodes[i] = new MaliciousNode(p_graph, p_malicious, p_txDistribution, numRounds); // MalDoNothing seems to point to nothing, so i think this needs to be replaced by MaliciousNode 
         else
            nodes[i] = new CompliantNode(p_graph, p_malicious, p_txDistribution, numRounds);
      }


      // initialize random follow graph
      boolean[][] followees = new boolean[numNodes][numNodes]; // followees[i][j] is true if i follows j
      for (int i = 0; i < numNodes; i++) {
         for (int j = 0; j < numNodes; j++) { 
            if (i == j) continue; // if they are both equal to the same number then you are looking at the same node so skip
            if(Math.random() < p_graph) { // p_graph is .1, .2, or .3, Math.random() returns a number from zero to one, this step is evaluating whether an edge exists and if so set both to true, if not just leave it
               followees[i][j] = true;
            }
         }
      }

      // notify all nodes of their followees
      for (int i = 0; i < numNodes; i++)
         nodes[i].setFollowees(followees[i]); // by only referencing i, it means send all of the j values - https://stackoverflow.com/questions/40800450/java-what-does-the-first-index-represent-in-2d-array

      // initialize a set of 500 valid Transactions with random ids
      int numTx = 500;
      HashSet<Integer> validTxIds = new HashSet<Integer>(); //hashset can not contain duplicates as its a set, its an implementation of a hashtable : https://stackoverflow.com/questions/9119840/how-do-hashsets-in-java-work
      Random random = new Random(); // An instance of this class is used to generate a stream of pseudorandom numbers.
      for (int i = 0; i < numTx; i++) {
         int r = random.nextInt(); // Returns the next pseudorandom, uniformly distributed int value from this random number generator's sequence.
         validTxIds.add(r); 
      }


      // distribute the 500 Transactions throughout the nodes, to initialize
      // the starting state of Transactions each node has heard. The distribution
      // is random with probability p_txDistribution for each Transaction-Node pair.
      for (int i = 0; i < numNodes; i++) {
         HashSet<Transaction> pendingTransactions = new HashSet<Transaction>();
         for(Integer txID : validTxIds) { // for each loop, so txID is the ref value and it will loop through all validTxIds
            if (Math.random() < p_txDistribution) // p_txDistribution is .01, .05, or .10., this is the probability of assigning a transaction to each node
               pendingTransactions.add(new Transaction(txID));
         }
         nodes[i].setPendingTransaction(pendingTransactions); // each node gets its own TX list, as the pendingTransactions get initialized as new after deciding the node number on line 68
      }


      // Simulate for numRounds times
      for (int round = 0; round < numRounds; round++) { // numRounds is either 10 or 20

         // gather all the proposals into a map. The key is the index of the node receiving
         // proposals. The value is an ArrayList containing 1x2 Integer arrays. The first
         // element of each array is the id of the transaction being proposed and the second
         // element is the index # of the node proposing the transaction.
         HashMap<Integer, Set<Candidate>> allProposals = new HashMap<>(); // hashmap allows nulls and is unsynchronized  and does not guarantee order over time, Candidate is defined as a class above, containing the TXID and the sender 

         for (int i = 0; i < numNodes; i++) {
            Set<Transaction> proposals = nodes[i].sendToFollowers(); // sendToFollowers should return all the proposed transactions to send to followers apart from on the final round , in which it sends the consencus transactions
            for (Transaction tx : proposals) {
               if (!validTxIds.contains(tx.id))
                  continue; // ensure that each tx is actually valid

               for (int j = 0; j < numNodes; j++) {
                  if(!followees[j][i]) continue; // tx only matters if j follows i, i.e. skip if not true

                  if (!allProposals.containsKey(j)) { // j denotes the index of any followers of i , so if j is already within the array we dont need to add a new candidate can just add to the old one as per below on line 100
                	  Set<Candidate> candidates = new HashSet<>();
                	  allProposals.put(j, candidates);
                  }
                  
                  Candidate candidate = new Candidate(tx, i);
                  allProposals.get(j).add(candidate); // add the potential candidate to all the proposals, where the key is the follower, as thats where it will be sent
               }

            }
         }

         // Distribute the Proposals to their intended recipients as Candidates
         for (int i = 0; i < numNodes; i++) {
            if (allProposals.containsKey(i))
               nodes[i].receiveFromFollowees(allProposals.get(i)); // see line 101 if doesnt make sense
         }
      }

      // print results
      for (int i = 0; i < numNodes; i++) {
         Set<Transaction> transactions = nodes[i].sendToFollowers();
         System.out.println("Transaction ids that Node " + i + " believes consensus on:");
         for (Transaction tx : transactions)
            System.out.println(tx.id);
         System.out.println();
         System.out.println();
      }

   }


}

