package Novamonitor.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ValidacaoEntidadesTest {

    private static Validator validator;

    @BeforeAll
    static void configurar() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private Pet criarPetValido() {
        Pet pet = new Pet();
        pet.setNome("Luna");
        pet.setIdade(6);
        pet.setEspecie("CACHORRO");
        pet.setNivelRisco("BAIXO");
        pet.setTutor(new Tutor());
        return pet;
    }

    @Test
    void petValidoNaoDeveTerErros() {
        assertTrue(validator.validate(criarPetValido()).isEmpty());
    }

    @Test
    void petComRiscoInvalidoDeveSerRejeitado() {
        Pet pet = criarPetValido();
        pet.setNivelRisco("CRITICO");

        Set<ConstraintViolation<Pet>> erros = validator.validate(pet);

        assertEquals(1, erros.size());
        assertEquals("nivelRisco", erros.iterator().next().getPropertyPath().toString());
    }

    @Test
    void petComIdadeAcimaDe30DeveSerRejeitado() {
        Pet pet = criarPetValido();
        pet.setIdade(31);

        assertFalse(validator.validate(pet).isEmpty());
    }

    @Test
    void tutorComTelefoneOuEmailInvalidoDeveSerRejeitado() {
        Tutor tutor = new Tutor();
        tutor.setNome("Tutor Teste");
        tutor.setEmail("email-invalido");
        tutor.setTelefone("123");

        Set<ConstraintViolation<Tutor>> erros = validator.validate(tutor);

        assertEquals(2, erros.size());
    }
}
