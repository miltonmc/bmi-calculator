package br.com.inovant.bmi;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.joda.time.DateTime;

public class DateAxisValueFormatter implements IAxisValueFormatter
{
    private final long startingMillis; // starting date in millis

    public DateAxisValueFormatter(long startingMillis) {
        this.startingMillis = startingMillis;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        final long originalMillis = startingMillis + (long) value;
        return new DateTime(originalMillis).toString("HH:mm:ss");
    }
}
