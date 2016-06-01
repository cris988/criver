/**
 * Argenta Christian
 * Speranza Cristian
 * Gennaio 2011
 * Copyright
 */

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;

public class Admin extends HttpServlet
{
	HttpSession session;
		
	PrintWriter out;
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException 
	{
		session = request.getSession(false);
		
		if (session != null) 
		{ 		
			if(session.getAttribute("admin").equals("true"))
			{
				headerHtml(request, response, findSection(request.getQueryString()));
				
				writeContent(request, response);
				
				footerHtml(request,response);
			}
			else
			{
				session.invalidate();
				loginRedirect(response);				
			}
			
		} 
		else
		{
			loginRedirect(response);
		}
	}
	
	private void contentHome(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		response.setContentType( "text/html" );
	    out = response.getWriter();
		out.println("<b>BENVENUTO</b> " +
				session.getAttribute("username") +
				"<br>Attraverso il menù in alto puoi accedere alle varie funzionalit&agrave; del portale!!!");
	}
	
	private void adminUser(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
	    response.setContentType( "text/html" );
	    out = response.getWriter();
	    out.println("<div align=\"center\">" +
	    		"<table id=\"nuoviconti\">" +
	    		"<head>" +
	    		"<tr>" +
	    		"<th>Codice Utente</th>" +
	    		"<th>Nome</th>" +
	    		"<th>Cognome</th>" +
	    		"<th>Codice Conto</th>" +
	    		"<th>Saldo Iniziale</th>" +
	    		"<th></th>" +
	    		"</tr>" +
	    		"</thead>" +
	    		"<tbody>");
	    	    
	    /** Recupera i conti **/
	    NodeList login = Util.readXML(getPath("login"), "User");
	    int conteggio=0;
	    for(int i=0; i<login.getLength();i++)
	    {
	    	if(((Element)login.item(i)).getAttribute("enabled").equals("false"))
	    	{
	    		String usercode = Util.getNodeValue(login.item(i), "CodiceCliente");
	    		Node conto = Util.readXML(getPath("conti"), "CodiceCliente", usercode);
	    		Node correntista = Util.readXML(getPath("correntisti"), "CodiceCliente", usercode);
	    		String saldo = Util.getNodeValue(conto, "Saldo");
	    		String codiceconto = Util.getNodeValue(conto, "CodiceConto");
	    		String nome = Util.getNodeValue(correntista, "Nome");
	    		String cognome = Util.getNodeValue(correntista, "Cognome");
	    		out.println("<tr>" +
	    				"<td>" + usercode +
	    				"</td>" + 
	    				"<td>" + nome +
	    				"</td>" +
	    				"<td>" + cognome +
	    				"</td>" +
	    				"<td>" + codiceconto +
	    				"</td>" +
	    				"<td>" + saldo +
	    				"</td>" +
	    				"<td><form action=\"AbilitaConto\" method=\"post\"><input type=\"hidden\" name=\"cod\" value=\""+usercode+"\"><input type=\"submit\" value=\"ABILITA CONTO\"></form>" +
	    				"</td>" +
	    				"</tr>");
	    		conteggio ++;
	    	}
	    	
	    }
	    if(conteggio==0) out.println("<td colspan=\"6\" align=\"center\"><em>Nessuna richiesta di apertura conto...</em></td>");
	    
	    out.println("</tbody>" +
	    		"</table>" +
	    		"</div>");
	}
	

	private void addTitolo(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		response.setContentType( "text/html" );
	    out = response.getWriter();
	    out.println("<script type=\"text/javascript\" src=\"javascript/scriptNuovoUtente.js\"></script>" +
	    		"<form onSubmit=\"return controllo();\" action=\"EditList\" method=\"get\">" +
	    		"<table>" +
	    		"<input type=\"hidden\" name= \"m\" value=\"0\">" +
	    		"<tr><td>Nome</td><td><input type=\"text\" name=\"Nome\"></td></tr>" +
	    		"<tr><td>Disponibilita</td><td><input type=\"text\" name=\"Disponibilita\"></td></tr>" +
	    		"<tr><td>Prezzo</td><td><input type=\"text\" name=\"Prezzo\"></td></tr>" +
	    		"<tr><td>Categoria</td><td><input type=\"text\" name=\"Categoria\"></td></tr>" +
	    		"</table>" +
	    		"<input type=\"submit\" value=\"AGGIUNGI\">"+
	    		"</form>");
	}
	private void adminListino(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		// Recupera tutte le azioni
		NodeList list = Util.readXML(getPath("listino"), "Azione");
		
    	response.setContentType( "text/html" );
	    out = response.getWriter();
	 	out.println("<script type=\"text/javascript\" src=\"javascript/scriptListino.js\"></script>");
	    
	 	out.println("<div align=\"center\">");

		if ( list != null)
		{
		 	out.println("<h2>LISTINO</h2>");
		 	
		 	out.println("<form action=\"EditList\" method=\"post\">");
		 					
		 	out.println("<table id=\"listino\">");
		 	
			out.println("<thead>" +
							"<tr>" +
								"<th scope=\"col\">Codice</th>" +
								"<th scope=\"col\">Nome</th>" +
								"<th scope=\"col\">Prezzo</th>" +
								"<th scope=\"col\">Disponibilità</th>" +
								"<th scope=\"col\">Categoria</th>" +
								"<th scope=\"col\"></th>" +
							"</tr>" +
						"</thead>");
			
			out.println("<tbody>");
			/*
			 * Legge le azioni del listino e le stampa
			 */
			for ( int i=0; i<list.getLength(); i++)
			{
				Node azione = list.item(i);
				
				String codAzione = Util.getNodeValue(azione, "CodiceAzione");
				String disponibilita = Util.getNodeValue(azione, "Disponibilita");

				out.println("<tr>" +
								"<td>" + codAzione + "</td>" +
								"<input type=\"hidden\" name=\"cod"+i+"\" value=\""+codAzione+"\">" +
								"<td><input name=\"nome"+i+"\" id=\"nome"+i+"\" type=\"text\" size=\"10\" value=\"" + Util.getNodeValue(azione, "Nome") + "\"></td>" +
								"<td><input name=\"prezzo"+i+"\" id=\"prezzo"+i+"\" type=\"text\" size=\"10\" value=\"" + Util.getNodeValue(azione, "Prezzo") + "\"></td>" +
								"<td><input name=\"disponibilita"+i+"\" id=\"disponibilita"+i+"\" type=\"text\" size=\"10\" value=\"" + disponibilita + "\"></td>" +
								"<td><input name=\"categoria"+i+"\" id=\"categoria"+i+"\" type=\"text\" size=\"10\" value=\"" + Util.getNodeValue(azione, "Categoria") + "\"></td>" +
								"<td align=\"center\"><a href=\"EditList?m=1&cod="+codAzione+"\"><img  id=\"del_ico\"src=\"images/table/carrello/del.png\" title=\"Elimina\"></a></td>" +
							"</tr>");
			}
			out.println("</tbody>"+
					"<tfoot>"+
					"<tr>"+
					"<td colspan=\"6\" align=\"right\">"+
					"<em>Modificare il listino quindi salvare</em>"+
					"<input type=\"reset\" value=\"RESET\">" +
					"<input type=\"button\" onclick=\"window.location='Admin?55'\" value=\"NUOVO TITOLO\">" +
					"<input type=\"submit\" value=\"SALVA LISTINO\">"+
					"</td>"+
					"</tr>"+
					"</tfoot>");
			
			out.println("</table>");
						
			out.println("</form>");

		}
		else
	    {
	    	out.println("<span>Si &egrave; verificato un errore, si prega di riprovare</span>");
	    }
		out.println("</div>");	
	}
	
	private void headerHtml (HttpServletRequest request,
			HttpServletResponse response,
			String sezione) throws IOException, ServletException
	{
		response.setContentType( "text/html" );
	    out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>CRIVER ONLINE BANKING - " + sezione + "</title>");
		out.println("<link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\" media=\"all\">");
		out.println("</head>");
		out.println("<body align=\"center\">");
		out.println("<div id=container>");
		out.println("<img src=\"images/logo.png\">");
		out.println("<br>");
		menuHtml(request,response);
		out.println("<br>");
		out.println("<div id=\"content\">");
	}
	
	private void menuHtml (HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/admin/menu.html");
	    dispatcher.include(request,response);
	}
	
	private void footerHtml (HttpServletRequest request,
			HttpServletResponse response) throws IOException
	{
		response.setContentType( "text/html" );
	    out = response.getWriter();
	    out.println("</div>" +
	    			"</div>" +
	    			"</body>" + 
	    			"</html>");
		out.close();
	}
	
	private void loginRedirect (HttpServletResponse response) throws IOException
	{
		response.sendRedirect("LoginAdmin");
	}
	
	private void writeContent(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		/*
		 * SCRIVE IL CONTENUTO DELLA PAGINA SUL RESPONSE
		 * home = 0
		 * portafoglio = 1
		 * listino = 2
		*/
		// Prende sezione dall'url
		int sez;
		try
		{
			sez = Integer.parseInt(request.getQueryString());
		}
		catch (Exception e){
			sez = 0;
		}
		switch ( sez )
		{
			case 0:
				contentHome(request, response);
				break;
			case 1:
				adminUser(request, response);
				break;
			case 2:
				adminListino(request, response);
				break;
			case 10:
				logout(request, response);
				break;
			case 55:
				addTitolo(request, response);
				break;
			default:
				contentHome(request, response);
		}
	}
	
	private String findSection(String sezione)
	{
		int sez;
		try
		{
			sez = Integer.parseInt(sezione);
		}
		catch ( Exception e)
		{
			sez = 0;
		}
		
		switch ( sez )
		{
			case 0:
				return "Home";
			case 1:
				return "Portafoglio";
			case 2:
				return "Listino";
			case 3:
				return "Carrello";
			case 10:
				return "Logout";
			default:
				return "Home";
		}
	}
	
	private void logout(HttpServletRequest request,
			HttpServletResponse response) throws IOException
	{
		session.invalidate();
		loginRedirect(response);
	}
	private String getPath(String name)
	{
		// Prende il path dell'xml
		return (String) getServletContext().getAttribute(name);
	}
}