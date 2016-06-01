/**
 * Argenta Christian
 * Speranza Cristian
 * Gennaio 2011
 * Copyright
 */

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class AbilitaConto extends HttpServlet
{
	HttpSession session;
		
	PrintWriter out;
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException 
	{
		session = request.getSession(false);
		
		if (session != null) 
		{ 		
			if(session.getAttribute("admin").equals("true"))
			{
				
				//mettere a true l'attributo
				
				Node nodo = Util.readXML(getServletContext().getRealPath("login.xml"), "CodiceCliente", request.getParameter("cod"));
				System.out.println("");
				Document doc = nodo.getOwnerDocument();
				((Element)nodo).setAttribute("enabled", "true");
				Util.writeXML(doc, getServletContext().getRealPath("login.xml"));
			}
			else
			{
				session.invalidate();
				response.sendRedirect("LoginAdmin");				
			}
			response.sendRedirect("Admin?1");
			
		} 
		else
		{
			response.sendRedirect("LoginAdmin");	
		}
	}
}