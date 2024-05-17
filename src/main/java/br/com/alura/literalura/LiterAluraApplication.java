package br.com.alura.literalura;

import br.com.alura.literalura.service.AuthorService;
import br.com.alura.literalura.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class LiterAluraApplication implements CommandLineRunner {

    private final BookService bookService;
    private final AuthorService authorService;
    private final Scanner scanner;

    public static void main(String[] args) {
        SpringApplication.run(LiterAluraApplication.class, args);
    }

    public LiterAluraApplication(BookService bookService, AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n### Catálogo de Livros ###");
            System.out.println("### MENU ###");
            System.out.println("1. Buscar livro por titulo");// API
            System.out.println("2. Listar livros registrados");// DB
            System.out.println("3. Lista autores registrados");// DB
            System.out.println("4. Listar autores vivos em um determinado ano");// DB
            System.out.println("5. Listar livros em um determinado idioma");// DB
            System.out.println("6. Listar Top 10 livros mais baixados");// DB
            System.out.println("7. Buscar autor por nome");// DB
            System.out.println("8. Buscar autor por ano de nascimento");// DB
            System.out.println("9. Buscar autor por ano de falecimento");// DB
            System.out.println("10. Estatísticas de download");// DB
            System.out.println("0. Sair");
            System.out.println("\n");
            System.out.print("Escolha uma opção: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consumir a quebra de linha

            switch (choice) {
                case 1:
                    System.out.print("Digite o título do livro: ");
                    String title = scanner.nextLine();
                    bookService.saveBookWithAuthors(title);
                    break;
                case 2:
                    bookService.printAllBooks();
                    break;
                case 3:
                    authorService.displayAuthorsWithBooks();
                    break;
                case 4:
                    System.out.print("Insira o ano que deseja pesquisar: ");
                    int year = Integer.parseInt(scanner.nextLine());
                    authorService.listAuthorsAliveInYear(year);
                    break;
                case 5:
                    System.out.print("Insira o idioma para realizar a busca: ");
                    System.out.print("\nes - espanhol" + "\n" + "en - inglês" + "\n" + "fr - francês" + "\n" + "pt - português" + "\n" + "tl - tagalo\n\n");
                    String language = scanner.nextLine();
                    bookService.listBooksByLanguage(language);
                    break;
                case 6:
                    bookService.printTopDownloads();
                    break;
                case 7:
                    System.out.print("Insira o nome do Autor que deseja pesquisar: ");
                    String name = scanner.nextLine();
                    authorService.findAuthorByName(name);
                    break;
                case 8:
                    System.out.print("Insira o ano de nascimento que deseja pesquisar: ");
                    int yearOfBirth = Integer.parseInt(scanner.nextLine());
                    authorService.displayAuthorsByYearOfBirth(yearOfBirth);
                    break;
                case 9:
                    System.out.print("Insira o ano de falescimento que deseja pesquisar: ");
                    int yearOfDeath = Integer.parseInt(scanner.nextLine());
                    authorService.displayAuthorsByYearOfDeath(yearOfDeath);
                    break;
                case 10:
                    bookService.generatingStatistics();
                    break;
                case 0:
                    System.out.println("Saindo do catálogo de livros...");
                    return;
                default:
                    System.out.println("Opção inválida, tente novamente.");
                    break;
            }
        }
    }

}