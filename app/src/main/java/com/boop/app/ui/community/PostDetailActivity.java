package com.boop.app.ui.community;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.boop.app.R;
import com.boop.app.data.model.CommunityComment;
import com.boop.app.data.model.CommunityPost;
import com.boop.app.utils.ThemeManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POST_ID = "post_id";

    private FirebaseFirestore db;
    private String currentUserId;
    private String postId;
    private CommunityPost currentPost;
    private LinearLayout containerComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        db            = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        postId        = getIntent().getStringExtra(EXTRA_POST_ID);
        containerComments = findViewById(R.id.container_comments);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_send_comment).setOnClickListener(v -> sendComment());

        loadPost();
        loadComments();
    }

    // ── Cargar post ───────────────────────────────────────────────

    private void loadPost() {
        db.collection("community_posts").document(postId).get()
                .addOnSuccessListener(doc -> {
                    currentPost = doc.toObject(CommunityPost.class);
                    if (currentPost == null) return;
                    currentPost.setId(doc.getId());
                    bindPost(currentPost);
                });
    }

    private void bindPost(CommunityPost post) {
        // Icono
        ImageView ivIcon = findViewById(R.id.iv_user_icon);
        if ("dog".equals(post.getUserIconType()))
            ivIcon.setImageResource(R.drawable.ic_dog);
        else if ("rabbit".equals(post.getUserIconType()))
            ivIcon.setImageResource(R.drawable.ic_rabbit);
        else if ("bird".equals(post.getUserIconType()))
            ivIcon.setImageResource(R.drawable.ic_bird);
        else
            ivIcon.setImageResource(R.drawable.ic_cat);

        ((TextView) findViewById(R.id.tv_user_name)).setText(post.getUserName());
        ((TextView) findViewById(R.id.tv_post_content)).setText(post.getContent());
        ((TextView) findViewById(R.id.tv_post_time)).setText(getRelativeTime(post.getTimestamp()));

        // Tipo
        TextView tvType = findViewById(R.id.tv_post_type);
        if ("question".equals(post.getType())) {
            tvType.setText("❓ Pregunta");
            tvType.setTextColor(getColor(R.color.alert_red));
        } else if ("adoption".equals(post.getType())) {
            tvType.setText("🐾 Adopción");
            tvType.setTextColor(getColor(R.color.boop_green_dark));
        } else {
            tvType.setText("🔄 Intercambio");
            tvType.setTextColor(getColor(R.color.text_secondary_light));
        }

        // Mostrar botón eliminar solo si es el autor
        if (currentUserId.equals(post.getUserId())) {
            TextView btnDelete = findViewById(R.id.btn_delete_post);
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("Eliminar publicación")
                            .setMessage("¿Seguro que quieres eliminar esta publicación?")
                            .setPositiveButton("Eliminar", (dialog, which) ->
                                    db.collection("community_posts").document(postId)
                                            .delete()
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(this, "Publicación eliminada", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                            )
                            .setNegativeButton("Cancelar", null)
                            .show()
            );
        }
    }

    // ── Cargar comentarios ────────────────────────────────────────

    private void loadComments() {
        db.collection("community_posts").document(postId)
                .collection("comments")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(snapshots -> {
                    containerComments.removeAllViews();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        CommunityComment comment = doc.toObject(CommunityComment.class);
                        comment.setId(doc.getId());
                        containerComments.addView(buildCommentCard(comment));
                    }
                });
    }

    // ── Enviar comentario ─────────────────────────────────────────

    private void sendComment() {
        EditText etComment = findViewById(R.id.et_comment);
        String content = etComment.getText().toString().trim();

        if (TextUtils.isEmpty(content)) {
            etComment.setError("Escribe algo antes de enviar");
            return;
        }

        // Leer nombre del usuario desde Firestore
        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(userDoc -> {
                    String name = userDoc.getString("name");

                    Map<String, Object> comment = new HashMap<>();
                    comment.put("authorId",   currentUserId);
                    comment.put("authorName", name != null ? name : "Usuario");
                    comment.put("content",    content);
                    comment.put("timestamp",  System.currentTimeMillis());

                    db.collection("community_posts").document(postId)
                            .collection("comments")
                            .add(comment)
                            .addOnSuccessListener(ref -> {
                                // Actualizar contador de comentarios
                                db.collection("community_posts").document(postId)
                                        .update("comments",
                                                com.google.firebase.firestore.FieldValue.increment(1));
                                etComment.setText("");
                                loadComments();
                                Toast.makeText(this, "Comentario publicado", Toast.LENGTH_SHORT).show();
                            });
                });
    }

    // ── Construir card de comentario ──────────────────────────────

    private View buildCommentCard(CommunityComment comment) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dpToPx(8);
        card.setLayoutParams(cardParams);
        card.setRadius(dpToPx(12));
        card.setCardElevation(dpToPx(1));
        card.setCardBackgroundColor(getColor(R.color.surface_light));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        card.addView(content);

        // Autor y tiempo
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        headerParams.bottomMargin = dpToPx(4);
        header.setLayoutParams(headerParams);

        TextView tvAuthor = new TextView(this);
        tvAuthor.setText(comment.getAuthorName());
        tvAuthor.setTypeface(null, android.graphics.Typeface.BOLD);
        tvAuthor.setTextSize(12);
        tvAuthor.setTextColor(getColor(R.color.text_primary_light));
        LinearLayout.LayoutParams authorParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvAuthor.setLayoutParams(authorParams);
        header.addView(tvAuthor);

        TextView tvTime = new TextView(this);
        tvTime.setText(getRelativeTime(comment.getTimestamp()));
        tvTime.setTextSize(10);
        tvTime.setTextColor(getColor(R.color.text_tertiary_light));
        header.addView(tvTime);
        content.addView(header);

        // Contenido
        TextView tvContent = new TextView(this);
        tvContent.setText(comment.getContent());
        tvContent.setTextSize(13);
        tvContent.setTextColor(getColor(R.color.text_primary_light));
        tvContent.setLineSpacing(0, 1.3f);
        content.addView(tvContent);

        // Botón eliminar si es el autor
        if (currentUserId.equals(comment.getAuthorId())) {
            TextView btnDelete = new TextView(this);
            btnDelete.setText("Eliminar");
            btnDelete.setTextSize(11);
            btnDelete.setTextColor(getColor(R.color.alert_red));
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            deleteParams.topMargin = dpToPx(4);
            btnDelete.setLayoutParams(deleteParams);
            btnDelete.setClickable(true);
            btnDelete.setFocusable(true);
            btnDelete.setOnClickListener(v ->
                    db.collection("community_posts").document(postId)
                            .collection("comments").document(comment.getId())
                            .delete()
                            .addOnSuccessListener(unused -> {
                                db.collection("community_posts").document(postId)
                                        .update("comments",
                                                com.google.firebase.firestore.FieldValue.increment(-1));
                                loadComments();
                            })
            );
            content.addView(btnDelete);
        }

        return card;
    }

    private String getRelativeTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        if (diff < 60_000)           return "Hace un momento";
        if (diff < 3_600_000)        return "Hace " + (diff / 60_000) + " min";
        if (diff < 86_400_000)       return "Hace " + (diff / 3_600_000) + " h";
        if (diff < 7 * 86_400_000L)  return "Hace " + (diff / 86_400_000) + " días";
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(new Date(timestamp));
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
