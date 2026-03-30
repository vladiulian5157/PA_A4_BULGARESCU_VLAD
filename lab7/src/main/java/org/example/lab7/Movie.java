package org.example.lab7;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    private Integer duration;

    private Double score;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    public Movie() {}

    public Movie(String title, LocalDate releaseDate, Integer duration, Double score, Genre genre) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.score = score;
        this.genre = genre;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }
}