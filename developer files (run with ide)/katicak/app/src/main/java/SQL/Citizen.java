package SQL;

import java.sql.ResultSet;
import java.sql.SQLException;

public record Citizen(int id, int age, int happiness, int save_id, int civil_coordinate_x, int civil_coordinate_y,
                      int work_coordinate_x, int work_coordinate_y) {
    public Citizen(ResultSet resultSet) throws SQLException {
        this(
                resultSet.getInt(1),
                resultSet.getInt(2),
                resultSet.getInt(3),
                resultSet.getInt(4),
                resultSet.getInt(5),
                resultSet.getInt(6),
                resultSet.getInt(7),
                resultSet.getInt(8)
        );
    }
}
