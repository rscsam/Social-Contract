# Social Contract

This is the Android version of an application that integrates 
many types of social media into one application that then
allows users to gain following and follow new people
utilizing a centralized virtual currency called "coins."

The code for the Social Contract server is located at https://github.com/rscsam/Social-Contract-Server

<h2>Using git</h2>
<h3>Branches and Pull Requests</h3>
All code for this project should be done on your own branch.
When you have finished your current task and are ready
to merge your code into the main project, you should create
a pull request from your branch into master (or whatever branch
you want to merge into).  You may not accept your own pull
request unless you have consent from the rest of the team.
Rather, you should ask another team member to review your pull
request and confirm the merge.  

If you are reviewing a pull request, you should always leave
a comment on it, regardless of whether you accept or decline.

<h2>Coding Conventions</h2>
<h3>Activities and Fragments</h3>
In general, if two screens share common elements fixed to the same
part of the screen, they should be coded as Fragments
that are managed by the same activity.

In order to maintain a level of re-usability and easy refactoring,
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

<h4>Layouts</h4>
Each element of a layout should be given an id.  The id name
does not have to follow a strict rule, but it should be something
to the effect of

[what the View does][type of View]

For example, say you have a `TextView` (often abbreviated to tv)
that reads "This is a prompt."  A good idea for an id name
for this `TextView` would be `prompt_tv`.  Note that with
layout ids, you should always use lowercase letters separated by
underscores.

Additionally, for ease of reading, you should make the id
either the first or the last attribute inside of a View tag
