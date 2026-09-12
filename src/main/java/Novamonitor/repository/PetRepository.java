package Novamonitor.repository;

import Novamonitor.entity.Pet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByNivelRisco(String nivelRisco);

    List<Pet> findByEspecie(String especie);

    List<Pet> findByTutorId(Long tutorId);
}