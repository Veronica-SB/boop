package com.boop.app.ui.auth;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * AuthRepository — único punto de contacto con Firebase para autenticación.
 * No sabe nada de UI. Solo habla con Firebase y devuelve el resultado
 * a través de callbacks.
 */
public class AuthRepository {

    // ── Interfaz de callback genérica ────────────────────────────────────────
    // El ViewModel le pasa una implementación de esta interfaz y el Repository
    // la llama cuando Firebase responde (éxito o error).

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String errorMessage);
    }

    // ── Instancias de Firebase ────────────────────────────────────────────────

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public AuthRepository() {
        this.auth = FirebaseAuth.getInstance();
        this.db   = FirebaseFirestore.getInstance();
    }

    // ── Login con email y contraseña ─────────────────────────────────────────

    public void signIn(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(result.getUser()))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ── Registro con email, contraseña y nombre ──────────────────────────────

    public void register(String email, String password, String name, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = result.getUser();
                    if (user == null) {
                        callback.onError("Error al crear el usuario");
                        return;
                    }
                    // Guardar el perfil del usuario en Firestore
                    // La colección se llama "users" (inglés, según convenciones)
                    saveUserToFirestore(user, name, callback);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ── Recuperar contraseña ─────────────────────────────────────────────────

    public void sendPasswordReset(String email, AuthCallback callback) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> callback.onSuccess(null)) // null: no hay usuario en este caso
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ── Google Sign-In ───────────────────────────────────────────────────────
    // La Activity lanza el selector de cuentas de Google y obtiene un idToken.
    // Este método recibe ese token, lo verifica con Firebase y autentica al usuario.

    public void signInWithGoogle(String idToken, AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = result.getUser();
                    if (user == null) {
                        callback.onError("Error al autenticar con Google");
                        return;
                    }
                    // Si es la primera vez que entra con Google, creamos su documento en Firestore
                    // isNewUser() nos lo dice Firebase directamente
                    if (result.getAdditionalUserInfo() != null
                            && result.getAdditionalUserInfo().isNewUser()) {
                        saveUserToFirestore(user, user.getDisplayName(), callback);
                    } else {
                        // Usuario ya existente → login normal
                        callback.onSuccess(user);
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ── Método privado: guardar usuario en Firestore ─────────────────────────
    // Se llama tanto desde register() como desde signInWithGoogle() (primer acceso).
    // Estructura del documento: users/{uid} → { name, email, createdAt }

    private void saveUserToFirestore(FirebaseUser user, String name, AuthCallback callback) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name",      name != null ? name : "");
        userData.put("email",     user.getEmail());
        userData.put("createdAt", com.google.firebase.Timestamp.now());

        db.collection("users")
                .document(user.getUid()) // El ID del documento es el UID de Firebase Auth
                .set(userData)
                .addOnSuccessListener(unused -> callback.onSuccess(user))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}