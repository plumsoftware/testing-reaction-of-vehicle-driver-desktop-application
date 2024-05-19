package utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

fun stringFormatter(): AnnotatedString {
    return buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
            append("Настоящая Политика конфиденциальности описывает, как предоставляемое вами персональное информация используется и обрабатывается в рамках нашего десктоп-приложения для проверки времени реакции водителя.")
        }
        append("\n")
        append("\n")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("1. Собираемая информация\n")
        }
        withStyle(style = SpanStyle(fontStyle = FontStyle.Normal)) {
            append("При регистрации в приложении для проверки времени реакции водителя, мы можем запросить следующие персональные данные:")
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("\n- ")
            append("Имя, фамилия, отчество (при наличии)")
            append("\n- ")
            append("Возраст")

            append("\n- ")
            append("Пол")

            append("\n- ")
            append("Стаж вождения")

            append("\n- ")
            append("Категория водительского удостоверения")
        }
        append("\n")
        append("\n")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("2. Использование информации\n")
        }
        withStyle(style = SpanStyle(fontStyle = FontStyle.Normal)) {
            append("Данные, которые вы предоставляете при регистрации, используются исключительно для целей, связанных с функционированием десктоп-приложения для проверки времени реакции водителя. Мы можем использовать эту информацию для обеспечения вам доступа к функционалу приложения и для улучшения его работы.")
        }
        append("\n")
        append("\n")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("3. Защита информации\n")
        }
        withStyle(style = SpanStyle(fontStyle = FontStyle.Normal)) {
            append("Мы ценим вашу конфиденциальность и принимаем все необходимые меры для защиты ваших персональных данных от несанкционированного доступа, разглашения или утраты.")
        }
        append("\n")
        append("\n")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("4. Предоставление информации третьим лицам\n")
        }
        withStyle(style = SpanStyle(fontStyle = FontStyle.Normal)) {
            append("Ваши персональные данные не будут переданы третьим лицам без вашего согласия, за исключением случаев, предусмотренных законодательством.")
        }
        append("\n")
        append("\n")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("5. Обновление информации\n")
        }
        withStyle(style = SpanStyle(fontStyle = FontStyle.Normal)) {
            append("Вы имеете право в любое время обновить или исправить свои персональные данные, а также запросить удаление вашей учетной записи из системы.")
        }
        append("\n")
        append("\n")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("6. Согласие с Политикой конфиденциальности\n")
        }
        withStyle(style = SpanStyle(fontStyle = FontStyle.Normal)) {
            append("Регистрируясь в десктоп-приложении для проверки времени реакции водителя, вы соглашаетесь с условиями настоящей Политики конфиденциальности.")
        }
        append("\n")
        append("\n")
        append("Если у вас возникли вопросы или требуется дополнительная информация, пожалуйста, свяжитесь с нами по указанным контактным данным.")
        withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
            append("\nДата вступления в силу: 01.01.2020")
        }
    }
}