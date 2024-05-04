package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class AirportController {

    private Map<String, Airport> airports = new HashMap<>();
    private Map<Integer, Flight> flights = new HashMap<>();
    private Map<Integer, Passenger> passengers = new HashMap<>();
    private Map<Integer, Set<Integer>> flightPassengerMap = new HashMap<>();

    @PostMapping("/add_airport")
    public String addAirport(@RequestBody Airport airport){
        airports.put(airport.getAirportName(), airport);
        return "SUCCESS";
    }

    @GetMapping("/get-largest-aiport")
    public String getLargestAirportName(){
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

    @GetMapping("/get-shortest-time-travel-between-cities")
    public double getShortestDurationOfPossibleBetweenTwoCities(@RequestParam("fromCity") City fromCity, @RequestParam("toCity") City toCity){
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

    @GetMapping("/get-number-of-people-on-airport-on/{date}")
    public int getNumberOfPeopleOn(@PathVariable("date") Date date, @RequestParam("airportName") String airportName){
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

    @GetMapping("/calculate-fare")
    public int calculateFlightFare(@RequestParam("flightId")Integer flightId){
        Flight flight = flights.get(flightId);
        if (flight == null) {
            return -1;
        }

        int numberOfPassengers = flightPassengerMap.getOrDefault(flightId, new HashSet<>()).size();
        return 3000 + numberOfPassengers * 50;
    }


    @PostMapping("/book-a-ticket")
    public String bookATicket(@RequestParam("flightId")Integer flightId,@RequestParam("passengerId")Integer passengerId){
        Flight flight = flights.get(flightId);
        Passenger passenger = passengers.get(passengerId);

        if (flight == null || passenger == null || flightPassengerMap.getOrDefault(flightId, new HashSet<>()).contains(passengerId) || flightPassengerMap.getOrDefault(flightId, new HashSet<>()).size() >= flight.getMaxCapacity()) {
            return "FAILURE";
        }

        flightPassengerMap.computeIfAbsent(flightId, k -> new HashSet<>()).add(passengerId);
        return "SUCCESS";
    }

    @PutMapping("/cancel-a-ticket")
    public String cancelATicket(@RequestParam("flightId")Integer flightId,@RequestParam("passengerId")Integer passengerId){
        Flight flight = flights.get(flightId);
        Passenger passenger = passengers.get(passengerId);

        if (flight == null || passenger == null || !flightPassengerMap.getOrDefault(flightId, new HashSet<>()).contains(passengerId)) {
            return "FAILURE";
        }

        flightPassengerMap.get(flightId).remove(passengerId);
        return "SUCCESS";
    }

    @GetMapping("/get-count-of-bookings-done-by-a-passenger/{passengerId}")
    public int countOfBookingsDoneByPassengerAllCombined(@PathVariable("passengerId")Integer passengerId){
        int count = 0;

        for (Set<Integer> passengersSet : flightPassengerMap.values()) {
            if (passengersSet.contains(passengerId)) {
                count++;
            }
        }

        return count;
    }

    @PostMapping("/add-flight")
    public String addFlight(@RequestBody Flight flight){
        flights.put(flight.getFlightId(), flight);
        return "SUCCESS";
    }


    @GetMapping("/get-aiportName-from-flight-takeoff/{flightId}")
    public String getAirportNameFromFlightId(@PathVariable("flightId")Integer flightId){
        Flight flight = flights.get(flightId);
        return flight != null ? flight.getFromCity().toString() : null;
    }

    @GetMapping("/calculate-revenue-collected/{flightId}")
    public int calculateRevenueOfAFlight(@PathVariable("flightId")Integer flightId){
        Flight flight = flights.get(flightId);
        if (flight == null) {
            return -1;
        }

        int numberOfPassengers = flightPassengerMap.getOrDefault(flightId, new HashSet<>()).size();
        return 3000 * numberOfPassengers;
    }

    @PostMapping("/add-passenger")
    public String addPassenger(@RequestBody Passenger passenger){
        passengers.put(passenger.getPassengerId(), passenger);
        return "SUCCESS";
    }
}
