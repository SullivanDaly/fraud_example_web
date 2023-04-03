package com.vaticle;

import com.typedb.examples.fraud.dao.BankDAO;
import com.typedb.examples.fraud.dao.CardholderDAO;
import com.typedb.examples.fraud.dao.MerchantDAO;
import com.typedb.examples.fraud.dao.TransactionDAO;
import com.typedb.examples.fraud.model.*;
import org.eclipse.microprofile.graphql.DefaultValue;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.example.TypeDB_SessionWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@GraphQLApi
public class HelloGraphQLResource {

    @Query
    @Description("Say hello")
    public String sayHello(@DefaultValue("World") String name) {
        return "Hello " + name;
    }

    @Query
    @Description("Test query")
    public List<Bank> getAllBanks(){
        List<Bank> lBank = new ArrayList<Bank>();
        lBank.add(new Bank("test1", new BankCoordinates("21", "32")));
        lBank.add(new Bank("test2", new BankCoordinates("45", "56")));
        return lBank;
    }

        @Query
    @Description("Test query")
    public String getStrBanks(){
        List<Bank> lBank = new ArrayList<Bank>();
        lBank.add(new Bank("test1", new BankCoordinates("21", "32")));
        lBank.add(new Bank("test2", new BankCoordinates("45", "56")));
        return lBank.get(0).toString() + lBank.get(1).toString();
    }

    @Query
    @Description("test read")
    public Set<Bank> getMatchBank() throws IOException {
        TypeDB_SessionWrapper typeDBw = new TypeDB_SessionWrapper();
        BankDAO bankDAO = new BankDAO(typeDBw);
        return bankDAO.retrieveAll();
    }

    @Query
    @Description("test read")
    public Set<Merchant> getMatchMerchant() throws IOException {
        TypeDB_SessionWrapper typeDBw = new TypeDB_SessionWrapper();
        MerchantDAO merchantDAO = new MerchantDAO(typeDBw);
        return merchantDAO.retrieveAll();
    }

    @Query
    @Description("test read")
    public Set<Cardholder> getMatchCardholder() throws IOException {
        TypeDB_SessionWrapper typeDBw = new TypeDB_SessionWrapper();
        CardholderDAO cardholderDAO = new CardholderDAO(typeDBw);
        return cardholderDAO.retrieveAll();
    }

    /*
    {
        matchCardholder {
          date_of_birth
          gender
          job
          person_first_name
          person_last_name
          cardholderCoordinates {
            latitude
            longitude
          }
          creditCard {
            card_number
            bank {
              bank_name
              bankCoordinates {
                latitude
                longitude
              }
            }
          }
        }
       }
     */

    @Query
    @Description("test read")
    public Set<Transaction> getMatchTransaction() throws IOException {
        TypeDB_SessionWrapper typeDBw = new TypeDB_SessionWrapper();
        TransactionDAO transactionDAO = new TransactionDAO(typeDBw);
        return transactionDAO.retrieveAll();
    }

    /*
    {
  matchTransaction {
    amount
    date_transaction
    transaction_number
    merchant {
      company_cat
      company_name
      merchantCoordinates {
        latitude
        longitude
      }
    }
		cardholder {
		  date_of_birth
		  gender
		  job
		  person_first_name
		  person_last_name
      cardholderCoordinates {
        latitude
        longitude
      }
      creditCard {
        card_number
        bank {
          bank_name
          bankCoordinates {
            latitude
            longitude
          }
        }
      }
		}
  }
}
     */
}