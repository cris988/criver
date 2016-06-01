/**
 * Argenta Christian
 * Speranza Cristian
 * Gennaio 2011
 * Copyright
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Node;


public class Search extends HttpServlet {
	
	HttpSession session;
	PrintWriter out;
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException 
	{
		session = request.getSession(false);

		if (session != null) 
		{
			
			// Recupera la parola da cercare
			String parola = (String) request.getParameter("search");
			
			// Carica l'xml
			Node xml = Util.readXML((String)getServletContext().getAttribute("listino"));
			
			// // Cerca la parola
			ArrayList<Node> listNode = new ArrayList<Node>();
			searchValue(listNode, xml, parola);
			
			getServletContext().setAttribute("risultati", listNode);
			response.sendRedirect("Core?20");
		} 
		else
		{
			response.sendRedirect("Login");
		}
		
	}
	
	public static void searchValue(ArrayList<Node> listNode,
			Node node, String value)
	{
		/*
		 * CERCA IN RICORSIONE TUTTI I NODI CHE HANNO IL VALUE
		 */
		
		/*
		 *  Cerca con espressione regolare
		 *  . qualsiasi carattere
		 *  * 0 o più volte
		 */
		Pattern pattern = Pattern.compile(".*"+value+".*");
		
		searchValue_helper(listNode, node, value, pattern);
	}
		
	private static void searchValue_helper(ArrayList<Node> listNode,
			Node node, String value, Pattern pattern)
	{
		if ( node != null )
		{
			if ( node.getNodeType() == Node.TEXT_NODE )
			{
				Matcher match = pattern.matcher(node.getNodeValue());
				if ( match.matches() )
				{
					listNode.add(node);
				}
			}
			else
			{
				Node sibling = node.getNextSibling();
				Node child = node.getFirstChild();
				searchValue_helper(listNode, child, value, pattern);
				searchValue_helper(listNode, sibling, value, pattern);
			}
		}
	}
}