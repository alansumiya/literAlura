package br.com.alura.literAlura.repository;

import br.com.alura.literAlura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNome(String nome);
    @Query("SELECT a from Autor a WHERE :anoReferencia >= a.dataNascimento AND :anoReferencia <= a.dataFalecimento")
    List<Autor> autorVivoDeterminadoAno(Integer anoReferencia);
}
