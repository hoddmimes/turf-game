
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
            
           private MongoCollection mUserCollection;
           private MongoCollection mSubscriptionCollection;

            public MongoAux( String pDbName, String pDbHost, int pDbPort ) {
               mDbName = pDbName;
               mDbHost = pDbHost;
               mDbPort = pDbPort;
            }

            public void connectToDatabase() {
               try {
                 mClient = new MongoClient( mDbHost, mDbPort);
                 mDb = mClient.getDatabase( mDbName );
            
                 mUserCollection = mDb.getCollection("User");
              
                 mSubscriptionCollection = mDb.getCollection("Subscription");
              
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
            
                    mUserCollection = null;
                
                    mSubscriptionCollection = null;
                
                 }
            }

            private void close() {
                if (mClient != null) {
                    mClient.close();
                    mDb = null;

            
                    mUserCollection = null;
                
                    mSubscriptionCollection = null;
                
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
                
                        createUserCollection();
                   
                        createSubscriptionCollection();
                   
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
            UpdateResult tUpdSts = mUserCollection.updateOne( tFilter, tDocSet);
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
            UpdateResult tUpdSts = mSubscriptionCollection.updateOne( tFilter, tDocSet);
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
        

            class DbKey {
                String      mKeyName;
                boolean     mUnique;

                DbKey( String pKeyName, boolean pUnique ) {
                    mKeyName = pKeyName;
                    mUnique = pUnique;
                }
            }

            }
        