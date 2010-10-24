/* MANVWeb Javascript prototype */
convertDate = function(timestamp){
    var Zeit = new Date();
    Zeit.setTime(timestamp);
    return Zeit;
}

retrieveData = function(){
    var url = "/MANVWeb/";
    var req = getXMLHttpRequest();
    req.open("GET", url, true);
    req.onreadystatechange = function(){
        if(req.readyState == 4 && req.status == 200){
            var jsonData = eval('(' + req.responseText + ')');

            // Rendern der Resultate
            tableRows  = "<table>";
            tableRows += " <tr>";
            tableRows += "   <td><b>NodeID</b></td>";
            tableRows += "   <td><b>Alarm Puls</b></td>";
            tableRows += "   <td><b>Alarm Atmung</b></td>";
            tableRows += "   <td><b>Timestamp</b></td>";
            tableRows += " </tr>";

            for(i in jsonData){
                row = jsonData[i];

                tableRows += " <tr>";
                tableRows += "  <td>" + row['nodeID']          + "</td>";
                tableRows += "  <td>" + row['alarm_pulse']     + "</td>";
                tableRows += "  <td>" + row['alarm_breathing'] + "</td>";
                tableRows += "  <td>" + convertDate(row['serverTimestamp']) + "</td>";
                tableRows += " </tr>";

            }

            tableRows += "</table>";

            var droppoint = document.getElementById('droppoint');
            droppoint.innerHTML = tableRows;

        }
    }
    req.send(null);


}

startUpdating = function(){
    self.retrieveData();

    // Refetch data after 1 Second
    window.setTimeout("startUpdating()", 1000);

}
