package ontologyInterface;

import java.util.EnumSet;

public enum SemRelation {
  SYNONYM, ANTONYM, HYPERNYM, HOLONYM, HYPONYM, MERONYM;
  public static final EnumSet<SemRelation> allSemanticRelations =
      EnumSet.range(
          SYNONYM, MERONYM);
  public static final EnumSet<SemRelation> upwardRelations =
      EnumSet.of(
          HYPERNYM, HOLONYM);
  public static final EnumSet<SemRelation> downwardRelations =
      EnumSet.of(
          HYPONYM, MERONYM);
  public static final EnumSet<SemRelation> horizontalRelations =
      EnumSet.of(
          SYNONYM, ANTONYM);
  public static final EnumSet<SemRelation> synonymRelation =
      EnumSet.of(SYNONYM);

  public SemRelation oppositeRel() {
    switch (this) {
    case SYNONYM:
      return SYNONYM;
    case ANTONYM:
      return ANTONYM;
    case HYPERNYM:
      return HYPONYM;
    case HYPONYM:
      return HYPERNYM;
    case HOLONYM:
      return MERONYM;
    case MERONYM:
      return HOLONYM;
    }
    return null;
  }
}
