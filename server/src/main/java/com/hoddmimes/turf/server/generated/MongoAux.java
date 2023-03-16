
/*
* Copyright (c)  Hoddmimes Solution AB 2021.
*
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
            
    package com.hoddmimes.turf.server.generated;


    import com.mongodb.BasicDBObject;
    import com.mongodb.MongoClient;
    import com.mongodb.client.MongoCollection;
    import com.mongodb.client.MongoDatabase;
    import com.mongodb.client.MongoIterable;
    import com.mongodb.client.model.CreateCollectionOptions;
    import com.mongodb.client.model.ValidationOptions;
    import org.bson.conversions.Bson;
    import com.mongodb.client.model.UpdateOptions;
    import com.mongodb.client.FindIterable;
    import com.mongodb.client.MongoCursor;
    import com.mongodb.client.model.IndexOptions;


    import org.bson.BsonType;
    import org.bson.Document;
    import org.bson.types.ObjectId;

    import com.mongodb.client.model.Filters;
    import com.mongodb.client.result.DeleteResult;
    import com.mongodb.client.result.UpdateResult;

    import java.util.List;
    import java.util.Date;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.stream.Collectors;
    import com.hoddmimes.jsontransform.DateUtils;

    
    import com.hoddmimes.turf.server.generated.*;
    
    public class MongoAux {
            private String mDbName;
            private String mDbHost;
            private int mDbPort;

            private MongoClient mClient;
            private MongoDatabase mDb;
            
           private MongoCollection mHourRegionStatCollection;
           private MongoCollection mRegionCollection;
           private MongoCollection mDayRankingUserInitCollection;
           private MongoCollection mDayRankingUserCollection;
           private MongoCollection mDayRankingRegionCollection;
           private MongoCollection mUserCollection;
           private MongoCollection mSubscriptionCollection;
           private MongoCollection mFirstEntryCollection;
           private MongoCollection mZoneHeatMapCollection;

            public MongoAux( String pDbName, String pDbHost, int pDbPort ) {
               mDbName = pDbName;
               mDbHost = pDbHost;
               mDbPort = pDbPort;
            }

            public void connectToDatabase() {
               try {
                 mClient = new MongoClient( mDbHost, mDbPort);
                 mDb = mClient.getDatabase( mDbName );
            
                 mHourRegionStatCollection = mDb.getCollection("HourRegionStat");
              
                 mRegionCollection = mDb.getCollection("Region");
              
                 mDayRankingUserInitCollection = mDb.getCollection("DayRankingUserInit");
              
                 mDayRankingUserCollection = mDb.getCollection("DayRankingUser");
              
                 mDayRankingRegionCollection = mDb.getCollection("DayRankingRegion");
              
                 mUserCollection = mDb.getCollection("User");
              
                 mSubscriptionCollection = mDb.getCollection("Subscription");
              
                 mFirstEntryCollection = mDb.getCollection("FirstEntry");
              
                 mZoneHeatMapCollection = mDb.getCollection("ZoneHeatMap");
              
               }
               catch(Exception e ) {
                  e.printStackTrace();
               }
            }

            public void  dropDatabase()
            {
                MongoClient tClient = new MongoClient(mDbHost, mDbPort);
                MongoDatabase tDb = tClient.getDatabase( mDbName );


                try {
                    tDb.drop();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                finally {
                   if (mClient != null) {
                        mClient.close();
                   }
                   mDb = null;
            
                    mHourRegionStatCollection = null;
                
                    mRegionCollection = null;
                
                    mDayRankingUserInitCollection = null;
                
                    mDayRankingUserCollection = null;
                
                    mDayRankingRegionCollection = null;
                
                    mUserCollection = null;
                
                    mSubscriptionCollection = null;
                
                    mFirstEntryCollection = null;
                
                    mZoneHeatMapCollection = null;
                
                 }
            }

            private void close() {
                if (mClient != null) {
                    mClient.close();
                    mDb = null;

            
                    mHourRegionStatCollection = null;
                
                    mRegionCollection = null;
                
                    mDayRankingUserInitCollection = null;
                
                    mDayRankingUserCollection = null;
                
                    mDayRankingRegionCollection = null;
                
                    mUserCollection = null;
                
                    mSubscriptionCollection = null;
                
                    mFirstEntryCollection = null;
                
                    mZoneHeatMapCollection = null;
                
                }
            }




            private void createCollection(String pCollectionName, List<DbKey> pKeys, Bson pValidator )
            {
                MongoClient tClient = new MongoClient(mDbHost, mDbPort);
                MongoDatabase db = tClient.getDatabase( mDbName );

                //ValidationOptions validationOptions = new ValidationOptions().validator(pValidator);
                //CreateCollectionOptions tOptions = new CreateCollectionOptions().validationOptions(validationOptions);
                //db.createCollection(pCollectionName, tOptions );
                db.createCollection(pCollectionName);

                MongoCollection tCollection = db.getCollection( pCollectionName );
                if (pKeys != null) {
                  for ( DbKey dbk : pKeys ) {
                       tCollection.createIndex(new BasicDBObject(dbk.mKeyName, 1), new IndexOptions().unique(dbk.mUnique));
                  }
                }
                tClient.close();
            }


            private boolean collectionExit( String pCollectionName, MongoIterable<String> pCollectionNameNames ) {
              MongoCursor<String> tItr = pCollectionNameNames.iterator();
              while( tItr.hasNext()) {
                String tName = tItr.next();
                if (tName.compareTo( pCollectionName ) == 0) {
                  return true;
                }
              }
              return false;
            }


            public void createDatabase( boolean pReset ) {
                this.close();
                MongoClient mClient = new MongoClient(mDbHost, mDbPort);
                MongoDatabase tDB = mClient.getDatabase( mDbName );
                MongoIterable<String> tCollectionNames = tDB.listCollectionNames();

                
                       if ((pReset) || (!collectionExit("HourRegionStat", tCollectionNames ))) {
                          createHourRegionStatCollection();
                       }
                   
                       if ((pReset) || (!collectionExit("Region", tCollectionNames ))) {
                          createRegionCollection();
                       }
                   
                       if ((pReset) || (!collectionExit("DayRankingUserInit", tCollectionNames ))) {
                          createDayRankingUserInitCollection();
                       }
                   
                       if ((pReset) || (!collectionExit("DayRankingUser", tCollectionNames ))) {
                          createDayRankingUserCollection();
                       }
                   
                       if ((pReset) || (!collectionExit("DayRankingRegion", tCollectionNames ))) {
                          createDayRankingRegionCollection();
                       }
                   
                       if ((pReset) || (!collectionExit("User", tCollectionNames ))) {
                          createUserCollection();
                       }
                   
                       if ((pReset) || (!collectionExit("Subscription", tCollectionNames ))) {
                          createSubscriptionCollection();
                       }
                   
                       if ((pReset) || (!collectionExit("FirstEntry", tCollectionNames ))) {
                          createFirstEntryCollection();
                       }
                   
                       if ((pReset) || (!collectionExit("ZoneHeatMap", tCollectionNames ))) {
                          createZoneHeatMapCollection();
                       }
                   
            }


            
        private void createHourRegionStatCollection() {
            ArrayList<DbKey> tKeys = new ArrayList<>();
            
                tKeys.add( new DbKey("id", false));
                tKeys.add( new DbKey("createTime", false));

            createCollection("HourRegionStat", tKeys, null );
        }
    
        private void createRegionCollection() {
            ArrayList<DbKey> tKeys = new ArrayList<>();
            
                    tKeys.add( new DbKey("id", true));

            createCollection("Region", tKeys, null );
        }
    
        private void createDayRankingUserInitCollection() {
            ArrayList<DbKey> tKeys = new ArrayList<>();
            
                tKeys.add( new DbKey("date", false));
                tKeys.add( new DbKey("userId", false));

            createCollection("DayRankingUserInit", tKeys, null );
        }
    
        private void createDayRankingUserCollection() {
            ArrayList<DbKey> tKeys = new ArrayList<>();
            
                tKeys.add( new DbKey("date", false));
                tKeys.add( new DbKey("userId", false));
                tKeys.add( new DbKey("regionId", false));

            createCollection("DayRankingUser", tKeys, null );
        }
    
        private void createDayRankingRegionCollection() {
            ArrayList<DbKey> tKeys = new ArrayList<>();
            
                tKeys.add( new DbKey("date", false));
                tKeys.add( new DbKey("regionId", false));

            createCollection("DayRankingRegion", tKeys, null );
        }
    
        private void createUserCollection() {
            ArrayList<DbKey> tKeys = new ArrayList<>();
            
                    tKeys.add( new DbKey("mailAddr", true));

            createCollection("User", tKeys, null );
        }
    
        private void createSubscriptionCollection() {
            ArrayList<DbKey> tKeys = new ArrayList<>();
            
                tKeys.add( new DbKey("mailAddr", false));
                tKeys.add( new DbKey("zoneName", false));
                tKeys.add( new DbKey("zoneId", false));

            createCollection("Subscription", tKeys, null );
        }
    
        private void createFirstEntryCollection() {
            ArrayList<DbKey> tKeys = new ArrayList<>();
            
                    tKeys.add( new DbKey("timestamp", true));

            createCollection("FirstEntry", tKeys, null );
        }
    
        private void createZoneHeatMapCollection() {
            ArrayList<DbKey> tKeys = new ArrayList<>();
            
                tKeys.add( new DbKey("regionId", false));
                    tKeys.add( new DbKey("zoneId", true));

            createCollection("ZoneHeatMap", tKeys, null );
        }
    
                    public MongoCollection getHourRegionStatCollection() {
                      return mHourRegionStatCollection;
                    }
                
                    public MongoCollection getRegionCollection() {
                      return mRegionCollection;
                    }
                
                    public MongoCollection getDayRankingUserInitCollection() {
                      return mDayRankingUserInitCollection;
                    }
                
                    public MongoCollection getDayRankingUserCollection() {
                      return mDayRankingUserCollection;
                    }
                
                    public MongoCollection getDayRankingRegionCollection() {
                      return mDayRankingRegionCollection;
                    }
                
                    public MongoCollection getUserCollection() {
                      return mUserCollection;
                    }
                
                    public MongoCollection getSubscriptionCollection() {
                      return mSubscriptionCollection;
                    }
                
                    public MongoCollection getFirstEntryCollection() {
                      return mFirstEntryCollection;
                    }
                
                    public MongoCollection getZoneHeatMapCollection() {
                      return mZoneHeatMapCollection;
                    }
                
        /**
        * CRUD DELETE methods
        */
        public long deleteHourRegionStat( Bson pFilter) {
           DeleteResult tResult = mHourRegionStatCollection.deleteMany( pFilter );
           return tResult.getDeletedCount();
        }

        public long deleteHourRegionStatByMongoId( String pMongoObjectId) {
            Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));
            DeleteResult tResult = mHourRegionStatCollection.deleteOne( tFilter );
            return tResult.getDeletedCount();
        }

        
            public long deleteHourRegionStat( int pId, long pCreateTime ) {
             
        Bson tKeyFilter= Filters.and( 
            Filters.eq("id", pId) ,
            Filters.eq("createTime", pCreateTime) );
    
             DeleteResult tResult =  mHourRegionStatCollection.deleteMany(tKeyFilter);
             return tResult.getDeletedCount();
            }
        
            public long deleteHourRegionStatById( int pId ) {
                
            Bson tKeyFilter= Filters.eq("id", pId); 
                DeleteResult tResult =  mHourRegionStatCollection.deleteMany(tKeyFilter);
                return tResult.getDeletedCount();
            }
        
            public long deleteHourRegionStatByCreateTime( long pCreateTime ) {
                
            Bson tKeyFilter= Filters.eq("createTime", pCreateTime); 
                DeleteResult tResult =  mHourRegionStatCollection.deleteMany(tKeyFilter);
                return tResult.getDeletedCount();
            }
        
        /**
        * CRUD INSERT methods
        */
        public void insertHourRegionStat( HourRegionStat pHourRegionStat ) {
            Document tDoc = pHourRegionStat.getMongoDocument();
            mHourRegionStatCollection.insertOne( tDoc );
            ObjectId _tId = tDoc.getObjectId("_id");
            if (_tId != null) {
                pHourRegionStat.setMongoId( _tId.toString());
            }
        }

        public void insertHourRegionStat( List<HourRegionStat> pHourRegionStatList ) {
           List<Document> tList = pHourRegionStatList.stream().map( HourRegionStat::getMongoDocument).collect(Collectors.toList());
           mHourRegionStatCollection.insertMany( tList );
           for( int i = 0; i < tList.size(); i++ ) {
             ObjectId _tId = tList.get(i).getObjectId("_id");
             if (_tId != null) {
                pHourRegionStatList.get(i).setMongoId( _tId.toString());
             }
           }
        }

    
        /**
        * CRUD UPDATE (INSERT) methods
        */
        public UpdateResult updateHourRegionStatByMongoId( String pMongoObjectId, HourRegionStat pHourRegionStat ) {
            Bson tFilter =  Filters.eq("_id", new ObjectId(pMongoObjectId));
            Document tDocSet = new Document("$set", pHourRegionStat.getMongoDocument());
            UpdateResult tUpdSts = mHourRegionStatCollection.updateOne( tFilter, tDocSet, new UpdateOptions());
            return tUpdSts;
        }

        public UpdateResult updateHourRegionStat( HourRegionStat pHourRegionStat, boolean pUpdateAllowInsert ) {
        UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
        
        Bson tKeyFilter= Filters.and( 
            Filters.eq("id", pHourRegionStat.getId().get()) ,
            Filters.eq("createTime", pHourRegionStat.getCreateTime().get()) );
    


        Document tDocSet = new Document("$set", pHourRegionStat.getMongoDocument());

        UpdateResult tUpdSts = mHourRegionStatCollection.updateOne( tKeyFilter, tDocSet, tOptions);
        return tUpdSts;
        }


        public UpdateResult updateHourRegionStat( int pId, long pCreateTime, HourRegionStat pHourRegionStat, boolean pUpdateAllowInsert ) {
          UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
          
        Bson tKeyFilter= Filters.and( 
            Filters.eq("id", pId) ,
            Filters.eq("createTime", pCreateTime) );
    

           Document tDocSet = new Document("$set", pHourRegionStat.getMongoDocument());

           UpdateResult tUpdSts = mHourRegionStatCollection.updateOne( tKeyFilter, tDocSet, tOptions);
           return tUpdSts;
        }

        public UpdateResult updateHourRegionStat( Bson pFilter, HourRegionStat pHourRegionStat, boolean pUpdateAllowInsert ) {
           UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
           Document tDocSet = new Document("$set", pHourRegionStat.getMongoDocument());
           UpdateResult tUpdSts = mHourRegionStatCollection.updateOne( pFilter, tDocSet, tOptions);
           return tUpdSts;
        }
    
        /**
        * CRUD FIND methods
        */

        public List<HourRegionStat> findHourRegionStat( Bson pFilter  ) {
         return findHourRegionStat( pFilter, null );
        }

        public List<HourRegionStat> findHourRegionStat( Bson pFilter, Bson pSortDoc  ) {

        FindIterable<Document> tDocuments = (pSortDoc == null) ? this.mHourRegionStatCollection.find( pFilter ) :
        this.mHourRegionStatCollection.find( pFilter ).sort( pSortDoc );


        if (tDocuments == null) {
        return null;
        }

        List<HourRegionStat> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        HourRegionStat tHourRegionStat = new HourRegionStat();
        tHourRegionStat.decodeMongoDocument( tDoc );
        tResult.add( tHourRegionStat );
        }
        return tResult;
        }



        public List<HourRegionStat> findAllHourRegionStat()
        {
           List<HourRegionStat> tResult = new ArrayList<>();

           FindIterable<Document> tDocuments  = this.mHourRegionStatCollection.find();
           MongoCursor<Document> tIter = tDocuments.iterator();
           while( tIter.hasNext()) {
               Document tDoc = tIter.next();
               HourRegionStat tHourRegionStat = new HourRegionStat();
               tHourRegionStat.decodeMongoDocument( tDoc );
               tResult.add(tHourRegionStat);
            }
            return tResult;
        }

        public HourRegionStat findHourRegionStatByMongoId( String pMongoObjectId ) {
        Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));

        FindIterable<Document> tDocuments = this.mHourRegionStatCollection.find( tFilter );
        if (tDocuments == null) {
            return null;
        }

        List<HourRegionStat> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        HourRegionStat tHourRegionStat = new HourRegionStat();
        tHourRegionStat.decodeMongoDocument( tDoc );
        tResult.add( tHourRegionStat );
        }
        return (tResult.size() > 0) ? tResult.get(0) : null;
        }


        public List<HourRegionStat> findHourRegionStat( int pId, long pCreateTime ) {
            
        Bson tKeyFilter= Filters.and( 
            Filters.eq("id", pId) ,
            Filters.eq("createTime", pCreateTime) );
    

        FindIterable<Document> tDocuments = this.mHourRegionStatCollection.find( tKeyFilter );
        if (tDocuments == null) {
           return null;
        }

        List<HourRegionStat> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
           Document tDoc = tIter.next();
           HourRegionStat tHourRegionStat = new HourRegionStat();
           tHourRegionStat.decodeMongoDocument( tDoc );
           tResult.add( tHourRegionStat );
        }
        return tResult;
        }

        
            public List<HourRegionStat> findHourRegionStatById( int pId ) {
            List<HourRegionStat> tResult = new ArrayList<>();
            
            Bson tKeyFilter= Filters.eq("id", pId); 

            FindIterable<Document> tDocuments  = this.mHourRegionStatCollection.find( tKeyFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            HourRegionStat tHourRegionStat = new HourRegionStat();
            tHourRegionStat.decodeMongoDocument( tDoc );
            tResult.add(tHourRegionStat);
            }
            return tResult;
            }
        
            public List<HourRegionStat> findHourRegionStatByCreateTime( long pCreateTime ) {
            List<HourRegionStat> tResult = new ArrayList<>();
            
            Bson tKeyFilter= Filters.eq("createTime", pCreateTime); 

            FindIterable<Document> tDocuments  = this.mHourRegionStatCollection.find( tKeyFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            HourRegionStat tHourRegionStat = new HourRegionStat();
            tHourRegionStat.decodeMongoDocument( tDoc );
            tResult.add(tHourRegionStat);
            }
            return tResult;
            }
        
        /**
        * CRUD DELETE methods
        */
        public long deleteRegionStat( Bson pFilter) {
           DeleteResult tResult = mRegionCollection.deleteMany( pFilter );
           return tResult.getDeletedCount();
        }

        public long deleteRegionStatByMongoId( String pMongoObjectId) {
            Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));
            DeleteResult tResult = mRegionCollection.deleteOne( tFilter );
            return tResult.getDeletedCount();
        }

        
            public long deleteRegionStatById( int pId ) {
                
            Bson tKeyFilter= Filters.eq("id", pId); 
                DeleteResult tResult =  mRegionCollection.deleteMany(tKeyFilter);
                return tResult.getDeletedCount();
            }
        
        /**
        * CRUD INSERT methods
        */
        public void insertRegionStat( RegionStat pRegionStat ) {
            Document tDoc = pRegionStat.getMongoDocument();
            mRegionCollection.insertOne( tDoc );
            ObjectId _tId = tDoc.getObjectId("_id");
            if (_tId != null) {
                pRegionStat.setMongoId( _tId.toString());
            }
        }

        public void insertRegionStat( List<RegionStat> pRegionStatList ) {
           List<Document> tList = pRegionStatList.stream().map( RegionStat::getMongoDocument).collect(Collectors.toList());
           mRegionCollection.insertMany( tList );
           for( int i = 0; i < tList.size(); i++ ) {
             ObjectId _tId = tList.get(i).getObjectId("_id");
             if (_tId != null) {
                pRegionStatList.get(i).setMongoId( _tId.toString());
             }
           }
        }

    
        /**
        * CRUD UPDATE (INSERT) methods
        */
        public UpdateResult updateRegionStatByMongoId( String pMongoObjectId, RegionStat pRegionStat ) {
            Bson tFilter =  Filters.eq("_id", new ObjectId(pMongoObjectId));
            Document tDocSet = new Document("$set", pRegionStat.getMongoDocument());
            UpdateResult tUpdSts = mRegionCollection.updateOne( tFilter, tDocSet, new UpdateOptions());
            return tUpdSts;
        }

        public UpdateResult updateRegionStat( RegionStat pRegionStat, boolean pUpdateAllowInsert ) {
        UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
        
        Bson tKeyFilter= Filters.and( 
            Filters.eq("id", pRegionStat.getId().get()) );
    


        Document tDocSet = new Document("$set", pRegionStat.getMongoDocument());

        UpdateResult tUpdSts = mRegionCollection.updateOne( tKeyFilter, tDocSet, tOptions);
        return tUpdSts;
        }


        public UpdateResult updateRegionStat( int pId, RegionStat pRegionStat, boolean pUpdateAllowInsert ) {
          UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
          
        Bson tKeyFilter= Filters.and( 
            Filters.eq("id", pId) );
    

           Document tDocSet = new Document("$set", pRegionStat.getMongoDocument());

           UpdateResult tUpdSts = mRegionCollection.updateOne( tKeyFilter, tDocSet, tOptions);
           return tUpdSts;
        }

        public UpdateResult updateRegionStat( Bson pFilter, RegionStat pRegionStat, boolean pUpdateAllowInsert ) {
           UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
           Document tDocSet = new Document("$set", pRegionStat.getMongoDocument());
           UpdateResult tUpdSts = mRegionCollection.updateOne( pFilter, tDocSet, tOptions);
           return tUpdSts;
        }
    
        /**
        * CRUD FIND methods
        */

        public List<RegionStat> findRegionStat( Bson pFilter  ) {
         return findRegionStat( pFilter, null );
        }

        public List<RegionStat> findRegionStat( Bson pFilter, Bson pSortDoc  ) {

        FindIterable<Document> tDocuments = (pSortDoc == null) ? this.mRegionCollection.find( pFilter ) :
        this.mRegionCollection.find( pFilter ).sort( pSortDoc );


        if (tDocuments == null) {
        return null;
        }

        List<RegionStat> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        RegionStat tRegionStat = new RegionStat();
        tRegionStat.decodeMongoDocument( tDoc );
        tResult.add( tRegionStat );
        }
        return tResult;
        }



        public List<RegionStat> findAllRegionStat()
        {
           List<RegionStat> tResult = new ArrayList<>();

           FindIterable<Document> tDocuments  = this.mRegionCollection.find();
           MongoCursor<Document> tIter = tDocuments.iterator();
           while( tIter.hasNext()) {
               Document tDoc = tIter.next();
               RegionStat tRegionStat = new RegionStat();
               tRegionStat.decodeMongoDocument( tDoc );
               tResult.add(tRegionStat);
            }
            return tResult;
        }

        public RegionStat findRegionStatByMongoId( String pMongoObjectId ) {
        Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));

        FindIterable<Document> tDocuments = this.mRegionCollection.find( tFilter );
        if (tDocuments == null) {
            return null;
        }

        List<RegionStat> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        RegionStat tRegionStat = new RegionStat();
        tRegionStat.decodeMongoDocument( tDoc );
        tResult.add( tRegionStat );
        }
        return (tResult.size() > 0) ? tResult.get(0) : null;
        }


        public List<RegionStat> findRegionStat( int pId ) {
            
        Bson tKeyFilter= Filters.and( 
            Filters.eq("id", pId) );
    

        FindIterable<Document> tDocuments = this.mRegionCollection.find( tKeyFilter );
        if (tDocuments == null) {
           return null;
        }

        List<RegionStat> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
           Document tDoc = tIter.next();
           RegionStat tRegionStat = new RegionStat();
           tRegionStat.decodeMongoDocument( tDoc );
           tResult.add( tRegionStat );
        }
        return tResult;
        }

        
            public List<RegionStat> findRegionStatById( int pId ) {
            List<RegionStat> tResult = new ArrayList<>();
            
            Bson tKeyFilter= Filters.eq("id", pId); 

            FindIterable<Document> tDocuments  = this.mRegionCollection.find( tKeyFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            RegionStat tRegionStat = new RegionStat();
            tRegionStat.decodeMongoDocument( tDoc );
            tResult.add(tRegionStat);
            }
            return tResult;
            }
        
        /**
        * CRUD DELETE methods
        */
        public long deleteDayRankingInitUser( Bson pFilter) {
           DeleteResult tResult = mDayRankingUserInitCollection.deleteMany( pFilter );
           return tResult.getDeletedCount();
        }

        public long deleteDayRankingInitUserByMongoId( String pMongoObjectId) {
            Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));
            DeleteResult tResult = mDayRankingUserInitCollection.deleteOne( tFilter );
            return tResult.getDeletedCount();
        }

        
            public long deleteDayRankingInitUser( String pDate, int pUserId ) {
             
        Bson tKeyFilter= Filters.and( 
            Filters.eq("date", pDate) ,
            Filters.eq("userId", pUserId) );
    
             DeleteResult tResult =  mDayRankingUserInitCollection.deleteMany(tKeyFilter);
             return tResult.getDeletedCount();
            }
        
            public long deleteDayRankingInitUserByDate( String pDate ) {
                
            Bson tKeyFilter= Filters.eq("date", pDate); 
                DeleteResult tResult =  mDayRankingUserInitCollection.deleteMany(tKeyFilter);
                return tResult.getDeletedCount();
            }
        
            public long deleteDayRankingInitUserByUserId( int pUserId ) {
                
            Bson tKeyFilter= Filters.eq("userId", pUserId); 
                DeleteResult tResult =  mDayRankingUserInitCollection.deleteMany(tKeyFilter);
                return tResult.getDeletedCount();
            }
        
        /**
        * CRUD INSERT methods
        */
        public void insertDayRankingInitUser( DayRankingInitUser pDayRankingInitUser ) {
            Document tDoc = pDayRankingInitUser.getMongoDocument();
            mDayRankingUserInitCollection.insertOne( tDoc );
            ObjectId _tId = tDoc.getObjectId("_id");
            if (_tId != null) {
                pDayRankingInitUser.setMongoId( _tId.toString());
            }
        }

        public void insertDayRankingInitUser( List<DayRankingInitUser> pDayRankingInitUserList ) {
           List<Document> tList = pDayRankingInitUserList.stream().map( DayRankingInitUser::getMongoDocument).collect(Collectors.toList());
           mDayRankingUserInitCollection.insertMany( tList );
           for( int i = 0; i < tList.size(); i++ ) {
             ObjectId _tId = tList.get(i).getObjectId("_id");
             if (_tId != null) {
                pDayRankingInitUserList.get(i).setMongoId( _tId.toString());
             }
           }
        }

    
        /**
        * CRUD UPDATE (INSERT) methods
        */
        public UpdateResult updateDayRankingInitUserByMongoId( String pMongoObjectId, DayRankingInitUser pDayRankingInitUser ) {
            Bson tFilter =  Filters.eq("_id", new ObjectId(pMongoObjectId));
            Document tDocSet = new Document("$set", pDayRankingInitUser.getMongoDocument());
            UpdateResult tUpdSts = mDayRankingUserInitCollection.updateOne( tFilter, tDocSet, new UpdateOptions());
            return tUpdSts;
        }

        public UpdateResult updateDayRankingInitUser( DayRankingInitUser pDayRankingInitUser, boolean pUpdateAllowInsert ) {
        UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
        
        Bson tKeyFilter= Filters.and( 
            Filters.eq("date", pDayRankingInitUser.getDate().get()) ,
            Filters.eq("userId", pDayRankingInitUser.getUserId().get()) );
    


        Document tDocSet = new Document("$set", pDayRankingInitUser.getMongoDocument());

        UpdateResult tUpdSts = mDayRankingUserInitCollection.updateOne( tKeyFilter, tDocSet, tOptions);
        return tUpdSts;
        }


        public UpdateResult updateDayRankingInitUser( String pDate, int pUserId, DayRankingInitUser pDayRankingInitUser, boolean pUpdateAllowInsert ) {
          UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
          
        Bson tKeyFilter= Filters.and( 
            Filters.eq("date", pDate) ,
            Filters.eq("userId", pUserId) );
    

           Document tDocSet = new Document("$set", pDayRankingInitUser.getMongoDocument());

           UpdateResult tUpdSts = mDayRankingUserInitCollection.updateOne( tKeyFilter, tDocSet, tOptions);
           return tUpdSts;
        }

        public UpdateResult updateDayRankingInitUser( Bson pFilter, DayRankingInitUser pDayRankingInitUser, boolean pUpdateAllowInsert ) {
           UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
           Document tDocSet = new Document("$set", pDayRankingInitUser.getMongoDocument());
           UpdateResult tUpdSts = mDayRankingUserInitCollection.updateOne( pFilter, tDocSet, tOptions);
           return tUpdSts;
        }
    
        /**
        * CRUD FIND methods
        */

        public List<DayRankingInitUser> findDayRankingInitUser( Bson pFilter  ) {
         return findDayRankingInitUser( pFilter, null );
        }

        public List<DayRankingInitUser> findDayRankingInitUser( Bson pFilter, Bson pSortDoc  ) {

        FindIterable<Document> tDocuments = (pSortDoc == null) ? this.mDayRankingUserInitCollection.find( pFilter ) :
        this.mDayRankingUserInitCollection.find( pFilter ).sort( pSortDoc );


        if (tDocuments == null) {
        return null;
        }

        List<DayRankingInitUser> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        DayRankingInitUser tDayRankingInitUser = new DayRankingInitUser();
        tDayRankingInitUser.decodeMongoDocument( tDoc );
        tResult.add( tDayRankingInitUser );
        }
        return tResult;
        }



        public List<DayRankingInitUser> findAllDayRankingInitUser()
        {
           List<DayRankingInitUser> tResult = new ArrayList<>();

           FindIterable<Document> tDocuments  = this.mDayRankingUserInitCollection.find();
           MongoCursor<Document> tIter = tDocuments.iterator();
           while( tIter.hasNext()) {
               Document tDoc = tIter.next();
               DayRankingInitUser tDayRankingInitUser = new DayRankingInitUser();
               tDayRankingInitUser.decodeMongoDocument( tDoc );
               tResult.add(tDayRankingInitUser);
            }
            return tResult;
        }

        public DayRankingInitUser findDayRankingInitUserByMongoId( String pMongoObjectId ) {
        Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));

        FindIterable<Document> tDocuments = this.mDayRankingUserInitCollection.find( tFilter );
        if (tDocuments == null) {
            return null;
        }

        List<DayRankingInitUser> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        DayRankingInitUser tDayRankingInitUser = new DayRankingInitUser();
        tDayRankingInitUser.decodeMongoDocument( tDoc );
        tResult.add( tDayRankingInitUser );
        }
        return (tResult.size() > 0) ? tResult.get(0) : null;
        }


        public List<DayRankingInitUser> findDayRankingInitUser( String pDate, int pUserId ) {
            
        Bson tKeyFilter= Filters.and( 
            Filters.eq("date", pDate) ,
            Filters.eq("userId", pUserId) );
    

        FindIterable<Document> tDocuments = this.mDayRankingUserInitCollection.find( tKeyFilter );
        if (tDocuments == null) {
           return null;
        }

        List<DayRankingInitUser> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
           Document tDoc = tIter.next();
           DayRankingInitUser tDayRankingInitUser = new DayRankingInitUser();
           tDayRankingInitUser.decodeMongoDocument( tDoc );
           tResult.add( tDayRankingInitUser );
        }
        return tResult;
        }

        
            public List<DayRankingInitUser> findDayRankingInitUserByDate( String pDate ) {
            List<DayRankingInitUser> tResult = new ArrayList<>();
            
            Bson tKeyFilter= Filters.eq("date", pDate); 

            FindIterable<Document> tDocuments  = this.mDayRankingUserInitCollection.find( tKeyFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            DayRankingInitUser tDayRankingInitUser = new DayRankingInitUser();
            tDayRankingInitUser.decodeMongoDocument( tDoc );
            tResult.add(tDayRankingInitUser);
            }
            return tResult;
            }
        
            public List<DayRankingInitUser> findDayRankingInitUserByUserId( int pUserId ) {
            List<DayRankingInitUser> tResult = new ArrayList<>();
            
            Bson tKeyFilter= Filters.eq("userId", pUserId); 

            FindIterable<Document> tDocuments  = this.mDayRankingUserInitCollection.find( tKeyFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            DayRankingInitUser tDayRankingInitUser = new DayRankingInitUser();
            tDayRankingInitUser.decodeMongoDocument( tDoc );
            tResult.add(tDayRankingInitUser);
            }
            return tResult;
            }
        
        /**
        * CRUD DELETE methods
        */
        public long deleteDayRankingUser( Bson pFilter) {
           DeleteResult tResult = mDayRankingUserCollection.deleteMany( pFilter );
           return tResult.getDeletedCount();
        }

        public long deleteDayRankingUserByMongoId( String pMongoObjectId) {
            Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));
            DeleteResult tResult = mDayRankingUserCollection.deleteOne( tFilter );
            return tResult.getDeletedCount();
        }

        
            public long deleteDayRankingUser( String pDate, int pUserId, int pRegionId ) {
             
        Bson tKeyFilter= Filters.and( 
            Filters.eq("date", pDate) ,
            Filters.eq("userId", pUserId) ,
            Filters.eq("regionId", pRegionId) );
    
             DeleteResult tResult =  mDayRankingUserCollection.deleteMany(tKeyFilter);
             return tResult.getDeletedCount();
            }
        
            public long deleteDayRankingUserByDate( String pDate ) {
                
            Bson tKeyFilter= Filters.eq("date", pDate); 
                DeleteResult tResult =  mDayRankingUserCollection.deleteMany(tKeyFilter);
                return tResult.getDeletedCount();
            }
        
            public long deleteDayRankingUserByUserId( int pUserId ) {
                
            Bson tKeyFilter= Filters.eq("userId", pUserId); 
                DeleteResult tResult =  mDayRankingUserCollection.deleteMany(tKeyFilter);
                return tResult.getDeletedCount();
            }
        
            public long deleteDayRankingUserByRegionId( int pRegionId ) {
                
            Bson tKeyFilter= Filters.eq("regionId", pRegionId); 
                DeleteResult tResult =  mDayRankingUserCollection.deleteMany(tKeyFilter);
                return tResult.getDeletedCount();
            }
        
        /**
        * CRUD INSERT methods
        */
        public void insertDayRankingUser( DayRankingUser pDayRankingUser ) {
            Document tDoc = pDayRankingUser.getMongoDocument();
            mDayRankingUserCollection.insertOne( tDoc );
            ObjectId _tId = tDoc.getObjectId("_id");
            if (_tId != null) {
                pDayRankingUser.setMongoId( _tId.toString());
            }
        }

        public void insertDayRankingUser( List<DayRankingUser> pDayRankingUserList ) {
           List<Document> tList = pDayRankingUserList.stream().map( DayRankingUser::getMongoDocument).collect(Collectors.toList());
           mDayRankingUserCollection.insertMany( tList );
           for( int i = 0; i < tList.size(); i++ ) {
             ObjectId _tId = tList.get(i).getObjectId("_id");
             if (_tId != null) {
                pDayRankingUserList.get(i).setMongoId( _tId.toString());
             }
           }
        }

    
        /**
        * CRUD UPDATE (INSERT) methods
        */
        public UpdateResult updateDayRankingUserByMongoId( String pMongoObjectId, DayRankingUser pDayRankingUser ) {
            Bson tFilter =  Filters.eq("_id", new ObjectId(pMongoObjectId));
            Document tDocSet = new Document("$set", pDayRankingUser.getMongoDocument());
            UpdateResult tUpdSts = mDayRankingUserCollection.updateOne( tFilter, tDocSet, new UpdateOptions());
            return tUpdSts;
        }

        public UpdateResult updateDayRankingUser( DayRankingUser pDayRankingUser, boolean pUpdateAllowInsert ) {
        UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
        
        Bson tKeyFilter= Filters.and( 
            Filters.eq("date", pDayRankingUser.getDate().get()) ,
            Filters.eq("userId", pDayRankingUser.getUserId().get()) ,
            Filters.eq("regionId", pDayRankingUser.getRegionId().get()) );
    


        Document tDocSet = new Document("$set", pDayRankingUser.getMongoDocument());

        UpdateResult tUpdSts = mDayRankingUserCollection.updateOne( tKeyFilter, tDocSet, tOptions);
        return tUpdSts;
        }


        public UpdateResult updateDayRankingUser( String pDate, int pUserId, int pRegionId, DayRankingUser pDayRankingUser, boolean pUpdateAllowInsert ) {
          UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
          
        Bson tKeyFilter= Filters.and( 
            Filters.eq("date", pDate) ,
            Filters.eq("userId", pUserId) ,
            Filters.eq("regionId", pRegionId) );
    

           Document tDocSet = new Document("$set", pDayRankingUser.getMongoDocument());

           UpdateResult tUpdSts = mDayRankingUserCollection.updateOne( tKeyFilter, tDocSet, tOptions);
           return tUpdSts;
        }

        public UpdateResult updateDayRankingUser( Bson pFilter, DayRankingUser pDayRankingUser, boolean pUpdateAllowInsert ) {
           UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
           Document tDocSet = new Document("$set", pDayRankingUser.getMongoDocument());
           UpdateResult tUpdSts = mDayRankingUserCollection.updateOne( pFilter, tDocSet, tOptions);
           return tUpdSts;
        }
    
        /**
        * CRUD FIND methods
        */

        public List<DayRankingUser> findDayRankingUser( Bson pFilter  ) {
         return findDayRankingUser( pFilter, null );
        }

        public List<DayRankingUser> findDayRankingUser( Bson pFilter, Bson pSortDoc  ) {

        FindIterable<Document> tDocuments = (pSortDoc == null) ? this.mDayRankingUserCollection.find( pFilter ) :
        this.mDayRankingUserCollection.find( pFilter ).sort( pSortDoc );


        if (tDocuments == null) {
        return null;
        }

        List<DayRankingUser> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        DayRankingUser tDayRankingUser = new DayRankingUser();
        tDayRankingUser.decodeMongoDocument( tDoc );
        tResult.add( tDayRankingUser );
        }
        return tResult;
        }



        public List<DayRankingUser> findAllDayRankingUser()
        {
           List<DayRankingUser> tResult = new ArrayList<>();

           FindIterable<Document> tDocuments  = this.mDayRankingUserCollection.find();
           MongoCursor<Document> tIter = tDocuments.iterator();
           while( tIter.hasNext()) {
               Document tDoc = tIter.next();
               DayRankingUser tDayRankingUser = new DayRankingUser();
               tDayRankingUser.decodeMongoDocument( tDoc );
               tResult.add(tDayRankingUser);
            }
            return tResult;
        }

        public DayRankingUser findDayRankingUserByMongoId( String pMongoObjectId ) {
        Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));

        FindIterable<Document> tDocuments = this.mDayRankingUserCollection.find( tFilter );
        if (tDocuments == null) {
            return null;
        }

        List<DayRankingUser> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        DayRankingUser tDayRankingUser = new DayRankingUser();
        tDayRankingUser.decodeMongoDocument( tDoc );
        tResult.add( tDayRankingUser );
        }
        return (tResult.size() > 0) ? tResult.get(0) : null;
        }


        public List<DayRankingUser> findDayRankingUser( String pDate, int pUserId, int pRegionId ) {
            
        Bson tKeyFilter= Filters.and( 
            Filters.eq("date", pDate) ,
            Filters.eq("userId", pUserId) ,
            Filters.eq("regionId", pRegionId) );
    

        FindIterable<Document> tDocuments = this.mDayRankingUserCollection.find( tKeyFilter );
        if (tDocuments == null) {
           return null;
        }

        List<DayRankingUser> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
           Document tDoc = tIter.next();
           DayRankingUser tDayRankingUser = new DayRankingUser();
           tDayRankingUser.decodeMongoDocument( tDoc );
           tResult.add( tDayRankingUser );
        }
        return tResult;
        }

        
            public List<DayRankingUser> findDayRankingUserByDate( String pDate ) {
            List<DayRankingUser> tResult = new ArrayList<>();
            
            Bson tKeyFilter= Filters.eq("date", pDate); 

            FindIterable<Document> tDocuments  = this.mDayRankingUserCollection.find( tKeyFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            DayRankingUser tDayRankingUser = new DayRankingUser();
            tDayRankingUser.decodeMongoDocument( tDoc );
            tResult.add(tDayRankingUser);
            }
            return tResult;
            }
        
            public List<DayRankingUser> findDayRankingUserByUserId( int pUserId ) {
            List<DayRankingUser> tResult = new ArrayList<>();
            
            Bson tKeyFilter= Filters.eq("userId", pUserId); 

            FindIterable<Document> tDocuments  = this.mDayRankingUserCollection.find( tKeyFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            DayRankingUser tDayRankingUser = new DayRankingUser();
            tDayRankingUser.decodeMongoDocument( tDoc );
            tResult.add(tDayRankingUser);
            }
            return tResult;
            }
        
            public List<DayRankingUser> findDayRankingUserByRegionId( int pRegionId ) {
            List<DayRankingUser> tResult = new ArrayList<>();
            
            Bson tKeyFilter= Filters.eq("regionId", pRegionId); 

            FindIterable<Document> tDocuments  = this.mDayRankingUserCollection.find( tKeyFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            DayRankingUser tDayRankingUser = new DayRankingUser();
            tDayRankingUser.decodeMongoDocument( tDoc );
            tResult.add(tDayRankingUser);
            }
            return tResult;
            }
        
        /**
        * CRUD DELETE methods
        */
        public long deleteDayRankingRegion( Bson pFilter) {
           DeleteResult tResult = mDayRankingRegionCollection.deleteMany( pFilter );
           return tResult.getDeletedCount();
        }

        public long deleteDayRankingRegionByMongoId( String pMongoObjectId) {
            Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));
            DeleteResult tResult = mDayRankingRegionCollection.deleteOne( tFilter );
            return tResult.getDeletedCount();
        }

        
            public long deleteDayRankingRegion( String pDate, int pRegionId ) {
             
        Bson tKeyFilter= Filters.and( 
            Filters.eq("date", pDate) ,
            Filters.eq("regionId", pRegionId) );
    
             DeleteResult tResult =  mDayRankingRegionCollection.deleteMany(tKeyFilter);
             return tResult.getDeletedCount();
            }
        
            public long deleteDayRankingRegionByDate( String pDate ) {
                
            Bson tKeyFilter= Filters.eq("date", pDate); 
                DeleteResult tResult =  mDayRankingRegionCollection.deleteMany(tKeyFilter);
                return tResult.getDeletedCount();
            }
        
            public long deleteDayRankingRegionByRegionId( int pRegionId ) {
                
            Bson tKeyFilter= Filters.eq("regionId", pRegionId); 
                DeleteResult tResult =  mDayRankingRegionCollection.deleteMany(tKeyFilter);
                return tResult.getDeletedCount();
            }
        
        /**
        * CRUD INSERT methods
        */
        public void insertDayRankingRegion( DayRankingRegion pDayRankingRegion ) {
            Document tDoc = pDayRankingRegion.getMongoDocument();
            mDayRankingRegionCollection.insertOne( tDoc );
            ObjectId _tId = tDoc.getObjectId("_id");
            if (_tId != null) {
                pDayRankingRegion.setMongoId( _tId.toString());
            }
        }

        public void insertDayRankingRegion( List<DayRankingRegion> pDayRankingRegionList ) {
           List<Document> tList = pDayRankingRegionList.stream().map( DayRankingRegion::getMongoDocument).collect(Collectors.toList());
           mDayRankingRegionCollection.insertMany( tList );
           for( int i = 0; i < tList.size(); i++ ) {
             ObjectId _tId = tList.get(i).getObjectId("_id");
             if (_tId != null) {
                pDayRankingRegionList.get(i).setMongoId( _tId.toString());
             }
           }
        }

    
        /**
        * CRUD UPDATE (INSERT) methods
        */
        public UpdateResult updateDayRankingRegionByMongoId( String pMongoObjectId, DayRankingRegion pDayRankingRegion ) {
            Bson tFilter =  Filters.eq("_id", new ObjectId(pMongoObjectId));
            Document tDocSet = new Document("$set", pDayRankingRegion.getMongoDocument());
            UpdateResult tUpdSts = mDayRankingRegionCollection.updateOne( tFilter, tDocSet, new UpdateOptions());
            return tUpdSts;
        }

        public UpdateResult updateDayRankingRegion( DayRankingRegion pDayRankingRegion, boolean pUpdateAllowInsert ) {
        UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
        
        Bson tKeyFilter= Filters.and( 
            Filters.eq("date", pDayRankingRegion.getDate().get()) ,
            Filters.eq("regionId", pDayRankingRegion.getRegionId().get()) );
    


        Document tDocSet = new Document("$set", pDayRankingRegion.getMongoDocument());

        UpdateResult tUpdSts = mDayRankingRegionCollection.updateOne( tKeyFilter, tDocSet, tOptions);
        return tUpdSts;
        }


        public UpdateResult updateDayRankingRegion( String pDate, int pRegionId, DayRankingRegion pDayRankingRegion, boolean pUpdateAllowInsert ) {
          UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
          
        Bson tKeyFilter= Filters.and( 
            Filters.eq("date", pDate) ,
            Filters.eq("regionId", pRegionId) );
    

           Document tDocSet = new Document("$set", pDayRankingRegion.getMongoDocument());

           UpdateResult tUpdSts = mDayRankingRegionCollection.updateOne( tKeyFilter, tDocSet, tOptions);
           return tUpdSts;
        }

        public UpdateResult updateDayRankingRegion( Bson pFilter, DayRankingRegion pDayRankingRegion, boolean pUpdateAllowInsert ) {
           UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
           Document tDocSet = new Document("$set", pDayRankingRegion.getMongoDocument());
           UpdateResult tUpdSts = mDayRankingRegionCollection.updateOne( pFilter, tDocSet, tOptions);
           return tUpdSts;
        }
    
        /**
        * CRUD FIND methods
        */

        public List<DayRankingRegion> findDayRankingRegion( Bson pFilter  ) {
         return findDayRankingRegion( pFilter, null );
        }

        public List<DayRankingRegion> findDayRankingRegion( Bson pFilter, Bson pSortDoc  ) {

        FindIterable<Document> tDocuments = (pSortDoc == null) ? this.mDayRankingRegionCollection.find( pFilter ) :
        this.mDayRankingRegionCollection.find( pFilter ).sort( pSortDoc );


        if (tDocuments == null) {
        return null;
        }

        List<DayRankingRegion> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        DayRankingRegion tDayRankingRegion = new DayRankingRegion();
        tDayRankingRegion.decodeMongoDocument( tDoc );
        tResult.add( tDayRankingRegion );
        }
        return tResult;
        }



        public List<DayRankingRegion> findAllDayRankingRegion()
        {
           List<DayRankingRegion> tResult = new ArrayList<>();

           FindIterable<Document> tDocuments  = this.mDayRankingRegionCollection.find();
           MongoCursor<Document> tIter = tDocuments.iterator();
           while( tIter.hasNext()) {
               Document tDoc = tIter.next();
               DayRankingRegion tDayRankingRegion = new DayRankingRegion();
               tDayRankingRegion.decodeMongoDocument( tDoc );
               tResult.add(tDayRankingRegion);
            }
            return tResult;
        }

        public DayRankingRegion findDayRankingRegionByMongoId( String pMongoObjectId ) {
        Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));

        FindIterable<Document> tDocuments = this.mDayRankingRegionCollection.find( tFilter );
        if (tDocuments == null) {
            return null;
        }

        List<DayRankingRegion> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        DayRankingRegion tDayRankingRegion = new DayRankingRegion();
        tDayRankingRegion.decodeMongoDocument( tDoc );
        tResult.add( tDayRankingRegion );
        }
        return (tResult.size() > 0) ? tResult.get(0) : null;
        }


        public List<DayRankingRegion> findDayRankingRegion( String pDate, int pRegionId ) {
            
        Bson tKeyFilter= Filters.and( 
            Filters.eq("date", pDate) ,
            Filters.eq("regionId", pRegionId) );
    

        FindIterable<Document> tDocuments = this.mDayRankingRegionCollection.find( tKeyFilter );
        if (tDocuments == null) {
           return null;
        }

        List<DayRankingRegion> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
           Document tDoc = tIter.next();
           DayRankingRegion tDayRankingRegion = new DayRankingRegion();
           tDayRankingRegion.decodeMongoDocument( tDoc );
           tResult.add( tDayRankingRegion );
        }
        return tResult;
        }

        
            public List<DayRankingRegion> findDayRankingRegionByDate( String pDate ) {
            List<DayRankingRegion> tResult = new ArrayList<>();
            
            Bson tKeyFilter= Filters.eq("date", pDate); 

            FindIterable<Document> tDocuments  = this.mDayRankingRegionCollection.find( tKeyFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            DayRankingRegion tDayRankingRegion = new DayRankingRegion();
            tDayRankingRegion.decodeMongoDocument( tDoc );
            tResult.add(tDayRankingRegion);
            }
            return tResult;
            }
        
            public List<DayRankingRegion> findDayRankingRegionByRegionId( int pRegionId ) {
            List<DayRankingRegion> tResult = new ArrayList<>();
            
            Bson tKeyFilter= Filters.eq("regionId", pRegionId); 

            FindIterable<Document> tDocuments  = this.mDayRankingRegionCollection.find( tKeyFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            DayRankingRegion tDayRankingRegion = new DayRankingRegion();
            tDayRankingRegion.decodeMongoDocument( tDoc );
            tResult.add(tDayRankingRegion);
            }
            return tResult;
            }
        
        /**
        * CRUD DELETE methods
        */
        public long deleteUser( Bson pFilter) {
           DeleteResult tResult = mUserCollection.deleteMany( pFilter );
           return tResult.getDeletedCount();
        }

        public long deleteUserByMongoId( String pMongoObjectId) {
            Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));
            DeleteResult tResult = mUserCollection.deleteOne( tFilter );
            return tResult.getDeletedCount();
        }

        
            public long deleteUserByMailAddr( String pMailAddr ) {
                
            Bson tKeyFilter= Filters.eq("mailAddr", pMailAddr); 
                DeleteResult tResult =  mUserCollection.deleteMany(tKeyFilter);
                return tResult.getDeletedCount();
            }
        
        /**
        * CRUD INSERT methods
        */
        public void insertUser( User pUser ) {
            Document tDoc = pUser.getMongoDocument();
            mUserCollection.insertOne( tDoc );
            ObjectId _tId = tDoc.getObjectId("_id");
            if (_tId != null) {
                pUser.setMongoId( _tId.toString());
            }
        }

        public void insertUser( List<User> pUserList ) {
           List<Document> tList = pUserList.stream().map( User::getMongoDocument).collect(Collectors.toList());
           mUserCollection.insertMany( tList );
           for( int i = 0; i < tList.size(); i++ ) {
             ObjectId _tId = tList.get(i).getObjectId("_id");
             if (_tId != null) {
                pUserList.get(i).setMongoId( _tId.toString());
             }
           }
        }

    
        /**
        * CRUD UPDATE (INSERT) methods
        */
        public UpdateResult updateUserByMongoId( String pMongoObjectId, User pUser ) {
            Bson tFilter =  Filters.eq("_id", new ObjectId(pMongoObjectId));
            Document tDocSet = new Document("$set", pUser.getMongoDocument());
            UpdateResult tUpdSts = mUserCollection.updateOne( tFilter, tDocSet, new UpdateOptions());
            return tUpdSts;
        }

        public UpdateResult updateUser( User pUser, boolean pUpdateAllowInsert ) {
        UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
        
        Bson tKeyFilter= Filters.and( 
            Filters.eq("mailAddr", pUser.getMailAddr().get()) );
    


        Document tDocSet = new Document("$set", pUser.getMongoDocument());

        UpdateResult tUpdSts = mUserCollection.updateOne( tKeyFilter, tDocSet, tOptions);
        return tUpdSts;
        }


        public UpdateResult updateUser( String pMailAddr, User pUser, boolean pUpdateAllowInsert ) {
          UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
          
        Bson tKeyFilter= Filters.and( 
            Filters.eq("mailAddr", pMailAddr) );
    

           Document tDocSet = new Document("$set", pUser.getMongoDocument());

           UpdateResult tUpdSts = mUserCollection.updateOne( tKeyFilter, tDocSet, tOptions);
           return tUpdSts;
        }

        public UpdateResult updateUser( Bson pFilter, User pUser, boolean pUpdateAllowInsert ) {
           UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
           Document tDocSet = new Document("$set", pUser.getMongoDocument());
           UpdateResult tUpdSts = mUserCollection.updateOne( pFilter, tDocSet, tOptions);
           return tUpdSts;
        }
    
        /**
        * CRUD FIND methods
        */

        public List<User> findUser( Bson pFilter  ) {
         return findUser( pFilter, null );
        }

        public List<User> findUser( Bson pFilter, Bson pSortDoc  ) {

        FindIterable<Document> tDocuments = (pSortDoc == null) ? this.mUserCollection.find( pFilter ) :
        this.mUserCollection.find( pFilter ).sort( pSortDoc );


        if (tDocuments == null) {
        return null;
        }

        List<User> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        User tUser = new User();
        tUser.decodeMongoDocument( tDoc );
        tResult.add( tUser );
        }
        return tResult;
        }



        public List<User> findAllUser()
        {
           List<User> tResult = new ArrayList<>();

           FindIterable<Document> tDocuments  = this.mUserCollection.find();
           MongoCursor<Document> tIter = tDocuments.iterator();
           while( tIter.hasNext()) {
               Document tDoc = tIter.next();
               User tUser = new User();
               tUser.decodeMongoDocument( tDoc );
               tResult.add(tUser);
            }
            return tResult;
        }

        public User findUserByMongoId( String pMongoObjectId ) {
        Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));

        FindIterable<Document> tDocuments = this.mUserCollection.find( tFilter );
        if (tDocuments == null) {
            return null;
        }

        List<User> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        User tUser = new User();
        tUser.decodeMongoDocument( tDoc );
        tResult.add( tUser );
        }
        return (tResult.size() > 0) ? tResult.get(0) : null;
        }


        public List<User> findUser( String pMailAddr ) {
            
        Bson tKeyFilter= Filters.and( 
            Filters.eq("mailAddr", pMailAddr) );
    

        FindIterable<Document> tDocuments = this.mUserCollection.find( tKeyFilter );
        if (tDocuments == null) {
           return null;
        }

        List<User> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
           Document tDoc = tIter.next();
           User tUser = new User();
           tUser.decodeMongoDocument( tDoc );
           tResult.add( tUser );
        }
        return tResult;
        }

        
            public List<User> findUserByMailAddr( String pMailAddr ) {
            List<User> tResult = new ArrayList<>();
            
            Bson tKeyFilter= Filters.eq("mailAddr", pMailAddr); 

            FindIterable<Document> tDocuments  = this.mUserCollection.find( tKeyFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            User tUser = new User();
            tUser.decodeMongoDocument( tDoc );
            tResult.add(tUser);
            }
            return tResult;
            }
        
        /**
        * CRUD DELETE methods
        */
        public long deleteSubscription( Bson pFilter) {
           DeleteResult tResult = mSubscriptionCollection.deleteMany( pFilter );
           return tResult.getDeletedCount();
        }

        public long deleteSubscriptionByMongoId( String pMongoObjectId) {
            Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));
            DeleteResult tResult = mSubscriptionCollection.deleteOne( tFilter );
            return tResult.getDeletedCount();
        }

        
            public long deleteSubscription( String pMailAddr, String pZoneName, int pZoneId ) {
             
        Bson tKeyFilter= Filters.and( 
            Filters.eq("mailAddr", pMailAddr) ,
            Filters.eq("zoneName", pZoneName) ,
            Filters.eq("zoneId", pZoneId) );
    
             DeleteResult tResult =  mSubscriptionCollection.deleteMany(tKeyFilter);
             return tResult.getDeletedCount();
            }
        
            public long deleteSubscriptionByMailAddr( String pMailAddr ) {
                
            Bson tKeyFilter= Filters.eq("mailAddr", pMailAddr); 
                DeleteResult tResult =  mSubscriptionCollection.deleteMany(tKeyFilter);
                return tResult.getDeletedCount();
            }
        
            public long deleteSubscriptionByZoneName( String pZoneName ) {
                
            Bson tKeyFilter= Filters.eq("zoneName", pZoneName); 
                DeleteResult tResult =  mSubscriptionCollection.deleteMany(tKeyFilter);
                return tResult.getDeletedCount();
            }
        
            public long deleteSubscriptionByZoneId( int pZoneId ) {
                
            Bson tKeyFilter= Filters.eq("zoneId", pZoneId); 
                DeleteResult tResult =  mSubscriptionCollection.deleteMany(tKeyFilter);
                return tResult.getDeletedCount();
            }
        
        /**
        * CRUD INSERT methods
        */
        public void insertSubscription( Subscription pSubscription ) {
            Document tDoc = pSubscription.getMongoDocument();
            mSubscriptionCollection.insertOne( tDoc );
            ObjectId _tId = tDoc.getObjectId("_id");
            if (_tId != null) {
                pSubscription.setMongoId( _tId.toString());
            }
        }

        public void insertSubscription( List<Subscription> pSubscriptionList ) {
           List<Document> tList = pSubscriptionList.stream().map( Subscription::getMongoDocument).collect(Collectors.toList());
           mSubscriptionCollection.insertMany( tList );
           for( int i = 0; i < tList.size(); i++ ) {
             ObjectId _tId = tList.get(i).getObjectId("_id");
             if (_tId != null) {
                pSubscriptionList.get(i).setMongoId( _tId.toString());
             }
           }
        }

    
        /**
        * CRUD UPDATE (INSERT) methods
        */
        public UpdateResult updateSubscriptionByMongoId( String pMongoObjectId, Subscription pSubscription ) {
            Bson tFilter =  Filters.eq("_id", new ObjectId(pMongoObjectId));
            Document tDocSet = new Document("$set", pSubscription.getMongoDocument());
            UpdateResult tUpdSts = mSubscriptionCollection.updateOne( tFilter, tDocSet, new UpdateOptions());
            return tUpdSts;
        }

        public UpdateResult updateSubscription( Subscription pSubscription, boolean pUpdateAllowInsert ) {
        UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
        
        Bson tKeyFilter= Filters.and( 
            Filters.eq("mailAddr", pSubscription.getMailAddr().get()) ,
            Filters.eq("zoneName", pSubscription.getZoneName().get()) ,
            Filters.eq("zoneId", pSubscription.getZoneId().get()) );
    


        Document tDocSet = new Document("$set", pSubscription.getMongoDocument());

        UpdateResult tUpdSts = mSubscriptionCollection.updateOne( tKeyFilter, tDocSet, tOptions);
        return tUpdSts;
        }


        public UpdateResult updateSubscription( String pMailAddr, String pZoneName, int pZoneId, Subscription pSubscription, boolean pUpdateAllowInsert ) {
          UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
          
        Bson tKeyFilter= Filters.and( 
            Filters.eq("mailAddr", pMailAddr) ,
            Filters.eq("zoneName", pZoneName) ,
            Filters.eq("zoneId", pZoneId) );
    

           Document tDocSet = new Document("$set", pSubscription.getMongoDocument());

           UpdateResult tUpdSts = mSubscriptionCollection.updateOne( tKeyFilter, tDocSet, tOptions);
           return tUpdSts;
        }

        public UpdateResult updateSubscription( Bson pFilter, Subscription pSubscription, boolean pUpdateAllowInsert ) {
           UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
           Document tDocSet = new Document("$set", pSubscription.getMongoDocument());
           UpdateResult tUpdSts = mSubscriptionCollection.updateOne( pFilter, tDocSet, tOptions);
           return tUpdSts;
        }
    
        /**
        * CRUD FIND methods
        */

        public List<Subscription> findSubscription( Bson pFilter  ) {
         return findSubscription( pFilter, null );
        }

        public List<Subscription> findSubscription( Bson pFilter, Bson pSortDoc  ) {

        FindIterable<Document> tDocuments = (pSortDoc == null) ? this.mSubscriptionCollection.find( pFilter ) :
        this.mSubscriptionCollection.find( pFilter ).sort( pSortDoc );


        if (tDocuments == null) {
        return null;
        }

        List<Subscription> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        Subscription tSubscription = new Subscription();
        tSubscription.decodeMongoDocument( tDoc );
        tResult.add( tSubscription );
        }
        return tResult;
        }



        public List<Subscription> findAllSubscription()
        {
           List<Subscription> tResult = new ArrayList<>();

           FindIterable<Document> tDocuments  = this.mSubscriptionCollection.find();
           MongoCursor<Document> tIter = tDocuments.iterator();
           while( tIter.hasNext()) {
               Document tDoc = tIter.next();
               Subscription tSubscription = new Subscription();
               tSubscription.decodeMongoDocument( tDoc );
               tResult.add(tSubscription);
            }
            return tResult;
        }

        public Subscription findSubscriptionByMongoId( String pMongoObjectId ) {
        Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));

        FindIterable<Document> tDocuments = this.mSubscriptionCollection.find( tFilter );
        if (tDocuments == null) {
            return null;
        }

        List<Subscription> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        Subscription tSubscription = new Subscription();
        tSubscription.decodeMongoDocument( tDoc );
        tResult.add( tSubscription );
        }
        return (tResult.size() > 0) ? tResult.get(0) : null;
        }


        public List<Subscription> findSubscription( String pMailAddr, String pZoneName, int pZoneId ) {
            
        Bson tKeyFilter= Filters.and( 
            Filters.eq("mailAddr", pMailAddr) ,
            Filters.eq("zoneName", pZoneName) ,
            Filters.eq("zoneId", pZoneId) );
    

        FindIterable<Document> tDocuments = this.mSubscriptionCollection.find( tKeyFilter );
        if (tDocuments == null) {
           return null;
        }

        List<Subscription> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
           Document tDoc = tIter.next();
           Subscription tSubscription = new Subscription();
           tSubscription.decodeMongoDocument( tDoc );
           tResult.add( tSubscription );
        }
        return tResult;
        }

        
            public List<Subscription> findSubscriptionByMailAddr( String pMailAddr ) {
            List<Subscription> tResult = new ArrayList<>();
            
            Bson tKeyFilter= Filters.eq("mailAddr", pMailAddr); 

            FindIterable<Document> tDocuments  = this.mSubscriptionCollection.find( tKeyFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            Subscription tSubscription = new Subscription();
            tSubscription.decodeMongoDocument( tDoc );
            tResult.add(tSubscription);
            }
            return tResult;
            }
        
            public List<Subscription> findSubscriptionByZoneName( String pZoneName ) {
            List<Subscription> tResult = new ArrayList<>();
            
            Bson tKeyFilter= Filters.eq("zoneName", pZoneName); 

            FindIterable<Document> tDocuments  = this.mSubscriptionCollection.find( tKeyFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            Subscription tSubscription = new Subscription();
            tSubscription.decodeMongoDocument( tDoc );
            tResult.add(tSubscription);
            }
            return tResult;
            }
        
            public List<Subscription> findSubscriptionByZoneId( int pZoneId ) {
            List<Subscription> tResult = new ArrayList<>();
            
            Bson tKeyFilter= Filters.eq("zoneId", pZoneId); 

            FindIterable<Document> tDocuments  = this.mSubscriptionCollection.find( tKeyFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            Subscription tSubscription = new Subscription();
            tSubscription.decodeMongoDocument( tDoc );
            tResult.add(tSubscription);
            }
            return tResult;
            }
        
        /**
        * CRUD DELETE methods
        */
        public long deleteFirstEntry( Bson pFilter) {
           DeleteResult tResult = mFirstEntryCollection.deleteMany( pFilter );
           return tResult.getDeletedCount();
        }

        public long deleteFirstEntryByMongoId( String pMongoObjectId) {
            Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));
            DeleteResult tResult = mFirstEntryCollection.deleteOne( tFilter );
            return tResult.getDeletedCount();
        }

        
            public long deleteFirstEntryByTimestamp( long pTimestamp ) {
                
            Bson tKeyFilter= Filters.eq("timestamp", pTimestamp); 
                DeleteResult tResult =  mFirstEntryCollection.deleteMany(tKeyFilter);
                return tResult.getDeletedCount();
            }
        
        /**
        * CRUD INSERT methods
        */
        public void insertFirstEntry( FirstEntry pFirstEntry ) {
            Document tDoc = pFirstEntry.getMongoDocument();
            mFirstEntryCollection.insertOne( tDoc );
            ObjectId _tId = tDoc.getObjectId("_id");
            if (_tId != null) {
                pFirstEntry.setMongoId( _tId.toString());
            }
        }

        public void insertFirstEntry( List<FirstEntry> pFirstEntryList ) {
           List<Document> tList = pFirstEntryList.stream().map( FirstEntry::getMongoDocument).collect(Collectors.toList());
           mFirstEntryCollection.insertMany( tList );
           for( int i = 0; i < tList.size(); i++ ) {
             ObjectId _tId = tList.get(i).getObjectId("_id");
             if (_tId != null) {
                pFirstEntryList.get(i).setMongoId( _tId.toString());
             }
           }
        }

    
        /**
        * CRUD UPDATE (INSERT) methods
        */
        public UpdateResult updateFirstEntryByMongoId( String pMongoObjectId, FirstEntry pFirstEntry ) {
            Bson tFilter =  Filters.eq("_id", new ObjectId(pMongoObjectId));
            Document tDocSet = new Document("$set", pFirstEntry.getMongoDocument());
            UpdateResult tUpdSts = mFirstEntryCollection.updateOne( tFilter, tDocSet, new UpdateOptions());
            return tUpdSts;
        }

        public UpdateResult updateFirstEntry( FirstEntry pFirstEntry, boolean pUpdateAllowInsert ) {
        UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
        
        Bson tKeyFilter= Filters.and( 
            Filters.eq("timestamp", pFirstEntry.getTimestamp().get()) );
    


        Document tDocSet = new Document("$set", pFirstEntry.getMongoDocument());

        UpdateResult tUpdSts = mFirstEntryCollection.updateOne( tKeyFilter, tDocSet, tOptions);
        return tUpdSts;
        }


        public UpdateResult updateFirstEntry( long pTimestamp, FirstEntry pFirstEntry, boolean pUpdateAllowInsert ) {
          UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
          
        Bson tKeyFilter= Filters.and( 
            Filters.eq("timestamp", pTimestamp) );
    

           Document tDocSet = new Document("$set", pFirstEntry.getMongoDocument());

           UpdateResult tUpdSts = mFirstEntryCollection.updateOne( tKeyFilter, tDocSet, tOptions);
           return tUpdSts;
        }

        public UpdateResult updateFirstEntry( Bson pFilter, FirstEntry pFirstEntry, boolean pUpdateAllowInsert ) {
           UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
           Document tDocSet = new Document("$set", pFirstEntry.getMongoDocument());
           UpdateResult tUpdSts = mFirstEntryCollection.updateOne( pFilter, tDocSet, tOptions);
           return tUpdSts;
        }
    
        /**
        * CRUD FIND methods
        */

        public List<FirstEntry> findFirstEntry( Bson pFilter  ) {
         return findFirstEntry( pFilter, null );
        }

        public List<FirstEntry> findFirstEntry( Bson pFilter, Bson pSortDoc  ) {

        FindIterable<Document> tDocuments = (pSortDoc == null) ? this.mFirstEntryCollection.find( pFilter ) :
        this.mFirstEntryCollection.find( pFilter ).sort( pSortDoc );


        if (tDocuments == null) {
        return null;
        }

        List<FirstEntry> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        FirstEntry tFirstEntry = new FirstEntry();
        tFirstEntry.decodeMongoDocument( tDoc );
        tResult.add( tFirstEntry );
        }
        return tResult;
        }



        public List<FirstEntry> findAllFirstEntry()
        {
           List<FirstEntry> tResult = new ArrayList<>();

           FindIterable<Document> tDocuments  = this.mFirstEntryCollection.find();
           MongoCursor<Document> tIter = tDocuments.iterator();
           while( tIter.hasNext()) {
               Document tDoc = tIter.next();
               FirstEntry tFirstEntry = new FirstEntry();
               tFirstEntry.decodeMongoDocument( tDoc );
               tResult.add(tFirstEntry);
            }
            return tResult;
        }

        public FirstEntry findFirstEntryByMongoId( String pMongoObjectId ) {
        Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));

        FindIterable<Document> tDocuments = this.mFirstEntryCollection.find( tFilter );
        if (tDocuments == null) {
            return null;
        }

        List<FirstEntry> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        FirstEntry tFirstEntry = new FirstEntry();
        tFirstEntry.decodeMongoDocument( tDoc );
        tResult.add( tFirstEntry );
        }
        return (tResult.size() > 0) ? tResult.get(0) : null;
        }


        public List<FirstEntry> findFirstEntry( long pTimestamp ) {
            
        Bson tKeyFilter= Filters.and( 
            Filters.eq("timestamp", pTimestamp) );
    

        FindIterable<Document> tDocuments = this.mFirstEntryCollection.find( tKeyFilter );
        if (tDocuments == null) {
           return null;
        }

        List<FirstEntry> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
           Document tDoc = tIter.next();
           FirstEntry tFirstEntry = new FirstEntry();
           tFirstEntry.decodeMongoDocument( tDoc );
           tResult.add( tFirstEntry );
        }
        return tResult;
        }

        
            public List<FirstEntry> findFirstEntryByTimestamp( long pTimestamp ) {
            List<FirstEntry> tResult = new ArrayList<>();
            
            Bson tKeyFilter= Filters.eq("timestamp", pTimestamp); 

            FindIterable<Document> tDocuments  = this.mFirstEntryCollection.find( tKeyFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            FirstEntry tFirstEntry = new FirstEntry();
            tFirstEntry.decodeMongoDocument( tDoc );
            tResult.add(tFirstEntry);
            }
            return tResult;
            }
        
        /**
        * CRUD DELETE methods
        */
        public long deleteTurfActivityZone( Bson pFilter) {
           DeleteResult tResult = mZoneHeatMapCollection.deleteMany( pFilter );
           return tResult.getDeletedCount();
        }

        public long deleteTurfActivityZoneByMongoId( String pMongoObjectId) {
            Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));
            DeleteResult tResult = mZoneHeatMapCollection.deleteOne( tFilter );
            return tResult.getDeletedCount();
        }

        
            public long deleteTurfActivityZone( int pRegionId, int pZoneId ) {
             
        Bson tKeyFilter= Filters.and( 
            Filters.eq("regionId", pRegionId) ,
            Filters.eq("zoneId", pZoneId) );
    
             DeleteResult tResult =  mZoneHeatMapCollection.deleteMany(tKeyFilter);
             return tResult.getDeletedCount();
            }
        
            public long deleteTurfActivityZoneByRegionId( int pRegionId ) {
                
            Bson tKeyFilter= Filters.eq("regionId", pRegionId); 
                DeleteResult tResult =  mZoneHeatMapCollection.deleteMany(tKeyFilter);
                return tResult.getDeletedCount();
            }
        
            public long deleteTurfActivityZoneByZoneId( int pZoneId ) {
                
            Bson tKeyFilter= Filters.eq("zoneId", pZoneId); 
                DeleteResult tResult =  mZoneHeatMapCollection.deleteMany(tKeyFilter);
                return tResult.getDeletedCount();
            }
        
        /**
        * CRUD INSERT methods
        */
        public void insertTurfActivityZone( TurfActivityZone pTurfActivityZone ) {
            Document tDoc = pTurfActivityZone.getMongoDocument();
            mZoneHeatMapCollection.insertOne( tDoc );
            ObjectId _tId = tDoc.getObjectId("_id");
            if (_tId != null) {
                pTurfActivityZone.setMongoId( _tId.toString());
            }
        }

        public void insertTurfActivityZone( List<TurfActivityZone> pTurfActivityZoneList ) {
           List<Document> tList = pTurfActivityZoneList.stream().map( TurfActivityZone::getMongoDocument).collect(Collectors.toList());
           mZoneHeatMapCollection.insertMany( tList );
           for( int i = 0; i < tList.size(); i++ ) {
             ObjectId _tId = tList.get(i).getObjectId("_id");
             if (_tId != null) {
                pTurfActivityZoneList.get(i).setMongoId( _tId.toString());
             }
           }
        }

    
        /**
        * CRUD UPDATE (INSERT) methods
        */
        public UpdateResult updateTurfActivityZoneByMongoId( String pMongoObjectId, TurfActivityZone pTurfActivityZone ) {
            Bson tFilter =  Filters.eq("_id", new ObjectId(pMongoObjectId));
            Document tDocSet = new Document("$set", pTurfActivityZone.getMongoDocument());
            UpdateResult tUpdSts = mZoneHeatMapCollection.updateOne( tFilter, tDocSet, new UpdateOptions());
            return tUpdSts;
        }

        public UpdateResult updateTurfActivityZone( TurfActivityZone pTurfActivityZone, boolean pUpdateAllowInsert ) {
        UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
        
        Bson tKeyFilter= Filters.and( 
            Filters.eq("regionId", pTurfActivityZone.getRegionId().get()) ,
            Filters.eq("zoneId", pTurfActivityZone.getZoneId().get()) );
    


        Document tDocSet = new Document("$set", pTurfActivityZone.getMongoDocument());

        UpdateResult tUpdSts = mZoneHeatMapCollection.updateOne( tKeyFilter, tDocSet, tOptions);
        return tUpdSts;
        }


        public UpdateResult updateTurfActivityZone( int pRegionId, int pZoneId, TurfActivityZone pTurfActivityZone, boolean pUpdateAllowInsert ) {
          UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
          
        Bson tKeyFilter= Filters.and( 
            Filters.eq("regionId", pRegionId) ,
            Filters.eq("zoneId", pZoneId) );
    

           Document tDocSet = new Document("$set", pTurfActivityZone.getMongoDocument());

           UpdateResult tUpdSts = mZoneHeatMapCollection.updateOne( tKeyFilter, tDocSet, tOptions);
           return tUpdSts;
        }

        public UpdateResult updateTurfActivityZone( Bson pFilter, TurfActivityZone pTurfActivityZone, boolean pUpdateAllowInsert ) {
           UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
           Document tDocSet = new Document("$set", pTurfActivityZone.getMongoDocument());
           UpdateResult tUpdSts = mZoneHeatMapCollection.updateOne( pFilter, tDocSet, tOptions);
           return tUpdSts;
        }
    
        /**
        * CRUD FIND methods
        */

        public List<TurfActivityZone> findTurfActivityZone( Bson pFilter  ) {
         return findTurfActivityZone( pFilter, null );
        }

        public List<TurfActivityZone> findTurfActivityZone( Bson pFilter, Bson pSortDoc  ) {

        FindIterable<Document> tDocuments = (pSortDoc == null) ? this.mZoneHeatMapCollection.find( pFilter ) :
        this.mZoneHeatMapCollection.find( pFilter ).sort( pSortDoc );


        if (tDocuments == null) {
        return null;
        }

        List<TurfActivityZone> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        TurfActivityZone tTurfActivityZone = new TurfActivityZone();
        tTurfActivityZone.decodeMongoDocument( tDoc );
        tResult.add( tTurfActivityZone );
        }
        return tResult;
        }



        public List<TurfActivityZone> findAllTurfActivityZone()
        {
           List<TurfActivityZone> tResult = new ArrayList<>();

           FindIterable<Document> tDocuments  = this.mZoneHeatMapCollection.find();
           MongoCursor<Document> tIter = tDocuments.iterator();
           while( tIter.hasNext()) {
               Document tDoc = tIter.next();
               TurfActivityZone tTurfActivityZone = new TurfActivityZone();
               tTurfActivityZone.decodeMongoDocument( tDoc );
               tResult.add(tTurfActivityZone);
            }
            return tResult;
        }

        public TurfActivityZone findTurfActivityZoneByMongoId( String pMongoObjectId ) {
        Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));

        FindIterable<Document> tDocuments = this.mZoneHeatMapCollection.find( tFilter );
        if (tDocuments == null) {
            return null;
        }

        List<TurfActivityZone> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
        Document tDoc = tIter.next();
        TurfActivityZone tTurfActivityZone = new TurfActivityZone();
        tTurfActivityZone.decodeMongoDocument( tDoc );
        tResult.add( tTurfActivityZone );
        }
        return (tResult.size() > 0) ? tResult.get(0) : null;
        }


        public List<TurfActivityZone> findTurfActivityZone( int pRegionId, int pZoneId ) {
            
        Bson tKeyFilter= Filters.and( 
            Filters.eq("regionId", pRegionId) ,
            Filters.eq("zoneId", pZoneId) );
    

        FindIterable<Document> tDocuments = this.mZoneHeatMapCollection.find( tKeyFilter );
        if (tDocuments == null) {
           return null;
        }

        List<TurfActivityZone> tResult = new ArrayList<>();
        MongoCursor<Document> tIter = tDocuments.iterator();
        while ( tIter.hasNext()) {
           Document tDoc = tIter.next();
           TurfActivityZone tTurfActivityZone = new TurfActivityZone();
           tTurfActivityZone.decodeMongoDocument( tDoc );
           tResult.add( tTurfActivityZone );
        }
        return tResult;
        }

        
            public List<TurfActivityZone> findTurfActivityZoneByRegionId( int pRegionId ) {
            List<TurfActivityZone> tResult = new ArrayList<>();
            
            Bson tKeyFilter= Filters.eq("regionId", pRegionId); 

            FindIterable<Document> tDocuments  = this.mZoneHeatMapCollection.find( tKeyFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            TurfActivityZone tTurfActivityZone = new TurfActivityZone();
            tTurfActivityZone.decodeMongoDocument( tDoc );
            tResult.add(tTurfActivityZone);
            }
            return tResult;
            }
        
            public List<TurfActivityZone> findTurfActivityZoneByZoneId( int pZoneId ) {
            List<TurfActivityZone> tResult = new ArrayList<>();
            
            Bson tKeyFilter= Filters.eq("zoneId", pZoneId); 

            FindIterable<Document> tDocuments  = this.mZoneHeatMapCollection.find( tKeyFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            TurfActivityZone tTurfActivityZone = new TurfActivityZone();
            tTurfActivityZone.decodeMongoDocument( tDoc );
            tResult.add(tTurfActivityZone);
            }
            return tResult;
            }
        

            class DbKey {
                String      mKeyName;
                boolean     mUnique;

                DbKey( String pKeyName, boolean pUnique ) {
                    mKeyName = pKeyName;
                    mUnique = pUnique;
                }
            }

            }
            