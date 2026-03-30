import java.time.LocalDate;
import java.util.*;

interface Profile {
    String getId();
    String getName();
}

class Person implements Profile, Comparable<Person> {
    private final String id;
    private final String name;
    private final LocalDate birthDate;
    protected Map<Profile, String> relationships = new HashMap<>();

    public Person(String id, String name, LocalDate birthDate) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
    }

    public void addRelationship(Profile other, String description) {
        relationships.put(other, description);
    }

    public Map<Profile, String> getRelationships() {
        return relationships;
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getName() { return name; }

    public LocalDate getBirthDate() { return birthDate; }

    @Override
    public int compareTo(Person o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return "Person: " + name + " (" + id + ")";
    }
}

class Programmer extends Person {
    private final String favoriteLanguage;

    public Programmer(String id, String name, LocalDate birthDate, String favoriteLanguage) {
        super(id, name, birthDate);
        this.favoriteLanguage = favoriteLanguage;
    }

    public String getFavoriteLanguage() {
        return favoriteLanguage;
    }

    @Override
    public String toString() {
        return "Programmer: " + getName() + " (fav: " + favoriteLanguage + ")";
    }
}

class Designer extends Person {
    private final String designStyle;

    public Designer(String id, String name, LocalDate birthDate, String designStyle) {
        super(id, name, birthDate);
        this.designStyle = designStyle;
    }

    public String getDesignStyle() {
        return designStyle;
    }

    @Override
    public String toString() {
        return "Designer: " + getName() + " (style: " + designStyle + ")";
    }
}

class Company implements Profile, Comparable<Company> {
    private final String id;
    private final String name;
    private final String industry;
    protected Map<Profile, String> relationships = new HashMap<>();

    public Company(String id, String name, String industry) {
        this.id = id;
        this.name = name;
        this.industry = industry;
    }

    public void addRelationship(Profile other, String description) {
        relationships.put(other, description);
    }

    public Map<Profile, String> getRelationships() {
        return relationships;
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getName() { return name; }

    public String getIndustry() { return industry; }

    @Override
    public int compareTo(Company o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return "Company: " + name + " (" + industry + ")";
    }
}

class ProfileNameComparator implements Comparator<Profile> {
    @Override
    public int compare(Profile p1, Profile p2) {
        return p1.getName().compareTo(p2.getName());
    }
}

class SocialNetwork {
    private List<Profile> profiles = new ArrayList<>();

    public void addProfile(Profile p) {
        profiles.add(p);
    }

    public int getImportance(Profile p) {
        if (p instanceof Person person)
            return person.getRelationships().size();
        if (p instanceof Company company)
            return company.getRelationships().size();
        return 0;
    }

    public void printNetwork() {
        profiles.stream()
                .sorted(Comparator.comparingInt(this::getImportance).reversed())
                .forEach(p -> System.out.println(p + " | importance = " + getImportance(p)));
    }

    public List<Profile> getProfiles() {
        return profiles;
    }
}

public class Main {
    public static void main(String[] args) {

        SocialNetwork network = new SocialNetwork();

        Programmer p1 = new Programmer("P1", "Alice", LocalDate.of(1995, 5, 10), "Java");
        Designer d1 = new Designer("D1", "Bob", LocalDate.of(1990, 3, 20), "Minimalist");
        Person p2 = new Person("P2", "Charlie", LocalDate.of(1988, 1, 15));

        Company c1 = new Company("C1", "TechCorp", "Software");
        Company c2 = new Company("C2", "DesignHub", "Creative");

        p1.addRelationship(d1, "friends");
        p1.addRelationship(c1, "employee: senior developer");

        d1.addRelationship(p1, "friends");
        d1.addRelationship(c2, "freelancer");

        p2.addRelationship(p1, "colleague");

        c1.addRelationship(p1, "employs");
        c2.addRelationship(d1, "collaborates");

        network.addProfile(p1);
        network.addProfile(d1);
        network.addProfile(p2);
        network.addProfile(c1);
        network.addProfile(c2);

        List<Profile> mixedList = new ArrayList<>(network.getProfiles());
        mixedList.sort(Comparator.comparing(Profile::getName));
        System.out.println("Sorted by name:");
        mixedList.forEach(System.out::println);

        System.out.println("\nNetwork sorted by importance:");
        network.printNetwork();
    }
}
