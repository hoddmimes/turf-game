package com.hoddmimes.tomcat;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;





public class TestClient {

    private static String DEFAULT_JSON_RQST = "{\"ZN_RegisterUserRqst\" : {\"mailAddress\" : \"joshua@falken.com\",\"zoneName\" : \"vinghästvägen\"}}";
    private String SERVICE_URL = "http://localhost:8282/turf/app/test";

    String mJsonRqstStr;


    public static void main(String[] args) {
        TestClient tc = new TestClient();
        tc.parseArguments( args );
        tc.test();
    }

    private void parseArguments( String args[] ) {
        int i = 0;

        while (i < args.length) {
            if (args[i].compareToIgnoreCase("-file") == 0) {
                mJsonRqstStr = readFile(args[i + 1]);
                i++;
            }
            if (args[i].compareToIgnoreCase("-url") == 0) {
                SERVICE_URL = args[i + 1];
                i++;
            }
            i++;
        }

        if (mJsonRqstStr == null) {
            mJsonRqstStr = DEFAULT_JSON_RQST;
        }
    }

    private String readFile( String pFilename ) {
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(pFilename));
            String line;

            while (( line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return sb.toString();
    }

    private JsonElement validateRqst(String pJsonStr ) {
        JsonParser tParser = new JsonParser();
        try {
            return tParser.parse(pJsonStr);
        }
        catch( Throwable e) {
            System.out.println("Invalid JSON request syntax");
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    private String readServiceResponse( BufferedReader pReader ) {
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            while ((line = pReader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return sb.toString();
    }

    private void test() {

        JsonElement tJsonRqst = validateRqst(mJsonRqstStr);

        // Step2: Now pass JSON File Data to REST Service
        try {
            URL url = new URL(SERVICE_URL);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(tJsonRqst.toString());
            out.flush();
            out.close();


            String tRspStr = readServiceResponse(new BufferedReader(new InputStreamReader(connection.getInputStream())));

            System.out.println("\n\n\n" + tRspStr);
        } catch (Exception e) {
            System.out.println("\nError while calling JSON REST Service");
            System.out.println(e);
        }
    }
}