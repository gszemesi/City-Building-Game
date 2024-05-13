package SQL;

import java.sql.ResultSet;
import java.sql.SQLException;

public record City(int coordinate_x, int coordinate_y, int save_id, int flag, int level) {
    public City(ResultSet resultSet) throws SQLException {
        this(
                resultSet.getInt(1),
                resultSet.getInt(2),
                resultSet.getInt(3),
                resultSet.getInt(4),
                resultSet.getInt(5)
        );
    }
}
