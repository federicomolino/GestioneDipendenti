function aggiungiPresenza(){
    const motivazioneRichiesta = document.getElementById("motivazioneRichiesta");
    const inserisciPresenza = document.getElementById("inserisciPresenza");
    const selezionaDipendente = document.getElementById("selezionaDipendente");

    let motivazione = motivazioneRichiesta.value;
    let dipendente = selezionaDipendente.value;

    if(dipendente !== "" && motivazione === "MANCATA_TIMBRATURA"){
        inserisciPresenza.classList.remove("d-none");
        inserisciPresenza.classList.add("d-block");
    }else{
        inserisciPresenza.classList.remove("d-block");
        inserisciPresenza.classList.add("d-none");
    }
}

//Verifiche preliminari per abilitazione bottone
function aggiungiDatiPresenza(){
    const modalitaGiornataLavorativa = document.getElementById("modalitaGiornataLavorativa");
    const oraEntrata = document.getElementById("oraEntrata").value;
    const orauscita = document.getElementById("orauscita").value;
    const statoGiornataLavorativa = document.getElementById("statoGiornataLavorativa");
    const buttonAggiungiPresenza = document.getElementById("buttonAggiungiPresenza");

    let modalitaGiornataLavorativaValue = modalitaGiornataLavorativa.value;
    let statoGiornataLavorativaValue = statoGiornataLavorativa.value;

    //confronto le date
    const [hEntrata, mEntrata] = oraEntrata.split(":").map(Number);
    const [hUscita, mUscita] = orauscita.split(":").map(Number);

    const minutiEntrata = hEntrata * 60 + mEntrata;
    const minutiUscita = hUscita * 60 + mUscita;

    if(modalitaGiornataLavorativaValue !== "" && statoGiornataLavorativaValue !== "" &&
        minutiUscita > minutiEntrata){
        buttonAggiungiPresenza.disabled = false;
    }else{
        buttonAggiungiPresenza.disabled = true;
    }
}


/*Comportamento dinamico della select in automatico*/
document.addEventListener("DOMContentLoaded", function () {
    // Quando l'utente cambia l'orario di entrata o uscita
       document.getElementById("oraEntrata").addEventListener("input", aggiungiDatiPresenza);
       document.getElementById("orauscita").addEventListener("input", aggiungiDatiPresenza);

    // Quando l'utente cambia la modalità o lo stato della giornata
    document.getElementById("modalitaGiornataLavorativa").addEventListener("change", aggiungiDatiPresenza);
    document.getElementById("statoGiornataLavorativa").addEventListener("change", aggiungiDatiPresenza);

    document.getElementById("motivazioneRichiesta").addEventListener("change",aggiungiPresenza);
    document.getElementById("selezionaDipendente").addEventListener("change", aggiungiPresenza);
});