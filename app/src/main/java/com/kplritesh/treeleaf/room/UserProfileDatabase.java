package com.kplritesh.treeleaf.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.kplritesh.treeleaf.interfaces.UserProfileDao;
@Database(entities = {NewUserProfileEntity.class}, version = 1)
public abstract class UserProfileDatabase extends RoomDatabase {
    public abstract UserProfileDao userProfileDao();
}
