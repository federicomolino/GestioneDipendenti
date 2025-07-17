document.querySelectorAll(".form-check-input").forEach((checkbox) => {
    const container = checkbox.closest(".d-flex");
    const badgeChiusa = container.querySelector(".giornataChiusa");
    const badgeAperta = container.querySelector(".giornataAperta");

    // Funzione per aggiornare il badge in base allo stato dello switch
    function aggiornaBadge() {
        if (checkbox.checked) {
            badgeChiusa.style.display = "none";
            badgeAperta.style.display = "inline-block";
        } else {
            badgeChiusa.style.display = "inline-block";
            badgeAperta.style.display = "none";
        }
    }

    // Imposta lo stato iniziale
    aggiornaBadge();

    // Riascolta il cambiamento dello switch
    checkbox.addEventListener("change", aggiornaBadge);
});


//Cambio pagine e visualizzo 5 per volta
let pagina = 1;
const righePerPagina = 5;

function mostraPagina(){
    const paginaCorrente = document.getElementById("paginaCorrente");
    const righe = document.querySelectorAll(".presenza-row");
    //arrotonda un numero decimale all'intero più piccolo che è maggiore o uguale al numero dato
    const totalePagine = Math.ceil(righe.length / righePerPagina);

    // Blocca la pagina tra 1 e totalePagine
    if (pagina < 1) pagina = 1;
    if (pagina > totalePagine) pagina = totalePagine;

    // Mostra solo le righe della pagina corrente
    righe.forEach((riga, index) => {
        if (index >= (pagina - 1) * righePerPagina && index < pagina * righePerPagina) {
            riga.style.display = "";
        } else {
            riga.style.display = "none";
        }
    });

    // Aggiorna numero di pagina visualizzato
    if (paginaCorrente) {
        paginaCorrente.textContent = pagina;
    }
}

function cambiaPagina(direzione) {
    pagina -= direzione;
    mostraPagina();
}

function selezioneRicerca(){
    const selectOpzioneDiv = document.getElementById("selectOpzioneDiv");
    const ricercaPerGiornoDiv = document.getElementById("ricercaPerGiornoDiv");
    const ricercaPerMeseDiv = document.getElementById("ricercaPerMeseDiv");
    const selectOpzione = document.getElementById("selectOpzione");
    const buttonRicercaDiv = document.getElementById("buttonRicercaDiv");
    const refreshPage = document.getElementById("refreshPage")

    const scelta = selectOpzione.value;

    if(scelta === ""){
        ricercaPerGiornoDiv.classList.add("d-none");
        ricercaPerGiornoDiv.classList.remove("d-block");

        ricercaPerMeseDiv.classList.add("d-none");
        ricercaPerMeseDiv.classList.remove("d-block");

        buttonRicercaDiv.classList.add("d-none");
        buttonRicercaDiv.classList.remove("d-block");

        refreshPage.classList.add("d-block");
        refreshPage.classList.remove("d-none");

    }else if(scelta === "Data"){
        ricercaPerGiornoDiv.classList.remove("d-none");
        ricercaPerGiornoDiv.classList.add("d-block");

        ricercaPerMeseDiv.classList.add("d-none");
        ricercaPerMeseDiv.classList.remove("d-block");

        buttonRicercaDiv.classList.remove("d-none");
        buttonRicercaDiv.classList.add("d-block");
    }else{
       ricercaPerMeseDiv.classList.remove("d-none");
       ricercaPerMeseDiv.classList.add("d-block");

       ricercaPerGiornoDiv.classList.add("d-none");
       ricercaPerGiornoDiv.classList.remove("d-block");

       buttonRicercaDiv.classList.remove("d-none");
       buttonRicercaDiv.classList.add("d-block");
    }
}

document.addEventListener("DOMContentLoaded", () => {
    mostraPagina();
    const select = document.getElementById("selectOpzione");
    if (select) {
        select.addEventListener("change", selezioneRicerca);
    }
    selezioneRicerca()
});