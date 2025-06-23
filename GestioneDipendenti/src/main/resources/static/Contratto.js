function toggleDateContratto (){
    const tipoContratto = document.getElementById("tipoContratto").value;
    const dateContrattoDiv = document.getElementById("dateContratto")

    if(tipoContratto === "INDETERMINATO"){
        dateContrattoDiv.style.display = "none";
    }else{
        dateContrattoDiv.style.display = "block";
    }
}