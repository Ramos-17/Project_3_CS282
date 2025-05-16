//Patrol
/*
1. Secure Paths – Class: Patrol

Type of graph: Weighted undirected
Goal: Find a way to connect all planets (nodes) with patrol routes (edges) such that the total patrol cost is minimized.
Algorithm hint: This is a Minimum Spanning Tree (MST) problem — you likely need to implement Prim’s or Kruskal’s algorithm.
Input: patrol.txt – contains undirected edges like Earth Mars 50.
Output: The patrol edges and the total cost.  
*/
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Patrol {
    private List<Edge> edges;
    private Set<String> planets;

    public Patrol() {
        edges = new ArrayList<> ();
        planets = new HashSet<> ();
        loadGraph("patrol.txt");
    }

    public void patrolEdges() {
        List<Edge> finish = new ArrayList<> ();
        Map<String, String> parent = new HashMap<> ();

        for (String planet : planets) {
            parent.put(planet, planet);
        }

        edges.sort(Comparator.comparingInt(e -> e.weight));

        for (Edge edge : edges) {
            String p1 = find(parent, edge.planet1);
            String p2 = find(parent, edge.planet2);

            if(!p1.equals(p2)) {
                finish.add(edge);
                parent.put(p1,p2);
            }
        }

        int cost = finish.stream().mapToInt(e -> e.weight).sum();

        System.out.println("Total Cost: " + cost);
        for (Edge e : finish) {
            System.out.println("( " + e.planet1 + ", " + e.planet2 + ", " + e.weight + ")");
        }

    }

    private String find(Map<String, String> parent, String node) {
        if (!parent.get(node).equals(node)) {
            parent.put(node, find(parent,parent.get(node)));
        }
        return parent.get(node);
    }

    private void loadGraph(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length != 3) {
                    continue;
                }

                String planet1 = parts[0];
                String planet2 = parts[1];
                int weight = Integer.parseInt(parts[2]);

                edges.add(new Edge(planet1, planet2, weight));

                planets.add(planet1);
                planets.add(planet2);
            }

        } catch (IOException e) {
            System.err.println("Error reading " + filename + ": " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in " + filename + ": " + e.getMessage());
        }
    }

    private static class Edge {
    String planet1, planet2;
    int weight;

    Edge(String planet1, String planet2, int weight) {
        this.planet1 = planet1;
        this.planet2 = planet2;
        this.weight = weight;
        }
    }
}