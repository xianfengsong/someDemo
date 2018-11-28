package tools;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * author Xianfeng <br/>
 * date 18-9-27 下午4:51 <br/>
 * Desc: 对update_time列做单独处理，每次都赋值为当前时间
 * (使用时需要把这个类放到项目中，有些麻烦，没有启用)
 */
public class UpdateTimeTypeHandler implements TypeHandler<Date> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setDate(i, new java.sql.Date(System.currentTimeMillis()));
    }

    @Override
    public Date getResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getDate(columnName);
    }

    @Override
    public Date getResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getDate(columnIndex);
    }

    @Override
    public Date getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getDate(columnIndex);
    }
}
