
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.io.IOException;




    import org.bson.BsonType;
    import org.bson.Document;
    import org.bson.conversions.Bson;
    import com.mongodb.BasicDBObject;
    import org.bson.types.ObjectId;
    import com.hoddmimes.jsontransform.MessageMongoInterface;
    import com.hoddmimes.jsontransform.MongoDecoder;
    import com.hoddmimes.jsontransform.MongoEncoder;


import com.hoddmimes.jsontransform.MessageInterface;
import com.hoddmimes.jsontransform.JsonDecoder;
import com.hoddmimes.jsontransform.JsonEncoder;
import com.hoddmimes.jsontransform.ListFactory;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



            

            @SuppressWarnings({"WeakerAccess","unused","unchecked"})
            public class Subscription implements MessageInterface , MessageMongoInterface
            {
                public static String NAME = "Subscription";

            
                private String mMongoId = null;
                    private String mMailAddr;
                    private String mZoneName;
                    private Integer mZoneId;
                    private String mCreateTime;
                    private String mNotificationTime;
                    private Integer mNotifications;
               public Subscription()
               {
                
               }

               public Subscription(String pJsonString ) {
                    
                    JsonDecoder tDecoder = new JsonDecoder( pJsonString );
                    this.decode( tDecoder );
               }
    
            public String getMongoId() {
            return this.mMongoId;
            }

            public void setMongoId( String pMongoId ) {
            this.mMongoId = pMongoId;
            }
        
            public Subscription setMailAddr( String pMailAddr ) {
            mMailAddr = pMailAddr;
            return this;
            }
            public Optional<String> getMailAddr() {
              return  Optional.ofNullable(mMailAddr);
            }
        
            public Subscription setZoneName( String pZoneName ) {
            mZoneName = pZoneName;
            return this;
            }
            public Optional<String> getZoneName() {
              return  Optional.ofNullable(mZoneName);
            }
        
            public Subscription setZoneId( Integer pZoneId ) {
            mZoneId = pZoneId;
            return this;
            }
            public Optional<Integer> getZoneId() {
              return  Optional.ofNullable(mZoneId);
            }
        
            public Subscription setCreateTime( String pCreateTime ) {
            mCreateTime = pCreateTime;
            return this;
            }
            public Optional<String> getCreateTime() {
              return  Optional.ofNullable(mCreateTime);
            }
        
            public Subscription setNotificationTime( String pNotificationTime ) {
            mNotificationTime = pNotificationTime;
            return this;
            }
            public Optional<String> getNotificationTime() {
              return  Optional.ofNullable(mNotificationTime);
            }
        
            public Subscription setNotifications( Integer pNotifications ) {
            mNotifications = pNotifications;
            return this;
            }
            public Optional<Integer> getNotifications() {
              return  Optional.ofNullable(mNotifications);
            }
        

        public String getMessageName() {
        return "Subscription";
        }
    

        public JsonObject toJson() {
            JsonEncoder tEncoder = new JsonEncoder();
            this.encode( tEncoder );
            return tEncoder.toJson();
        }

        
        public void encode( JsonEncoder pEncoder) {

        
            JsonEncoder tEncoder = new JsonEncoder();
            pEncoder.add("Subscription", tEncoder.toJson() );
            //Encode Attribute: mMailAddr Type: String List: false
            tEncoder.add( "mailAddr", mMailAddr );
        
            //Encode Attribute: mZoneName Type: String List: false
            tEncoder.add( "zoneName", mZoneName );
        
            //Encode Attribute: mZoneId Type: int List: false
            tEncoder.add( "zoneId", mZoneId );
        
            //Encode Attribute: mCreateTime Type: String List: false
            tEncoder.add( "createTime", mCreateTime );
        
            //Encode Attribute: mNotificationTime Type: String List: false
            tEncoder.add( "notificationTime", mNotificationTime );
        
            //Encode Attribute: mNotifications Type: int List: false
            tEncoder.add( "notifications", mNotifications );
        
        }

        
        public void decode( JsonDecoder pDecoder) {

        
            JsonDecoder tDecoder = pDecoder.get("Subscription");
        
            //Decode Attribute: mMailAddr Type:String List: false
            mMailAddr = tDecoder.readString("mailAddr");
        
            //Decode Attribute: mZoneName Type:String List: false
            mZoneName = tDecoder.readString("zoneName");
        
            //Decode Attribute: mZoneId Type:int List: false
            mZoneId = tDecoder.readInteger("zoneId");
        
            //Decode Attribute: mCreateTime Type:String List: false
            mCreateTime = tDecoder.readString("createTime");
        
            //Decode Attribute: mNotificationTime Type:String List: false
            mNotificationTime = tDecoder.readString("notificationTime");
        
            //Decode Attribute: mNotifications Type:int List: false
            mNotifications = tDecoder.readInteger("notifications");
        

        }
    

        @Override
        public String toString() {
             Gson gsonPrinter = new GsonBuilder().setPrettyPrinting().create();
             return  gsonPrinter.toJson( this.toJson());
        }
    
        public Document getMongoDocument() {
            MongoEncoder tEncoder = new MongoEncoder();
            
            mongoEncode( tEncoder );
            return tEncoder.getDoc();
        }

     protected void mongoEncode(  MongoEncoder pEncoder ) {
        
                pEncoder.add("mailAddr",  mMailAddr );
                pEncoder.add("zoneName",  mZoneName );
                pEncoder.add("zoneId",  mZoneId );
                pEncoder.add("createTime",  mCreateTime );
                pEncoder.add("notificationTime",  mNotificationTime );
                pEncoder.add("notifications",  mNotifications );
    }
    
        public void decodeMongoDocument( Document pDoc ) {

            Document tDoc = null;
            List<Document> tDocLst = null;
            MongoDecoder tDecoder = new MongoDecoder( pDoc );

            
            ObjectId _tId = pDoc.get("_id", ObjectId.class);
            this.mMongoId = _tId.toString();
            
           mMailAddr = tDecoder.readString("mailAddr");
        
           mZoneName = tDecoder.readString("zoneName");
        
           mZoneId = tDecoder.readInteger("zoneId");
        
           mCreateTime = tDecoder.readString("createTime");
        
           mNotificationTime = tDecoder.readString("notificationTime");
        
           mNotifications = tDecoder.readInteger("notifications");
        
        } // End decodeMongoDocument
    

        public static  Builder getSubscriptionBuilder() {
            return new Subscription.Builder();
        }


        public static class  Builder {
          private Subscription mInstance;

          private Builder () {
            mInstance = new Subscription();
          }

        
                        public Builder setMailAddr( String pValue ) {
                        mInstance.setMailAddr( pValue );
                        return this;
                    }
                
                        public Builder setZoneName( String pValue ) {
                        mInstance.setZoneName( pValue );
                        return this;
                    }
                
                        public Builder setZoneId( Integer pValue ) {
                        mInstance.setZoneId( pValue );
                        return this;
                    }
                
                        public Builder setCreateTime( String pValue ) {
                        mInstance.setCreateTime( pValue );
                        return this;
                    }
                
                        public Builder setNotificationTime( String pValue ) {
                        mInstance.setNotificationTime( pValue );
                        return this;
                    }
                
                        public Builder setNotifications( Integer pValue ) {
                        mInstance.setNotifications( pValue );
                        return this;
                    }
                

        public Subscription build() {
            return mInstance;
        }

        }
    
            }
            
        /**
            Possible native attributes
            o "boolean" mapped to JSON "Boolean"
            o "byte" mapped to JSON "Integer"
            o "char" mapped to JSON "Integer"
            o "short" mapped to JSON "Integer"
            o "int" mapped to JSON "Integer"
            o "long" mapped to JSON "Integer"
            o "double" mapped to JSON "Numeric"
            o "String" mapped to JSON "String"
            o "byte[]" mapped to JSON "String" (Base64 string)


            An attribute could also be an "constant" i.e. having the property "constantGroup", should then refer to an defined /Constang/Group
             conastants are mapped to JSON strings,


            If the type is not any of the types below it will be refer to an other structure / object

        **/

    