
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
            public class ZN_LoadZoneSubscriptionsRsp implements MessageInterface 
            {
            
                    private List<ZN_SubscriptionData> mSubscriptions;
               public ZN_LoadZoneSubscriptionsRsp() {}

               public ZN_LoadZoneSubscriptionsRsp(String pJsonString ) {
                    JsonDecoder tDecoder = new JsonDecoder( pJsonString );
                    this.decode( tDecoder );
               }
    
            public ZN_LoadZoneSubscriptionsRsp setSubscriptions( List<ZN_SubscriptionData> pSubscriptions ) {
              if (pSubscriptions == null) {
                mSubscriptions = null;
                return this;
              }

            int tSize = pSubscriptions.size();

            if ( mSubscriptions == null)
            mSubscriptions = new ArrayList<>( tSize + 1 );


            mSubscriptions .addAll( pSubscriptions );
            return this;
            }


            public ZN_LoadZoneSubscriptionsRsp addSubscriptions( List<ZN_SubscriptionData> pSubscriptions ) {

            if ( mSubscriptions == null)
            mSubscriptions = new ArrayList<>();

            mSubscriptions .addAll( pSubscriptions );
            return this;
            }

            public ZN_LoadZoneSubscriptionsRsp addSubscriptions( ZN_SubscriptionData pSubscriptions ) {

            if ( pSubscriptions == null) {
            return this; // Not supporting null in vectors, well design issue
            }

            if ( mSubscriptions == null) {
            mSubscriptions = new ArrayList<>();
            }

            mSubscriptions.add( pSubscriptions );
            return this;
            }


            public Optional<List<ZN_SubscriptionData>> getSubscriptions() {

            if (mSubscriptions == null) {
                return  Optional.ofNullable(null);
            }

            List<ZN_SubscriptionData> tList = new ArrayList<>( mSubscriptions.size() );
            tList.addAll( mSubscriptions );
            return  Optional.ofNullable(tList);
            }

        

        public String getMessageName() {
        return "ZN_LoadZoneSubscriptionsRsp";
        }
    

        public JsonObject toJson() {
            JsonEncoder tEncoder = new JsonEncoder();
            this.encode( tEncoder );
            return tEncoder.toJson();
        }

        
        public void encode( JsonEncoder pEncoder) {

        
            JsonEncoder tEncoder = new JsonEncoder();
            pEncoder.add("ZN_LoadZoneSubscriptionsRsp", tEncoder.toJson() );
            //Encode Attribute: mSubscriptions Type: ZN_SubscriptionData Array: true
            tEncoder.addMessageArray("subscriptions", mSubscriptions );
        
        }

        
        public void decode( JsonDecoder pDecoder) {

        
            JsonDecoder tDecoder = pDecoder.get("ZN_LoadZoneSubscriptionsRsp");
        
            //Decode Attribute: mSubscriptions Type:ZN_SubscriptionData Array: true
            mSubscriptions = (List<ZN_SubscriptionData>) tDecoder.readMessageArray( "subscriptions", ZN_SubscriptionData.class );
        

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

    