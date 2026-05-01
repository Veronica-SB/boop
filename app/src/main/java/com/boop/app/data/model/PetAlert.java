package com.boop.app.data.model;

import com.google.firebase.firestore.PropertyName;
import java.util.Date;

/**
 * BOOP — Modelo de datos: Recordatorio
 *
 * Representa un documento en la subcolección "pets/{petId}/reminders/{alertId}".
 *
 * Roles y permisos:
 * - OWNER: puede crear, editar y eliminar recordatorios
 * - EDITOR: solo puede actualizar confirmedBy y confirmedAt (marcar como hecho)
 *
 * Subcolección Firestore: pets/{petId}/reminders/{alertId}
 */
public class PetAlert {

    // ── Identificadores ──────────────────────────────────────────────────────

    // ID del documento (no se guarda en Firestore, se asigna al leer)
    private String id;

    // ID de la mascota a la que pertenece este recordatorio
    @PropertyName("petId")
    private String petId;

    // ── Datos del recordatorio ────────────────────────────────────────────────

    @PropertyName("type")
    private String type; // Usa AlertType.name() para guardar

    @PropertyName("title")
    private String title;

    @PropertyName("dueDate")
    private Date dueDate;

    @PropertyName("repeatMode")
    private String repeatMode; // Usa RepeatMode.name() para guardar

    @PropertyName("isEnabled")
    private boolean isEnabled;

    @PropertyName("urgencyLevel")
    private String urgencyLevel; // Usa UrgencyLevel.name() para guardar

    // ── Campos del sistema de roles ───────────────────────────────────────────

    // UID del usuario que creó el recordatorio (siempre el OWNER)
    @PropertyName("createdBy")
    private String createdBy;

    // UID del usuario que confirmó el recordatorio (OWNER o EDITOR)
    // Null si no ha sido confirmado todavía
    @PropertyName("confirmedBy")
    private String confirmedBy;

    // Fecha y hora en que se confirmó el recordatorio
    // Null si no ha sido confirmado todavía
    @PropertyName("confirmedAt")
    private Date confirmedAt;

    // ── Constructor vacío requerido por Firebase ──────────────────────────────

    public PetAlert() {
        this.type = AlertType.OTHER.name();
        this.repeatMode = RepeatMode.NONE.name();
        this.isEnabled = true;
        this.urgencyLevel = UrgencyLevel.SCHEDULED.name();
    }

    // ── Getters y Setters ─────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @PropertyName("petId")
    public String getPetId() { return petId; }
    @PropertyName("petId")
    public void setPetId(String petId) { this.petId = petId; }

    @PropertyName("type")
    public String getType() { return type; }
    @PropertyName("type")
    public void setType(String type) { this.type = type; }

    @PropertyName("title")
    public String getTitle() { return title; }
    @PropertyName("title")
    public void setTitle(String title) { this.title = title; }

    @PropertyName("dueDate")
    public Date getDueDate() { return dueDate; }
    @PropertyName("dueDate")
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    @PropertyName("repeatMode")
    public String getRepeatMode() { return repeatMode; }
    @PropertyName("repeatMode")
    public void setRepeatMode(String repeatMode) { this.repeatMode = repeatMode; }

    @PropertyName("isEnabled")
    public boolean isEnabled() { return isEnabled; }
    @PropertyName("isEnabled")
    public void setEnabled(boolean enabled) { isEnabled = enabled; }

    @PropertyName("urgencyLevel")
    public String getUrgencyLevel() { return urgencyLevel; }
    @PropertyName("urgencyLevel")
    public void setUrgencyLevel(String urgencyLevel) { this.urgencyLevel = urgencyLevel; }

    @PropertyName("createdBy")
    public String getCreatedBy() { return createdBy; }
    @PropertyName("createdBy")
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    @PropertyName("confirmedBy")
    public String getConfirmedBy() { return confirmedBy; }
    @PropertyName("confirmedBy")
    public void setConfirmedBy(String confirmedBy) { this.confirmedBy = confirmedBy; }

    @PropertyName("confirmedAt")
    public Date getConfirmedAt() { return confirmedAt; }
    @PropertyName("confirmedAt")
    public void setConfirmedAt(Date confirmedAt) { this.confirmedAt = confirmedAt; }

    // ── Enums ─────────────────────────────────────────────────────────────────

    /**
     * Tipos de recordatorio disponibles.
     * Se guarda en Firestore como String usando .name() (ej: "VACCINE").
     */
    public enum AlertType {
        VACCINE("Vacuna"),
        MEDICATION("Medicación"),
        VET_VISIT("Cita vet."),
        DEWORMING("Desparasitar"),
        HYGIENE("Higiene"),
        OTHER("Otro");

        private final String displayName;
        AlertType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    /**
     * Modos de repetición del recordatorio.
     * DAILY añadido respecto al modelo original.
     */
    public enum RepeatMode {
        NONE("No"),
        DAILY("Diaria"),
        MONTHLY("Mensual"),
        ANNUAL("Anual"),
        CUSTOM("Personalizar");

        private final String displayName;
        RepeatMode(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    /**
     * Nivel de urgencia del recordatorio.
     * Determina el color de la tarjeta en la UI.
     */
    public enum UrgencyLevel {
        URGENT,     // 🔴 Vencida o vence hoy
        UPCOMING,   // 🟡 Vence en menos de 2 semanas
        SCHEDULED   // 🟢 Programada sin urgencia
    }
}
