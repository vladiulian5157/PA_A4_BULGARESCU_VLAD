import java.util.*;

public class Main {
  public static void main(String[] args) {
    BestRouteProblem problem = new BestRouteProblem();

    City c1 = new City("Iasi", 0, 0, 300000);
    City c2 = new City("Vaslui", 50, 40, 100000);
    GasStation g1 = new GasStation("Petrom", 20, 10, 7.2);

    problem.addLocation(c1);
    problem.addLocation(c2);
    problem.addLocation(g1);

    Road r1 = new Road(c1, g1, RoadType.COUNTRY, 25, 90);
    Road r2 = new Road(g1, c2, RoadType.HIGHWAY, 45, 130);

    problem.addRoad(r1);
    problem.addRoad(r2);

    System.out.println("Valid? " + problem.isValid());
    System.out.println("Se poate ajunge din Iasi în Vaslui? " + problem.canReach(c1, c2));
  }
}

enum RoadType {
  HIGHWAY,
  EXPRESS,
  COUNTRY
}

abstract sealed class Location permits City, Airport, GasStation {
  protected final String name;
  protected final double x;
  protected final double y;

  public Location(String name, double x, double y) {
    this.name = name;
    this.x = x;
    this.y = y;
  }

  public String name() { return name; }
  public double x() { return x; }
  public double y() { return y; }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Location other)) return false;
    return this.name.equals(other.name) &&
            this.x == other.x &&
            this.y == other.y;
  }

  @Override
  public int hashCode() {
    return name.hashCode() + Double.hashCode(x) + Double.hashCode(y);
  }
}

final class City extends Location {
  private final int population;

  public City(String name, double x, double y, int population) {
    super(name, x, y);
    this.population = population;
  }

  public int population() { return population; }
}

final class Airport extends Location {
  private final int terminals;

  public Airport(String name, double x, double y, int terminals) {
    super(name, x, y);
    this.terminals = terminals;
  }

  public int terminals() { return terminals; }
}

final class GasStation extends Location {
  private final double gasPrice;

  public GasStation(String name, double x, double y, double gasPrice) {
    super(name, x, y);
    this.gasPrice = gasPrice;
  }

  public double gasPrice() { return gasPrice; }
}

class Road {
  private final Location from;
  private final Location to;
  private final RoadType type;
  private final double length;
  private final double speedLimit;

  public Road(Location from, Location to, RoadType type, double length, double speedLimit) {
    this.from = from;
    this.to = to;
    this.type = type;
    this.length = length;
    this.speedLimit = speedLimit;
  }

  public Location from() { return from; }
  public Location to() { return to; }
  public RoadType type() { return type; }
  public double length() { return length; }
  public double speedLimit() { return speedLimit; }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Road other)) return false;
    return this.from.equals(other.from) &&
            this.to.equals(other.to) &&
            this.type == other.type;
  }

  @Override
  public int hashCode() {
    return from.hashCode() + to.hashCode() + type.hashCode();
  }
}

class BestRouteProblem {
  private final Set<Location> locations = new HashSet<>();
  private final Set<Road> roads = new HashSet<>();

  public boolean addLocation(Location loc) {
    return locations.add(loc);
  }

  public boolean addRoad(Road road) {
    return roads.add(road);
  }

  public boolean isValid() {
    for (Road r : roads) {
      double dx = r.from().x() - r.to().x();
      double dy = r.from().y() - r.to().y();
      double euclid = Math.sqrt(dx * dx + dy * dy);

      if (r.length() < euclid) return false;
      if (!locations.contains(r.from()) || !locations.contains(r.to())) return false;
    }
    return true;
  }

  public boolean canReach(Location start, Location end) {
    if (!locations.contains(start) || !locations.contains(end)) return false;

    Map<Location, List<Location>> graph = new HashMap<>();
    for (Location loc : locations) graph.put(loc, new ArrayList<>());

    for (Road r : roads) {
      graph.get(r.from()).add(r.to());
      graph.get(r.to()).add(r.from());
    }

    Set<Location> visited = new HashSet<>();
    Queue<Location> queue = new LinkedList<>();
    queue.add(start);

    while (!queue.isEmpty()) {
      Location current = queue.poll();
      if (current.equals(end)) return true;

      for (Location next : graph.get(current)) {
        if (visited.add(next)) queue.add(next);
      }
    }

    return false;
  }
}
