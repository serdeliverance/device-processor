package deviceprocessor.utils

object ListUtils {
  def average(list: List[Double]): Double =
    list match {
      case Nil => 0
      case _   => list.sum / list.size
    }
}
