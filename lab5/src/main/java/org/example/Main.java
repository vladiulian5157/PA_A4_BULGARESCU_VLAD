import java.awt.Desktop;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;

public class Main {

    public static class Resource {
        private String id;
        private String title;
        private String location;
        private String year;
        private String author;

        public Resource(String id, String title, String location, String year, String author) {
            this.id = id;
            this.title = title;
            this.location = location;
            this.year = year;
            this.author = author;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getLocation() { return location; }
        public String getYear() { return year; }
        public String getAuthor() { return author; }

        @Override
        public String toString() {
            return "Resource{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", location='" + location + '\'' +
                    ", year='" + year + '\'' +
                    ", author='" + author + '\'' +
                    '}';
        }
    }

    public static class ResourceRepository {

        private final Map<String, Resource> resources = new HashMap<>();

        public void addResource(Resource resource) {
            resources.put(resource.getId(), resource);
        }

        public Resource getResource(String id) {
            return resources.get(id);
        }

        public Collection<Resource> getAll() {
            return resources.values();
        }

        public void openResource(String id) throws IOException {
            Resource res = resources.get(id);
            if (res == null) {
                System.out.println("Resource not found: " + id);
                return;
            }

            String loc = res.getLocation();
            Desktop desktop = Desktop.getDesktop();

            if (loc.startsWith("http://") || loc.startsWith("https://")) {
                desktop.browse(URI.create(loc));
            } else {
                desktop.open(new File(loc));
            }
        }
    }

    public static void main(String[] args) {
        try {
            ResourceRepository repo = new ResourceRepository();

            new AddCommand(new Resource(
                    "knuth67",
                    "The Art of Computer Programming",
                    "d:/books/programming/tacp.ps",
                    "1967",
                    "Donald E. Knuth"
            )).execute(repo);

            new AddCommand(new Resource(
                    "jvm25",
                    "The Java Virtual Machine Specification",
                    "https://docs.oracle.com/javase/specs/jvms/se25/html/index.html",
                    "2025",
                    "Tim Lindholm & others"
            )).execute(repo);

            new AddCommand(new Resource(
                    "java25",
                    "The Java Language Specification",
                    "https://docs.oracle.com/javase/specs/jls/se25/jls25.pdf",
                    "2025",
                    "James Gosling & others"
            )).execute(repo);

            new ListCommand().execute(repo);
            new ViewCommand("jvm25").execute(repo);
            new ReportCommand("catalog-report.html").execute(repo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class CatalogException extends Exception {
    public CatalogException(String message) { super(message); }
    public CatalogException(String message, Throwable cause) { super(message, cause); }
}

interface Command {
    void execute(Main.ResourceRepository repo) throws CatalogException;
}

class AddCommand implements Command {
    private final Main.Resource resource;

    public AddCommand(Main.Resource resource) {
        this.resource = resource;
    }

    @Override
    public void execute(Main.ResourceRepository repo) {
        repo.addResource(resource);
        System.out.println("Added: " + resource);
    }
}

class ListCommand implements Command {
    @Override
    public void execute(Main.ResourceRepository repo) {
        System.out.println("=== Catalog Contents ===");
        for (Main.Resource r : repo.getAll()) {
            System.out.println(r);
        }
    }
}

class ViewCommand implements Command {
    private final String id;

    public ViewCommand(String id) {
        this.id = id;
    }

    @Override
    public void execute(Main.ResourceRepository repo) throws CatalogException {
        try {
            repo.openResource(id);
        } catch (Exception e) {
            throw new CatalogException("Cannot open resource: " + id, e);
        }
    }
}

class LoadCommand implements Command {
    private final String fileName;

    public LoadCommand(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void execute(Main.ResourceRepository repo) throws CatalogException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new CatalogException("File not found: " + fileName);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] p = line.split("\\|");
                if (p.length != 5) {
                    throw new CatalogException("Invalid line: " + line);
                }

                Main.Resource r = new Main.Resource(p[0], p[1], p[2], p[3], p[4]);
                repo.addResource(r);
                count++;
            }

            System.out.println("Loaded " + count + " resources from " + fileName);

        } catch (IOException e) {
            throw new CatalogException("Error reading file: " + fileName, e);
        }
    }
}

class ReportCommand implements Command {
    private final String outputFile;

    public ReportCommand(String outputFile) {
        this.outputFile = outputFile;
    }

    @Override
    public void execute(Main.ResourceRepository repo) throws CatalogException {
        try {
            Configuration cfg = new Configuration(new Version("2.3.32"));
            StringTemplateLoader loader = new StringTemplateLoader();

            String templateContent =
                    "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "<head>\n" +
                            "  <meta charset=\"UTF-8\">\n" +
                            "  <title>Catalog Report</title>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "<h1>Catalog Report</h1>\n" +
                            "<table border=\"1\" cellspacing=\"0\" cellpadding=\"4\">\n" +
                            "  <tr>\n" +
                            "    <th>ID</th>\n" +
                            "    <th>Title</th>\n" +
                            "    <th>Author</th>\n" +
                            "    <th>Year</th>\n" +
                            "    <th>Location</th>\n" +
                            "  </tr>\n" +
                            "  <#list items as r>\n" +
                            "  <tr>\n" +
                            "    <td>${r.id}</td>\n" +
                            "    <td>${r.title}</td>\n" +
                            "    <td>${r.author}</td>\n" +
                            "    <td>${r.year}</td>\n" +
                            "    <td><a href=\"${r.location}\">${r.location}</a></td>\n" +
                            "  </tr>\n" +
                            "  </#list>\n" +
                            "</table>\n" +
                            "</body>\n" +
                            "</html>";

            loader.putTemplate("catalogTemplate", templateContent);
            cfg.setTemplateLoader(loader);

            Template template = cfg.getTemplate("catalogTemplate", "UTF-8");

            List<Map<String, Object>> list = new ArrayList<>();
            for (Main.Resource r : repo.getAll()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", r.getId());
                m.put("title", r.getTitle());
                m.put("author", r.getAuthor());
                m.put("year", r.getYear());
                m.put("location", r.getLocation());
                list.add(m);
            }

            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("items", list);

            try (Writer out = new OutputStreamWriter(
                    new FileOutputStream(outputFile), StandardCharsets.UTF_8)) {
                template.process(dataModel, out);
            }

            System.out.println("Report generated: " + outputFile);
            Desktop.getDesktop().browse(new File(outputFile).toURI());

        } catch (Exception e) {
            throw new CatalogException("Error generating report", e);
        }
    }
}
