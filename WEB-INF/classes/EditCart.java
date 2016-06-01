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

public class EditCart extends HttpServlet
{
	HttpSession session;
	PrintWriter out;
	
	protected void doPost (HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException 
	{
		session = request.getSession(false);
		if (session != null) 
		{ 
			// aggiorno campi cookie
			
			Enumeration param = request.getParameterNames();
			String code;
			int value;
			int numazioni=0;
			if(session.getAttribute("numazioni")!=null)
			{
				numazioni = Integer.parseInt(session.getAttribute("numazioni").toString());
			}
			else
			{
				session.setAttribute("numazioni", "0");
			}
			while (param.hasMoreElements())
			{
				
				String key = (String) param.nextElement();
				/*System.out.println(key);
				System.out.println(key.substring(0,4));*/
				
				if( key.substring(0,4).equals("txt_") )
				{
					code=key.substring(4);
					value=Integer.parseInt(request.getParameter(key));
					boolean find_other = false;
					if(value!=0)
					{
						for(int i=1;i<=numazioni;i++)
						{
							if(code.equals(session.getAttribute("Azione"+i)))
							{
								int old = (Integer) session.getAttribute("Azione"+i+"_num");
								session.setAttribute("Azione"+i+"_num", (value+old));
								find_other=true;
							}
						}
						if(!find_other)
						{
							
							numazioni ++;
							session.setAttribute("numazioni", numazioni);
							session.setAttribute("Azione"+numazioni, code);
							session.setAttribute("Azione"+numazioni+"_num", value);
						}
					}
				}
			}
			response.sendRedirect("Core?3");
		}
	}
	
	protected void doGet (HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException 
	{
		session = request.getSession(false);
		if (session != null) 
		{ 
			
			// aggiorno campi cookie
			int mode = Integer.parseInt(request.getParameter("m"));
			int numAzioni = Integer.parseInt(session.getAttribute("numazioni").toString());

			int i = 0;
			if(request.getParameter("i")!=null)
			{
				i = Integer.parseInt(request.getParameter("i"));
			}
			switch (mode)
			{
				case 0:
					// EDIT
					int tolti=0;
					int[] togli =new int [numAzioni];
					for(int j=1;j<=numAzioni;j++)
					{
						int valore=Integer.parseInt(request.getParameter("valore"+j));
						if(valore==0)
						{
							deleteAzione(numAzioni,j,false);
							togli[tolti]=j;
							tolti++;
							
						}
						else
						{
							session.setAttribute("Azione"+j+"_num", valore);
						}
					}
					for(int x=0;x<tolti;x++)
					{
						session.removeAttribute("Azione"+togli[x]);
						session.removeAttribute("Azione"+togli[x]+"_num");
					}
					numAzioni=numAzioni-tolti;
					session.setAttribute("numazioni",(numAzioni));
					break;
				case 1:
					// DELETE
					deleteAzione(numAzioni,i,true);
					break;
				default:
					// NOTHING
			}
			response.sendRedirect("Core?3");
		}
	}
	private void deleteAzione(int numAzioni, int i, boolean elimina)
	{
		for(int j=i;j<numAzioni;j++)
		{
			session.setAttribute("Azione"+j, session.getAttribute("Azione"+(j+1)));
			session.setAttribute("Azione"+j+"_num", session.getAttribute("Azione"+(j+1)+"_num"));
		}
		if(elimina)
		{
			session.removeAttribute("Azione"+numAzioni);
			session.removeAttribute("Azione"+numAzioni+"_num");
			session.setAttribute("numazioni",(numAzioni-1));
		}
	}
}