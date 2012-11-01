package ontologyInterface;

import java.io.IOException;
import java.io.Serializable;

public interface ExternalWord extends Word {
  int addSense(boolean active, String definition)
      throws IOException;

  ExternalSense getSense(int senseIndex);

  boolean removeSense(int senseIndex) throws IOException;

  void setUserData(Serializable data) throws IOException;

  Serializable getUserData();
}
