package com.logistics;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FlightPathFinder {
  private static final Logger logger = LoggerFactory.getLogger(FlightPathFinder.class);

  public static class Flight {
    String airline;
    String flightID;
    String originAirport;
    String destinationAirport;
    String originCountry;
    String destinationCountry;
    int totalFlightTime;  // in minutes
    String departureTime;
    String arrivalTime;

    Flight(String airline, String flightID, String originAirport, String destinationAirport,
           String originCountry, String destinationCountry, int totalFlightTime,
           String departureTime, String arrivalTime) {
      this.airline = airline;
      this.flightID = flightID;
      this.originAirport = originAirport;
      this.destinationAirport = destinationAirport;
      this.originCountry = originCountry;
      this.destinationCountry = destinationCountry;
      this.totalFlightTime = totalFlightTime;
      this.departureTime = departureTime;
      this.arrivalTime = arrivalTime;
    }
  }

  // Method to load flight data from a CSV file
 
  public static List<Flight> loadFlights(String csvFile) {
    List<Flight> flights = new ArrayList<>();
    String line;

    try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        br.readLine(); // skip header
        while ((line = br.readLine()) != null) {
            String[] flightData = line.split(",");
            if (flightData.length < 11) {
                logger.warn("Skipping invalid flight data: {}", line);
                continue;
            }

            String airline = flightData[0].trim();
            String flightID = flightData[1].trim();
            String originAirport = flightData[3].trim();
            String destinationAirport = flightData[6].trim();
            String originCountry = flightData[5].trim();
            String destinationCountry = flightData[9].trim();
            String departureTime = flightData[2].trim();
            String arrivalTime = flightData[7].trim();

            int totalFlightTime = parseTotalFlightTime(flightData[10].trim());
            if (totalFlightTime == -1) continue;

            flights.add(new Flight(airline, flightID, originAirport, destinationAirport, originCountry, destinationCountry, totalFlightTime, departureTime, arrivalTime));
        }
    } catch (IOException e) {
        logger.error("Error reading flight data", e);
    }

    logger.info("Loaded {} flights", flights.size());
    return flights;
}

private static int parseTotalFlightTime(String timeString) {
    try {
        String[] timeParts = timeString.split(" ");
        int hours = 0;
        int minutes = 0;

        for (int i = 0; i < timeParts.length; i++) {
            if (timeParts[i].contains("hr")) {
                hours = Integer.parseInt(timeParts[i - 1].trim());
            } else if (timeParts[i].contains("min")) {
                minutes = Integer.parseInt(timeParts[i - 1].trim());
            }
        }

        return hours * 60 + minutes;
    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
        logger.warn("Error parsing flight time: {}", timeString, e);
        return -1;
    }
}

  // Method to use Google Maps API to calculate travel time between two airports (simulated)
  public static int getTravelTimeBetweenAirports(String origin, String destination) {
    // Placeholder for Google Maps API call
    // The API call would return the travel time between the two airports in minutes
    // This is just a placeholder value
    // To implement this, use the Google Maps Distance Matrix API as demonstrated in the previous sections
    return 60; // Assuming 60 minutes travel time for simplicity
  }

  // Dijkstra's Algorithm to find the shortest path in terms of flight time
  public static List<Flight> findShortestFlightPath(List<Flight> flights, String origin, String destination) {
    // Use a priority queue to explore the shortest paths
    PriorityQueue<FlightNode> pq = new PriorityQueue<>(Comparator.comparingInt(fn -> fn.totalTime));
    Map<String, FlightNode> airportMap = new HashMap<>();

    pq.add(new FlightNode(origin, null, 0, null));
    System.out.println("Starting search from: " + origin);  // Debugging statement

    while (!pq.isEmpty()) {
      FlightNode current = pq.poll();

      System.out.println("Processing airport: " + current.airport + " with total time: " + current.totalTime + " min");

      if (current.airport.equals(destination)) {
        System.out.println("Destination reached: " + destination);  // Debugging statement
        return buildPath(current);
      }

      for (Flight flight : flights) {
        if (flight.originAirport.equals(current.airport)) {
          int newTime = current.totalTime + flight.totalFlightTime;

          if (!airportMap.containsKey(flight.destinationAirport) || newTime < airportMap.get(flight.destinationAirport).totalTime) {
            FlightNode nextNode = new FlightNode(flight.destinationAirport, current, newTime, flight);
            airportMap.put(flight.destinationAirport, nextNode);
            pq.add(nextNode);
            System.out.println("Adding connection: " + flight.originAirport + " -> " + flight.destinationAirport + " with flight time: " + flight.totalFlightTime + " min");
          }
        }
      }

      // Check for nearby airports (e.g., moving from SHJ to DXB)
      if (current.previous != null) {
        int travelTimeToNearby = getTravelTimeBetweenAirports(current.airport, "DXB"); // Example for DXB
        for (Flight flight : flights) {
          if (flight.originAirport.equals("DXB")) { // Evaluate flights from DXB
            int totalTimeWithTransfer = current.totalTime + travelTimeToNearby + flight.totalFlightTime;
            if (!airportMap.containsKey(flight.destinationAirport) || totalTimeWithTransfer < airportMap.get(flight.destinationAirport).totalTime) {
              FlightNode nextNode = new FlightNode(flight.destinationAirport, current, totalTimeWithTransfer, flight);
              airportMap.put(flight.destinationAirport, nextNode);
              pq.add(nextNode);
              System.out.println("Evaluating nearby airport DXB -> " + flight.destinationAirport + " with total time: " + totalTimeWithTransfer + " min");
            }
          }
        }
      }
    }

    return null;  // No path found
  }

  // Helper method to build the path from the destination node
  private static List<Flight> buildPath(FlightNode node) {
    List<Flight> path = new ArrayList<>();
    while (node.previous != null) {
      path.add(node.flight);
      node = node.previous;
    }
    Collections.reverse(path);
    return path;
  }

  // Node class to represent an airport and the path to it
  static class FlightNode {
    String airport;
    FlightNode previous;
    int totalTime;
    Flight flight;

    FlightNode(String airport, FlightNode previous, int totalTime) {
      this.airport = airport;
      this.previous = previous;
      this.totalTime = totalTime;
    }

    FlightNode(String airport, FlightNode previous, int totalTime, Flight flight) {
      this.airport = airport;
      this.previous = previous;
      this.totalTime = totalTime;
      this.flight = flight;
    }
  }
  public static void main(String[] args) {
    // Update the path to your CSV file
    String csvFile = "C:\\Users\\jesse\\Desktop\\LogisticsRouteProject\\src\\data\\airline_path_df23.csv";

    // Load flights data
    List<Flight> flights = loadFlights(csvFile);

    // Define origin and destination airports
    String originAirport = "BOS";  // Boston Logan International Airport
    String destinationAirport = "MAA";  // Chennai International Airport

    // Find the shortest path from BOS to MAA
    List<Flight> shortestPath = findShortestFlightPath(flights, originAirport, destinationAirport);

    // Output the results
    if (shortestPath != null) {
      System.out.println("Shortest path found:");
      for (Flight flight : shortestPath) {
        System.out.println(flight.airline + "\t" + flight.flightID + "\t" + flight.originAirport + "\t" +
                flight.originCountry + "\t" + flight.destinationAirport + "\t" + flight.destinationCountry + "\t" +
                flight.totalFlightTime + " min" + "\tDeparture: " + flight.departureTime + "\tArrival: " + flight.arrivalTime);
      }
    } else {
      System.out.println("No connecting flight path found.");
    }
  }
}
