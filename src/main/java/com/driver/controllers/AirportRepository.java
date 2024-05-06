package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
public class AirportRepository {
    HashMap<String, Airport> airportDB = new HashMap<>();
    HashMap<Integer, Flight> flightDB = new HashMap<>();
    HashMap<Integer, Passenger> passengerDB = new HashMap<>();
    HashMap<Integer, List<Integer>> ticketsDB = new HashMap<>();

    public void addairport(Airport airport)
    {
        airportDB.put(airport.getAirportName(), airport);
    }

    public String getLargestAirportName(){
        int count = 0;
        //finding the largest airport
        for(Airport airport : airportDB.values())
        {
            if(airport.getNoOfTerminals() >= count)
            {
                count = airport.getNoOfTerminals();
            }
        }

        // checking if the airports have same no of terminals

        List<String> list = new ArrayList<>();
        for(Airport airport : airportDB.values())
        {
            if(airport.getNoOfTerminals() == count)
            {
                list.add(airport.getAirportName());
            }
        }
        Collections.sort(list);

        return list.get(0);
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromcity, City tocity)
    {
        double time = Double.MAX_VALUE;

        for(Flight flight : flightDB.values())
        {
            if(flight.getFromCity() == fromcity && flight.getToCity() == tocity)
            {
                time = Math.min(time, flight.getDuration());
            }
        }
        return time == Double.MAX_VALUE ? -1 : time;
    }

    public int getNumberOfPeopleOn(Date date, String airportName){
        int ans = 0;
        if(airportDB.containsKey(airportName)) {
            City city = airportDB.get(airportName).getCity();
            for (Integer flightId : ticketsDB.keySet()) {
                Flight flight = flightDB.get(flightId);
                if (flight.getFlightDate().equals(date) && (flight.getToCity().equals(city) || flight.getFromCity().equals(city))) {
                    ans += ticketsDB.get(flightId).size();
                }
            }
        }
        return ans;
    }
    public int calculateFlightFare(Integer flightId)
    {
        int size = ticketsDB.get(flightId).size();
        return 3000 + (size * 50);
    }
    public String bookATicket(Integer flightId, Integer passengerId)
    {
        if(ticketsDB.containsKey(flightId)) {
            List<Integer> list = ticketsDB.get(flightId);
            Flight flight = flightDB.get(flightId);
            if (list.size() == flight.getMaxCapacity())
                return "FAILURE";
            if (list.contains(passengerId))
                return "FAILURE";
            list.add(passengerId);
            ticketsDB.put(flightId, list);
            return "SUCCESS";
        }else {
            List<Integer> list = new ArrayList<>();
            list.add(passengerId);
            ticketsDB.put(flightId, list);
            return "SUCCESS";
        }
    }

    public String cancelATicket(Integer flightId, Integer passengerId){
        if(ticketsDB.containsKey(flightId)){
            boolean removed = false;
            List<Integer> passengerList = ticketsDB.get(flightId);
            if(passengerList == null)
                return "FAILURE";
            if(passengerList.contains(passengerId)){
                passengerList.remove(passengerId);
                removed = true;
            }
            if(removed) {
                ticketsDB.put(flightId, passengerList);
                return "SUCCESS";
            }
            else
                return "FAILURE";
        }
        return "FAILURE";
    }
    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId){
        int count = 0;
        for(List<Integer> list : ticketsDB.values()){
            for(Integer i : list){
                if( i == passengerId)
                    count++;
            }
        }
        return count;
    }
    public void addFlight(Flight flight){
        flightDB.put(flight.getFlightId(),flight);
    }

    public String getAirportNameFromFlightId(Integer flightId){
        for(Flight flight : flightDB.values()){
            if(flight.getFlightId() == flightId) {
                City city = flight.getFromCity();
                for (Airport airport : airportDB.values()) {
                    if (airport.getCity().equals(city))
                        return airport.getAirportName();
                }
            }
        }
        return null;
    }

    public int calculateRevenueOfAFlight(Integer flightId){
        if(ticketsDB.containsKey(flightId)) {
            int count = ticketsDB.get(flightId).size();
            int revenue = 0;
            for (int i = 0; i < count; i++) {
                revenue += 3000 + (i * 50);
            }
            return revenue;
        }
        return 0;
    }

    public void addPassenger(Passenger passenger){
        passengerDB.put(passenger.getPassengerId(), passenger);
    }
}