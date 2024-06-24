package com.example.doan;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Order#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Order extends Fragment {

    private ArrayList<Order> orders;
    private OrderAdapter adapter;
    private String loggedInUsername;

    public static Fragment_Order newInstance(String username) {
        Fragment_Order fragment = new Fragment_Order();
        Bundle args = new Bundle();
        args.putString("username", username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loggedInUsername = getArguments().getString("username");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__order, container, false);

        ListView listView = view.findViewById(R.id.lvOrder);
        orders = new ArrayList<>();
        adapter = new OrderAdapter(getActivity(), orders);
        listView.setAdapter(adapter);

        registerForContextMenu(listView);  // Register for context menu

        loadOrders();

        return view;
    }

    private void loadOrders() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM Orders", null);
        if (cursor != null) {
            orders.clear();
            if (cursor.moveToFirst()) {
                do {
                    String orderId = cursor.getString(cursor.getColumnIndexOrThrow("orderid"));
                    int customerID = cursor.getInt(cursor.getColumnIndexOrThrow("customerid"));
                    String dishID = cursor.getString(cursor.getColumnIndexOrThrow("dishid"));
                    int price = cursor.getInt(cursor.getColumnIndexOrThrow("total_price"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("orderdate"));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

                    Order order = new Order(orderId, customerID, dishID, quantity, date, price);
                    orders.add(order);
                } while (cursor.moveToNext());
            }
            cursor.close();
            adapter.notifyDataSetChanged();
        }
    }
////////////////////////// phần xóa
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.lvOrder) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
      int delete=item.getItemId();
      if(delete==R.id.delete){
          deleteOrder(info.position);
      }
      return true;
    }

    private void deleteOrder(int position) {
        Order order = orders.get(position);

        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.delete("Orders", "orderid = ?", new String[]{order.getOrderid()});
        database.close();

        orders.remove(position);
        adapter.notifyDataSetChanged();
    }
}
