package br.com.inovant.bmi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class CalculatorActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "br.com.inovant.bmi";

    private EditText height;
    private EditText weight;

    private LineChart mChart;
    private ArrayList<Entry> values = new ArrayList<>();
    private ArrayList<Entry> normal = new ArrayList<>();
    private long initialDate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Restore preferences
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        final String weightValue = settings.getString("weight", "");
        final String heightValue = settings.getString("height", "");

        height = (EditText) findViewById(R.id.etHeight);
        height.setText(heightValue);
        weight = (EditText) findViewById(R.id.etWeight);
        weight.setText(weightValue);
        final TextView result = (TextView) findViewById(R.id.result);
        final TextView category = (TextView) findViewById(R.id.category);

        Button calculate = (Button) findViewById(R.id.btCalculate);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float bmi = Float.valueOf(weight.getText().toString()) / (Float.valueOf(height.getText().toString()) * Float.valueOf(height.getText().toString()));
                result.setText(String.valueOf(bmi));
                String categoryStr;
                if(bmi < 18.5){
                    categoryStr = getString(R.string.underweight);
                } else if(bmi < 25){
                    categoryStr = getString(R.string.normal_weight);
                } else if(bmi < 30){
                    categoryStr = getString(R.string.overweight);
                } else{
                    categoryStr = getString(R.string.obesity);
                }
                category.setText(categoryStr);
                reloadChart(bmi);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mChart = (LineChart) findViewById(R.id.chart);
        mChart.setTouchEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calculator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("weight", weight.getText().toString());
        editor.putString("height", height.getText().toString());

        // Commit the edits!
        editor.commit();
    }

    private void reloadChart(float bmi) {
        long date = new DateTime().getMillis();
        if(values.size() == 0){
            initialDate = date;
            DateAxisValueFormatter formatter = new DateAxisValueFormatter(initialDate);
            mChart.getXAxis().setValueFormatter(formatter);
        }

        values.add(new Entry(date - initialDate, bmi));
        normal.add(new Entry(date - initialDate, 25));

        mChart.resetTracking();

        LineDataSet d = new LineDataSet(values, "BMC");
        d.setLineWidth(2.5f);
        d.setColor(ColorTemplate.COLORFUL_COLORS[3]);

        LineDataSet n = new LineDataSet(normal, "Normal");
        n.enableDashedLine(15f, 15f, 0f);
        n.setDrawIcons(false);
        n.setLineWidth(2.5f);
        n.setColor(ColorTemplate.COLORFUL_COLORS[1]);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(d);
        dataSets.add(n);

        LineData data = new LineData(dataSets);
        mChart.setData(data);
        mChart.invalidate();
    }
}
