package florence.core.model.shared

import florence.core.model.shared.FontLengthUnit.*

type FontSize = FontSizeIn[FontLengthUnit]

/** @param value font size value
  * @param unit font size unit
  * @param pixelCompatible evidence that this font size can be converted into pixel units. We cannot built a FontSizeIn unless we provide this evidence
  */
final case class FontSizeIn[+Unit <: FontLengthUnit](
    value: Double,
    unit: Unit
)(using pixelCompatible: PixelCompatibleFont[Unit]):

  def toPixels: FontSizeIn[Px.type] =
    FontSizeIn(pixelCompatible.toPixels(value, unit), Px)

  override def toString: String =
    s"$value${unit.name}"

object FontSizeSyntax:

  extension (value: Double)
    def rem(using PixelCompatibleFont[Rem.type]): FontSizeIn[Rem.type] = FontSizeIn(value, Rem)
    def px(using PixelCompatibleFont[Px.type]): FontSizeIn[Px.type]    = FontSizeIn(value, Px)
    def cm(using PixelCompatibleFont[Cm.type]): FontSizeIn[Cm.type]    = FontSizeIn(value, Cm)
    def mm(using PixelCompatibleFont[Mm.type]): FontSizeIn[Mm.type]    = FontSizeIn(value, Mm)
    def q(using PixelCompatibleFont[Q.type]): FontSizeIn[Q.type]       = FontSizeIn(value, Q)
    def in(using PixelCompatibleFont[In.type]): FontSizeIn[In.type]    = FontSizeIn(value, In)
    def pc(using PixelCompatibleFont[Pc.type]): FontSizeIn[Pc.type]    = FontSizeIn(value, Pc)
    def pt(using PixelCompatibleFont[Pt.type]): FontSizeIn[Pt.type]    = FontSizeIn(value, Pt)

/** Some of the font length units specified by https://developer.mozilla.org/en-US/docs/Web/CSS/length#syntax
  */
sealed trait FontLengthUnit:
  def name: String

object FontLengthUnit:

  sealed trait RootRelative(override val name: String) extends FontLengthUnit
  case object Rem                                      extends RootRelative("rem")

  sealed trait Absolute(override val name: String) extends FontLengthUnit
  case object Px                                   extends Absolute("px")
  case object Cm                                   extends Absolute("cm")
  case object Mm                                   extends Absolute("mm")
  case object Q                                    extends Absolute("Q")
  case object In                                   extends Absolute("in")
  case object Pc                                   extends Absolute("pc")
  case object Pt                                   extends Absolute("pt")
