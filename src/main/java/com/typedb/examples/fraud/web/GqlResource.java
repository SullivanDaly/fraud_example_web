package com.typedb.examples.fraud.web;

import com.typedb.examples.fraud.dao.BankDAO;
import com.typedb.examples.fraud.dao.CardholderDAO;
import com.typedb.examples.fraud.dao.MerchantDAO;
import com.typedb.examples.fraud.dao.TransactionDAO;
import com.typedb.examples.fraud.model.Bank;
import com.typedb.examples.fraud.model.Cardholder;
import com.typedb.examples.fraud.model.Merchant;
import com.typedb.examples.fraud.model.Transaction;
import com.typedb.examples.fraud.result.CardHolderMerchant;
import java.util.Set;
import javax.inject.Inject;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
public class GqlResource {

  @Inject
  BankDAO bankDAO;
  @Inject
  MerchantDAO merchantDAO;
  @Inject
  CardholderDAO cardholderDAO;
  @Inject
  TransactionDAO transactionDAO;

  @Query
  @Description("Get all banks")
  public Set<Bank> getBanks() {

    return bankDAO.getAll();
  }

  @Query
  @Description("Get all merchants")
  public Set<Merchant> getMerchants() {

    return merchantDAO.getAll();
  }

  @Query
  @Description("Get all cardholders")
  public Set<Cardholder> getCardholders() {

    return cardholderDAO.getAll();
  }

  @Query
  @Description("Get all transactions")
  public Set<Transaction> getTransactions() {

    return transactionDAO.getAll();
  }

  @Query
  @Description("Get cardholders and merchants from safe transactions")
  public Set<CardHolderMerchant> getCardholdersWithSafeTransactions() {

    return cardholderDAO.getWithSafeMerchants();
  }

  @Query
  @Description("Get cardholders and merchants from unsafe transactions")
  public Set<CardHolderMerchant> getCardholdersWithUnsafeTransactions() {

    return cardholderDAO.getWithUnsafeMerchants();
  }
}