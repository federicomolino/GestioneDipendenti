document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('select.dipartimento-select').forEach(select => {
    select.addEventListener('change', function() {
      if (this.value) {
        document.querySelectorAll('select.dipartimento-select').forEach(s => {
          if (s !== this) {
            s.disabled = true;
          }
        });
      } else {
        document.querySelectorAll('select.dipartimento-select').forEach(s => {
          s.disabled = false;
        });
      }
    });
  });
});