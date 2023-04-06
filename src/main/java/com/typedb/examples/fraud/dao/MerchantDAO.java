package com.typedb.examples.fraud.dao;

import com.typedb.examples.fraud.model.Merchant;
import com.typedb.examples.fraud.model.MerchantCoordinates;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.example.TypeDB_SessionWrapper;

public class MerchantDAO {

  private final TypeDB_SessionWrapper wrapper;
  private final String queryInsert = "insert \n"
      + "$gcc isa Geo_coordinate, has longitude %s, has latitude %s;\n"
      + "$com isa Company, has name \"%s\", has company_type \"%s\";\n"
      + "$rel(geo: $gcc, identify: $com) isa geolocate;";

  private final String queryGet =
      "$mgeo isa Geo_coordinate, has longitude $mlon, has latitude $mlat;"
          + "$com isa Company, has name $na, has company_type $comt;"
          + "(geo: $mgeo, identify: $com) isa geolocate;";

  private final List<String> args = Stream.of("na", "comt", "mlat", "mlon")
      .collect(Collectors.toList());

  public MerchantDAO(TypeDB_SessionWrapper wrapper) {
    this.wrapper = wrapper;
  }

  private String getQueryStr(Merchant currentMerchant) {
    String result = queryInsert.formatted(
        currentMerchant.getMerchantCoordinates().getLongitude(),
        currentMerchant.getMerchantCoordinates().getLatitude(),
        currentMerchant.getCompany_name(),
        currentMerchant.getCompany_cat()
    );
    return result;
  }

  public void insert_all(Set<Merchant> merchants) throws IOException {
    Set<String> queries = merchants.stream().map(this::getQueryStr).collect(Collectors.toSet());
    wrapper.load_data(queries);
  }

  public Set<Merchant> retrieveAll() throws IOException {
    Set<Merchant> merchants = new HashSet<Merchant>();
    Set<List<String>> merchantsStr = wrapper.read_data("match " + queryGet, args);
    for (List<String> currentMerchant : merchantsStr) {
      merchants.add(merchantBuilder(currentMerchant));
    }
    return merchants;
  }

  public Hashtable<String, Merchant> retrieveInternal() throws IOException {
    Hashtable<String, Merchant> merchants = new Hashtable<String, Merchant>();
    Set<List<String>> merchantsStr = wrapper.read_data("match " + queryGet, args);
    for (List<String> currentMerchant : merchantsStr) {
      merchants.put(currentMerchant.get(0), merchantBuilder(currentMerchant));
    }
    return merchants;
  }

  public Merchant merchantBuilder(List<String> merchantParam) {
    return merchantBuilder(merchantParam, 0);
  }

  public Merchant merchantBuilder(List<String> merchantParam, int beginParam) {
    Merchant resultMerchant = new Merchant(merchantParam.get(beginParam + 0), merchantParam.get(beginParam + 1),
        new MerchantCoordinates(merchantParam.get(beginParam + 2), merchantParam.get(beginParam + 3)));
    return resultMerchant;
  }

  public String getQueryGet() {
    return queryGet;
  }

  public List<String> getArgs() {
    return args;
  }
}
