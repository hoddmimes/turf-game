
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
            public class TG_Response implements MessageInterface 
            {
                public static String NAME = "TG_Response";

            
                    private Boolean mSuccess;
                    private String mReason;
                    private String mOptionPage;
               public TG_Response()
               {
                
               }

               public TG_Response(String pJsonString ) {
                    
                    JsonDecoder tDecoder = new JsonDecoder( pJsonString );
                    this.decode( tDecoder );
               }
    
            public TG_Response setSuccess( Boolean pSuccess ) {
            mSuccess = pSuccess;
            return this;
            }
            public Optional<Boolean> getSuccess() {
              return  Optional.ofNullable(mSuccess);
            }
        
            public TG_Response setReason( String pReason ) {
            mReason = pReason;
            return this;
            }
            public Optional<String> getReason() {
              return  Optional.ofNullable(mReason);
            }
        
            public TG_Response setOptionPage( String pOptionPage ) {
            mOptionPage = pOptionPage;
            return this;
            }
            public Optional<String> getOptionPage() {
              return  Optional.ofNullable(mOptionPage);
            }
        
        public String getMessageName() {
        return "TG_Response";
        }
    

        public JsonObject toJson() {
            JsonEncoder tEncoder = new JsonEncoder();
            this.encode( tEncoder );
            return tEncoder.toJson();
        }

        
        public void encode( JsonEncoder pEncoder) {

        
            JsonEncoder tEncoder = new JsonEncoder();
            pEncoder.add("TG_Response", tEncoder.toJson() );
            //Encode Attribute: mSuccess Type: boolean List: false
            tEncoder.add( "success", mSuccess );
        
            //Encode Attribute: mReason Type: String List: false
            tEncoder.add( "reason", mReason );
        
            //Encode Attribute: mOptionPage Type: String List: false
            tEncoder.add( "optionPage", mOptionPage );
        
        }

        
        public void decode( JsonDecoder pDecoder) {

        
            JsonDecoder tDecoder = pDecoder.get("TG_Response");
        
            //Decode Attribute: mSuccess Type:boolean List: false
            mSuccess = tDecoder.readBoolean("success");
        
            //Decode Attribute: mReason Type:String List: false
            mReason = tDecoder.readString("reason");
        
            //Decode Attribute: mOptionPage Type:String List: false
            mOptionPage = tDecoder.readString("optionPage");
        

        }
    

        @Override
        public String toString() {
             Gson gsonPrinter = new GsonBuilder().setPrettyPrinting().create();
             return  gsonPrinter.toJson( this.toJson());
        }
    

        public static  Builder getTG_ResponseBuilder() {
            return new TG_Response.Builder();
        }


        public static class  Builder {
          private TG_Response mInstance;

          private Builder () {
            mInstance = new TG_Response();
          }

        
                        public Builder setSuccess( Boolean pValue ) {
                        mInstance.setSuccess( pValue );
                        return this;
                    }
                
                        public Builder setReason( String pValue ) {
                        mInstance.setReason( pValue );
                        return this;
                    }
                
                        public Builder setOptionPage( String pValue ) {
                        mInstance.setOptionPage( pValue );
                        return this;
                    }
                

        public TG_Response build() {
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

    