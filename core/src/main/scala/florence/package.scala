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

package florence

// model exports
export florence.core.model.ChartDef
export florence.core.model.LineSeries
export florence.core.model.LineData
export florence.core.model.AxisDef
export florence.core.model.shared.StyleTypes.*
export florence.core.model.styling.ChartStyleDef
export florence.core.model.styling.CommonStyleProps
export florence.core.model.styling.BackgroundStyle
export florence.core.model.styling.BorderStyle
export florence.core.model.styling.TitleStyle
export florence.core.model.styling.AxisStyle
export florence.core.model.styling.LegendStyle
export florence.core.model.styling.LineSeriesStyle
export florence.core.model.styling.WithCommonProps
export florence.core.model.styling.WithCommonProps.*

// dsl exports
export florence.core.dsl.ChartDsl.*
export florence.core.dsl.styling.ChartStylingDsl.*

//rendering exports
export florence.core.rendering.Interpreter
export florence.core.rendering.Interpreters
export florence.core.rendering.LineChartInterpreter
export florence.core.rendering.LineChartInterpreterInstances.*
export florence.core.rendering.Renderer
export florence.core.rendering.RendererExtensions.*

object instances:
  export florence.core.rendering.LineChartInterpreterInstances.given
  export florence.core.model.styling.WithCommonProps.given
