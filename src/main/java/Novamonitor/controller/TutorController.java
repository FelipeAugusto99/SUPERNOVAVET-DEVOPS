package Novamonitor.controller;

import Novamonitor.entity.Tutor;
import Novamonitor.repository.TutorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutores")
public class TutorController {

    @Autowired
    private TutorRepository repository;

    @GetMapping
    public List<Tutor> listar() {
        return repository.findAll();
    }

    @PostMapping
    public Tutor cadastrar(@Valid @RequestBody Tutor tutor) {
        return repository.save(tutor);
    }
}