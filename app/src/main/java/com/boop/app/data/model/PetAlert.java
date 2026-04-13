package com.boop.app.data.model;

import java.util.Date;

/**
 * BOOP — Modelo de datos: Alerta / Recordatorio (Java)
 * Corresponde a la pantalla "Nuevo recordatorio"
 */
public class PetAlert {

    private String id;
    private String petId;
    private AlertType type;
    private String title;
    private Date dueDate;
    private RepeatMode repeatMode;
    private boolean isEnabled;
    private UrgencyLevel urgencyLevel;

    // Constructor vacío requerido por Firebase
    public PetAlert() {
        this.type = AlertType.OTHER;
        this.repeatMode = RepeatMode.NONE;
        this.isEnabled = true;
        this.urgencyLevel = UrgencyLevel.SCHEDULED;
    }

    // ── Getters y Setters ──

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }

    public AlertType getType() { return type; }
    public void setType(AlertType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public RepeatMode getRepeatMode() { return repeatMode; }
    public void setRepeatMode(RepeatMode repeatMode) { this.repeatMode = repeatMode; }

    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }

    public UrgencyLevel getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(UrgencyLevel urgencyLevel) { this.urgencyLevel = urgencyLevel; }

    // ── Enums ──

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

    public enum RepeatMode {
        NONE("No"),
        ANNUAL("Anual"),
        MONTHLY("Mensual"),
        CUSTOM("Personalizar");

        private final String displayName;
        RepeatMode(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum UrgencyLevel {
        URGENT,     // 🔴 Vencida o hoy
        UPCOMING,   // 🟡 Próximas 2 semanas
        SCHEDULED   // 🟢 Programadas
    }
}
