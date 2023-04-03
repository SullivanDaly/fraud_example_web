package com.typedb.examples.fraud.dao;

import com.typedb.examples.fraud.model.*;
import org.example.TypeDB_SessionWrapper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CardholderDAO {

    private final TypeDB_SessionWrapper wrapper;
    private final String queryInsert = "insert\n"
            + "$gcp isa Geo_coordinate, has longitude %s, has latitude %s;\n"
            + "$add isa Address, has street \"%s\", has city \"%s\", has state \"%s\", has zip %s;\n"
            + "$per isa Person, has first_name \"%s\", has last_name \"%s\", has gender \"%s\", has job \"%s\", has date_of_birth %s;\n"
            + "$car isa Card, has card_number %s;"
            + "$r3(location: $add, geo: $gcp, identify: $per) isa locate;\n";

    private final String queryGet = "match $geo isa Geo_coordinate, has longitude $lon, has latitude $lat;"
        + "$add isa Address, has street $street, has city $city, has state $state, has zip $zip;"
        + "$per isa Person, has first_name $first, has last_name $last, has gender $gen, has job $job, has date_of_birth $birth;"
        + "$car isa Card, has card_number $nbcar;"
        + "$ban isa Bank, has name $bank;"
        + "(location: $add, geo: $geo, identify: $per) isa locate;"
        + "(owner: $per, attached_card: $car, attached_bank: $ban) isa bank_account;";

    private final List<String> lArg = Arrays.asList("first", "last", "gen", "job", "birth", "street", "city", "state", "zip", "nbcar", "bank", "lat", "lon");

    public CardholderDAO( TypeDB_SessionWrapper wrapper) {
        this.wrapper = wrapper;
    }

    private String getQueryStr(Cardholder currentCardholder) {
        String result = queryInsert.formatted(
                    currentCardholder.getCardholderCoordinates().getLongitude(),
                    currentCardholder.getCardholderCoordinates().getLatitude(),
                    currentCardholder.getAddress().getStreet(),
                    currentCardholder.getAddress().getCity(),
                    currentCardholder.getAddress().getState(),
                    currentCardholder.getAddress().getZip(),
                    currentCardholder.getPerson_first_name(),
                    currentCardholder.getPerson_last_name(),
                    currentCardholder.getGender(),
                    currentCardholder.getJob(),
                    currentCardholder.getDate_of_birth(),
                    currentCardholder.getCreditCard().getCard_number()
            );
        return (result);
    }


    public void insert_all(Set<Cardholder> lCardholder) throws IOException {
        Set<String> queryStrs = lCardholder.stream().map(this::getQueryStr).collect(Collectors.toSet());
        wrapper.load_data(queryStrs);
    }

    public Set<Cardholder> retrieveAll() throws IOException {
        BankDAO bankDAO = new BankDAO(wrapper);
        Hashtable<String, Bank> hBank = bankDAO.retrieveInternal();
        Set<Cardholder> sCardholder = new HashSet<Cardholder>();
        Set<List<String>> sCardholderStr = wrapper.read_data(queryGet, lArg);
        for (List<String> currentStrCardholder : sCardholderStr) {
            Address tempAddress = new Address(currentStrCardholder.get(5), currentStrCardholder.get(6), currentStrCardholder.get(7), currentStrCardholder.get(8));
            CardholderCoordinates tempCoord = new CardholderCoordinates(currentStrCardholder.get(11), currentStrCardholder.get(12));
            CreditCard tempCard = new CreditCard(currentStrCardholder.get(9), hBank.get(currentStrCardholder.get(10)));
            sCardholder.add(new Cardholder(currentStrCardholder.get(0), currentStrCardholder.get(1), currentStrCardholder.get(2),
                    currentStrCardholder.get(3), currentStrCardholder.get(4), tempAddress, tempCoord, tempCard));
        }
        return sCardholder;
    }

    public Hashtable<String, Cardholder> retrieveInternal() throws IOException {
        BankDAO bankDAO = new BankDAO(wrapper);
        Hashtable<String, Bank> hBank = bankDAO.retrieveInternal();
        Hashtable<String, Cardholder> hCardholder = new Hashtable<String, Cardholder>();
        Set<List<String>> sCardholderStr = wrapper.read_data(queryGet, lArg);
        for (List<String> currentStrCardholder : sCardholderStr) {
            Address tempAddress = new Address(currentStrCardholder.get(5), currentStrCardholder.get(6), currentStrCardholder.get(7), currentStrCardholder.get(8));
            CardholderCoordinates tempCoord = new CardholderCoordinates(currentStrCardholder.get(11), currentStrCardholder.get(12));
            CreditCard tempCard = new CreditCard(currentStrCardholder.get(9), hBank.get(currentStrCardholder.get(10)));
            hCardholder.put(currentStrCardholder.get(0)+currentStrCardholder.get(1), new Cardholder(currentStrCardholder.get(0), currentStrCardholder.get(1), currentStrCardholder.get(2),
                    currentStrCardholder.get(3), currentStrCardholder.get(4), tempAddress, tempCoord, tempCard));
        }
        return hCardholder;
    }


}
