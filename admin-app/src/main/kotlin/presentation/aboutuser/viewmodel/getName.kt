package presentation.aboutuser.viewmodel

fun getname(name: String, surname: String, patronymic: String) : String{
    return "$surname $name $patronymic"
}