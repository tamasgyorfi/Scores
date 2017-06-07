package hu.bets.points.dbaccess;

public class DatabaseException extends RuntimeException {

    public DatabaseException(Exception e) {
        super(e);
    }
}
