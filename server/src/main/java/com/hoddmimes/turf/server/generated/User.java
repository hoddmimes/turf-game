
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
            public class User implements MessageInterface , MessageMongoInterface
            {
                public static String NAME = "User";

            
                private String mMongoId = null;
                    private String mMailAddr;
                    private String mPassword;
                    private String mLastLogin;
                    private Integer mLoginCounts;
                    private Boolean mConfirmed;
                    private String mConfirmationId;
               public User()
               {
                
               }

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
            //Encode Attribute: mMailAddr Type: String List: false
            tEncoder.add( "mailAddr", mMailAddr );
        
            //Encode Attribute: mPassword Type: String List: false
            tEncoder.add( "password", mPassword );
        
            //Encode Attribute: mLastLogin Type: String List: false
            tEncoder.add( "lastLogin", mLastLogin );
        
            //Encode Attribute: mLoginCounts Type: int List: false
            tEncoder.add( "loginCounts", mLoginCounts );
        
            //Encode Attribute: mConfirmed Type: boolean List: false
            tEncoder.add( "confirmed", mConfirmed );
        
            //Encode Attribute: mConfirmationId Type: String List: false
            tEncoder.add( "confirmationId", mConfirmationId );
        
        }

        
        public void decode( JsonDecoder pDecoder) {

        
            JsonDecoder tDecoder = pDecoder.get("User");
        
            //Decode Attribute: mMailAddr Type:String List: false
            mMailAddr = tDecoder.readString("mailAddr");
        
            //Decode Attribute: mPassword Type:String List: false
            mPassword = tDecoder.readString("password");
        
            //Decode Attribute: mLastLogin Type:String List: false
            mLastLogin = tDecoder.readString("lastLogin");
        
            //Decode Attribute: mLoginCounts Type:int List: false
            mLoginCounts = tDecoder.readInteger("loginCounts");
        
            //Decode Attribute: mConfirmed Type:boolean List: false
            mConfirmed = tDecoder.readBoolean("confirmed");
        
            //Decode Attribute: mConfirmationId Type:String List: false
            mConfirmationId = tDecoder.readString("confirmationId");
        

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
                pEncoder.add("password",  mPassword );
                pEncoder.add("lastLogin",  mLastLogin );
                pEncoder.add("loginCounts",  mLoginCounts );
                pEncoder.add("confirmed",  mConfirmed );
                pEncoder.add("confirmationId",  mConfirmationId );
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
    

        public static  Builder getUserBuilder() {
            return new User.Builder();
        }


        public static class  Builder {
          private User mInstance;

          private Builder () {
            mInstance = new User();
          }

        
                        public Builder setMailAddr( String pValue ) {
                        mInstance.setMailAddr( pValue );
                        return this;
                    }
                
                        public Builder setPassword( String pValue ) {
                        mInstance.setPassword( pValue );
                        return this;
                    }
                
                        public Builder setLastLogin( String pValue ) {
                        mInstance.setLastLogin( pValue );
                        return this;
                    }
                
                        public Builder setLoginCounts( Integer pValue ) {
                        mInstance.setLoginCounts( pValue );
                        return this;
                    }
                
                        public Builder setConfirmed( Boolean pValue ) {
                        mInstance.setConfirmed( pValue );
                        return this;
                    }
                
                        public Builder setConfirmationId( String pValue ) {
                        mInstance.setConfirmationId( pValue );
                        return this;
                    }
                

        public User build() {
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

    