window.onload = main;
var CONTEXT_PATH = $('#contextPathHolder').attr('data-contextPath');
var hostPath = window.location.protocol + "//" + window.location.host;
var table = $("#tableQuotations");
var request;

function main () {
  asynchReq(CONTEXT_PATH+"/GetQuotations", loadQuotations);
}

function asynchReq(url, cfunc) {
  request = new XMLHttpRequest();
  request.open("GET", url, true);
  request.onreadystatechange = cfunc;
  request.send();
}

function loadQuotations() {
  if (request.readyState == 4 && request.status == 200) {
    var quotations = JSON.parse(request.responseText);
    var table, row, col = [];

    // Extract columns name for the header
    for (var i = 0; i < quotations.length; i++) {
      for (var key in quotations[i]) {
        if (col.indexOf(key) === -1) {
          col.push(key);
        }
      }
    }

    // Create the table header using the keys extracted
    row = table.insertRow(-1);
    for (var i = 0; i < quotations.length; i++) {
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
