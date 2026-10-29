package Novamonitor.controller;

import Novamonitor.entity.Pet;
import Novamonitor.entity.Tutor;
import Novamonitor.repository.PetRepository;
import Novamonitor.repository.TutorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorControllerTest {

    @Mock
    private TutorRepository repository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private TutorController controller;

    private Tutor criarTutor(String nome, String email) {
        Tutor tutor = new Tutor();
        tutor.setNome(nome);
        tutor.setEmail(email);
        tutor.setTelefone("11977776666");
        tutor.setPerfil("TUTOR");
        return tutor;
    }

    @Test
    void deveCadastrarTutor() {
        Tutor tutor = criarTutor("Tutor Teste", "tutor.teste@supernovavet.com");
        when(repository.save(tutor)).thenReturn(tutor);

        Tutor salvo = controller.cadastrar(tutor);

        assertEquals("Tutor Teste", salvo.getNome());
        verify(repository).save(tutor);
    }

    @Test
    void deveAtualizarSomenteNomeEmailETelefone() {
        Tutor existente = criarTutor("Tutor Teste", "tutor.teste@supernovavet.com");
        existente.setId(3L);
        Tutor novosDados = criarTutor("Tutor Atualizado", "tutor.atualizado@supernovavet.com");
        novosDados.setTelefone("11966665555");
        novosDados.setPerfil("ADMIN");

        when(repository.findById(3L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Tutor.class))).thenAnswer(i -> i.getArgument(0));

        Tutor atualizado = controller.atualizar(3L, novosDados);

        assertEquals("Tutor Atualizado", atualizado.getNome());
        assertEquals("tutor.atualizado@supernovavet.com", atualizado.getEmail());
        assertEquals("11966665555", atualizado.getTelefone());
        assertEquals("TUTOR", atualizado.getPerfil());
    }

    @Test
    void deveRetornar404AoAtualizarTutorInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> controller.atualizar(99L, criarTutor("X", "x@x.com")));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }

    @Test
    void deveExcluirTutorSemPets() {
        when(repository.existsById(3L)).thenReturn(true);
        when(petRepository.findByTutorId(3L)).thenReturn(List.of());

        controller.excluir(3L);

        verify(repository).deleteById(3L);
    }

    @Test
    void naoDeveExcluirTutorComPetsVinculados() {
        when(repository.existsById(1L)).thenReturn(true);
        when(petRepository.findByTutorId(1L)).thenReturn(List.of(new Pet()));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> controller.excluir(1L));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(repository, never()).deleteById(any());
    }

    @Test
    void deveRetornar404AoExcluirTutorInexistente() {
        when(repository.existsById(99L)).thenReturn(false);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> controller.excluir(99L));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }
}
