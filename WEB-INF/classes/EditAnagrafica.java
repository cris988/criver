/**
 * Argenta Christian
 * Speranza Cristian
 * Gennaio 2011
 * Copyright
 */

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.servlet.*;
import javax.servlet.http.*;


import org.w3c.dom.*;
import org.xml.sax.SAXException;


public class EditAnagrafica extends HttpServlet
{
	HttpSession session;
	PrintWriter out;
	
	protected void doPost (HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException 
	{
		session = request.getSession(false);
		
		// Prende il correntista
		Node correntista = Util.readXML(getPath("correntisti"), "CodiceCliente", session.getAttribute("codice").toString());
		
		// Prende il doc dei xml
		Document doc_correntista = correntista.getOwnerDocument();
				
		Node node = correntista.getFirstChild();
		
		do
		{
			String name = node.getNodeName();
			String value = request.getParameter(name);
			
			node.getFirstChild().setNodeValue(value);
			node = node.getNextSibling();
			
		}
		while ( node!=null );
		
		//Domwalk.walk(correntista);
		
		Util.writeXML(doc_correntista, getPath("correntisti"));
		
		response.sendRedirect("Core?1");
	
	}
	
	private String getPath(String name)
	{
		// Prende il path dell'xml
		return (String) getServletContext().getAttribute(name);
	}
}