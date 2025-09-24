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

package florence.core.model.shared

// Shared types for styling and rendering

object StyleTypes:

  enum Alignment:
    case Left, Center, Right

  enum LegendPosition:
    case Top, Right, Bottom, Left, TopRight, TopLeft, BottomRight, BottomLeft

  enum LineType:
    case Solid, Dashed, Dotted, DashDot

  enum MarkerType:
    case None, Circle, Square, Triangle, Star, Plus

  enum SliceLabelPosition:
    case Inside, Outside, None

  final case class FontSpec(
      family: String = "sans-serif",
      size: Double = 12.0,
      weight: String = "normal"
  )

  final case class LineStyle(
      colour: String,
      width: Double = 1.0,
      dash: Option[List[Double]] = None
  )

  final case class Padding(
      top: Double = 10.0,
      right: Double = 10.0,
      bottom: Double = 10.0,
      left: Double = 10.0
  )

  final case class Margins(
      top: Double = 40,
      right: Double = 40,
      bottom: Double = 50,
      left: Double = 50
  )
end StyleTypes
