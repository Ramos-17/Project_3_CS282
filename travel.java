import java.util.*;
import java.io.*;

public class travel {
    // Graph representation using adjacency list
    private Map<String, List<Edge>> graph;
    private Map<String, Integer> distances;
    private Map<String, String> previous;
    private Set<String> visited;

    // Edge class to represent weighted directed edges
    private static class Edge {
        String destination;
        int weight;

        Edge(String destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }

    public travel() {
        graph = new HashMap<>();
        distances = new HashMap<>();
        previous = new HashMap<>();
        visited = new HashSet<>();
    }

    // Add an edge to the graph
    public void addEdge(String source, String destination, int weight) {
        graph.putIfAbsent(source, new ArrayList<>());
        graph.get(source).add(new Edge(destination, weight));
    }

    // Dijkstra's algorithm implementation
    public List<String> findShortestPath(String start, String end) {
        // Initialize distances
        for (String planet : graph.keySet()) {
            distances.put(planet, Integer.MAX_VALUE);
        }
        distances.put(start, 0);

        // Priority queue for Dijkstra's algorithm
        PriorityQueue<String> priority = new PriorityQueue<>(
            (a, b) -> distances.get(a) - distances.get(b)
        );
        priority.offer(start);

        while (!priority.isEmpty()) {
            String current = priority.poll();
            
            if (current.equals(end)) {
                break;
            }

            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);

            // Skip if current planet has no outgoing edges
            if (!graph.containsKey(current)) {
                continue;
            }

            // Check all neighbors
            for (Edge edge : graph.get(current)) {
                String neighbor = edge.destination;
                int newDistance = distances.get(current) + edge.weight;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, current);
                    priority.offer(neighbor);
                }
            }
        }

        // Reconstruct path
        List<String> path = new ArrayList<>();
        if (distances.get(end) == Integer.MAX_VALUE) {
            return path; // No path exists
        }

        for (String at = end; at != null; at = previous.get(at)) {
            path.add(0, at);
        }
        return path;
    }

    // Read input from file and build graph
    public void readInput(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 3) {
                    String source = parts[0];
                    String destination = parts[1];
                    int weight = Integer.parseInt(parts[2]);
                    addEdge(source, destination, weight);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }

    public void quickTravel(String start, String end) {
        List<String> path = findShortestPath(start, end);
        if (path.isEmpty()) {
            System.out.println("No path exists from " + start + " to " + end);
        } else {
            System.out.println("Path: " + String.join(", ", path));
            System.out.println("Total Travel Time: " + distances.get(end));
        }
    }

    // Main method to run the program
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java travel <input_file> <start_planet> <end_planet>");
            return;
        }

        travel myTravel = new travel();
        myTravel.readInput(args[0]);

        String start = args[1];
        String end = args[2];

        myTravel.quickTravel(start, end);
        }
    }