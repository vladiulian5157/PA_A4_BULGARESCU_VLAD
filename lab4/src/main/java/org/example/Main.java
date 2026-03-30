package org.example;

import com.github.javafaker.Faker;
import org.jgrapht.Graph;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Intersection implements Comparable<Intersection> {
    private final String name;

    public Intersection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Intersection other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return "Intersection{" + name + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Intersection)) return false;
        Intersection that = (Intersection) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

class Street implements Comparable<Street> {
    private final String name;
    private final int length;
    private final Intersection a;
    private final Intersection b;

    public Street(String name, int length, Intersection a, Intersection b) {
        this.name = name;
        this.length = length;
        this.a = a;
        this.b = b;
    }

    public int getLength() {
        return length;
    }

    public Intersection getA() {
        return a;
    }

    public Intersection getB() {
        return b;
    }

    @Override
    public int compareTo(Street other) {
        return Integer.compare(this.length, other.length);
    }

    @Override
    public String toString() {
        return name + " (" + length + "m) between " + a.getName() + " and " + b.getName();
    }
}

class City {
    private final Set<Intersection> intersections = new HashSet<>();
    private final List<Street> streets = new LinkedList<>();

    public void addIntersection(Intersection i) {
        intersections.add(i);
    }

    public void addStreet(Street s) {
        streets.add(s);
    }

    public Set<Intersection> getIntersections() {
        return intersections;
    }

    public List<Street> getStreets() {
        return streets;
    }

    public List<Street> streetsLongerThanWithHighDegree(int minLength) {
        Map<Intersection, Long> degree = streets.stream()
                .flatMap(s -> Arrays.stream(new Intersection[]{s.getA(), s.getB()}))
                .collect(Collectors.groupingBy(i -> i, Collectors.counting()));

        return streets.stream()
                .filter(s -> s.getLength() > minLength)
                .filter(s -> degree.getOrDefault(s.getA(), 0L) >= 3 ||
                        degree.getOrDefault(s.getB(), 0L) >= 3)
                .collect(Collectors.toList());
    }

    public Graph<Intersection, Street> toGraph() {
        Graph<Intersection, Street> graph = new WeightedMultigraph<>(Street.class);

        intersections.forEach(graph::addVertex);

        for (Street s : streets) {
            graph.addEdge(s.getA(), s.getB(), s);
            graph.setEdgeWeight(s, s.getLength());
        }

        return graph;
    }

    public List<Set<Street>> kBestMSTs(int k) {
        Graph<Intersection, Street> graph = toGraph();
        List<Set<Street>> solutions = new ArrayList<>();

        Set<Street> base = new KruskalMinimumSpanningTree<>(graph)
                .getSpanningTree()
                .getEdges();
        solutions.add(base);

        for (Street removed : base) {
            Graph<Intersection, Street> copy = toGraph();
            copy.removeEdge(removed);

            try {
                Set<Street> mst = new KruskalMinimumSpanningTree<>(copy)
                        .getSpanningTree()
                        .getEdges();

                if (mst.size() == intersections.size() - 1 && !solutions.contains(mst)) {
                    solutions.add(mst);
                }
            } catch (Exception ignored) {}
        }

        solutions.sort(Comparator.comparingInt(
                set -> set.stream().mapToInt(Street::getLength).sum()
        ));

        return solutions.stream().limit(k).collect(Collectors.toList());
    }
}

public class Main {
    public static void main(String[] args) {

        Faker faker = new Faker();
        City city = new City();

        List<Intersection> intersections = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> new Intersection(faker.address().streetName()))
                .collect(Collectors.toList());

        intersections.forEach(city::addIntersection);

        Random r = new Random();
        for (int i = 0; i < 20; i++) {
            Intersection a = intersections.get(r.nextInt(10));
            Intersection b = intersections.get(r.nextInt(10));
            if (a != b) {
                city.addStreet(new Street(
                        faker.rockBand().name(),
                        r.nextInt(200) + 20,
                        a, b
                ));
            }
        }

        System.out.println("=== All Streets ===");
        city.getStreets().forEach(System.out::println);

        System.out.println("\n=== Streets longer than 100m and connected to degree>=3 ===");
        city.streetsLongerThanWithHighDegree(100).forEach(System.out::println);

        System.out.println("\n=== Top 3 MST Solutions ===");
        List<Set<Street>> sols = city.kBestMSTs(3);

        int idx = 1;
        for (Set<Street> sol : sols) {
            int cost = sol.stream().mapToInt(Street::getLength).sum();
            System.out.println("Solution " + (idx++) + " (cost=" + cost + "):");
            sol.forEach(System.out::println);
            System.out.println();
        }
    }
}
