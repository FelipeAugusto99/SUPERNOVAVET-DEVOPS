package Novamonitor.controller;

import Novamonitor.entity.Tutor;
import Novamonitor.repository.PetRepository;
import Novamonitor.repository.TutorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/tutores")
public class TutorController {

    @Autowired
    private TutorRepository repository;

    @Autowired
    private PetRepository petRepository;

    @GetMapping
    public List<Tutor> listar() {
        return repository.findAll();
    }

    @PostMapping
    public Tutor cadastrar(@Valid @RequestBody Tutor tutor) {
        return repository.save(tutor);
    }

    @PutMapping("/{id}")
    public Tutor atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Tutor tutor
    ) {
        Tutor tutorExistente = repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Tutor não encontrado"
                        )
                );

        tutorExistente.setNome(tutor.getNome());
        tutorExistente.setEmail(tutor.getEmail());
        tutorExistente.setTelefone(tutor.getTelefone());

        return repository.save(tutorExistente);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Tutor não encontrado"
            );
        }

        if (!petRepository.findByTutorId(id).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível excluir o tutor porque existem pets vinculados a ele."
            );
        }

        repository.deleteById(id);
    }
}