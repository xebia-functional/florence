package florence.renderer

import org.scalajs.dom.CanvasRenderingContext2D

import florence.core.model.shared.StyleTypes.FontSpec
import florence.core.model.shared.TextBaseline
import florence.core.rendering.TextMeasurer

/** Measures text dimensions in an HTML5 Canvas
  */
final class CanvasTextMeasurer(ctx: CanvasRenderingContext2D) extends TextMeasurer:

  override def measureWidth(text: String, font: FontSpec): Double =
    withFont(font) {
      val measurements = ctx.measureText(text)

      /** It is more accurate to do this addition rather than to take the measurements.width property
        * according to https://developer.mozilla.org/en-US/docs/Web/API/TextMetrics#measuring_text_width
        */
      measurements.actualBoundingBoxLeft + measurements.actualBoundingBoxRight
    }

  override def measureHeight(text: String, font: FontSpec): Double =
    withFont(font) {
      withBaseline(TextBaseline.Bottom) {
        ctx.measureText(text).fontBoundingBoxAscent
      }
    }

  /** Auxiliary method that sets the context's font within the scope of the run function
    */
  private def withFont[A](font: FontSpec)(run: => A): A =
    val prevFont = ctx.font
    ctx.font = s"${font.weight} ${font.size} ${font.family}"
    val result = run
    ctx.font = prevFont
    result

  /** Auxiliary method that sets the context's textBaseline within the scope of the run function
    */
  private def withBaseline[A](baseline: TextBaseline)(run: => A): A =
    val prevBaseline = ctx.textBaseline
    ctx.textBaseline = baseline.name
    val result = run
    ctx.textBaseline = prevBaseline
    result
