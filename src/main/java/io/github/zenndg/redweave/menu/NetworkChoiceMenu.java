package io.github.zenndg.redweave.menu;

import java.util.List;

/** A menu that shows the network choice, which takes the rows the server sends (answer 400). */
public interface NetworkChoiceMenu {
  void acceptChoices(List<NodeScreenChoices.Row> rows);
}
