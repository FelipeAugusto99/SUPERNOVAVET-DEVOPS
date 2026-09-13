-- ============================================================
-- SUPERNOVA VET
-- Script de criação das tabelas principais da aplicação
-- Banco de dados: PostgreSQL
-- ============================================================


-- ============================================================
-- TABELA: ch_tutor
-- Armazena os tutores responsáveis pelos pets.
-- ============================================================

CREATE TABLE ch_tutor (
    id_tutor BIGSERIAL PRIMARY KEY,
    nm_tutor VARCHAR(255) NOT NULL,
    ds_email VARCHAR(255) NOT NULL,
    nr_telefone VARCHAR(11) NOT NULL,
    ds_senha VARCHAR(255),
    ds_perfil VARCHAR(30)
);

COMMENT ON TABLE ch_tutor IS
'Tabela responsável por armazenar os tutores dos pets.';

COMMENT ON COLUMN ch_tutor.id_tutor IS
'Identificador único do tutor.';

COMMENT ON COLUMN ch_tutor.nm_tutor IS
'Nome do tutor.';

COMMENT ON COLUMN ch_tutor.ds_email IS
'Endereço de e-mail do tutor.';

COMMENT ON COLUMN ch_tutor.nr_telefone IS
'Número de telefone do tutor.';

COMMENT ON COLUMN ch_tutor.ds_senha IS
'Senha criptografada utilizada para autenticação.';

COMMENT ON COLUMN ch_tutor.ds_perfil IS
'Perfil de acesso associado ao tutor.';


-- ============================================================
-- TABELA: ch_pet
-- Armazena os pets cadastrados no sistema.
-- Cada pet está relacionado a um tutor.
-- ============================================================

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

COMMENT ON TABLE ch_pet IS
'Tabela responsável por armazenar os pets cadastrados.';

COMMENT ON COLUMN ch_pet.id_pet IS
'Identificador único do pet.';

COMMENT ON COLUMN ch_pet.nm_pet IS
'Nome do pet.';

COMMENT ON COLUMN ch_pet.nr_idade IS
'Idade do pet em anos.';

COMMENT ON COLUMN ch_pet.ds_especie IS
'Espécie do pet.';

COMMENT ON COLUMN ch_pet.ds_nivel_risco IS
'Nível de risco do pet: BAIXO, MEDIO ou ALTO.';

COMMENT ON COLUMN ch_pet.id_tutor IS
'Identificador do tutor responsável pelo pet.';