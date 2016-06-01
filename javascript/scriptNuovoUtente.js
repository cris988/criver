function controllo(){
  var f = document.forms[0];
  var n = f.elements.length;
  
  for(var i = 0; i < n; i++){
    if(f.elements[i].value == ''){
      alert('Error: value '+f.elements[i].name+ ' is empty');
      return false;
    }
  }
}