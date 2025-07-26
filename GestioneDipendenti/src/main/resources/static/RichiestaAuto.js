function aggiungiEliminaAuto(){
    const inputRadio1 = document.getElementById("radioDefault1");
    const inputRadio2 = document.getElementById("radioDefault2");
    const aggiungiAutoDiv = document.getElementById("aggiungiAutoDiv");
    const eliminaAutoDiv = document.getElementById("eliminaAutoDiv");

    if(inputRadio1.checked){
        aggiungiAutoDiv.classList.remove("d-none");
        aggiungiAutoDiv.classList.add("d-block");

        eliminaAutoDiv.classList.add("d-none");
        eliminaAutoDiv.classList.remove("d-block");
    }

    if(inputRadio2.checked){
        eliminaAutoDiv.classList.remove("d-none");
        eliminaAutoDiv.classList.add("d-block");

        aggiungiAutoDiv.classList.remove("d-block");
        aggiungiAutoDiv.classList.add("d-none");
    }
}

function verificaCampiRichiestaAuto(){
    const richiediAutoButton = document.getElementById("richiediAuto");
    const idAutoInput = document.getElementById("idAutoInput").value.trim();

    if(idAutoInput === "" || idAutoInput === null){
        richiediAutoButton.disabled = true;
    }else{
        richiediAutoButton.disabled = false;
    }

    //Legge il valore successMessage da data-attribute
    const formRichiesta = document.getElementById("formRichiestaAuto");

    console.warn("prima della condizione");
    if (formRichiesta) {
        // Disabilita l'input username
        const inputUsername = document.getElementById("usernameUtenteRichiesta");
        const ricercaUtenteButton = document.getElementById("ricercaUtenteButton")
        if (inputUsername){
            inputUsername.disabled = true;
            ricercaUtenteButton.disabled = true;
        }

        // Mostra il messaggio
        const messaggio = document.getElementById("messageUtenteInserito");
        if (messaggio) messaggio.classList.remove("d-none");
    }

    console.warn("dopo");
}

document.addEventListener("DOMContentLoaded", function () {
    aggiungiEliminaAuto();
    verificaCampiRichiestaAuto();

    document.getElementById("radioDefault1").addEventListener("change", aggiungiEliminaAuto);
    document.getElementById("radioDefault2").addEventListener("change", aggiungiEliminaAuto);
    document.getElementById("idAutoInput").addEventListener("change", verificaCampiRichiestaAuto);
});