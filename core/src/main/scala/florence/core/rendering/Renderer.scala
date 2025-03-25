package florence.core.rendering

/** Typeclass for rendering a Drawing to a specific context/platform
  * @tparam C the rendering context type (e.g., Canvas, SVG, etc.)
  */
trait Renderer[C]:
  def render(drawing: Drawing, context: C): Unit

object Renderer:
  def apply[C](using renderer: Renderer[C]): Renderer[C] = renderer

  def render[C](drawing: Drawing, context: C)(using renderer: Renderer[C]): Unit =
    renderer.render(drawing, context)

extension (drawing: Drawing)
  def renderTo[C](context: C)(using renderer: Renderer[C]): Unit =
    Renderer.render(drawing, context)
