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
import android.widget.TextView;

import com.alpha.modulegnoga.R;
import com.cnoga.singular.mobile.sdk.constants.MeasurementConstants;
import com.cnoga.singular.mobile.sdk.constants.RangeConstant;
import com.cnoga.singular.mobile.sdk.measurement.ParamValueView;

public class MeasurementParamItemView extends LinearLayout {

    public static final int[] PARAM_NAME = new int[]{
            R.string.heart_rate, R.string.spo2,
            R.string.blood_pressure, R.string.blood_pressure, R.string.cbg, R.string.hgb,
            R.string.po2, R.string.pco2, R.string.hct, R.string.bv,
            R.string.co, R.string.map, R.string.ph, R.string.co2,
            R.string.rbc, R.string.sv, R.string.hba1c, R.string.o2,
            R.string.wbc, R.string.plt, R.string.k, R.string.na,
            R.string.ca, R.string.cl, R.string.tBili, R.string.alb,
            R.string.hpi, R.string.blvct, R.string.hco3, R.string.svo2,
            R.string.param4, R.string.param5, R.string.blood_pressure};

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

    public TextView abbr;

    public TextView group;

    public TextView name;

    public ParamValueView value;

    public TextView unit;

    private int mParameterType = MeasurementConstants.PARAM_TYPE_UNKNOWN;

    public MeasurementParamItemView(Context context) {
        super(context);
        initView(context);
    }

    public MeasurementParamItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MeasurementParamItemView(Context context, @Nullable AttributeSet attrs,
                                    int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.measurement_param_item, this);
        abbr = (TextView) view.findViewById(R.id.measurement_param_item_abbr);
        group = (TextView) view.findViewById(R.id.measurement_param_item_group);
        name = (TextView) view.findViewById(R.id.measurement_param_item_name);
        value = (ParamValueView) view.findViewById(R.id.measurement_param_item_value);
        unit = (TextView) view.findViewById(R.id.measurement_param_item_unit);
    }

    public void setParamType(Context context, int type) {
        setParamType(context, type, MeasurementConstants.MODE_MEASUREMENT);
    }

    public void setParamType(Context context, int type, int mode) {
        if (type == MeasurementConstants.PARAM_TYPE_SYSTOLIC_BLOOD_PRESSURE
                || type == MeasurementConstants.PARAM_TYPE_DIASTOLIC_BLOOD_PRESSURE) {
            mParameterType = MeasurementConstants.PARAM_TYPE_BLOOD_PRESSURE;
        } else {
            mParameterType = type;
        }
        value.setParamType(context, mParameterType, mode);
        setParamInfo(context);
    }

    /**
     * set the parameter type
     */
    private void setParamInfo(Context context) {
        if (mParameterType == MeasurementConstants.PARAM_TYPE_BLOOD_PRESSURE) {
            setParamName(context, abbr, R.string.hemna_bp);
            group.setText(getParamGroupResId(mParameterType));
            setParamName(context, name, R.string.blood_pressure);
            unit.setText(R.string.unit_mmhg);
        }
        if (mParameterType < 0 || mParameterType > MeasurementConstants.PARAM_TYPE_MAX) {
            return;
        } else {
            setParamName(context, abbr, PARAM_ABBR[mParameterType]);
            group.setText(getParamGroupResId(mParameterType));
            setParamName(context, name, PARAM_NAME[mParameterType]);
            unit.setText(PARAM_UNIT[mParameterType]);
        }
    }

    private void setParamName(Context context, TextView name, int resourcesId) {
        String parameterName = context.getResources().getString(resourcesId);
        name.setText(parameterName);
        if (parameterName.contains(RangeConstant.TWO)) {
            Spannable spannable = new SpannableString(parameterName);
            spannable.setSpan(new AbsoluteSizeSpan(context.getResources()
                            .getDimensionPixelSize(R.dimen.symbol)),
                    parameterName.length() - 1,
                    parameterName.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new SubscriptSpan(),
                    parameterName.length() - 1,
                    parameterName.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            name.setText(spannable);
        }
    }

    private int getParamGroupResId(int parameterType) {
        int group = R.string.hemodynamics;
        switch (parameterType) {
            case MeasurementConstants.PARAM_TYPE_OXYGEN_SATURATION:
            case MeasurementConstants.PARAM_TYPE_SYSTOLIC_BLOOD_PRESSURE:
            case MeasurementConstants.PARAM_TYPE_DIASTOLIC_BLOOD_PRESSURE:
            case MeasurementConstants.PARAM_TYPE_BLOOD_PRESSURE:
            case MeasurementConstants.PARAM_TYPE_MEAN_ARTERIAL_PRESSURE:
            case MeasurementConstants.PARAM_TYPE_HEART_RATE:
            case MeasurementConstants.PARAM_TYPE_CARDIAC_OUTPUT:
            case MeasurementConstants.PARAM_TYPE_STROKE_VOLUME:
            case MeasurementConstants.PARAM_TYPE_BLOOD_VISCOSITY:
            case MeasurementConstants.PARAM_TYPE_HPI:
            case MeasurementConstants.PARAM_TYPE_BLVCT:
                group = R.string.hemodynamics;
                break;
            case MeasurementConstants.PARAM_TYPE_BLOOD_PH:
            case MeasurementConstants.PARAM_TYPE_PCO2:
            case MeasurementConstants.PARAM_TYPE_PO2:
            case MeasurementConstants.PARAM_TYPE_O2:
            case MeasurementConstants.PARAM_TYPE_CO2:
            case MeasurementConstants.PARAM_TYPE_SVO2:
                group = R.string.blood_gases;
                break;
            case MeasurementConstants.PARAM_TYPE_HEMOGLOBIN:
            case MeasurementConstants.PARAM_TYPE_HEMATOCRIT:
            case MeasurementConstants.PARAM_TYPE_RED_BLOOD_CELLS:
            case MeasurementConstants.PARAM_TYPE_WBC:
            case MeasurementConstants.PARAM_TYPE_PLT:
                group = R.string.hematology;
                break;
            case MeasurementConstants.PARAM_TYPE_GLYCOSYLATE_HEMOGLOBIN:
            case MeasurementConstants.PARAM_TYPE_K:
            case MeasurementConstants.PARAM_TYPE_NA:
            case MeasurementConstants.PARAM_TYPE_CA:
            case MeasurementConstants.PARAM_TYPE_CL:
            case MeasurementConstants.PARAM_TYPE_TBILI:
            case MeasurementConstants.PARAM_TYPE_ALB:
            case MeasurementConstants.PARAM_TYPE_HCO3:
                group = R.string.biochemistry;
                break;
            default:
                break;
        }
        return group;
    }

}
