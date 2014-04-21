package com.mikedg.glass.control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

//https://developers.google.com/glass/develop/gdk/ui-widgets
public class TuggableView extends CardScrollView {

    private final View mContentView;

    /**
     * Initializes a TuggableView that uses the specified layout
     * resource for its user interface.
     */
    public TuggableView(Context context, int layoutResId) {
        this(context, LayoutInflater.from(context)
                .inflate(layoutResId, null));
    }

    /**
     * Initializes a TuggableView that uses the specified view
     * for its user interface.
     */
    public TuggableView(Context context, View view) {
        super(context);

        mContentView = view;
        setAdapter(new SingleCardAdapter());
        activate();
    }

    /**
     * Overridden to return false so that all motion events still
     * bubble up to the activity's onGenericMotionEvent() method after
     * they are handled by the card scroller. This allows the activity
     * to handle TAP gestures using a GestureDetector instead of the
     * card scroller's OnItemClickedListener.
     */
    @Override
    protected boolean dispatchGenericFocusedEvent(MotionEvent event) {
        super.dispatchGenericFocusedEvent(event);
        return false;
    }

    /** Holds the single "card" inside the card scroll view. */
    private class SingleCardAdapter extends CardScrollAdapter {

        @Override
        public int getPosition(Object item) {
            return 0;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return mContentView;
        }

        @Override
        public View getView(int position, View recycleView,
                            ViewGroup parent) {
            return mContentView;
        }
    }
}