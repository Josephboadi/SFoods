package com.example.sesafoods;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.sesafoods.Objects.Tray;

import java.util.List;

@Dao
public interface TrayDAO {
    @Query("SELECT * FROM tray")
    List<Tray> getAll();

    @Insert
    void insertAll(Tray... trays);

    @Query("DELETE FROM tray")
    void deleteAll();

    @Query("SELECT * FROM tray WHERE meal_id= :mealID")
    Tray getTray(String mealID);

    @Query("UPDATE tray SET meal_quantity = meal_quantity + :mealQty WHERE id = :trayId")
    void updateTray(int trayId, int mealQty);
//    @Delete
//    void delete(User user);
}
