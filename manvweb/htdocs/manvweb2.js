/* MANVWeb Javascript prototype */
var MANVWeb = function(){

    this.retrieveData = function(){
        alert("Roger that");
        var url = "/manvweb/";
        var req = getXMLHttpRequest();
        req.open("GET", url, true);
        req.onreadystatechange = function(){
            if(req.readyState == 4 && req.status == 200){
                alert(req.responseText);
            }
        }
        req.send(null);
        alert("Forgiven");
    }
}
