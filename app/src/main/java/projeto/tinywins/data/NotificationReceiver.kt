package projeto.tinywins.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import projeto.tinywins.R

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_TITLE = "EXTRA_TITLE"
        const val EXTRA_MESSAGE = "EXTRA_MESSAGE"
        const val NOTIFICATION_ID = "NOTIFICATION_ID"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Seu Desafio te Espera!"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Não se esqueça de completar sua meta de hoje."
        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)

        println("Alarme recebido! Mostrando notificação ID: $notificationId para: $title")

        showNotification(context, notificationId, title, message)
    }

    private fun showNotification(context: Context, notificationId: Int, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "tiny_wins_reminders_channel"
        val channelName = "Lembretes de Desafios"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH // IMPORTANTE: Usar HIGH para garantir visibilidade
            ).apply {
                description = "Canal para lembretes de hábitos e tarefas do Tiny Wins."
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridade alta
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}