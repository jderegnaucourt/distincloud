package ontologyInterface;

public class OntologyException extends Exception {
  private static final long serialVersionUID =
      6578359432138181706L;

  public enum ExceptionKind {
    MAXRELATEDNESSTOOHIGH, WORDNOTFOUND, SENSENOTFOUND,
    WORD1NOTFOUND, WORD2NOTFOUND, WORDALREADYPRESENT,
    DATANOTLOADED, EXPIREDVERSIONEXCEPTION;
  }

  protected ExceptionKind exceptionKind;

  public OntologyException(ExceptionKind exceptionKind) {
    super();
    this.exceptionKind = exceptionKind;
  }

  public ExceptionKind getExceptionKind() {
    return exceptionKind;
  }
}
