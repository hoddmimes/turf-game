
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
package com.hoddmimes.turf.common.generated;

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





import com.hoddmimes.jsontransform.MessageInterface;
import com.hoddmimes.jsontransform.JsonDecoder;
import com.hoddmimes.jsontransform.JsonEncoder;
import com.hoddmimes.jsontransform.ListFactory;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



            

            @SuppressWarnings({"WeakerAccess","unused","unchecked"})
            public class ZN_MailConfirmation implements MessageInterface 
            {
                public static String NAME = "ZN_MailConfirmation";

            
                    private String mConfirmationId;
                    private String mMailAddress;
               public ZN_MailConfirmation()
               {
                
               }

               public ZN_MailConfirmation(String pJsonString ) {
                    
                    JsonDecoder tDecoder = new JsonDecoder( pJsonString );
                    this.decode( tDecoder );
               }
    
            public ZN_MailConfirmation setConfirmationId( String pConfirmationId ) {
            mConfirmationId = pConfirmationId;
            return this;
            }
            public Optional<String> getConfirmationId() {
              return  Optional.ofNullable(mConfirmationId);
            }
        
            public ZN_MailConfirmation setMailAddress( String pMailAddress ) {
            mMailAddress = pMailAddress;
            return this;
            }
            public Optional<String> getMailAddress() {
              return  Optional.ofNullable(mMailAddress);
            }
        
        public String getMessageName() {
        return "ZN_MailConfirmation";
        }
    

        public JsonObject toJson() {
            JsonEncoder tEncoder = new JsonEncoder();
            this.encode( tEncoder );
            return tEncoder.toJson();
        }

        
        public void encode( JsonEncoder pEncoder) {

        
            JsonEncoder tEncoder = new JsonEncoder();
            pEncoder.add("ZN_MailConfirmation", tEncoder.toJson() );
            //Encode Attribute: mConfirmationId Type: String List: false
            tEncoder.add( "confirmationId", mConfirmationId );
        
            //Encode Attribute: mMailAddress Type: String List: false
            tEncoder.add( "mailAddress", mMailAddress );
        
        }

        
        public void decode( JsonDecoder pDecoder) {

        
            JsonDecoder tDecoder = pDecoder.get("ZN_MailConfirmation");
        
            //Decode Attribute: mConfirmationId Type:String List: false
            mConfirmationId = tDecoder.readString("confirmationId");
        
            //Decode Attribute: mMailAddress Type:String List: false
            mMailAddress = tDecoder.readString("mailAddress");
        

        }
    

        @Override
        public String toString() {
             Gson gsonPrinter = new GsonBuilder().setPrettyPrinting().create();
             return  gsonPrinter.toJson( this.toJson());
        }
    

        public static  Builder getZN_MailConfirmationBuilder() {
            return new ZN_MailConfirmation.Builder();
        }


        public static class  Builder {
          private ZN_MailConfirmation mInstance;

          private Builder () {
            mInstance = new ZN_MailConfirmation();
          }

        
                        public Builder setConfirmationId( String pValue ) {
                        mInstance.setConfirmationId( pValue );
                        return this;
                    }
                
                        public Builder setMailAddress( String pValue ) {
                        mInstance.setMailAddress( pValue );
                        return this;
                    }
                

        public ZN_MailConfirmation build() {
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

    