package florence.model

import scala.util.control.NoStackTrace

final class InvalidComputedFontSize(received: String) extends NoStackTrace:
  override val getMessage: String =
    s"Invalid computed font size. Expected a float number followed by the 'px' suffix, but received: $received"
