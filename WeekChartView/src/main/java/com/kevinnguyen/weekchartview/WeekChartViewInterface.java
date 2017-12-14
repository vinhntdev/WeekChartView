package com.kevinnguyen.weekchartview;

import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;

/**
 * WeekChartViewInterface
 */
public interface WeekChartViewInterface {

    void setAmounts(int[] amounts);

    void setViewBackgroundColor(@ColorRes int resid);

    void setViewTextColor(@ColorRes int resid);

    void setViewTextSize(@DimenRes int resid);

    void setViewLineColor(@ColorRes int resid);

    void setViewStartGradientColor(@ColorRes int resid);

    void setViewEndGradientColor(@ColorRes int resid);

    void setViewLineSize(@DimenRes int resid);

    void setAnimationTime(int milliseconds);

    void setAnimationEnable(boolean enable);

}
