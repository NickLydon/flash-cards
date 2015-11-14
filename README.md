# flash-cards

Flash cards on a locally running website to help the user learn the German language

[![Build Status](https://travis-ci.org/NickLydon/flash-cards.svg?branch=master)](https://travis-ci.org/NickLydon/flash-cards)

## Usage

`lein run`

It will host a local website by default at <http://localhost:4000>.
When prompted with a German phrase, enter the English equivalent and vice versa.
If your answer is exactly correct then your score will be incremented. If it's fairly close then it stays the same, otherwise it will be decremented.

Click on one of the categories, e.g. [nationalities](http://localhost:4000?tag=nationalities), to practice a specific set of phrases.

Copyright © 2015 Nicholas Lydon
