package com.example.sesafoods.Objects;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Tray {

    @PrimaryKey (autoGenerate = true)
    public int id;

    @ColumnInfo(name = "meal_id")
    public String mealId;

    @ColumnInfo(name = "meal_name")
    public String mealName;

    @ColumnInfo(name = "meal_price")
    public float mealPrice;

    @ColumnInfo(name = "meal_quantity")
    public int mealQuantity;

    @ColumnInfo(name = "restaurant_id")
    public String restaurantId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public float getMealPrice() {
        return mealPrice;
    }

    public void setMealPrice(float mealPrice) {
        this.mealPrice = mealPrice;
    }

    public int getMealQuantity() {
        return mealQuantity;
    }

    public void setMealQuantity(int mealQuantity) {
        this.mealQuantity = mealQuantity;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

}
