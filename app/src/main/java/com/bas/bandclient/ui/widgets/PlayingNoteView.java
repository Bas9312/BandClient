package com.bas.bandclient.ui.widgets;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupMenu;

import com.bas.bandclient.R;
import com.bas.bandclient.models.Note;

/**
 * Created by bas on 23.11.16.
 */

public class PlayingNoteView extends View{
    private OneVisualNote note;
    private String text = null;

    private Paint circlePaint = new Paint();
    private Paint blackPaint = new Paint();
    private Paint whitePaint = new Paint();
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    double procent = 100.0;
    private TypeOfFilling typeOfFilling = TypeOfFilling.Circle;

    public enum TypeOfFilling {Circle, BottomTop}

    public PlayingNoteView(Context context) {
        super(context);

        init();
    }

    public void setSize(int size) {
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();

        params.height = size;
        params.width = size;
        setLayoutParams(params);
    }

    private void init() {
        circlePaint.setColor(Color.BLACK);
        blackPaint.setColor(Color.BLACK);
        whitePaint.setColor(Color.WHITE);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);

        textPaint.setTextSize(50);
        textPaint.setStyle(Paint.Style.STROKE);
    }

    public PlayingNoteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("onDraw");

        if (typeOfFilling == TypeOfFilling.Circle) {
            if (procent < 99.5) {
                canvas.drawCircle(getHeight() / 2, getHeight() / 2, getHeight() / 2, blackPaint);
                canvas.drawCircle(getHeight() / 2, getHeight() / 2, getHeight() / 2 - 10, whitePaint);
            }
            canvas.drawCircle(getHeight() / 2, getHeight() / 2, (float) (getHeight() * (procent / 100.0) / 2), circlePaint);
        } else if (typeOfFilling == TypeOfFilling.BottomTop) {
            canvas.drawCircle(getHeight() / 2, getHeight() / 2, (float) (getHeight() / 2), circlePaint);
            canvas.drawRect(0, 0, getHeight(), (float) (getHeight() * ((100.0 - procent) / 100.0)), whitePaint);

        }

        if (note.getNote() != null) {
            System.out.println("Draw noteText: " + note.getNote().toString());
            int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;
            canvas.drawText(text == null ? note.getNote().toString() : text, getHeight() / 2, yPos, textPaint);
        }
    }

    public void setNote(OneVisualNote note) {
        this.note = note;
    }

    public Note getNote() {
        return note.getNote();
    }

    public void setColor(int argb) {
        circlePaint.setColor(argb);
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return circlePaint.getColor();
    }

    public void setProcent(double procent, TypeOfFilling typeOfFilling) {
        this.procent = procent;
        this.typeOfFilling = typeOfFilling;
    }
}
