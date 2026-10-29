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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetControllerTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private TutorRepository tutorRepository;

    @InjectMocks
    private PetController controller;

    private Pet criarPet(String nome, String risco) {
        Tutor tutor = new Tutor();
        tutor.setEmail("admin@supernovavet.com");

        Pet pet = new Pet();
        pet.setNome(nome);
        pet.setIdade(4);
        pet.setEspecie("CACHORRO");
        pet.setNivelRisco(risco);
        pet.setTutor(tutor);
        return pet;
    }

    @Test
    void deveUsarTutorJaCadastradoAoCriarPet() {
        Tutor tutorBanco = new Tutor();
        tutorBanco.setId(1L);
        Pet pet = criarPet("Thor", "MEDIO");

        when(tutorRepository.findByEmail("admin@supernovavet.com"))
                .thenReturn(Optional.of(tutorBanco));
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        Pet salvo = controller.cadastrar(pet);

        assertSame(tutorBanco, salvo.getTutor());
        verify(tutorRepository, never()).save(any());
    }

    @Test
    void deveCriarTutorQuandoNaoExisteAoCriarPet() {
        Pet pet = criarPet("Thor", "MEDIO");

        when(tutorRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(tutorRepository.save(any(Tutor.class))).thenAnswer(i -> i.getArgument(0));
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        controller.cadastrar(pet);

        verify(tutorRepository).save(pet.getTutor());
    }

    @Test
    void deveConverterRiscoParaMaiusculo() {
        when(petRepository.findByNivelRisco("ALTO")).thenReturn(List.of(criarPet("Luna", "ALTO")));

        List<Pet> pets = controller.buscarPorRisco("alto");

        assertEquals(1, pets.size());
    }

    @Test
    void deveMontarResumoPorRisco() {
        when(petRepository.findByNivelRisco("BAIXO")).thenReturn(List.of(criarPet("A", "BAIXO")));
        when(petRepository.findByNivelRisco("MEDIO")).thenReturn(List.of());
        when(petRepository.findByNivelRisco("ALTO"))
                .thenReturn(List.of(criarPet("B", "ALTO"), criarPet("C", "ALTO")));
        when(petRepository.findAll())
                .thenReturn(List.of(criarPet("A", "BAIXO"), criarPet("B", "ALTO"), criarPet("C", "ALTO")));

        Map<String, Integer> resumo = controller.resumoPorRisco();

        assertEquals(1, resumo.get("BAIXO"));
        assertEquals(0, resumo.get("MEDIO"));
        assertEquals(2, resumo.get("ALTO"));
        assertEquals(3, resumo.get("TOTAL"));
    }

    @Test
    void deveDeletarPet() {
        String resposta = controller.deletar(5L);

        verify(petRepository).deleteById(5L);
        assertEquals("Pet deletado com sucesso!", resposta);
    }
}
