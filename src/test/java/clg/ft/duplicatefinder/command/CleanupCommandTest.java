package clg.ft.duplicatefinder.command;

import java.util.regex.Pattern;

public class CleanupCommandTest {

  public static void main(String[] args) {
    String regex = "J:.*";
    Pattern whatToMatch = Pattern.compile(regex);
    String data =
        "J:\\home.backup\\blonde\\series\\b\\barbie.griffen\\Barbie Griffen Black Lingerie 059.jpg";
    System.out.println(whatToMatch.matcher(data).find());
    System.out.println(data.startsWith(regex));
  }
}
