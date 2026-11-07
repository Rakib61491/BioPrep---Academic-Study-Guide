package com.example.bioprep.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    UserAccountEntity getUserByEmail(String email);

    @Query("SELECT * FROM user_accounts WHERE id = :id LIMIT 1")
    UserAccountEntity getUserById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(UserAccountEntity user);

    @Update
    void updateUser(UserAccountEntity user);

    @Query("DELETE FROM user_accounts")
    void clearAllUsers();
}

