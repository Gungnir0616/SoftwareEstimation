package com.example.back.Mapper;
import com.example.back.entity.User;
import org.apache.ibatis.annotations.*;
import com.example.back.Controller.UserRegisterRequest;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE userName = #{userName}")
    User findByUserName(String userName);

    @Insert("INSERT INTO user (userName, password, phone) " +
            "VALUES (#{userName}, #{password}, #{phone})")
    void insertUser(User user);

    @Update("UPDATE user SET userName = #{userName}, password = #{password}, phone = #{phone}, mailbox = #{mailbox}, gender = #{gender}, role = #{role}, department = #{department} WHERE userName = #{userName}")
    void updateUser(User user);

    @Delete("DELETE FROM user")
    void deleteAllUsers();

}