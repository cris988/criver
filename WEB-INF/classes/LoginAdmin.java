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

public class LoginAdmin extends HttpServlet
{
	HttpSession session;
	
	public void init()
	{
		// Imposta le variabili con il path dei file
		
		String listino = getServletContext().getRealPath("listino.xml");
		String conti = getServletContext().getRealPath("conti.xml");
		String correntisti = getServletContext().getRealPath("correntisti.xml");
		String login = getServletContext().getRealPath("loginAdmin.xml");
		String loginuser = getServletContext().getRealPath("login.xml");
		
		getServletContext().setAttribute("listino", listino);
		getServletContext().setAttribute("conti", conti);
		getServletContext().setAttribute("loginAdmin", login);
		getServletContext().setAttribute("login", loginuser);
		getServletContext().setAttribute("correntisti", correntisti);
	}
	
	protected void doGet (HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException 
	{
		session = request.getSession(false);
		// Disabilita la cache
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setDateHeader("Expires", 0); // Proxies.
		if (session == null) {
			loginHtml(response);
		} else {
			if(session.getAttribute("admin").equals("true"))
			{
				homeHtml(response);
			}
			else
			{
				session.invalidate();
				loginHtml(response);				
			}
		}
	}
	
	protected void doPost(HttpServletRequest request, 
 			 HttpServletResponse response) throws ServletException, IOException
 	{
		session = request.getSession(false);
		if (session == null) {
			boolean verificato = checkUser(request, response);
	        if ( verificato == false)
	        	loginHtml(response);
	    	else
	    		homeHtml(response);
		} else {
			if(session.getAttribute("admin").equals("true"))
			{
				homeHtml(response);
			}
			else
			{
				session.invalidate();
				loginHtml(response);				
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
			String filename = (String) getServletContext().getAttribute("loginAdmin");
			Node user = Util.readXML(filename, "Username", inputUser);
			
			// Se la password combacia
			if ( user != null && Util.getNodeValue(user, "Password").equals(inputPwd) )
			{
				session = request.getSession();
				session.setAttribute("username", Util.getNodeValue(user, "Username"));
	    		session.setAttribute("admin", "true");
				return true;
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
	
	private void loginHtml (HttpServletResponse response) throws IOException
	{
	    response.sendRedirect("admin/loginAdmin.html");
	}
	
	private void homeHtml (HttpServletResponse response) throws IOException, ServletException
	{
		response.sendRedirect("Admin");
	}
 }
