package ontologyInterface;

import java.io.IOException;

public interface Sense {
  String getDefinition();

  void activateDeactivate(boolean activate)
      throws IOException;

  boolean isActive();
}
