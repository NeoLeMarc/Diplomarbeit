/* MANVWeb Javascript prototype */
var lastBreathing = {};
var lastPulse     = {};

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
            var pulseChanged = 0;
            var breathingChanged = 0;

            // Rendern der Resultate
            tableRows  = "<table id='nodesTable'>";
            tableRows += " <tr>";
            tableRows += "   <td><b>NodeID</b></td>";
            tableRows += "   <td><b>Alarm Puls</b></td>";
            tableRows += "   <td><b>Alarm Atmung</b></td>";
            tableRows += "   <td><b>Puls</b></td>";
            tableRows += "   <td><b>Atmung</b></td>";
            tableRows += "   <td><b>Timestamp</b></td>";
            tableRows += " </tr>";

            for(i in jsonData){
                row = jsonData[i];

                if(row['alarm_pulse'] == 'true')
                    tableRows += " <tr class='alert'>";
                else
                    tableRows += " <tr class='ok'>";

                tableRows += "  <td>" + row['nodeID']          + "</td>";
                tableRows += "  <td id='alarm'>" + row['alarm_pulse']     + "</td>";
                tableRows += "  <td id='alarm'>" + row['alarm_breathing'] + "</td>";
               
                pulse     = "";
                breathing = "";
                nodeID    = row['nodeID'];

                if(row['dataMessages'].length){
                    pulseValue      = row['dataMessages'][0]['pulse'];
                    breathingValue  = row['dataMessages'][0]['breathing'];

                    pulse     += " " + pulseValue;
                    breathing += " " + breathingValue;

                    // Flashing
                    if(pulseValue != lastPulse[row['nodeID']]){
                        lastPulse[row['nodeID']] = pulseValue;

                        // Flash blue border
                        window.setTimeout("flashTable('" + nodeID + "_pulse')", 0);
                    }

                    if(breathingValue != lastBreathing[row['nodeID']]){
                        lastBreathing[row['nodeID']] = breathingValue;

                        // Flash blue border
                        window.setTimeout("flashTable('" + nodeID + "_breathing')", 0);
                    }
                }

                tableRows += "    <td id='" + nodeID + "_pulse'>" + pulse + "</td>";
                tableRows += "    <td id='" + nodeID + "_breathing'>" + breathing + "</td>";
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

flashTable = function(columnID){
    column = document.getElementById(columnID);
    column.className = 'ready';
}

startUpdating = function(){
    self.retrieveData();


    // Refetch data after 1 Second
    window.setTimeout("startUpdating()", 1000);

}
