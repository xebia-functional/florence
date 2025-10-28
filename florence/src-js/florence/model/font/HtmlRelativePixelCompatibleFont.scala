package florence.model.font

import org.scalajs.dom

import florence.core.model.shared.{FontLengthUnit, PixelCompatibleFont}
import florence.model.InvalidComputedFontSize

object HtmlRelativePixelCompatibleFont:

  /** Relative font size compatibility which takes the font size of the root element as the baseline font size
    */
  given rootPixelCompatible: PixelCompatibleFont[FontLengthUnit.RootRelative] =
    new PixelCompatibleFont.RelativeCompatible:
      override def getBaselineFontSize: Double =
        // getComputedStyle should always return the fontSize in pixels
        dom.window.getComputedStyle(dom.document.documentElement).fontSize match
          case fontSize @ s"${size}px" =>
            size.toDoubleOption.getOrElse(throw InvalidComputedFontSize(fontSize))

          case other => throw InvalidComputedFontSize(other)
