package florence.core.model.shared

/** Some of the font length units specified by https://developer.mozilla.org/en-US/docs/Web/CSS/length#syntax
  */
enum FontSize(unit: String):
  self =>
  case Px(value: Int)     extends FontSize("px")
  case Cm(value: Double)  extends FontSize("cm")
  case Mm(value: Double)  extends FontSize("mm")
  case Q(value: Double)   extends FontSize("Q")
  case In(value: Double)  extends FontSize("in")
  case Pc(value: Double)  extends FontSize("pc")
  case Rem(value: Double) extends FontSize("rem")

  override def toString: String =
    self match
      case Px(value)  => s"$value$unit"
      case Cm(value)  => s"$value$unit"
      case Mm(value)  => s"$value$unit"
      case Q(value)   => s"$value$unit"
      case In(value)  => s"$value$unit"
      case Pc(value)  => s"$value$unit"
      case Rem(value) => s"$value$unit"

object FontSizeSyntax:
  extension (value: Int) def px: FontSize = FontSize.Px(value)

  extension (value: Double)
    def cm: FontSize  = FontSize.Cm(value)
    def mm: FontSize  = FontSize.Mm(value)
    def q: FontSize   = FontSize.Q(value)
    def in: FontSize  = FontSize.In(value)
    def pc: FontSize  = FontSize.Pc(value)
    def rem: FontSize = FontSize.Rem(value)
