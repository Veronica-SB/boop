package com.boop.app.ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.boop.app.R;

public class CommunityFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tab switching — maqueta visual
        TextView tabAll = view.findViewById(R.id.tab_all);
        TextView tabQuestions = view.findViewById(R.id.tab_questions);
        TextView tabAdoption = view.findViewById(R.id.tab_adoption);
        TextView tabExchange = view.findViewById(R.id.tab_exchange);

        View.OnClickListener tabListener = v -> {
            // Reset all
            for (TextView tab : new TextView[]{tabAll, tabQuestions, tabAdoption, tabExchange}) {
                tab.setBackgroundResource(R.drawable.bg_tab_inactive);
                tab.setTextColor(requireContext().getColor(R.color.text_secondary_light));
            }
            // Set active
            TextView active = (TextView) v;
            active.setBackgroundResource(R.drawable.bg_tab_active);
            active.setTextColor(requireContext().getColor(R.color.boop_green));
        };

        tabAll.setOnClickListener(tabListener);
        tabQuestions.setOnClickListener(tabListener);
        tabAdoption.setOnClickListener(tabListener);
        tabExchange.setOnClickListener(tabListener);

        // TODO: Cargar feed desde base de datos
    }
}
