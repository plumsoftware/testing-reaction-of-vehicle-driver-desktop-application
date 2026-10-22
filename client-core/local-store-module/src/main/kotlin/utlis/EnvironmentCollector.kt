package utlis

import data.model.dto.test.TestEnvironment
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.awt.KeyboardFocusManager
import java.net.InetAddress
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Собирает сведения о рабочем месте для записи вместе с результатами теста.
 * Вызывать вне UI-потока: на Windows запускается PowerShell (до [WMI_TIMEOUT_SECONDS] с при первом вызове).
 */
object EnvironmentCollector {

    /**
     * Версия методики измерения. Увеличивать при любом изменении, влияющем на сопоставимость
     * результатов (способ измерения времени, правила засчитывания ошибок, протокол тестов).
     * 1 — исходная версия; 2 — время по нажатию и отрисовке стимула, учёт фальстартов,
     * статистика только по правильным реакциям, тест с машиной по красному сигналу;
     * 3 — обучение перед тестом, 5 тренировочных попыток перед зачётной частью, ответ с клавиатуры
     * (устройство ввода выбирается перед тестом и записывается в сессию).
     */
    const val METHOD_VERSION = "3"

    private const val WMI_TIMEOUT_SECONDS = 5L

    private data class MonitorInfo(val name: String, val widthCm: Int, val heightCm: Int)

    /** Данные мониторов из EDID; запрашиваются один раз за запуск программы. */
    private val monitors: List<MonitorInfo> by lazy { readMonitorsFromWmi() }

    fun collect(): TestEnvironment {
        val window = KeyboardFocusManager.getCurrentKeyboardFocusManager().activeWindow
        val device: GraphicsDevice? = window?.graphicsConfiguration?.device
            ?: runCatching { GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice }.getOrNull()

        val scale = device?.defaultConfiguration?.defaultTransform?.scaleX
        val displayMode = device?.displayMode

        // Если монитор один — данные однозначны; если несколько — сопоставить EDID с окном нельзя,
        // поэтому записываются все мониторы
        val monitorName: String
        val monitorSizeCm: String
        val diagonal: Double?
        when (monitors.size) {
            0 -> {
                monitorName = ""
                monitorSizeCm = ""
                diagonal = null
            }

            1 -> {
                val monitor = monitors.first()
                monitorName = monitor.name
                monitorSizeCm = sizeText(monitor)
                diagonal = diagonalInches(monitor)
            }

            else -> {
                monitorName = "Несколько мониторов: " + monitors.joinToString("; ") { "${it.name} (${sizeText(it)} см)" }
                monitorSizeCm = ""
                diagonal = null
            }
        }

        return TestEnvironment(
            monitorName = monitorName,
            monitorSizeCm = monitorSizeCm,
            monitorDiagonalInches = diagonal,
            screenResolution = displayMode?.let { "${it.width}×${it.height}" } ?: "",
            refreshRateHz = displayMode?.refreshRate?.takeIf { it > 0 },
            scalePercent = scale?.let { (it * 100).roundToInt() },
            testWindowPx = if (window != null && scale != null) {
                "${(window.width * scale).roundToInt()}×${(window.height * scale).roundToInt()}"
            } else "",
            computerName = System.getenv("COMPUTERNAME")
                ?: runCatching { InetAddress.getLocalHost().hostName }.getOrDefault(""),
            operatingSystem = "${System.getProperty("os.name")} ${System.getProperty("os.version")}",
            // Свойство задаёт лаунчер собранного приложения; при запуске из Gradle его нет
            appVersion = System.getProperty("jpackage.app-version") ?: "dev",
            methodVersion = METHOD_VERSION
        )
    }

    private fun sizeText(monitor: MonitorInfo): String =
        if (monitor.widthCm > 0 && monitor.heightCm > 0) "${monitor.widthCm}×${monitor.heightCm}" else ""

    private fun diagonalInches(monitor: MonitorInfo): Double? {
        if (monitor.widthCm <= 0 || monitor.heightCm <= 0) return null
        val cm = sqrt((monitor.widthCm * monitor.widthCm + monitor.heightCm * monitor.heightCm).toDouble())
        return (cm / 2.54 * 10).roundToInt() / 10.0
    }

    /** Модель и физический размер мониторов из EDID через WMI. Только Windows. */
    private fun readMonitorsFromWmi(): List<MonitorInfo> {
        if (!System.getProperty("os.name").startsWith("Windows")) return emptyList()

        val script = """
            ${'$'}ids = Get-CimInstance -Namespace root\wmi -ClassName WmiMonitorID
            ${'$'}params = Get-CimInstance -Namespace root\wmi -ClassName WmiMonitorBasicDisplayParams
            foreach (${'$'}id in ${'$'}ids) {
                ${'$'}p = ${'$'}params | Where-Object { ${'$'}_.InstanceName -eq ${'$'}id.InstanceName }
                ${'$'}mf = -join (${'$'}id.ManufacturerName | Where-Object { ${'$'}_ -ne 0 } | ForEach-Object { [char]${'$'}_ })
                ${'$'}nm = -join (${'$'}id.UserFriendlyName | Where-Object { ${'$'}_ -ne 0 } | ForEach-Object { [char]${'$'}_ })
                "${'$'}mf ${'$'}nm|${'$'}(${'$'}p.MaxHorizontalImageSize)|${'$'}(${'$'}p.MaxVerticalImageSize)"
            }
        """.trimIndent()

        return try {
            val process = ProcessBuilder("powershell", "-NoProfile", "-NonInteractive", "-Command", script)
                .redirectErrorStream(true)
                .start()
            if (!process.waitFor(WMI_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly()
                return emptyList()
            }
            process.inputStream.bufferedReader().readLines()
                .mapNotNull { line ->
                    val parts = line.split("|")
                    if (parts.size != 3) return@mapNotNull null
                    MonitorInfo(
                        name = parts[0].trim(),
                        widthCm = parts[1].trim().toIntOrNull() ?: 0,
                        heightCm = parts[2].trim().toIntOrNull() ?: 0
                    )
                }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
