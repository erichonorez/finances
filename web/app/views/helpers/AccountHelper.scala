package views.helpers

object AccountHelper {

  import views.html.helper.FieldConstructor
  implicit val myFields = FieldConstructor(views.html.helpers.myFieldConstructorTemplate.f)

  def transactionAmountClassOf(value: BigDecimal): String = {
    if (value.compare(BigDecimal.valueOf(0)) < 0) "debit" else "credit"
  }

}