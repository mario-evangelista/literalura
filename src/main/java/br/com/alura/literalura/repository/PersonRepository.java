package br.com.alura.literalura.repository;

import br.com.alura.literalura.dominio.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    @Query("SELECT a FROM Person a WHERE :year BETWEEN a.birthYear AND COALESCE(a.deathYear, :year)")
    List<Person> findAuthorsAliveInYear(@Param("year") int year);

    Optional<Person> findByName(String name);

    @Query("SELECT a FROM Person a LEFT JOIN FETCH a.authoredBooks WHERE upper(a.name) LIKE upper(concat('%', :name, '%'))")
    List<Person> findByNameContainingIgnoreCaseWithBooks(String name);
}