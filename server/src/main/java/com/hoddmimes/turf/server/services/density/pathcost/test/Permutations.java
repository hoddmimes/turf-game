package com.hoddmimes.turf.server.services.density.pathcost.test;


import java.util.Arrays;
import java.util.List;

public class Permutations {


    public interface PermuteCallback {
        public void nextPermutation(List<Integer> pList );
    }

    List<Integer> mList;
    int mCount;


    public Permutations(List<Integer> pList) {
        mList = pList;
    }

    public static void main(String[] args) {
        List<Integer> tList = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8 );
        Permutations p = new Permutations(tList);
        p.permute( new PermutationConsumer());
    }

    public void permute(PermuteCallback pCallback) {
        long tStart = System.currentTimeMillis();
        mCount = 0;
        permute(mList, 0, mList.size() - 1, pCallback);
        long tStop = (System.currentTimeMillis() - tStart) / 1000L;
        System.out.println("Exec Time: " +tStop + " count: " + mCount  );
    }

    /**
     * print permutations of array
     *
     * @param pList       original int array,
     * @param pStartIndex start index
     * @param pEndIndex   end index
     */
    private void permute(List<Integer> pList, int pStartIndex, int pEndIndex, PermuteCallback pCallback) {
        int j;
        if (pStartIndex == pEndIndex) {
            mCount++;
            pCallback.nextPermutation(pList);
        } else {
            for (j = pStartIndex; j <= pEndIndex; j++) {
                swap(pList, pStartIndex, j);
                permute(pList, pStartIndex + 1, pEndIndex, pCallback);
                swap(pList, pStartIndex, j); // backtrack
            }
        }
    }

    void swap(List<Integer> tList, int x, int y) {
        int t = tList.get(x);
        tList.set(x, tList.get(y));
        tList.set(y, t);
    }

    static class PermutationConsumer implements PermuteCallback {
        @Override
        public void nextPermutation(List<Integer> pList) {
           // System.out.println(pList);
        }
    }

}