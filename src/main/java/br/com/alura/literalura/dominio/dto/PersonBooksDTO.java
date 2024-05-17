package br.com.alura.literalura.dominio.dto;

import br.com.alura.literalura.dominio.entity.Person;
import br.com.alura.literalura.dominio.entity.Book;

import java.util.List;

public class PersonBooksDTO {

    private final Person person;
    private final List<Book> books;

    public PersonBooksDTO(Person person, List<Book> books) {
        this.person = person;
        this.books = books;
    }

    public Person getAuthor() {
        return person;
    }

    public List<Book> getBooks() {
        return books;
    }
}
