
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
            public class User implements MessageInterface , MessageMongoInterface
            {
            
                    private String mMailAddr;
                    private String mPassword;
                    private String mLastLogin;
                    private Integer mLoginCounts;
               public User() {}

               public User(String pJsonString ) {
                    JsonDecoder tDecoder = new JsonDecoder( pJsonString );
                    this.decode( tDecoder );
               }
    
            public User setMailAddr( String pMailAddr ) {
            mMailAddr = pMailAddr;
            return this;
            }
            public Optional<String> getMailAddr() {
              return  Optional.ofNullable(mMailAddr);
            }
        
            public User setPassword( String pPassword ) {
            mPassword = pPassword;
            return this;
            }
            public Optional<String> getPassword() {
              return  Optional.ofNullable(mPassword);
            }
        
            public User setLastLogin( String pLastLogin ) {
            mLastLogin = pLastLogin;
            return this;
            }
            public Optional<String> getLastLogin() {
              return  Optional.ofNullable(mLastLogin);
            }
        
            public User setLoginCounts( Integer pLoginCounts ) {
            mLoginCounts = pLoginCounts;
            return this;
            }
            public Optional<Integer> getLoginCounts() {
              return  Optional.ofNullable(mLoginCounts);
            }
        

        public String getMessageName() {
        return "User";
        }
    

        public JsonObject toJson() {
            JsonEncoder tEncoder = new JsonEncoder();
            this.encode( tEncoder );
            return tEncoder.toJson();
        }

        
        public void encode( JsonEncoder pEncoder) {

        
            JsonEncoder tEncoder = new JsonEncoder();
            pEncoder.add("User", tEncoder.toJson() );
            //Encode Attribute: mMailAddr Type: String Array: false
            tEncoder.add( "mailAddr", mMailAddr );
        
            //Encode Attribute: mPassword Type: String Array: false
            tEncoder.add( "password", mPassword );
        
            //Encode Attribute: mLastLogin Type: String Array: false
            tEncoder.add( "lastLogin", mLastLogin );
        
            //Encode Attribute: mLoginCounts Type: int Array: false
            tEncoder.add( "loginCounts", mLoginCounts );
        
        }

        
        public void decode( JsonDecoder pDecoder) {

        
            JsonDecoder tDecoder = pDecoder.get("User");
        
            //Decode Attribute: mMailAddr Type:String Array: false
            mMailAddr = tDecoder.readString("mailAddr");
        
            //Decode Attribute: mPassword Type:String Array: false
            mPassword = tDecoder.readString("password");
        
            //Decode Attribute: mLastLogin Type:String Array: false
            mLastLogin = tDecoder.readString("lastLogin");
        
            //Decode Attribute: mLoginCounts Type:int Array: false
            mLoginCounts = tDecoder.readInteger("loginCounts");
        

        }
    

        @Override
        public String toString() {
             Gson gsonPrinter = new GsonBuilder().setPrettyPrinting().create();
             return  gsonPrinter.toJson( this.toJson());
        }
    
        public Document getMongoDocument() {
            MongoEncoder tEncoder = new MongoEncoder();
        
                tEncoder.add("mailAddr",  mMailAddr );
                tEncoder.add("password",  mPassword );
                tEncoder.add("lastLogin",  mLastLogin );
                tEncoder.add("loginCounts",  mLoginCounts );
            return tEncoder.getDoc();
    }
    
        public void decodeMongoDocument( Document pDoc ) {
            Document tDoc = null;
            List<Document> tDocLst = null;

            MongoDecoder tDecoder = new MongoDecoder( pDoc );
        
           mMailAddr = tDecoder.readString("mailAddr");
        
           mPassword = tDecoder.readString("password");
        
           mLastLogin = tDecoder.readString("lastLogin");
        
           mLoginCounts = tDecoder.readInteger("loginCounts");
        
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

    