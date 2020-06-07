package com.throwsnew.springbootstudy.accessdata.mysql.mapper;

import com.throwsnew.springbootstudy.accessdata.mysql.model.User;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;


/**
 * author Xianfeng <br/>
 * date 19-7-31 下午3:31 <br/>
 * Desc:
 */
public interface UserMapper {

    @Select("select * from users")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "age", column = "age"),
            @Result(property = "name", column = "name")})
    List<User> listUser();

    @Insert("insert into users(age,name) values (#{age},#{name})")
    void insert(User user);

    @Delete("DELETE FROM users")
    void delete();


}
