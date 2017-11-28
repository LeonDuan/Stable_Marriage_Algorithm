package pa1;/*
 * Name: 
 * EID:
 */

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Your solution goes in this class.
 * 
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 * 
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */
public class Program1 extends AbstractProgram1 {
    /**
     * Determines whether a candidate Matching represents a solution to the
     * Stable Marriage problem. Study the description of a Matching in the
     * project documentation to help you with this.
     */
    public boolean isStableMatching(Matching marriage) {
        int total = marriage.getWorkerCount();
        ArrayList<Integer> matching = marriage.getWorkerMatching();

        //iterate through all workers in the matching
        for(int worker = 0; worker < total; worker++){
            ArrayList<Integer> w_pref_list = marriage.getWorkerPreference().get(worker);
            int w_curr_job = matching.get(worker);
            for(int i = 0; i < total; i++){
                int potential_job = w_pref_list.get(i);
                if(potential_job == w_curr_job) {//can't find a better upon reaching worker's current job
                    break;
                }
                else{
                    int job_curr_worker = matching.indexOf(potential_job);
                    ArrayList<Integer> j_pref_list = marriage.getJobPreference().get(potential_job);
                    if(j_pref_list.indexOf(worker) < j_pref_list.indexOf(job_curr_worker)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines a solution to the Stable Marriage problem from the given input
     * set. Study the project description to understand the variables which
     * represent the input to your solution.
     * 
     * @return A stable Matching.
     */
    public Matching stableHiringGaleShapley(Matching marriage) {
        //initialize matching; everyone is jobless
        ArrayList<ArrayList<Integer>> preserved_worker_pref_list = new ArrayList<>();
        for(int i = 0; i < marriage.getJobCount();i++){
            ArrayList<Integer> temp = new ArrayList<>();
            for(int j = 0; j < marriage.getJobCount(); j++){
                temp.add(marriage.getWorkerPreference().get(i).get(j));
            }
            preserved_worker_pref_list.add(temp);
        }
        ArrayList<Integer> worker_matching = new ArrayList<>();
        ArrayList<Integer> job_matching = new ArrayList<>();
        for (int i = 0; i < marriage.getWorkerCount(); i++){
            worker_matching.add(-1);
            job_matching.add(-1);
        }

        ArrayList<Integer> joblessWorker = new ArrayList<>();//a list that contains the jobless workers.
        for (int i = 0; i < marriage.getWorkerCount(); i++){
            joblessWorker.add(i);
        }

//        //truncate the lazy workers' preference lists to increase performance
//        int num_hardworking = 0;
//        ArrayList<Boolean> hardworking = marriage.getWorkerHardworking();
//        for(int i = 0; i < hardworking.size(); i ++){
//            if(hardworking.get(i) == true){
//                num_hardworking ++;
//            }
//        }
//        if(!(num_hardworking == 0)){
//            for(int i = 0; i < hardworking.size(); i++) {
//                if (hardworking.get(i) == false) {
//                    marriage.getWorkerPreference().get(i).subList(0,num_hardworking).clear();
//                }
//            }
//        }


        ArrayList<Boolean> jobTaken = new ArrayList<>();
        for(int i = 0; i < marriage.getJobCount(); i++){
            jobTaken.add(false);
        }

        ArrayList<ArrayList<Integer>> jobPreferenceByWorkerNum = new ArrayList<>();
        for(int i = 0; i < marriage.getJobCount(); i++){
            ArrayList<Integer> new_list = new ArrayList<>(marriage.getWorkerCount());
            for(int j = 0; j < marriage.getJobCount(); j++){
                new_list.add(-1);
            }
            ArrayList<Integer> old_list = marriage.getJobPreference().get(i);
            for(int j = 0; j < old_list.size(); j++){
                new_list.set(old_list.get(j),j);
            }
            jobPreferenceByWorkerNum.add(new_list);
        }

        while(joblessWorker.isEmpty() == false) {//while there are jobless workers
            int currentWorker = joblessWorker.get(0);//get the first on the jobless list
            ArrayList<Integer> w_pref_list = marriage.getWorkerPreference().get(currentWorker);
            int desiredJob = w_pref_list.get(0);
            w_pref_list.remove(0);

            if (jobTaken.get(desiredJob) == false) {
                worker_matching.set(currentWorker, desiredJob);
                job_matching.set(desiredJob, currentWorker);
                jobTaken.set(desiredJob, true);
                joblessWorker.remove(0);
            } else {
                ArrayList<Integer> cur_job_pref_list_by_worker_num = jobPreferenceByWorkerNum.get(desiredJob);
                int job_cur_worker = job_matching.get(desiredJob);
                if (cur_job_pref_list_by_worker_num.get(currentWorker) < cur_job_pref_list_by_worker_num.get(job_cur_worker)) {
                    worker_matching.set(currentWorker, desiredJob);
                    job_matching.set(desiredJob, currentWorker);
                    joblessWorker.remove(0);
                    joblessWorker.add(job_cur_worker);
                } else {
                    continue;
                }
            }
        }
        Matching result = new Matching(marriage.getJobCount(),marriage.getWorkerCount(),marriage.getJobPreference(),preserved_worker_pref_list,marriage.getJobFulltime(),marriage.getWorkerHardworking());
        result.setWorkerMatching(worker_matching);
        return result;
    }
}
