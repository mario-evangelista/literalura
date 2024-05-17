package br.com.alura.literalura.service;

import br.com.alura.literalura.dominio.entity.Book;
import br.com.alura.literalura.dominio.entity.Person;
import br.com.alura.literalura.repository.BookRepository;
import br.com.alura.literalura.repository.PersonRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class BookService {

    private static final String GUTENDEX_API_URL = "https://gutendex.com/books/";

    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    private final BookRepository bookRepository;

    @Autowired
    private PersonRepository authorRepository;

    @PersistenceContext
    @Autowired
    private EntityManager entityManager;

    @Autowired
    public BookService(RestTemplate restTemplate, BookRepository bookRepository) {
        this.restTemplate = restTemplate;
        this.bookRepository = bookRepository;
    }

    public void saveBookWithAuthors(String title) {
        // Fazer a chamada à API GUTENDEX para buscar o livro por título
        String apiUrl = GUTENDEX_API_URL + "?search=" + title;
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        // Analisar a resposta e extrair os detalhes do livro, conforme necessário

        // Exemplo de análise de resposta e salvamento no banco de dados
        JSONObject responseObject = new JSONObject(response.getBody());
        JSONArray booksArray = responseObject.getJSONArray("results");

        // Verificar se há pelo menos um livro retornado
        if (booksArray.length() > 0) {
            JSONObject bookJson = booksArray.getJSONObject(0); // Obter o primeiro livro da lista

            Book book = new Book();

            // Verificar se o título do livro é maior que 255 caracteres
            if (bookJson.getString("title").length() > 255) {
                // Truncar o título para 255 caracteres
                String truncatedTitle = bookJson.getString("title").substring(0, 255);
                book.setTitle(truncatedTitle);
            } else {
                book.setTitle(bookJson.getString("title"));
            }

            book.setDownloadCount(bookJson.getDouble("download_count"));

            // set languages
            JSONArray languagesJson = bookJson.getJSONArray("languages");
            List<String> languages = new ArrayList<>();
            for (int l = 0; l < languagesJson.length(); l++) {
                languages.add(languagesJson.getString(l));
            }
            book.setLanguages(languages);

            // Save the book in the database
            bookRepository.save(book);

            JSONArray authorsArray = bookJson.getJSONArray("authors");
            for (int j = 0; j < authorsArray.length(); j++) {
                JSONObject authorJson = authorsArray.getJSONObject(j);
                String authorName = authorJson.getString("name");

                // Check if the author already exists in the database
                Optional<Person> existingAuthor = authorRepository.findByName(authorName);
                Person author;
                if (existingAuthor.isPresent()) {
                    author = existingAuthor.get();
                } else {
                    author = new Person();
                    // Save the new author in the database
                    author.setName(authorJson.getString("name"));

                    if (authorJson.has("birth_year") && !authorJson.isNull("birth_year")) {
                        author.setBirthYear(authorJson.getInt("birth_year"));
                    } else {
                        author.setDeathYear(null);
                    }

                    if (authorJson.has("death_year") && !authorJson.isNull("death_year")) {
                        author.setDeathYear(authorJson.getInt("death_year"));
                    } else {
                        author.setDeathYear(null);
                    }

                    authorRepository.save(author);
                }

                // Associate the author with the book
                book.getAuthors().add(author);
            }

            // Save the updated book (with authors) in the database
            bookRepository.save(book);
        } else {
            // Se nenhum livro foi encontrado, você pode lidar com isso de acordo com a lógica do seu aplicativo
            System.out.println("Nenhum livro encontrado com o título fornecido.");
        }
    }

    @Transactional()
    public void printAllBooks() {
        List<Book> books = bookRepository.findAll();

        if (books.isEmpty()) {
            System.out.println("\nNenhum livro encontrado no banco de dados.");
        } else {
            for (Book book : books) {
                System.out.println("\n------------LIVRO------------");
                System.out.println("Título: " + (book.getTitle() != null ? book.getTitle() : "N/A"));
                System.out.println("Autores: ");
                if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                    for (Person author : book.getAuthors()) {
                        System.out.println("   - " + author.getName());
                    }
                } else {
                    System.out.println("   - N/A");
                }
                System.out.println("Idiomas: ");
                if (book.getLanguages() != null && !book.getLanguages().isEmpty()) {
                    for (String language : book.getLanguages()) {
                        System.out.println("   - " + language);
                    }
                } else {
                    System.out.println("   - N/A");
                }
                System.out.println("Número de Downloads: " + (book.getDownloadCount() != null ? book.getDownloadCount() : "N/A"));
                System.out.println("-----------------------------\n");
            }
        }
    }

    public void listBooksByLanguage(String language) {
        // Buscar os livros no banco de dados pelo idioma fornecido
        List<Book> books = bookRepository.findByLanguagesContaining(language);

        // Verificar se há livros no idioma fornecido
        if (books.isEmpty()) {
            System.out.println("\nNão existem livros nesse idioma: " + language + ", no banco de dados");
            return;
        }

        // Iterar sobre os livros encontrados e exibir seus atributos no console
        for (Book book : books) {
            System.out.println("\n------------ LIVRO ------------");
            System.out.println("Título: " + (book.getTitle() != null ? book.getTitle() : "N/A"));

            // Verificar se há autores
            if (!book.getAuthors().isEmpty()) {
                System.out.println("Autor(es):");
                for (Person author : book.getAuthors()) {
                    System.out.println("   - " + author.getName());
                }
            } else {
                System.out.println("Autor(es): N/A");
            }

            System.out.println("Idioma: " + (book.getLanguages().isEmpty() ? "N/A" : language));
            System.out.println("Número de Downloads: " + (book.getDownloadCount() != null ? book.getDownloadCount() : "N/A"));
            System.out.println("-------------------------------\n");
        }
    }

    // Top 10 livros mais baixados:
    public void printTopDownloads() {
        // Consulta no banco de dados para recuperar os top 10 livros mais baixados
        List<Book> topDownloads = bookRepository.findTop10ByOrderByDownloadCountDesc();

        // Exibe os detalhes dos top 10 livros mais baixados no console
        System.out.println("\nTop 10 Livros Mais Baixados:");
        System.out.println("\n-----------------------------\n");
        for (int i = 0; i < topDownloads.size(); i++) {
            Book book = topDownloads.get(i);
            System.out.println("Posição: " + (i + 1));
            System.out.println("Título: " + book.getTitle());
            System.out.println("Autor(es): ");
            for (Person author : book.getAuthors()) {
                System.out.println("   - " + author.getName());
            }
            System.out.println("Número de Downloads: " + book.getDownloadCount());
            System.out.println("\n-----------------------------\n");
        }
    }

    public DoubleSummaryStatistics getDownloadCountStatistics() {
        TypedQuery<Double> query = entityManager.createQuery("SELECT b.downloadCount FROM Book b", Double.class);
        List<Double> downloadCounts = query.getResultList();

        DoubleSummaryStatistics stats = downloadCounts.stream()
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        return stats;
    }

    public void generatingStatistics() {
        // Obtendo as estatísticas de download
        DoubleSummaryStatistics downloadCountStats = getDownloadCountStatistics();

        // Exibindo as estatísticas
        System.out.println("\n-----------------------------\n");
        System.out.println("Estatísticas de Download:");
        System.out.println("Soma: " + downloadCountStats.getSum());
        System.out.println("Média: " + downloadCountStats.getAverage());
        System.out.println("Mínimo: " + downloadCountStats.getMin());
        System.out.println("Máximo: " + downloadCountStats.getMax());
        System.out.println("Contagem: " + downloadCountStats.getCount());
        System.out.println("\n-----------------------------\n");
    }

}

