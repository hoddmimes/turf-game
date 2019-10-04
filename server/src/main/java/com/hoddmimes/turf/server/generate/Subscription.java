
            package com.hoddmimes.turf.server.generate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.io.IOException;




    import org.bson.BsonType;
    import org.bson.Document;
    import org.bson.conversions.Bson;
    import com.mongodb.BasicDBObject;
    import com.hoddmimes.jsontransform.MessageMongoInterface;
    import com.hoddmimes.jsontransform.MongoDecoder;
    import com.hoddmimes.jsontransform.MongoEncoder;


import com.hoddmimes.jsontransform.MessageInterface;
import com.hoddmimes.jsontransform.JsonDecoder;
import com.hoddmimes.jsontransform.JsonEncoder;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

            

            @SuppressWarnings({"WeakerAccess","unused","unchecked"})
            public class Subscription implements MessageInterface , MessageMongoInterface
            {
            
                    private String mMailAddr;
                    private String mZoneName;
                    private Integer mZoneId;
               public Subscription() {}

               public Subscription(String pJsonString ) {
                    JsonDecoder tDecoder = new JsonDecoder( pJsonString );
                    this.decode( tDecoder );
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
            //Encode Attribute: mMailAddr Type: String Array: false
            tEncoder.add( "mailAddr", mMailAddr );
        
            //Encode Attribute: mZoneName Type: String Array: false
            tEncoder.add( "zoneName", mZoneName );
        
            //Encode Attribute: mZoneId Type: int Array: false
            tEncoder.add( "zoneId", mZoneId );
        
        }

        
        public void decode( JsonDecoder pDecoder) {

        
            JsonDecoder tDecoder = pDecoder.get("Subscription");
        
            //Decode Attribute: mMailAddr Type:String Array: false
            mMailAddr = tDecoder.readString("mailAddr");
        
            //Decode Attribute: mZoneName Type:String Array: false
            mZoneName = tDecoder.readString("zoneName");
        
            //Decode Attribute: mZoneId Type:int Array: false
            mZoneId = tDecoder.readInteger("zoneId");
        

        }
    

        @Override
        public String toString() {
             Gson gsonPrinter = new GsonBuilder().setPrettyPrinting().create();
             return  gsonPrinter.toJson( this.toJson());
        }
    
        public Document getMongoDocument() {
            MongoEncoder tEncoder = new MongoEncoder();
        
                tEncoder.add("mailAddr",  mMailAddr );
                tEncoder.add("zoneName",  mZoneName );
                tEncoder.add("zoneId",  mZoneId );
            return tEncoder.getDoc();
    }
    
        public void decodeMongoDocument( Document pDoc ) {
            Document tDoc = null;
            List<Document> tDocLst = null;

            MongoDecoder tDecoder = new MongoDecoder( pDoc );
        
           mMailAddr = tDecoder.readString("mailAddr");
        
           mZoneName = tDecoder.readString("zoneName");
        
           mZoneId = tDecoder.readInteger("zoneId");
        
        } // End decodeMongoDocument
    
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

    