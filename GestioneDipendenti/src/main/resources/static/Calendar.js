const giornataChiusa = document.getElementById("giornataChiusa");
const giornataAperta = document.getElementById("giornataAperta");
const switchCheckbox = document.querySelector(".form-check-input");

function aggiornaBadge(){
    if(switchCheckbox.checked){
        giornataChiusa.style.display = "none";
        giornataAperta.style.display = "inline-block";
    }else{
        giornataChiusa.style.display = "inline-block";
        giornataAperta.style.display = "none";
    }
}

aggiornaBadge();

// Riascolta l'evento ogni volta che cambia
switchCheckbox.addEventListener("change",aggiornaBadge);