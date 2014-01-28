package com.github.christophelg.duplicatefinder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.github.christophelg.duplicatefinder.command.CleanupCommand;
import com.github.christophelg.duplicatefinder.command.Command;
import com.github.christophelg.duplicatefinder.command.DisplayCommand;
import com.github.christophelg.duplicatefinder.command.DuplicateCommand;
import com.github.christophelg.duplicatefinder.command.HashCommand;
import com.github.christophelg.duplicatefinder.command.LoadCommand;

public class DuplicateFinder {

  public final static Map<String, Command> commands = new HashMap<String, Command>() {

    private static final long serialVersionUID = -8308572818633747409L;

    {
      put("hash", new HashCommand());
      put("duplicate", new DuplicateCommand());
      put("load", new LoadCommand());
      put("cleanup", new CleanupCommand());
      put("display", new DisplayCommand());
    }
  };

  public static void main(String[] args) throws IOException {
    DuplicateFinder sdf = new DuplicateFinder();
    JCommander jc = new JCommander(sdf);
    jc.setProgramName(DuplicateFinder.class.getCanonicalName());

    // loop over the commands and register them
    for (Map.Entry<String, Command> entry : commands.entrySet()) {
      jc.addCommand(entry.getKey(), entry.getValue());
    }

    if (args.length == 0) {
      jc.usage();
      return;
    }

    try {
      jc.parse(args);
    } catch (ParameterException pe) {
      pe.printStackTrace();
      jc.usage();
      return;
    }

    // find out the given command and run it
    Command command2Run = commands.get(jc.getParsedCommand());
    if (command2Run == null) {
      System.err.println("WTF");
    } else {
      command2Run.validate();
      command2Run.run();
    }
  }
}
