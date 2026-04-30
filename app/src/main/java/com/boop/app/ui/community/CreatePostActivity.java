package com.boop.app.ui.community;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.R;
import com.boop.app.data.model.CommunityPost;
import com.boop.app.data.model.Pet;
import com.boop.app.utils.ThemeManager;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreatePostActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private EditText etContent;
    private ChipGroup chipGroupCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        db            = FirebaseFirestore.getInstance();
        currentUser   = FirebaseAuth.getInstance().getCurrentUser();
        etContent     = findViewById(R.id.et_post_content);
        chipGroupCategory = findViewById(R.id.chip_group_category);

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());
        findViewById(R.id.btn_publish).setOnClickListener(v -> publishPost());
        findViewById(R.id.btn_publish_large).setOnClickListener(v -> publishPost());
    }

    private void publishPost() {
        String content = etContent.getText().toString().trim();
        String type    = getSelectedType();

        if (TextUtils.isEmpty(content)) {
            etContent.setError("El mensaje no puede estar vacío");
            etContent.requestFocus();
            return;
        }
        if (currentUser == null) {
            Toast.makeText(this, "Debes iniciar sesión para publicar", Toast.LENGTH_SHORT).show();
            return;
        }

        CommunityPost post = new CommunityPost();
        post.setUserId(currentUser.getUid());
        post.setType(type);
        post.setContent(content);
        post.setTimestamp(System.currentTimeMillis());
        post.setLikes(0);
        post.setComments(0);

// Leer nombre desde Firestore
        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(userDoc -> {
                    String name = userDoc.getString("name");
                    post.setUserName(name != null && !name.isEmpty() ? name : "Usuario");

                    // Leer especie de la primera mascota para el icono
                    db.collection("pets")
                            .whereEqualTo("ownerId", currentUser.getUid())
                            .limit(1)
                            .get()
                            .addOnSuccessListener(pets -> {
                                String iconType = "dog";
                                if (!pets.isEmpty()) {
                                    String species = pets.getDocuments().get(0).getString("species");
                                    if (Pet.PetSpecies.CAT.name().equals(species))         iconType = "cat";
                                    else if (Pet.PetSpecies.RABBIT.name().equals(species)) iconType = "rabbit";
                                    else if (Pet.PetSpecies.BIRD.name().equals(species))   iconType = "bird";
                                }
                                post.setUserIconType(iconType);
                                savePost(post);
                            })
                            .addOnFailureListener(e -> {
                                post.setUserIconType("dog");
                                savePost(post);
                            });
                })
                .addOnFailureListener(e -> {
                    post.setUserName("Usuario");
                    post.setUserIconType("dog");
                    savePost(post);
                });
    }

    private void savePost(CommunityPost post) {
        db.collection("community_posts")
                .add(post)
                .addOnSuccessListener(ref -> {
                    ref.update("id", ref.getId());
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al publicar. Inténtalo de nuevo.",
                                Toast.LENGTH_SHORT).show());
    }

    private String getSelectedType() {
        int checkedId = chipGroupCategory.getCheckedChipId();
        if (checkedId == R.id.chip_question) return "question";
        if (checkedId == R.id.chip_adoption) return "adoption";
        if (checkedId == R.id.chip_exchange) return "exchange";
        return "question";
    }
}