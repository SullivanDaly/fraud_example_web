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

  private final List<String> lArg = Stream.of("na", "comt", "mlat", "mlon")
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

  public void insert_all(Set<Merchant> lMerchant) throws IOException {
    Set<String> queryStrs = lMerchant.stream().map(this::getQueryStr).collect(Collectors.toSet());
    wrapper.load_data(queryStrs);
  }

  public Set<Merchant> retrieveAll() throws IOException {
    Set<Merchant> sMerchant = new HashSet<Merchant>();
    Set<List<String>> sMerchantStr = wrapper.read_data("match " + queryGet, lArg);
    for (List<String> currentStrMerchant : sMerchantStr) {
      sMerchant.add(merchantBuilder(currentStrMerchant));
    }
    return sMerchant;
  }

  public Hashtable<String, Merchant> retrieveInternal() throws IOException {
    Hashtable<String, Merchant> hMerchant = new Hashtable<String, Merchant>();
    Set<List<String>> sMerchantStr = wrapper.read_data("match " + queryGet, lArg);
    for (List<String> currentStrMerchant : sMerchantStr) {
      hMerchant.put(currentStrMerchant.get(0), merchantBuilder(currentStrMerchant));
    }
    Set<Merchant> sMerchant = new HashSet<Merchant>();
    return hMerchant;
  }

  public Merchant merchantBuilder(List<String> pList) {
    return merchantBuilder(pList, 0);
  }

  public Merchant merchantBuilder(List<String> pList, int pBegin) {
    Merchant tmpMerchant = new Merchant(pList.get(pBegin + 0), pList.get(pBegin + 1),
        new MerchantCoordinates(pList.get(pBegin + 2), pList.get(pBegin + 3)));
    return tmpMerchant;
  }

  public String getQueryGet() {
    return queryGet;
  }

  public List<String> getlArg() {
    return lArg;
  }
}
