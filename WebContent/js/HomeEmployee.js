$(document).ready(function() {
	  asynchReq("GetQuotations", loadQuotations);
	  submitCatch();
	  linkButtonsModal();
	  checkPrice();
	})
	
var quotationId;


function asynchReq(url, cfunc) {
  var request;
  request = new XMLHttpRequest();
  request.open("GET", url, true);
  request.onreadystatechange = function() { cfunc(request); }
  request.send();
}
	
function submitCatch() {
	$("form").submit(function(e) {
	    e.preventDefault();
	    $.ajax({
	        type: 'POST',
	        url: 'PriceQuote',
	        data: $('#form').serialize(),
	        success: function() {
	            asynchReq("GetQuotations", loadQuotations)
	            removeRow();
	            $('#modal').hide();
	        	},
	        error: function() {
	            alert("The price action was unsuccessful");
	        	}
	    	});
	    });
}

function linkButtonsModal() {
	var modal = document.getElementById("modal");
	
	$('#tableToPrice button').on('click', function() {
		asynchReq("PriceQuote?javascript=true&quotation=" + this.id, loadModal);
		quotationId = this.id;
		modal.style.display = "block";
		checkPrice();
	});
	
	$('.close').on('click', function() {
		modal.style.display = "none";
	})
	
	window.onclick = function(event) {
		  if (event.target == modal) {
		    modal.style.display = "none";
		  }
		}
}

function loadModal(request) {

	if (request.readyState == 4 && request.status == 200) {
		var quotation = JSON.parse(request.responseText);
		$('#modal #quotCustomer').html(quotation.customer);
		$('#modal #quotProduct').html(quotation.product + " | " + quotation.code);
		$('#modal #prodOptions').html(quotation.options);
		$('#modal img').attr('src', "GetImage?image="+quotation.productid);
	}
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

function removeRow() {
	var rowToRemove = document.getElementById(quotationId).closest("tr").rowIndex;
	document.getElementById("tableToPrice").deleteRow(rowToRemove);
}

function checkPrice() {
	var priceInput = document.getElementById("quotPrice");
	priceInput.oninput = function() {
		if(priceInput.value <= 0) {
			priceInput.setCustomValidity("Price must be > 0");
			priceInput.style.borderColor = "red";
			priceInput.reportValidity();
		} else {
			priceInput.setCustomValidity("");
			priceInput.style.borderColor = "grey";
		}
	}
}