package com.example.demo;

import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateReport extends AppCompatActivity {

    WebView webView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_report_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.shareButton:
                printPDF(webView);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        generateReport();

    }

    /**
     * Generate a friendly, readable report on user's screen. The report includes a table with
     * consumed foods info, some charts to visualize data, etc.
     * The report is created based on html-css file, which makes it esay for exporting later.
     */
    private void generateReport() {

        final NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(true);

            readData(new FoodInfoCallBack() {

                @Override
                public void onCallBack2(List<FoodInfo> value) throws Exception {

                    List<FoodReport> foodReports = UserInfo.instance.getFoodReports();
                    ArrayList<Long> revenue = new ArrayList<>();
                    ArrayList<Integer> order = new ArrayList<>();
                    for (int i = 0; i < 4; i++) {
                        revenue.add((long) 0);
                        order.add(0);
                    }

                    for (FoodReport foodReport : foodReports) {
                        if (Integer.parseInt(UserInfo.instance.getMonth()) == Integer.parseInt(foodReport.getDate().substring(5, 7))) {
                            int date = Integer.parseInt(foodReport.getDate().substring(8));
                            int i = 0;
                            if (date <= 7) {
                                i = 0;
                            } else if (date <= 15) {
                                i = 1;
                            } else if (date <= 23) {
                                i = 2;
                            } else {
                                i = 3;
                            }
                            order.set(i, order.get(i) + 1);
                            int rev = 0;
                            for (FoodInfo foodInfo : foodReport.getFoods()) {
                                rev += Integer.parseInt(foodInfo.getPrice()) * foodInfo.getQuantity();
                            }
                            revenue.set(i, revenue.get(i) + rev);
                        }
                    }
                    InputStream inputStream = getAssets().open("pdfdata.html");
                    String str = "";
                    StringBuffer buf = new StringBuffer();
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        if (inputStream != null) {
                            while ((str = reader.readLine()) != null) {
                                buf.append(str + "\n");
                            }
                        }
                    } finally {
                        try {
                            inputStream.close();
                        } catch (Throwable ignore) {}

                    }

                    buf.append("function drawQuantityChart() {\n");

                    buf.append("var data = new google.visualization.DataTable();\n" +
                            "        data.addColumn('string', 'Food');\n" +
                            "        data.addColumn('number', 'Quantity');\n" +
                            "        data.addRows([\n");
                    for (int i = 0; i < value.size(); i++) {
                            buf.append("['" + value.get(i).getName() + "', " + value.get(i).getQuantity() + "]");
                            if (i != value.size() - 1) {
                                buf.append(",\n");
                            }
                            else {
                                buf.append("\n");
                            }
                    }

                    buf.append("]);\n");

                    buf.append("var options = {'title':'Consumed food by quantity',\n" +
                            "                       'width':350,\n" +
                            "                       'height':300};\n" +
                            "\n" +
                            "        // Instantiate and draw our chart, passing in some options.\n" +
                            "        var chart = new google.visualization.PieChart(document.getElementById('quantity_chart_div'));\n" +
                            "        chart.draw(data, options);\n" +
                            "      }\n");


                    buf.append("function drawRevenueChart() {\n");

                    buf.append("var data = new google.visualization.DataTable();\n" +
                            "        data.addColumn('string', 'Food');\n" +
                            "        data.addColumn('number', 'Revenue');\n" +
                            "        data.addRows([\n");
                    for (int i = 0; i < value.size(); i++) {
                        buf.append("['" + value.get(i).getName() + "', " + Integer.parseInt(value.get(i).getPrice())*value.get(i).getQuantity() + "]");
                        if (i != value.size() - 1) {
                            buf.append(",\n");
                        }
                        else {
                            buf.append("\n");
                        }
                    }

                    buf.append("]);\n");

                    buf.append("var options = {'title':'Consumed food by revenue',\n" +
                            "                       'width':350,\n" +
                            "                       'height':300\n" +
                            "};\n" +
                            "\n" +
                            "        // Instantiate and draw our chart, passing in some options.\n" +
                            "        var chart = new google.visualization.PieChart(document.getElementById('revenue_chart_div'));\n" +
                            "        chart.draw(data, options);\n" +
                            "      }\n");

                    buf.append("function drawLineChart() {\n" +
                            "    var data = new google.visualization.DataTable();\n" +
                            "\tdata.addColumn('string', 'Date');\n" +
                            "\tdata.addColumn('number', 'Order');\n" +
                            "\tdata.addColumn('number', 'Revenue');\n" +
                            "\tdata.addRows([\n");
                    buf.append("['01 - 07', " + order.get(0) + "," + revenue.get(0) + "],\n");
                    buf.append("['08 - 15', " + order.get(1) + "," + revenue.get(1) + "],\n");
                    buf.append("['16 - 23', " + order.get(2) + "," + revenue.get(2) + "],\n");
                    buf.append("['24 - 31', " + order.get(3) + "," + revenue.get(3) + "]\n");

                    buf.append("    ]);\n" +
                            "    var options = {\n" +
                            "    title:'Orders and revenues by week',\n" +
                            "\tseries: {\n" +
                            "\t\t0: {targetAxisIndex: 0},\n" +
                            "\t\t1: {targetAxisIndex: 1}\n" +
                            "\t\t},\n" +
                            "\tvAxes: {\n" +
                            "\t\t\t0: {title: 'Number of Order'},\n" +
                            "\t\t\t1: {title: 'Revenue (VND)'}\n" +
                            "\t}\n" +
                            "    };\n" +
                            "\t\n" +
                            "\tvar chart = new google.visualization.LineChart(document.getElementById('barchart'));\n" +
                            "\tchart.draw(data, options);\n" +
                            "\t}");

                    buf.append("    </script>\n" +
                            "  </head>\n");

                    buf.append("<body>\n" +
                            "<h1 class=\"title\">Consume Food In Entire Month</h1>\n" +
                            "<p>This is an overview of all the consumed food in month.</p>\n" +
                            "<table>\n" +
                            "<tr class=\"movierow\"><th class=\"column1\">No.</th><th class=\"column2\">Name</th><th class=\"column3\">Price</th><th class=\"column4\">Quantity</th><th class=\"column5\">Revenue</th></tr>\n");



                    int no = 1;
                    int totalQuantity = 0;
                    long totalPrice = 0;
                    for (int i = 0; i < value.size(); i++) {
                        if (value.get(i).getQuantity() != 0) {
                            buf.append("<tr class=\"movierow\"><td class=\"column1\">");
                            buf.append(no);
                            buf.append("</td><td class=\"column2\">");
                            buf.append(value.get(i).getName());
                            buf.append("</td><td class=\"column3\">");
                            buf.append(numberFormat.format(Long.parseLong(value.get(i).getPrice())));
                            buf.append("</td><td class=\"column4\">");
                            buf.append(value.get(i).getQuantity());
                            totalQuantity += value.get(i).getQuantity();
                            buf.append("</td><td class=\"column5\">");
                            buf.append(numberFormat.format(value.get(i).getQuantity() * Integer.parseInt(value.get(i).getPrice())));
                            totalPrice += value.get(i).getQuantity() * Integer.parseInt(value.get(i).getPrice());
                            buf.append("</td></tr>" + "\n");
                            no++;
                        }
                    }

                    buf.append("<tr class=\"movierow\"><th colspan=\"3\">TOTAL</th><th class=\"column4\">");
                    buf.append(totalQuantity);
                    buf.append("</th><th class=\"column5\">");
                    buf.append(numberFormat.format(totalPrice));
                    buf.append("</th></tr>" + "\n");

                    buf.append("</table>\n");

                    buf.append("    <div id=\"quantity_chart_div\"></div>\n" +
                                    "    <div id=\"revenue_chart_div\"></div>\n" +
                            "<div id=\"barchart\", style=\"width: 300px; height: 200px;\"></div>\n" +
                            "  </body>\n" +
                            "</html>");

                    Log.d("HTML", buf.toString());

                    webView = (WebView) findViewById(R.id.webView);
                    webView.setInitialScale(1);
                    webView.getSettings().setUseWideViewPort(true);
                    webView.getSettings().setLoadWithOverviewMode(true);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadDataWithBaseURL("", buf.toString(), "text/html; charset=utf-8", "UTF-8", "");
                }
            });
    }

    /**
     * Calling system's service to print the current report, or export it to PDF file type.
     * @param webView a reference to the webView which displays the report.
     */
    private void printPDF(WebView webView) {
        PrintManager printManager = (PrintManager)getSystemService(Context.PRINT_SERVICE);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        PrintDocumentAdapter printDocumentAdapter = webView.createPrintDocumentAdapter(UserInfo.instance.getUserName() + "-" + simpleDateFormat.format(date) + "-Report");

        String jobName = getString(R.string.app_name) + " Document";
        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build();

        printManager.print(jobName, printDocumentAdapter, printAttributes);
    }


    /**
     * Read the data about all the orders in specific month from Firebase Realtime Database.
     * @param myCallback a reference to FoodInfoCallBack interface
     */
    private void readData(final FoodInfoCallBack myCallback) {

        final int month = Integer.parseInt(UserInfo.instance.getMonth());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Vendors").child(UserInfo.instance.getUserName()).child("completed_orders");
        ref.orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FoodInfo> foodInfos = new ArrayList<>();
                List<FoodInfo> sample = UserInfo.instance.getFood();
                for (int i = 0; i < sample.size(); i++) {
                    foodInfos.add(new FoodInfo(sample.get(i)));
                }
                if (snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Order order = dataSnapshot.getValue(Order.class);
                        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date = sdf1.parse(order.getDate());
                            String strDate = sdf2.format(date);

                            if (month == Integer.parseInt(strDate.substring(5,7))) {
                                for (OrderFood orderFood: order.getFoods()) {
                                    String fName = orderFood.getName();
                                    for (FoodInfo fi : foodInfos) {
                                        if (fi.getName().equals(fName)) {
                                            fi.setQuantity(fi.getQuantity() + Integer.parseInt(orderFood.getQuantity()));
                                            break;
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.d("ERROR", e.getMessage());
                        }
                    }
                    try {
                        myCallback.onCallBack2(foodInfos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}