package deviceprocessor.utils

import deviceprocessor.utils.ListUtils.average
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ListUtilsSpec extends AnyWordSpec with Matchers {
  "calculate average" in {
    val values = List(50, 40).map(_.toDouble)

    val result = average(values)

    result shouldBe 45
  }

  "average return zero when having empty list of values" in {
    val values = List.empty

    val result = average(values)

    result shouldBe 0
  }
}
