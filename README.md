# Social Contract

This is the Android version of an application that integrates 
many types of social media into one application that then
allows users to gain following and follow new people
utilizing a centralized virtual currency called "coins."
<h2>Coding Conventions</h2>
<h3>Activities and Fragments</h3>
In general, if two screens share common elements fixed to the same
part of the screen, they should be coded as Fragments
that are managed by the same activity.

In order to maintain a level of reusability and easy refactoring,
all `Fragment`s will interact with their   `Activity` through
interfaces defined by the `Fragment` and implemented by the
`Activity`.  Many examples of this pattern are already being
implemented within the application.

<h3>Naming and Style Conventions</h3>
<h4>Fragments</h4>
Listener interfaces defined within fragments should follow the
following naming convention:

[Fragment Name]FListener

For example, if you have a `Fragment` called `HomeFragment`,
you should name your interface `HomeFListener`

<h4>Method Names</h4>
Method names should always follow typical Java conventions
(camelcase, etc.).  Additionally, all onClick methods
should follow the following naming convention:

onClick[View Name]

For example, if you have a button that is called `submitButton`,
your should name your onClick method `onClickSubmit`.

If the onClick is declared inside of a `Fragment` initially,
it should follow the following naming convention:

onClick[Fragment Name][View Name]

For example, if you have `submitButton`'s onClick declared
inside of the `HomeFragment`, the corresponding onClick
method should be called `onClickHomeSubmit`
