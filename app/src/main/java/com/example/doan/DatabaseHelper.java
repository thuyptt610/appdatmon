package com.example.doan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DatabaseHelper extends SQLiteOpenHelper { // file Thuy

    private static final String DATABASE_NAME = "doannhom06.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Orders");
        db.execSQL("DROP TABLE IF EXISTS Dishes");
        db.execSQL("DROP TABLE IF EXISTS Users");
        onCreate(db);
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Users (" +
                "customerid INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nameC NVARCHAR(255) NOT NULL," +
                "email NVARCHAR(255) NOT NULL UNIQUE," +
                "phone TEXT," +
                "username NVARCHAR(255)," +
                "password VARCHAR(55)," +
                "img_User NVARCHAR(255)," +
                "is_admin INTEGER NOT NULL CHECK (is_admin IN (0, 1))" +
                ");");

        db.execSQL("CREATE TABLE Dishes (" +
                "dishid NVARCHAR(10) PRIMARY KEY," +
                "nameD NVARCHAR(155) NOT NULL," +
                "description NVARCHAR(255)," +
                "price INTEGER," +
                "quantityD INTEGER NOT NULL DEFAULT 0," +
                "imageD NVARCHAR(255)" +
                ");");

        db.execSQL("CREATE TABLE Orders (" +
                "orderid INTEGER PRIMARY KEY AUTOINCREMENT," +
                "customerid INTEGER NOT NULL," +
                "dishid NVARCHAR(10) NOT NULL," +
                "quantity INTEGER NOT NULL," +
                "orderdate DATETIME," +
                "total_price INTEGER," +
                "FOREIGN KEY (customerid) REFERENCES Users(customerid)," +
                "FOREIGN KEY (dishid) REFERENCES Dishes(dishid)" +
                ");");
    }
    public boolean insertData(String nameC, String email, String phone, String username, String password, String img_User, int is_admin) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nameC", nameC);
        contentValues.put("email", email);
        contentValues.put("phone", phone);
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("img_User", img_User);
        contentValues.put("is_admin", is_admin);
        long result = MyDB.insert("Users", null, contentValues);
        return result != -1;
    }
// cập số lượng
public boolean decrementDishQuantity(String dishId, int quantityChange) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.execSQL("UPDATE Dishes SET quantityD = quantityD - ? WHERE dishid = ?", new Object[]{quantityChange, dishId});
    return true;
}
    public Boolean checkusername(String username) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from Users where username = ?", new String[]{username});
        return cursor.getCount() > 0;
    }

    public Boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from Users where email = ? and password = ?", new String[]{email, password});
        return cursor.getCount() > 0;
    }

    public int checkIsAdminByEmail(String email, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select is_admin from Users where email = ? and password = ?", new String[]{email, password});
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        } else {
            return -1;
        }
    }

    public boolean updatePassword(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", password);
        int rowsUpdated = db.update("Users", values, "email = ?", new String[]{email});
        return rowsUpdated > 0;
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT username, password FROM Users WHERE email = ?", new String[]{email});
    }


/////////////////// Thêm vào bảng Order//////////////////////
  /*  public boolean insertOrder(String orderId, int customerId, String dishId, int quantity, long timestamp, long totalPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("customerid", customerId);
        contentValues.put("dishid", dishId);
        contentValues.put("quantity", quantity);
        contentValues.put("orderdate", new Date(timestamp).toString());
        contentValues.put("total_price", totalPrice);

        try {
            long result = db.insert("Orders", null, contentValues);
            return result != -1;
        } catch (SQLiteConstraintException e) {
            Log.e("DatabaseError", "Error inserting data into Orders table: " + e.getMessage());
            return false;
        }
    }*/
public boolean insertOrder(String orderId, int customerId, String dishId, int quantity, long timestamp, long totalPrice) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();

    Date orderDate = new Date(timestamp);

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    String formattedDate = dateFormat.format(orderDate);

    contentValues.put("customerid", customerId);
    contentValues.put("dishid", dishId);
    contentValues.put("quantity", quantity);
    contentValues.put("orderdate", formattedDate);
    contentValues.put("total_price", totalPrice);

    try {
        long result = db.insert("Orders", null, contentValues);
        return result != -1;
    } catch (SQLiteConstraintException e) {
        Log.e("DatabaseError", "Error inserting data into Orders table: " + e.getMessage());
        return false;
    }
}

    /* public int getCustomerIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT customerid FROM Users WHERE username = ?", new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            int customerId = cursor.getInt(cursor.getColumnIndexOrThrow("customerid"));
            cursor.close();
            return customerId;
        }
        return -1;
    }*/
    // dăng nhập
    public int getCustomerIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT customerid FROM Users WHERE email = ?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndexOrThrow("customerid"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return -1;
    }
    public String getImageResNameByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT img_User FROM Users WHERE email = ?", new String[]{email});
        if (cursor != null && cursor.moveToFirst()) {
            String imageName = cursor.getString(cursor.getColumnIndexOrThrow("img_User"));
            cursor.close();
            return imageName;
        }
        return null;
    }

    public Cursor getOrdersByCustomerId(int customerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Orders WHERE customerid = ?", new String[]{String.valueOf(customerId)});
    }

    public String getNameCByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Users",
                new String[]{"nameC"},
                "email = ?",
                new String[]{email},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String nameC = cursor.getString(cursor.getColumnIndexOrThrow("nameC"));
            cursor.close();
            Log.d("DatabaseHelper", "getNameCByEmail: " + nameC); // Thêm log
            return nameC;
        }
        return null;
    }
    public String getPhoneByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Users",
                new String[]{"phone"},
                "email = ?",
                new String[]{email},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            cursor.close();
            return phone;
        }
        return null;
    }

    public String getEmailByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Users",
                new String[]{"email"},
                "username = ?",
                new String[]{username},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            cursor.close();
            return email;
        }
        return null;
    }

    public byte[] getUserImageByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT img_User FROM Users WHERE username = ?", new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            byte[] imgBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("img_User"));
            cursor.close();
            return imgBytes;
        }
        return null;
    }
    //////////////////////////////////////////////


    //////////////////////////////////////////////



    public Cursor getDishById(String dishId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Dishes WHERE dishid = ?", new String[]{dishId});
    }




    public void insertDish(String dishId, String name, String description, int price, int quantity, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dishid", dishId);
        values.put("nameD", name);
        values.put("description", description);
        values.put("price", price);
        values.put("quantityD", quantity);
        values.put("imageD", image);
        db.insert("Dishes", null, values);
    }

    public Cursor getAllDishes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Dishes", null);
    }

    public Cursor getDishByName(String dishName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Dishes WHERE nameD = ?", new String[]{dishName});
    }

    public boolean updateDish(String dishId, String name, String description, int price, int quantity, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nameD", name);
        values.put("description", description);
        values.put("price", price);
        values.put("quantityD", quantity);
        values.put("imageD", image);
        int rowsUpdated = db.update("Dishes", values, "dishid = ?", new String[]{dishId});
        return rowsUpdated > 0;
    }

    public boolean deleteDish(String dishId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("Dishes", "dishid = ?", new String[]{dishId});
        return rowsDeleted > 0;
    }

    public Cursor getOrderDetailsByOrderId(String orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Orders WHERE orderid = ?", new String[]{orderId});
    }

    public Cursor getOrdersByCustomerIdAndDateRange(int customerId, long startDate, long endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Orders WHERE customerid = ? AND orderdate BETWEEN ? AND ?",
                new String[]{String.valueOf(customerId), String.valueOf(startDate), String.valueOf(endDate)});
    }

    public Cursor getOrdersByDishId(String dishId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Orders WHERE dishid = ?", new String[]{dishId});
    }

    public Cursor getOrderSummaryByCustomerId(int customerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT dishid, SUM(quantity) as total_quantity, SUM(total_price) as total_spent " +
                "FROM Orders WHERE customerid = ? GROUP BY dishid", new String[]{String.valueOf(customerId)});
    }

    public int getDishQuantity(String dishId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT quantityD FROM Dishes WHERE dishid = ?", new String[]{dishId});
        if (cursor != null && cursor.moveToFirst()) {
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantityD"));
            cursor.close();
            return quantity;
        }
        return -1;
    }

    public boolean updateDishQuantity(String dishId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantityD", newQuantity);
        int rowsUpdated = db.update("Dishes", values, "dishid = ?", new String[]{dishId});
        return rowsUpdated > 0;
    }

    public boolean reduceDishQuantity(String dishId, int quantityToReduce) {
        int currentQuantity = getDishQuantity(dishId);
        if (currentQuantity >= quantityToReduce) {
            return updateDishQuantity(dishId, currentQuantity - quantityToReduce);
        }
        return false;
    }
    /////////////////user_info
    public User getUserById(int customerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE customerId = ?", new String[]{String.valueOf(customerId)});

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String nameC = cursor.getString(1);
            String email = cursor.getString(2);
            String phone = cursor.getString(3);
            String username = cursor.getString(4);
            String password = cursor.getString(5);
            String imgUser = cursor.getString(6);
            int isAdmin = cursor.getInt(7);

            User user = new User(id, nameC, email, phone, username, password, imgUser, isAdmin);
            cursor.close();
            db.close();
            return user;
        } else {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            return null;
        }
    }
    //////////////////   chat box
    public User getUserByEmailChatBox(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE email = ?", new String[]{email});
        if (cursor != null && cursor.moveToFirst()) {
            int customerIdIndex = cursor.getColumnIndex("customerid");
            int nameCIndex = cursor.getColumnIndex("nameC");
            int phoneIndex = cursor.getColumnIndex("phone");
            int usernameIndex = cursor.getColumnIndex("username");
            int passwordIndex = cursor.getColumnIndex("password");
            int imgUserIndex = cursor.getColumnIndex("img_User");
            int isAdminIndex = cursor.getColumnIndex("is_admin");

            if (customerIdIndex != -1 && nameCIndex != -1 && phoneIndex != -1 &&
                    usernameIndex != -1 && passwordIndex != -1 && imgUserIndex != -1 && isAdminIndex != -1) {

                int customerId = cursor.getInt(customerIdIndex);
                String nameC = cursor.getString(nameCIndex);
                String phone = cursor.getString(phoneIndex);
                String username = cursor.getString(usernameIndex);
                String password = cursor.getString(passwordIndex);
                String imgUser = cursor.getString(imgUserIndex);
                int isAdmin = cursor.getInt(isAdminIndex);

                cursor.close();

                return new User(customerId, nameC, email, phone, username, password, imgUser, isAdmin);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }


    public boolean updateToken(int customerId, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("token", token);  // Thêm trường token mới

        int rowsUpdated = db.update("Users", values, "customerid = ?", new String[]{String.valueOf(customerId)});
        return rowsUpdated > 0;
    }

    /////////////////////////////////////////
}
