package com.kplritesh.treeleaf;

import android.app.Application;

import androidx.room.Room;

import com.kplritesh.treeleaf.room.UserProfileDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {
private UserProfileDatabase userProfileDatabase;
private ExecutorService databaseWriteExecutor;
    @Override
    public void onCreate() {
        super.onCreate();

        userProfileDatabase = Room.databaseBuilder(this, UserProfileDatabase.class, "user-profile-database")
                .fallbackToDestructiveMigration()
                .build();

        databaseWriteExecutor = Executors.newFixedThreadPool(4);
    }

    public UserProfileDatabase getUserProfileDatabase(){
        return userProfileDatabase;
    }

    public ExecutorService getDatabaseWriteExecutor(){
        return databaseWriteExecutor;
    }
}
