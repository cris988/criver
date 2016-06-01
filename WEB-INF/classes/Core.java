/**
 * Argenta Christian
 * Speranza Cristian
 * Gennaio 2011
 * Copyright
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;

public class Core extends HttpServlet
{
	HttpSession session;
	PrintWriter out;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException 
	{
		session = request.getSession(false);		
		if ( checkSession(session) )
		{
			headerHtml(request, response, findSection(request.getQueryString()));
			
			writeContent(request, response);
			
			footerHtml(request,response);
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
		out.println("<h2>BENVENUTO</h2> " +
				session.getAttribute("username") +
				"<br>Attraverso il men� in alto puoi accedere alle varie funzionalit&agrave; del portale!!!");
	}
	
	public void headerHtml (HttpServletRequest request,
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
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/menu.html");
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
	
	private void contentPortafoglio(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
	    // Informazioni relative a un solo CONTO;
	    Node conto = null;
	    // Informazioni relative a un solo CORRENTISTA;
	    Node correntista = null;

    	response.setContentType( "text/html" );
	    out = response.getWriter();
	    out.println("<div align=\"center\">");
	    
	    String codiceCorrentista = (String)session.getAttribute("codice");
	    
	    /** Recupera il correntista **/
	    correntista = Util.readXML(getPath("correntisti"), "CodiceCliente", codiceCorrentista);    
	    
	    if ( correntista != null )
	    {
	    	/** Recupera il conto del correntista **/
	    	conto = Util.readXML(getPath("conti"), "CodiceCliente", codiceCorrentista);
	    	
	    	if ( conto!= null )
	    	{		
				// Dati del conto
				String codiceConto = Util.getNodeValue(conto, "CodiceConto");
				String saldo = Util.getNodeValue(conto, "Saldo");
				
				// Azioni del correntista
				NodeList azioniCorr = ((Element)conto).getElementsByTagName("Azione");

				float valoreAzioni = 0;
				float saldoFinale;
				
    			out.println("<h2>PORTAFOGLIO</h2>");
    			
    			out.println("<table id=\"ver-zebra\">" +
    					"<colgroup>" +
					   	"<col class=\"vzebra-odd\" />" +
						"<col class=\"vzebra-even\" />"+
						"</colgroup>"+
    					"<thead>" +
    					"<tr>" +
    					"<th scope=\"col\" id=\"vzebra-comedy\">ANAGRAFICA</th>" +
    					"<th scope=\"col\" id=\"vzebra-comedy\">CONTO</th>" +
    					"</tr>" +
    					"</thead>" +
    					"<tbody>" +
    					"<tr>" +
    					"<td valign=\"top\">");
    			
	    		/** LETTURA E STAMPA DATI ANAGRAFICI **/
				out.println("<table id=\"anagrafica\">");
				
				for ( int j=0; j < correntista.getChildNodes().getLength() - 1; j++ )
				{
					//Nome campo + valore campo
					String dataName = correntista.getChildNodes().item(j+1).getNodeName();
					String data = Util.getNodeValue(correntista.getChildNodes().item(j+1));

					out.println("<tr>" + 
							"<td>" + 
							dataName + ": " +
							"</td>" +
							"<td>" +
							data +
							"</td>"+
							"</tr>");
				}
				out.println("</table>" +
						"</td>" +
						"<td valign=\"top\">");
				
				
				/** LETTURA E STAMPA DATI CONTO **/

				/* Calcola il valore azioni
				 * Quantit� * prezzo 
				 */
				for ( int j=0; j<azioniCorr.getLength(); j++)
				{
					float prezzo = 0;
					float quantita;
					
					Node azioneCorr = azioniCorr.item(j);
					
					Node azioneListino = Util.readXML(getPath("listino"), "CodiceAzione", Util.getNodeValue(azioneCorr, "CodiceAzione"));		
					
					prezzo = Float.parseFloat(Util.getNodeValue(azioneListino, "Prezzo"));
					quantita = Float.parseFloat(Util.getNodeValue(azioneCorr, "Quantita"));
					
					valoreAzioni += prezzo * quantita;
				}
				
				saldoFinale = Float.parseFloat(saldo) + valoreAzioni;
				
				out.println("<table id=\"conto\">");
								
				out.println("<tbody>" +
								"<tr>" +
									"<td>Codice conto</td>" +
									"<td>" + codiceConto + "</td>" +
								"</tr>" +
								"<tr>" +
									"<td>Saldo liquido</td>" +
									"<td>" + saldo + "</td>" +
								"</tr>" +
								"<tr>" +
									"<td>Valore azioni</td>" +
									"<td>" + valoreAzioni + "</td>" +
								"</tr>" + 
								"<tr>" +

								"<tr>" +
									"<th>SALDO TOTALE</th>" +
									"<td>" + saldoFinale + "</td>" +
								"</tr>" +
							"</tbody>");
								
				out.println("</table>" +
						"</td>" +
						"</tr>" +
						"</tbody>" +
						"</table>" +
						"<button type=\"button\" onclick=\"window.location='Core?66'\">MODIFICA DATI</button>");

				out.println("<hr><h3>Titoli / Azioni</h3>");
				
				out.println("<table id=\"listino\">");
				
				out.println("<thead>" +
								"<tr>" +
									"<th>Codice</th>" +
									"<th>Nome</th>" +
									"<th>Quantit&agrave;</th>" +
								"</tr>" +
							"</thead>");
				
				/*
				 * Legge le azioni del correntista e le stampa
				 */
				for ( int j=0; j<azioniCorr.getLength(); j++)
				{
					
					Node azioneCorr = azioniCorr.item(j);
					
					Node azioneListino = Util.readXML(getPath("listino"), "CodiceAzione", Util.getNodeValue(azioneCorr, "CodiceAzione"));
					String codiceAzione = Util.getNodeValue(azioneCorr, "CodiceAzione");
					String quantitaAzione = Util.getNodeValue(azioneCorr, "Quantita");
					String nomeAzione = Util.getNodeValue(azioneListino, "Nome");
					
					out.println("<tbody>" +
									"<tr>" +
										"<td>" + codiceAzione +	"</td>" +
										"<td>" + nomeAzione + "</td>" +
										"<td>" + quantitaAzione + "</td>" +
									"</tr>" +
								"</tbody>");
				}

				out.println("</table>");
	    	}
			else
		    {
		    	out.println("<span>Si &egrave; verificato un errore, si prega di riprovare</span>");
		    }
	    }
	    else
	    {
	    	out.println("<span>Si &egrave; verificato un errore, si prega di riprovare</span>");
	    }
	    out.println("</div>");
	}

	private void editingAnagrafica(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
	    // Informazioni relative a un solo CONTO;
	    Node conto = null;
	    // Informazioni relative a un solo CORRENTISTA;
	    Node correntista = null;

    	response.setContentType( "text/html" );
	    out = response.getWriter();
	    out.println("<div align=\"center\">");
	    
	    String codiceCorrentista = (String)session.getAttribute("codice");
	    
	    /** Recupera il correntista **/
	    correntista = Util.readXML(getPath("correntisti"), "CodiceCliente", codiceCorrentista);    
	    
	    if ( correntista != null )
	    {
	    	/** Recupera il conto del correntista **/
	    	conto = Util.readXML(getPath("conti"), "CodiceCliente", codiceCorrentista);
	    	
	    	if ( conto!= null )
	    	{		
				// Dati del conto
				String codiceConto = Util.getNodeValue(conto, "CodiceConto");
				String saldo = Util.getNodeValue(conto, "Saldo");
				
				// Azioni del correntista
				NodeList azioniCorr = ((Element)conto).getElementsByTagName("Azione");

				float valoreAzioni = 0;
				float saldoFinale;
				
    			out.println("<h2>PORTAFOGLIO</h2>");
    			
    			out.println("<form action=\"EditAnagrafica\" method=\"post\">" +
    					"<input type=\"hidden\" name=\"CodiceCliente\" value=\""+Util.getNodeValue(correntista.getChildNodes().item(0))+"\">" +
    					"<table id=\"ver-zebra\">" +
    					"<colgroup>" +
					   	"<col class=\"vzebra-odd\" />" +
						"<col class=\"vzebra-even\" />"+
						"</colgroup>"+
    					"<thead>" +
    					"<tr>" +
    					"<th scope=\"col\" id=\"vzebra-comedy\">ANAGRAFICA</th>" +
    					"<th scope=\"col\" id=\"vzebra-comedy\">CONTO</th>" +
    					"</tr>" +
    					"</thead>" +
    					"<tbody>" +
    					"<tr>" +
    					"<td valign=\"top\">");
    			
	    		/** LETTURA E STAMPA DATI ANAGRAFICI **/
				out.println("<table id=\"anagrafica\">");
				
				for ( int j=0; j < correntista.getChildNodes().getLength() - 1; j++ )
				{
					//Nome campo + valore campo
					String dataName = correntista.getChildNodes().item(j+1).getNodeName();
					String data = Util.getNodeValue(correntista.getChildNodes().item(j+1));

					out.println("<tr>" + 
							"<td>" + 
							dataName +
							"</td>" +
							"<td>" +
							"<input name=\""+dataName+"\"type=\"text\" value=\""+data +"\">"+
							"</td>"+
							"</tr>");
				}
				out.println("</table>" +
						"</td>" +
						"<td valign=\"top\">");
				
				
				/** LETTURA E STAMPA DATI CONTO **/

				/* Calcola il valore azioni
				 * Quantita * prezzo 
				 */
				for ( int j=0; j<azioniCorr.getLength(); j++)
				{
					float prezzo = 0;
					float quantita;
					
					Node azioneCorr = azioniCorr.item(j);
					
					Node azioneListino = Util.readXML(getPath("listino"), "CodiceAzione", Util.getNodeValue(azioneCorr, "CodiceAzione"));		
					
					prezzo = Float.parseFloat(Util.getNodeValue(azioneListino, "Prezzo"));
					quantita = Float.parseFloat(Util.getNodeValue(azioneCorr, "Quantita"));
					
					valoreAzioni += prezzo * quantita;
				}
				
				saldoFinale = Float.parseFloat(saldo) + valoreAzioni;
				
				out.println("<table id=\"conto\">");
								
				out.println("<tbody>" +
								"<tr>" +
									"<td>Codice conto</td>" +
									"<td>" + codiceConto + "</td>" +
								"</tr>" +
								"<tr>" +
									"<td>Saldo liquido</td>" +
									"<td>" + saldo + "</td>" +
								"</tr>" +
								"<tr>" +
									"<td>Valore azioni</td>" +
									"<td>" + valoreAzioni + "</td>" +
								"</tr>" + 
								"<tr>" +

								"<tr>" +
									"<th>SALDO TOTALE</th>" +
									"<td>" + saldoFinale + "</td>" +
								"</tr>" +
							"</tbody>");
								
				out.println("</table>" +
						"</td>" +
						"</tr>" +
						"</tbody>" +
						"</table>" +
						"<input type=\"reset\" value=\"RESET\">" +
						"<input type=\"submit\" value=\"CONFERMA\">" +
						"</form>");

				out.println("<hr>");
				
				out.println("<table id=\"portafoglio_azioni\">");
				
				out.println("<thead>" +
								"<tr>" +
									"<th>Codice</th>" +
									"<th>Nome</th>" +
									"<th>Quantit&agrave;</th>" +
								"</tr>" +
							"</thead>");
				
				/*
				 * Legge le azioni del correntista e le stampa
				 */
				for ( int j=0; j<azioniCorr.getLength(); j++)
				{
					
					Node azioneCorr = azioniCorr.item(j);
					Node azioneListino = Util.readXML(getPath("listino"), "CodiceAzione", Util.getNodeValue(azioneCorr, "CodiceAzione"));
					String codiceAzione = Util.getNodeValue(azioneCorr, "CodiceAzione");
					String quantitaAzione = Util.getNodeValue(azioneCorr, "Quantita");
					String nomeAzione = Util.getNodeValue(azioneListino, "Nome");
					
					out.println("<tbody>" +
									"<tr>" +
										"<td>" + codiceAzione +	"</td>" +
										"<td>" + nomeAzione + "</td>" +
										"<td>" + quantitaAzione + "</td>" +
									"</tr>" +
								"</tbody>");
				}

				out.println("</table>");
	    	}
			else
		    {
		    	out.println("<span>Si &egrave; verificato un errore, si prega di riprovare</span>");
		    }
	    }
	    else
	    {
	    	out.println("<span>Si &egrave; verificato un errore, si prega di riprovare</span>");
	    }
	    out.println("</div>");
	}


	private void contentListino(HttpServletRequest request,
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
		 	
		 	out.println("<form action=\"Search\" method=\"get\">" +
	 				"Cerca valore:" +
		 			"<input type=\"text\" id=\"search\" name=\"search\">" +
		 			"<input type=\"submit\" value=\"Cerca\">" +
		 			"<input type=\"button\" value=\"Annulla\" onclick=\"window.location='Core?2'\">" +
		 			"</form>");
	 	
		 	NodeList list2 = Util.readXML(getPath("listino"), "Categoria");
			String[] categorie = new String [list.getLength()];
			int count=0;
			for ( int i=0; i<list2.getLength(); i++)
			{
				String categoria = list2.item(i).getTextContent();
				if(i==0) 
				{
					categorie[i]=categoria;
					count++;
				}
				else
				{
					int control=0;
					for(int j=0;j<count;j++)
					{
						if(!categoria.equals(categorie[j]))
						{
							control ++;
						}
						if(control==(count))
						{
							categorie[count]=categoria;
							count++;
						}
					}
				}
				
			}
		 	
		 			out.println("<form action=\"Search\" method=\"get\">" +
	 				"<b>Ricerca per categoria:</b>" +
		 			"<select id=\"search\" name=\"search\">" +
		 			"<option value=\"\" selected=\"selected\"></option>");
		 			for(int i=0;i<count;i++)
		 			{
		 				out.println("<option value=\""+categorie[i]+"\">"+categorie[i]+"</option>");
		 			}
		 			out.println("<input type=\"submit\">" +
		 			"</form>");
		 	
		 	out.println("<form action=\"EditCart\" method=\"post\">");
		 					
		 	out.println("<table id=\"listino\">");
		 	
			out.println("<thead>" +
							"<tr>" +
								"<th scope=\"col\">Codice</th>" +
								"<th scope=\"col\">Categoria</th>" +
								"<th scope=\"col\">Nome</th>" +
								"<th scope=\"col\">Prezzo</th>" +
								"<th scope=\"col\">Disponibilit&agrave;</th>" +
								"<th scope=\"col\">Quantit&agrave;</th>" +
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

		    	String textboxName="txt_" + codAzione;
		    	String checkboxName="chk_" + codAzione;

				out.println("<tr>" +
								"<td>" + codAzione + "</td>" +
								"<td>" + Util.getNodeValue(azione, "Categoria") + "</td>" +
								"<td>" + Util.getNodeValue(azione, "Nome") + "</td>" +
								"<td>" + Util.getNodeValue(azione, "Prezzo") + "</td>" +
								"<td>" + disponibilita + "</td>" +
								"<td>" +
									"<input name=\""+checkboxName+"\" id=\""+checkboxName+"\" type=\"checkbox\" onclick=\""+textboxName+".disabled=!this.checked; if(this.checked==true){"+textboxName+".focus();}\" >" +
									"<input name=\""+textboxName+"\" id=\""+textboxName+"\" type=\"text\" size=\"5\" value=\"0\" disabled onkeyup=\"setMaximumValue(this.id, " + disponibilita + ")\" onblur=\"controllo(this.id, "+checkboxName+".id);\" >" +
								"</td>" +
							"</tr>");
			}
			out.println("</tbody>"+
					"<tfoot>"+
					"<tr>"+
					"<td colspan=\"6\" align=\"right\">"+
					"<em>Selezionare il numero di azioni che si intende acquistare &nbsp;</em>"+
					"<input type=\"submit\" value=\"Aggiungi al Carrello\">"+
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
	
	private void contentListinoSearch(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		// Recupera tutte le azioni
		@SuppressWarnings("unchecked")
		ArrayList<Node> list = (ArrayList<Node>) getServletContext().getAttribute("risultati");
		
    	response.setContentType( "text/html" );
	    out = response.getWriter();
	 	out.println("<script type=\"text/javascript\" src=\"javascript/scriptListino.js\"></script>");
	    
	 	out.println("<div align=\"center\">");

	 	out.println("<h2>LISTINO</h2>");
	 	
	 	out.println("<form action=\"Search\" method=\"get\">" +
	 				"Cerca valore:" +
		 			"<input type=\"text\" id=\"search\" name=\"search\">" +
		 			"<input type=\"submit\" value=\"Cerca\">" +
		 			"<input type=\"button\" value=\"Annulla\" onclick=\"window.location='Core?2'\">" +
		 			"</form>");
	 	
	 	out.println("<form action=\"EditCart\" method=\"post\">");
	 					
	 	out.println("<table id=\"listino\">");
	 	
		out.println("<thead>" +
						"<tr>" +
							"<th scope=\"col\">Codice</th>" +
							"<th scope=\"col\">Categoria</th>" +
							"<th scope=\"col\">Nome</th>" +
							"<th scope=\"col\">Prezzo</th>" +
							"<th scope=\"col\">Disponibilit�</th>" +
							"<th scope=\"col\">Quantit&agrave;</th>" +
						"</tr>" +
					"</thead>");
		
			if ( list.size() != 0)
			{
				out.println("<tbody>");
				
				/*
				 * Legge le azioni del listino e le stampa
				 */
				for ( int i=0; i<list.size(); i++)
				{
					Node azione = Util.getParentNode(list.get(i), "Azione");
					
					String codAzione = Util.getNodeValue(azione, "CodiceAzione");
					String disponibilita = Util.getNodeValue(azione, "Disponibilita");
	
			    	String textboxName="txt_" + codAzione;
			    	String checkboxName="chk_" + codAzione;
	
					out.println("<tr>" +
									"<td>" + codAzione + "</td>" +
									"<td>" + Util.getNodeValue(azione, "Categoria") + "</td>" +
									"<td>" + Util.getNodeValue(azione, "Nome") + "</td>" +
									"<td>" + Util.getNodeValue(azione, "Prezzo") + "</td>" +
									"<td>" + disponibilita + "</td>" +
									"<td>" +
										"<input name=\""+checkboxName+"\" id=\""+checkboxName+"\" type=\"checkbox\" onclick=\""+textboxName+".disabled=!this.checked; if(this.checked==true){"+textboxName+".focus();}\" >" +
										"<input name=\""+textboxName+"\" id=\""+textboxName+"\" type=\"text\" size=\"5\" value=\"0\" disabled onkeyup=\"setMaximumValue(this.id, " + disponibilita + ")\" onblur=\"controllo(this.id, "+checkboxName+".id);\" >" +
									"</td>" +
								"</tr>");
				}
				
			out.println("</tbody>");
			
			out.println("<tfoot>"+
					"<tr>"+
					"<td colspan=\"6\" align=\"right\">"+
					"<em>Selezionare il numero di azioni che si intende acquistare &nbsp;</em>"+
					"<input type=\"submit\" value=\"Aggiungi al Carrello\">"+
					"</td>"+
					"</tr>"+
					"</tfoot>");
			}
			else
		    {
				out.println("<tbody>" + 
								"<tr>"+
									"<td colspan=\"6\" align=\"center\">"+
										"<span>Valore non trovato</span>" +
									"</td>" +
								"</tr>" + 
							"</tbody>");
		    }
			
			out.println("</table>");
						
			out.println("</form>");

		out.println("</div>");	
	}
	
	private void contentCarrello(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
    	response.setContentType( "text/html" );
	    out = response.getWriter();
	    out.println("<div align=\"center\">");
	    out.println("<h2>MODIFICA CARRELLO</h2>");
		
		if( session.getAttribute("numazioni") !=null && Integer.parseInt(session.getAttribute("numazioni").toString())!=0 )
		{
			// Prende il conto del cliente
			Node conto = Util.readXML(getPath("conti"), "CodiceCliente", session.getAttribute("codice").toString());
			
			float saldo = Float.parseFloat(Util.getNodeValue(conto, "Saldo"));
			
			int numAzioni = Integer.parseInt(session.getAttribute("numazioni").toString());
			float costoTotale=0;		

		 	out.println("<script type=\"text/javascript\" src=\"javascript/scriptCarrello.js\"></script>");
		    
		    out.println("<form onSubmit=\"return controllosaldo();\" action=\"Checkout\" method=\"post\">");
		    
		    out.println("<table id=\"background-image\" summary=\"Carrello\">");
		    
		    out.println("<thead>" +
			    			"<tr>" +
			    				"<th>AZIONE</th>" +
			    				"<th>VALORE</th>" +
			    				"<th>QUANTIT&Agrave;</th>" +
			    				"<th>COSTO</th>" +
			    				"<th></th>" +
			    			"</tr>" +
		    			"</thead>");
		    
		    /*
		     * Legge e stampa le azioni acquistate
		     */
			for ( int i=0; i<numAzioni; i++)
			{
				Node azioneListino = Util.readXML(getPath("listino"), "CodiceAzione", session.getAttribute("Azione"+(i+1)).toString());
				
				String nome = Util.getNodeValue(azioneListino, "Nome");
				float prezzo = Float.parseFloat(Util.getNodeValue(azioneListino, "Prezzo"));
				int quantita = Integer.parseInt(session.getAttribute("Azione"+(i+1)+"_num").toString());
				
				float costo = prezzo * quantita;
				
				costoTotale += costo;
				
				out.println("<tbody>" +
								"<tr>" +
									"<td>" + nome + "</td>" +
									"<td>" + prezzo + "</td>" +
									"<td>" + quantita + "</td>" +
									"<td>" + costo + "</td>" +
									"<td align=\"center\"><a href=\"EditCart?m=1&n="+numAzioni+"&i="+(i+1)+"\"><img  id=\"del_ico\"src=\"images/table/carrello/del.png\" title=\"Elimina\"></a></td>" +
			        			"</tr>" +
		        			"</tbody>");
			}
			
		    out.println("<tfoot>" +
	    					"<tr>" +
	    						"<td colspan=\"2\" align=\"right\">COSTO TOTALE</td>" +
	    						"<td>" + costoTotale + "</td>" +
	    						"<td colspan=\"1\"></td>"+
	    					"</tr>" +
	    					"<tr>" +
	    						"<td colspan=\"2\" align=\"right\">SALDO DISPONIBILE</td>" + 
	    						"<td>" + saldo + "</td>" +
	    						"<td colspan=\"1\"></td>"+
	    					"</tr>" +
	    					"<tr>" +
	    						"<td colspan=\"2\" align=\"right\">SALDO FINALE</td>" +
	    						"<td>" + ( saldo - costoTotale ) + "</td>" +
	    						"<td colspan=\"1\"></td>"+
	    					"</tr>" +
	    					"<tr>" +
	    						"<td colspan=\"5\" align=\"right\">"+
	    							"<input id=\"del_cart_button\" type=\"button\" onclick=\"window.location='DelCart'\" value=\"SVUOTA CARRELLO\">" +
	    							"<input id=\"edit_cart_button\" type=\"button\" onclick=\"window.location='Core?55'\" value=\"MODIFICA CARRELLO\">" +
	    							"<input id=\"checkout_button\" type=\"submit\" value=\"CHECKOUT\">" +
	    						"</td>" +
	    					"</tr>" +
		    			"</tfoot>");
		    		
		    out.println("</table>" +
				    	"</form>");
		}
		else
		{
			out.println("<br><b><i>IL CARRELLO &Egrave; VUOTO</i></b>");
		}
	}
	
	private void editingCarrello(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		// Prende il conto del cliente
		Node conto = Util.readXML(getPath("conti"), "CodiceCliente", session.getAttribute("codice").toString());
		
		float saldo = Float.parseFloat(Util.getNodeValue(conto, "Saldo"));
		
    	response.setContentType( "text/html" );
	    out = response.getWriter();
	 	out.println("<script type=\"text/javascript\" src=\"javascript/scriptCarrello.js\"></script>");
	    
	    out.println("<div align=\"center\">");
	    out.println("<h2>CARRELLO</h2>");
		
		if( session.getAttribute("numazioni") !=null && Integer.parseInt(session.getAttribute("numazioni").toString())!=0)
		{
			int numAzioni = Integer.parseInt(session.getAttribute("numazioni").toString());
			float costoTotale=0;
			  
		    out.println("<form action=\"EditCart\" method=\"get\">" +
		    "<input type=\"hidden\" name=\"m\" value=\"0\">");
		    
		    out.println("<table id=\"edit-cart\" summary=\"Carrello\">");
		    
		    out.println("<thead>" +
			    			"<tr>" +
			    				"<th>AZIONE</th>" +
			    				"<th>VALORE</th>" +
			    				"<th>QUANTIT&Agrave;</th>" +
			    				"<th>DISPONIBILIT&Agrave;</th>" +
			    			"</tr>" +
		    			"</thead>");
		    
		    /*
		     * Legge e stampa le azioni acquistate
		     */
			for ( int i=0; i<numAzioni; i++)
			{
				Node azioneListino = Util.readXML(getPath("listino"), "CodiceAzione", session.getAttribute("Azione"+(i+1)).toString());
				
				String nome = Util.getNodeValue(azioneListino, "Nome");
				float prezzo = Float.parseFloat(Util.getNodeValue(azioneListino, "Prezzo"));
				float disponibilita = Float.parseFloat(Util.getNodeValue(azioneListino, "Disponibilita"));
				int quantita = Integer.parseInt(session.getAttribute("Azione"+(i+1)+"_num").toString());
				
				float costo = prezzo * quantita;
				
				costoTotale += costo;
				
				out.println("<tbody>" +
								"<tr>" +
									"<td>" + nome + "</td>" +
									"<td>" + prezzo + "</td>" +
									"<td><input name=\"valore"+(i+1)+"\" id=\"valore"+(i+1)+"\" onkeyup=\"setMaximumValue(this.id, " + disponibilita + ")\" onblur=\"controllo(this.id);\" type=\"text\" value=\"" + quantita + "\"></td>" +
									"<td>su " + disponibilita + "</td>" +
			        			"</tr>" +
		        			"</tbody>");
			}
		    
		    out.println("<tfoot>" +
							"<tr>" +
								"<td colspan=\"4\" align=\"right\">"+
									"SALDO CONTABILE: " + saldo +
								"</td>" +
							"</tr>" +
	    					"<tr>" +
	    						"<td colspan=\"4\" align=\"right\">"+
	    							"<input type=\"reset\" value=\"RESET\">" +
	    							"<input type=\"button\" onclick=\"window.location='Core?3'\" value=\"ANNULLA MODIFICHE\">" +
	    							"<input type=\"submit\" value=\"AGGIORNA CARRELLO\">" +
	    						"</td>" +
	    					"</tr>" +
		    			"</tfoot>");
		    		
		    out.println("</table>" +
				    	"</form>");
		}
		else
		{
			out.println("<br><b><i>IL CARRELLO &Egrave; VUOTO</i></b>");
		}
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
				contentPortafoglio(request, response);
				break;
			case 2:
				contentListino(request, response);
				break;
			case 3:
				contentCarrello(request, response);
				break;
			case 9:
				logout(request, response);
				break;
			case 20:
				contentListinoSearch(request, response);
				break;
			case 55:
				editingCarrello(request, response);
				break;
			case 66:
				editingAnagrafica(request, response);
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

	public void loginRedirect (HttpServletResponse response) throws IOException
	{
		response.sendRedirect("Login");
	}

	private void logout(HttpServletRequest request,
			HttpServletResponse response) throws IOException
	{
		session.invalidate();
		response.sendRedirect("index.html");
	}

	private String getPath(String name)
	{
		// Prende il path dell'xml
		return (String) getServletContext().getAttribute(name);
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