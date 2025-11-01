package florence.core.model.shared

import florence.core.model.shared.FontLengthUnit.Absolute.*
import florence.core.model.shared.FontLengthUnit.RootRelative.*

/** @param value font size value
  * @param unit font size unit
  */
final case class FontSize(value: Double, unit: FontLengthUnit):
  override def toString: String =
    s"$value${unit.name}"

object FontSizeSyntax:

  extension (value: Double)
    def rem: FontSize = FontSize(value, Rem)
    def px: FontSize  = FontSize(value, Px)
    def cm: FontSize  = FontSize(value, Cm)
    def mm: FontSize  = FontSize(value, Mm)
    def q: FontSize   = FontSize(value, Q)
    def in: FontSize  = FontSize(value, In)
    def pc: FontSize  = FontSize(value, Pc)
    def pt: FontSize  = FontSize(value, Pt)

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
