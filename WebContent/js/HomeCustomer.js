window.onload = main;
var request;

function main () {
  asynchReq("GetQuotations", loadQuotations);
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
    var table = document.getElementById("tableQuotations");
    var row, col = [];

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
