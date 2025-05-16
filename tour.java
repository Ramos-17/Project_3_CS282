/*
Tour Planets – Class: Tour
Type of graph: Weighted directed, same data as travel
Goal: Visit all planets exactly once and return to the starting planet with the least cost.
Algorithm hint: This is the Traveling Salesman Problem (TSP). You might use backtracking, dynamic programming, or heuristics to approximate a solution, since TSP is NP-hard.
Input: travel.txt
Output: A complete round-trip path and the total tour time.
*/

import java.io.*;                                                // Import I/O classes
import java.util.*;                                              // Import utility classes

public class tour {                                              // Define class Tour
    private Map<String, Map<String, Integer>> graph;             // Adjacency map: planet → (neighbor → time)
    private List<String> planets;                                // List of all planet names
    private List<String> bestTour;                               // Best tour found so far
    private int bestTime;                                        // Travel time of best tour

    public tour(String filename) {                               // Constructor takes input filename
        graph = new HashMap<>();                                 // Initialize graph map
        planets = new ArrayList<>();                            // Initialize planet list
        bestTour = new ArrayList<>();                           // Initialize best tour list
        bestTime = Integer.MAX_VALUE;                           // Set best time to max value initially
        loadGraph(filename);                                     // Load graph data from file
    }

    private void loadGraph(String filename) {                     // Read graph from text file
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {  // Open file reader
            String line;                                         // Variable for each line
            while ((line = br.readLine()) != null) {            // Read until EOF
                String[] parts = line.trim().split("\\s+");     // Split line by whitespace
                if (parts.length != 3) continue;                // Skip malformed lines

                String planet1 = parts[0];                      // Source planet
                String planet2 = parts[1];                      // Destination planet
                int weight = Integer.parseInt(parts[2]);        // Travel time

                graph.putIfAbsent(planet1, new HashMap<>());    // Ensure source key exists
                graph.get(planet1).put(planet2, weight);        // Add directed edge

                if (!planets.contains(planet1))                  // If source not in list
                    planets.add(planet1);                        //   add it
                if (!planets.contains(planet2))                  // If destination not in list
                    planets.add(planet2);                        //   add it
            }
        } catch (IOException e) {                                 // Handle file I/O errors
            System.err.println("Error reading file: " + e.getMessage());  // Print error
        }
    }

    public void quickTour(String startPlanet) {                   // Find TSP tour from start
        if (!graph.containsKey(startPlanet)) {                   // Verify starting planet exists
            System.out.println("Starting planet not found: " + startPlanet);  // Print error
            return;                                              // Exit method
        }
        List<String> remaining = new ArrayList<>(planets);       // Copy all planets
        remaining.remove(startPlanet);                           // Remove the start planet
        exploreAllTours(startPlanet, remaining, 0);         // Generate all tours

        if (bestTour.isEmpty()) {                                // If no tour was found
            System.out.println("No tour found.");                // Print error
        } else {
            System.out.println("Path: " + String.join(", ", bestTour));       // Print best path
            System.out.println("Total Tour Time: " + bestTime);               // Print best time
        }
    }

    private void exploreAllTours(String start, List<String> list, int index) {  // Permute remaining
        if (index == list.size()) {                              // If a full permutation is ready
            int time = calculateTime(start, list);           // Calculate its total time
            if (time < bestTime) {                               // If it's better than current best
                bestTime = time;                                 //   update bestTime
                bestTour = new ArrayList<>();                    //   reset bestTour
                bestTour.add(start);                             //   add starting planet
                bestTour.addAll(list);                           //   add the permutation
                bestTour.add(start);                             //   return to start
            }
            return;                                               // Backtrack
        }
        for (int i = index; i < list.size(); i++) {              // Iterate through swaps
            Collections.swap(list, index, i);                    // Swap element at i into position index
            exploreAllTours(start, list, index + 1);        // Recurse to next position
            Collections.swap(list, index, i);                    // Swap back to restore order
        }
    }

    private int calculateTime(String start, List<String> tour) {  // Compute the time of a given tour
        int total = 0;                                           // Initialize total time
        String current = start;                                  // Start from the startPlanet
        for (String next : tour) {                               // For each step in the tour
            Integer w = graph.get(current).get(next);            //   lookup travel time
            if (w == null) return Integer.MAX_VALUE;             //   if no edge, abort
            total += w;                                          //   add to total
            current = next;                                      //   move to next planet
        }
        Integer back = graph.get(current).get(start);            // Time to return to start
        if (back == null) return Integer.MAX_VALUE;              //   abort if missing
        total += back;                                           //   add return leg
        return total;                                            // Return computed total
    }

    public static void main(String[] args) {                      // Program entry point
        if (args.length != 2) {                                  // Expect 2 arguments
            System.out.println("Usage: java Tour <input_file> <start_planet>");  // Print usage
            return;                                              // Exit
        }
        tour solver = new tour(args[0]);                         // Create solver with filename
        solver.quickTour(args[1]);                               // Run quickTour with start planet
    }
}
