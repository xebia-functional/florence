/*
 * Copyright 2025 Xebia Functional Open Source <https://www.xebia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package florence.renderer

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

import org.scalajs.dom.{CanvasRenderingContext2D, HTMLCanvasElement}

import florence.core.model.shared.StyleTypes.*
import florence.core.rendering.*

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

  given canvasRenderer: Renderer[CanvasRenderingContext2D] = CanvasRenderer
end CanvasRenderer

object CanvasRendererExtensions:

  extension (canvas: HTMLCanvasElement)
    def getContext2D(): CanvasRenderingContext2D =
      canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    def render(drawing: Drawing): Unit =
      val ctx = getContext2D()
      CanvasRenderer.render(drawing, ctx)

  import florence.core.dsl.styling.StyledChart
  import florence.core.model.Chart
  import florence.core.model.Chart.LineChart
  import florence.core.model.styling.ChartStyle
  import florence.core.model.styling.ChartStyle.LineChartStyle
  import florence.core.model.styling.WithCommonProps

  extension [C <: Chart, S <: ChartStyle](styled: StyledChart[C, S])

    def renderToFit(ctx: CanvasRenderingContext2D)(using
        interpreter: Interpreter[StyledChart[C, S], Drawing]
    ): Unit =
      val cw     = ctx.canvas.width.toDouble
      val ch     = ctx.canvas.height.toDouble
      val propsS = summon[WithCommonProps[ChartStyle]].getCommonProps(styled.style)
      val w      = propsS.width
      val h      = propsS.height
      val sx     = if w == 0 then 1.0 else cw / w.toDouble
      val sy     = if h == 0 then 1.0 else ch / h.toDouble
      val drw    = interpreter.interpret(styled)
      CanvasRenderer.render(drw.transformed(sx = sx, sy = sy), ctx)

  extension [C <: Chart, S <: ChartStyle](chart: C)

    def renderWithFit(style: S, ctx: CanvasRenderingContext2D)(using
        interpreter: Interpreter[(C, S), Drawing]
    ): Unit =
      val cw     = ctx.canvas.width.toDouble
      val ch     = ctx.canvas.height.toDouble
      val propsS = summon[WithCommonProps[ChartStyle]].getCommonProps(style)
      val w      = propsS.width
      val h      = propsS.height
      val sx     = if w == 0 then 1.0 else cw / w.toDouble
      val sy     = if h == 0 then 1.0 else ch / h.toDouble
      val drw    = interpreter.interpret((chart, style))
      CanvasRenderer.render(drw.transformed(sx = sx, sy = sy), ctx)

  extension (styled: StyledChart[LineChart, LineChartStyle])

    def renderToResize(ctx: CanvasRenderingContext2D)(using
        interpreter: Interpreter[StyledChart[LineChart, LineChartStyle], Drawing]
    ): Unit =
      val cw = ctx.canvas.width
      val ch = ctx.canvas.height
      val s  = styled.style
      val s2: LineChartStyle = LineChartStyle(
        commonProps = s.commonProps.copy(width = cw, height = ch),
        xAxis = s.xAxis,
        yAxis = s.yAxis,
        seriesStyles = s.seriesStyles,
        defaultSeriesStyle = s.defaultSeriesStyle,
        showPoints = s.showPoints
      )
      val drw = interpreter.interpret(styled.copy(style = s2))
      CanvasRenderer.render(drw, ctx)

  extension (chart: LineChart)

    def renderWithResize(style: LineChartStyle, ctx: CanvasRenderingContext2D)(using
        interpreter: Interpreter[(LineChart, LineChartStyle), Drawing]
    ): Unit =
      val cw = ctx.canvas.width
      val ch = ctx.canvas.height
      val s2: LineChartStyle = LineChartStyle(
        commonProps = style.commonProps.copy(width = cw, height = ch),
        xAxis = style.xAxis,
        yAxis = style.yAxis,
        seriesStyles = style.seriesStyles,
        defaultSeriesStyle = style.defaultSeriesStyle,
        showPoints = style.showPoints
      )
      val drw = interpreter.interpret((chart, s2))
      CanvasRenderer.render(drw, ctx)
