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


public class EditList extends HttpServlet
{
	HttpSession session;
	PrintWriter out;
	
	protected void doPost (HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException 
	{
		session = request.getSession(false);
		
		if (session != null) 
		{ 
			if(session.getAttribute("admin").equals("true"))
			{
				int len = (Util.readXML(getPath("listino"), "CodiceAzione")).getLength();
				
				for ( int i=0; i<len; i++)
				{
					
					// Prende l'azione
					Node azioneParent = Util.readXML(getPath("listino"), "CodiceAzione", request.getParameter("cod"+i));
					// Prende il doc dell'xml
					Document doc_listino = azioneParent.getOwnerDocument();
					
					// Imposta nuovo nome
					Node nome = Util.getFindChildNode(azioneParent, "Nome");
					nome.getFirstChild().setNodeValue(request.getParameter("nome"+i));
					
					// Imposta nuovo prezzo
					Node cognome = Util.getFindChildNode(azioneParent, "Prezzo");
					cognome.getFirstChild().setNodeValue(request.getParameter("prezzo"+i));
					
					// Imposta nuovo disponibilita
					Node disponibilita = Util.getFindChildNode(azioneParent, "Disponibilita");
					disponibilita.getFirstChild().setNodeValue(request.getParameter("disponibilita"+i));
					
					// Imposta nuovo categoria
					Node categoria = Util.getFindChildNode(azioneParent, "Categoria");
					categoria.getFirstChild().setNodeValue(request.getParameter("categoria"+i));
					
					Util.writeXML(doc_listino, getPath("listino"));
				}
				response.sendRedirect("Admin?2");
			}
			else
			{
				session.invalidate();
				response.sendRedirect("LoginAdmin");				
			}
			
		}
	}
	
	protected void doGet (HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException 
	{
		session = request.getSession(false);
		if (session != null) 
		{ 
			int mode = Integer.parseInt(request.getParameter("m"));
			
			if ( mode == 1)
			{
				// DELETE
				String codice = request.getParameter("cod");
				deleteAzione(codice);
			}
			else if ( mode == 0)
			{
				// ADD
				Node listino = Util.readXML(getPath("listino"));
				
				Document doc_listino = listino.getOwnerDocument();
				
				String value;

				String nuovoCodiceAzione = Util.nuovoCodice(getPath("listino"), "CodiceAzione");
				
				/* Codice */
				Node codiceAzioneNodo = doc_listino.createElement("CodiceAzione"); 
				Node codiceTextNode = doc_listino.createTextNode(nuovoCodiceAzione);
				
				codiceAzioneNodo.appendChild(codiceTextNode);
				
				/* Nome */
				Node NomeNodo = doc_listino.createElement("Nome");
				value = request.getParameter("Nome");
				Node NomeTextNode = doc_listino.createTextNode(value);
				
				NomeNodo.appendChild(NomeTextNode);
				
				/* Prezzo */
				Node PrezzoNodo = doc_listino.createElement("Prezzo");
				value = request.getParameter("Prezzo");
				Node PrezzoTextNode = doc_listino.createTextNode(value);
				
				PrezzoNodo.appendChild(PrezzoTextNode);
				
				/* Disponibilita */
				Node DisponibilitaNodo = doc_listino.createElement("Disponibilita");
				value = request.getParameter("Disponibilita");
				Node DisponibilitaTextNode = doc_listino.createTextNode(value);
				
				DisponibilitaNodo.appendChild(DisponibilitaTextNode);
				
				/* Categoria */
				Node CategoriaNodo = doc_listino.createElement("Categoria");
				value = request.getParameter("Categoria");
				Node CategoriaTextNode = doc_listino.createTextNode(value);
				
				CategoriaNodo.appendChild(CategoriaTextNode);
				
				Node nuovoTitolo = doc_listino.createElement("Azione");
				nuovoTitolo.appendChild(codiceAzioneNodo);
				nuovoTitolo.appendChild(NomeNodo);
				nuovoTitolo.appendChild(PrezzoNodo);
				nuovoTitolo.appendChild(DisponibilitaNodo);
				nuovoTitolo.appendChild(CategoriaNodo);
				
				listino.appendChild(nuovoTitolo);
				Util.writeXML(doc_listino, getPath("listino"));
				
			}
			response.sendRedirect("Admin?2");
		}
		else
		{
			response.sendRedirect("LoginAdmin");
		}
	}
	private void deleteAzione(String codice)
	{
		// Trova il nodo azione con codice
		Node azione = Util.readXML(getPath("listino"), "CodiceAzione", codice);
		
		// Prende tutti i conti e listino
		Node conti = Util.readXML(getPath("conti"));
		Node listino = azione.getParentNode();
		
		// Doc per il dom
		Document doc_conti = conti.getOwnerDocument();
		Document doc_listino = listino.getOwnerDocument();
		
		// Prendo il primo conto
		Node conto=conti.getFirstChild();

		do 
		{
			// Cerco l'azione nel conto
			Node azioneConto = Util.getFindChildNode(conto, "CodiceAzione", codice);
			
			if ( azioneConto != null )
			{
				// Saldo
				Node saldo = Util.getFindChildNode(conto, "Saldo");
				
				// Prendo quantita nel conto
				Float quantita = Float.parseFloat(
						Util.getNodeValue(
								Util.getNextNode(conto, "Quantita")));
				
				// Prendo prezzo nel listino
				Float prezzo = Float.parseFloat(
						Util.getNodeValue(
								Util.getFindChildNode(azione, "Prezzo")));
				
				// Totale da liquidare
				Float liquidare = quantita * prezzo;
				
				// Prendi saldo
				Float saldoNuovo = Float.parseFloat(saldo.getFirstChild().getNodeValue());
				
				saldoNuovo += liquidare;
				
				// Aggiorna saldo
				saldo.getFirstChild().setNodeValue(Float.toString(saldoNuovo));
				
				conto.removeChild(azioneConto);
				
				//Domwalk.walk(conto);
			}
			
			conto = conto.getNextSibling();
			
		} while (conto!=null);
		
		listino.removeChild(azione);

		//Domwalk.walk(listino);
		
		// Scrivi xml
		Util.writeXML(doc_listino, getPath("listino"));
		Util.writeXML(doc_conti, getPath("conti"));
		
	}
	
	private String getPath(String name)
	{
		// Prende il path dell'xml
		return (String) getServletContext().getAttribute(name);
	}
}