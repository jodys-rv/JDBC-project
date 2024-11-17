package projects.exception;
@SuppressWarnings("serial")
public class DbException extends RuntimeException {
  /**
   * converts checked to unchecked exception
   */
  public DbException() {
  }
  
  /**
   * converts checked to unchecked exception
   */
  public DbException(String message) {
    super(message);
  }
  
  /**
   * converts checked to unchecked exception
   */
  public DbException(Throwable cause) {
    super(cause);
  }
  
  /**
   * converts checked to unchecked exception
   */
  public DbException(String message, Throwable cause) {
    super(message, cause);
  }
}