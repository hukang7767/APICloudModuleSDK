package com.alpha.modulegnoga.measurement;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.SubscriptSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alpha.modulegnoga.R;
import com.cnoga.singular.mobile.sdk.constants.MeasurementConstants;
import com.cnoga.singular.mobile.sdk.constants.RangeConstant;
import com.cnoga.singular.mobile.sdk.measurement.ParamValueView;

public class ParamDetailInfoView extends LinearLayout {

    public static final int[] PARAM_ABBR = new int[]{
            R.string.hemna_hr, R.string.hemna_spo2,
            R.string.chart_type_sys, R.string.chart_type_dia, R.string.bm_cbg, R.string.hemto_hgb,
            R.string.bg_po2, R.string.bg_pco2, R.string.hemto_hct, R.string.hemna_bv,
            R.string.hemna_co, R.string.hemna_map, R.string.bg_ph, R.string.bm_co2,
            R.string.hemto_rbc, R.string.hemna_sv, R.string.bm_hba1c, R.string.bg_o2,
            R.string.bm_wbc, R.string.bm_plt, R.string.bm_k, R.string.bm_na,
            R.string.bm_ca, R.string.bm_cl, R.string.bm_bili, R.string.bm_alb,
            R.string.bm_hpi, R.string.bm_blvct, R.string.bm_hco3, R.string.bm_svo2,
            R.string.param4, R.string.param5, R.string.hemna_bp};

    public static final int[] PARAM_UNIT = new int[]{
            R.string.unit_beats_min, R.string.unit_per,
            R.string.unit_mmhg, R.string.unit_mmhg, R.string.unit_mg_dl, R.string.unit_g_dl,
            R.string.unit_mmhg, R.string.unit_mmhg, R.string.unit_per, R.string.unit_per,
            R.string.unit_l_min, R.string.unit_mmhg, R.string.unit_not_sure, R.string.unit_mmol_l,
            R.string.unit_m_ul, R.string.unit_ml_beat, R.string.unit_per, R.string.unit_ml_dl,
            R.string.unit_109_l, R.string.unit_109_l, R.string.unit_meq_l, R.string.unit_meq_l,
            R.string.unit_mg_dl, R.string.unit_meq_l, R.string.unit_mg_dl, R.string.unit_mg_dl,
            R.string.unit_per, R.string.unit_cm_sec, R.string.unit_mmol_l, R.string.unit_per,
            R.string.unit_not_sure, R.string.unit_not_sure, R.string.unit_mmhg};

    private TextView mParamAbbrTv;

    private ParamValueView mParamValueTv;

    private TextView mUnitTv;

    private RelativeLayout mDiaLayout;

    private ParamValueView mDiaValueTv;

    private int mParameterType = -1;

    public ParamDetailInfoView(Context context) {
        super(context);
        initView(context);
    }

    public ParamDetailInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ParamDetailInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.param_detail_info, this);

        mParamAbbrTv = (TextView) view.findViewById(R.id.param_detail_parameter_name);
        mParamValueTv = (ParamValueView) view.findViewById(R.id.param_detail_parameter_value);
        mUnitTv = (TextView) view.findViewById(R.id.param_detail_parameter_unit);
        mDiaLayout = (RelativeLayout) view.findViewById(R.id.param_detail_parameter_bp_layout);
        mDiaValueTv = (ParamValueView) view.findViewById(R.id.param_detail_dia_value);
    }

    public void setParamType(Context context, int type) {
        setParamType(context, type, MeasurementConstants.MODE_MEASUREMENT);
    }

    public void setParamType(Context context, int type, int mode) {
        mParameterType = type;
        if (mParameterType == MeasurementConstants.PARAM_TYPE_BLOOD_PRESSURE) {
            mParamValueTv.setParamType(context, MeasurementConstants.PARAM_TYPE_SYSTOLIC_BLOOD_PRESSURE, mode);
            mDiaValueTv.setParamType(context, MeasurementConstants.PARAM_TYPE_DIASTOLIC_BLOOD_PRESSURE, mode);
        } else {
            mParamValueTv.setParamType(context, type, mode);
        }
        setParamAbbrUnit(context);
    }

    /**
     * set the parameter type
     */
    private void setParamAbbrUnit(Context context) {
        if (mParameterType == MeasurementConstants.PARAM_TYPE_BLOOD_PRESSURE) {
            setParamAbbrString(mParamAbbrTv, context, R.string.hemna_bp);
            mUnitTv.setText(PARAM_UNIT[MeasurementConstants.PARAM_TYPE_SYSTOLIC_BLOOD_PRESSURE]);
            mDiaLayout.setVisibility(View.VISIBLE);
        }
        if (mParameterType < 0 || mParameterType > MeasurementConstants.PARAM_TYPE_MAX) {
            return;
        } else {
            setParamAbbrString(mParamAbbrTv, context, PARAM_ABBR[mParameterType]);
            mUnitTv.setText(PARAM_UNIT[mParameterType]);
            mDiaLayout.setVisibility(View.GONE);
        }
    }

    private void setParamAbbrString(TextView abbr, Context context, int resourceId) {
        String paramAbbr = context.getResources().getString(resourceId);
        if (paramAbbr.contains(RangeConstant.TWO)) {
            Spannable spannable = new SpannableString(paramAbbr);
            spannable.setSpan(new AbsoluteSizeSpan(context.getResources()
                            .getDimensionPixelSize(R.dimen.symbol)),
                    paramAbbr.length() - 1,
                    paramAbbr.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new SubscriptSpan(),
                    paramAbbr.length() - 1,
                    paramAbbr.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            abbr.setText(spannable);
        } else {
            abbr.setText(paramAbbr);
        }
    }

}
