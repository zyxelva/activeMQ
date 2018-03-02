package msgSend.xmlsend;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/*
 ** Unmarshaller 绫讳娇瀹㈡埛绔簲鐢ㄧ▼搴忚兘澶熷皢 XML 鏁版嵁杞崲涓� Java 鍐呭瀵硅薄鏍戙��
 **	澶囨敞锛歮arshal(搴忓垪鍖栥�佹帓鍒椼�佹暣鐞�)
 **	Marshaller 绫讳娇瀹㈡埛绔簲鐢ㄧ▼搴忚兘澶熷皢 Java 鍐呭鏍戣浆鎹㈠洖 XML 鏁版嵁銆�
 * */

@XmlRootElement
public class JaxbUtil {
	/**
	 * JavaBean -> xml
	 *
	 * @param obj
	 * @param encoding
	 * @return
	 */
	public static String convertToXml(Object obj) {
		try {
			JAXBContext context;
			
			context = JAXBContext.newInstance(obj.getClass());
			
			Marshaller marshaller = context.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(baos, (String) marshaller.getProperty(Marshaller.JAXB_ENCODING));
			xmlStreamWriter.writeStartDocument((String) marshaller.getProperty(Marshaller.JAXB_ENCODING), "1.0");

			marshaller.marshal(obj, xmlStreamWriter);
			xmlStreamWriter.writeEndDocument();
			xmlStreamWriter.close();

			return baos.toString("UTF-8");
			
		} catch (JAXBException e) {
			//throw new BusinessException("E0000000");
		} catch (XMLStreamException e) {
			//throw new BusinessException("E0000000");
		} catch (UnsupportedEncodingException e) {
			//throw new BusinessException("E0000000");
		}
        return null;
	}

	/**
	 * xml -> JavaBean *
	 *
	 * @param xml
	 * @param c
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T converyToJavaBean(String xml, Class<T> c) {
		T t = null;
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(c);
			
			//JaxbUtil.java:65 涓厤缃殑 XML 瑙ｆ瀽鍣ㄦ棤娉曢闃插拰闄愬埗澶栭儴瀹炰綋杩涜瑙ｆ瀽銆傝繖浼氫娇瑙ｆ瀽鍣ㄦ毚闇插湪 XML External Entities 鏀诲嚮涔嬩笅
			/*Unmarshaller unmarshaller = context.createUnmarshaller();
			t = (T) unmarshaller.unmarshal(new StringReader(xml));*/
			XMLInputFactory xif = XMLInputFactory.newInstance();  
			xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
			xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
			XMLStreamReader xsr = xif.createXMLStreamReader(new StringReader(xml));
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return (T)unmarshaller.unmarshal(xsr);

		} catch (JAXBException e) {
			//throw new BusinessException("E0000000");
		} catch (XMLStreamException e) {
			//throw new BusinessException("E0000000");
		}
        return t;
	}
}
