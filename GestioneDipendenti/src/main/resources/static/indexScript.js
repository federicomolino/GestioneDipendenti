function toggleDataInput (){
    const stato = document.getElementById("statoSelect").value;
    const ferieDiv = document.getElementById("ferieInput");
    const permessoDiv = document.getElementById("permessoInput");
    const oraEntrataDiv = document.getElementById("oraEntrataInput");
    const oraUscitaDiv = document.getElementById("oraUscitaInput");

    // Abilita/disabilita tutti gli input nei div Entrata/Uscita
    const setDisabledInputs = (div, disabled) => {
        const inputs = div.querySelectorAll("input");
        inputs.forEach(input => input.disabled = disabled);
    };

    /*Verifico se è stato inserito ferie*/
    if (stato === "FERIE"){
        ferieDiv.style.display = "block";
        permessoDiv.style.display = "none";

    }else if(stato === "PERMESSO"){
        permessoDiv.style.display = "block";
        ferieDiv.style.display = "none";
        oraEntrataDiv.style.display = "none";
        oraUscitaDiv.style.display = "none";
        setDisabledInputs(oraEntrataDiv, true);
        setDisabledInputs(oraUscitaDiv, true);

    }else{
        oraEntrataDiv.style.display = "block";
         oraUscitaDiv.style.display = "block";
        ferieDiv.style.display = "none";
        permessoDiv.style.display = "none";
    }
}

function timbraturaFeriali(){
    const addTimbratura = document.getElementById("addTimbratura");
    const dataGiornaliera = document.getElementById("dataGiornaliera");
    const alertWeekEnd = document.getElementById("alertWeekEnd");

    //Mi prendo la data
    const data = dataGiornaliera.textContent.trim();
    //converto da gg/mm/yyyy a ogetto date
    const [giorno, mese, anno] = data.split("/").map(Number);
    const dataNuova = new Date(anno, mese-1, giorno);
    const giornoSettimana = dataNuova.getDay();


    if(giornoSettimana == 0 || giornoSettimana == 6){
        alertWeekEnd.classList.remove("d-none");
        alertWeekEnd.classList.add("d-block");
        addTimbratura.disabled = true;
    }else{
        alertWeekEnd.classList.add("d-none");
        alertWeekEnd.classList.remove("d-block");
        addTimbratura.disabled = false;
    }
}

/*Comportamento dinamico della select in automatico*/
document.addEventListener("DOMContentLoaded", function () {
    timbraturaFeriali();
});
