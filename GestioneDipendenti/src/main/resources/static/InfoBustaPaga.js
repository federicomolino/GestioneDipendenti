function RicercaPerMeseDefault(){
    const ricercaPerMese = document.getElementById("ricercaPerMese");

    const dataDiOggi = new Date();
    dataDiOggi.setMonth(dataDiOggi.getMonth()-1);

    //Mi formatto la data in YYYY-MM
    const meseCorrente = dataDiOggi.toISOString().slice(0,7);

    ricercaPerMese.value = meseCorrente;
}

function GeneraBustePaghe(){
    const generaBustePagheBtn = document.getElementById("generaBustePaghe");

    const dataDiOggi = new Date();
    const giorno = dataDiOggi.getDate();

    generaBustePagheBtn.disabled = !(giorno >= 1 && giorno<=5);
}

document.addEventListener("DOMContentLoaded", function () {
    GeneraBustePaghe();
    RicercaPerMeseDefault();
});