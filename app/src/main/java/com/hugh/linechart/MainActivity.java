package com.hugh.linechart;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LeafLineChart leafLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        leafLineChart =  findViewById(R.id.leaf_chart);

        Axis axisX = new Axis(getAxisValuesX());
        axisX.setAxisColor(Color.parseColor("#33B5E5")).setTextColor(Color.DKGRAY).setHasLines(true);
        Axis axisY = new Axis(getAxisValuesY());
        axisY.setAxisColor(Color.parseColor("#33B5E5")).setTextColor(Color.DKGRAY).setHasLines(true).setShowText(true);
        leafLineChart.setAxisX(axisX);
        leafLineChart.setAxisY(axisY);

        List<Line> lines = new ArrayList<>();
        lines.add(getFoldLine());
        leafLineChart.setChartData(lines);

        leafLineChart.showWithAnimation(1000);

        Axis axisX1 = new Axis(getAxisValuesX());
        axisX.setAxisColor(Color.parseColor("#33B5E5")).setTextColor(Color.DKGRAY).setHasLines(true);
        Axis axisY1 = new Axis(getAxisValuesY());
        axisY.setAxisColor(Color.parseColor("#33B5E5")).setTextColor(Color.DKGRAY).setHasLines(true).setShowText(true);
        leafLineChart.setAxisX(axisX1);
        leafLineChart.setAxisY(axisY1);

        List<Line> lines1 = new ArrayList<>();
        lines.add(getFoldLine1());
        leafLineChart.setChartData(lines1);

        leafLineChart.showWithAnimation(1000);
    }

    private Line getFoldLine1(){
        List<PointValue> pointValues = new ArrayList<>();

        PointValue p = new PointValue();
        p.setX( (1 - 1) / 7f);
        p.setLabel(String.valueOf(90));
        p.setY(90 / 100f);
        pointValues.add(p);
        for (int i = 2; i <= 7; i++) {
            PointValue pointValue = new PointValue();
            pointValue.setX( (i - 1) / 6f);
            int var = (int) (Math.random() * 100);
            //float var = 20f;
            pointValue.setLabel(String.valueOf(var));
            pointValue.setY(var / 100f);
            pointValues.add(pointValue);
        }

        Line line = new Line(pointValues);
        line.setLineColor(Color.parseColor("#33B5E5"))
                .setLineWidth(2)
                .setPointColor(Color.BLUE)
                //.setCubic(false)
                .setPointRadius(2)
                .setFill(true)
                .setFillColor(Color.parseColor("#33B5E5"))
                //.setHasLabels(false)
                .setLabelColor(Color.parseColor("#33B5E5"));
        return line;
    }


    private Line getFoldLine(){
        List<PointValue> pointValues = new ArrayList<>();

        PointValue p = new PointValue();
        p.setX( (1 - 1) / 7f);
        p.setLabel(String.valueOf(90));
        p.setY(90 / 100f);
        pointValues.add(p);
        for (int i = 2; i <= 7; i++) {
            PointValue pointValue = new PointValue();
            pointValue.setX( (i - 1) / 6f);
            int var = (int) (Math.random() * 100);
            //float var = 20f;
            pointValue.setLabel(String.valueOf(var));
            pointValue.setY(var / 100f);
            pointValues.add(pointValue);
        }


        Line line = new Line(pointValues);
        line.setLineColor(Color.parseColor("#33B5E5"))
                .setLineWidth(2)
                .setPointColor(Color.BLUE)
                //.setCubic(false)
                .setPointRadius(2)
                .setFill(true)
                .setFillColor(Color.parseColor("#33B5E5"))
                //.setHasLabels(false)
                .setLabelColor(Color.parseColor("#33B5E5"));
        return line;
    }

    private List<AxisValue> getAxisValuesX(){
        List<AxisValue> axisValues = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            AxisValue value = new AxisValue();
            value.setLabel(i + "/01");
            axisValues.add(value);
        }
        return axisValues;
    }

    private List<AxisValue> getAxisValuesY(){
        List<AxisValue> axisValues = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            AxisValue value = new AxisValue();
            value.setLabel(String.valueOf(i * 10));
            axisValues.add(value);
        }
        return axisValues;
    }
}
