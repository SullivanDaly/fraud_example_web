package com.typedb.examples.fraud.dao;

import com.typedb.examples.fraud.Pair.CardHolder__Merchant;
import com.typedb.examples.fraud.model.Bank;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import org.example.TypeDB_SessionWrapper;

public class GenericDAO {

  private final TypeDB_SessionWrapper wrapper;

  private final String queryRule = "(person: $per, company: $com, $geo1, $geo2) isa same_place;";

  public GenericDAO(TypeDB_SessionWrapper wrapper) {
    this.wrapper = wrapper;
  }

  public Set<CardHolder__Merchant> retrieveRule() throws IOException {
    CardholderDAO cardholderDAO = new CardholderDAO(wrapper);
    MerchantDAO merchantDAO = new MerchantDAO(wrapper);
    BankDAO bankDAO = new BankDAO(wrapper);
    Hashtable<String, Bank> hBank = bankDAO.retrieveInternal();

    List<String> lArgs = cardholderDAO.getlArg();
    int sizeCut = lArgs.size();
    lArgs.addAll(merchantDAO.getlArg());

    Set<CardHolder__Merchant> sRule = new HashSet<CardHolder__Merchant>();
    Set<List<String>> sRuleStr = wrapper.read_data("match " + cardholderDAO.getQueryGet()
        + merchantDAO.getQueryGet() + queryRule, lArgs);

    for (List<String> currentStrRule : sRuleStr) {
      sRule.add(new CardHolder__Merchant(cardholderDAO.cardholderBuilder(currentStrRule, hBank),
          merchantDAO.merchantBuilder(currentStrRule, sizeCut)));
    }

    return sRule;
  }

}
