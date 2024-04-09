package domain.model

enum class Gender {
    MALE {
        override fun toString(): String {
            return "Мужчина"
        }
    },
    FEMALE {
        override fun toString(): String {
            return "Женщина"
        }
    }
}