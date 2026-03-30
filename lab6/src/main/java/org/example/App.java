import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;

class Database {
    private static Database instance;
    private final HikariDataSource dataSource;

    private Database() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/moviesdb");
        config.setUsername("postgres");
        config.setPassword("password");
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);

        dataSource = new HikariDataSource(config);
    }

    public static synchronized Database getInstance() {
        if (instance == null) instance = new Database();
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}

class Genre {
    private int id;
    private String name;

    public Genre(int id, String name) { this.id = id; this.name = name; }
    public Genre(String name) { this.name = name; }

    public int getId() { return id; }
    public String getName() { return name; }
}

class Actor {
    private int id;
    private String name;

    public Actor(int id, String name) { this.id = id; this.name = name; }
    public Actor(String name) { this.name = name; }

    public int getId() { return id; }
    public String getName() { return name; }
}

class Movie {
    private int id;
    private String title;
    private LocalDate releaseDate;
    private int duration;
    private double score;
    private Genre genre;

    public Movie(int id, String title, LocalDate releaseDate, int duration, double score, Genre genre) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.score = score;
        this.genre = genre;
    }

    public Movie(String title, LocalDate releaseDate, int duration, double score, Genre genre) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.score = score;
        this.genre = genre;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public int getDuration() { return duration; }
    public double getScore() { return score; }
    public Genre getGenre() { return genre; }
}

class GenreDAO {

    public int create(Genre genre) throws SQLException {
        String sql = "INSERT INTO genres(name) VALUES (?) RETURNING id";
        try (Connection con = Database.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, genre.getName());
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    public Genre findById(int id) throws SQLException {
        String sql = "SELECT * FROM genres WHERE id = ?";
        try (Connection con = Database.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? new Genre(rs.getInt("id"), rs.getString("name")) : null;
        }
    }

    public Genre findByName(String name) throws SQLException {
        String sql = "SELECT * FROM genres WHERE name = ?";
        try (Connection con = Database.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? new Genre(rs.getInt("id"), rs.getString("name")) : null;
        }
    }
}

class ActorDAO {

    public int create(Actor actor) throws SQLException {
        String sql = "INSERT INTO actors(name) VALUES (?) RETURNING id";
        try (Connection con = Database.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, actor.getName());
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    public Actor findById(int id) throws SQLException {
        String sql = "SELECT * FROM actors WHERE id = ?";
        try (Connection con = Database.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? new Actor(rs.getInt("id"), rs.getString("name")) : null;
        }
    }
}

class MovieDAO {

    public int create(Movie movie) throws SQLException {
        String sql = """
            INSERT INTO movies(title, release_date, duration, score, genre_id)
            VALUES (?, ?, ?, ?, ?) RETURNING id
        """;

        try (Connection con = Database.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, movie.getTitle());
            stmt.setDate(2, Date.valueOf(movie.getReleaseDate()));
            stmt.setInt(3, movie.getDuration());
            stmt.setDouble(4, movie.getScore());
            stmt.setInt(5, movie.getGenre().getId());

            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    public Movie findById(int id) throws SQLException {
        String sql = """
            SELECT m.*, g.name AS genre_name
            FROM movies m
            JOIN genres g ON m.genre_id = g.id
            WHERE m.id = ?
        """;

        try (Connection con = Database.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) return null;

            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));

            return new Movie(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    rs.getDouble("score"),
                    genre
            );
        }
    }
}

class MovieActorDAO {

    public void addActorToMovie(int movieId, int actorId) throws SQLException {
        String sql = "INSERT INTO movie_actor(movie_id, actor_id) VALUES (?, ?)";

        try (Connection con = Database.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, movieId);
            stmt.setInt(2, actorId);
            stmt.executeUpdate();
        }
    }
}

class HtmlReportGenerator {

    public void generate() throws Exception {
        String template = """
                <html>
                <head><title>Movie Report</title></head>
                <body>
                <h1>All Movies</h1>
                <table border="1">
                <tr><th>Title</th><th>Release</th><th>Duration</th><th>Score</th><th>Genre</th></tr>
                {{ROWS}}
                </table>
                </body>
                </html>
                """;

        StringBuilder rows = new StringBuilder();

        String sql = "SELECT * FROM movie_report";

        try (Connection con = Database.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rows.append("<tr>")
                        .append("<td>").append(rs.getString("title")).append("</td>")
                        .append("<td>").append(rs.getDate("release_date")).append("</td>")
                        .append("<td>").append(rs.getInt("duration")).append("</td>")
                        .append("<td>").append(rs.getDouble("score")).append("</td>")
                        .append("<td>").append(rs.getString("genre")).append("</td>")
                        .append("</tr>");
            }
        }

        String finalHtml = template.replace("{{ROWS}}", rows.toString());
        Files.write(Paths.get("movies_report.html"), finalHtml.getBytes());

        System.out.println("HTML report generated.");
    }
}

public class App {
    public static void main(String[] args) {
        try {
            GenreDAO genreDAO = new GenreDAO();
            MovieDAO movieDAO = new MovieDAO();
            ActorDAO actorDAO = new ActorDAO();
            MovieActorDAO movieActorDAO = new MovieActorDAO();

            Genre action = genreDAO.findByName("Action");
            if (action == null) {
                int id = genreDAO.create(new Genre("Action"));
                action = genreDAO.findById(id);
            }

            Movie movie = new Movie("Inception", LocalDate.of(2010, 7, 16), 148, 8.8, action);
            int movieId = movieDAO.create(movie);

            int actorId = actorDAO.create(new Actor("Leonardo DiCaprio"));
            movieActorDAO.addActorToMovie(movieId, actorId);

            HtmlReportGenerator generator = new HtmlReportGenerator();
            generator.generate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
