package br.com.alura.literalura.repository;

import br.com.alura.literalura.dominio.entity.Person;
import br.com.alura.literalura.dominio.entity.Book;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Transactional()
    @Override
    List<Book> findAll();

    List<Book> findByAuthors(Person person);

    List<Book> findByLanguagesContaining(String language);

    List<Book> findTop10ByOrderByDownloadCountDesc();
}
