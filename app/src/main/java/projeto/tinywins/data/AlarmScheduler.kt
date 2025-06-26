package projeto.tinywins.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedule(challenge: TinyWinChallenge, timeInMillis: Long) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(NotificationReceiver.EXTRA_TITLE, challenge.title)
            putExtra(NotificationReceiver.EXTRA_MESSAGE, "Lembrete para completar seu desafio!")
            putExtra(NotificationReceiver.NOTIFICATION_ID, challenge.id.hashCode())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            challenge.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Verifico se o app pode agendar alarmes exatos
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
            println("Alarme EXATO agendado para '${challenge.title}'")
        } else {
            // Se não puder, agendo um alarme normal (pode ter um pequeno atraso)
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
            println("Alarme NÃO EXATO agendado para '${challenge.title}'")
        }
    }

    fun cancel(challenge: TinyWinChallenge) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            challenge.id.hashCode(),
            Intent(context, NotificationReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        println("Alarme cancelado para '${challenge.title}'")
    }
}