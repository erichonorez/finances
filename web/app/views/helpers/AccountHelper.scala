package views.helpers

import java.text.SimpleDateFormat
import java.util.Date

object AccountHelper {

  import views.html.helper.FieldConstructor
  implicit val myFields = FieldConstructor(views.html.helpers.myFieldConstructorTemplate.f)

  def transactionAmountClassOf(value: BigDecimal): String = {
    if (value.compare(BigDecimal.valueOf(0)) < 0) "debit" else "credit"
  }

  def formatDate(date: Date): String = {
    val format: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    format.format(date)
  }

}