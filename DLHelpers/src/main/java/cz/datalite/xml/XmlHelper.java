package cz.datalite.xml;

import cz.datalite.helpers.StringHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

@SuppressWarnings({"unused", "WeakerAccess", "Duplicates"})
public class XmlHelper
{
    /**
     * XPath faktory
     */
    public static final XPathFactory X_PATH_FACTORY = XPathFactory.newInstance();

    /**
     * Cache pro uložení jaxb kontextu podle java package
     */
    public static Map<String, JAXBContext> jaxbContexPackageCache = new LinkedHashMap<>() ;

    /**
     * Transforem Factory
     */
    public final static TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance() ;

    /**
     * Document faktory
     */
    public final static DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance() ;
    static {
        DOCUMENT_BUILDER_FACTORY.setNamespaceAware(true);
    }

    private static final Object MUTEX = new Object() ;

    /**
     * @param packageName       java package
     * @return JAXB kontext
     */
    public static JAXBContext createContext( String packageName )
    {
        if (jaxbContexPackageCache.containsKey(packageName))
        {
            return jaxbContexPackageCache.get(packageName);
        }
        else
        {
            synchronized (MUTEX)
            {
                try
                {
                    JAXBContext context = JAXBContext.newInstance(packageName);

                    jaxbContexPackageCache.put(packageName, context);

                    return context;
                }
                catch (JAXBException e)
                {
                    throw new IllegalStateException(e);
                }
            }
        }
    }

    /**
     * @param obj  převaděný objekt
     * @return JAXB kontext
     */
    public static JAXBContext createContext( Object obj )
    {
        return createContext( obj.getClass().getPackage().getName() ) ;
    }

    /**
     * @param rootElement       kořenový element
     * @return JAXB kontext
     */
    public static JAXBContext createContext( Class rootElement )
    {
        return createContext( rootElement.getPackage().getName() ) ;
    }

    /**
     * Převod XML dokumentu na řetezec
     *
     * @param document      převáděný dokument
     * @param ident         odsazovat výstup
     * @return převedný dokument
     */
    public static String documentToString( Document document, boolean ident )
    {
        try
        {
            Transformer transformer = TRANSFORMER_FACTORY.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, ( ident ) ? "yes" : "no" ) ;

            DOMSource source = new DOMSource( document ) ;
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);

            transformer.transform(source, result);

            return sw.toString() ;
        }
        catch (TransformerException e)
        {
            throw new IllegalStateException( e ) ;
        }
    }

    /**
     * Převod XML dokumentu na řetezec
     *
     * @param document      převáděný dokument
     * @param ident         odsazovat výstup
     * @return převedný dokument
     */
    public static String nodeToString( Node document, boolean ident )
    {
        try
        {
            Transformer transformer = TRANSFORMER_FACTORY.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, ( ident ) ? "yes" : "no" ) ;

            DOMSource source = new DOMSource( document ) ;
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);

            transformer.transform(source, result);

            return sw.toString() ;
        }
        catch (TransformerException e)
        {
            throw new IllegalStateException( e ) ;
        }
    }

    /**
     * Převod retezce na dokument
     *
     * @param data      prevadena data
     * @return prevedny dokument
     */
    public static Document listToDocument( List<String> data )
    {
        return stringToDocument(StringHelper.notNullConcat("\n", data.toArray(new String[data.size()]))) ;
    }

    /**
     * Převod retezce na dokument
     *
     * @param data          prevadena data
     * @param targetType    cilovy typ
     * @return prevedny dokument
     */
    public static <T> T listToObject( List<String> data, Class<T> targetType )
    {
        return stringToObject(StringHelper.notNullConcat("\n", data.toArray(new String[data.size()])), targetType) ;
    }

    /**
     * Převod retezce na XML
     * @param data          převáděná data
     * @param targetType    cilovy typ
     * @return převedena data
     */
    public static <T> T stringToObject( String data,Class<T> targetType )
    {
        try
        {
            DocumentBuilder builder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream( data.getBytes("UTF-8")));

            if (targetType != Document.class)
            {
                try
                {
                    JAXBContext jc = createContext( targetType ) ;
                    Unmarshaller unmarshaller = jc.createUnmarshaller();

                    //noinspection unchecked
                    return (T)unmarshaller.unmarshal(document) ;
                }
                catch (JAXBException e)
                {
                    throw new IllegalStateException(e);
                }
            }

            //noinspection unchecked
            return (T)document ;
        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            throw new IllegalStateException( e ) ;
        }
    }


    /**
     * Převod retezce na XML
     * @param data      převáděná data
     * @return převedena data
     */
    public static Document stringToDocument( String data )
    {
        try
        {
            DocumentBuilder builder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder() ;

            Document document = builder.parse(new ByteArrayInputStream( data.getBytes("UTF-8")));

            document.normalizeDocument() ;

            return document ;
        }
        catch (ParserConfigurationException | IOException | SAXException e)
        {
            throw new IllegalStateException( e ) ;
        }
    }

    /**
     * Převod XML objektu na řetezec
     *
     * @param document      převáděný objekt
     * @param ident         odsazovat výstup
     * @return převedný dokument
     */
    public static String objectToString( Object document, boolean ident )
    {
        if ( document instanceof Document )
        {
            return documentToString( (Document)document, ident ) ;
        }
        else if ( document instanceof Node )
        {
            return nodeToString( (Node)document, ident ) ;
        }
        try
        {
            JAXBContext jc = createContext( document );
            StringWriter writer = new StringWriter();
            Marshaller marshaller = jc.createMarshaller();

            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, ident ) ;
            marshaller.marshal( document, writer ) ;

            return writer.toString() ;
        }
        catch (JAXBException e)
        {
            throw new IllegalStateException( e ) ;
        }
    }

    /**
     * @param obj       objekt
     * @return document
     */
    public static <T> Document objectToDocument( T obj )
    {
         return stringToDocument( objectToString( obj, false ) ) ;
    }


    /**
     *
     * @param document      převáděná hodnota
     * @param maxLen        maximální délka řetezce
     * @return seznam řetezců reprezentují převedný objekt
     */
    public static List<String> stringToList( String document, int maxLen )
    {
        String value = document ;
        List<String> result = new ArrayList<>() ;

        while ( ( value != null ) && ( value.length() > 0 ) )
        {
              if ( value.length() > maxLen )
              {
                  result.add( value.substring( 0, maxLen ) ) ;
                  value = value.substring( maxLen ) ;
              }
              else
              {
                  result.add( value ) ;
                  value = null ;
              }
        }

        return result ;
    }

    /**
     * @param document      převáděný objekt
     * @param ident         odsazovat výstup
     * @param maxLen        maximální délka řetezce
     * @return seznam řetezců reprezentují převedný objekt
     */
    public static List<String> objectToList( Object document, boolean ident, int maxLen )
    {
        return stringToList( objectToString( document, ident ), maxLen ) ;
    }

    /**
     * @param document      převáděný objekt
     * @param ident         odsazovat výstup
     * @param maxLen        maximální délka řetezce
     * @return seznam řetezců reprezentují převedný objekt
     */
    public static List<String> documentToList( Document document, boolean ident, int maxLen )
    {
        return stringToList( documentToString( document, ident ), maxLen ) ;
    }

    /**
     *
     * @param document          XML dokument
     * @param targetClass       cilový objekt
     * @return prevedeny objekt
     */
    public static <T> T documentToObject( Document document, Class<T> targetClass )
    {
        return stringToObject( documentToString( document, false ), targetClass ) ;
    }

    /**
     *
     * @param document          XML dokument
     * @param targetClass       cilový objekt
     * @return prevedeny objekt
     */
    public static <T> T nodeToObject( Node document, Class<T> targetClass )
    {
        return stringToObject(nodeToString(document, false), targetClass);
    }
}
