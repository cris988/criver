function setMaximumValue(id,maxVal){
	var txtObj=document.getElementById(id).value;
	var tempVal = parseInt(txtObj);
	
	
	if(!isNaN(tempVal))
	{
		if(tempVal>maxVal)
		{
			alert("La quantità inserita è maggiore della disponibilità dell'azione...","Valore Errato");
			document.getElementById(id).select();
			document.getElementById(id).value = maxVal;
			document.getElementById(id).focus();
		}
		if(tempVal<0)
		{
			alert("La quantità inserita è negativa...","Valore Errato");
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
function controllo(id,idcheck){
	var txtObj=document.getElementById(id).value;
	var tempVal = parseInt(txtObj);
	if(txtObj=="" || txtObj=="0")
	{
		document.getElementById(id).value = 0;
		document.getElementById(id).disabled = true;
		document.getElementById(idcheck).checked = false;
	}
}