
    package com.hoddmimes.turf.server.generated;


    import com.mongodb.BasicDBObject;
    import com.mongodb.MongoClient;
    import com.mongodb.client.MongoCollection;
    import com.mongodb.client.MongoDatabase;
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
    import java.util.ArrayList;
    import java.util.stream.Collectors;

    
    import com.hoddmimes.turf.server.generated.*;
    
    public class MongoAux {
            private String mDbName;
            private String mDbHost;
            private int mDbPort;

            private MongoClient mClient;
            private MongoDatabase mDb;
            
           private MongoCollection mHourRegionStatCollection;
           private MongoCollection mRegionCollection;
           private MongoCollection mUserCollection;
           private MongoCollection mSubscriptionCollection;
           private MongoCollection mFirstEntryCollection;

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
              
                 mUserCollection = mDb.getCollection("User");
              
                 mSubscriptionCollection = mDb.getCollection("Subscription");
              
                 mFirstEntryCollection = mDb.getCollection("FirstEntry");
              
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
                
                    mUserCollection = null;
                
                    mSubscriptionCollection = null;
                
                    mFirstEntryCollection = null;
                
                 }
            }

            private void close() {
                if (mClient != null) {
                    mClient.close();
                    mDb = null;

            
                    mHourRegionStatCollection = null;
                
                    mRegionCollection = null;
                
                    mUserCollection = null;
                
                    mSubscriptionCollection = null;
                
                    mFirstEntryCollection = null;
                
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


            public void createDatabase() {
                this.close();
                MongoClient mClient = new MongoClient(mDbHost, mDbPort);
                MongoDatabase tDB = mClient.getDatabase( mDbName );
                
                        createHourRegionStatCollection();
                   
                        createRegionCollection();
                   
                        createUserCollection();
                   
                        createSubscriptionCollection();
                   
                        createFirstEntryCollection();
                   
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
                    Filters.eq("id", pId),
                    Filters.eq("createTime", pCreateTime));

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
            Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));
            Document tDocSet = new Document("$set", pHourRegionStat.getMongoDocument());
            UpdateResult tUpdSts = mHourRegionStatCollection.updateOne( tFilter, tDocSet, new UpdateOptions());
            return tUpdSts;
        }


        public UpdateResult updateHourRegionStat( int pId, long pCreateTime, HourRegionStat pHourRegionStat, boolean pUpdateAllowInsert ) {
          UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
          Bson tFilter= Filters.and( 
             Filters.eq("id", pId),
             Filters.eq("createTime", pCreateTime));

           Document tDocSet = new Document("$set", pHourRegionStat.getMongoDocument());

           UpdateResult tUpdSts = mHourRegionStatCollection.updateOne( tFilter, tDocSet, tOptions);
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

        public List<HourRegionStat> findHourRegionStat( Bson pFilter, Document pSortDoc  ) {

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
        Bson tFilter= Filters.and( 
        Filters.eq("id", pId),
        Filters.eq("createTime", pCreateTime));


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
        return tResult;
        }

        
            public List<HourRegionStat> findHourRegionStatById( int pId ) {
            List<HourRegionStat> tResult = new ArrayList<>();
            Bson tFilter= Filters.eq("id", pId);

            FindIterable<Document> tDocuments  = this.mHourRegionStatCollection.find( tFilter );
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
            Bson tFilter= Filters.eq("createTime", pCreateTime);

            FindIterable<Document> tDocuments  = this.mHourRegionStatCollection.find( tFilter );
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
            Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));
            Document tDocSet = new Document("$set", pRegionStat.getMongoDocument());
            UpdateResult tUpdSts = mRegionCollection.updateOne( tFilter, tDocSet, new UpdateOptions());
            return tUpdSts;
        }


        public UpdateResult updateRegionStat( int pId, RegionStat pRegionStat, boolean pUpdateAllowInsert ) {
          UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
          Bson tFilter= Filters.and( 
             Filters.eq("id", pId));

           Document tDocSet = new Document("$set", pRegionStat.getMongoDocument());

           UpdateResult tUpdSts = mRegionCollection.updateOne( tFilter, tDocSet, tOptions);
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

        public List<RegionStat> findRegionStat( Bson pFilter, Document pSortDoc  ) {

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
        Bson tFilter= Filters.and( 
        Filters.eq("id", pId));


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
        return tResult;
        }

        
            public List<RegionStat> findRegionStatById( int pId ) {
            List<RegionStat> tResult = new ArrayList<>();
            Bson tFilter= Filters.eq("id", pId);

            FindIterable<Document> tDocuments  = this.mRegionCollection.find( tFilter );
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
            Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));
            Document tDocSet = new Document("$set", pUser.getMongoDocument());
            UpdateResult tUpdSts = mUserCollection.updateOne( tFilter, tDocSet, new UpdateOptions());
            return tUpdSts;
        }


        public UpdateResult updateUser( String pMailAddr, User pUser, boolean pUpdateAllowInsert ) {
          UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
          Bson tFilter= Filters.and( 
             Filters.eq("mailAddr", pMailAddr));

           Document tDocSet = new Document("$set", pUser.getMongoDocument());

           UpdateResult tUpdSts = mUserCollection.updateOne( tFilter, tDocSet, tOptions);
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

        public List<User> findUser( Bson pFilter, Document pSortDoc  ) {

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
        Bson tFilter= Filters.and( 
        Filters.eq("mailAddr", pMailAddr));


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
        return tResult;
        }

        
            public List<User> findUserByMailAddr( String pMailAddr ) {
            List<User> tResult = new ArrayList<>();
            Bson tFilter= Filters.eq("mailAddr", pMailAddr);

            FindIterable<Document> tDocuments  = this.mUserCollection.find( tFilter );
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
                    Filters.eq("mailAddr", pMailAddr),
                    Filters.eq("zoneName", pZoneName),
                    Filters.eq("zoneId", pZoneId));

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
            Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));
            Document tDocSet = new Document("$set", pSubscription.getMongoDocument());
            UpdateResult tUpdSts = mSubscriptionCollection.updateOne( tFilter, tDocSet, new UpdateOptions());
            return tUpdSts;
        }


        public UpdateResult updateSubscription( String pMailAddr, String pZoneName, int pZoneId, Subscription pSubscription, boolean pUpdateAllowInsert ) {
          UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
          Bson tFilter= Filters.and( 
             Filters.eq("mailAddr", pMailAddr),
             Filters.eq("zoneName", pZoneName),
             Filters.eq("zoneId", pZoneId));

           Document tDocSet = new Document("$set", pSubscription.getMongoDocument());

           UpdateResult tUpdSts = mSubscriptionCollection.updateOne( tFilter, tDocSet, tOptions);
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

        public List<Subscription> findSubscription( Bson pFilter, Document pSortDoc  ) {

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
        Bson tFilter= Filters.and( 
        Filters.eq("mailAddr", pMailAddr),
        Filters.eq("zoneName", pZoneName),
        Filters.eq("zoneId", pZoneId));


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
        return tResult;
        }

        
            public List<Subscription> findSubscriptionByMailAddr( String pMailAddr ) {
            List<Subscription> tResult = new ArrayList<>();
            Bson tFilter= Filters.eq("mailAddr", pMailAddr);

            FindIterable<Document> tDocuments  = this.mSubscriptionCollection.find( tFilter );
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
            Bson tFilter= Filters.eq("zoneName", pZoneName);

            FindIterable<Document> tDocuments  = this.mSubscriptionCollection.find( tFilter );
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
            Bson tFilter= Filters.eq("zoneId", pZoneId);

            FindIterable<Document> tDocuments  = this.mSubscriptionCollection.find( tFilter );
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
            Bson tFilter=  Filters.eq("_id", new ObjectId(pMongoObjectId));
            Document tDocSet = new Document("$set", pFirstEntry.getMongoDocument());
            UpdateResult tUpdSts = mFirstEntryCollection.updateOne( tFilter, tDocSet, new UpdateOptions());
            return tUpdSts;
        }


        public UpdateResult updateFirstEntry( long pTimestamp, FirstEntry pFirstEntry, boolean pUpdateAllowInsert ) {
          UpdateOptions tOptions = new UpdateOptions().upsert(pUpdateAllowInsert);
          Bson tFilter= Filters.and( 
             Filters.eq("timestamp", pTimestamp));

           Document tDocSet = new Document("$set", pFirstEntry.getMongoDocument());

           UpdateResult tUpdSts = mFirstEntryCollection.updateOne( tFilter, tDocSet, tOptions);
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

        public List<FirstEntry> findFirstEntry( Bson pFilter, Document pSortDoc  ) {

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
        Bson tFilter= Filters.and( 
        Filters.eq("timestamp", pTimestamp));


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
        return tResult;
        }

        
            public List<FirstEntry> findFirstEntryByTimestamp( long pTimestamp ) {
            List<FirstEntry> tResult = new ArrayList<>();
            Bson tFilter= Filters.eq("timestamp", pTimestamp);

            FindIterable<Document> tDocuments  = this.mFirstEntryCollection.find( tFilter );
            MongoCursor<Document> tIter = tDocuments.iterator();
            while( tIter.hasNext()) {
            Document tDoc = tIter.next();
            FirstEntry tFirstEntry = new FirstEntry();
            tFirstEntry.decodeMongoDocument( tDoc );
            tResult.add(tFirstEntry);
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
        