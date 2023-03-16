
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
            public class ZN_SubscriptionData implements MessageInterface 
            {
                public static String NAME = "ZN_SubscriptionData";

            
                    private String mZoneName;
                    private String mSubscrTime;
                    private String mNotificationTime;
                    private Integer mNotifications;
                    private String mSubscrId;
               public ZN_SubscriptionData()
               {
                
               }

               public ZN_SubscriptionData(String pJsonString ) {
                    
                    JsonDecoder tDecoder = new JsonDecoder( pJsonString );
                    this.decode( tDecoder );
               }
    
            public ZN_SubscriptionData setZoneName( String pZoneName ) {
            mZoneName = pZoneName;
            return this;
            }
            public Optional<String> getZoneName() {
              return  Optional.ofNullable(mZoneName);
            }
        
            public ZN_SubscriptionData setSubscrTime( String pSubscrTime ) {
            mSubscrTime = pSubscrTime;
            return this;
            }
            public Optional<String> getSubscrTime() {
              return  Optional.ofNullable(mSubscrTime);
            }
        
            public ZN_SubscriptionData setNotificationTime( String pNotificationTime ) {
            mNotificationTime = pNotificationTime;
            return this;
            }
            public Optional<String> getNotificationTime() {
              return  Optional.ofNullable(mNotificationTime);
            }
        
            public ZN_SubscriptionData setNotifications( Integer pNotifications ) {
            mNotifications = pNotifications;
            return this;
            }
            public Optional<Integer> getNotifications() {
              return  Optional.ofNullable(mNotifications);
            }
        
            public ZN_SubscriptionData setSubscrId( String pSubscrId ) {
            mSubscrId = pSubscrId;
            return this;
            }
            public Optional<String> getSubscrId() {
              return  Optional.ofNullable(mSubscrId);
            }
        
        public String getMessageName() {
        return "ZN_SubscriptionData";
        }
    

        public JsonObject toJson() {
            JsonEncoder tEncoder = new JsonEncoder();
            this.encode( tEncoder );
            return tEncoder.toJson();
        }

        
        public void encode( JsonEncoder pEncoder) {

        
            JsonEncoder tEncoder = pEncoder;
            //Encode Attribute: mZoneName Type: String List: false
            tEncoder.add( "zoneName", mZoneName );
        
            //Encode Attribute: mSubscrTime Type: String List: false
            tEncoder.add( "subscrTime", mSubscrTime );
        
            //Encode Attribute: mNotificationTime Type: String List: false
            tEncoder.add( "notificationTime", mNotificationTime );
        
            //Encode Attribute: mNotifications Type: int List: false
            tEncoder.add( "notifications", mNotifications );
        
            //Encode Attribute: mSubscrId Type: String List: false
            tEncoder.add( "subscrId", mSubscrId );
        
        }

        
        public void decode( JsonDecoder pDecoder) {

        
            JsonDecoder tDecoder = pDecoder;
        
            //Decode Attribute: mZoneName Type:String List: false
            mZoneName = tDecoder.readString("zoneName");
        
            //Decode Attribute: mSubscrTime Type:String List: false
            mSubscrTime = tDecoder.readString("subscrTime");
        
            //Decode Attribute: mNotificationTime Type:String List: false
            mNotificationTime = tDecoder.readString("notificationTime");
        
            //Decode Attribute: mNotifications Type:int List: false
            mNotifications = tDecoder.readInteger("notifications");
        
            //Decode Attribute: mSubscrId Type:String List: false
            mSubscrId = tDecoder.readString("subscrId");
        

        }
    

        @Override
        public String toString() {
             Gson gsonPrinter = new GsonBuilder().setPrettyPrinting().create();
             return  gsonPrinter.toJson( this.toJson());
        }
    

        public static  Builder getZN_SubscriptionDataBuilder() {
            return new ZN_SubscriptionData.Builder();
        }


        public static class  Builder {
          private ZN_SubscriptionData mInstance;

          private Builder () {
            mInstance = new ZN_SubscriptionData();
          }

        
                        public Builder setZoneName( String pValue ) {
                        mInstance.setZoneName( pValue );
                        return this;
                    }
                
                        public Builder setSubscrTime( String pValue ) {
                        mInstance.setSubscrTime( pValue );
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
                
                        public Builder setSubscrId( String pValue ) {
                        mInstance.setSubscrId( pValue );
                        return this;
                    }
                

        public ZN_SubscriptionData build() {
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

    