package com.kplritesh.treeleaf.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kplritesh.treeleaf.room.NewUserProfileEntity;

import java.util.List;

@Dao
public interface UserProfileDao {

    @Insert
    void insert(NewUserProfileEntity config);

    @Update
    void update(NewUserProfileEntity config);

    @Delete
    void delete(NewUserProfileEntity config);

    @Query("SELECT * FROM NewUserProfileEntity")
    List<NewUserProfileEntity> getAllUserProfiles();

    @Query("SELECT * FROM NewUserProfileEntity WHERE id = :userId")
    NewUserProfileEntity getSelUserProfiles(int userId);
}
