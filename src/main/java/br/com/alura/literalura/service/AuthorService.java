package br.com.alura.literalura.service;

import br.com.alura.literalura.dominio.dto.PersonBooksDTO;
import br.com.alura.literalura.dominio.entity.Person;
import br.com.alura.literalura.dominio.entity.Book;
import br.com.alura.literalura.repository.PersonRepository;
import br.com.alura.literalura.repository.BookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    @Autowired
    private PersonRepository authorRepository;
    @Autowired
    private final BookRepository bookRepository;
    @Autowired
    private EntityManager entityManager;

    @Autowired
    public AuthorService(PersonRepository personRepository, BookRepository bookRepository) {
        this.authorRepository = personRepository;
        this.bookRepository = bookRepository;
    }

    public List<PersonBooksDTO> getAllAuthorsWithBooks() {
        List<Person> people = authorRepository.findAll();
        return people.stream().map(author -> {
            List<Book> books = bookRepository.findByAuthors(author);
            return new PersonBooksDTO(author, books);
        }).collect(Collectors.toList());
    }

    public void displayAuthorsWithBooks() {
        List<PersonBooksDTO> authorsWithBooks = getAllAuthorsWithBooks();
        for (PersonBooksDTO authorWithBooks : authorsWithBooks) {
            System.out.println("\n------------AUTOR------------");
            System.out.println("Autor: " + authorWithBooks.getAuthor().getName());
            System.out.println("Livros:");
            for (Book book : authorWithBooks.getBooks()) {
                System.out.println("- " + book.getTitle());
            }
            System.out.println();
        }
        System.out.println("-----------------------------\n");
    }

    @Transactional
    public void listAuthorsAliveInYear(int year) {
        // Buscar todos os autores do banco de dados
        List<Person> authors = authorRepository.findAll();

        // Se a lista de autores estiver vazia, imprimir uma mensagem e retornar
        if (authors.isEmpty()) {
            System.out.println("Nenhum autor encontrado no banco de dados.");
            return;
        }

        // Iterar sobre cada autor para verificar se está vivo no ano fornecido
        for (Person author : authors) {
            // Verificar se o ano de falecimento está presente e se é maior que o ano fornecido
            if (author.getDeathYear() == null || author.getDeathYear() > year) {
                // Imprimir os detalhes do autor
                System.out.println("\n------------ AUTOR ------------");
                System.out.println("Nome: " + author.getName());
                System.out.println("Ano de Nascimento: " + author.getBirthYear());
                System.out.println("Ano de Falecimento: " + (author.getDeathYear() != null ? author.getDeathYear() : "Vivo"));

                // Buscar os livros associados a esse autor
                List<Book> authoredBooks = author.getAuthoredBooks();
                System.out.println("Livros:");
                if (authoredBooks.isEmpty()) {
                    System.out.println("   - Nenhum livro encontrado para este autor.");
                } else {
                    for (Book book : authoredBooks) {
                        System.out.println("   - " + book.getTitle());
                    }
                }
                System.out.println("------------------------------\n");
            }
        }
    }

    // Buscar autor por nome
    public void findAuthorByName(String name) {
        List<Person> authors = authorRepository.findByNameContainingIgnoreCaseWithBooks(name);
        if (authors.isEmpty()) {
            System.out.println("\nNenhum autor encontrado com o nome: " + name);
            return;
        }
        for (Person author : authors) {
            System.out.println("\n------------ AUTOR ------------");
            System.out.println("Nome: " + (author.getName() != null ? author.getName() : "N/A"));
            System.out.println("Ano de Nascimento: " + (author.getBirthYear() != null ? author.getBirthYear() : "N/A"));
            System.out.println("Ano de Falecimento: " + (author.getDeathYear() != null ? author.getDeathYear() : "N/A"));
            System.out.println("Livros:");
            if (author.getAuthoredBooks() != null && !author.getAuthoredBooks().isEmpty()) {
                for (Book book : author.getAuthoredBooks()) {
                    System.out.println("   - " + book.getTitle());
                }
            } else {
                System.out.println("   - N/A");
            }
            System.out.println("-------------------------------\n");
        }
    }

    @Transactional
    public void displayAuthorsByYearOfBirth(int birthYear) {
        TypedQuery<Person> query = entityManager.createQuery(
                "SELECT a FROM Person a WHERE a.birthYear = :year", Person.class);
        query.setParameter("year", birthYear);
        List<Person> authors = query.getResultList();

        if (authors.isEmpty()) {
            System.out.println("\nNenhum autor encontrado com o ano de nascimento: " + birthYear);
            return;
        }

        for (Person author : authors) {
            System.out.println("\n------------ AUTOR ------------");
            System.out.println("Nome: " + (author.getName() != null ? author.getName() : "N/A"));
            System.out.println("Ano de Nascimento: " + (author.getBirthYear() != null ? author.getBirthYear() : "N/A"));
            System.out.println("Ano de Falecimento: " + (author.getDeathYear() != null ? author.getDeathYear() : "N/A"));
            System.out.println("Livros:");
            if (author.getAuthoredBooks() != null && !author.getAuthoredBooks().isEmpty()) {
                for (Book book : author.getAuthoredBooks()) {
                    System.out.println("   - " + book.getTitle());
                }
            } else {
                System.out.println("   - N/A");
            }
            System.out.println("-------------------------------\n");
        }
    }

    @Transactional
    public void displayAuthorsByYearOfDeath(int deathYear) {
        TypedQuery<Person> query = entityManager.createQuery(
                "SELECT a FROM Person a WHERE a.deathYear = :year", Person.class);
        query.setParameter("year", deathYear);
        List<Person> authors = query.getResultList();

        if (authors.isEmpty()) {
            System.out.println("\nNenhum autor encontrado com o ano de nascimento: " + deathYear);
            return;
        }

        for (Person author : authors) {
            System.out.println("\n------------ AUTOR ------------");
            System.out.println("Nome: " + (author.getName() != null ? author.getName() : "N/A"));
            System.out.println("Ano de Nascimento: " + (author.getBirthYear() != null ? author.getBirthYear() : "N/A"));
            System.out.println("Ano de Falecimento: " + (author.getDeathYear() != null ? author.getDeathYear() : "N/A"));
            System.out.println("Livros:");
            if (author.getAuthoredBooks() != null && !author.getAuthoredBooks().isEmpty()) {
                for (Book book : author.getAuthoredBooks()) {
                    System.out.println("   - " + book.getTitle());
                }
            } else {
                System.out.println("   - N/A");
            }
            System.out.println("-------------------------------\n");
        }
    }
}

