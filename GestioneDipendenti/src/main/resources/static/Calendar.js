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