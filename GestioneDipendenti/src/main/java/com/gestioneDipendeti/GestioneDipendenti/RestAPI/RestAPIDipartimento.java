package com.gestioneDipendeti.GestioneDipendenti.RestAPI;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipartimento;
import com.gestioneDipendeti.GestioneDipendenti.Repository.DipartimentoRepository;
import com.gestioneDipendeti.GestioneDipendenti.Repository.RuoloRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.DipartimentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/dipartimento")
public class RestAPIDipartimento {

    @Autowired
    private DipartimentoRepository dipartimentoRepository;

    @Autowired
    private DipartimentoService dipartimentoService;

    @Autowired
    private RuoloRepository ruoloRepository;

    private static final Logger logApiDipartimento = Logger.getLogger(RestAPIDipartimento.class.getName());

    @GetMapping
    public List<Dipartimento> showDipartimento(){
        List<Dipartimento> dipartimento = dipartimentoRepository.findAll();
        if (dipartimento.isEmpty()){
            ResponseEntity.notFound().build();
            return dipartimento;
        }
        ResponseEntity.ok(dipartimento);
        return dipartimento;
    }

    @PostMapping()
    public ResponseEntity<Dipartimento> addDipartimento(@RequestBody Dipartimento dipartimento){
        try {
            dipartimentoService.addDipartimento(dipartimento);
            logApiDipartimento.info("Dipartimento Creato");
            return ResponseEntity.ok(dipartimento);
        }catch (Exception ex){
            logApiDipartimento.info("POST /dipartimenti - Error: {}" + dipartimento);
            logApiDipartimento.warning("Nessun Dipartimento creato");
            return ResponseEntity.badRequest().body(dipartimento);
        }
    }
}
