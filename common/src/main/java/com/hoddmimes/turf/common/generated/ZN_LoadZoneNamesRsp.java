
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
            public class ZN_LoadZoneNamesRsp implements MessageInterface 
            {
            
                    private List<String> mRegionNames;
                    private List<ZN_ZoneNames> mRegions;
               public ZN_LoadZoneNamesRsp() {}

               public ZN_LoadZoneNamesRsp(String pJsonString ) {
                    JsonDecoder tDecoder = new JsonDecoder( pJsonString );
                    this.decode( tDecoder );
               }
    
            public ZN_LoadZoneNamesRsp setRegionNames(List<String> pRegionNames ) {
            if ( pRegionNames == null) {
            mRegionNames = null;
            } else {
            mRegionNames = new ArrayList<>( pRegionNames.size() );
            mRegionNames.addAll( pRegionNames );
            }
            return this;
            }

            public Optional<List<String>> getRegionNames()
            {
            return  Optional.ofNullable(mRegionNames);
            }
        
            public ZN_LoadZoneNamesRsp setRegions( List<ZN_ZoneNames> pRegions ) {
              if (pRegions == null) {
                mRegions = null;
                return this;
              }

            int tSize = pRegions.size();

            if ( mRegions == null)
            mRegions = new ArrayList<>( tSize + 1 );


            mRegions .addAll( pRegions );
            return this;
            }


            public ZN_LoadZoneNamesRsp addRegions( List<ZN_ZoneNames> pRegions ) {

            if ( mRegions == null)
            mRegions = new ArrayList<>();

            mRegions .addAll( pRegions );
            return this;
            }

            public ZN_LoadZoneNamesRsp addRegions( ZN_ZoneNames pRegions ) {

            if ( pRegions == null) {
            return this; // Not supporting null in vectors, well design issue
            }

            if ( mRegions == null) {
            mRegions = new ArrayList<>();
            }

            mRegions.add( pRegions );
            return this;
            }


            public Optional<List<ZN_ZoneNames>> getRegions() {

            if (mRegions == null) {
                return  Optional.ofNullable(null);
            }

            List<ZN_ZoneNames> tList = new ArrayList<>( mRegions.size() );
            tList.addAll( mRegions );
            return  Optional.ofNullable(tList);
            }

        

        public String getMessageName() {
        return "ZN_LoadZoneNamesRsp";
        }
    

        public JsonObject toJson() {
            JsonEncoder tEncoder = new JsonEncoder();
            this.encode( tEncoder );
            return tEncoder.toJson();
        }

        
        public void encode( JsonEncoder pEncoder) {

        
            JsonEncoder tEncoder = new JsonEncoder();
            pEncoder.add("ZN_LoadZoneNamesRsp", tEncoder.toJson() );
            //Encode Attribute: mRegionNames Type: String Array: true
            tEncoder.addStringArray("regionNames", mRegionNames );
        
            //Encode Attribute: mRegions Type: ZN_ZoneNames Array: true
            tEncoder.addMessageArray("regions", mRegions );
        
        }

        
        public void decode( JsonDecoder pDecoder) {

        
            JsonDecoder tDecoder = pDecoder.get("ZN_LoadZoneNamesRsp");
        
            //Decode Attribute: mRegionNames Type:String Array: true
            mRegionNames = tDecoder.readStringArray("regionNames");
        
            //Decode Attribute: mRegions Type:ZN_ZoneNames Array: true
            mRegions = (List<ZN_ZoneNames>) tDecoder.readMessageArray( "regions", ZN_ZoneNames.class );
        

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

    