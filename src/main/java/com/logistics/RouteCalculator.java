package com.logistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

@Service
public class RouteCalculator {
    private static final Logger logger = LoggerFactory.getLogger(RouteCalculator.class);
    private final NearestAirportFinder airportFinder;
    private final FlightPathFinder flightPathFinder;
    private final GeoApiContext geoContext;

    public RouteCalculator() {
        this.airportFinder = new NearestAirportFinder();
        this.flightPathFinder = new FlightPathFinder();
        this.geoContext = new GeoApiContext.Builder()
            .apiKey("AIzaSyALPLc8gr9SlTQBe9Y1V_dA92e6W95QwmQ")
            .build();
    }

    public Map<String, Object> calculateRoute(String origin, String destination, String departureTime) {
        try {
            logger.info("Calculating route from {} to {} at {}", origin, destination, departureTime);
            
            double[] originCoords = getCoordinates(origin);
            double[] destCoords = getCoordinates(destination);
            
            logger.info("Origin coordinates: {}, {}", originCoords[0], originCoords[1]);
            logger.info("Destination coordinates: {}, {}", destCoords[0], destCoords[1]);

            List<NearestAirportFinder.Airport> airports = NearestAirportFinder.loadAirports("C:\\Users\\jesse\\Desktop\\LogisticsRouteProject\\src\\data\\updated_airports2.csv");
            logger.info("Loaded {} airports", airports.size());

            NearestAirportFinder.Airport nearestOrigin = NearestAirportFinder.findNearestAirport(originCoords[0], originCoords[1], airports);
            NearestAirportFinder.Airport nearestDest = NearestAirportFinder.findNearestAirport(destCoords[0], destCoords[1], airports);
            
            logger.info("Nearest origin airport: {}", nearestOrigin.name);
            logger.info("Nearest destination airport: {}", nearestDest.name);

            List<FlightPathFinder.Flight> flights = FlightPathFinder.loadFlights("C:\\Users\\jesse\\Desktop\\LogisticsRouteProject\\src\\data\\airline_path_df23.csv");
            logger.info("Loaded {} flights", flights.size());

            List<FlightPathFinder.Flight> shortestPath = FlightPathFinder.findShortestFlightPath(flights, nearestOrigin.name, nearestDest.name);
            
            if (shortestPath == null || shortestPath.isEmpty()) {
                logger.warn("No flight path found");
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("error", "No flight path found between the given airports");
                return errorResult;
            }

            logger.info("Found shortest path with {} flights", shortestPath.size());

            Map<String, Object> result = new HashMap<>();
            result.put("originAirport", nearestOrigin.name);
            result.put("destinationAirport", nearestDest.name);
            result.put("departureTime", departureTime);
            result.put("flightPath", shortestPath);

            return result;
        } catch (Exception e) {
            logger.error("Error calculating route", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "Failed to calculate route: " + e.getMessage());
            return errorResult;
        }
    }

    private double[] getCoordinates(String address) throws Exception {
        GeocodingResult[] results = GeocodingApi.geocode(geoContext, address).await();
        if (results.length > 0) {
            return new double[]{results[0].geometry.location.lat, results[0].geometry.location.lng};
        }
        throw new Exception("Could not find coordinates for " + address);
    }
}