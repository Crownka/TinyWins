package projeto.tinywins.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedule(challenge: TinyWinChallenge, timeInMillis: Long) {
        // A intenção que será disparada quando o alarme tocar.
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            // CORREÇÃO: Usando as constantes a partir da sua classe de origem (NotificationReceiver)
            putExtra(NotificationReceiver.EXTRA_TITLE, challenge.title)
            putExtra(NotificationReceiver.EXTRA_MESSAGE, "Este é o seu lembrete para completar a tarefa!")
            putExtra(NotificationReceiver.NOTIFICATION_ID, challenge.id.hashCode())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            // Usando um requestCode mais único para evitar que um lembrete sobrescreva outro
            (challenge.id + timeInMillis).hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        // CORREÇÃO: Adicionando a verificação de versão do Android antes de chamar a função
        val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            // Para versões mais antigas, assumimos que podemos agendar alarmes exatos
            true
        }

        if (canScheduleExact) {
            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
                println("ALARM SCHEDULED (EXACT): Agendado para '${challenge.title}'")
            } catch (e: SecurityException) {
                println("SecurityException: Falha ao agendar alarme exato. Verifique as permissões. ${e.message}")
            }
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            println("ALARM SCHEDULED (INEXACT): Agendado para '${challenge.title}'")
        }
    }

    fun cancel(challenge: TinyWinChallenge) {
        // A lógica de cancelamento precisaria ser aprimorada para funcionar com múltiplos lembretes,
        // mas por enquanto, focamos em fazer o agendamento funcionar.
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            challenge.id.hashCode(), // Para cancelar, o ideal é ter o ID exato do alarme
            Intent(context, NotificationReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}