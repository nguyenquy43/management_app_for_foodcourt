package com.example.demo;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MonthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MonthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonthFragment newInstance(String param1, String param2) {
        MonthFragment fragment = new MonthFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_recycler_view, container, false);

        final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        
        readData(new DateObjectCallBack() {
            @Override
            public void onCallBack1(ArrayList<DateObject> value) {
                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Collections.sort(value, new Comparator<DateObject>() {
                        @Override
                        public int compare(DateObject t0, DateObject t1) {
                            int ret = 0;
                            try {
                                ret = sdf.parse(t0.getDate()).compareTo(sdf.parse(t1.getDate()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return ret;
                        }
                    });

                    Collections.reverse(value);

                    ObjectAdapter adapter = new ObjectAdapter(value);

                    recyclerView.setAdapter(adapter);
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        return view;
    }

    /** A callback method to receive data from Firebase Database
     * @param dateObjectCallBack the DateObjectCallBack interface reference
     */
    private void readData(final DateObjectCallBack dateObjectCallBack) {

        final int month = Integer.parseInt(mParam1);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Vendors").child(UserInfo.instance.getUserName()).child("completed_orders");
        ref.orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    ArrayList<DateObject> tempObjects = new ArrayList<DateObject>();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        // Date date = (Date) data.child("dated").getValue().toString();
                        Order order = data.getValue(Order.class);
                        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date = sdf1.parse(order.getDate());

                            final String strDate = sdf2.format(date);

                            if ((month == Integer.parseInt(strDate.substring(5,7)))) {

                                boolean isExists = false;

                                for (DateObject object : tempObjects) {
                                    if (strDate.equals(object.getDate())) {
                                        isExists = true;
                                        object.setNumberOfOrder(object.getNumberOfOrder() + 1);
                                        for (OrderFood food : order.getFoods()) {
                                            object.setRevenue(object.getRevenue() + Integer.parseInt(food.getPrice()) * Integer.parseInt(food.getQuantity()));
                                        }
                                        break;
                                    }
                                }

                                if (!isExists) {

                                    DateObject object1 = new DateObject(strDate, 1, 0);

                                    for (OrderFood food : order.getFoods()) {
                                        object1.setRevenue(object1.getRevenue() + Integer.parseInt(food.getPrice()) * Integer.parseInt(food.getQuantity()));
                                    }

                                    tempObjects.add(object1);
                                }
                            }
                        }
                        catch (Exception e) {
                            Log.d("ERROR", e.getMessage());
                        }
                    }
                    dateObjectCallBack.onCallBack1(tempObjects);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}