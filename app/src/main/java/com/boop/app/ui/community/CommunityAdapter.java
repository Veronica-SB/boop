package com.boop.app.ui.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.boop.app.R;
import com.boop.app.data.model.CommunityPost;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.PostViewHolder> {

    private final List<CommunityPost> posts;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CommunityAdapter(List<CommunityPost> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_community_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        CommunityPost post = posts.get(position);

        holder.tvUserName.setText(post.getUserName());
        holder.tvContent.setText(post.getContent());
        holder.tvLikes.setText("❤️ " + post.getLikes());
        holder.tvComments.setText("💬 " + post.getComments());
        holder.tvTime.setText(getRelativeTime(post.getTimestamp()));

        if ("question".equals(post.getType())) {
            holder.tvType.setText("❓ Pregunta");
            holder.tvType.setTextColor(holder.itemView.getContext().getColor(R.color.alert_red));
            holder.tvType.setBackgroundResource(R.drawable.bg_pill_orange_light);
        } else if ("adoption".equals(post.getType())) {
            holder.tvType.setText("🐾 Adopción");
            holder.tvType.setTextColor(holder.itemView.getContext().getColor(R.color.boop_green_dark));
            holder.tvType.setBackgroundResource(R.drawable.bg_pill_green_light);
        } else if ("exchange".equals(post.getType())) {
            holder.tvType.setText("🔄 Intercambio");
            holder.tvType.setTextColor(holder.itemView.getContext().getColor(R.color.text_secondary_light));
            holder.tvType.setBackgroundResource(R.drawable.bg_pill_green_light);
        }


        if ("dog".equals(post.getUserIconType())) holder.ivUserIcon.setImageResource(R.drawable.ic_dog);
        else holder.ivUserIcon.setImageResource(R.drawable.ic_cat);

        if (post.getLocation() != null && !post.getLocation().isEmpty()) {
            holder.tvExtra.setVisibility(View.VISIBLE);
            holder.tvExtra.setText("📍 " + post.getLocation());
        } else {
            holder.tvExtra.setVisibility(View.GONE);
        }

        holder.tvLikes.setOnClickListener(v -> {
            int nuevoLikes = post.getLikes() + 1;
            post.setLikes(nuevoLikes);
            holder.tvLikes.setText("❤️ " + nuevoLikes);
            db.collection("community_posts")
                    .document(post.getId())
                    .update("likes", FieldValue.increment(1))
                    .addOnFailureListener(e -> {
                        post.setLikes(nuevoLikes - 1);
                        holder.tvLikes.setText("❤️ " + (nuevoLikes - 1));
                    });
        });
    }

    @Override
    public int getItemCount() { return posts.size(); }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvTime, tvType, tvContent, tvExtra, tvComments, tvLikes;
        ImageView ivUserIcon;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvTime     = itemView.findViewById(R.id.tv_post_time);
            tvType     = itemView.findViewById(R.id.tv_post_type);
            tvContent  = itemView.findViewById(R.id.tv_post_content);
            tvExtra    = itemView.findViewById(R.id.tv_post_extra);
            tvComments = itemView.findViewById(R.id.tv_comments_count);
            tvLikes    = itemView.findViewById(R.id.tv_likes_count);
            ivUserIcon = itemView.findViewById(R.id.iv_user_icon);
        }
    }
    private String getRelativeTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        if (diff < 60_000)                    return "Hace un momento";
        if (diff < 3_600_000)                 return "Hace " + (diff / 60_000) + " min";
        if (diff < 86_400_000)                return "Hace " + (diff / 3_600_000) + " h";
        if (diff < 7 * 86_400_000L)           return "Hace " + (diff / 86_400_000) + " días";
        return new java.text.SimpleDateFormat("dd/MM/yyyy",
                java.util.Locale.getDefault()).format(new java.util.Date(timestamp));
    }
}
