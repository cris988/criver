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

public class Checkout extends HttpServlet
{
	HttpSession session;
	PrintWriter out;
	
	protected void doPost (HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException 
	{		
		session = request.getSession(false);

		// Prende il conto del cliente, saldo e listino azioni
		Node conto = Util.readXML(getPath("conti"), "CodiceCliente", session.getAttribute("codice").toString());
		Node listino = Util.readXML(getPath("listino"), "Azione").item(0);
		Node saldoNode = Util.getNextNode(conto.getFirstChild(), "Saldo");
		
		Document doc_conto = null;
		Document doc_listino = null;
		// Prende il doc dei xml
		doc_conto = conto.getOwnerDocument();
		doc_listino = listino.getOwnerDocument();
		
		listino = listino.getParentNode();

		// Usati per conservare le azioni da modificare negli xml dopo averle lette
		ArrayList<Node> listAzioni = new ArrayList<Node>();
		ArrayList<Node> listAzioniListino = new ArrayList<Node>();
		
		float saldo = Float.parseFloat(Util.getNodeValue(saldoNode));
		float costoTotale=0;
		
		int numAzioni = Integer.parseInt(session.getAttribute("numazioni").toString());
		
		/*
		 * SCRIVE IL CONTO CON LE AZIONI COMPRATE E IL NUOVO SALDO
		 * 
		 */
		for ( int i=0; i<numAzioni; i++)
		{
			/* 
			 * Recupera:
			 * il codice azione
			 * l'azione nel listino
			 * la quantita acquistata
			 */
			String codiceAzione = session.getAttribute("Azione"+(i+1)).toString();
			
			// Prende l'azione dal listino
			Node azioneListino = Util.getFindChildNode(listino, "CodiceAzione", codiceAzione);

			int quantitaAzione = Integer.parseInt(session.getAttribute("Azione"+(i+1)+"_num").toString());
			float prezzo = Float.parseFloat(Util.getNodeValue(azioneListino, "Prezzo"));
			
			// Cerco nel conto l'azione attuale
			Node azioneCorr = Util.getFindChildNode(conto, "CodiceAzione", codiceAzione);
			
			// Calcolo del costo totale delle azioni comprate
			float costo = prezzo * quantitaAzione;
			costoTotale += costo;
			int totAzione=quantitaAzione;
			
			if ( azioneCorr != null )
			{
				/*
				System.out.print("\n\n STAMPO AZIONE \n\n");
				Domwalk.walk(azioneCorr);
				System.out.print("\n\n");*/	
				
				// Se l'azione è già posseduta aggiorna la quantita
				
				/* MODIFICA CONTO */
				Node quantitaNode = Util.getFindChildNode(azioneCorr, "Quantita");
				totAzione += Integer.parseInt(Util.getNodeValue(quantitaNode));
				
				// Salva la nuova quantita posseduta
				quantitaNode.getFirstChild().setNodeValue(Integer.toString(totAzione));
				
				//forse da cancellare
				azioneCorr.appendChild(quantitaNode);
				listAzioni.add(azioneCorr);
				
				/*
				System.out.print("\n\n STAMPO AZIONE AGGIORNATA \n\n");
				Domwalk.walk(azioneCorr);
				System.out.print("\n\n");*/
			}
			else
			{
				// Crea l'azione
				
				/* MODIFICA CONTO */
				Node azioneNode = doc_conto.createElement("Azione");
				Node codiceNode = doc_conto.createElement("CodiceAzione");
				Node quantitaNode = doc_conto.createElement("Quantita");
				
				Node codiceTextNode = doc_conto.createTextNode(codiceAzione);
				Node quantitaTextNode = doc_conto.createTextNode(Integer.toString(quantitaAzione));
				
				codiceNode.appendChild(codiceTextNode);
				quantitaNode.appendChild(quantitaTextNode);
				
				azioneNode.appendChild(codiceNode);
				azioneNode.appendChild(quantitaNode);
				listAzioni.add(azioneNode);
				
				/*
				System.out.print("\n\n STAMPO AZIONE NUOVA\n\n");
				Domwalk.walk(azioneNode);
				System.out.print("\n\n");*/
			}
			/* MODIFICA LISTINO */
			Node disponibilitaNode = Util.getNextNode(azioneListino.getFirstChild(), "Disponibilita");
			int disponibilitaAzione = Integer.parseInt(Util.getNodeValue(disponibilitaNode));
			disponibilitaAzione -= quantitaAzione;
			
			disponibilitaNode.getFirstChild().setNodeValue(Integer.toString(disponibilitaAzione));
			
			azioneListino.appendChild(disponibilitaNode);
			
			listAzioniListino.add(azioneListino);
			
			/*
			System.out.print("\n\n STAMPO LISTINO \n\n");
			Domwalk.walk(azioneListino);
			System.out.print("\n\n");*/
		}
		
		// Aggiorna il saldo e lo salva nel nodo
		saldo -= costoTotale;
		saldoNode.getFirstChild().setNodeValue(Float.toString(saldo));
		
		conto.appendChild(saldoNode);
		
		for ( int i=0; i<listAzioni.size(); i++ )
		{
			conto.appendChild(listAzioni.get(i));
			doc_listino.getFirstChild().getNextSibling().appendChild(listAzioniListino.get(i));
		}		
		/*
		System.out.print("\n\n STAMPO CONTO \n\n");
		Domwalk.walk(conto);
		System.out.print("\n\n");
		*/
		Util.writeXML(doc_conto, getPath("conti"));
		Util.writeXML(doc_listino, getPath("listino"));
		
    	response.setContentType( "text/html" );
	    out = response.getWriter();

		response.sendRedirect("DelCart");
		
	}

	private String getPath(String name)
	{
		// Prende il path dell'xml
		return (String) getServletContext().getAttribute(name);
	}
}
 
