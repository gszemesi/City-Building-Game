package SQL;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author Varga Bence
 */
public record Save(int id, String save_name, int money, int year, int month, int week, Timestamp created_at) {
    public Save(ResultSet resultSet) throws SQLException {
        this(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getInt(3),
                resultSet.getInt(4),
                resultSet.getInt(5),
                resultSet.getInt(6),
                resultSet.getTimestamp(7));
    }
}
