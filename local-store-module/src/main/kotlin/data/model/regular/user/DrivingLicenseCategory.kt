package data.model.regular.user

enum class DrivingLicenseCategory {

    /**
    Для актуализации категорий водитеских удостоверений использовать
    Постановление Правительства РФ от ... "О допуске к управлению транспортными средствами" (вместе с "Правилами проведения экзаменов на право управления транспортными средствами и выдачи водительских удостоверений")
    Раздел "III. Выдача российских национальных и международных водительских удостоверений"
     **/

    Empty {
        override fun toString(): String {
            return ""
        }
    },

    NoDrivingLicense {
        override fun toString(): String {
            return "Нет водительских прав"
        }
    },



    A {
        override fun toString(): String {
            return "A"
        }
    },

    B {
        override fun toString(): String {
            return "B"
        }
    },

    C {
        override fun toString(): String {
            return "C"
        }
    },

    D {
        override fun toString(): String {
            return "D"
        }
    },



    BE {
        override fun toString(): String {
            return "BE"
        }
    },

    CE {
        override fun toString(): String {
            return "CE"
        }
    },

    DE {
        override fun toString(): String {
            return "DE"
        }
    },



    Tm {
        override fun toString(): String {
            return "Tm"
        }
    },

    Tb {
        override fun toString(): String {
            return "Tb"
        }
    },

    M {
        override fun toString(): String {
            return "M"
        }
    },



    A1 {
        override fun toString(): String {
            return "A1"
        }
    },

    B1 {
        override fun toString(): String {
            return "B1"
        }
    },

    C1 {
        override fun toString(): String {
            return "C1"
        }
    },

    D1 {
        override fun toString(): String {
            return "D1"
        }
    },




    C1E {
        override fun toString(): String {
            return "C1E"
        }
    },

    D1E {
        override fun toString(): String {
            return "D1E"
        }
    };

    companion object {
        fun fromString(value: String): DrivingLicenseCategory {
            return entries.find { it.toString() == value } ?: Empty
        }
    }
}