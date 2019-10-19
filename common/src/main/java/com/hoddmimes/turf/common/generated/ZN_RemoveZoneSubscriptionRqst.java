
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
            public class ZN_RemoveZoneSubscriptionRqst implements MessageInterface 
            {
            
                    private String mSubscrId;
               public ZN_RemoveZoneSubscriptionRqst() {}

               public ZN_RemoveZoneSubscriptionRqst(String pJsonString ) {
                    JsonDecoder tDecoder = new JsonDecoder( pJsonString );
                    this.decode( tDecoder );
               }
    
            public ZN_RemoveZoneSubscriptionRqst setSubscrId( String pSubscrId ) {
            mSubscrId = pSubscrId;
            return this;
            }
            public Optional<String> getSubscrId() {
              return  Optional.ofNullable(mSubscrId);
            }
        

        public String getMessageName() {
        return "ZN_RemoveZoneSubscriptionRqst";
        }
    

        public JsonObject toJson() {
            JsonEncoder tEncoder = new JsonEncoder();
            this.encode( tEncoder );
            return tEncoder.toJson();
        }

        
        public void encode( JsonEncoder pEncoder) {

        
            JsonEncoder tEncoder = new JsonEncoder();
            pEncoder.add("ZN_RemoveZoneSubscriptionRqst", tEncoder.toJson() );
            //Encode Attribute: mSubscrId Type: String Array: false
            tEncoder.add( "subscrId", mSubscrId );
        
        }

        
        public void decode( JsonDecoder pDecoder) {

        
            JsonDecoder tDecoder = pDecoder.get("ZN_RemoveZoneSubscriptionRqst");
        
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

    