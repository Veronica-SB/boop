package com.boop.app.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.boop.app.R;
import com.google.android.material.button.MaterialButton;

public class OnboardingStepFragment extends Fragment {

    private static final String ARG_STEP = "step";

    public static OnboardingStepFragment newInstance(int step) {
        OnboardingStepFragment f = new OnboardingStepFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STEP, step);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding_step, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int step = getArguments() != null ? getArguments().getInt(ARG_STEP, 1) : 1;

        TextView tvStepLabel  = view.findViewById(R.id.tv_step_label);
        TextView tvSkip       = view.findViewById(R.id.tv_skip);
        ImageView illustration = view.findViewById(R.id.ob_illustration);
        TextView tvTitle      = view.findViewById(R.id.ob_title);
        TextView tvBody       = view.findViewById(R.id.ob_body);
        MaterialButton btnNext = view.findViewById(R.id.btn_next);
        View dot1 = view.findViewById(R.id.dot1);
        View dot2 = view.findViewById(R.id.dot2);
        View dot3 = view.findViewById(R.id.dot3);

        // Content por paso
        tvStepLabel.setText(step + " de 3");

        switch (step) {
            case 1:
                illustration.setImageResource(R.drawable.ic_dog);
                tvTitle.setText(getString(R.string.ob1_title));
                tvBody.setText(getString(R.string.ob1_body));
                btnNext.setText("Siguiente →");
                activateDot(dot1, dot2, dot3, 1);
                break;
            case 2:
                illustration.setImageResource(R.drawable.ic_cat);
                tvTitle.setText(getString(R.string.ob2_title));
                tvBody.setText(getString(R.string.ob2_body));
                btnNext.setText("Siguiente →");
                activateDot(dot1, dot2, dot3, 2);
                break;
            case 3:
                illustration.setImageResource(R.drawable.ic_rabbit);
                tvTitle.setText(getString(R.string.ob3_title));
                tvBody.setText(getString(R.string.ob3_body));
                btnNext.setText("¡Empezar!");
                activateDot(dot1, dot2, dot3, 3);
                break;
        }

        // Botones
        btnNext.setOnClickListener(v -> {
            if (getActivity() instanceof OnboardingActivity) {
                ((OnboardingActivity) getActivity()).goToNextStep();
            }
        });

        tvSkip.setOnClickListener(v -> {
            if (getActivity() instanceof OnboardingActivity) {
                ((OnboardingActivity) getActivity()).skipOnboarding();
            }
        });
    }

    private void activateDot(View d1, View d2, View d3, int active) {
        // Activo = ancho 24dp, inactivo = 8dp
        int activePx  = dpToPx(24);
        int inactivePx = dpToPx(8);
        int activeDrawable   = R.drawable.bg_ob_dot_active;
        int inactiveDrawable = R.drawable.bg_ob_dot_inactive;

        setDot(d1, active == 1 ? activePx : inactivePx, active == 1 ? activeDrawable : inactiveDrawable);
        setDot(d2, active == 2 ? activePx : inactivePx, active == 2 ? activeDrawable : inactiveDrawable);
        setDot(d3, active == 3 ? activePx : inactivePx, active == 3 ? activeDrawable : inactiveDrawable);
    }

    private void setDot(View dot, int widthPx, int drawableRes) {
        ViewGroup.LayoutParams lp = dot.getLayoutParams();
        lp.width = widthPx;
        dot.setLayoutParams(lp);
        dot.setBackgroundResource(drawableRes);
    }

    private int dpToPx(int dp) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
