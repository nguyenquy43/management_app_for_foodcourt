package com.example.demo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.util.List;

public class ObjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    public class ViewHolder0 extends RecyclerView.ViewHolder {
        public TextView textViewNoDate;
        public TextView textViewMonth;
        public TextView textViewTotalOrder;
        public TextView textViewRevenue;
        public LinearLayout parentLinearLayout;
        public ViewHolder0(@NonNull View itemView) {
            super(itemView);

            textViewNoDate = (TextView) itemView.findViewById(R.id.textViewNoDate);
            textViewMonth = (TextView) itemView.findViewById(R.id.textViewMonth);
            textViewRevenue = (TextView) itemView.findViewById(R.id.textViewRevenue);
            textViewTotalOrder = (TextView) itemView.findViewById(R.id.textViewTotalOrder);
            parentLinearLayout = (LinearLayout) itemView.findViewById(R.id.parentLinearLayout);

        }
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {

        public TextView monthRevenue;
        public TextView monthOrder;
        public Button reportButton;

        public ViewHolder2(@NonNull View itemView) {
            super(itemView);

            monthRevenue = (TextView) itemView.findViewById(R.id.monthRevenueTextView);
            monthOrder = (TextView) itemView.findViewById(R.id.monthOrderTextView);
            reportButton = (Button) itemView.findViewById(R.id.reportButton);
        }
    }

    private List<DateObject> mObjects;
    public ObjectAdapter(List<DateObject> objects) {
        mObjects = objects;
    }

    @Override
    public int getItemViewType (int position) {
        if (mObjects.size() == 0) return R.layout.empty_month;
        else {
            if (position == 0) return R.layout.month_header;
            else return R.layout.recycler_item;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case R.layout.empty_month:
                viewHolder = new ViewHolder1(inflater.inflate(R.layout.empty_month, parent, false));
                break;
            case R.layout.recycler_item:
                viewHolder = new ViewHolder0(inflater.inflate(R.layout.recycler_item, parent, false));
                break;
            case R.layout.month_header:
                viewHolder = new ViewHolder2(inflater.inflate(R.layout.month_header, parent, false));
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        final NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(true);

        switch (holder.getItemViewType()) {
            case R.layout.empty_month:
                ViewHolder1 viewHolder1 = (ViewHolder1)holder;
                break;
            case R.layout.month_header:
                ViewHolder2 viewHolder2 = (ViewHolder2)holder;
                int totalOrder = 0;
                long totalRevenue = 0;
                for (DateObject dateObject : mObjects) {
                    totalOrder += dateObject.getNumberOfOrder();
                    totalRevenue += dateObject.getRevenue();
                }
                TextView textView4 = viewHolder2.monthOrder;
                textView4.setText(String.valueOf(totalOrder));
                TextView textView5 = viewHolder2.monthRevenue;
                textView5.setText(numberFormat.format(totalRevenue));
                final String month = mObjects.get(position).getDate().substring(5,7);
                Button button = viewHolder2.reportButton;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserInfo.instance.setMonth(month);
                        context.startActivity(new Intent(context, CreateReport.class));
                    }
                });
                break;
            case R.layout.recycler_item:
                ViewHolder0 viewHolder0 = (ViewHolder0)holder;
                DateObject object = mObjects.get(position - 1);
                List<FoodReport> foodReports = UserInfo.instance.getFoodReports();
                FoodReport foodReport = null;

                for (FoodReport foodReport1 : foodReports) {
                    if (foodReport1.getDate().equals(object.getDate())) {
                        foodReport = foodReport1;
                    }
                }

                // Set item views based on your views and data model
                final TextView textView = viewHolder0.textViewNoDate;
                textView.setText(object.getDate().substring(8));
                TextView textView1 = viewHolder0.textViewMonth;
                textView1.setText(new DateFormatSymbols().getMonths()[Integer.parseInt(object.getDate().substring(5,7)) - 1]);
                TextView textView2 = viewHolder0.textViewRevenue;
                textView2.setText(numberFormat.format(object.getRevenue()));
                TextView textView3 = viewHolder0.textViewTotalOrder;
                textView3.setText("Total Orders: " + String.valueOf(object.getNumberOfOrder()));



                LinearLayout linearLayout = viewHolder0.parentLinearLayout;
                for (int j = 0; j < foodReport.getFoods().size(); j++) {

                    LinearLayout linearLayout1 = new LinearLayout(linearLayout.getContext());
                    linearLayout1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    linearLayout1.setOrientation(LinearLayout.HORIZONTAL);

                    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    params.setMargins(10, 15, 0, 15);
                    TextView textViewFoodName = new TextView(linearLayout1.getContext());
                    textViewFoodName.setLayoutParams(params);
                    textViewFoodName.setText(foodReport.getFoods().get(j).getName());
                    textViewFoodName.setTextColor(linearLayout1.getResources().getColor(R.color.blackText));
                    linearLayout1.addView(textViewFoodName);

                    TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    params1.setMargins(0, 15, 10, 15);
                    TextView textViewRevenue = new TextView(linearLayout1.getContext());
                    textViewRevenue.setLayoutParams(params1);
                    textViewRevenue.setTextColor(linearLayout1.getResources().getColor(R.color.blueText));
                    textViewRevenue.setText(numberFormat.format(Integer.parseInt(foodReport.getFoods().get(j).getPrice()) * foodReport.getFoods().get(j).getQuantity()));
                    linearLayout1.addView(textViewRevenue);

                    linearLayout.addView(linearLayout1);
                }

                break;
        }
    }

    @Override
    public int getItemCount() {
        return mObjects.size() + 1;
    }
}
