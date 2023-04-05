package org.example;

import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import com.vaticle.typedb.client.api.TypeDBOptions;
import com.vaticle.typedb.client.api.TypeDBSession;
import com.vaticle.typedb.client.api.TypeDBTransaction;
import com.vaticle.typedb.client.api.answer.ConceptMap;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class TypeDB_SessionWrapper {

  private final String ip_server;
  private final String port_server;
  private final String database_name;

  public TypeDB_SessionWrapper(String ip_server, String port_server, String database_name)
      throws FileNotFoundException {
    this.ip_server = ip_server;
    this.port_server = port_server;
    this.database_name = database_name;
  }

  public TypeDB_SessionWrapper() throws FileNotFoundException {
    this.ip_server = "127.0.0.1";
    this.port_server = "1729";
    this.database_name = "test_FRAUD";
  }

  public void load_data(Set<String> lInsert) throws IOException {
    TypeDBClient client = TypeDB.coreClient(ip_server + ":" + port_server);
    TypeDBSession session = client.session(database_name, TypeDBSession.Type.DATA);
    try (TypeDBTransaction writeTransaction = session.transaction(
        TypeDBTransaction.Type.WRITE)) { // WRITE transaction is open
      for (String currentInsert : lInsert) {
        writeTransaction.query().insert(currentInsert);
      }
      writeTransaction.commit(); // to persist changes, a write transaction must always be committed
      System.out.println("Data Loaded");
    }
    client.close();
  }

  public String getDatabase_name() {
    return database_name;
  }


  public Set<List<String>> read_data(String query, List<String> lArg) throws IOException {
    TypeDBClient client = TypeDB.coreClient(ip_server + ":" + port_server);
    Set<List<String>> lBank = new HashSet<List<String>>();
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    TypeDBSession session = client.session(database_name, TypeDBSession.Type.DATA);
    TypeDBTransaction readTransaction = session.transaction(TypeDBTransaction.Type.READ,
        TypeDBOptions.core().infer(true));
    if (readTransaction.isOpen()) {

      Stream<ConceptMap> queryAnswers = readTransaction.query().match(query);

      queryAnswers.forEach(queryAnswer -> {
        List<String> currentList = new ArrayList<String>();
        for (String arg : lArg) {
          currentList.add(queryAnswer.get(arg).asAttribute().getValue().toString());
        }
        lBank.add(currentList);
      });

      System.out.println("Read DONE");
    }
    return lBank;
  }


}
