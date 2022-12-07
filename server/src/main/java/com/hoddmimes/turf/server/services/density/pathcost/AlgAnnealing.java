package com.hoddmimes.turf.server.services.density.pathcost;

import com.google.gson.JsonArray;
import com.hoddmimes.turf.server.common.TurfZone;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class AlgAnnealing implements PathCostInterface
{
    private Distances mDistances;
    private List<Node>      mNodeList;
    private List<Integer>   mBestNodePath;
    private int mNumberOfIterations;
    private double mCoolingRate;
    private double mStartingTemprature;
    private Random  mRandom;

    public AlgAnnealing(double pStartingTemprature, int pNumberOfIterations, double pCoolingRate) {
        mStartingTemprature = pStartingTemprature;
        mNumberOfIterations = pNumberOfIterations;
        mCoolingRate = pCoolingRate;
        mRandom = new Random();
    }

    @Override
    public void initialize(List<TurfZone> pZones) {
        mNodeList = new ArrayList<>();
        for (int i = 0; i < pZones.size(); i++) {
            TurfZone z = pZones.get(i);
            mNodeList.add(new Node( i, z.getLong(), z.getLatitude()));
        }
        mDistances = new Distances( mNodeList );
    }

    @Override
    public void initialize(JsonArray pJsonPoints ) {
        mNodeList = new ArrayList<>();
        for (int i = 0; i < pJsonPoints.size(); i++) {
            JsonArray jPoint = pJsonPoints.get(i).getAsJsonArray();
            mNodeList.add(new Node( i, jPoint.get(0).getAsDouble(), jPoint.get(1).getAsDouble()));
        }
        //String tOrderStr = mNodeList.stream().mapToInt( n-> n.nodeId).boxed().collect( Collectors.toList()).toString();
        //System.out.println("Init Node list: " +  tOrderStr );
        mDistances = new Distances( mNodeList );
    }

    @Override
    public double getDistance() {
        System.out.println("Starting temprature: " + mStartingTemprature + ", # of iterations: " + mNumberOfIterations + " and colling rate: " + mCoolingRate);
        double t = mStartingTemprature;

        Route tBestSolution = new Route( mNodeList );
        Route tCurrentSolution = new Route( mNodeList );

        double tBestDistance = tBestSolution.getDistance();
        //System.out.println("Initial distance of travel: " + tBestDistance);

        for (int i = 0; i < mNumberOfIterations; i++) {
            if (t > 0.1) {
                tCurrentSolution.swapCities();
                double tCurrentDistance = tCurrentSolution.getDistance();
                if (tCurrentDistance < tBestDistance) {
                    tBestDistance = tCurrentDistance;
                } else if (Math.exp((tBestDistance - tCurrentDistance) / t) < Math.random()) {
                    tCurrentSolution.revertSwap();
                }
                t *= mCoolingRate;
            } else {
                continue;
            }
            if (i % 100 == 0) {
                //System.out.println("Iteration #" + i);
            }
        }
        mBestNodePath = tCurrentSolution.getCurrentPath();
        return tBestDistance;
    }







    @Override
    public List<Integer> getPath() {
        return mBestNodePath;
    }



    class Route {

        private ArrayList<Node> mRoute;
        private ArrayList<Node> mPreviousRoute;


        public Route(List<Node> pNodeList ) {
            mRoute = new ArrayList<>(pNodeList);
            //System.out.println( "init list order" + this.getCurrentListOrder());
        }

        private int generateRandomIndex() {
            return (int) (Math.random() * mRoute.size());
        }

        private String getCurrentListOrder() {
            StringBuilder sb = new StringBuilder();
            for( Node n : mRoute ) {
                sb.append( String.valueOf(  n.nodeId) + " ");
            }
            return sb.toString();
        }

        public void swapCities() {
            int a = generateRandomIndex();
            int b = generateRandomIndex();
            mPreviousRoute = new ArrayList<>(mRoute);
            Node x = mRoute.get(a);
            Node y = mRoute.get(b);

            //System.out.println("route-before swap: " + this.getCurrentListOrder());
            mRoute.set(a, y);
            mRoute.set(b, x);
            //System.out.println("route-after swap: " + this.getCurrentListOrder());
        }


        public void revertSwap() {
            mRoute = new ArrayList(mPreviousRoute);
        }

        List<Integer> getCurrentPath() {
           return  mRoute.stream().mapToInt( n -> n.nodeId).boxed().collect(Collectors.toList());
        }

        public Node getNode(int index) {
            return mRoute.get(index);
        }



        private double getDistance() {
            int tDistance = 0;
            for (int i = 0; i < mRoute.size() - 1; i++) {
                double d = mDistances.getDistance( mRoute.get(i).nodeId, mRoute.get(i+1).nodeId);
                tDistance += d;
            }
            //System.out.println(" cost: " + tDistance);
            return tDistance;
        }


    }
}
