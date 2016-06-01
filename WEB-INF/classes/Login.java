/**
 * Argenta Christian
 * Speranza Cristian
 * Gennaio 2011
 * Copyright
 */

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;


public class Login extends HttpServlet
{
	HttpSession session;
	
	public void init()
	{
		// Imposta le variabili con il path dei file
		
		String listino = getServletContext().getRealPath("listino.xml");
		String conti = getServletContext().getRealPath("conti.xml");
		String correntisti = getServletContext().getRealPath("correntisti.xml");
		String login = getServletContext().getRealPath("login.xml");
		
		getServletContext().setAttribute("listino", listino);
		getServletContext().setAttribute("conti", conti);
		getServletContext().setAttribute("login", login);
		getServletContext().setAttribute("correntisti", correntisti);
	}
	
	protected void doGet (HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException 
	{
		session = request.getSession(false);
		// Disabilita la cache
		/*response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setDateHeader("Expires", 0); // Proxies.*/
		if ( checkSession(session) )
		{
			homeHtml(response);
		}
		else
		{
			loginHtml(request, response);				
		}	
	}
	
	protected void doPost(HttpServletRequest request, 
 			 HttpServletResponse response) throws ServletException, IOException
 	{
		session = request.getSession(false);
		if (session == null) {
			boolean verificato = checkUser(request, response);
	        if ( verificato == false)
	        	loginHtml(request, response);
	    	else
	    		homeHtml(response);
		} else {
			if(session.getAttribute("admin").equals("false"))
			{
				homeHtml(response);
			}
			else
			{
				session.invalidate();
				loginHtml(request, response);				
			}
		}
    }
	
	private boolean checkUser(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		// Salva dati inseriti
    	String inputUser = request.getParameter("user");
    	String inputPwd = request.getParameter("pwd");
		
		if ( inputUser != "" && inputPwd != "" )
		{
	    	// Li cerca nell'xml
			String filename = (String) getServletContext().getAttribute("login");
			Node username = Util.readXML(filename, "Username", inputUser);
			
			if ( username != null)
			{
				Node user = Util.getParentNode(username, "User");
				
				// Se la password combacia
				if ( user.getAttributes().item(0).getNodeValue().equals("true") && 
						username != null &&
						Util.getNodeValue(username, "Password").equals(inputPwd) )
				{
	
					session = request.getSession();
					session.setAttribute("username", Util.getNodeValue(username, "Username"));	
		    		session.setAttribute("codice", Util.getNodeValue(username, "CodiceCliente"));
		    		session.setAttribute("admin", "false");
	
		    		return true;
				}
				else
					return false;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	private void loginHtml (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		getServletContext().getRequestDispatcher("/login.html").forward(request, response);
	}
	
	private void homeHtml (HttpServletResponse response) throws IOException, ServletException
	{
		response.sendRedirect("Core");
	}
	
	private boolean checkSession(HttpSession session)
	{
		if ( session != null )
		{
			if ( session.getAttribute("admin").equals("false")
				//getServletContext().getAttribute("admin").equals("false")
				)
				return true;
			else
			{
				session.invalidate();
				return false;
			}
		}
		else
			return false;
	}
 }
