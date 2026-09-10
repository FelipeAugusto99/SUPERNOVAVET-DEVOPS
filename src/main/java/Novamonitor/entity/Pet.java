package Novamonitor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "ch_pet")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pet")
    private Long id;

    @NotBlank(message = "O nome do pet é obrigatório")
    @Column(name = "nm_pet")
    private String nome;

    @NotNull(message = "A idade é obrigatória")
    @Min(value = 0, message = "A idade não pode ser negativa")
    @Max(value = 30, message = "A idade deve ser menor ou igual a 30")
    @Column(name = "nr_idade")
    private Integer idade;

    @NotBlank(message = "A espécie é obrigatória")
    @Column(name = "ds_especie")
    private String especie;

    @NotBlank(message = "O nível de risco é obrigatório")
    @Pattern(
            regexp = "BAIXO|MEDIO|ALTO",
            message = "O nível de risco deve ser BAIXO, MEDIO ou ALTO"
    )
    @Column(name = "ds_nivel_risco")
    private String nivelRisco;

    @NotNull(message = "O tutor é obrigatório")
    @ManyToOne
    @JoinColumn(name = "id_tutor")
    private Tutor tutor;
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getNivelRisco() {
        return nivelRisco;
    }

    public void setNivelRisco(String nivelRisco) {
        this.nivelRisco = nivelRisco;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }
}