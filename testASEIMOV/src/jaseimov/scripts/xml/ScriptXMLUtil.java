/*
 * Copyright (C) 2010 Aday Talavera Hierro <aday.talavera@gmail.com>
 *
 * This file is part of JASEIMOV.
 *
 * JASEIMOV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JASEIMOV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JASEIMOV.  If not, see <http://www.gnu.org/licenses/>.
 */

package jaseimov.scripts.xml;

import jaseimov.scripts.xml.bindings.Script;
import java.io.File;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ScriptXMLUtil
{
  // XML schema file location
  //public final static String XML_SCHEMA = "xml-resources/jaxb/script/script_aseimov.xsd";
  public final static String XML_SCHEMA = "script_aseimov.xsd";

  public static Script parseXMLScript(String filename) throws JAXBException
  {
    return (Script) getUnmarshaller().unmarshal(new File(filename));
  }

  public static void saveScript(Script script, String filename) throws JAXBException
  {
    // Configure output
    Marshaller m = getMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

    m.marshal(script, new File(filename));
  }

  public static void validateXMLScript(String filename) throws JAXBException, SAXException, IOException
  {
    Script script = parseXMLScript(filename);

    JAXBContext jaxbContext = getJAXBContext();
    JAXBSource source = new JAXBSource(jaxbContext, script);

    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = sf.newSchema(ScriptXMLUtil.class.getResource(XML_SCHEMA));
    //Schema schema = sf.newSchema(new File(XML_SCHEMA));

    Validator validator = schema.newValidator();
    // Validator to grab exception message
    /*validator.setErrorHandler(new org.xml.sax.ErrorHandler()
    {
      private String errorMessage = null;

      public void warning(SAXParseException exception) throws SAXParseException
      {
        printException(exception);
        throw exception;
      }

      public void error(SAXParseException exception) throws SAXException
      {
        printException(exception);
        throw exception;
      }

      public void fatalError(SAXParseException exception) throws SAXParseException
      {
        printException(exception);
        throw exception;
      }

      private void printException(SAXParseException e)
      {
        System.out.println(e.getMessage());
      }
    });*/

    validator.validate(source);
  }

  /*
   * Private utils
   */
  private static JAXBContext getJAXBContext() throws JAXBException
  {
    return JAXBContext.newInstance(Script.class.getPackage().getName());
  }

  private static Unmarshaller getUnmarshaller() throws JAXBException
  {
    return getJAXBContext().createUnmarshaller();
  }

  private static Marshaller getMarshaller() throws JAXBException
  {
    return getJAXBContext().createMarshaller();
  }
}
