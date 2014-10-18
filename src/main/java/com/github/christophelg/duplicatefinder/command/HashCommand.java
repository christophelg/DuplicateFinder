package com.github.christophelg.duplicatefinder.command;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.ReadOptions;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.christophelg.duplicatefinder.leveldb.DbFactory;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

@Parameters(separators = "=", commandDescription = "Hash all the files of a directory")
public class HashCommand implements Command {

  @Parameter(description = "The list of directories to hash", required = true)
  private List<String> directoryNames;

  private List<File> directories;

  @Parameter(names = {"--database"}, description = "Where to find the database", required = true)
  private String database;

  private File databaseLocation;

  public void validate() {
    directories = Lists.newArrayListWithCapacity(directoryNames.size());
    for (String file : directoryNames) {
      File directory = new File(file);
      directories.add(directory);
      if (!directory.isDirectory()) {
        throw new IllegalArgumentException("Excepted only directories:" + file + " is not one");
      }
    }

    databaseLocation = new File(database);
    if (databaseLocation.exists() && !databaseLocation.isDirectory()) {
      throw new IllegalArgumentException("Database location must be a directory");
    }
  }

  public void run() throws IOException {
    DB db = DbFactory.getConnection(databaseLocation, true);
    db.resumeCompactions();
    PerimeterFilter pf = new PerimeterFilter();
    SimpleFileVisitor<Path> hasherVisitor = new Hasher(db, pf);

    for (File directory : directories) {
      System.out.println("Now processing directory:" + directory.toPath());
      // Add new entries
      Files.walkFileTree(directory.toPath(), hasherVisitor);
      // Remove stalled entries
      removeStalledEntries(db, pf, directory);
    }
    db.close();
  }

  private void removeStalledEntries(DB hashDb, PerimeterFilter pf, File dir) throws IOException {
    // Search on the disk for stalled entry in the db
    System.out.println("Now removing stalled entries from:" + dir);
    ReadOptions ro = new ReadOptions();
    ro.snapshot(hashDb.getSnapshot());

    DBIterator iterator = hashDb.iterator(ro);
    iterator.seekToFirst();
    String currentDirname = dir.toString();
    iterator.seek(currentDirname.getBytes());
    while (iterator.hasNext()) {
      Map.Entry<byte[], byte[]> entry = iterator.next();
      String filename = new String(entry.getKey());
      if (filename.startsWith(currentDirname)) {
        File file = new File(filename);
        if (!pf.accept(file)) {
          System.out.println("Found a stalled entry:" + filename);
          hashDb.delete(entry.getKey());
        }
      } else {
        break;
      }
    }
    iterator.close();
  }

  private static class Hasher extends SimpleFileVisitor<Path> {
    private DB hashDb;
    private PerimeterFilter filter;

    public Hasher(DB db, PerimeterFilter pf) {
      hashDb = db;
      filter = pf;
    }

    public FileVisitResult visitFile(Path path, BasicFileAttributes paramBasicFileAttributes)
        throws IOException {
      if (filter.accept(path.toFile())) {
        byte[] fileKey = path.toString().getBytes();
        byte[] fileHash = hashDb.get(fileKey);
        if (fileHash == null) {
          System.out.println("we need to hash:" + path.toString());
          HashCode hash = com.google.common.io.Files.hash(path.toFile(), Hashing.md5());
          // Pass by 'toString' to get a human representation
          hashDb.put(fileKey, hash.toString().getBytes());
        }
      } else {
        System.out.println("Excluding:" + path);
      }
      return FileVisitResult.CONTINUE;
    }
  }

  /**
   * This class implements the filter that keeps the files that we want to hash. <br/>
   * We will remove:
   * <ol>
   * <li>not existing !</li>
   * <li>empty files</li>
   * <li>links</li>
   * </ol>
   */
  private static class NotEndWith implements Predicate<String> {
    private String suffix;

    public NotEndWith(String s) {
      suffix = s;
    }

    @Override
    public boolean apply(String input) {
      return !input.endsWith(suffix);
    }

  }

  private static class PerimeterFilter {
    @SuppressWarnings("unchecked")
    private static final Predicate<String> forbiddenSuffice = Predicates.and(new NotEndWith("lnk"),
        new NotEndWith("IBO"), new NotEndWith("IFO"), new NotEndWith("txt"), new NotEndWith("ini"));

    public boolean accept(File file) {
      return file.exists() && file.length() > 0 && forbiddenSuffice.apply(file.getName());
    }
  }
}
