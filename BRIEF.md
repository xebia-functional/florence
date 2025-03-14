> Note: This description is a wish list, not a list of requirements written in stone. 

## Goal

To build a small but delightful charting library, that is super easy to integrate into Scala and Scala.js projects.

In the first instance: The aim is to make something useful, joyful, and empowering, not something perfect.

## Overview

The ability to efficiently describe and render charts and graphs is one of the key tools for data engineers, backend engineers who are building tooling, survey results, and benchmarking suites of all kinds.

Despite Scala being one of the most popular languages for big data, we do not have any good solution for creating charts in pure Scala. People regularly discuss what the 'least worst' approach for charting in Scala currently is, utilising JS or Java libraries, and yet, no pure Scala library has emerged to meet this obvious need.

Anecdotally, "How to integrate with Chart.js" is probably one of the most common questions regarding Scala.js interop with JavaScript libraries, and the main driver for people having to use the NPM ecosystem with Scala.js.

Our goal is the make the lives of data engineers, tool builders, and Scala.js frontend developers more satisfying, efficient, and joyful, by giving them the chart building tools they deserve.

Ease of integration and low barrier to entry are our the market differentiators. We will not win on feature parity with other solutions (for now). The user experience should feel like:

1. Add library dependancy.
2. Add one, one-line import.
3. Describe chart.
4. Run program.
5. Admire chart.
6. Feel empowered and delighted.
7. Goto 3.

## Requirements

### Three types of chart, only

For the first release, we should focus on a maximum of three generally-useful and popular types of chart. Keep it simple. For example, they could be:

1. Pie charts
2. Bar charts
3. Line charts

The implementation should leave the door open to adding new chart types.

### Vanilla Scala

Backend Scala engineers tend to start all conversations by asking which effect system they should use, since effect systems are essential to good programming in the domain of asynchronous service building and data processing, i.e. what we do in our day jobs!

However, the value of effect systems diminishes when you move into the domains of frontend programming, and even scripting. Many Scala.js Frontend frameworks either do not employ effect systems at all, or only in a very light touch way.

The solution should primarily work with vanilla Scala, but we could optionally add support for effect systems later with libraries that wrap the core offering.

### Declarative API or DSL

Whether the interface onto this library is a beautifully crafted DSL, or a builder-style API designed for maximum discoverability / ergonomics, it should focus on allowing the programmer to express their desired chart as naturally and readably as possible.

Whatever the style of the interface, remember that your users are probably going to generate parts of it programmatically. For example, I might declare the base structure of a pie chart, but dynamically feed in a list of segments and values. The manual chart building experience should be excellent, but so should the programatic chart building experience.

### Style options, colour schemes, and themes

We do not need to support every styling option under the sun, but programmers are scared of colour. To help them avoid panic buying magenta, we should provide a couple of simple colour schemes / themes, and a mechanism to apply / create their own.

### Rendering specification

The output of the API should be a data specification of what needs to be rendered, independent of any particular target platform.

The domain will need to be considered carefully. One question for example, is whether or not the specification even needs to describe the notion of a chart, or if it merely describes the layout of drawing primitives?

### Platform specific renderers

The library will need to be able to render to at least two platforms:

1. Output as images as PNGs and JPGs and so on via the JVM (i.e. from a Scala-CLI script using the Haoyi Li libs)
2. Output via Canvas API or SVG for Scala.js users

The design of these modules should be that you can extend to other platforms by writing another renderer.
### Bonus: Effect system wrappers

While not the primary focus, it would help uptake if the charting library could be easily integrated with frameworks that utilise Cats Effect and ZIO.

## Non-goals for V1

The library will be useful to Scala developers even if it is rather limited in the first instance. We should not:

1. Aim to replicate all the capabilities of other libraries in this space
2. Worry too much about future extensibility. Although the brief suggests it as a goal, it isn't worth getting blocked over. We can rework it later as necessary.
3. Animation. Eventually someone is going to want to animate their charts. That's a high quality problem for later.
4. Get too fancy with the types. The whole point is that this library has a super low barrier to entry.
