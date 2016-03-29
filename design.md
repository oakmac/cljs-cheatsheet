# ClojureScript Cheatsheet Design

Below are some design notes for the [ClojureScript cheatsheet].

## Type

Open Sans (weights 300, 400, 600) was chosen for its clean appearance and
excellent legibility. Droid Sans Mono was chosen as a compliment pair for
monospaced code. Both typefaces have a neutral appearance and keep the focus on
the content, not the type.

Type differentiation is used to distinguish between groups headers, section
headers, row labels, and table symbols. Each has a distinct appearance and can
be recognized at a glance.

## Info Tooltips

Info tooltips (marked with the circle "i" icon) provide context and additional
information helpful to developers coming from other languages. Comparisons to
JavaScript are mentioned as relevant. Tooltips are unobtrusive unless hovered
over. Grey is used to make them visually "wash over" when not needed.

Tooltips have a small mouse pointer launch target, but once opened the range
expands to allow for the mouse to enter the tooltip body and "explore" the
content. This also allows the user to copy/paste content from a tooltip.

## Tables

Tables contain no additional marks other than labels and content. Color is used
to separate table rows.

## Colors

High contrast colors are used to ensure legibility on a wide range of devices
and screens.

## Literal Forms

Where appropriate, section headers contain the literal representation or
shorthand version of their form. Users already familiar with the literal forms
will instantly recognize their connection to the section. Users unfamiliar with
the literal forms may be confused at first, but should eventually have an
"aha!" moment when they make the connection. This is my favorite feature of the
cheatsheet :)

## Search

Search highlights matching symbols in yellow and leaves the containing section
visible for context. Sections with no match are hidden and the content "folds"
smartly around the matched sections. Red is used in the search bar to indicate
no match.

## Inline Tooltips

Inline tooltips provide rich information about each function, macro, or special
form. Like the info tooltips, inline tooltips allow for the mouse to enter the
body to "explore" the content and copy/paste.

Mousing over a related symbol in a tooltip highlights it on the page, visually
connecting related sections. When a related symbol is underneath the tooltip
body (as often happens due to the organization of the symbols), opacity is used
to show the location of the related symbol.

## Content Organization

Sections are defined and ordered by common usage first, implementation
organization second. Links are repeated in multiple sections where appropriate.

## Footer

The footer contains no color and provides relevant links to related material.

## Documentation

Documentation is contained in simple [kidif files] that allow for management
and collaboration using existing tools (git, text editors, etc).

Docstrings were extracted from the code and converted to Markdown format to
allow for more structure than simple text can provide (ie: code highlighting,
links, tables, etc). The descriptions were edited to have consistent voice and
structure.

## Responsive Layout

The content folds into three layouts depending on browser width. The sections
are organized in each layout to fill the space as best they can.

[ClojureScript cheatsheet]:http://cljs.info/cheatsheet
[kidif files]:https://github.com/oakmac/kidif.js
