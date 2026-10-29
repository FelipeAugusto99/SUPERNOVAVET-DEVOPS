package Novamonitor.service;

import Novamonitor.entity.Pet;
import Novamonitor.entity.Tutor;
import Novamonitor.repository.PetRepository;
import Novamonitor.repository.TutorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetserviceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private TutorRepository tutorRepository;

    @InjectMocks
    private Petservice petservice;

    private Tutor criarTutor() {
        Tutor tutor = new Tutor();
        tutor.setId(1L);
        tutor.setNome("Administrador");
        tutor.setEmail("admin@supernovavet.com");
        tutor.setTelefone("11999999999");
        return tutor;
    }

    private Pet criarPet(String nome, String risco) {
        Pet pet = new Pet();
        pet.setNome(nome);
        pet.setIdade(6);
        pet.setEspecie("CACHORRO");
        pet.setNivelRisco(risco);
        pet.setTutor(criarTutor());
        return pet;
    }

    @Test
    void deveListarTodosOsPets() {
        when(petRepository.findAll())
                .thenReturn(List.of(criarPet("Luna", "BAIXO"), criarPet("Thor", "MEDIO")));

        List<Pet> pets = petservice.listar();

        assertEquals(2, pets.size());
        verify(petRepository).findAll();
    }

    @Test
    void deveRetornarNullQuandoPetNaoExiste() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(petservice.buscarPorId(99L));
    }

    @Test
    void deveCadastrarPetComTutorExistente() {
        Tutor tutorBanco = criarTutor();
        Pet pet = criarPet("Luna", "BAIXO");

        when(tutorRepository.findByEmail("admin@supernovavet.com"))
                .thenReturn(Optional.of(tutorBanco));
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        Pet salvo = petservice.cadastrar(pet);

        assertSame(tutorBanco, salvo.getTutor());
        verify(petRepository).save(pet);
    }

    @Test
    void deveLancarErroAoCadastrarPetComTutorInexistente() {
        Pet pet = criarPet("Luna", "BAIXO");

        when(tutorRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> petservice.cadastrar(pet));
        verify(petRepository, never()).save(any());
    }

    @Test
    void deveAtualizarPetExistente() {
        Pet existente = criarPet("Luna", "BAIXO");
        existente.setId(1L);
        Pet novosDados = criarPet("Luna Atualizada", "ALTO");
        novosDados.setIdade(7);

        when(petRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        Pet atualizado = petservice.atualizar(1L, novosDados);

        assertEquals("Luna Atualizada", atualizado.getNome());
        assertEquals(7, atualizado.getIdade());
        assertEquals("ALTO", atualizado.getNivelRisco());
    }

    @Test
    void deveRetornarNullAoAtualizarPetInexistente() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(petservice.atualizar(99L, criarPet("X", "BAIXO")));
        verify(petRepository, never()).save(any());
    }

    @Test
    void deveDeletarPet() {
        petservice.deletar(1L);

        verify(petRepository).deleteById(1L);
    }
}
