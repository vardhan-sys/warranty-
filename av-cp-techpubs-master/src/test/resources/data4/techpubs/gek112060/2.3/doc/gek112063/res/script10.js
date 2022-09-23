window.onload = initiFrame;

function initiFrame() {
	
		document.getElementById('closebtn').style.visibility = 'hidden';
	
	for (var i=0; i<document.links.length; i++) {
		var value=document.links[i];
		
		if((String(value).indexOf("ICN")>0) && (String(value).indexOf(".htm")>0)){
			
		document.links[i].target = "content";
		document.links[i].onclick = setiFrame;
		}
	}
	
}

function setiFrame() {
	
	document.getElementById('closebtn').style.visibility = 'visible';
	document.getElementById("content").style.display = "block";	
	document.getElementById("dataModuleId").style.width='50%';
	document.getElementById("content").contentWindow.document.location.href = this.href;
	return false;
}


function closeFrame() {	
	document.getElementById("content").style.display = "none";	
	document.getElementById('closebtn').style.visibility = 'hidden';
	document.getElementById("dataModuleId").style.width='100%';
	return false;
}
