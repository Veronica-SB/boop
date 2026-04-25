package com.boop.app.ui.community;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {

    private CommunityAdapter adapter;
    private final List<CommunityPost> allPosts = new ArrayList<>();
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;
    private String currentFilter = "all";

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

        db = FirebaseFirestore.getInstance();

        view.findViewById(R.id.btn_back_community).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.homeFragment));

        view.findViewById(R.id.btn_new_post).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CreatePostActivity.class)));

        RecyclerView rvFeed = view.findViewById(R.id.rv_community_feed);
        rvFeed.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CommunityAdapter(allPosts);
        rvFeed.setAdapter(adapter);

        TextView tabAll       = view.findViewById(R.id.tab_all);
        TextView tabQuestions = view.findViewById(R.id.tab_questions);
        TextView tabAdoption  = view.findViewById(R.id.tab_adoption);
        TextView tabExchange  = view.findViewById(R.id.tab_exchange);

        View.OnClickListener tabListener = v -> {
            for (TextView tab : new TextView[]{tabAll, tabQuestions, tabAdoption, tabExchange}) {
                tab.setBackgroundResource(R.drawable.bg_tab_inactive);
                tab.setTextColor(requireContext().getColor(R.color.text_secondary_light));
            }
            ((TextView) v).setBackgroundResource(R.drawable.bg_tab_active);
            ((TextView) v).setTextColor(requireContext().getColor(R.color.boop_green));

            int id = v.getId();
            if      (id == R.id.tab_questions) currentFilter = "question";
            else if (id == R.id.tab_adoption)  currentFilter = "adoption";
            else if (id == R.id.tab_exchange)  currentFilter = "exchange";
            else                               currentFilter = "all";

            listenToPosts();
        };

        tabAll.setOnClickListener(tabListener);
        tabQuestions.setOnClickListener(tabListener);
        tabAdoption.setOnClickListener(tabListener);
        tabExchange.setOnClickListener(tabListener);

        listenToPosts();
    }

    private void listenToPosts() {
        if (listenerRegistration != null) listenerRegistration.remove();

        Query query = db.collection("community_posts")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        if (!"all".equals(currentFilter))
            query = query.whereEqualTo("type", currentFilter);

        listenerRegistration = query.addSnapshotListener((snapshots, error) -> {
            if (error != null) { Log.e("CommunityFragment", "Error", error); return; }
            if (snapshots == null) return;

            allPosts.clear();
            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                CommunityPost post = doc.toObject(CommunityPost.class);
                if (post != null) {
                    post.setId(doc.getId());
                    allPosts.add(post);
                }
            }
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (listenerRegistration != null) listenerRegistration.remove();
    }
}
