package com.fekracomputers.islamiclibrary.reading;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.fekracomputers.islamiclibrary.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPrefDialogInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisplayOptionsPopupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayOptionsPopupFragment extends DialogFragment {

    public static final String TAG_FRAGMENT_DISPLAY_OPTIONS = "fragment_display_options";
    public static final int LAYOUT_OPTIONS = 0;
    public static final int BRIGHTNESS_OPTIONS = 1;
    private static final String ARG_TYPE = "TYPE";
    private static final String ARA_INITIAL_ZOOM = "initial_zoom";
    /**
     * 0 : layout options
     * 1 : brightness
     */
    private int mCurrentView;
    private OnPrefDialogInteractionListener mOnPrefDialogInteractionListener;
    private TextView prefTextSizeTV;

    public DisplayOptionsPopupFragment() {
        // Required empty public constructor
    }


    /**
     * @param type 0 : layout options
     *             1 : brightness
     */
    public static DisplayOptionsPopupFragment newInstance(int type, int initialZoom) {
        DisplayOptionsPopupFragment fragment = new DisplayOptionsPopupFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        args.putInt(ARA_INITIAL_ZOOM, initialZoom);

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        int mInitialZoom = 100;
        if (getArguments() != null) {
            mCurrentView = getArguments().getInt(ARG_TYPE);
            mInitialZoom = getArguments().getInt(ARA_INITIAL_ZOOM);
        }

        final ImageButton LightningOptionsBtn = view.findViewById(R.id.lighting_options_button);
        final ImageButton LayoutOptionsBtn = view.findViewById(R.id.layout_options_button);
        final ViewAnimator viewAnimator = view.findViewById(R.id.settings_view_container);

        viewAnimator.setDisplayedChild(mCurrentView);
        LayoutOptionsBtn.setSelected(mCurrentView == 0);
        LightningOptionsBtn.setSelected(mCurrentView == 1);


        LightningOptionsBtn.setOnClickListener(v -> {
            if (mCurrentView != 1) {
                viewAnimator.setDisplayedChild(1);
                mCurrentView = 1;
                v.setSelected(true);
                LayoutOptionsBtn.setSelected(false);
            }

        });
        LayoutOptionsBtn.setOnClickListener(v -> {
            if (mCurrentView != 0) {
                viewAnimator.setDisplayedChild(0);
                mCurrentView = 0;
                LayoutOptionsBtn.setSelected(true);
                LightningOptionsBtn.setSelected(false);
            }

        });


        final ViewGroup prefTextSize = viewAnimator.findViewById(R.id.pref_text_size);

        prefTextSizeTV = prefTextSize.findViewById(R.id.setting_text);
        prefTextSizeTV.setText(getString(R.string.pref_zoom_precent, mInitialZoom));
        prefTextSize.findViewById(R.id.button_plus).setOnClickListener(new View.OnClickListener() {

            final int update_sign = +1;

            @Override
            public void onClick(View v) {
                changeZoom(update_sign);
            }
        });

        prefTextSize.findViewById(R.id.button_minus).setOnClickListener(new View.OnClickListener() {
            final int update_sign = -1;

            @Override
            public void onClick(View v) {
                changeZoom(update_sign);
            }
        });

        final SwitchCompat nightModeSwitch = viewAnimator.findViewById(R.id.pref_night_mode);
        final SwitchCompat tashkeelSwitch = viewAnimator.findViewById(R.id.pref_tashkeel);
        final SwitchCompat PinchZoomSwitch = viewAnimator.findViewById(R.id.pref_pinch_zoom);
        //nightModeSwitch.setOnCheckedChangeListener(null);
        nightModeSwitch.setChecked(mOnPrefDialogInteractionListener.isThemeNightMode());
        tashkeelSwitch.setChecked(mOnPrefDialogInteractionListener.isTashkeel());
        PinchZoomSwitch.setChecked(mOnPrefDialogInteractionListener.isPinchZoom());


        nightModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                mOnPrefDialogInteractionListener.setThemeNightMode(nightModeSwitch.isChecked()));
        tashkeelSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                mOnPrefDialogInteractionListener.setTashkeel(tashkeelSwitch.isChecked()));

        PinchZoomSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                mOnPrefDialogInteractionListener.setPinchZoom(PinchZoomSwitch.isChecked()));


        final ViewGroup prefTheme = viewAnimator.findViewById(R.id.pref_theme);
        ImageButton buttonDay = prefTheme.findViewById(R.id.button_day);
        ImageButton buttonSepia = prefTheme.findViewById(R.id.button_sepia);
        ImageButton buttonNight = prefTheme.findViewById(R.id.button_night);

        View.OnClickListener themeImageButtonOnClickListener = v -> {
            boolean isDesiredThemeNightMode = Integer.valueOf((String) v.getTag()) == 0;

            if (mOnPrefDialogInteractionListener.isThemeNightMode() ^ isDesiredThemeNightMode)//Not equal ;)
            {
                mOnPrefDialogInteractionListener.setThemeNightMode(isDesiredThemeNightMode);
                v.setSelected(true);
            }
        };

        buttonDay.setOnClickListener(themeImageButtonOnClickListener);
        buttonSepia.setOnClickListener(themeImageButtonOnClickListener);
        buttonNight.setOnClickListener(themeImageButtonOnClickListener);

    }

    public void changeZoom(int direction) {
        int new_zoom = mOnPrefDialogInteractionListener.zoomUpdatedByPercent(direction * 5);
        prefTextSizeTV.setText(getString(R.string.pref_zoom_precent, new_zoom));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.display_options_popup, container);
        final ViewAnimator viewAnimator = v.findViewById(R.id.settings_view_container);
        viewAnimator.setDisplayedChild(mCurrentView);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.TOP | Gravity.END);
            // window.setDimAmount(0);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            //getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPrefDialogInteractionListener) {
            mOnPrefDialogInteractionListener = (OnPrefDialogInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPrefDialogInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnPrefDialogInteractionListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPrefDialogInteractionListener {
        /**
         * @param percent the update percent in the zoom
         * @return the new zoom
         */
        int zoomUpdatedByPercent(int percent);

        boolean isThemeNightMode();

        void setThemeNightMode(boolean isDesiredThemeNight);

        boolean isTashkeel();

        void setTashkeel(boolean checked);

        boolean isPinchZoom();

        void setPinchZoom(boolean checked);
    }
}
