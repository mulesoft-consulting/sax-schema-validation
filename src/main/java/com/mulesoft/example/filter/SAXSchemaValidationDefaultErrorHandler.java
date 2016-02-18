package com.mulesoft.example.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SAXSchemaValidationDefaultErrorHandler implements ErrorHandler {

	private static Log logger = LogFactory.getLog(SAXSchemaValidationDefaultErrorHandler.class);
	
	@Override
	public void error(SAXParseException e) throws SAXException {
		throw new SAXException(e);
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		throw new SAXException(e);
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
		logger.warn(e);
	}

}
