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

package florence.core.rendering

import florence.core.dsl.styling.StyledChart
import florence.core.model.ChartDef
import florence.core.model.styling.ChartStyleDef

/** Typeclass for rendering a Drawing to a specific context/platform
  *
  * @tparam Ctx the rendering context type (e.g., Canvas, SVG, etc.)
  */
trait Renderer[Ctx]:
  def render(drawing: Drawing, context: Ctx): Unit

object Renderer:
  def apply[Ctx](using renderer: Renderer[Ctx]): Renderer[Ctx] = renderer

  def render[Ctx](drawing: Drawing, context: Ctx)(using renderer: Renderer[Ctx]): Unit =
    renderer.render(drawing, context)

object RendererExtensions:

  extension [C <: ChartDef, S <: ChartStyleDef, Ctx](chart: C)

    def renderWith(style: S, context: Ctx)(using
        interpreter: Interpreter[(C, S), Drawing],
        renderer: Renderer[Ctx]
    ): Unit =
      val drawing = interpreter.interpret((chart, style))
      renderer.render(drawing, context)

  extension [C <: ChartDef, Ctx](chart: C)

    def renderTo(
        context: Ctx
    )(using interpreter: Interpreter[C, Drawing], renderer: Renderer[Ctx]): Unit =
      val drawing = interpreter.interpret(chart)
      renderer.render(drawing, context)

  extension (drawing: Drawing)
    def renderTo[Ctx](context: Ctx)(using renderer: Renderer[Ctx]): Unit =
      Renderer.render(drawing, context)

  extension [C <: ChartDef, S <: ChartStyleDef, Ctx](styledChart: StyledChart[C, S])

    def renderTo(
        context: Ctx
    )(using interpreter: Interpreter[StyledChart[C, S], Drawing], renderer: Renderer[Ctx]): Unit =
      val drawing = interpreter.interpret(styledChart)
      renderer.render(drawing, context)
