package SQL;

public class SQLScript {
    //language=SQL
    public static final String createSQL = """
            CREATE TABLE IF NOT EXISTS save
            (
                id         INTEGER PRIMARY KEY AUTOINCREMENT,
                save_name  VARCHAR(20)        NOT NULL,
                money      INTEGER            NOT NULL,
                year       INTEGER            NOT NULL,
                month      INTEGER            NOT NULL,
                week       INTEGER            NOT NULL,
                created_at TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP
            );
            
            CREATE TABLE IF NOT EXISTS city
            (
                coordinate_x  INTEGER,
                coordinate_y  INTEGER,
                save_id       INTEGER NOT NULL REFERENCES save (id) ON DELETE CASCADE,
                flag          INTEGER NOT NULL,
                level         INTEGER NOT NULL,
                
                PRIMARY KEY (coordinate_x, coordinate_y, save_id)
            );
                
            CREATE TABLE IF NOT EXISTS citizen
            (
                id            INTEGER PRIMARY KEY AUTOINCREMENT,
                age           INTEGER NOT NULL CHECK ( age >= 18 ),
                happiness     INTEGER NOT NULL CHECK ( happiness BETWEEN 0 AND 100),
                save_id       INTEGER NOT NULL REFERENCES save (id) ON DELETE CASCADE,
                civil_coordinate_x INTEGER, civil_coordinate_y INTEGER,
                work_coordinate_x INTEGER, work_coordinate_y INTEGER,
                
                FOREIGN KEY (civil_coordinate_x, civil_coordinate_y, save_id) REFERENCES city (coordinate_x, coordinate_y, save_id),
                FOREIGN KEY (work_coordinate_x, work_coordinate_y, save_id) REFERENCES city (coordinate_x, coordinate_y, save_id)
            );
            """;

    //language=SQL
    public static final String dropSQL = """ 
            DROP TABLE IF EXISTS save;
            DROP TABLE IF EXISTS city;
            DROP TABLE IF EXISTS citizen;
            """;
}
