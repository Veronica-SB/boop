package com.boop.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.boop.app.HomeActivity;
import com.boop.app.R;
import com.boop.app.ui.addpet.AddPetActivity;
import com.boop.app.utils.ThemeManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AuthActivity extends AppCompatActivity {

    // Código de resultado para identificar el Intent de Google Sign-In
    private static final int RC_GOOGLE_SIGN_IN = 9001;

    private boolean isLoginMode = true;

    // ── Vistas ────────────────────────────────────────────────────────────────
    private TextInputLayout  tilName, tilEmail, tilPassword;
    private TextInputEditText etName, etEmail, etPassword;
    private MaterialButton   btnSubmit, btnGoogle;
    private TextView         tvForgot, tabLogin, tabRegister;
    private View             layoutNameField;

    // ── ViewModel y Google Sign-In ────────────────────────────────────────────
    private AuthViewModel    viewModel;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        initViews();
        initViewModel();
        initGoogleSignIn();
        setupTabListeners();
        setupButtonListeners();
    }

    // ── Inicialización de vistas ──────────────────────────────────────────────

    private void initViews() {
        tabLogin        = findViewById(R.id.tab_login);
        tabRegister     = findViewById(R.id.tab_register);
        layoutNameField = findViewById(R.id.layout_name_field);
        tilName         = findViewById(R.id.til_name);
        etName          = findViewById(R.id.et_name);
        tilEmail        = findViewById(R.id.til_email);
        etEmail         = findViewById(R.id.et_email);
        tilPassword     = findViewById(R.id.til_password);
        etPassword      = findViewById(R.id.et_password);
        tvForgot        = findViewById(R.id.tv_forgot);
        btnSubmit       = findViewById(R.id.btn_auth_submit);
        btnGoogle       = findViewById(R.id.btn_google);
    }

    // ── Inicialización del ViewModel y sus observadores ───────────────────────

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Observa el éxito: si user es null es que se envió el email de recuperación
        viewModel.authSuccess.observe(this, user -> {
            if (user == null) {
                // Caso recuperar contraseña — no navegamos, solo avisamos
                Toast.makeText(this, "Revisa tu correo para restablecer la contraseña",
                        Toast.LENGTH_LONG).show();
            } else if (isNewUser(user)) {
                // Usuario recién registrado → flujo de añadir mascota
                goToAddPet();
            } else {
                // Usuario existente que hace login → pantalla principal
                goToHome();
            }
        });

        // Observa los errores y los muestra en el campo correspondiente
        viewModel.authError.observe(this, errorMessage -> {
            if (errorMessage != null) {
                tilPassword.setError(errorMessage);
            }
        });

        // Observa el estado de carga para bloquear/desbloquear los botones
        viewModel.isLoading.observe(this, loading -> {
            btnSubmit.setEnabled(!loading);
            btnGoogle.setEnabled(!loading);
        });
    }

    // ── Configuración de Google Sign-In ──────────────────────────────────────

    private void initGoogleSignIn() {
        // Pedimos el idToken usando el web_client_id del google-services.json
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, options);
    }

    // ── Listeners de tabs ─────────────────────────────────────────────────────

    private void setupTabListeners() {
        tabLogin.setOnClickListener(v -> switchToLogin());
        tabRegister.setOnClickListener(v -> switchToRegister());
    }

    private void switchToLogin() {
        isLoginMode = true;
        tabLogin.setBackgroundResource(R.drawable.bg_tab_selected);
        tabLogin.setTextColor(getColor(R.color.white));
        tabRegister.setBackgroundResource(android.R.color.transparent);
        tabRegister.setTextColor(getColor(R.color.text_secondary_light));
        btnSubmit.setText("Entrar");
        layoutNameField.setVisibility(View.GONE);
        tvForgot.setVisibility(View.VISIBLE);
        clearErrors();
    }

    private void switchToRegister() {
        isLoginMode = false;
        tabRegister.setBackgroundResource(R.drawable.bg_tab_selected);
        tabRegister.setTextColor(getColor(R.color.white));
        tabLogin.setBackgroundResource(android.R.color.transparent);
        tabLogin.setTextColor(getColor(R.color.text_secondary_light));
        btnSubmit.setText("Crear cuenta");
        layoutNameField.setVisibility(View.VISIBLE);
        tvForgot.setVisibility(View.GONE);
        clearErrors();
    }

    // ── Listeners de botones ──────────────────────────────────────────────────

    private void setupButtonListeners() {

        // Botón principal: login o registro según el tab activo
        btnSubmit.setOnClickListener(v -> {
            if (!validarFormulario()) return;

            String email    = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (isLoginMode) {
                viewModel.signIn(email, password);
            } else {
                String name = etName.getText().toString().trim();
                viewModel.register(email, password, name);
            }
        });

        // Botón Google: lanza el selector de cuentas del sistema
        btnGoogle.setOnClickListener(v -> {
            // Cerramos cualquier sesión de Google previa para que siempre
            // muestre el selector de cuentas y no entre automáticamente
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
            });
        });

        // Recuperar contraseña
        tvForgot.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                tilEmail.setError("Escribe tu correo primero");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.setError("Formato de correo no válido");
                return;
            }
            viewModel.sendPasswordReset(email);
        });
    }

    // ── Resultado de Google Sign-In ───────────────────────────────────────────

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Obtenemos el idToken que Firebase necesita para autenticar
                GoogleSignInAccount account = task.getResult(ApiException.class);
                viewModel.signInWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // El usuario canceló o hubo un error en el selector
                Toast.makeText(this, "Google Sign-In cancelado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ── Validación del formulario ─────────────────────────────────────────────

    private boolean validarFormulario() {
        clearErrors();
        boolean valido = true;

        // Validar nombre solo en modo registro
        if (!isLoginMode) {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                tilName.setError("El nombre es obligatorio");
                valido = false;
            }
        }

        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            tilEmail.setError("El correo es obligatorio");
            valido = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Formato de correo no válido");
            valido = false;
        }

        String password = etPassword.getText().toString();
        if (password.isEmpty()) {
            tilPassword.setError("La contraseña es obligatoria");
            valido = false;
        } else if (password.length() < 8) {
            tilPassword.setError("Mínimo 8 caracteres");
            valido = false;
        }

        return valido;
    }

    // Limpia todos los mensajes de error del formulario
    private void clearErrors() {
        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
    }

    // ── Navegación ────────────────────────────────────────────────────────────

    private void goToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish(); // el usuario no puede volver atrás al login
    }

    private void goToAddPet() {
        startActivity(new Intent(this, AddPetActivity.class));
        finish(); // usuario nuevo no debería poder volver al login
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    // Determina si el usuario acaba de registrarse comprobando su fecha de creación
    // Si se registró hace menos de 5 segundos, es nuevo
    private boolean isNewUser(com.google.firebase.auth.FirebaseUser user) {
        long creationTime    = user.getMetadata().getCreationTimestamp();
        long lastSignInTime  = user.getMetadata().getLastSignInTimestamp();
        return (lastSignInTime - creationTime) < 5000;
    }
}