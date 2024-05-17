package br.com.alura.literalura.dominio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Integer birthYear;
    private Integer deathYear;
    private String name;

    @ManyToMany(mappedBy = "authors")
    private List<Book> authoredBooks;

    @ManyToMany(mappedBy = "translators")
    private List<Book> translatedBooks;

    public String getName() {
        return name;
    }

    // Getters e setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getDeathYear() {
        return deathYear;
    }

    public void setDeathYear(Integer deathYear) {
        this.deathYear = deathYear;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getAuthoredBooks() {
        return authoredBooks;
    }
}

