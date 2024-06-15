package domain.model.regular.user

enum class Gender {
    EMPTY{
        override fun toString(): String {
            return ""
        }
    },
    MALE {
        override fun toString(): String {
            return "Мужчина"
        }
    },
    FEMALE {
        override fun toString(): String {
            return "Женщина"
        }
    };

    companion object {
        fun fromString(value: String): Gender {
            return Gender.entries.find { it.toString() == value } ?: MALE
        }
    }
}