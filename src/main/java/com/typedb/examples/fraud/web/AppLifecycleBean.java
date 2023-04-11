package com.typedb.examples.fraud.web;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.typedb.examples.fraud.dao.BankDAO;
import com.typedb.examples.fraud.dao.CardholderDAO;
import com.typedb.examples.fraud.dao.MerchantDAO;
import com.typedb.examples.fraud.dao.TransactionDAO;
import com.typedb.examples.fraud.model.Bank;
import com.typedb.examples.fraud.model.BankCoordinates;
import com.typedb.examples.fraud.model.Cardholder;
import com.typedb.examples.fraud.model.Merchant;
import com.typedb.examples.fraud.model.Transaction;
import com.vaticle.typedb.client.api.TypeDBClient;
import com.vaticle.typedb.client.api.TypeDBSession;
import com.vaticle.typedb.client.api.TypeDBTransaction;
import com.vaticle.typeql.lang.TypeQL;
import io.quarkus.runtime.StartupEvent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AppLifecycleBean {

  private static final Logger LOGGER = Logger.getLogger("AppLifecycleBean");

  @Inject
  TypeDBClient client;

  @Inject
  BankDAO bankDAO;
  @Inject
  CardholderDAO cardholerDAO;
  @Inject
  MerchantDAO merchantDAO;
  @Inject
  TransactionDAO transactionDAO;

  void onStart(@Observes StartupEvent ev) {

    LOGGER.info("Deleting database");

    client.databases().get("fraud").delete();

    LOGGER.info("Creating database");

    client.databases().create("fraud");

    LOGGER.info("Creating schema");

    try (TypeDBSession session = client.session("fraud", TypeDBSession.Type.SCHEMA)) {

      try (TypeDBTransaction tx = session.transaction(TypeDBTransaction.Type.WRITE)) {

        URL file = this.getClass().getClassLoader().getResource("schema.tql");

        assert file != null;

        String query = Files.readString(Paths.get(file.toURI()));

        tx.query().define(TypeQL.parseQuery(query).asDefine());

        tx.commit();
      }
      catch (Exception ex) {

        ex.printStackTrace(System.err);
      }
    }

    LOGGER.info("Preparing data");

    var testBanks = new HashSet<Bank>();
    var testCardholders = new HashSet<Cardholder>();
    var testMerchants = new HashSet<Merchant>();
    var testTransactions = new HashSet<Transaction>();

    testBanks.add(new Bank("ABC", new BankCoordinates("30.5", "-90.3")));
    testBanks.add(new Bank("MNO", new BankCoordinates("33.986391", "-81.200714")));
    testBanks.add(new Bank("QRS", new BankCoordinates("43.7", "-88.2")));
    testBanks.add(new Bank("XYZ", new BankCoordinates("40.98", "-90.4")));

    try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("data.csv")) {

      assert is != null;

      try (InputStreamReader isr = new InputStreamReader(is)) {

        CsvToBean<Transaction> csv = new CsvToBeanBuilder<Transaction>(isr).withType(Transaction.class).build();

        testTransactions.addAll(csv.parse());

        testTransactions.forEach(tx -> {

          Bank bank = testBanks.stream().skip((int) (testBanks.size() * Math.random())).findFirst().get();

          tx.getCardholder().getCc().setBank(bank);
        });

        testMerchants.addAll(testTransactions.stream().map(Transaction::getMerchant).collect(Collectors.toSet()));
        testCardholders.addAll(testTransactions.stream().map(Transaction::getCardholder).collect(Collectors.toSet()));
      }
    }
    catch (Exception ex) {
      LOGGER.error(ex);
    }

    LOGGER.info("Inserting data");

    bankDAO.insertAll(testBanks);
    cardholerDAO.insertAll(testCardholders);
    merchantDAO.insertAll(testMerchants);
    transactionDAO.insertAll(testTransactions);
  }
}
