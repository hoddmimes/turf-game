
            package com.hoddmimes.turf.common.generated;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.io.IOException;





import com.hoddmimes.jsontransform.MessageInterface;
import com.hoddmimes.jsontransform.JsonDecoder;
import com.hoddmimes.jsontransform.JsonEncoder;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

            

            @SuppressWarnings({"WeakerAccess","unused","unchecked"})
            public class ZN_SubscriptionData implements MessageInterface 
            {
            
                    private String mZoneName;
                    private String mSubscrTime;
                    private String mNotificationTime;
                    private Integer mNotifications;
                    private String mSubscrId;
               public ZN_SubscriptionData() {}

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
            //Encode Attribute: mZoneName Type: String Array: false
            tEncoder.add( "zoneName", mZoneName );
        
            //Encode Attribute: mSubscrTime Type: String Array: false
            tEncoder.add( "subscrTime", mSubscrTime );
        
            //Encode Attribute: mNotificationTime Type: String Array: false
            tEncoder.add( "notificationTime", mNotificationTime );
        
            //Encode Attribute: mNotifications Type: int Array: false
            tEncoder.add( "notifications", mNotifications );
        
            //Encode Attribute: mSubscrId Type: String Array: false
            tEncoder.add( "subscrId", mSubscrId );
        
        }

        
        public void decode( JsonDecoder pDecoder) {

        
            JsonDecoder tDecoder = pDecoder;
        
            //Decode Attribute: mZoneName Type:String Array: false
            mZoneName = tDecoder.readString("zoneName");
        
            //Decode Attribute: mSubscrTime Type:String Array: false
            mSubscrTime = tDecoder.readString("subscrTime");
        
            //Decode Attribute: mNotificationTime Type:String Array: false
            mNotificationTime = tDecoder.readString("notificationTime");
        
            //Decode Attribute: mNotifications Type:int Array: false
            mNotifications = tDecoder.readInteger("notifications");
        
            //Decode Attribute: mSubscrId Type:String Array: false
            mSubscrId = tDecoder.readString("subscrId");
        

        }
    

        @Override
        public String toString() {
             Gson gsonPrinter = new GsonBuilder().setPrettyPrinting().create();
             return  gsonPrinter.toJson( this.toJson());
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

    