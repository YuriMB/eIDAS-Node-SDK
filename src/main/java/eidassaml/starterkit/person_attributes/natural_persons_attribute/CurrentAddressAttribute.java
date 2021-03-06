/* 
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * 
 * Date: 09 Feb 2016
 * Authors: Governikus GmbH & Co. KG
 * 
*/
package eidassaml.starterkit.person_attributes.natural_persons_attribute;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import eidassaml.starterkit.person_attributes.EidasPersonAttributes;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import eidassaml.starterkit.EidasAttribute;
import eidassaml.starterkit.EidasNaturalPersonAttributes;
import eidassaml.starterkit.Utils;
import eidassaml.starterkit.template.TemplateLoader;

public class CurrentAddressAttribute implements EidasAttribute{

	protected final static String CVAddressTemp = "<eidas:LocatorDesignator>$locatorDesignator</eidas:LocatorDesignator>"+
											"<eidas:Thoroughfare>$thoroughfare</eidas:Thoroughfare>"+
											"<eidas:PostName>$postName</eidas:PostName>"+
											"<eidas:PostCode>$postCode</eidas:PostCode>";
	
	private String locatorDesignator;
	private String thoroughfare;
	private String postName;
	private String postCode;

	public CurrentAddressAttribute(){}

	public CurrentAddressAttribute(String locatorDesignator,
			String thoroughfare, String postName, String postCode) {
		super();
		this.locatorDesignator = locatorDesignator;
		this.thoroughfare = thoroughfare;
		this.postName = postName;
		this.postCode = postCode;
	}
	
	/**
	 * Adds a root XML Container around the given CurrentAddressAttribute. Otherwise the XML is not well formed and will raise an exception
	 * 
	 * @param xmlString  
	 * @throws SAXException
	 */
	public CurrentAddressAttribute(String xmlString) throws SAXException
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			String xml = "<root>"+xmlString+"</root>";
			SAXParser saxParser = factory.newSAXParser();
			AddressAttributeXMLHandler handler = new AddressAttributeXMLHandler();
			saxParser.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")), handler);
			this.locatorDesignator = handler.locatorDesignator;
			this.thoroughfare = handler.thoroughfare;
			this.postName = handler.postName;
			this.postCode = handler.postCode;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new SAXException(e);
		}
	}
	
	public String getLocatorDesignator() {
		return locatorDesignator;
	}

	public void setLocatorDesignator(String locatorDesignator) {
		this.locatorDesignator = locatorDesignator;
	}

	public String getThoroughfare() {
		return thoroughfare;
	}

	public void setThoroughfare(String thoroughfare) {
		this.thoroughfare = thoroughfare;
	}

	public String getPostName() {
		return postName;
	}
	
	public void setPostName(String postName) {
		this.postName = postName;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	@Override
	public String generate() {
		String value = CVAddressTemp.replace("$locatorDesignator", locatorDesignator)
				.replace("$thoroughfare", thoroughfare)
				.replace("$postName", postName)
				.replace("$postCode", postCode);
		return TemplateLoader.GetTemplateByName("currentAddress").replace("$value", Utils.ToBase64(value));
	}

	@Override
	public String type() {
		return EidasAttribute.TYPE_CurrentAddress;
	}
	
	@Override
	public String toString() {
		return type() + " " + this.locatorDesignator + " " + this.thoroughfare + " , " + this.postCode + " " + this.postName;
	}
	
	@Override
	public EidasPersonAttributes getPersonAttributeType() {
		return EidasNaturalPersonAttributes.CurrentAddress;
	}

	@Override
	public void setValue(String value) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			String xml = "<root>"+value+"</root>";
			SAXParser saxParser = factory.newSAXParser();
			AddressAttributeXMLHandler handler = new AddressAttributeXMLHandler();
			saxParser.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")), handler);
			this.locatorDesignator = handler.locatorDesignator;
			this.thoroughfare = handler.thoroughfare;
			this.postName = handler.postName;
			this.postCode = handler.postCode;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new IllegalArgumentException("Could not parse address", e);
		}
	}

	public String getValue() {
		return CVAddressTemp.replace("$locatorDesignator", getLocatorDesignator())
				.replace("$thoroughfare", getThoroughfare())
				.replace("$postName", getPostName())
				.replace("$postCode", getPostCode());
	}

	class AddressAttributeXMLHandler extends DefaultHandler {
		
		private String locatorDesignator = "";
		private String thoroughfare = "";
		private String postName = "";
		private String postCode = "";
		
		private String currentQName = "";
		
		
		@Override
		public void startElement(String uri, String localName,
		        String qName, Attributes attributes) throws SAXException {
			currentQName = qName.toLowerCase();
		}
		
		@Override
		public void characters(char ch[], int start, int length)
				throws SAXException {
			String value = new String(ch, start, length).trim();
			if(Utils.IsNullOrEmpty(value))
				return;
			if(currentQName.contains("locatordesignator")){
				locatorDesignator = value;return;
			}else if(currentQName.contains("thoroughfare")){
				thoroughfare = value;return;
			}else if(currentQName.contains("postname")){
				postName = value;return;
			}else if(currentQName.contains("postcode")){
				postCode = value;return;
			}
		}	
	}
	
}
