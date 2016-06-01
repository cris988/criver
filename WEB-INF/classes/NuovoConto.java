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

public class NuovoConto extends HttpServlet
{	
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
	
	protected void doPost (HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException 
	{
		
		Node correntistaParent = Util.readXML(getPath("correntisti"));
		Node loginParent = Util.readXML(getPath("login"));
		Node contiParent = Util.readXML(getPath("conti"));
		
		Document doc_correntista = correntistaParent.getOwnerDocument();
		Document doc_login = loginParent.getOwnerDocument();
		Document doc_conto = contiParent.getOwnerDocument();
		
		String value;

		String nuovoCodiceCliente = Util.nuovoCodice(getPath("correntisti"), "CodiceCliente");
		String nuovoCodiceConto = Util.nuovoCodice(getPath("conti"), "CodiceConto");
		
		/** Inizio anagrafica **/
		
		/* Codice */
		Node codiceClienteNodo = doc_correntista.createElement("CodiceCliente"); 
		Node codiceTextNode = doc_correntista.createTextNode(nuovoCodiceCliente);
		
		codiceClienteNodo.appendChild(codiceTextNode);
		
		/* Nome */
		Node nomeNodo = doc_correntista.createElement("Nome");
		value = request.getParameter("nome");
		Node nomeTextNode = doc_correntista.createTextNode(value);
		
		nomeNodo.appendChild(nomeTextNode);
		
		/* Cognome */
		Node cognomeNodo = doc_correntista.createElement("Cognome");
		value = request.getParameter("cognome");
		Node cognomeTextNode = doc_correntista.createTextNode(value);
		
		cognomeNodo.appendChild(cognomeTextNode);
		
		/* Data Nascita */
		Node dataNodo = doc_correntista.createElement("DataNascita");
		value = request.getParameter("giorno")+"/"+request.getParameter("mese")+"/"+request.getParameter("anno");
		Node dataTextNode = doc_correntista.createTextNode(value);
		
		dataNodo.appendChild(dataTextNode);	
		
		/* Indirizzo */
		Node indirizzoNodo = doc_correntista.createElement("Indirizzo");
		value = request.getParameter("indirizzo");
		Node indirizzoTextNode = doc_correntista.createTextNode(value);
		
		indirizzoNodo.appendChild(indirizzoTextNode);
		
		/* Citta */
		Node cittaNodo = doc_correntista.createElement("Citta");
		value = request.getParameter("citta");
		Node cittaTextNode = doc_correntista.createTextNode(value);
		
		cittaNodo.appendChild(cittaTextNode);
		
		/* Provincia */
		Node provinciaNodo = doc_correntista.createElement("Provincia");
		value = request.getParameter("provincia");
		Node provinciaTextNode = doc_correntista.createTextNode(value);
		
		provinciaNodo.appendChild(provinciaTextNode);
		
		/* Cap */
		Node capNodo = doc_correntista.createElement("CAP");
		value = request.getParameter("cap");
		Node capTextNode = doc_correntista.createTextNode(value);
		
		capNodo.appendChild(capTextNode);
		
		/* Stato */
		Node statoNodo = doc_correntista.createElement("Stato");
		value = request.getParameter("stato");
		Node statoTextNode = doc_correntista.createTextNode(value);
		
		statoNodo.appendChild(statoTextNode);
		
		/* Telefono */
		Node telefonoNodo = doc_correntista.createElement("Telefono");
		value = request.getParameter("telefono");
		Node telefonoTextNode = doc_correntista.createTextNode(value);
		
		telefonoNodo.appendChild(telefonoTextNode);
		
		/* Email */
		Node emailNodo = doc_correntista.createElement("Email");
		value = request.getParameter("email");
		Node emailTextNode = doc_correntista.createTextNode(value);
		
		emailNodo.appendChild(emailTextNode);
		
		
		Node nuovoCorrentista = doc_correntista.createElement("Correntista");
		
		nuovoCorrentista.appendChild(codiceClienteNodo);
		nuovoCorrentista.appendChild(nomeNodo);
		nuovoCorrentista.appendChild(cognomeNodo);
		nuovoCorrentista.appendChild(dataNodo);
		nuovoCorrentista.appendChild(indirizzoNodo);
		nuovoCorrentista.appendChild(cittaNodo);
		nuovoCorrentista.appendChild(provinciaNodo);
		nuovoCorrentista.appendChild(capNodo);
		nuovoCorrentista.appendChild(statoNodo);
		nuovoCorrentista.appendChild(telefonoNodo);
		nuovoCorrentista.appendChild(emailNodo);
		
		correntistaParent.appendChild(nuovoCorrentista);
		
		//Domwalk.walk(nuovoCorrentista);
		
		/** Fine anagrafica **/
		
		/** Inizio login **/
		
		/* Username */
		Node usernameNodo = doc_login.createElement("Username");
		value = request.getParameter("username");
		Node usernameTextNode = doc_login.createTextNode(value);
		
		usernameNodo.appendChild(usernameTextNode);
		
		/* Password */
		Node passwordNodo = doc_login.createElement("Password");
		value = request.getParameter("password");
		Node passwordTextNode = doc_login.createTextNode(value);
		
		passwordNodo.appendChild(passwordTextNode);
		
		/* Codice cliente */
		Node codiceClienteLoginNodo = doc_login.createElement("CodiceCliente");
		Node codiceClienteLoginTextNode = doc_login.createTextNode(nuovoCodiceCliente);
		
		codiceClienteLoginNodo.appendChild(codiceClienteLoginTextNode);
		
		Node nuovoLogin = doc_login.createElement("User");
		Attr enabled = doc_login.createAttribute("enabled");
		enabled.setValue("false");
		((Element)nuovoLogin).setAttributeNode(enabled);
		nuovoLogin.appendChild(usernameNodo);
		nuovoLogin.appendChild(passwordNodo);
		nuovoLogin.appendChild(codiceClienteLoginNodo);
		
		loginParent.appendChild(nuovoLogin);
		
		/** Fine login **/
		
		/** Inizio conto **/
		
		/* Codice conto */
		Node codiceContoNodo = doc_conto.createElement("CodiceConto");
		Node codiceContoTextNode = doc_conto.createTextNode(nuovoCodiceConto);
		
		codiceContoNodo.appendChild(codiceContoTextNode);
		
		/* Codice cliente */
		Node codiceClienteContoNodo = doc_conto.createElement("CodiceCliente");
		Node codiceClienteContoTextNode = doc_conto.createTextNode(nuovoCodiceCliente);
		
		codiceClienteContoNodo.appendChild(codiceClienteContoTextNode);
		
		/* Saldo */
		Node saldoNodo = doc_conto.createElement("Saldo");
		value = request.getParameter("saldo");
		Node saldoTextNode = doc_conto.createTextNode(value);
		
		saldoNodo.appendChild(saldoTextNode);
		
		Node nuovoConto = doc_conto.createElement("Conto");
		
		nuovoConto.appendChild(codiceContoNodo);
		nuovoConto.appendChild(codiceClienteContoNodo);
		nuovoConto.appendChild(saldoNodo);
		
		contiParent.appendChild(nuovoConto);
		
		/** Fine conto **/
		
		/** Scrivo xml **/		
		Util.writeXML(doc_correntista, getPath("correntisti"));
		Util.writeXML(doc_login, getPath("login"));
		Util.writeXML(doc_conto, getPath("conti"));
		
		response.sendRedirect("Login");
		
	}
	
	private String getPath(String name)
	{
		// Prende il path dell'xml
		return (String) getServletContext().getAttribute(name);
	}
	
}