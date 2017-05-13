package models

import org.svomz.apps.finances.core.application.interpreter._

/**
  * Created by Eric on 5/13/2017.
  */
object TimePeriodMapper {

  def fromString(s: String): TimePeriod = {
    s match {
        case d if d.equals("this-month") => ThisMonth
        case d if d.equals("last-month") => LastMonth
        case d if d.equals("last-three-months") => LastThreeMonths
        case d if d.equals("this-year") => ThisYear
        case d if d.equals("last-year") => LastYear
        case _ => ThisMonth
    }
  }

  def toLabel(p: TimePeriod): String = {
    p match {
      case ThisMonth => "This month"
      case LastMonth => "Last month"
      case LastThreeMonths => "Last 3 months"
      case ThisYear => "This year"
      case LastYear => "Last year"
    }
  }
}
