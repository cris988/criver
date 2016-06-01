/**
 * Argenta Christian
 * Speranza Cristian
 * Gennaio 2011
 * Copyright
 */

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Util {

	public static String nuovoCodice(String path, String id)
	{
		String code;
		do
		{
			code = UUID.randomUUID().toString().substring(24);
		}
		while( Util.readXML(path, id, code)!=null );
		return code;
	}
	
	public static Node getFindChildNode(Node node, String id)
	{
		/*
		 * PERMETTE DI CERCARE NEI NODI FIGLI ID E VALUE
		 * Nota: nodi univoci per i figli
		 * @param node: Node in cui ci si trova
		 * @param id: Nodo in cui si vuole andare
		 * @return Node: Nodo padre
		 */
		
		NodeList listNode = ((Element)node).getElementsByTagName(id);
		
		if ( listNode.getLength() != 0 )
		{
			return listNode.item(0);
		}
		return null;
	}
	

	public static Node getFindChildNode(Node node, String id, String value)
	{
		/*
		 * PERMETTE DI CERCARE NEI NODI FIGLI ID E VALUE
		 * Nota: nodi univoci per i figli
		 * @param node: Node in cui ci si trova
		 * @param id: Nodo in cui si vuole andare
		 * @parama value: Valore da cercare
		 * @return Node: Nodo padre
		 */
		
		NodeList listNode = ((Element)node).getElementsByTagName(id);
		
		if ( listNode.getLength() != 0 )
		{
			int i=0;
			do 
			{
				if ( getNodeValue(listNode.item(i)).equals(value) )
				{
					return listNode.item(i).getParentNode();
				}
				i++;
			}
			while ( i<listNode.getLength() );
		}
		return null;
	}

	public static Node getNextNode(Node node, String id)
	{
		/*
		 * PERMETTE DI SPOSTARMI NEI NODI DELLO STESSO LIVELLO
		 * Nota: nodi univoci per lo stesso livello
		 * @param node: Node in cui ci si trova
		 * @param id: Nodo in cui si vuole andare
		 * @return Node: nodo in cui si � andati
		 */
		
		node = node.getParentNode();
		
		NodeList listNode = ((Element)node).getElementsByTagName(id);
		
		return listNode.item(0);
	}

	public static Node getParentNode(Node nodo, String parent)
	{
		/*
		 * TROVA IL NODO PADRE
		 */
		
		/* Se il nodo cercato � quello attuale lo ritorna */ 
		if ( nodo.getNodeName().equals(parent) )
		{
			return nodo;
		}
		else
		{
			/* Altrimenti lo cerca*/
			do
			{
				nodo = nodo.getParentNode();	
			}
			while ( nodo != null && !nodo.getNodeName().equals(parent));
			
			return nodo;
		}
	}
	
	public static String getNodeValue(Node node)
	{
		/*
		 * RITORNA IL VALORE TESTUALE DEL FIGLIO DEL NODO
		 * @param node: Node in cui ci si trova
		 * @return String: valore del nodo
		 */
		return node.getFirstChild().getNodeValue();
	}

	public static String getNodeValue(Node node, String id)
	{
		/*
		 * RITORNA IL VALORE TESTUALE DI UNO DEI FIGLI DEL NODO
		 * @param node: Node in cui ci si trova
		 * @param id: id del campo
		 * @return String: valore del nodo
		 */
		
		NodeList list = ((Element)node).getElementsByTagName(id);
		
		return list.item(0).getFirstChild().getNodeValue();
	}
	
	public static Node readXML(String filename, String id, String value)
	{
		/*
		 * RICERCA NEL FILE XML IL NODO CHE CORRISPONDE A UN CERTO VALORE PASSATO
		 * @param filename: Nome del file da leggere
		 * @param id: Tag del nodo da cercare
		 * @param value: Valore del nodo da confrontare
		 * @return Node: ritorna il nodo padre di id
		 */
		
		Node child = readXML(filename);
	    // Lista di tutti gli elementi
	    NodeList list = ((Element)child).getElementsByTagName(id);	    
	    
	    if ( list.getLength()!=0 )
	    {
		    int i=0;
		    while ( i<list.getLength())
		    {
		    	Node nodo = list.item(i).getFirstChild();
		    	
		    	if ( nodo.getNodeValue().equals(value) )
		    	{
			    	do
			    	{
			    		nodo = nodo.getParentNode();	
			    	}
			    	while( nodo.getParentNode() != child );
			    	
			    	return nodo;
		    	}
		    	else
		    	{
		    		i++;
		    	}
		    }
	    }
	    return null;
	}
	
	public static NodeList readXML(String filename, String id)
	{
		/*
		 * LEGGE I FILE XML
		 * @param filename: Nome del file da leggere
		 * @param id: Tag del nodo
		 * @return NodeList: lista di nodi
		 */
		
		Node child = readXML(filename);
	    // Lista di tutti gli elementi
	    NodeList list = ((Element)child).getElementsByTagName(id);
	    
		    
		return list;
	}
	
	public static Node readXML(String filename)
	{
		/*
		 * LEGGE I FILE XML
		 * @param filename: Nome del file da leggere
		 * @return Node: ritorna il nodo parent di tutti
		 */
		
		Document doc=null;
		
		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	
	    try {
	    	dbf.setIgnoringElementContentWhitespace(true);
	        db = dbf.newDocumentBuilder();
	    	doc = db.parse(new File(filename));

			//******* ALBERO DOM *********/
			
			// Elemento corrispondente al DOCTYPE
		    Node child = doc.getFirstChild();
		    // Elemento corrispondente al primo elemento del documento
		    child = child.getNextSibling();
		    
		    return child;
	    }	
		catch (IOException se){
			System.err.println("Il file " + filename + " non e' stato trovato");
			return null;
		}
		catch (SAXException se){
			System.err.println("Non � possibile fare il parser");
			return null;
		}	
		catch (ParserConfigurationException pce){
			return null;
		}
	}

	public static void writeXML(Document doc, String path)
	{
		try 
		{
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer transf = tf.newTransformer();
	        
	        transf.setOutputProperty("indent","yes");
	    	transf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","4");
	    	transf.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doc.getDoctype().getSystemId());
	        
	        DOMSource sorgente = new DOMSource(doc);
	        StreamResult out = new StreamResult(path);
	        transf.transform(sorgente, out);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
