package com.typedb.examples.fraud.dao;

import com.typedb.examples.fraud.Pair.CardHolderMerchant;
import com.typedb.examples.fraud.model.Address;
import com.typedb.examples.fraud.model.Bank;
import com.typedb.examples.fraud.model.Cardholder;
import com.typedb.examples.fraud.model.CardholderCoordinates;
import com.typedb.examples.fraud.model.CreditCard;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.example.TypeDB_SessionWrapper;

public class CardholderDAO {

  private final TypeDB_SessionWrapper wrapper;
  private final String queryInsert = "insert\n" +
      "$gcp isa Geo_coordinate, has longitude %s, has latitude %s;\n" +
      "$add isa Address, has street \"%s\", has city \"%s\", has state \"%s\", has zip %s;\n" +
      "$per isa Person, has first_name \"%s\", has last_name \"%s\", has gender \"%s\", has job \"%s\", has date_of_birth %s;\n" +
      "$car isa Card, has card_number %s;" +
      "$r3(location: $add, geo: $gcp, identify: $per) isa locate;\n";

  private final String queryGet =
      "$geo isa Geo_coordinate, has longitude $lon, has latitude $lat;" +
          "$add isa Address, has street $street, has city $city, has state $state, has zip $zip;" +
          "$per isa Person, has first_name $first, has last_name $last, has gender $gen, has job $job, has date_of_birth $birth;" +
          "$car isa Card, has card_number $nbcar;" +
          "$ban isa Bank, has name $bank;" +
          "(location: $add, geo: $geo, identify: $per) isa locate;" +
          "(owner: $per, attached_card: $car, attached_bank: $ban) isa bank_account;";

  private final String querySafe = "(person: $per, company: $com, $geo1, $geo2) isa same_place;";

  private final String queryUnsafe = "(person: $per, company: $com) isa unsafe_relationship";

  private final List<String> args = Stream.of("first", "last", "gen", "job", "birth", "street",
      "city", "state", "zip", "nbcar", "bank", "lat", "lon").collect(Collectors.toList());


  public CardholderDAO(TypeDB_SessionWrapper wrapper) {
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


  public void insert_all(Set<Cardholder> cardholderParam) throws IOException {
    Set<String> queries = cardholderParam.stream().map(this::getQueryStr)
        .collect(Collectors.toSet());
    wrapper.load_data(queries);
  }

  public Set<Cardholder> retrieveAll() throws IOException {
    BankDAO bankDAO = new BankDAO(wrapper);
    Hashtable<String, Bank> banks = bankDAO.retrieveInternal();
    Set<Cardholder> cardholders = new HashSet<Cardholder>();
    Set<Hashtable<String, String>> cardholdersStr = wrapper.read_data("match " + queryGet, args);
    for (Hashtable<String, String> currentCardholder : cardholdersStr) {
      cardholders.add(cardholderBuilder(currentCardholder, banks));
    }
    return cardholders;
  }

  public Hashtable<String, Cardholder> retrieveInternal() throws IOException {
    BankDAO bankDAO = new BankDAO(wrapper);
    Hashtable<String, Bank> banks = bankDAO.retrieveInternal();
    Hashtable<String, Cardholder> cardholders = new Hashtable<String, Cardholder>();
    Set<Hashtable<String, String>> cardholdersStr = wrapper.read_data("match " + queryGet, args);
    for (Hashtable<String, String> currentCardholder : cardholdersStr) {
      cardholders.put(currentCardholder.get(0) + currentCardholder.get(1),
          cardholderBuilder(currentCardholder, banks));
    }
    return cardholders;
  }

  public Cardholder cardholderBuilder(Hashtable<String, String> cardholdersParam,
      Hashtable<String, Bank> banksParam) {
    Address tempAddress = new Address(cardholdersParam.get("street"), cardholdersParam.get("city"),
        cardholdersParam.get("state"), cardholdersParam.get("zip"));
    CardholderCoordinates tempCoord = new CardholderCoordinates(cardholdersParam.get("lat"),
        cardholdersParam.get("lon"));
    CreditCard tempCard = new CreditCard(cardholdersParam.get("nbcar"),
        banksParam.get(cardholdersParam.get("bank")));
    Cardholder resultCardholder = new Cardholder(cardholdersParam.get("first"),
        cardholdersParam.get("last"),
        cardholdersParam.get("gen"),
        cardholdersParam.get("job"), cardholdersParam.get("birth"), tempAddress, tempCoord,
        tempCard);

    return resultCardholder;
  }

  public Set<CardHolderMerchant> retrievePossibleSafeTransaction() throws IOException {
    MerchantDAO merchantDAO = new MerchantDAO(wrapper);
    BankDAO bankDAO = new BankDAO(wrapper);
    Hashtable<String, Bank> banks = bankDAO.retrieveInternal();

    List<String> args = this.args;
    args.addAll(merchantDAO.getArgs());

    Set<CardHolderMerchant> rules = new HashSet<CardHolderMerchant>();
    Set<Hashtable<String, String>> rulesStr = wrapper.read_data("match " + this.getQueryGet()
        + merchantDAO.getQueryGet() + querySafe, args);

    for (Hashtable<String, String> currentStrRule : rulesStr) {
      rules.add(new CardHolderMerchant(cardholderBuilder(currentStrRule, banks),
          merchantDAO.merchantBuilder(currentStrRule)));
    }

    return rules;
  }

  public Set<CardHolderMerchant> retrieveUnsafeTransaction() throws IOException {
    MerchantDAO merchantDAO = new MerchantDAO(wrapper);
    BankDAO bankDAO = new BankDAO(wrapper);
    Hashtable<String, Bank> banks = bankDAO.retrieveInternal();

    List<String> args = this.args;
    args.addAll(merchantDAO.getArgs());

    Set<CardHolderMerchant> rules = new HashSet<CardHolderMerchant>();
    Set<Hashtable<String, String>> rulesStr = wrapper.read_data("match " + this.getQueryGet()
        + merchantDAO.getQueryGet() + queryUnsafe, args);

    for (Hashtable<String, String> currentStrRule : rulesStr) {
      rules.add(new CardHolderMerchant(cardholderBuilder(currentStrRule, banks),
          merchantDAO.merchantBuilder(currentStrRule)));
    }

    return rules;
  }

  public String getQueryGet() {
    return queryGet;
  }

  public List<String> getArgs() {
    return args;
  }
}
