window.onload = main;
$(document).ready(function() {
  submitCatch();
})

function main () {
	asynchReq("GetQuotations", loadQuotations);
	setInterval(updateQuotations, 5000);
	asynchReq("GetQuotations?pending=true", numQuot);
}

function asynchReq(url, cfunc) {
  var request;
  request = new XMLHttpRequest();
  request.open("GET", url, true);
  request.onreadystatechange = function() { cfunc(request); }
  request.send();
}

function updateQuotations() {
  asynchReq("GetQuotations", loadQuotations);
  asynchReq("GetQuotations?pending=true", numQuot);
}

function loadQuotations(request) {
  if (request.readyState == 4 && request.status == 200) {
    var quotations = JSON.parse(request.responseText);
    var table = document.getElementById("tableQuotations");
    var row, col = [];
    
    // Empty the table to refresh it
    table.innerHTML = "";
    
    // Extract columns name for the header
    for (var key in quotations[0]) {
      if (col.indexOf(key) === -1) {
        col.push(key);
      }
    }

    // Create the table header using the keys extracted
    row = table.insertRow(-1);
    for (var i = 0; i < col.length; i++) {
      var header = document.createElement("th");
      header.innerHTML = col[i];
      row.appendChild(header);
    }

    // Add JSON data to the table as rows
    for (var i = 0; i < quotations.length; i++) {

      row = table.insertRow(-1);
      for (var j = 0; j < col.length; j++) {
        var tabCell = row.insertCell(-1);
        tabCell.innerHTML = quotations[i][col[j]];
      }
    }

  }
}

function changeOptions() {
  var product = document.getElementById("productChoice");
  var prodId = product.options[product.selectedIndex].value;
  asynchReq("GetOptions?product=" + prodId, loadOptions);
  $("#productImage").attr("src", "GetImage?image=" + prodId);
}

function loadOptions(request) {
  if (request.readyState == 4 && request.status == 200) {
	  var options = JSON.parse(request.responseText);
	  var optionsBox = document.getElementById("checkBoxContainer");
	  
	  optionsBox.innerHTML = "";
	  if(options.length > 0) {
		  optionsBox.innerHTML = "<h5> Choose the options you're interested in: </h5>"
		  for(var i = 0; i < options.length; i++) {
			  var check = document.createElement("input");
			  var label = document.createElement("label");
			  label.innerHTML = "&ensp;" + options[i].name;
			  check.setAttribute("type", "checkbox");
			  check.setAttribute("name", options[i].optionCode);
			  check.setAttribute("value", options[i].id);
			  optionsBox.appendChild(check);
			  optionsBox.appendChild(label);
			  optionsBox.appendChild(document.createElement("br"));
		  }
	  }
  }
}

function numQuot(request) {
	if (request.readyState == 4 && request.status == 200) {
		var pendingRequests = request.responseText;
		var line = document.getElementById("pending");
		
		line.innerHTML = "Pending Requests: " + pendingRequests;
	}
}

/*function sendForm() {
	var formData = new FormData();
	var product = document.getElementById("productChoice");
	var optionsChecked = document.querySelectorAll("input[type=checkbox]:checked");
	
	formData.append(product.name, product.value);
	for(var i = 0; i < optionsChecked.length; i++) {
		formData.append(optionsChecked[i].name, optionsChecked[i].value);
	}
	
	request = new XMLHttpRequest();
	request.open("POST", "HomeCustomerJS", true);
	request.setRequestHeader("Content-Type", "multipart/form-data");
	request.onreadystatechange = function() {
		if(request.readyState == 4 && request.status == 200) {
			console.log("form submitted successfully! \n");
		}
		return false;
	}
	request.send(formData);
}*/
function submitCatch() {
	$("form").submit(function(e) {
	    e.preventDefault();
	    $.ajax({
	        type: 'POST',
	        url: 'HomeCustomerJS',
	        data: $('#form').serialize(),
	        success: function() {
	            console.log("The form post was successful");
	        	asynchReq("GetQuotations?pending=true", numQuot);
	        	},
	        error: function() {
	            console.log("The form post was unsuccessful");
	        	}
	    	});
	    });
}
