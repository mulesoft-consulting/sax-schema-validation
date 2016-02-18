package com.mulesoft.example.filter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.routing.filter.Filter;
import org.mule.api.transport.PropertyScope;
import org.mule.config.i18n.MessageFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class SAXSchemaValidationFilter implements Filter, Initialisable {

	private String schemaLocation;

	private ErrorHandler errorHandler;

	private XMLReader reader;

	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	@Override
	public void initialise() throws InitialisationException {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(this.getClass().getResource("/" + schemaLocation));

			factory.setValidating(false);
			factory.setNamespaceAware(true);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			factory.setSchema(schema);

			SAXParser parser = factory.newSAXParser();

			reader = parser.getXMLReader();
			reader.setErrorHandler(errorHandler);
		} catch (Exception e) {
			throw new InitialisationException(MessageFactory.createStaticMessage("Unable to initialise filter"), e, this);
		}
	}

	@Override
	public boolean accept(MuleMessage message) {
		Object payload = message.getPayload();
		InputSource source = null;

		if (payload instanceof String) {
			source = new InputSource(new ByteArrayInputStream(((String) payload).getBytes()));
		} else if (payload instanceof byte[]) {
			source = new InputSource(new ByteArrayInputStream((byte[]) payload));
		} else if (payload instanceof InputStream) {
			source = new InputSource((InputStream) payload);
		} else {
			message.setProperty("schemaValidationError", "Payload is not of type: " + String.class.getName() + ", byte[] or " + InputStream.class.getName(), PropertyScope.INVOCATION);
			return false;
		}

		try {
			reader.parse(source);
		} catch (Exception e) {
			message.setProperty("schemaValidationError", e.getMessage(), PropertyScope.INVOCATION);
			return false;
		}
		
		return true;
	}

}
