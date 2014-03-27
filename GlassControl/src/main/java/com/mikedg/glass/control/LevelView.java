/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.mikedg.glass.control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
/*
For debugging purposes, we've repurposed the GDK level sample from https://github.com/googleglass/gdk-level-sample
 */
/**
 * View used to draw the level line.
 */
public class LevelView extends View {

    public static final int COUNT = 5;
    private Paint mPaints[] = new Paint[COUNT];

    private float mAngle = 0.f;
    private Paint mPaint;

    public LevelView(Context context) {
        this(context, null, 0);
        init();
    }

    public LevelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public LevelView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init();
    }

    private void init() {
        mPaints[0] = new Paint();
        mPaints[0].setColor(Color.BLUE);
        mPaints[0].setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaints[0].setStrokeWidth(5);
        mPaints[1] = new Paint(mPaints[0]);
        mPaints[1].setColor(Color.GREEN);
        mPaints[2] = new Paint(mPaints[0]);
        mPaints[2].setColor(Color.CYAN);
        mPaints[3] = new Paint(mPaints[0]);
        mPaints[3].setColor(Color.YELLOW);
        mPaints[4] = new Paint(mPaints[0]);
        mPaints[4].setColor(Color.RED);

        setColor(0);
    }

    /**
     * Set the angle of the level line.
     *
     * @param angleRadians Angle of the level line.
     */
    public void setAngle(float angleRadians) {
        mAngle = angleRadians;
        // Redraw the line.
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight() / 2;

        // Compute the coordinates.
        float y = (float) Math.tan(mAngle) * width / 2;

        // Draw the level line.
        canvas.drawLine(0, y + height, width, -y + height, mPaint);
    }

    public void setColor(int color) {
        //FIXME: somehow this can hit 5, wth? happens randomly though and not often
        mPaint = mPaints[Math.min(color, COUNT - 1)];
    }
}
