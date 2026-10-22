package tests.common

import java.util.concurrent.atomic.AtomicLong
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.SourceDataLine
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

/**
 * Звуковой сигнал «клаксон» с определением фактического момента начала звучания.
 *
 * Аудиолиния открывается заранее и непрерывно получает тишину, поэтому при подаче сигнала
 * не тратится время на открытие устройства. Отсчёты клаксона ставятся в очередь сразу за
 * уже записанной тишиной; отдельный поток следит за позицией воспроизведения
 * ([SourceDataLine.getLongFramePosition]) и фиксирует System.nanoTime(), когда она доходит
 * до первого отсчёта клаксона. Это момент выдачи звука аудиосистемой; задержку самой звуковой
 * карты и наушников/колонок программа измерить не может — она постоянна для одного оборудования.
 *
 * Сигнал: два тона 420 и 500 Гц (типичная пара автомобильного клаксона), 400 мс,
 * нарастание и спад по 2 мс (без щелчка, но без растягивания фронта).
 */
class HornPlayer : AutoCloseable {

    private val format = AudioFormat(SAMPLE_RATE.toFloat(), 16, 1, true, false)
    private val line: SourceDataLine = AudioSystem.getSourceDataLine(format).apply {
        // Небольшой буфер — меньше задержка между постановкой звука в очередь и его выводом
        open(format, BUFFER_FRAMES * BYTES_PER_FRAME)
        start()
    }

    /** Размер буфера аудиолинии, мс — записывается в показатели теста. */
    val bufferMs: Double = line.bufferSize / BYTES_PER_FRAME * 1000.0 / SAMPLE_RATE

    private val framesWritten = AtomicLong(0)

    @Volatile
    private var pendingHorn: ((Long) -> Unit)? = null

    @Volatile
    private var running = true

    private val horn: ByteArray = buildHorn()
    private val silence = ByteArray(CHUNK_FRAMES * BYTES_PER_FRAME)

    private val writer = Thread({
        while (running) {
            val onStart = pendingHorn
            if (onStart != null) {
                pendingHorn = null
                val hornStartFrame = framesWritten.get()
                watchOnset(hornStartFrame, onStart)
                write(horn)
            } else {
                write(silence)
            }
        }
    }, "horn-writer").apply {
        isDaemon = true
        priority = Thread.MAX_PRIORITY
        start()
    }

    /**
     * Подаёт сигнал. [onSoundStarted] вызывается из фонового потока с System.nanoTime()
     * момента, когда аудиосистема начала воспроизводить сигнал.
     */
    fun play(onSoundStarted: (Long) -> Unit) {
        pendingHorn = onSoundStarted
    }

    private fun write(bytes: ByteArray) {
        line.write(bytes, 0, bytes.size)
        framesWritten.addAndGet((bytes.size / BYTES_PER_FRAME).toLong())
    }

    private fun watchOnset(startFrame: Long, onStart: (Long) -> Unit) {
        Thread({
            while (running && line.longFramePosition < startFrame) {
                Thread.onSpinWait()
            }
            if (running) onStart(System.nanoTime())
        }, "horn-onset").apply {
            isDaemon = true
            priority = Thread.MAX_PRIORITY
            start()
        }
    }

    private fun buildHorn(): ByteArray {
        val frames = SAMPLE_RATE * HORN_DURATION_MS / 1000
        val rampFrames = SAMPLE_RATE * RAMP_MS / 1000
        val bytes = ByteArray(frames * BYTES_PER_FRAME)
        for (i in 0 until frames) {
            val t = i.toDouble() / SAMPLE_RATE
            val envelope = min(1.0, min(i.toDouble() / rampFrames, (frames - i).toDouble() / rampFrames))
            val sample = envelope * AMPLITUDE * (sin(2 * PI * 420 * t) + sin(2 * PI * 500 * t)) / 2
            val value = (sample * Short.MAX_VALUE).toInt()
            bytes[2 * i] = (value and 0xFF).toByte()
            bytes[2 * i + 1] = (value shr 8 and 0xFF).toByte()
        }
        return bytes
    }

    override fun close() {
        running = false
        writer.join(500)
        line.stop()
        line.close()
    }

    companion object {
        const val SAMPLE_RATE = 44_100
        private const val BYTES_PER_FRAME = 2
        private const val BUFFER_FRAMES = 1024
        private const val CHUNK_FRAMES = 128
        const val HORN_DURATION_MS = 400
        private const val RAMP_MS = 2
        private const val AMPLITUDE = 0.8
    }
}
