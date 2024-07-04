package gift.repository;

import gift.DTO.ProductDTO;
import gift.DTO.UserDTO;
import gift.domain.User.CreateUser;
import gift.domain.User.UpdateUser;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UserDTO> getUserList() {
        String sql = "SELECT * FROM Users WHERE isDelete=1";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(UserDTO.class));
    }

    public UserDTO getUser(Long id) {
        String sql = "SELECT * FROM Users WHERE id = ? and isDelete=1";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(UserDTO.class), id);
    }

    public int createUser(CreateUser create) {
        String sql = "INSERT INTO Users (email,password,isDelete) VALUES (?, ?,1)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, create.getEmail());
            ps.setString(2, create.getPassword());
            return ps;
        }, keyHolder) > 0) {
            return keyHolder.getKey().intValue();
        } else {
            return -1;
        }
    }

    public int updatePassword(Long id, UpdateUser update) {
        String sql = "UPDATE Users SET password = ? WHERE id = ? and isDelete=1";
        if (jdbcTemplate.update(sql,update.getPassword(), id) == 1) {
            return id.intValue();
        }
        return -1;
    }

    public int deleteUser(Long id) {
        String sql = "UPDATE Users SET isDelete = ? WHERE id = ? and isDelete=1";
        if (jdbcTemplate.update(sql,0, id) == 1) {
            return id.intValue();
        }
        return -1;
    }

    public boolean validateId(Long id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM Users WHERE id = ? and isDelete=1)";
        if (jdbcTemplate.queryForObject(sql, new Object[]{id}, Integer.class) == 1) {
            return true;
        }
        return false;
    }


}
