package florence.core.model.shared

/** Represents the baseline that indicates the reference position that is used when rendering text within a chart.
  * For example, if a text is displayed at position (x, y), and its text baseline is "bottom", it means that the lowest part of the text is displayed at (x, y).
  *
  * The definition of this type is based on this specification: https://developer.mozilla.org/en-US/docs/Web/API/CanvasRenderingContext2D/textBaseline
  */
enum TextBaseline(val name: String):
  case Top         extends TextBaseline("top")
  case Hanging     extends TextBaseline("hanging")
  case Middle      extends TextBaseline("middle")
  case Alphabetic  extends TextBaseline("alphabetic")
  case Ideographic extends TextBaseline("ideographic")
  case Bottom      extends TextBaseline("bottom")
