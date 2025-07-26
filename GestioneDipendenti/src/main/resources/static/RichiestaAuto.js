function aggiungiEliminaAuto(){
    const inputRadio1 = document.getElementById("radioDefault1");
    const inputRadio2 = document.getElementById("radioDefault2");
    const aggiungiAutoDiv = document.getElementById("aggiungiAutoDiv");
    const eliminaAutoDiv = document.getElementById("eliminaAutoDiv");

    if(inputRadio1 && inputRadio1.checked){
        aggiungiAutoDiv.classList.remove("d-none");
        aggiungiAutoDiv.classList.add("d-block");

        eliminaAutoDiv.classList.add("d-none");
        eliminaAutoDiv.classList.remove("d-block");
    }

    if(inputRadio2 && inputRadio2.checked){
        eliminaAutoDiv.classList.remove("d-none");
        eliminaAutoDiv.classList.add("d-block");

        aggiungiAutoDiv.classList.remove("d-block");
        aggiungiAutoDiv.classList.add("d-none");
    }
}

function verificaCampiRichiestaAuto(){
    const richiediAutoButton = document.getElementById("richiediAuto");
    const idAutoInput = document.getElementById("idAutoInput");

    if(!richiediAutoButton || !idAutoInput) return;

    const idAutoVal = idAutoInput.value.trim();

    richiediAutoButton.disabled = (idAutoVal === "");

    // Controllo se il form richiesta auto è presente (quindi se utente è stato verificato)
    const formRichiesta = document.getElementById("formRichiestaAuto");

    if (formRichiesta) {
        // Disabilita input username e bottone ricerca
        const inputUsername = document.getElementById("usernameUtenteRichiesta");
        const ricercaUtenteButton = document.getElementById("ricercaUtenteButton");
        if (inputUsername) inputUsername.disabled = true;
        if (ricercaUtenteButton) ricercaUtenteButton.disabled = true;

        // Mostra messaggio utente inserito
        const messaggio = document.getElementById("messageUtenteInserito");
        if (messaggio) messaggio.classList.remove("d-none");
    }
}

document.addEventListener("DOMContentLoaded", function () {
    aggiungiEliminaAuto();

    const radio1 = document.getElementById("radioDefault1");
    const radio2 = document.getElementById("radioDefault2");
    if (radio1) radio1.addEventListener("change", aggiungiEliminaAuto);
    if (radio2) radio2.addEventListener("change", aggiungiEliminaAuto);

    const idAutoInput = document.getElementById("idAutoInput");
    if (idAutoInput) {
        idAutoInput.addEventListener("input", verificaCampiRichiestaAuto);
        verificaCampiRichiestaAuto(); // verifica stato all'inizio
    }
});