function toggleDataInput (){
    const stato = document.getElementById("statoSelect").value;
    const ferieDiv = document.getElementById("ferieInput");
    /*Verifico se è stato inserito ferie*/
    if (stato === "FERIE"){
        ferieDiv.style.display = "block";
    }else{
        ferieDiv.style.dispaly = "none";
    }
}
