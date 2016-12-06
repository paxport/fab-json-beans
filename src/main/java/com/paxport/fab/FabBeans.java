package com.paxport.fab;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Convert FAB XML into JSON
 */
public class FabBeans {

    public final static String JSON_MEDIA_TYPE = "application/json";
    public final static String XML_MEDIA_TYPE = "application/xml";

    public <E> E unmarshal(InputStream source, String mediaType, Class<E> type){
        try {
            JAXBContext jc = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            if ( mediaType != null ){
                unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, mediaType);
            }
            unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, true);
            return (E) unmarshaller.unmarshal(source);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to unmarshal into type: " + type.getName(),e);
        }
    }

    public <E> E fromXML(InputStream source, Class<E> type){
        return unmarshal(source,XML_MEDIA_TYPE,type);
    }

    public <E> E fromJSON(InputStream source, Class<E> type){
        return unmarshal(source,JSON_MEDIA_TYPE,type);
    }

    public void marshal (Object bean, OutputStream out, String mediaType, boolean prettyPrint){
        try {
            // Create a JaxBContext
            JAXBContext jc = JAXBContext.newInstance(bean.getClass());

            // Create the Marshaller Object using the JaxB Context
            Marshaller marshaller = jc.createMarshaller();

            // Set the Marshaller media type to JSON or XML
            marshaller.setProperty(MarshallerProperties.MEDIA_TYPE,mediaType);

            // Set it to true if you need to include the JSON root element in the JSON output
            marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);

            // Set it to true if you need the JSON output to formatted
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, prettyPrint);

            marshaller.marshal(jc, out);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to marshal from type: " + bean.getClass().getName(),e);
        }
    }

    public void toXML(Object bean, OutputStream out, boolean prettyPrint){
        marshal(bean,out,XML_MEDIA_TYPE,prettyPrint);
    }

    public String toXML(Object bean, boolean prettyPrint){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        toXML(bean,out,prettyPrint);
        try {
            return out.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void toJSON(Object bean, OutputStream out, boolean prettyPrint){
        marshal(bean,out,JSON_MEDIA_TYPE,prettyPrint);
    }

    public String toJSON(Object bean, boolean prettyPrint){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        toJSON(bean,out,prettyPrint);
        try {
            return out.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
