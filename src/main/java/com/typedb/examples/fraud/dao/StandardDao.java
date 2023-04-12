package com.typedb.examples.fraud.dao;

import java.util.Set;

public interface StandardDao<T> {

  Set<T> getAll();

  void insertAll(Set<T> banks);
}
