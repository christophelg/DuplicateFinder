package clg.ft.duplicatefinder;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Logger;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.WriteOptions;

public class Tutorial {

  public static void main(String[] args) throws IOException {
    Options options = new Options();
    options.createIfMissing(true);
    options.cacheSize(100 * 1048576); // 100MB cache

    Logger logger = new Logger() {
      public void log(String message) {
        System.out.println(message);
      }
    };

    options.logger(logger);

    DB db = factory.open(new File("example"), options);
    try {
      createAndDelete(db);

      batchOperations(db);

      iterators(db);
    } finally {
      // Make sure you close the db to shutdown the
      // database and avoid resource leaks.
      db.close();
    }
  }

  public static void createAndDelete(DB db) {
    db.put(bytes("Tampa"), bytes("rocks"));
    String value = asString(db.get(bytes("Tampa")));
    System.out.println("GIGO:" + "rocks".equals(value));
    db.delete(bytes("Tampa"), new WriteOptions());
  }

  public static void batchOperations(DB db) throws IOException {
    WriteBatch batch = db.createWriteBatch();
    try {
      batch.delete(bytes("Denver")); // delete nothing
      batch.put(bytes("Tampa"), bytes("green"));
      batch.put(bytes("London"), bytes("red"));

      db.write(batch);
    } finally {
      batch.close();
    }
  }

  public static void iterators(DB db) throws IOException {
    DBIterator iterator = db.iterator();
    for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
      String key = asString(iterator.peekNext().getKey());
      String value = asString(iterator.peekNext().getValue());
      System.out.println(key + " = " + value);
    }
    iterator.close();
  }
}
