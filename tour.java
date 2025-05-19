import java.io.*;
import java.util.*;

public class tour {
    private Map<String, Map<String, Integer>> graph;
    private List<String> planets;
    private List<String> bestTour;
    private int bestTime;

    public tour(String filename) {
        graph = new HashMap<>();
        planets = new ArrayList<>();
        bestTour = new ArrayList<>(); 
        bestTime = Integer.MAX_VALUE; 
        loadGraph(filename); 
    }

    private void loadGraph(String filename) { 
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) { 
            String line; 
            while ((line = br.readLine()) != null) { 
                String[] parts = line.trim().split("\\s+"); 
                if (parts.length != 3)
                    continue; 

                String planet1 = parts[0]; 
                String planet2 = parts[1]; 
                int weight = Integer.parseInt(parts[2]); 

                graph.putIfAbsent(planet1, new HashMap<>()); 
                graph.get(planet1).put(planet2, weight); 

                if (!planets.contains(planet1)) 
                    planets.add(planet1); 
                if (!planets.contains(planet2)) 
                    planets.add(planet2); 
            }
        } catch (IOException e) { 
            System.err.println("Error reading file: " + e.getMessage()); 
        }
    }

    public void quickTour(String startPlanet) {
        if (!graph.containsKey(startPlanet)) {
            System.out.println("Starting planet not found: " + startPlanet);
            return;
        }
        List<String> remaining = new ArrayList<>(planets);
        remaining.remove(startPlanet);
        exploreAllTours(startPlanet, remaining, 0);

        if (bestTour.isEmpty()) {
            System.out.println("No tour found.");
            System.out.println("Path: " + String.join(", ", bestTour));
            System.out.println("Total Tour Time: " + bestTime);
        }
    }

    private void exploreAllTours(String start, List<String> list, int index) {
        if (index == list.size()) {
            int time = calculateTime(start, list);
            if (time < bestTime) {
                bestTime = time;
                bestTour = new ArrayList<>();
                bestTour.add(start);
                bestTour.addAll(list);
                bestTour.add(start);
            }
            return;
        }
        for (int i = index; i < list.size(); i++) {
            Collections.swap(list, index, i);
            exploreAllTours(start, list, index + 1);
            Collections.swap(list, index, i);
        }
    }

    private int calculateTime(String start, List<String> tour) {
        int total = 0;
        String current = start;
        for (String next : tour) {
            Integer w = graph.get(current).get(next);
            if (w == null)
                return Integer.MAX_VALUE;
            total += w;
            current = next;
        }

        Integer back = graph.get(current).get(start);
        if (back == null)
            return Integer.MAX_VALUE;
        total += back;

        return total;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java tour <input_file> <start_planet>"); //compile, then type this in the terminal
            return;
        }
        tour myTour = new tour(args[0]);
        myTour.quickTour(args[1]);
    }
}
