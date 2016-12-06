package com.paxport.fab;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.paxport.fab.beans.ObjectFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

/**
 * Convert FAB XML into JSON
 */
public class FabBeans {

    public final static String JSON_MEDIA_TYPE = "application/json";
    public final static String XML_MEDIA_TYPE = null;

    private final JAXBContext context = createJaxbContext();
    private final ObjectMapper mapper = createMapper();

    private static JAXBContext createJaxbContext() {
        try {
            return JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectMapper createMapper()
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JaxbAnnotationModule());
        // don't break if our version of model has new things in
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // don't bother sending nulls
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    public <E> E fromXML(InputStream source, Class<E> type){
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return unmarshaller.unmarshal(new StreamSource(source), type).getValue();
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to unmarshal into type: " + type.getName(),e);
        }
    }

    public <E> E fromXML(String xml, Class<E> type){
        return fromXML(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)),type);
    }

    public <E> E fromJSON(InputStream source, Class<E> type){
        try {
            return mapper.readValue(source,type);
        } catch (IOException e) {
            throw new RuntimeException("failed to read json into " + type.getName(),e);
        }
    }

    public <E> E fromJSON(String json, Class<E> type){
        return fromJSON(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)),type);
    }

    public void toXML(Object bean, OutputStream out, boolean prettyPrint){
        try {
            // Create the Marshaller Object using the JaxB Context
            Marshaller marshaller = context.createMarshaller();

            // Set it to true if you need the JSON output to formatted
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, prettyPrint);

            marshaller.marshal(bean, out);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to marshal from type: " + bean.getClass().getName(),e);
        }
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

    public void toJSON(Object bean, OutputStream out){
        try {
            mapper.writeValue(out,bean);
        } catch (IOException e) {
            throw new RuntimeException("failed to write json",e);
        }
    }

    public String toJSON(Object bean){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        toJSON(bean,out);
        try {
            return out.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public <E> void xmlToJson(InputStream source, OutputStream out, Class<E> type){
        E bean = fromXML(source,type);
        toJSON(bean,out);
    }

    public <E> String xmlToJson(InputStream source, Class<E> type){
        E bean = fromXML(source,type);
        return toJSON(bean);
    }

    public <E> String xmlToJson(String xml, Class<E> type){
        return xmlToJson(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)),type);
    }

    public <E> void jsonToXml(InputStream source, OutputStream out, Class<E> type, boolean prettyPrint){
        E bean = fromJSON(source,type);
        toXML(bean,out,prettyPrint);
    }

    public <E> String jsonToXml(InputStream source, Class<E> type, boolean prettyPrint){
        E bean = fromJSON(source,type);
        return toXML(bean,prettyPrint);
    }

    public <E> String jsonToXml(String json, Class<E> type, boolean prettyPrint){
        return jsonToXml(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)),type,prettyPrint);
    }

}
