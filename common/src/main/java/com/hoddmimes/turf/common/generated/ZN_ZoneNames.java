
            package com.hoddmimes.turf.common.generated;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.List;
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
            public class ZN_ZoneNames implements MessageInterface 
            {
            
                    private String mRegion;
                    private List<String> mNames;
               public ZN_ZoneNames() {}

               public ZN_ZoneNames(String pJsonString ) {
                    JsonDecoder tDecoder = new JsonDecoder( pJsonString );
                    this.decode( tDecoder );
               }
    
            public ZN_ZoneNames setRegion( String pRegion ) {
            mRegion = pRegion;
            return this;
            }
            public Optional<String> getRegion() {
              return  Optional.ofNullable(mRegion);
            }
        
            public ZN_ZoneNames setNames(List<String> pNames ) {
            if ( pNames == null) {
            mNames = null;
            } else {
            mNames = ListFactory.getList("[]");
            mNames.addAll( pNames );
            }
            return this;
            }

            public Optional<List<String>> getNames()
            {
            return  Optional.ofNullable(mNames);
            }
        

        public String getMessageName() {
        return "ZN_ZoneNames";
        }
    

        public JsonObject toJson() {
            JsonEncoder tEncoder = new JsonEncoder();
            this.encode( tEncoder );
            return tEncoder.toJson();
        }

        
        public void encode( JsonEncoder pEncoder) {

        
            JsonEncoder tEncoder = new JsonEncoder();
            pEncoder.add("ZN_ZoneNames", tEncoder.toJson() );
            //Encode Attribute: mRegion Type: String List: false
            tEncoder.add( "region", mRegion );
        
            //Encode Attribute: mNames Type: String List: true
            tEncoder.addStringArray("names", mNames );
        
        }

        
        public void decode( JsonDecoder pDecoder) {

        
            JsonDecoder tDecoder = pDecoder.get("ZN_ZoneNames");
        
            //Decode Attribute: mRegion Type:String List: false
            mRegion = tDecoder.readString("region");
        
            //Decode Attribute: mNames Type:String List: true
            mNames = tDecoder.readStringArray("names", "[]");
        

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

    