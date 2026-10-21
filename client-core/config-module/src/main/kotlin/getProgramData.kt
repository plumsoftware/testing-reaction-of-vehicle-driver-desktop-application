import data.ProgramNumberStore
import data.model.ProgramData
import java.util.Properties

/** Название и версия — из `program.properties`, который генерирует при сборке build-скрипт приложения. */
fun getProgramData(): ProgramData {
    val properties = Properties()
    ProgramData::class.java.getResourceAsStream("/program.properties")
        ?.reader(Charsets.UTF_8)
        ?.use(properties::load)
    return ProgramData(
        name = properties.getProperty("name", ""),
        version = properties.getProperty("version", ""),
        number = ProgramNumberStore.get(),
    )
}
