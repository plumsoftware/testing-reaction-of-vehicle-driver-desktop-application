import data.model.ProgramData

inline fun getProgramData() : ProgramData {
    val config = getConfig()
    return config.programData
}