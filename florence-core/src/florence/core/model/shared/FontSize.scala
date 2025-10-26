package florence.core.model.shared

final case class FontSize(value: Double, unit: FontLenghtUnit):
  override def toString: String =
    s"$value${unit.name}"

object FontSizeSyntax:

  extension (value: Double)
    def rem: FontSize = FontSize(value, FontLenghtUnit.Rem)

    def px: FontSize = FontSize(value, FontLenghtUnit.Px)
    def cm: FontSize = FontSize(value, FontLenghtUnit.Cm)
    def mm: FontSize = FontSize(value, FontLenghtUnit.Mm)
    def q: FontSize  = FontSize(value, FontLenghtUnit.Q)
    def in: FontSize = FontSize(value, FontLenghtUnit.In)
    def pc: FontSize = FontSize(value, FontLenghtUnit.Pc)
    def pt: FontSize = FontSize(value, FontLenghtUnit.Pt)

/** Some of the font lenght units specified by https://developer.mozilla.org/en-US/docs/Web/CSS/length#syntax
  */
sealed trait FontLenghtUnit:
  def name: String

object FontLenghtUnit:

  sealed trait RootRelative(override val name: String) extends FontLenghtUnit
  case object Rem                                      extends RootRelative("rem")

  sealed trait Absolute(override val name: String) extends FontLenghtUnit
  case object Px                                   extends Absolute("px")
  case object Cm                                   extends Absolute("cm")
  case object Mm                                   extends Absolute("mm")
  case object Q                                    extends Absolute("Q")
  case object In                                   extends Absolute("in")
  case object Pc                                   extends Absolute("pc")
  case object Pt                                   extends Absolute("pt")
