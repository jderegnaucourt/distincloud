package ontologyInterface;

import java.io.IOException;

public interface Word {
  int getSensesCount();

  Sense getSense(int senseIndex);

  void activateDeactivateSenses(boolean[] activate)
      throws IOException;
}
