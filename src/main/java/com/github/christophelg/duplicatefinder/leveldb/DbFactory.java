package com.github.christophelg.duplicatefinder.leveldb;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Logger;
import org.iq80.leveldb.Options;

public class DbFactory {

  public static DB getConnection(File dbLocation, boolean readWrite) throws IOException {
    Options options = new Options();
    options.createIfMissing(readWrite);
    options.cacheSize(10 * 1048576); // 10MB cache

    Logger logger = new Logger() {
      public void log(String message) {
        System.out.println(message);
      }
    };

    options.logger(logger);
    return factory.open(dbLocation, options);
  }
}
