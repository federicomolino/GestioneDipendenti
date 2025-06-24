function toggleDateContratto (){
    const tipoContratto = document.getElementById("tipoContratto").value;
    const dateContrattoDiv = document.getElementById("dateContratto")

    if(tipoContratto === "INDETERMINATO"){
        dateContrattoDiv.style.display = "none";
    }else{
        dateContrattoDiv.style.display = "block";
    }
}

/*Comportamento dinamico della select in automatico*/
document.addEventListener("DOMContentLoaded", function () {
    toggleDateContratto();
});