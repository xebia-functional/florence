package florence.core.model.shared

import florence.core.model.shared.FontLengthUnit.Absolute.*
import florence.core.model.shared.FontLengthUnit.RootRelative.*

/** A pixel compatible font is any font whose size that can be converted into pixel units
  */
trait PixelCompatibleFont[-LengthUnit <: FontLengthUnit]:

  /** @param value font size value
    * @param unit font size unit
    * @return the font size in pixels
    */
  def toPixels(value: Double, unit: LengthUnit): Double

object PixelCompatibleFont:

  given absoluteCompatible: PixelCompatibleFont[FontLengthUnit.Absolute] =
    // Conversions taken from https://developer.mozilla.org/en-US/docs/Web/CSS/length#absolute_length_units
    new PixelCompatibleFont:
      override def toPixels(value: Double, unit: FontLengthUnit.Absolute): Double =
        unit match
          case Px => value
          case Cm => value * 96 / 2.54
          case Mm => value * toPixels(1, Cm) / 10
          case Q  => value * toPixels(1, Cm) / 40
          case In => value * 96
          case Pc => value * toPixels(12, Pt)
          case Pt => value * toPixels(1, In) / 72

  trait RelativeCompatible[-LengthUnit <: FontLengthUnit.RootRelative]
      extends PixelCompatibleFont[LengthUnit]:

    /** @return a baseline font size that will be used to compute the amount of pixels for a given relative font size.
      * For example, an implementation of the 'rem' font size in the context of HTML, would take the font size of the root element and return it as the output of this method.
      * Note that this method can be side-effectful
      */
    def getBaselineFontSize: Double

    override final def toPixels(value: Double, unit: LengthUnit): Double =
      unit match
        case Rem => value * getBaselineFontSize
