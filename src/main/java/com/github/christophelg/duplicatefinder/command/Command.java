package com.github.christophelg.duplicatefinder.command;

import java.io.IOException;

public interface Command {

  public void validate();

  public void run() throws IOException;

}
