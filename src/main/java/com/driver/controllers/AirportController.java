package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/airport")
public class AirportController {

    private Map<String, Airport> airports = new HashMap<>();
    private Map<Integer, Flight> flights = new HashMap<>();
    private Map<Integer, Passenger> passengers = new HashMap<>();
    private Map<Integer, Set<Integer>> flightPassengerMap = new HashMap<>();

    // Method to add an airport
    @PostMapping("/add-airport")
    public String addAirport(@RequestBody Airport airport) {
        if (airport == null || airport.getAirportName() == null) {
            return "FAILURE: Invalid airport data";
        }
        airports.put(airport.getAirportName(), airport);
        return "SUCCESS: Airport added successfully";
    }

    // Method to get the name of the largest airport
    @GetMapping("/get-largest-airport")
    public String getLargestAirportName() {
        if (airports.isEmpty()) {
            return "FAILURE: No airports found";
        }
        String largestAirportName = null;
        int maxTerminals = Integer.MIN_VALUE;

        for (Airport airport : airports.values()) {
            if (airport.getNoOfTerminals() > maxTerminals) {
                maxTerminals = airport.getNoOfTerminals();
                largestAirportName = airport.getAirportName();
            } else if (airport.getNoOfTerminals() == maxTerminals) {
                largestAirportName = largestAirportName.compareTo(airport.getAirportName()) < 0 ? largestAirportName : airport.getAirportName();
            }
        }
        return largestAirportName;
    }

    // Method to get the shortest travel duration between two cities
    @GetMapping("/get-shortest-travel-duration")
    public double getShortestDurationOfPossibleBetweenTwoCities(@RequestParam("fromCity") City fromCity, @RequestParam("toCity") City toCity) {
        if (flights.isEmpty()) {
            return -1;
        }
        double shortestDuration = Double.MAX_VALUE;
        boolean found = false;

        for (Flight flight : flights.values()) {
            if (flight.getFromCity().equals(fromCity) && flight.getToCity().equals(toCity)) {
                shortestDuration = Math.min(shortestDuration, flight.getDuration());
                found = true;
            }
        }
        return found ? shortestDuration : -1;
    }

    // Method to get the number of people at an airport on a specific date
    @GetMapping("/get-number-of-people-on-airport")
    public int getNumberOfPeopleOnAirport(@RequestParam("date") Date date, @RequestParam("airportName") String airportName) {
        int count = 0;

        for (Flight flight : flights.values()) {
            if (Objects.equals(flight.getFlightDate(), date)) {
                if (flight.getFromCity().toString().equals(airportName) || flight.getToCity().toString().equals(airportName)) {
                    count += flightPassengerMap.getOrDefault(flight.getFlightId(), new HashSet<>()).size();
                }
            }
        }
        return count;
    }

    // Method to calculate the flight fare
    @GetMapping("/calculate-fare")
    public int calculateFlightFare(@RequestParam("flightId") Integer flightId) {
        Flight flight = flights.get(flightId);
        if (flight == null) {
            return -1;
        }
        int numberOfPassengers = flightPassengerMap.getOrDefault(flightId, new HashSet<>()).size();
        return 3000 + numberOfPassengers * 50;
    }

    // Method to book a ticket
    @PostMapping("/book-ticket")
    public String bookATicket(@RequestParam("flightId") Integer flightId, @RequestParam("passengerId") Integer passengerId) {
        Flight flight = flights.get(flightId);
        Passenger passenger = passengers.get(passengerId);

        if (flight == null || passenger == null || flightPassengerMap.getOrDefault(flightId, new HashSet<>()).contains(passengerId) || flightPassengerMap.getOrDefault(flightId, new HashSet<>()).size() >= flight.getMaxCapacity()) {
            return "FAILURE: Unable to book ticket";
        }

        flightPassengerMap.computeIfAbsent(flightId, k -> new HashSet<>()).add(passengerId);
        return "SUCCESS: Ticket booked successfully";
    }

    // Method to cancel a ticket
    @PutMapping("/cancel-ticket")
    public String cancelATicket(@RequestParam("flightId") Integer flightId, @RequestParam("passengerId") Integer passengerId) {
        Flight flight = flights.get(flightId);
        Passenger passenger = passengers.get(passengerId);

        if (flight == null || passenger == null || !flightPassengerMap.getOrDefault(flightId, new HashSet<>()).contains(passengerId)) {
            return "FAILURE: Unable to cancel ticket";
        }

        flightPassengerMap.get(flightId).remove(passengerId);
        return "SUCCESS: Ticket canceled successfully";
    }

    // Method to get the count of bookings done by a passenger
    @GetMapping("/get-booking-count-by-passenger")
    public int countOfBookingsDoneByPassengerAllCombined(@RequestParam("passengerId") Integer passengerId) {
        int count = 0;

        for (Set<Integer> passengersSet : flightPassengerMap.values()) {
            if (passengersSet.contains(passengerId)) {
                count++;
            }
        }

        return count;
    }

    // Method to add a flight
    @PostMapping("/add-flight")
    public String addFlight(@RequestBody Flight flight) {
        if (flight == null || flight.getFlightId() == null) {
            return "FAILURE: Invalid flight data";
        }
        flights.put(flight.getFlightId(), flight);
        return "SUCCESS: Flight added successfully";
    }

    // Method to get the airport name from the flight takeoff
    @GetMapping("/get-airport-name-from-flight-takeoff/{flightId}")
    public String getAirportNameFromFlightId(@PathVariable("flightId") Integer flightId) {
        Flight flight = flights.get(flightId);
        if (flight == null) {
            return "FAILURE: Flight not found";
        }
        return flight.getFromCity() != null ? flight.getFromCity().toString() : "Unknown";
    }

    // Method to calculate the revenue collected for a flight
    @GetMapping("/calculate-revenue-collected/{flightId}")
    public int calculateRevenueOfAFlight(@PathVariable("flightId") Integer flightId) {
        Flight flight = flights.get(flightId);
        if (flight == null) {
            return -1;
        }
        int numberOfPassengers = flightPassengerMap.getOrDefault(flightId, new HashSet<>()).size();
        return 3000 * numberOfPassengers;
    }

    // Method to add a passenger
    @PostMapping("/add-passenger")
    public String addPassenger(@RequestBody Passenger passenger) {
        if (passenger == null || passenger.getPassengerId() == 0) {
            return "FAILURE: Invalid passenger data";
        }
        // Ensure passengerId is not null and greater than 0
        if (passenger.getPassengerId() <= 0) {
            return "FAILURE: Passenger ID must be a positive non-zero integer";
        }
        passengers.put(passenger.getPassengerId(), passenger);
        return "SUCCESS: Passenger added successfully";
    }
}