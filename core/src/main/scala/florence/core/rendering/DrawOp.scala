package florence.core.rendering

import florence.core.model.shared.*

final case class ClearOp(colour: Option[String] = None)

final case class LineOp(
    x1: Double,
    y1: Double,
    x2: Double,
    y2: Double,
    style: LineStyle
)

final case class PolylineOp(
    points: List[(Double, Double)],
    style: LineStyle,
    closed: Boolean = false
)

final case class RectOp(
    x: Double,
    y: Double,
    width: Double,
    height: Double,
    fill: Option[String],
    stroke: Option[String],
    cornerRadius: Double = 0
)

final case class CircleOp(
    x: Double,
    y: Double,
    radius: Double,
    fill: Option[String],
    stroke: Option[String]
)

final case class ArcOp(
    x: Double,
    y: Double,
    radius: Double,
    startAngle: Double,
    endAngle: Double,
    fill: Option[String],
    stroke: Option[String]
)

final case class TextOp(
    text: String,
    x: Double,
    y: Double,
    font: FontSpec,
    colour: String,
    alignment: Alignment
)

final case class GroupOp(
    operations: List[DrawOp],
    transform: Option[Transform] = None
)

final case class Transform(
    translateX: Double = 0,
    translateY: Double = 0,
    scaleX: Double = 1,
    scaleY: Double = 1,
    rotation: Double = 0
)

type PathOp       = LineOp | PolylineOp
type ShapeOp      = RectOp | CircleOp | ArcOp
type StructuralOp = ClearOp | GroupOp

type DrawOp = PathOp | ShapeOp | TextOp | StructuralOp

final case class Drawing(ops: List[DrawOp]):
  def ++(other: Drawing): Drawing = Drawing(ops ++ other.ops)

  def withBackground(colour: String): Drawing =
    Drawing(ClearOp(Some(colour)) :: ops)

  def grouped: Drawing =
    Drawing(List(GroupOp(ops)))

  def transformed(
      tx: Double = 0,
      ty: Double = 0,
      sx: Double = 1,
      sy: Double = 1,
      rotation: Double = 0
  ): Drawing =
    Drawing(List(GroupOp(ops, Some(Transform(tx, ty, sx, sy, rotation)))))

object Drawing:
  val empty: Drawing               = Drawing(List.empty)
  def apply(op: DrawOp): Drawing   = Drawing(List(op))
  def apply(ops: DrawOp*): Drawing = Drawing(ops.toList)

  def line(
      x1: Double,
      y1: Double,
      x2: Double,
      y2: Double,
      colour: String = "black",
      width: Double = 1.0
  ): LineOp =
    LineOp(x1, y1, x2, y2, LineStyle(colour, width))

  def text(
      content: String,
      x: Double,
      y: Double,
      fontSize: Double = 12.0,
      fontFamily: String = "sans-serif",
      colour: String = "black"
  ): TextOp =
    TextOp(content, x, y, FontSpec(fontFamily, fontSize), colour, Alignment.Left)

  def rect(
      x: Double,
      y: Double,
      width: Double,
      height: Double,
      fill: Option[String] = None,
      stroke: Option[String] = Some("black")
  ): RectOp =
    RectOp(x, y, width, height, fill, stroke)
end Drawing
