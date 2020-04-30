package com.alpha.modulegnoga.measurement;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alpha.modulegnoga.R;
import com.cnoga.singular.mobile.sdk.constants.DeviceConstant;
import com.cnoga.singular.mobile.sdk.constants.MeasurementConstants;

import java.util.ArrayList;

public class MeasurementGroupCards extends RelativeLayout {

    private static final String TAG = "MeasurementGroupCards";

    public static final int[] ALL_HEMODYNAMICS_POSITION = new int[]{
            MeasurementConstants.PARAM_TYPE_OXYGEN_SATURATION,
            MeasurementConstants.PARAM_TYPE_BLOOD_PRESSURE,
            MeasurementConstants.PARAM_TYPE_MEAN_ARTERIAL_PRESSURE,
            MeasurementConstants.PARAM_TYPE_HEART_RATE,
            MeasurementConstants.PARAM_TYPE_CARDIAC_OUTPUT,
            MeasurementConstants.PARAM_TYPE_STROKE_VOLUME,
            MeasurementConstants.PARAM_TYPE_BLOOD_VISCOSITY,
            MeasurementConstants.PARAM_TYPE_HPI,
            MeasurementConstants.PARAM_TYPE_BLVCT};

    public static final int[] ALL_BLOOD_GASES_POSITION = new int[]{
            MeasurementConstants.PARAM_TYPE_BLOOD_PH,
            MeasurementConstants.PARAM_TYPE_PCO2,
            MeasurementConstants.PARAM_TYPE_PO2,
            MeasurementConstants.PARAM_TYPE_O2,
            MeasurementConstants.PARAM_TYPE_CO2,
            MeasurementConstants.PARAM_TYPE_SVO2};

    public static final int[] ALL_HEMATOLOGY_POSITION = new int[]{
            MeasurementConstants.PARAM_TYPE_HEMOGLOBIN,
            MeasurementConstants.PARAM_TYPE_HEMATOCRIT,
            MeasurementConstants.PARAM_TYPE_RED_BLOOD_CELLS,
            MeasurementConstants.PARAM_TYPE_WBC,
            MeasurementConstants.PARAM_TYPE_PLT};

    public static final int[] ALL_BIOCHEMISTRY_POSITION = new int[]{
            MeasurementConstants.PARAM_TYPE_GLYCOSYLATE_HEMOGLOBIN,
            MeasurementConstants.PARAM_TYPE_BLOOD_GLUCOSE_LEVEL,
            MeasurementConstants.PARAM_TYPE_K,
            MeasurementConstants.PARAM_TYPE_NA,
            MeasurementConstants.PARAM_TYPE_CA,
            MeasurementConstants.PARAM_TYPE_CL,
            MeasurementConstants.PARAM_TYPE_TBILI,
            MeasurementConstants.PARAM_TYPE_ALB,
            MeasurementConstants.PARAM_TYPE_HCO3};

    private static final ArrayList<Integer> HAVE_HEMODYNAMICS_POSITION = new ArrayList<>();
    private static final ArrayList<Integer> HAVE_BLOOD_GASES_POSITION = new ArrayList<>();
    private static final ArrayList<Integer> HAVE_HEMATOLOGY_POSITION = new ArrayList<>();
    private static final ArrayList<Integer> HAVE_BIOCHEMISTRY_POSITION = new ArrayList<>();
    private static Context sContext;

    private OnParamClickListener onParamClickListener;

    private byte[] capabilities;

    public void setOnParamClickListener(OnParamClickListener onParamClickListener) {
        this.onParamClickListener = onParamClickListener;
    }

    private int deviceType;

    private int mode;

    private LinearLayout mHemodynamicsLl;

    private RecyclerView mHemodynamicsRv;

    private ParamItemAdapter mHemodynamicsAdapter;

    private LinearLayout mBloodGasesLl;

    private RecyclerView mBloodGasesRv;

    private ParamItemAdapter mBloodGasesAdapter;

    private LinearLayout mHematologyLl;

    private RecyclerView mHematologyRv;

    private ParamItemAdapter mHematologyAdapter;

    private LinearLayout mBiochemistryLl;

    private RecyclerView mBiochemistryRv;

    private ParamItemAdapter mBiochemistryAdapter;

    public MeasurementGroupCards(Context context) {
        super(context);
        initView(context);
    }

    public MeasurementGroupCards(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MeasurementGroupCards(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        sContext = context.getApplicationContext();
        deviceType = DeviceConstant.DEVICE_TYPE_UNKNOWN;
        View view = View.inflate(context, R.layout.measurement_group_cards, this);
        mHemodynamicsLl = (LinearLayout) view.findViewById(R.id.params_group_hemodynamics);
        mHemodynamicsRv = (RecyclerView) view.findViewById(R.id.params_group_list_hemodynamics);
        mBloodGasesLl = (LinearLayout) view.findViewById(R.id.params_group_blood);
        mBloodGasesRv = (RecyclerView) view.findViewById(R.id.params_group_list_blood);
        mHematologyLl = (LinearLayout) view.findViewById(R.id.params_group_hematology);
        mHematologyRv = (RecyclerView) view.findViewById(R.id.params_group_list_hematology);
        mBiochemistryLl = (LinearLayout) view.findViewById(R.id.params_group_biochemistry);
        mBiochemistryRv = (RecyclerView) view.findViewById(R.id.params_group_list_biochemistry);
        mHemodynamicsRv.setFocusable(false);
        mBloodGasesRv.setFocusable(false);
        mHematologyRv.setFocusable(false);
        mBiochemistryRv.setFocusable(false);
    }

    public void init(byte[] capabilities) {
        init(capabilities, MeasurementConstants.MODE_MEASUREMENT);
    }


    public void init(byte[] capabilities, int mode) {
        this.capabilities = capabilities;
        this.mode = mode;
        HAVE_HEMODYNAMICS_POSITION.clear();
        HAVE_BLOOD_GASES_POSITION.clear();
        HAVE_HEMATOLOGY_POSITION.clear();
        HAVE_BIOCHEMISTRY_POSITION.clear();

        if (capabilities != null) {
            for (int i = 0; i < ALL_HEMODYNAMICS_POSITION.length; i++) {
                if (ALL_HEMODYNAMICS_POSITION[i] != MeasurementConstants.PARAM_TYPE_BLOOD_PRESSURE) {
                    if (capabilities[ALL_HEMODYNAMICS_POSITION[i]] == 0x01) {
                        HAVE_HEMODYNAMICS_POSITION.add(ALL_HEMODYNAMICS_POSITION[i]);
                    }
                } else {
                    if (capabilities[MeasurementConstants.PARAM_TYPE_SYSTOLIC_BLOOD_PRESSURE] == 0x01) {
                        HAVE_HEMODYNAMICS_POSITION.add(MeasurementConstants.PARAM_TYPE_BLOOD_PRESSURE);
                    }
                }
            }
            for (int i = 0; i < ALL_BLOOD_GASES_POSITION.length; i++) {
                if (capabilities[ALL_BLOOD_GASES_POSITION[i]] == 0x01) {
                    HAVE_BLOOD_GASES_POSITION.add(ALL_BLOOD_GASES_POSITION[i]);
                }
            }
            for (int i = 0; i < ALL_HEMATOLOGY_POSITION.length; i++) {
                if (capabilities[ALL_HEMATOLOGY_POSITION[i]] == 0x01) {
                    HAVE_HEMATOLOGY_POSITION.add(ALL_HEMATOLOGY_POSITION[i]);
                }
            }
            for (int i = 0; i < ALL_BIOCHEMISTRY_POSITION.length; i++) {
                if (capabilities[ALL_BIOCHEMISTRY_POSITION[i]] == 0x01) {
                    HAVE_BIOCHEMISTRY_POSITION.add(ALL_BIOCHEMISTRY_POSITION[i]);
                }
            }
            initView();
        }
    }

    private void initView() {
        // Hemodynamics Part
        if (HAVE_HEMODYNAMICS_POSITION.size() != 0) {
            mHemodynamicsAdapter = new ParamItemAdapter(HAVE_HEMODYNAMICS_POSITION, mode);
            mHemodynamicsAdapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
                @Override
                public void onParamItemClick(int position) {
                    if (onParamClickListener != null && position < HAVE_HEMODYNAMICS_POSITION.size()) {
                        onParamClickListener.onParamClick(HAVE_HEMODYNAMICS_POSITION.get(position));
                    }
                }
            });
            mHemodynamicsRv.setHasFixedSize(true);
            mHemodynamicsRv.setLayoutManager(new CustomLinearLayoutManager(getContext()));
            mHemodynamicsRv.setAdapter(mHemodynamicsAdapter);
            mHemodynamicsLl.setVisibility(VISIBLE);
        }

        // BloodGases Part
        if (HAVE_BLOOD_GASES_POSITION.size() != 0) {
            mBloodGasesAdapter = new ParamItemAdapter(HAVE_BLOOD_GASES_POSITION, mode);
            mBloodGasesAdapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
                @Override
                public void onParamItemClick(int position) {
                    if (onParamClickListener != null && position < HAVE_BLOOD_GASES_POSITION.size()) {
                        onParamClickListener.onParamClick(HAVE_BLOOD_GASES_POSITION.get(position));
                    }
                }
            });
            mBloodGasesRv.setHasFixedSize(true);
            mBloodGasesRv.setLayoutManager(new CustomLinearLayoutManager(getContext()));
            mBloodGasesRv.setAdapter(mBloodGasesAdapter);
            //mBloodGasesRv.addItemDecoration(mRecyclerViewDivider);
            mBloodGasesLl.setVisibility(VISIBLE);
        }

        // Hematology Part
        if (HAVE_HEMATOLOGY_POSITION.size() != 0) {
            mHematologyAdapter = new ParamItemAdapter(HAVE_HEMATOLOGY_POSITION, mode);
            mHematologyAdapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
                @Override
                public void onParamItemClick(int position) {
                    if (onParamClickListener != null && position < HAVE_HEMATOLOGY_POSITION.size()) {
                        onParamClickListener.onParamClick(HAVE_HEMATOLOGY_POSITION.get(position));
                    }
                }
            });
            mHematologyRv.setHasFixedSize(true);
            mHematologyRv.setLayoutManager(new CustomLinearLayoutManager(getContext()));
            mHematologyRv.setAdapter(mHematologyAdapter);
            //mHematologyRv.addItemDecoration(mRecyclerViewDivider);
            mHematologyLl.setVisibility(VISIBLE);
        }

        // Biochemistry Part
        if (HAVE_BIOCHEMISTRY_POSITION.size() != 0) {
            mBiochemistryAdapter = new ParamItemAdapter(HAVE_BIOCHEMISTRY_POSITION, mode);
            mBiochemistryAdapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
                @Override
                public void onParamItemClick(int position) {
                    if (onParamClickListener != null && position < HAVE_BIOCHEMISTRY_POSITION.size()) {
                        onParamClickListener.onParamClick(HAVE_BIOCHEMISTRY_POSITION.get(position));
                    }
                }
            });
            mBiochemistryRv.setHasFixedSize(true);
            mBiochemistryRv.setLayoutManager(new CustomLinearLayoutManager(getContext()));
            mBiochemistryRv.setAdapter(mBiochemistryAdapter);
            //mBiochemistryRv.addItemDecoration(mRecyclerViewDivider);
            mBiochemistryLl.setVisibility(VISIBLE);
        }
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void notifyDataSetChanged() {
        if (mHemodynamicsAdapter != null) {
            mHemodynamicsAdapter.notifyDataSetChanged();
        }
        if (mBloodGasesAdapter != null) {
            mBloodGasesAdapter.notifyDataSetChanged();
        }
        if (mHematologyAdapter != null) {
            mHematologyAdapter.notifyDataSetChanged();
        }
        if (mBiochemistryAdapter != null) {
            mBiochemistryAdapter.notifyDataSetChanged();
        }
    }

    public boolean isInitialized() {
        return capabilities != null;
    }

    private class ParamItemAdapter extends RecyclerView.Adapter {

        private OnRecyclerViewListener onRecyclerViewListener;

        public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
            this.onRecyclerViewListener = onRecyclerViewListener;
        }

        private ArrayList<Integer> position;

        private int mode;

        public ParamItemAdapter(ArrayList<Integer> position, int mode) {
            this.position = position;
            this.mode = mode;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.measurement_group_cards_item, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new ParamItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

            if ((mBiochemistryAdapter == null) || (mBiochemistryAdapter.getItemCount() == 0)) {
                mBiochemistryLl.setVisibility(GONE);
            } else {
                mBiochemistryLl.setVisibility(VISIBLE);
            }

            if ((mHematologyAdapter == null) || (mHematologyAdapter.getItemCount() == 0)) {
                mHematologyLl.setVisibility(GONE);
            } else {
                mHematologyLl.setVisibility(VISIBLE);
            }

            if ((mHemodynamicsAdapter == null) || (mHemodynamicsAdapter.getItemCount() == 0)) {
                mHemodynamicsLl.setVisibility(GONE);
            } else {
                mHemodynamicsLl.setVisibility(VISIBLE);
            }

            if ((mBloodGasesAdapter == null) || (mBloodGasesAdapter.getItemCount() == 0)) {
                mBloodGasesLl.setVisibility(GONE);
            } else {
                mBloodGasesLl.setVisibility(VISIBLE);
            }

            ParamItemViewHolder holder = (ParamItemViewHolder) viewHolder;
            holder.paramItemView.setParamType(sContext, position.get(i), mode);
        }

        @Override
        public int getItemCount() {
            return position == null ? 0 : position.size();
        }

        private class ParamItemViewHolder extends RecyclerView.ViewHolder implements
                OnClickListener {
            boolean init;

            View root;

            MeasurementParamItemView paramItemView;

            ParamItemViewHolder(View itemView) {
                super(itemView);
                init = false;

                root = itemView.findViewById(R.id.params_param_item_root);
                root.setOnClickListener(this);
                paramItemView = (MeasurementParamItemView) itemView.findViewById(R.id.measurement_param_item_view);
            }

            @Override
            public void onClick(View v) {
                int pos = getAdapterPosition();
                if (onRecyclerViewListener != null && 0 <= pos) {
                    onRecyclerViewListener.onParamItemClick(pos);
                }
            }
        }

    }

    /**
     * External interface
     */
    public interface OnParamClickListener {
        void onParamClick(int id);
    }

    /**
     * Internal interface， between ParamsGroupCards and sub RecyclerViews
     */
    private interface OnRecyclerViewListener {
        void onParamItemClick(int position);
    }

}