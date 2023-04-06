package com.typedb.examples.fraud.dao;

import com.typedb.examples.fraud.model.Bank;
import com.typedb.examples.fraud.model.BankCoordinates;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.example.TypeDB_SessionWrapper;

public class BankDAO {

  private final TypeDB_SessionWrapper wrapper;
  private final String queryInsert = "insert \n"
      + "$ban isa Bank, has name \"%s\", has company_type \"Bank\"; \n"
      + "$gcb isa Geo_coordinate, has latitude %s, has longitude %s;\n"
      + "$rel(geo: $gcb, identify: $ban) isa geolocate;";

  private final String queryGet =
      "match $geo isa Geo_coordinate, has longitude $lon, has latitude $lat;" +
          "$b isa Bank, has name $na;" +
          "(geo: $geo, identify: $b) isa geolocate;";

  private final List<String> args = Stream.of("na", "lat", "lon").collect(
      Collectors.toList());

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

  public void insertAll(Set<Bank> bankParam) throws IOException {

    Set<String> queries = bankParam.stream().map(this::getQueryStr).collect(Collectors.toSet());
    wrapper.load_data(queries);
  }

  public Set<Bank> retrieveAll() throws IOException {
    Set<Bank> banks = new HashSet<Bank>();
    Set<Hashtable<String, String>> banksStr = wrapper.read_data(queryGet, args);
    for (Hashtable<String, String> currentBank : banksStr) {
      banks.add(new Bank(currentBank.get("na"),
          new BankCoordinates(currentBank.get("lat"), currentBank.get("lon"))));
    }
    return banks;
  }

  public Hashtable<String, Bank> retrieveInternal() throws IOException {
    Hashtable<String, Bank> banks = new Hashtable<String, Bank>();
    Set<Hashtable<String, String>> banksStr = wrapper.read_data(queryGet, args);
    for (Hashtable<String, String> currentBank : banksStr) {
      banks.put(currentBank.get("na"), new Bank(currentBank.get("na"),
          new BankCoordinates(currentBank.get("lat"), currentBank.get("lon"))));
    }
    return banks;
  }
}
