
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
            public class ZN_UpdatePasswordRqst implements MessageInterface 
            {
            
                    private String mMailAddress;
                    private String mPassword;
                    private String mConfirmationId;
               public ZN_UpdatePasswordRqst() {}

               public ZN_UpdatePasswordRqst(String pJsonString ) {
                    JsonDecoder tDecoder = new JsonDecoder( pJsonString );
                    this.decode( tDecoder );
               }
    
            public ZN_UpdatePasswordRqst setMailAddress( String pMailAddress ) {
            mMailAddress = pMailAddress;
            return this;
            }
            public Optional<String> getMailAddress() {
              return  Optional.ofNullable(mMailAddress);
            }
        
            public ZN_UpdatePasswordRqst setPassword( String pPassword ) {
            mPassword = pPassword;
            return this;
            }
            public Optional<String> getPassword() {
              return  Optional.ofNullable(mPassword);
            }
        
            public ZN_UpdatePasswordRqst setConfirmationId( String pConfirmationId ) {
            mConfirmationId = pConfirmationId;
            return this;
            }
            public Optional<String> getConfirmationId() {
              return  Optional.ofNullable(mConfirmationId);
            }
        

        public String getMessageName() {
        return "ZN_UpdatePasswordRqst";
        }
    

        public JsonObject toJson() {
            JsonEncoder tEncoder = new JsonEncoder();
            this.encode( tEncoder );
            return tEncoder.toJson();
        }

        
        public void encode( JsonEncoder pEncoder) {

        
            JsonEncoder tEncoder = new JsonEncoder();
            pEncoder.add("ZN_UpdatePasswordRqst", tEncoder.toJson() );
            //Encode Attribute: mMailAddress Type: String Array: false
            tEncoder.add( "mailAddress", mMailAddress );
        
            //Encode Attribute: mPassword Type: String Array: false
            tEncoder.add( "password", mPassword );
        
            //Encode Attribute: mConfirmationId Type: String Array: false
            tEncoder.add( "confirmationId", mConfirmationId );
        
        }

        
        public void decode( JsonDecoder pDecoder) {

        
            JsonDecoder tDecoder = pDecoder.get("ZN_UpdatePasswordRqst");
        
            //Decode Attribute: mMailAddress Type:String Array: false
            mMailAddress = tDecoder.readString("mailAddress");
        
            //Decode Attribute: mPassword Type:String Array: false
            mPassword = tDecoder.readString("password");
        
            //Decode Attribute: mConfirmationId Type:String Array: false
            mConfirmationId = tDecoder.readString("confirmationId");
        

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

    