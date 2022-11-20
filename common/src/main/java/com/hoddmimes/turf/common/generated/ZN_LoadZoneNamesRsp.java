
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
            public class ZN_LoadZoneNamesRsp implements MessageInterface 
            {
                public static String NAME = "ZN_LoadZoneNamesRsp";

            
                    private List<String> mRegionNames;
                    private List<ZN_ZoneNames> mRegions;
               public ZN_LoadZoneNamesRsp()
               {
                
               }

               public ZN_LoadZoneNamesRsp(String pJsonString ) {
                    
                    JsonDecoder tDecoder = new JsonDecoder( pJsonString );
                    this.decode( tDecoder );
               }
    
            public ZN_LoadZoneNamesRsp setRegionNames(List<String> pRegionNames ) {
            if ( pRegionNames == null) {
            mRegionNames = null;
            } else {
            mRegionNames = ListFactory.getList("[]");
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


            if ( mRegions == null)
            mRegions = ListFactory.getList("[]");


            mRegions .addAll( pRegions );
            return this;
            }


            public ZN_LoadZoneNamesRsp addRegions( List<ZN_ZoneNames> pRegions ) {

            if ( mRegions == null)
            mRegions = ListFactory.getList("[]");

            mRegions .addAll( pRegions );
            return this;
            }

            public ZN_LoadZoneNamesRsp addRegions( ZN_ZoneNames pRegions ) {

            if ( pRegions == null) {
            return this; // Not supporting null in vectors, well design issue
            }

            if ( mRegions == null) {
            mRegions = ListFactory.getList("[]");
            }

            mRegions.add( pRegions );
            return this;
            }


            public Optional<List<ZN_ZoneNames>> getRegions() {

            if (mRegions == null) {
                return  Optional.ofNullable(null);
            }

             //List<ZN_ZoneNames> tList = ListFactory.getList("[]");
             //tList.addAll( mRegions );
             // return  Optional.ofNullable(tList);
             return Optional.ofNullable(mRegions);
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
            //Encode Attribute: mRegionNames Type: String List: true
            tEncoder.addStringArray("regionNames", mRegionNames );
        
            //Encode Attribute: mRegions Type: ZN_ZoneNames List: true
            tEncoder.addMessageArray("regions", mRegions );
        
        }

        
        public void decode( JsonDecoder pDecoder) {

        
            JsonDecoder tDecoder = pDecoder.get("ZN_LoadZoneNamesRsp");
        
            //Decode Attribute: mRegionNames Type:String List: true
            mRegionNames = tDecoder.readStringArray("regionNames", "[]");
        
            //Decode Attribute: mRegions Type:ZN_ZoneNames List: true
            mRegions = (List<ZN_ZoneNames>) tDecoder.readMessageArray( "regions", "[]", ZN_ZoneNames.class );
        

        }
    

        @Override
        public String toString() {
             Gson gsonPrinter = new GsonBuilder().setPrettyPrinting().create();
             return  gsonPrinter.toJson( this.toJson());
        }
    

        public static  Builder getZN_LoadZoneNamesRspBuilder() {
            return new ZN_LoadZoneNamesRsp.Builder();
        }


        public static class  Builder {
          private ZN_LoadZoneNamesRsp mInstance;

          private Builder () {
            mInstance = new ZN_LoadZoneNamesRsp();
          }

        
                        public Builder setRegionNames( List<String> pValue ) {
                        mInstance.setRegionNames( pValue );
                        return this;
                    }
                
                    public Builder setRegions( List<ZN_ZoneNames> pValue )  {
                        mInstance.setRegions( pValue );
                        return this;
                    }
                

        public ZN_LoadZoneNamesRsp build() {
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

    