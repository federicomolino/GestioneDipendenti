function aggiungiPresenza(){
    const motivazioneRichiesta = document.getElementById("motivazioneRichiesta");
    const inserisciPresenza = document.getElementById("inserisciPresenza");
    const selezionaDipendente = document.getElementById("selezionaDipendente");
    const richiestaFerie = document.getElementById("richiestaFerie");
    const buttonAggiungiFerie = document.getElementById("buttonAggiungiFerie");
    const inserisciRisposta = document.getElementById("inserisciRisposta");
    const rispostaGenerica = document.getElementById("rispostaGenerica");
    const buttonAggiungiRisposta = document.getElementById("buttonAggiungiRisposta");
    const textAreaRispostaGenerica = document.getElementById("textAreaRispostaGenerica");


    let motivazione = motivazioneRichiesta.value;
    let dipendente = selezionaDipendente.value;

    if(dipendente !== "" && motivazione === "MANCATA_TIMBRATURA"){
        inserisciPresenza.classList.remove("d-none");
        inserisciPresenza.classList.add("d-block");

        rispostaGenerica.classList.add("d-none");
        rispostaGenerica.classList.remove("d-block");
    }else{
        inserisciPresenza.classList.remove("d-block");
        inserisciPresenza.classList.add("d-none");
    }

    if(dipendente !== "" && motivazione === "FERIE"){
            richiestaFerie.classList.remove("d-none");
            richiestaFerie.classList.add("d-block");

            inserisciRisposta.classList.remove("d-none");
            inserisciRisposta.classList.add("d-block");

            rispostaGenerica.classList.add("d-none");
            rispostaGenerica.classList.remove("d-block");

            buttonAggiungiFerie.disabled = false;
    }else{
        richiestaFerie.classList.remove("d-block");
        richiestaFerie.classList.add("d-none");
    }

    if(dipendente !== "" && motivazione === "RICHIESTA_GENERICA"){
        rispostaGenerica.classList.remove("d-none");
        rispostaGenerica.classList.add("d-block");

        if(textAreaRispostaGenerica.value.trim() !== ""){
            buttonAggiungiRisposta.disabled = false;
        }else{
            buttonAggiungiRisposta.disabled = true;
        }
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
    document.getElementById("richiestaFerie").addEventListener("change", aggiungiPresenza);
    // Listener per abilitare/disabilitare bottone mentre scrivi nella textarea
    document.getElementById("textAreaRispostaGenerica").addEventListener("input", function() {
        const button = document.getElementById("buttonAggiungiRisposta");
        button.disabled = this.value.trim() === "";
    });
});