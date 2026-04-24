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
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.PostViewHolder> {

    private List<CommunityPost> posts;

    public CommunityAdapter(List<CommunityPost> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        CommunityPost post = posts.get(position);
        holder.tvUserName.setText(post.getUserName());
        holder.tvContent.setText(post.getContent());
        holder.tvLikes.setText("❤️ " + post.getLikes());
        holder.tvComments.setText("💬 " + post.getComments());

        // Set type badge
        if ("question".equals(post.getType())) {
            holder.tvType.setText("❓ Pregunta");
            holder.tvType.setTextColor(0xFFE65100);
            holder.tvType.setBackgroundResource(R.drawable.bg_pill_orange_light);
        } else if ("adoption".equals(post.getType())) {
            holder.tvType.setText("🐾 Adopción");
            holder.tvType.setTextColor(0xFF2E7D32);
            holder.tvType.setBackgroundResource(R.drawable.bg_pill_green_light);
        } else if ("exchange".equals(post.getType())) {
            holder.tvType.setText("🔄 Intercambio");
            holder.tvType.setTextColor(0xFF6A1B9A);
            holder.tvType.setBackgroundResource(R.drawable.bg_pill_purple_light);
        }

        // Icon
        if ("dog".equals(post.getUserIconType())) holder.ivUserIcon.setImageResource(R.drawable.ic_dog);
        else holder.ivUserIcon.setImageResource(R.drawable.ic_cat);

        // Extra info
        if (post.getLocation() != null && !post.getLocation().isEmpty()) {
            holder.tvExtra.setVisibility(View.VISIBLE);
            holder.tvExtra.setText("📍 " + post.getLocation());
        } else {
            holder.tvExtra.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvTime, tvType, tvContent, tvExtra, tvComments, tvLikes;
        ImageView ivUserIcon;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvTime = itemView.findViewById(R.id.tv_post_time);
            tvType = itemView.findViewById(R.id.tv_post_type);
            tvContent = itemView.findViewById(R.id.tv_post_content);
            tvExtra = itemView.findViewById(R.id.tv_post_extra);
            tvComments = itemView.findViewById(R.id.tv_comments_count);
            tvLikes = itemView.findViewById(R.id.tv_likes_count);
            ivUserIcon = itemView.findViewById(R.id.iv_user_icon);
        }
    }
}
