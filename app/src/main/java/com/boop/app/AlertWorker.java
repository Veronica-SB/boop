package com.boop.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class AlertWorker extends Worker {

    private static final String CHANNEL_ID = "boop_alerts";

    public AlertWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        createNotificationChannel();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) return Result.success();

        String userId = auth.getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CountDownLatch latch = new CountDownLatch(1);

        db.collection("pets")
                .whereEqualTo("ownerId", userId)
                .get()
                .addOnSuccessListener(pets -> {
                    for (QueryDocumentSnapshot petDoc : pets) {
                        String petName = petDoc.getString("name");
                        String petId   = petDoc.getId();

                        db.collection("pets").document(petId)
                                .collection("reminders")
                                .whereEqualTo("isEnabled", true)
                                .get()
                                .addOnSuccessListener(reminders -> {
                                    for (QueryDocumentSnapshot reminderDoc : reminders) {
                                        Date dueDate = reminderDoc.getDate("dueDate");
                                        String title = reminderDoc.getString("title");
                                        String confirmedBy = reminderDoc.getString("confirmedBy");

                                        if (dueDate == null || confirmedBy != null) continue;

                                        long diff = dueDate.getTime() - System.currentTimeMillis();
                                        long days  = diff / (1000 * 60 * 60 * 24);

                                        // Notificar si vence hoy o mañana
                                        if (days <= 1 && days >= 0) {
                                            String msg = days == 0
                                                    ? "Vence hoy"
                                                    : "Vence mañana";
                                            sendNotification(title + " · " + petName, msg);
                                        } else if (days < 0) {
                                            sendNotification(title + " · " + petName, "Recordatorio vencido");
                                        }
                                    }
                                    latch.countDown();
                                })
                                .addOnFailureListener(e -> latch.countDown());
                    }
                })
                .addOnFailureListener(e -> latch.countDown());

        try { latch.await(); } catch (InterruptedException e) { e.printStackTrace(); }

        return Result.success();
    }

    private void sendNotification(String title, String message) {
        NotificationManager manager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_bell)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Recordatorios BOOP",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notificaciones de recordatorios de tus mascotas");
            NotificationManager manager = (NotificationManager)
                    getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }
}