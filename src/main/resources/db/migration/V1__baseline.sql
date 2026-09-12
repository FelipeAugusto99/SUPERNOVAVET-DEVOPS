CREATE TABLE ch_tutor (
    id_tutor BIGSERIAL PRIMARY KEY,
    nm_tutor VARCHAR(255) NOT NULL,
    ds_email VARCHAR(255) NOT NULL,
    nr_telefone VARCHAR(11) NOT NULL
);

CREATE TABLE ch_pet (
    id_pet BIGSERIAL PRIMARY KEY,
    nm_pet VARCHAR(255) NOT NULL,
    nr_idade INTEGER NOT NULL,
    ds_especie VARCHAR(100) NOT NULL,
    ds_nivel_risco VARCHAR(20) NOT NULL,
    id_tutor BIGINT NOT NULL,

    CONSTRAINT fk_pet_tutor
        FOREIGN KEY (id_tutor)
        REFERENCES ch_tutor(id_tutor),

    CONSTRAINT ck_pet_idade
        CHECK (nr_idade BETWEEN 0 AND 30),

    CONSTRAINT ck_pet_nivel_risco
        CHECK (ds_nivel_risco IN ('BAIXO', 'MEDIO', 'ALTO'))
);