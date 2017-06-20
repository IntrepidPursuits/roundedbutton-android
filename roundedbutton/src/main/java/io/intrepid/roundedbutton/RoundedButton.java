package io.intrepid.roundedbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.Gravity;

public class RoundedButton extends AppCompatTextView {

    private static final int INVALID_COLOR = -1;

    @ColorInt
    private int fillColor;
    @ColorInt
    private int strokeColor;
    @Dimension
    private int strokeWidth;
    @ColorInt
    private int disabledColor;

    public RoundedButton(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public RoundedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public RoundedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.RoundedButton,
                    defStyleAttr,
                    defStyleRes
            );

            strokeColor = typedArray.getColor(R.styleable.RoundedButton_rb_stroke_color, INVALID_COLOR);
            strokeWidth = typedArray.getDimensionPixelSize(R.styleable.RoundedButton_rb_stroke_width, getResources().getDimensionPixelSize(R.dimen.rb_default_stroke_width));

            // use the default fill color if stroke isn't specified
            int defaultFillColorRes =
                    strokeColor == INVALID_COLOR ? R.color.rb_default_fill_color : android.R.color.transparent;
            int defaultFillColor = ContextCompat.getColor(context, defaultFillColorRes);
            fillColor = typedArray.getColor(R.styleable.RoundedButton_rb_fill_color, defaultFillColor);

            disabledColor = typedArray.getColor(R.styleable.RoundedButton_rb_disabled_color, ContextCompat.getColor(getContext(), R.color.rb_default_disabled_color));

            typedArray.recycle();
        }
        setGravity(Gravity.CENTER);
        updateBackground();
    }

    public void setFillColor(@ColorInt int color) {
        this.fillColor = color;
        updateBackground();
    }

    public void setStroke(@Dimension int strokeWidth, @ColorInt int color) {
        this.strokeWidth = strokeWidth;
        this.strokeColor = color;
        updateBackground();
    }

    public void setDisabledColor(@ColorInt int color) {
        this.disabledColor = color;
        updateBackground();
    }

    private void updateBackground() {
        StateListDrawable stateListDrawable = new StateListDrawable();
        Drawable defaultDrawable = createRoundedRectangleDrawable(fillColor, strokeColor, strokeWidth);
        int darkenedFill = darkenColor(fillColor);
        Drawable pressedDrawable = createRoundedRectangleDrawable(darkenedFill, strokeColor, strokeWidth);
        Drawable disabledDrawable = createRoundedRectangleDrawable(disabledColor, strokeColor, strokeWidth);
        stateListDrawable.addState(new int[] { -android.R.attr.state_enabled }, disabledDrawable);
        stateListDrawable.addState(new int[] { android.R.attr.state_pressed }, pressedDrawable);
        stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);

        setBackground(stateListDrawable);
    }

    private Drawable createRoundedRectangleDrawable(int fillColor, int strokeColor, int strokeWidth) {
        GradientDrawable rectDrawable = new GradientDrawable();
        rectDrawable.setShape(GradientDrawable.RECTANGLE);
        if (strokeColor != INVALID_COLOR) {
            rectDrawable.setStroke(strokeWidth, strokeColor);
        }
        rectDrawable.setColor(fillColor);
        // using some really large radius here, Android will automatically clip it down to height/2
        rectDrawable.setCornerRadius(10000);
        return rectDrawable;
    }

    // Create a slightly darkened color for touch indicator
    private int darkenColor(int originalColor) {
        if (originalColor == Color.TRANSPARENT) {
            return ContextCompat.getColor(getContext(), R.color.rb_darken_color);
        }
        float[] hsv = new float[3];
        Color.colorToHSV(originalColor, hsv);
        hsv[2] *= 0.9f;
        return Color.HSVToColor(hsv);
    }
}
