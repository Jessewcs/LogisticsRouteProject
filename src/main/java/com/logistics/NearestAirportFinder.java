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

import org.springframework.stereotype.Service;

@Service
public class NearestAirportFinder {

  public static class Airport {
    String name;
    String address;
    double latitude;
    double longitude;
    String type;

    public Airport(String name, String address, double latitude, double longitude, String type) {
      this.name = name;
      this.address = address;
      this.latitude = latitude;
      this.longitude = longitude;
      this.type = type;
    }

    @Override
    public String toString() {
      return name + " (" + latitude + ", " + longitude + ")";
    }
  }

  public static class Flight {
    String airline;
    String flightID;
    String originAirport;
    String destinationAirport;
    String originCountry;
    String destinationCountry;
    int totalFlightTime;  // in minutes

    public Flight(String airline, String flightID, String originAirport, String destinationAirport,
           String originCountry, String destinationCountry, int totalFlightTime) {
      this.airline = airline;
      this.flightID = flightID;
      this.originAirport = originAirport;
      this.destinationAirport = destinationAirport;
      this.originCountry = originCountry;
      this.destinationCountry = destinationCountry;
      this.totalFlightTime = totalFlightTime;
    }
  }

  // Haversine formula to calculate the distance between two lat/long points
  public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    final int R = 6371; // Radius of the earth in km

    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = R * c; // convert to kilometers

    return distance;
  }

  // Load the airport data from the CSV file
  // Load the airport data from the CSV file and filter by "International"
  public static List<Airport> loadAirports(String csvFile) {
    List<Airport> airports = new ArrayList<>();
    String line;

    try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
      br.readLine(); // skip header
      while ((line = br.readLine()) != null) {
        String[] airportData = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // This handles commas inside quotes

        String name = airportData[0].trim();
        String address = airportData[3].trim();
        double latitude = Double.parseDouble(airportData[4].trim());
        double longitude = Double.parseDouble(airportData[5].trim());
        String type = airportData[8].trim(); // Assuming the 'Type' column is the 9th column (index 8)

        // Filter to include only airports with "International" in their name
        if (type.equalsIgnoreCase("Airport") && name.toLowerCase().contains("international")) {
          airports.add(new Airport(name, address, latitude, longitude, type));
        }
      }
    } catch (IOException | NumberFormatException e) {
      e.printStackTrace();
    }

    return airports;
  }


  // Find the nearest airport to a given point
  public static Airport findNearestAirport(double lat, double lon, List<Airport> airports) {
    Airport nearestAirport = null;
    double minDistance = Double.MAX_VALUE;

    for (Airport airport : airports) {
      double distance = calculateDistance(lat, lon, airport.latitude, airport.longitude);

      if (distance < minDistance) {
        minDistance = distance;
        nearestAirport = airport;
      }
    }

    return nearestAirport;
  }

  // Load flights data from a CSV file
  public static List<Flight> loadFlights(String csvFile) {
    List<Flight> flights = new ArrayList<>();
    String line;

    try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
      br.readLine(); // skip header
      while ((line = br.readLine()) != null) {
        String[] flightData = line.split(",");

        String airline = flightData[0].trim();
        String flightID = flightData[1].trim();
        String originAirport = flightData[3].trim();
        String destinationAirport = flightData[6].trim();
        String originCountry = flightData[5].trim();
        String destinationCountry = flightData[9].trim();

        // Convert Total Flight Time to minutes
        int totalFlightTime = 0;
        try {
          String[] timeParts = flightData[10].trim().split(" ");
          int hours = 0;
          int minutes = 0;

          for (int i = 0; i < timeParts.length; i++) {
            if (timeParts[i].contains("hr")) {
              hours = Integer.parseInt(timeParts[i - 1].replace("hr", "").trim());
            } else if (timeParts[i].contains("min")) {
              minutes = Integer.parseInt(timeParts[i - 1].replace("min", "").trim());
            }
          }

          totalFlightTime = hours * 60 + minutes;
        } catch (NumberFormatException e) {
          System.err.println("Error parsing flight time for flight " + flightID + ": " + e.getMessage());
          continue; // skip invalid time entries
        } catch (ArrayIndexOutOfBoundsException e) {
          System.err.println("Unexpected time format for flight " + flightID + ": " + flightData[10].trim());
          continue; // skip lines with unexpected time format
        }

        flights.add(new Flight(airline, flightID, originAirport, destinationAirport, originCountry, destinationCountry, totalFlightTime));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return flights;
  }


  // Dijkstra's Algorithm to find the shortest path in terms of flight time
  public static List<Flight> findShortestFlightPath(List<Flight> flights, String origin, String destination) {
    PriorityQueue<FlightNode> pq = new PriorityQueue<>(Comparator.comparingInt(fn -> fn.totalTime));
    Map<String, FlightNode> airportMap = new HashMap<>();

    pq.add(new FlightNode(origin, null, 0));
    while (!pq.isEmpty()) {
      FlightNode current = pq.poll();

      if (current.airport.equals(destination)) {
        return buildPath(current);
      }

      for (Flight flight : flights) {
        if (flight.originAirport.equals(current.airport)) {
          int newTime = current.totalTime + flight.totalFlightTime;

          if (!airportMap.containsKey(flight.destinationAirport) || newTime < airportMap.get(flight.destinationAirport).totalTime) {
            FlightNode nextNode = new FlightNode(flight.destinationAirport, current, newTime, flight);
            airportMap.put(flight.destinationAirport, nextNode);
            pq.add(nextNode);
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
    // Example coordinates for origin and destination
    double originLat = 42.3601;  // Example: Boston
    double originLon = -71.0589; // Example: Boston
    double destinationLat = 13.0827;  // Example: Chennai
    double destinationLon = 80.2707;  // Example: Chennai

    // Load airports data
    String airportsFile = "C:\\Users\\jesse\\Desktop\\LogisticsRouteProject\\src\\data\\updated_airports2.csv";
    List<Airport> airports = loadAirports(airportsFile);

    // Find nearest airports
    Airport nearestToOrigin = findNearestAirport(originLat, originLon, airports);
    Airport nearestToDestination = findNearestAirport(destinationLat, destinationLon, airports);

    // Output the nearest airports
    if (nearestToOrigin != null) {
      System.out.println("Nearest airport to origin:");
      System.out.println(nearestToOrigin.name + " - " + nearestToOrigin.address);
    } else {
      System.out.println("No airport found near the origin.");
    }

    if (nearestToDestination != null) {
      System.out.println("\nNearest airport to destination:");
      System.out.println(nearestToDestination.name + " - " + nearestToDestination.address);
    } else {
      System.out.println("No airport found near the destination.");
    }

    // Proceed to find the shortest flight path if both airports are found
    if (nearestToOrigin != null && nearestToDestination != null) {
      String flightsFile = "C:\\Users\\jesse\\Desktop\\LogisticsRouteProject\\src\\data\\airline_path_df23.csv";
      List<Flight> flights = loadFlights(flightsFile);

      // Find the shortest path from the origin airport to the destination airport
      List<Flight> shortestPath = findShortestFlightPath(flights, nearestToOrigin.name, nearestToDestination.name);

      // Output the results
      if (shortestPath != null) {
        System.out.println("\nShortest flight path found:");
        for (Flight flight : shortestPath) {
          System.out.println(flight.airline + " flight " + flight.flightID + " from " + flight.originAirport + " to " + flight.destinationAirport
                  + " (" + flight.totalFlightTime + " minutes)");
        }
      } else {
        System.out.println("No direct or connecting flights found between the selected airports.");
      }
    }
  }
}
