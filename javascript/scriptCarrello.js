function setMaximumValue(id,maxVal){
	var txtObj=document.getElementById(id).value;
	var tempVal = parseInt(txtObj);
	
	
	if(!isNaN(tempVal))
	{
		if(tempVal>maxVal)
		{
			alert("La quantit� inserita � maggiore della disponibilit� dell'azione...","Valore Errato");
			document.getElementById(id).select();
			document.getElementById(id).value = maxVal;
			document.getElementById(id).focus();
		}
		if(tempVal<0)
		{
			alert("La quantit� inserita � negativa","Valore Errato");
			document.getElementById(id).select();
			document.getElementById(id).value = 0;
			document.getElementById(id).focus();
		}
	}
	else if(isNaN(tempVal) && txtObj!="")
	{
		alert("Inserire un valore numerico...","Valore Errato");
		document.getElementById(id).select();
		document.getElementById(id).value = 0;
		document.getElementById(id).focus();
	}
}
function controllo(id){
	var txtObj=document.getElementById(id).value;
	var tempVal = parseInt(txtObj);
	if(txtObj=="" || txtObj=="0")
	{
	
function controllosaldo(saldo){
	if(saldo<0)
	{
		alert('ATTENZIONE, Saldo finale inferiore a zero... modificare il carrello!!!');
		return false;
	}
}