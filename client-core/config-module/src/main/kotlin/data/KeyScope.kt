package data

/** Для какого хранилища нужен ключ: у локальных и у сетевых баз ключи разные. */
enum class KeyScope(internal val fileName: String) {
    LOCAL("key-local.bin"),
    NETWORK("key-network.bin"),
}
