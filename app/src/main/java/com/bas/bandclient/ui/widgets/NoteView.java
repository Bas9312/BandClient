package com.bas.bandclient.ui.widgets;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
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

public class NoteView extends View implements View.OnTouchListener{
    private float dX, dY;
    private OneVisualNote note;
    private boolean isClicked = false;

    private Paint circlePaint = new Paint();
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public NoteView(Context context) {
        super(context);
        setOnTouchListener(this);

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
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);

        textPaint.setTextSize(50);
        textPaint.setStyle(Paint.Style.STROKE);
    }

    public NoteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);

        init();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("onDraw");

        canvas.drawCircle(getHeight() / 2, getHeight() / 2, getHeight() / 2, circlePaint);

        if (note.getNote() != null) {
            System.out.println("Draw noteText: " + note.getNote().toString());
            int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;
            canvas.drawText(note.getNote().toString(), getHeight() / 2, yPos, textPaint);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                isClicked = true;
                break;

            case MotionEvent.ACTION_MOVE:
                isClicked = false;
                float parentWidth = ((View)getParent()).getWidth();
                float parentHeight = ((View)getParent()).getHeight();

                float x = event.getRawX() + dX;
                if (x < 0) x = 0;
                if (x > (parentWidth - getWidth())) x = parentWidth - getWidth();

                float y = event.getRawY() + dY;
                if (y < 0) y = 0;
                if (y > (parentHeight - getHeight())) y = parentHeight - getHeight();

                view.animate()
                        .x(x)
                        .y(y)
                        .setDuration(0)
                        .start();
                break;
            case MotionEvent.ACTION_UP:
                if (isClicked) {
                    showPopupActions();
                }
                return false;
            default:
                return false;
        }
        return true;
    }

    public void showPopupActions() {
        PopupMenu popupMenu = new PopupMenu(this.getContext(), this);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.action_edit_note:
                        showNoteEditDialog();
                        return true;

                    case R.id.action_delete:
                        ((ViewGroup)NoteView.this.getParent()).removeView(NoteView.this);
                        return true;

                    case R.id.action_change_size_big:
                        plusSize();
                        return true;
                    case R.id.action_change_size_small:
                        minusSize();
                        return true;
                    default:
                        return false;
                }
            }

        });

        MenuInflater inflater = popupMenu.getMenuInflater();

        inflater.inflate(R.menu.note_popup, popupMenu.getMenu());

        popupMenu.show();
    }

    public void plusSize() {
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.height = (int) (getHeight() * 1.2);
        params.width = (int) (getWidth() * 1.2);
        setLayoutParams(params);
    }

    public void minusSize() {
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.height = (int) (getHeight() * 0.8);
        params.width = (int) (getWidth() * 0.8);
        setLayoutParams(params);
    }

    private void showNoteEditDialog() {
        PopupMenu popupMenu = new PopupMenu(this.getContext(), this);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (!menuItem.hasSubMenu()) {
                    note.setNote(Note.parseFromString(menuItem.getTitle().toString()));
                    NoteView.this.invalidate();
                    Log.d("", menuItem.getTitle().toString() + " clicked");
                    return true;
                } else return false;
            }

        });
        int i = 0;
        if (popupMenu.getMenu() != null)
            popupMenu.getMenu().clear();
        for (Note.Octave octave : Note.Octave.values()) {
            SubMenu subMenu = popupMenu.getMenu().addSubMenu(i, Menu.NONE, i, octave.toString() + " октава");
            for (Note note : Note.values()) {
                note.setOctave(octave);
                subMenu.add(note.toString());
            }
            i++;
        }

        popupMenu.show();
    }

    public void setNote(OneVisualNote note) {
        this.note = note;
    }

    public Note getNote() {
        return note.getNote();
    }

    public float getNoteSize() {
        return getHeight();
    }
}
