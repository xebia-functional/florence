package florence.core.rendering

import florence.core.model.shared.StyleTypes.FontSpec

/** Measures the dimensions of a given text and font in pixels
  */
trait TextMeasurer:

  /** @return the with of the given text and font in pixels
    */
  def measureWidth(text: String, font: FontSpec): Double

  /** @return the height of the given text and font in pixels
    */
  def measureHeight(text: String, font: FontSpec): Double
