package es.predictia.metaclip.visualizer.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.xml.bind.DatatypeConverter;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class MetaclipExtractor {
	
	public static Optional<String> extract(File input) throws Exception{
		ImageInputStream iis = ImageIO.createImageInputStream(input);
		try{
			return extract(iis);
		}finally{
			iis.close();
		}
	}
	
	public static Optional<String> extract(InputStream input) throws Exception{
		ImageInputStream iis = ImageIO.createImageInputStream(input);
		try{
			return extract(iis);
		}finally{
			iis.close();
		}
	}

	public static Optional<String> extract(ImageInputStream input) throws Exception{
	    Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
	    if(readers.hasNext()){
			ImageReader reader = readers.next();
			reader.setInput(input, true);
			IIOMetadata metadata = reader.getImageMetadata(0);
			String[] names = metadata.getMetadataFormatNames();
			int length = names.length;
			for(int i = 0; i < length; i++){
				Element element = (Element)metadata.getAsTree(names[i]);
				Optional<String> metaclip = findMetaclipElement(element);
				if(metaclip.isPresent()){
					// TODO detect if the string format (e.g. base64...)
					String decoded = decompress(hexToBinary(metaclip.get()));
					return Optional.of(decoded);
				}
			}
		}
	    return Optional.empty();
	}
	
	private static byte[] hexToBinary(String hex) {
		 return DatatypeConverter.parseHexBinary(hex);
	}
	
	private static String decompress(byte[] data) throws IOException, DataFormatException {
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[1024];
		while (!inflater.finished()) {
			int count = inflater.inflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		outputStream.close();
		byte[] output = outputStream.toByteArray();
		return new String(output);
	}
	
	private static Optional<String> findMetaclipElement(Element elem) {
		List<String> result = new LinkedList<String>();
		findMetaclipElement(elem, result);
		if(!result.isEmpty()){
			return Optional.of(result.get(0));
		}
		return Optional.empty();
	}
	
	private static void findMetaclipElement(Element el, List<String> elementList) {
		if (METACLIP_TAG_NAME.equals(el.getTagName())){
			if(el.hasAttribute(METACLIP_ATTRIBUTE_NAME)){
				if(el.getAttribute(METACLIP_ATTRIBUTE_NAME).equalsIgnoreCase(METACLIP_ATTRIBUTE_VALUE)){
					elementList.add(el.getAttribute(METACLIP_ATTRIBUTE_DATA_NAME));
					return;
				}
			}
		}
		Element elem = getFirstElement(el);
		while (elem != null) {
			findMetaclipElement(elem, elementList);
			elem = getNextElement(elem);
		}
	}
	
	private static Element getFirstElement(Node parent) {
		Node n = parent.getFirstChild();
		while (n != null && Node.ELEMENT_NODE != n.getNodeType()) {
			n = n.getNextSibling();
		}
		if (n == null) {
			return null;
		}
		return (Element) n;
	}
	
	private static Element getNextElement(Element el) {
		Node nd = el.getNextSibling();
		while (nd != null) {
			if (nd.getNodeType() == Node.ELEMENT_NODE) {
				return (Element) nd;
			}
			nd = nd.getNextSibling();
		}
		return null;
	}

	private static final String METACLIP_TAG_NAME = "tEXtEntry";
	private static final String METACLIP_ATTRIBUTE_NAME = "keyword";
	private static final String METACLIP_ATTRIBUTE_VALUE = "metaclip";
	private static final String METACLIP_ATTRIBUTE_DATA_NAME = "value";	
	
}
