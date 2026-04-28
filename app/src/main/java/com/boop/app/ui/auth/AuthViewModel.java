package com.boop.app.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

/**
 * AuthViewModel — intermediario entre AuthActivity y AuthRepository.
 * La Activity observa los LiveData y reacciona cuando cambian.
 * El ViewModel nunca importa nada de Android UI (Context, View, etc.)
 */
public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;

    // ── LiveData que la Activity observa ─────────────────────────────────────
    // MutableLiveData es privado (solo el ViewModel lo modifica)
    // LiveData es público (la Activity solo puede leerlo, no modificarlo)

    private final MutableLiveData<FirebaseUser> _authSuccess = new MutableLiveData<>();
    public  final LiveData<FirebaseUser>         authSuccess  = _authSuccess;

    private final MutableLiveData<String> _authError = new MutableLiveData<>();
    public  final LiveData<String>         authError  = _authError;

    // Controla si hay una operación en curso (para deshabilitar botones)
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public  final LiveData<Boolean>         isLoading  = _isLoading;

    public AuthViewModel() {
        this.repository = new AuthRepository();
    }

    // ── Login con email y contraseña ─────────────────────────────────────────

    public void signIn(String email, String password) {
        _isLoading.setValue(true);
        repository.signIn(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                _isLoading.setValue(false);
                _authSuccess.setValue(user);
            }
            @Override
            public void onError(String errorMessage) {
                _isLoading.setValue(false);
                _authError.setValue(errorMessage);
            }
        });
    }

    // ── Registro con email, contraseña y nombre ──────────────────────────────

    public void register(String email, String password, String name) {
        _isLoading.setValue(true);
        repository.register(email, password, name, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                _isLoading.setValue(false);
                _authSuccess.setValue(user);
            }
            @Override
            public void onError(String errorMessage) {
                _isLoading.setValue(false);
                _authError.setValue(errorMessage);
            }
        });
    }

    // ── Recuperar contraseña ─────────────────────────────────────────────────

    public void sendPasswordReset(String email) {
        _isLoading.setValue(true);
        repository.sendPasswordReset(email, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                _isLoading.setValue(false);
                // Usamos null como señal de "email enviado correctamente"
                _authSuccess.setValue(null);
            }
            @Override
            public void onError(String errorMessage) {
                _isLoading.setValue(false);
                _authError.setValue(errorMessage);
            }
        });
    }

    // ── Google Sign-In ───────────────────────────────────────────────────────

    public void signInWithGoogle(String idToken) {
        _isLoading.setValue(true);
        repository.signInWithGoogle(idToken, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                _isLoading.setValue(false);
                _authSuccess.setValue(user);
            }
            @Override
            public void onError(String errorMessage) {
                _isLoading.setValue(false);
                _authError.setValue(errorMessage);
            }
        });
    }
}