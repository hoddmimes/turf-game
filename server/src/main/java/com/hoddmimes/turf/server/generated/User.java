
            package com.hoddmimes.turf.server.generated;

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
    import org.bson.types.ObjectId;
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
            
                private String mMongoId = null;
                    private String mMailAddr;
                    private String mPassword;
                    private String mLastLogin;
                    private Integer mLoginCounts;
                    private Boolean mConfirmed;
                    private String mConfirmationId;
               public User() {}

               public User(String pJsonString ) {
                    JsonDecoder tDecoder = new JsonDecoder( pJsonString );
                    this.decode( tDecoder );
               }
    
            public String getMongoId() {
            return this.mMongoId;
            }

            public void setMongoId( String pMongoId ) {
            this.mMongoId = pMongoId;
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
        
            public User setConfirmed( Boolean pConfirmed ) {
            mConfirmed = pConfirmed;
            return this;
            }
            public Optional<Boolean> getConfirmed() {
              return  Optional.ofNullable(mConfirmed);
            }
        
            public User setConfirmationId( String pConfirmationId ) {
            mConfirmationId = pConfirmationId;
            return this;
            }
            public Optional<String> getConfirmationId() {
              return  Optional.ofNullable(mConfirmationId);
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
        
            //Encode Attribute: mConfirmed Type: boolean Array: false
            tEncoder.add( "confirmed", mConfirmed );
        
            //Encode Attribute: mConfirmationId Type: String Array: false
            tEncoder.add( "confirmationId", mConfirmationId );
        
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
        
            //Decode Attribute: mConfirmed Type:boolean Array: false
            mConfirmed = tDecoder.readBoolean("confirmed");
        
            //Decode Attribute: mConfirmationId Type:String Array: false
            mConfirmationId = tDecoder.readString("confirmationId");
        

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
                tEncoder.add("confirmed",  mConfirmed );
                tEncoder.add("confirmationId",  mConfirmationId );
            return tEncoder.getDoc();
    }
    
        public void decodeMongoDocument( Document pDoc ) {
            Document tDoc = null;
            List<Document> tDocLst = null;


            MongoDecoder tDecoder = new MongoDecoder( pDoc );

            
            ObjectId _tId = pDoc.get("_id", ObjectId.class);
            this.mMongoId = _tId.toString();
            
           mMailAddr = tDecoder.readString("mailAddr");
        
           mPassword = tDecoder.readString("password");
        
           mLastLogin = tDecoder.readString("lastLogin");
        
           mLoginCounts = tDecoder.readInteger("loginCounts");
        
           mConfirmed = tDecoder.readBoolean("confirmed");
        
           mConfirmationId = tDecoder.readString("confirmationId");
        
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

    