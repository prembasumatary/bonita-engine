package com.bonitasoft.engine.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import com.bonitasoft.engine.bdm.model.BusinessObjectModel;
import com.bonitasoft.engine.bdm.model.builder.BusinessObjectModelBuilder;

public class IOUtilsTest {

    private final URL resource = BusinessObjectModel.class.getResource("/bom.xsd");

    @Test
    public void marshallAndUnmarshallShouldReturnTheSameObject() throws Exception {
        final BusinessObjectModel expected = new BusinessObjectModelBuilder().buildDefaultBOM();
        final byte[] xml = IOUtils.marshallObjectToXML(expected, resource);
        final BusinessObjectModel actual = IOUtils.unmarshallXMLtoObject(xml, BusinessObjectModel.class, resource);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void marshallANullObjectReturnsNull() throws Exception {
        final byte[] xml = IOUtils.marshallObjectToXML(null, resource);

        assertThat(xml).isNull();
    }

    @Test(expected = JAXBException.class)
    public void marshallAnObjectWithoutJAXBAnnotationsThrowsAJAXBException() throws Exception {
        final String message = "JAXB or not JAXB?";
        IOUtils.marshallObjectToXML(message, resource);
    }

    @Test
    public void unmarshallANullObjectReturnsNull() throws Exception {
        final BusinessObjectModel object = IOUtils.unmarshallXMLtoObject(null, BusinessObjectModel.class, resource);

        assertThat(object).isNull();
    }

    @Test(expected = JAXBException.class)
    public void unmarshallAnObjectWithoutJAXBAnnotationsThrowsAJAXBException() throws Exception {
        final String xml = "something";
        IOUtils.unmarshallXMLtoObject(xml.getBytes(), BusinessObjectModel.class, resource);
    }

}