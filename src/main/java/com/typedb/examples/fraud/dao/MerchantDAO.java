package com.typedb.examples.fraud.dao;

import com.typedb.examples.fraud.model.Merchant;
import com.typedb.examples.fraud.model.MerchantCoordinates;
import org.example.TypeDB_SessionWrapper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MerchantDAO {
    private final TypeDB_SessionWrapper wrapper;
    private final String queryInsert = "insert \n"
            + "$gcc isa Geo_coordinate, has longitude %s, has latitude %s;\n"
            + "$com isa Company, has name \"%s\", has company_type \"%s\";\n"
            + "$rel(geo: $gcc, identify: $com) isa geolocate;";

    private final String queryGet = "match $geo isa Geo_coordinate, has longitude $lon, has latitude $lat;"
        + "$b isa Company, has name $na, has company_type $com;"
        + "(geo: $geo, identify: $b) isa geolocate;";

    private final List<String> lArg = Arrays.asList("na", "com", "lat", "lon");

    public MerchantDAO(TypeDB_SessionWrapper wrapper) {
        this.wrapper = wrapper;
    }

    private String getQueryStr(Merchant currentMerchant){
        String result = queryInsert.formatted(
                    currentMerchant.getMerchantCoordinates().getLongitude(),
                    currentMerchant.getMerchantCoordinates().getLatitude(),
                    currentMerchant.getCompany_name(),
                    currentMerchant.getCompany_cat()
            );
        return result;
    }

    public void insert_all(Set<Merchant> lMerchant) throws IOException {
        Set<String> queryStrs = lMerchant.stream().map(this::getQueryStr).collect(Collectors.toSet());
        wrapper.load_data(queryStrs);
    }

    public Set<Merchant> retrieveAll() throws IOException {
        Set<Merchant> sMerchant = new HashSet<Merchant>();
        Set<List<String>> sMerchantStr = wrapper.read_data(queryGet, lArg);
        for (List<String> currentStrMerchant : sMerchantStr) {
            sMerchant.add(new Merchant(currentStrMerchant.get(0), currentStrMerchant.get(1),
                    new MerchantCoordinates(currentStrMerchant.get(2), currentStrMerchant.get(3))));
        }
        return sMerchant;
    }

    public Hashtable<String, Merchant> retrieveInternal() throws IOException {
        Hashtable<String, Merchant> hMerchant = new Hashtable<String, Merchant>();
        Set<List<String>> sMerchantStr = wrapper.read_data(queryGet, lArg);
        for (List<String> currentStrMerchant : sMerchantStr) {
            hMerchant.put(currentStrMerchant.get(0), new Merchant(currentStrMerchant.get(0), currentStrMerchant.get(1),
                    new MerchantCoordinates(currentStrMerchant.get(2), currentStrMerchant.get(3))));
        }
        Set<Merchant> sMerchant = new HashSet<Merchant>();
        return hMerchant;
    }

}
