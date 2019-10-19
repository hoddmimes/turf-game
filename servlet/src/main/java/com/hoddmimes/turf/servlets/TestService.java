package com.hoddmimes.turf.servlets;

import com.google.gson.JsonObject;
import com.hoddmimes.jsontransform.MessageInterface;
import com.hoddmimes.turf.common.generated.MessageFactory;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;

// Plain old Java Object it does not extend as class or implements
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/test")
public class TestService {

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public String sayJsonHello( String pRqstStr )
  {
    System.out.println("rqst: " + pRqstStr );

    // MessageFactory mf = new MessageFactory();
    // MessageInterface mi = mf.getMessageInstance( pRqstStr );


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    JsonObject jRsp = new JsonObject();

    jRsp.addProperty("type","response");
    jRsp.addProperty("service", this.getClass().getSimpleName());
    jRsp.addProperty("time", sdf.format( System.currentTimeMillis()));
    jRsp.addProperty("success",  true );

    return jRsp.toString();
  }


  // This method is called if TEXT_PLAIN is request
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String sayPlainTextHello() {
    return "Turf Test Service";
  }

  // This method is called if XML is request
  @GET
  @Produces(MediaType.TEXT_XML)
  public String sayXMLHello() {
    return "<?xml version=\"1.0\"?>" + "<hello> Turf Test Service" + "</hello>";
  }

  // This method is called if HTML is request
  @GET
  @Produces(MediaType.TEXT_HTML)
  public String sayHtmlHello() {
    return "<html> " + "<title>" + "TestService RBP" + "</title>"
        + "<body><h1>" + "Turf Test Service" + "</body></h1>" + "</html> ";
  }

}