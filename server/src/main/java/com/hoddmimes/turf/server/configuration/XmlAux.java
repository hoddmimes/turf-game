package com.hoddmimes.turf.server.configuration;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;


/**
 * Created by N446350 on 2016-09-06.
 */
public class XmlAux
{
    private static Transformer cTransformer = null;
    private static HashMap<String,Transformer> cXltCache = new HashMap<String,Transformer>();



    public static String transformTradeMessage( Element pMsgElement, String pXslFilename, List<NameValuePair> pParameters ) throws Exception {

        Transformer tTransformer = cXltCache.get(pXslFilename);
        if (tTransformer == null) {
            StreamSource tXslSource = new StreamSource(pXslFilename);
            TransformerFactory tFactory = TransformerFactory.newInstance();
            tTransformer = tFactory.newTransformer(tXslSource);
            cXltCache.put(pXslFilename, tTransformer);
        }

        if (pParameters != null) {
            for( NameValuePair nv : pParameters) {
                tTransformer.setParameter(nv.mName, nv.mValue);
            }
        }

        StringWriter tOutWriter = new StringWriter();
        StreamResult tResult = new StreamResult(tOutWriter);
        //StreamResult result = new StreamResult(new FileOutputStream("new.txt"));
        tTransformer.transform(new DOMSource(pMsgElement), tResult);
        return XmlAux.getTrimmedXML(tOutWriter.toString());
    }

    public static String transform( Element pMsgElement, String pXslFilename ) throws Exception {
        return transformTradeMessage( pMsgElement, pXslFilename, null);
    }

    public static  String transform(  String pInXml, String pXslFilename ) throws Exception {
        DocumentBuilderFactory tDocFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder tBuilder = tDocFactory.newDocumentBuilder();


        Document tDocument = tBuilder.parse(new InputSource( new StringReader(pInXml)));

        StreamSource tXslSource  = new StreamSource(pXslFilename);

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer tTransformer = tFactory.newTransformer(tXslSource);

        StringWriter tOutWriter = new StringWriter();
        StreamResult tResult = new StreamResult(tOutWriter);
        //StreamResult result = new StreamResult(new FileOutputStream("new.txt"));
        tTransformer.transform(new DOMSource(tDocument), tResult);
        return tOutWriter.toString();
    }



    public synchronized static  String elementToString(Element node) {
        StringWriter sw = new StringWriter();
        try {
            if (cTransformer == null) {
                cTransformer = TransformerFactory.newInstance().newTransformer();
            }
            //cTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            //cTransformer.setOutputProperty(OutputKeys.INDENT, "no");
            cTransformer.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            te.printStackTrace();
        }
        return sw.toString();
    }

    public static Document loadXMLFromString(String pXml) throws Exception
    {
        DocumentBuilderFactory tFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = tFactory.newDocumentBuilder();
        InputSource tInputStream = new InputSource(new StringReader(pXml));
        return builder.parse(tInputStream);
    }

    public static Document loadXMLFromFile(String pXmlFile) throws Exception
    {
        DocumentBuilderFactory tFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = tFactory.newDocumentBuilder();
        InputSource tInputStream = new InputSource(new InputStreamReader( new FileInputStream( pXmlFile )));
        return builder.parse(tInputStream);
    }


    public static boolean isElementPresent( Element tTopElement, String pElementPath ) {

        Element tCurrElement = tTopElement;
        String tPath = (pElementPath.startsWith("/")) ? pElementPath.substring(1) : pElementPath;
        tPath = (tPath.endsWith("/")) ? tPath.substring(0,tPath.length() - 1) : tPath;
        String[] tElemntTags = tPath.split("/");





        if (!tCurrElement.getNodeName().equals(tElemntTags[0])) {
            return false;
        }

        for (int i = 1; i < tElemntTags.length; i++) {
            boolean tFound = false;
            NodeList tNodeList = tCurrElement.getChildNodes();
            for (int j = 0; j < tNodeList.getLength(); j++) {
                if (tNodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                    if (((Element) tNodeList.item(j)).getNodeName().equals(tElemntTags[i])) {
                        tCurrElement = (Element) tNodeList.item(j);
                        tFound = true;
                        break;
                    }
                }
            }
            if (!tFound) {
                return false;
            }
        }
        return true;
    }


    public static Element getElement(  Element pTopElement, String pElementPath ) {
        Element tCurrElement = pTopElement;
        String tPath = (pElementPath.startsWith("/")) ? pElementPath.substring(1) : pElementPath;
        tPath = (tPath.endsWith("/")) ? tPath.substring(0,tPath.length() - 1) : tPath;
        String[] tElemntTags = tPath.split("/");

        /**
         * Check that the document element is equal with our first navigation element name
         */
        if (tCurrElement.getNodeName().compareTo( tElemntTags[0])  != 0) {
            return null;
        }


        for (int i = 1; i < tElemntTags.length; i++) {
            boolean tFound = false;
            NodeList tNodeList = tCurrElement.getChildNodes();
            for (int j = 0; j < tNodeList.getLength(); j++) {
                if (tNodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                    if (((Element) tNodeList.item(j)).getNodeName().equals(tElemntTags[i])) {
                        tCurrElement = (Element) tNodeList.item(j);
                        tFound = true;
                        break;
                    }
                }
            }
            if (!tFound) {
                return null;
            }
        }
        return tCurrElement;
    }

    public static String getElementValue( Element pElement ) {
        Node tNode = pElement.getFirstChild();
        if (tNode == null) {
            return new String("");
        } else {
            return pElement.getFirstChild().getTextContent();
        }
    }

    public static boolean isAttributePresent( Element pElement, String pAttributeName ) {
        if ((pElement.getAttribute( pAttributeName) != null) && (pElement.getAttribute( pAttributeName).length() > 0)) {
            return true;
        }
        return false;
    }

    public static String getStringAttribute(Element pElement, String pAttributeName, String pDefaultValue ) {
        if ((pElement.getAttribute( pAttributeName) != null) && (pElement.getAttribute( pAttributeName).length() > 0)) {
            return  pElement.getAttribute( pAttributeName);
        } else {
            return pDefaultValue;
        }
    }

    public static int getIntAttribute(Element pElement, String pAttributeName, int pDefaultValue ) {
        if ((pElement.getAttribute( pAttributeName) != null) && (pElement.getAttribute( pAttributeName).length() > 0)) {
            return  Integer.parseInt(pElement.getAttribute( pAttributeName));
        } else {
            return pDefaultValue;
        }
    }

    public static double getDoubleAttribute(Element pElement, String pAttributeName, double pDefaultValue ) {
        if ((pElement.getAttribute( pAttributeName) != null) && (pElement.getAttribute( pAttributeName).length() > 0)) {
            return  Double.parseDouble(pElement.getAttribute( pAttributeName));
        } else {
            return pDefaultValue;
        }
    }

    private static boolean isEndNode( Node pNode ) {
        NodeList tNodeList = pNode.getChildNodes();
        for( int i = 0; i < tNodeList.getLength(); i++) {
            if (tNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return false;
            }
        }
        return true;
    }

    private static void  trimNode( Node pNode, int pLevel ) {
       // System.out.println("Node[" + pLevel + "]" + pNode.getNodeName());
        NodeList tNodeList = pNode.getChildNodes();
        for (int i = 0; i < tNodeList.getLength(); i++) {
            Node tNode = tNodeList.item(i);
            if (tNode.getNodeType() == Node.ELEMENT_NODE) {
                if (isEndNode( tNode )) {

                    if ((tNode.getFirstChild() != null) && (tNode.getFirstChild().getTextContent() != null)) {
                        String tNewTextContent = tNode.getFirstChild().getTextContent().trim();
                        tNode.getFirstChild().setTextContent(tNewTextContent);
                    }
                }
                trimNode( tNode, (i+1));
            }
        }
    }

    public static String getTrimmedXML(String pXMLMessage) throws Exception {
        // Create xml document object
        //System.out.println("==============================================================");
        //System.out.println( pXMLMessage  );
        //System.out.println("==============================================================");
        Document tDocument = loadXMLFromString( pXMLMessage );
        Node tNode = tDocument.getDocumentElement();
        trimNode( tNode, 1 );


        // Transform back the document to string format.
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        //transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        //transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(tDocument), new StreamResult(writer));
        String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
        return output;
    }
}
