function toggleTextAreaRichiesta(){
    const textAreaRichiesta = document.getElementById("textAreaRichiesta");
    const errorTextAreaDiv = document.getElementById("errorTextAreaDiv");
    const errorLenghtTextAreaDiv = document.getElementById("errorLenghtTextAreaDiv");
    const buttonInoltraRichiesta = document.getElementById("buttonInoltraRichiesta")
    const selezionaResponsabile = document.getElementById("selezionaResponsabile").value;
    const motivazioneRichiesta = document.getElementById("motivazioneRichiesta").value;

    const value = textAreaRichiesta.value.trim();

    if(value === ""){
         errorTextAreaDiv.classList.remove("d-none");
    }else{
        errorTextAreaDiv.classList.add("d-none");
    }

    if(value.length === 255){
        errorLenghtTextAreaDiv.classList.add("d-block");
        errorLenghtTextAreaDiv.classList.remove("d-none");
    }else{
        errorLenghtTextAreaDiv.classList.add("d-none");
        errorLenghtTextAreaDiv.classList.remove("d-block");
    }

    //Verifico quando abilitare il buttone
    if(value !== "" && value.length <= 255 && selezionaResponsabile !== "" && motivazioneRichiesta !== ""){
        buttonInoltraRichiesta.disabled = false;
    }else{
        buttonInoltraRichiesta.disabled = true;
    }
}

/*Comportamento dinamico della select in automatico*/
document.addEventListener("DOMContentLoaded", function () {
    toggleTextAreaRichiesta();
});