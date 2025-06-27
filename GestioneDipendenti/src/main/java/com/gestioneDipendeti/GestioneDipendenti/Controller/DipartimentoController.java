package com.gestioneDipendeti.GestioneDipendenti.Controller;

import com.gestioneDipendeti.GestioneDipendenti.Entity.Dipartimento;
import com.gestioneDipendeti.GestioneDipendenti.Repository.DipartimentoRepository;
import com.gestioneDipendeti.GestioneDipendenti.Service.DipartimentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("dipartimento")
public class DipartimentoController {

    @Autowired
    private DipartimentoRepository dipartimentoRepository;

    @Autowired
    private DipartimentoService dipartimentoService;

    @GetMapping("/nuovo-dipartimento")
    public String showNewDipartimento(Model model){
        model.addAttribute("formNewDipartimento", new Dipartimento());
        model.addAttribute("dipartimenti", dipartimentoRepository.findAll());
        return "Dipartimento/CreaDipartimento";
    }

    @PostMapping("/nuovo-dipartimento")
    public String newDipartimento(@Valid @ModelAttribute("formNewDipartimento") Dipartimento dipartimento, BindingResult
                                  bindingResult, RedirectAttributes redirectAttributes, Model model){

        if (bindingResult.hasErrors()){
            model.addAttribute("dipartimenti", dipartimentoRepository.findAll());
            return "Dipartimento/CreaDipartimento";
        }
        Dipartimento dipartimentoCreato = dipartimentoRepository.findByDipartimento(dipartimento.getDipartimento().trim());
        if (dipartimentoCreato != null){
            bindingResult.rejectValue("dipartimento","errorDipartimentoNome","dipartimento già esistente");
            model.addAttribute("dipartimenti", dipartimentoRepository.findAll());
            return "Dipartimento/CreaDipartimento";
        }

        dipartimentoService.addDipartimento(dipartimento);
        redirectAttributes.addFlashAttribute("successMessage", "Diparimento creato correttamente!!");
        return "redirect:/dipartimento/nuovo-dipartimento";
    }
}
