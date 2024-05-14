package domain.model

enum class DrivingLicenseCategory {
    Empty {
        override fun toString(): String {
            return "Пусто"
        }
    },

    NoDrivingLicense {
        override fun toString(): String {
            return "Нет водительских плав"
        }
    },

    AM {
        override fun toString(): String {
            return "AM"
        }
    },

    A1 {
        override fun toString(): String {
            return "A1"
        }
    },

    A2 {
        override fun toString(): String {
            return "A2"
        }
    },

    A {
        override fun toString(): String {
            return "A"
        }
    },

    B1 {
        override fun toString(): String {
            return "B1"
        }
    },

    B {
        override fun toString(): String {
            return "B"
        }
    },

    B96 {
        override fun toString(): String {
            return "B96"
        }
    },

    BE {
        override fun toString(): String {
            return "BE"
        }
    },

    C1 {
        override fun toString(): String {
            return "C1"
        }
    },

    C1E {
        override fun toString(): String {
            return "C1E"
        }
    },

    C {
        override fun toString(): String {
            return "C"
        }
    },

    CE {
        override fun toString(): String {
            return "CE"
        }
    },

    D1 {
        override fun toString(): String {
            return "D1"
        }
    },

    D1E {
        override fun toString(): String {
            return "D1E"
        }
    },

    D {
        override fun toString(): String {
            return "D"
        }
    },

    DE {
        override fun toString(): String {
            return "DE"
        }
    },

    T {
        override fun toString(): String {
            return "T"
        }
    }
}