package florence.core.model.shared

import florence.core.model.shared.FontLengthUnit.Absolute.*
import florence.core.model.shared.FontLengthUnit.RootRelative.*

/** User-facing type to hide the complexity of the [[FontSizeIn]] type
  */
opaque type FontSize = FontSizeIn[FontLengthUnit]

object FontSize:

  def apply[LengthUnit <: FontLengthUnit](
      value: Double,
      unit: LengthUnit
  )(using pixelCompatible: PixelCompatibleFont[LengthUnit]): FontSize =
    FontSizeIn(value, unit)

  extension (fontSize: FontSize)
    def value: Double        = fontSize.value
    def unit: FontLengthUnit = fontSize.unit
    def toPixels: FontSize   = fontSize.toPixels
    def show: String         = s"${fontSize.value}${fontSize.unit.name}"

object FontSizeSyntax:

  extension (value: Double)
    def rem(using PixelCompatibleFont[Rem.type]): FontSize = FontSize[Rem.type](value, Rem)
    def px(using PixelCompatibleFont[Px.type]): FontSize   = FontSize[Px.type](value, Px)
    def cm(using PixelCompatibleFont[Cm.type]): FontSize   = FontSize[Cm.type](value, Cm)
    def mm(using PixelCompatibleFont[Mm.type]): FontSize   = FontSize[Mm.type](value, Mm)
    def q(using PixelCompatibleFont[Q.type]): FontSize     = FontSize[Q.type](value, Q)
    def in(using PixelCompatibleFont[In.type]): FontSize   = FontSize[In.type](value, In)
    def pc(using PixelCompatibleFont[Pc.type]): FontSize   = FontSize[Pc.type](value, Pc)
    def pt(using PixelCompatibleFont[Pt.type]): FontSize   = FontSize[Pt.type](value, Pt)

/** @param value font size value
  * @param unit font size unit
  * @param pixelCompatible evidence that this font size can be converted into pixel units. We cannot built a FontSizeIn unless we provide this evidence
  */
private[shared] final case class FontSizeIn[+LengthUnit <: FontLengthUnit](
    value: Double,
    unit: LengthUnit
)(using pixelCompatible: PixelCompatibleFont[LengthUnit]):

  def toPixels: FontSizeIn[Px.type] =
    FontSizeIn(pixelCompatible.toPixels(value, unit), Px)

  override def toString: String =
    s"$value${unit.name}"

/** Some of the font length units specified by https://developer.mozilla.org/en-US/docs/Web/CSS/length#syntax
  */
sealed trait FontLengthUnit:
  def name: String

object FontLengthUnit:

  enum RootRelative(override val name: String) extends FontLengthUnit:
    case Rem extends RootRelative("rem")

  enum Absolute(override val name: String) extends FontLengthUnit:
    case Px extends Absolute("px")
    case Cm extends Absolute("cm")
    case Mm extends Absolute("mm")
    case Q  extends Absolute("Q")
    case In extends Absolute("in")
    case Pc extends Absolute("pc")
    case Pt extends Absolute("pt")
