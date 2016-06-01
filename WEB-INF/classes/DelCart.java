/**
 * Argenta Christian
 * Speranza Cristian
 * Gennaio 2011
 * Copyright
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.http.*;

public class DelCart extends HttpServlet
{
	HttpSession session;
	PrintWriter out;
	protected void doGet (HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException 
	{
		session = request.getSession(false);
		if (session != null) 
		{ 
			 
			// aggiorno campi cookie
			Enumeration param = request.getParameterNames();
			int numazioni=0;
			if(session.getAttribute("numazioni")!=null)
			{
				numazioni = Integer.parseInt(session.getAttribute("numazioni").toString());
			}
			for(int i=1;i<=numazioni;i++)
			{
				session.removeAttribute("Azione"+numazioni);
				session.removeAttribute("Azione"+numazioni+"_num");
			}
			session.removeAttribute("numazioni");
			
			response.sendRedirect("Core");
		}
	}
}