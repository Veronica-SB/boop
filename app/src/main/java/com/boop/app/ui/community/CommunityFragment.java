package com.boop.app.ui.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;
import com.boop.app.R;
import com.boop.app.data.model.CommunityPost;
import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {

    private CommunityAdapter adapter;
    private final List<CommunityPost> allPosts = new ArrayList<>();

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

        // Botón atrás (vuelve al Home)
        view.findViewById(R.id.btn_back_community).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.homeFragment);
        });

        // Botón nueva publicación
        view.findViewById(R.id.btn_new_post).setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), CreatePostActivity.class));
        });

        // Setup RecyclerView
        RecyclerView rvFeed = view.findViewById(R.id.rv_community_feed);
        rvFeed.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        loadMockData();
        adapter = new CommunityAdapter(allPosts);
        rvFeed.setAdapter(adapter);

        // Tab switching — maqueta funcional
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

            // Filtrar (Simulación)
            filterPosts(v.getId());
        };

        tabAll.setOnClickListener(tabListener);
        tabQuestions.setOnClickListener(tabListener);
        tabAdoption.setOnClickListener(tabListener);
        tabExchange.setOnClickListener(tabListener);
    }

    private void loadMockData() {
        if (!allPosts.isEmpty()) return; // Evitar duplicados al volver al fragment

        allPosts.add(new CommunityPost("1", "u1", "Ana M.", "cat", "question", 
            "Mi gata lleva 2 días sin comer bien. ¿Habéis probado NutriPlus?", System.currentTimeMillis()));
        allPosts.get(0).setLikes(14);
        allPosts.get(0).setComments(8);

        allPosts.add(new CommunityPost("2", "u2", "Carlos V.", "dog", "exchange", 
            "Arnés Ruffwear talla M en perfecto estado. Lo dono a quien lo necesite.", System.currentTimeMillis()));
        allPosts.get(1).setLikes(22);
        allPosts.get(1).setComments(3);

        allPosts.add(new CommunityPost("3", "u3", "Protectora SOS", "dog", "adoption", 
            "Max busca un hogar responsable. Es muy cariñoso y juguetón.", System.currentTimeMillis()));
        allPosts.get(2).setLocation("L'Hospitalet");
        allPosts.get(2).setLikes(45);
        allPosts.get(2).setComments(12);
    }

    private void filterPosts(int tabId) {
        List<CommunityPost> filtered = new ArrayList<>();
        String targetType = "";
        if (tabId == R.id.tab_questions) targetType = "question";
        else if (tabId == R.id.tab_adoption) targetType = "adoption";
        else if (tabId == R.id.tab_exchange) targetType = "exchange";

        if (targetType.isEmpty()) {
            filtered.addAll(allPosts);
        } else {
            for (CommunityPost p : allPosts) {
                if (targetType.equals(p.getType())) filtered.add(p);
            }
        }
        
        adapter = new CommunityAdapter(filtered);
        View root = getView();
        if (root != null) {
            RecyclerView rvFeed = root.findViewById(R.id.rv_community_feed);
            rvFeed.setAdapter(adapter);
        }
    }
}
