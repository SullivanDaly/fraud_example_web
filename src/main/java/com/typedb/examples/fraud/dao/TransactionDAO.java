package com.typedb.examples.fraud.dao;

import com.typedb.examples.fraud.model.Cardholder;
import com.typedb.examples.fraud.model.Merchant;
import com.typedb.examples.fraud.model.Transaction;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.example.TypeDB_SessionWrapper;

public class TransactionDAO {

  private final TypeDB_SessionWrapper wrapper;

  private final String query1 = "match \n"
      + "$per isa Person, has first_name \"%s\", has last_name \"%s\";\n"
      + "$ban isa Bank, has name \"%s\";\n"
      + "$car isa Card, has card_number %s;\n"
      + "$com isa Company, has name \"%s\";\n";

  private final String query2 = "insert \n"
      + "$r1 (owner: $per, attached_card: $car, attached_bank: $ban) isa bank_account;\n"
      + "$r2 (used_card: $car ,to: $com) isa transaction, has timestamp %s, has amount %s, "
      + "has transaction_number \"%s\";";

  private final String queryGet =
      "match $per isa Person, has first_name $first, has last_name $last;"
          + "$car isa Card, has card_number $nbcar;"
          + "$com isa Company, has name $comp;"
          + "(owner: $per, attached_card: $car, $ban) isa bank_account;"
          + "(used_card: $car ,to: $com) isa transaction, has timestamp $time"
          + ", has amount $amoun, has transaction_number $transac;";

  private final List<String> args = Stream.of("first", "last", "comp", "amoun",
          "transac", "time").collect(Collectors.toList());


  public TransactionDAO(TypeDB_SessionWrapper wrapper) {
    this.wrapper = wrapper;
  }

  private String getQueryStr(Transaction currentTransaction) {
    String result = query1.formatted(
        currentTransaction.getCardholder().getPerson_first_name(),
        currentTransaction.getCardholder().getPerson_last_name(),
        currentTransaction.getCardholder().getCreditCard().getBank().getBank_name(),
        currentTransaction.getCardholder().getCreditCard().getCard_number(),
        currentTransaction.getMerchant().getCompany_name()
    );
    result += query2.formatted(
        currentTransaction.getDate_transaction_transform(),
        currentTransaction.getAmount(),
        currentTransaction.getTransaction_number()
    );
    return result;
  }

  public void insert_all(Set<Transaction> transactions) throws IOException {
    Set<String> queries = transactions.stream().map(this::getQueryStr)
        .collect(Collectors.toSet());
    wrapper.load_data(queries);
  }

  public Set<Transaction> retrieveAll() throws IOException {

    MerchantDAO merchantDAO = new MerchantDAO(wrapper);
    Hashtable<String, Merchant> merchants = merchantDAO.retrieveInternal();
    CardholderDAO cardholderDAO = new CardholderDAO(wrapper);
    Hashtable<String, Cardholder> cardholders = cardholderDAO.retrieveInternal();

    Set<Transaction> transactions = new HashSet<Transaction>();
    Set<Hashtable<String, String>> transactionsStr = wrapper.read_data(queryGet, args);
    for (Hashtable<String, String> currentTransaction : transactionsStr) {
      Cardholder tempCardholder = cardholders.get(
          currentTransaction.get(0) + currentTransaction.get(1));
      Merchant tempMerchant = merchants.get(currentTransaction.get(2));
      transactions.add(new Transaction(currentTransaction.get(3), currentTransaction.get(4),
          currentTransaction.get(5), tempMerchant, tempCardholder));
    }
    return transactions;
  }
}

