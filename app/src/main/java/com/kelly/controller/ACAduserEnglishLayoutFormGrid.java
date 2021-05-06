package com.kelly.controller;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.EditText;

public class ACAduserEnglishLayoutFormGrid extends ViewGroup {
    private int formColCount = 4;
    private int gridHorGrap = 10;
    private int gridVerGrap = 5;
    private int marginValue = 0;
    private int mode = 0;
    private int rowHeight = 48;

    public static class LayoutParams2 extends MarginLayoutParams {
        public int colCount;
        public int colIndex;
        public int rowCount;
        public int rowIndex;

        public LayoutParams2(int colIndex, int rowIndex, int colCount, int rowCount) {
            super(-2, -2);
            this.colIndex = colIndex;
            this.rowIndex = rowIndex;
            this.colCount = colCount;
            this.rowCount = rowCount;
        }
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int colCount;
        public int colIndex;
        public int rowCount;
        public int rowIndex;

        public LayoutParams(int colIndex, int rowIndex, int colCount, int rowCount) {
            super(-2, -2);
            this.colIndex = colIndex;
            this.rowIndex = rowIndex;
            this.colCount = colCount;
            this.rowCount = rowCount;
        }
    }

    public ACAduserEnglishLayoutFormGrid(Context context) {
        super(context);
        EditText txtField = new EditText(context);
        txtField.setText("Height");
        txtField.measure(MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(0, 0));
        this.rowHeight = txtField.getMeasuredHeight();
    }

    public void setFormColumnCount(int formColCount) {
        if (formColCount <= 0) {
            formColCount = 4;
        }
        this.formColCount = formColCount;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setHorVerGrap(int gridHorGrap, int gridVerGrap) {
        this.gridHorGrap = gridHorGrap;
        this.gridVerGrap = gridVerGrap;
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        LayoutParams params;
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int specHeight = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int count = getChildCount();
        if (heightMode != 1073741824) {
            int maxRowCount = 0;
            for (i = 0; i < count; i++) {
                params = (LayoutParams) getChildAt(i).getLayoutParams();
                if (params.rowCount + params.rowIndex > maxRowCount) {
                    maxRowCount = params.rowCount + params.rowIndex;
                }
            }
            specHeight = (((this.rowHeight + this.gridVerGrap) * maxRowCount) + this.marginValue) + this.marginValue;
        }
        setMeasuredDimension(specWidth, specHeight);
        int colWidth = ((specWidth - this.marginValue) - this.marginValue) / this.formColCount;
        for (i = 0; i < count; i++) {
            View child = getChildAt(i);
            params = (LayoutParams) child.getLayoutParams();
            child.measure(MeasureSpec.makeMeasureSpec((params.colCount * colWidth) - this.gridHorGrap, widthMode), MeasureSpec.makeMeasureSpec((params.rowCount * (this.rowHeight + this.gridVerGrap)) - this.gridVerGrap, heightMode));
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int count = getChildCount();
        int colWidth = (((right - left) - this.marginValue) - this.marginValue) / this.formColCount;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int childTop = (this.marginValue + (lp.rowIndex * (this.rowHeight + this.gridVerGrap))) + (this.gridVerGrap / 2);
                int childBottom = ((lp.rowCount * (this.rowHeight + this.gridVerGrap)) + childTop) - this.gridVerGrap;
                int childLeft;
                if (lp.colIndex > 200 || lp.colIndex <= 100) {
                    if (lp.colIndex <= 300 && lp.colIndex > 200) {
                        childLeft = ((this.marginValue + ((lp.colIndex - 200) * colWidth)) + (((1 - ((lp.colIndex - 200) % 2)) * colWidth) / 2)) + (this.gridHorGrap / 2);
                        child.layout(childLeft, childTop, (((lp.colCount * colWidth) + childLeft) - (colWidth / 2)) - (this.gridHorGrap / 2), childBottom);
                    } else if (lp.colIndex > 300) {
                        childLeft = ((this.marginValue + ((lp.colIndex - 300) * colWidth)) + (colWidth / 2)) + (this.gridHorGrap / 2);
                        child.layout(childLeft, childTop, ((lp.colCount * colWidth) + childLeft) - (this.gridHorGrap / 2), childBottom);
                    } else {
                        childLeft = (this.marginValue + (lp.colIndex * colWidth)) + (this.gridHorGrap / 2);
                        child.layout(childLeft, childTop, ((lp.colCount * colWidth) + childLeft) - (this.gridHorGrap / 2), childBottom);
                    }
                } else if (lp.colIndex == 103) {
                    childLeft = (this.marginValue + ((((lp.colIndex - 102) * this.formColCount) / 2) * colWidth)) + (this.gridHorGrap / 2);
                    child.layout(childLeft, childTop, (lp.colCount + childLeft) - (this.gridHorGrap / 2), childBottom);
                } else {
                    childLeft = (this.marginValue + ((((lp.colIndex - 100) * this.formColCount) / 3) * colWidth)) + (this.gridHorGrap / 2);
                    child.layout(childLeft, childTop, (lp.colCount + childLeft) - (this.gridHorGrap / 2), childBottom);
                }
            }
        }
    }
}
