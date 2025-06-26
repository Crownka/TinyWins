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

    // A função onReceive é chamada pelo Android quando o alarme dispara.
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("EXTRA_TITLE") ?: "Lembrete de Tarefa!"
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "Não se esqueça do seu desafio."
        // Usamos o ID do desafio (convertido para Int) como ID da notificação.
        val notificationId = intent.getIntExtra("EXTRA_ID", 0)

        println("ALARM RECEIVED: Tentando mostrar notificação para '$title'")

        showNotification(context, notificationId, title, message)
    }

    private fun showNotification(context: Context, notificationId: Int, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "tiny_wins_reminders_channel"
        val channelName = "Lembretes de Desafios"

        // Para Android 8.0+, é obrigatório criar um Canal de Notificação.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH // IMPORTANTE: Prioridade alta para o canal.
            ).apply {
                description = "Canal para lembretes de hábitos e tarefas."
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // IMPORTANTE: Verifique se este ícone existe.
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridade alta para a notificação.
            .setAutoCancel(true) // A notificação some ao ser clicada.
            .build()

        notificationManager.notify(notificationId, notification)
        println("NOTIFICATION SENT: Notificação com ID $notificationId enviada.")
    }
}