package pa1;

import java.util.ArrayList;
import java.util.Arrays;

public class Permutation {
    private int n;
    private int k;
    private boolean allowEmptySpots;

    private long count;
    private int[] workerCounts;
    private int[] pairing;
    private boolean done;

    /**
     * Initialize a permutation which will select pairings of worker to jobs.
     * There should be at most p pairings. No pairing should be used more than
     * once. No worker should be used more than once.
     * 
     * @param n
     *            Number of worker.
     * @param k
     *            Number of jobs available.
     */
    public Permutation(int n, int k, boolean allowEmptyPositions) {
        this.n = n;
        this.k = k;
        this.allowEmptySpots = allowEmptyPositions;

        this.count = 0;

        this.workerCounts = new int[this.n];
        Arrays.fill(this.workerCounts, 0);
        this.pairing = new int[this.k];

        if (allowEmptyPositions) {
            Arrays.fill(this.pairing, -1);
        } else {
            Arrays.fill(this.pairing, 0);
            this.workerCounts[0] = this.pairing.length;
        }

        this.done = false;
    }

    public Permutation(int n, int k) {
        this(n, k, false);
    }

	/**
	 * Return the next permutation as a Matching object, representing a solution
	 * to the Stable Matching problem contained in data.
	 * 
	 * @param data
	 *            The Matching object containing the problem to solve.
	 * @return The next candidate solution to the problem.
	 */
    public Matching getNextMatching(Matching data) {
        int[] pairing = getNextPairing();
        if (pairing == null) {
            return null;
        }

        Matching matching = convertPairingToMatching(data, pairing);
        return matching;
    }

    /**
     * Return a Matching (StableMarriage candidate) constructed from the int[]
     * generated by getNextPairing().
     * 
     * @param data
     *            The Matching object containing the problem to solve.
     * @param pairing
     *            The candidate solution as generated by getNextPairing.
     * @return The Matching corresponding to the provided pairing.
     */
    private Matching convertPairingToMatching(Matching data, int[] pairing) {

        int m = data.getJobCount();
        int n = data.getWorkerCount();

        int pairing_index = 0;
        ArrayList<Integer> worker_matching = new ArrayList<Integer>(0);
        for (int i = 0; i < n; i++) {
            worker_matching.add(-1);
        }

        for (int i = 0; i < m; i++) {
            if (pairing[pairing_index] != -1) {
                worker_matching.set(pairing[pairing_index], i);
            }

            if (pairing_index == pairing.length) {
                break;
            }

            pairing_index++;
        }

        return new Matching(data, worker_matching);
    }

    /**
     * Select the next pairing in "ascending order." Each worker can be
     * assigned to at most one spot, and each spot can be assigned to at most
     * one worker, and there are more worker than there are spots. Therefore,
     * the the length of the returned array will be k, and each spot in the
     * array will contain the index of worker which will fill that spot.
     * 
     * Values for the worker range from 0 to n-1, so let the counting system
     * be a base-n counting system. Each spot in the array must contain an
     * integer in [0,n-1) and when a position in the array wraps around, the
     * next highest position is incremented.
     * 
     * Keep track of the number of instances of a given worker's number are
     * present in the pairing. A pairing is only valid if each worker is paired
     * with a position at most 1 time.
     */
    private int[] getNextPairing() {
        if (done) {
            return null;
        }

        count++;

        do {
            incrementPairing();
        } while (!isValidPairing());

        int[] nextPairing = new int[pairing.length];
        System.arraycopy(pairing, 0, nextPairing, 0, pairing.length);
        return nextPairing;
    }

    private boolean isValidPairing() {
        for (int x : workerCounts) {
            if (x > 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Increment one position of the pairing and update arrays appropriately.
     * 
     * @param index
     *            The index of the array to increment.
     */
    private void incrementPairing(int index) {
        if (index == 0) {
            count++;
        }

        if (index >= pairing.length) {
            done = true;
            return;
        }

        removeWorker(pairing[index]);

        // update worker in matching
        pairing[index]++;
        if (pairing[index] >= n) {
            if (allowEmptySpots) {
                pairing[index] = -1;
            } else {
                pairing[index] = 0;
            }

            incrementPairing(index + 1);
        }

        addWorker(pairing[index]);
    }

    private void incrementPairing() {
        incrementPairing(0);
    }

    private void updateWorker(int worker, int update) {
        if (worker == -1 || worker >= n) {
            return;
        }

        workerCounts[worker] += update;
    }

    private void removeWorker(int worker) {
        updateWorker(worker, -1);
    }

    private void addWorker(int worker) {
        updateWorker(worker, 1);
    }
}
