package com.typedb.examples.fraud.dao;

import com.typedb.examples.fraud.model.Bank;
import com.typedb.examples.fraud.model.BankCoordinates;
import org.example.TypeDB_SessionWrapper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BankDAO {
    private final TypeDB_SessionWrapper wrapper;
    private final String queryInsert = "insert \n"
            + "$ban isa Bank, has name \"%s\", has company_type \"Bank\"; \n"
            + "$gcb isa Geo_coordinate, has latitude %s, has longitude %s;\n"
            + "$rel(geo: $gcb, identify: $ban) isa geolocate;";

    private final String queryGet = "match $geo isa Geo_coordinate, has longitude $lon, has latitude $lat;" +
            "$b isa Bank, has name $na;" +
            "(geo: $geo, identify: $b) isa geolocate;";

    private final List<String> lArg = Arrays.asList("na", "lat", "lon");

    public BankDAO(TypeDB_SessionWrapper wrapper) {
        this.wrapper = wrapper;
    }

    private String getQueryStr(Bank currentBank) {
        String result = queryInsert.formatted(
                    currentBank.getBank_name(),
                    currentBank.getBankCoordinates().getLatitude(),
                    currentBank.getBankCoordinates().getLongitude()
            );
        return (result);
    }

    public void insertAll(Set<Bank> lBank) throws IOException {

        Set<String> queryStrs = lBank.stream().map(this::getQueryStr).collect(Collectors.toSet());
        wrapper.load_data(queryStrs);
    }

    public Set<Bank> retrieveAll() throws IOException {
        Set<Bank> sBank = new HashSet<Bank>();
        Set<List<String>> sBankStr = wrapper.read_data(queryGet, lArg);
        for (List<String> currentStrBank : sBankStr) {
            sBank.add(new Bank(currentStrBank.get(0), new BankCoordinates(currentStrBank.get(1), currentStrBank.get(2))));
        }
        return sBank;
    }

    public Hashtable<String, Bank> retrieveInternal() throws IOException {
        Hashtable<String, Bank> hBank = new Hashtable<String, Bank>();
        Set<List<String>> sBankStr = wrapper.read_data(queryGet, lArg);
        for (List<String> currentStrBank : sBankStr) {
            hBank.put(currentStrBank.get(0), new Bank(currentStrBank.get(0), new BankCoordinates(currentStrBank.get(1), currentStrBank.get(2))));
        }
        return hBank;
    }
}
