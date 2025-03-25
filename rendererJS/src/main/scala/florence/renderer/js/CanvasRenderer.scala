package florence.renderer.js

import scala.scalajs.js

import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, HTMLCanvasElement}

import florence.core.model.shared.*
import florence.core.rendering.*

import js.JSConverters.*

/** Renderer for HTML5 Canvas
  *
  * Implements the Renderer typeclass for CanvasRenderingContext2D
  */
object CanvasRenderer extends Renderer[CanvasRenderingContext2D]:

  override def render(drawing: Drawing, ctx: CanvasRenderingContext2D): Unit =
    ctx.save()
    drawing.ops.foreach(renderOp(_, ctx))
    ctx.restore()

  private def renderOp(op: DrawOp, ctx: CanvasRenderingContext2D): Unit = op match
    case ClearOp(colour) =>
      val canvas = ctx.canvas
      ctx.clearRect(0, 0, canvas.width, canvas.height)
      colour.foreach { col =>
        ctx.fillStyle = col
        ctx.fillRect(0, 0, canvas.width, canvas.height)
      }

    case LineOp(x1, y1, x2, y2, style) =>
      ctx.beginPath()
      ctx.moveTo(x1, y1)
      ctx.lineTo(x2, y2)
      applyLineStyle(style, ctx)
      ctx.stroke()

    case PolylineOp(points, style, closed) =>
      if points.nonEmpty then
        ctx.beginPath()
        val (x, y) = points.head
        ctx.moveTo(x, y)
        points.tail.foreach { case (x, y) =>
          ctx.lineTo(x, y)
        }
        if closed then ctx.closePath()
        applyLineStyle(style, ctx)
        ctx.stroke()

        if closed then
          ctx.fillStyle = style.colour
          ctx.fill()

    case RectOp(x, y, width, height, fill, stroke, cornerRadius) =>
      ctx.beginPath()
      if cornerRadius > 0 then
        // Draw rounded rectangle
        ctx.moveTo(x + cornerRadius, y)
        ctx.lineTo(x + width - cornerRadius, y)
        ctx.quadraticCurveTo(x + width, y, x + width, y + cornerRadius)
        ctx.lineTo(x + width, y + height - cornerRadius)
        ctx.quadraticCurveTo(x + width, y + height, x + width - cornerRadius, y + height)
        ctx.lineTo(x + cornerRadius, y + height)
        ctx.quadraticCurveTo(x, y + height, x, y + height - cornerRadius)
        ctx.lineTo(x, y + cornerRadius)
        ctx.quadraticCurveTo(x, y, x + cornerRadius, y)
        ctx.closePath()
      else ctx.rect(x, y, width, height)

      fill.foreach { colour =>
        ctx.fillStyle = colour
        ctx.fill()
      }

      stroke.foreach { colour =>
        ctx.strokeStyle = colour
        ctx.stroke()
      }

    case CircleOp(x, y, radius, fill, stroke) =>
      ctx.beginPath()
      ctx.arc(x, y, radius, 0, Math.PI * 2)

      fill.foreach { colour =>
        ctx.fillStyle = colour
        ctx.fill()
      }

      stroke.foreach { colour =>
        ctx.strokeStyle = colour

        ctx.stroke()
      }

    case ArcOp(x, y, radius, startAngle, endAngle, fill, stroke) =>
      ctx.beginPath()
      ctx.arc(x, y, radius, startAngle, endAngle)

      fill.foreach { colour =>
        ctx.fillStyle = colour

        ctx.fill()
      }

      stroke.foreach { colour =>
        ctx.strokeStyle = colour

        ctx.stroke()
      }

    case TextOp(text, x, y, font, colour, alignment) =>
      ctx.font = s"${font.weight} ${font.size}px ${font.family}"
      ctx.fillStyle = colour

      ctx.textAlign = alignment match
        case Alignment.Left   => "left"
        case Alignment.Center => "center"
        case Alignment.Right  => "right"

      ctx.fillText(text, x, y)

    case GroupOp(operations, transform) =>
      ctx.save()

      transform.foreach { t =>
        ctx.translate(t.translateX, t.translateY)
        ctx.scale(t.scaleX, t.scaleY)
        ctx.rotate(t.rotation)
      }

      operations.foreach(renderOp(_, ctx))

      ctx.restore()

  private def applyLineStyle(style: LineStyle, ctx: CanvasRenderingContext2D): Unit =
    ctx.strokeStyle = style.colour
    ctx.lineWidth = style.width

    style.dash match
      case Some(dash) =>
        ctx.setLineDash(dash.toJSArray)
      case None => ctx.setLineDash(js.Array[Double]())

end CanvasRenderer

extension (canvas: HTMLCanvasElement)
  def getContext2D(): CanvasRenderingContext2D =
    canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  def render(drawing: Drawing): Unit =
    val ctx = getContext2D()
    CanvasRenderer.render(drawing, ctx)

given canvasRenderer: Renderer[CanvasRenderingContext2D] = CanvasRenderer
